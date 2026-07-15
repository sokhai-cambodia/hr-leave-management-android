# HR Leave Management — Native Android Client — Implementation Plan

## Context

`hr-leave-management-android` is currently empty except for `SPEC.md`. This plan breaks the build
into vertically-sliced, demoable tasks against the **already-built and already-deployed** FastAPI
backend (`../hr-leave-management/backend`, contract in `../hr-leave-management/PROJECT_FEATURES.md`).

This is the second of two client apps against one shared backend (per the project's course
structure: Full Stack Developer → `../hr-leave-management-flutter`, Android Application
Development → this repo). The sibling Flutter app is fully built (`../hr-leave-management-flutter/tasks/plan.md`,
14 phases, all done) and is the primary reference for this plan — not to be transliterated
line-for-line (different toolkit, different course rubric), but its `tasks/plan.md` contains a
verified backend ground-truth section and a trail of "found gaps" that this plan bakes in as
day-one decisions instead of re-discovering them:

- **The backend is now mature.** Everything the Flutter plan's Phase 11 added mid-project
  (`status`/`owner_id`/`approver_id` query params on `GET /leave-requests/` and
  `GET /leave-plan-requests/`, `GET /approvals/pending-count`, `GET /schedule/?year&month`) is
  live today. This plan uses the final endpoint shapes from Task 1, with **no separate
  backend-enhancement phase**.
- **Owner-scoped lists from the start.** Flutter's Task 11.4 found that its plain Leave
  Requests/Leave Plan Requests screens leaked every user's records to superusers and mixed a
  team-owner's own submissions with their team's pending items. Android's Phase 4/5 pass
  `owner_id=<me>` from Task 1, not as a later fix.
- **Schedule built directly, not superseded.** Flutter built a Public-Holidays-only calendar
  first (Task 10.1), then replaced it with the combined `/schedule/` endpoint (Task 11.3) once
  that endpoint existed. Since `/schedule/` already exists, Android's Phase 8 builds the combined
  holidays + team-leave calendar directly.
- **No duplicate nav surfaces.** Flutter removed a redundant Profile tile from its dashboard
  Quick Actions grid (Task 10.2) and a permanently-fake "Recent Activity" placeholder stat (Task
  10.3) after noticing them late. Android's Phase 2 dashboard is scoped to only real, wired tiles
  from the start — no placeholder cards left in the shipped grid.
- **Notifications live from day one.** Flutter's Phase 12 was written against a documented but
  not-yet-deployed backend contract and had to degrade gracefully (silent badge-poll failures,
  404-tolerant list). The backend is deployed now, so Android's Phase 9 doesn't need that
  fallback path — though the same *pattern* (session-lifecycle-tied polling, silent badge
  failures, normal error+retry on the list) is still followed as good practice, not as a
  deployment workaround.

Decisions carried over from the Flutter plan (same rationale, not re-litigated):
- **No self-signup screen** — admins provision all accounts via the Admin Users screen (Phase 10).
- **Admin CRUD (Phase 10) built after the transactional flows** (Phase 3–9) — those phases don't
  need to seed test data manually via Swagger UI the way Flutter originally did, though: the
  Flutter app's admin CRUD screens are already built and can seed/manage test data (teams, users,
  team-owner assignment, leave types, holidays, policies, balances) against the same backend
  instance in the meantime — use the Flutter app as the seeding tool, Swagger UI (`/docs`) as
  fallback.
- **Team-owner detection heuristic**: no `is_team_owner` flag exists on `User` — fetch
  `GET /teams` and compute `isApprover = teams.any { it.teamOwner?.id == currentUser.id }`
  client-side, once per session (Phase 2).
- Infinite-scroll/pagination, two-tab Approvals layout (Leave Requests / Leave Plan Requests),
  and the generic-CRUD-then-applied admin pattern are reused directly from the Flutter precedent.

## Verified Backend Ground Truth

(Consolidated from `../hr-leave-management/PROJECT_FEATURES.md` and the Flutter plan's own
verification pass against `backend/app/models.py` — treat the backend source as tiebreaker if
anything here is ambiguous while building.)

- **Auth**: `POST /api/v1/login/access-token` (OAuth2 form-urlencoded: `username`=email,
  `password`) → `{access_token, token_type: "bearer"}`. `POST /login/test-token` validates a
  token and returns `UserPublic` — use for session-restore on app start. `POST
  /password-recovery/{email}`, `POST /reset-password/` (`{token, new_password}`). **No
  refresh-token endpoint** — re-login on 401/expiry is correct behavior, not a shortcut. Token
  lifetime is 8 days.
- **List endpoints** wrap `{data: [...], count: N}`, params `skip`/`limit` (default 0/100).
  **Exception**: `GET /recommends/leave-plan?year=` returns `{leave_type_id, year, data: [...]}`
  — no `count`, not paginated, single object.
- **Status literals** (LeaveRequest & LeavePlanRequest): exactly
  `"draft" | "pending" | "approved" | "rejected"`, lowercase.
- **"Team owner" is not a `User` field.** The only role flag on `User` is `is_superuser`.
  Approver status is derived per-team via `Team.team_owner_id`/`team_owner` — see Phase 2.
- **Presentable (nested/trimmed) shapes**: `UserPresentable {id, full_name, email}`,
  `TeamPresentable {id, name, team_owner: UserPresentable|null}`,
  `LeaveTypePresentable {id, code, name}` — appear nested inside other resources, not the full
  resource.
- **`PublicHoliday.date`** is a plain `"YYYY-MM-DD"` string on the wire; leave request
  `start_date`/`end_date` and plan-detail `leave_date` are real `date` types. Model each per its
  actual backend type, don't assume uniformity.
- **`Policy.operation`/`value`** are free-form strings (`in`, `>`, `<`, `>=`, `<=`, `==` /
  `"[0,4]"`, `"50%"`), not enums — no client-side validation beyond "non-empty."
- **`GET /leave-requests/` and `GET /leave-plan-requests/`** support optional `status`,
  `owner_id`, `approver_id` query params that AND onto the base visibility scope (own + approver
  rows for non-superusers), not replace it.
- **`GET /approvals/pending-count`** → `{leave_requests, leave_plan_requests, total}` — cheap
  badge count, not the queue itself.
- **`GET /schedule/?year&month`** → month-scoped `{year, month, public_holidays: [...],
  team_leave: [...]}`, `team_leave` unifying Leave Requests and Leave Plan Requests
  (`source: "leave_request"|"leave_plan_request"`), filtered to `status == "approved"` and the
  caller's team. Empty `team_leave` (not an error) if the caller has no team.
- **`GET/PUT /notifications/*`**: `unread-count` for badge polling, `GET /` for the list
  (`entity_type` + `entity_id` drive navigation on tap), `PUT /{id}/read`, `PUT /mark-all-read`.

## Reference Patterns from the Flutter Client (architectural parity, not literal translation)

- Single Retrofit/OkHttp client with an auth interceptor reading the stored token — Dio-client
  equivalent, same idea (`core/network/`).
- Global 401 handling: clear stored token + force-navigate to login on any 401 from any request,
  guarded against double-firing from concurrent in-flight calls.
- One `AuthViewModel`-equivalent (or a repository read by every ViewModel that needs
  `currentUser`) owns login/logout + caches the current user after login.
- Role-based UI is a single boolean branch on `is_superuser` for admin nav entries, plus the
  computed `isApprover` for the Approvals entry — no other role concept exists.
- Two pure, directly unit-testable functions do the Schedule screen's heavy lifting: group
  entries by day (for calendar markers) and filter to the visible month (for the list sections
  below) — same shape as Flutter's `groupHolidaysByDay`/`groupTeamLeaveByDay`.
- Duplicate-date rejection for Leave Plan Requests is a pure function, tested directly, not
  buried in a Composable callback.

## Standard Verification Environment (used by every task below)

- `cd ../hr-leave-management && docker compose up -d` — Postgres, backend
  (`http://localhost:8000`, prefix `/api/v1`), Adminer, Mailcatcher (SMTP capture at
  `http://localhost:1080`, needed for Phase 1's forgot/reset-password task). **Or** point at the
  already-deployed Render backend if reusing the Flutter app's live test data is preferred —
  either is valid; pick per-task based on whether email capture (Mailcatcher) is needed.
  Superuser bootstrap: `FIRST_SUPERUSER`/`FIRST_SUPERUSER_PASSWORD` from
  `../hr-leave-management/.env`.
- Swagger UI at `http://localhost:8000/docs`, and/or the already-built Flutter app pointed at the
  same backend instance — both are valid tools for seeding/managing cross-user/cross-team test
  data (teams, team-owner assignment, leave types, holidays, policies) before Phase 10's in-app
  admin CRUD exists.
- Dev iteration: run via Android Studio on an emulator against `http://10.0.2.2:8000/api/v1` (or
  a `BuildConfig` field pointed at the deployed backend). Base URL injected once via Gradle
  `buildConfigField`, read from one config object — never hardcoded elsewhere.
- Per-resource example payloads at `../hr-leave-management/frontend/api-docs/*.md` if a
  request/response shape needs double-checking beyond `PROJECT_FEATURES.md`.

---

## Phase 0 — Environment & Scaffolding

### Task 0.1 — Project scaffold, dependencies, folder skeleton, lint
- **Depends on:** nothing
- New Android Studio project (Empty Compose Activity template), package `com.mitclass.hrleave`,
  min SDK 21 / target latest. Add Hilt, Navigation-Compose, Retrofit + OkHttp +
  kotlinx.serialization converter, `androidx.security:security-crypto`, Coroutines. Build the
  full `core|data|feature` skeleton per SPEC §4 (placeholder files OK). Compose `MaterialTheme`
  wired to SPEC §7's design tokens (`Color.kt`, `Type.kt`, `Shape.kt`) — light-first, dark toggle
  available. `BuildConfig` field for `API_BASE_URL` (default `http://10.0.2.2:8000/api/v1`).
- **Acceptance:** `./gradlew lint` clean; `./gradlew assembleDebug` succeeds; folder tree matches
  SPEC §4; app launches to a placeholder screen themed with the brand colors.
- **Verify:** run on emulator, confirm launch with no crash; confirm theme colors visually match
  SPEC §7 (brick red accent, correct corner radii).

### Task 0.2 — Core network/storage infra + connectivity smoke test
- **Depends on:** 0.1
- `core/storage/TokenStore.kt` (EncryptedSharedPreferences get/set/clear); `core/network/`:
  Retrofit instance, `AuthInterceptor` (adds bearer header from `TokenStore`), a 401-handling
  `Authenticator`/interceptor stub (completed in Task 1.2); `core/errors/ApiError.kt` mapping
  FastAPI's two error shapes (`detail: string` and `detail: [{loc,msg,type}]` for 422) into one
  readable message. Throwaway smoke screen calling `GET /utils/health-check/`.
- **Acceptance:** smoke screen shows true/false correctly; unit test on the error mapper for the
  422-list shape.
- **Verify:** backend up → `true`; `docker compose stop backend` → graceful failure message, no
  crash.

**Checkpoint 0** — confirm the dev workflow/emulator setup before building UI on top of it.

---

## Phase 1 — Auth & Session

### Task 1.1 — Login + token storage + current-user fetch + minimal authenticated screen
- **Depends on:** 0.2
- `data/remote/dto/UserDto.kt`, `data/repository/AuthRepository.kt` (`login()`, `fetchMe()`),
  `feature/auth/LoginViewModel.kt`, `feature/auth/LoginScreen.kt`, minimal post-login welcome
  screen.
- **Acceptance:** valid creds → token persisted, `/users/me` cached, navigates; invalid creds →
  backend message shown, no nav; logout clears token; password field obscured with a
  show/hide toggle; login button disabled mid-request (loading state).
- **Verify:** log in as `FIRST_SUPERUSER`; confirm welcome screen shows correct email; log out.

### Task 1.2 — Session bootstrap + global 401 handling
- **Depends on:** 1.1
- App-start bootstrap: stored token → `POST /login/test-token` validate → route to shell or
  clear+login. Complete the OkHttp `Authenticator`: any 401 → clear token, navigate to Login
  clearing the back stack, guarded against concurrent double-fire. `NavHost` start destination
  decided by bootstrap result, not hardcoded to Login.
- **Acceptance:** no token → opens straight to Login, never flashes authenticated UI; kill+relaunch
  while logged in → restores session; deactivating the token's user via `/docs` → next
  authenticated call anywhere triggers forced logout, not a silent failure.
- **Verify:** relaunch test; then via `/docs` deactivate the test user, trigger any authenticated
  call, confirm forced logout.

### Task 1.3 — Forgot password + reset password
- **Depends on:** 1.1
- `AuthRepository.recoverPassword()/resetPassword()`; two screens off Login. Reset screen uses a
  manual "paste your reset token" field (no deep-linking in scope — matches the Flutter
  precedent).
- **Acceptance:** known email → success state; unknown → backend's 404 surfaced; valid
  token+new password → can log in with it; invalid/expired token → backend's message shown.
- **Verify:** with Mailcatcher running, trigger recovery, open `http://localhost:1080`, copy the
  token from the captured email, complete reset, log in with the new password.

**Checkpoint 1** — full auth lifecycle demoable against the real backend.

---

## Phase 2 — App Shell, Role-Adaptive Navigation, Dashboard

### Task 2.1 — App shell + Navigation-Compose graph + static role-based nav (superuser branch)
- **Depends on:** 1.2
- `core/navigation/`: `NavGraph.kt`, `Destinations.kt`. Persistent shell (`ModalNavigationDrawer`
  or bottom nav + drawer for admin items — pick one and use consistently) listing: Dashboard,
  Schedule, Leave Plan Requests, Leave Requests, Recommendations, Approvals *(hidden pending
  2.2)*, Profile — plus, only for `is_superuser`: Policies, Leave Types, Teams, Leave Balances
  (admin), Admin Users. Dashboard: profile card (name/email/team) + a 2-column
  `LazyVerticalGrid` quick-actions grid containing only tiles that are wired to real screens by
  the end of this plan (no placeholder tiles committed — SPEC §8's dashboard description).
- **Acceptance:** superuser sees full menu; plain employee sees baseline only; every entry
  navigates somewhere real (no dead taps, rubric §5); "No team assigned" shown when `team ==
  null`.
- **Verify:** compare menus between `FIRST_SUPERUSER` login and a freshly created plain employee.

### Task 2.2 — Team-owner detection + Approvals nav entry + Pending Approvals card
- **Depends on:** 2.1
- `data/repository/TeamsRepository.kt` (`GET /teams`, open to any authenticated user). At
  dashboard bootstrap, fetch teams once per session, compute `isApprover =
  teams.any { it.teamOwner?.id == currentUser.id }`, cache for the session; show/hide the
  Approvals nav entry accordingly. `data/repository/ApprovalsRepository.kt`
  (`GET /approvals/pending-count`) wired to a tappable "Pending Approvals" dashboard card
  (visible only when `isApprover`), navigating straight to the Approvals queue (Phase 7) on tap.
- **Acceptance:** team-owning user sees "Approvals" + the card; non-owning user sees neither;
  the team-owner check runs once per session, not per screen.
- **Verify:** via `/docs` (or the Flutter admin screens), set a test employee as a team's
  `team_owner_id`; confirm the entry and card appear for them and not for another employee.

**Checkpoint 2** — role-adaptive shell demoable with 3 test accounts (superuser, team-owner,
plain employee).

---

## Phase 3 — Leave Balances

### Task 3.1 — Leave balances list, wired into dashboard
- **Depends on:** 2.2
- `data/remote/dto/LeaveBalanceDto.kt`, `data/repository/LeaveBalancesRepository.kt`
  (`GET /leave-balances/me`). Employee-facing list under `feature/dashboard/` (the admin CRUD
  version lives under `feature/admin/` in Phase 10): leave type, `balance`, `taken_balance`,
  `available_balance` (trusted from server, not recomputed), filtered by year.
- **Acceptance:** numbers match `/docs`; unit test on DTO parsing of the three balance fields;
  empty state handled.
- **Verify:** seed balances via `/docs` or the Flutter app if none exist; confirm match; confirm
  empty state for a user with none.

**Checkpoint 3.**

---

## Phase 4 — Leave Requests (owner lifecycle)

### Task 4.1 — List + detail (read-only), owner-scoped from the start
- **Depends on:** 3.1
- `data/remote/dto/LeaveRequestDto.kt` (incl. nested `owner`/`leave_type`/`approver`, `status`),
  repository calling `GET /leave-requests/?owner_id=<me>` (own submissions only, regardless of
  role — avoids the gap Flutter's Task 11.4 had to fix later), status-colored chips, paged list
  (page size 20), detail screen.
- **Acceptance:** list shows only the current user's own requests even for a superuser; tapping
  opens the correct detail; status chip colors are visually distinct per state.
- **Verify:** seed 2–3 requests in varying statuses via `/docs`.

### Task 4.2 — Create draft, edit, delete (draft-only)
- **Depends on:** 4.1
- Create form (leave type dropdown, start/end date pickers, description); edit reuses the form;
  delete with a confirmation dialog. Hide/disable edit/delete for non-draft as a UX nicety, but
  the backend's 400 on illegal transitions is the real guard and must surface cleanly.
- **Acceptance:** create → draft row appears; edit persists; delete removes; attempting an
  illegal edit/delete on a non-draft row is handled gracefully, not a crash.
- **Verify:** full create/edit/delete loop; seed a `pending` request via `/docs`, try editing it
  in-app, confirm graceful rejection.

### Task 4.3 — Submit action + balance-debit visibility
- **Depends on:** 4.2
- "Submit" on a draft's detail → `PUT /{id}/submit`. Handle the "no line approver" failure with a
  friendly message ("You don't have a line approver assigned yet, contact an admin").
- **Acceptance:** valid submit → `pending` + `approver_id` assigned; no-approver submit →
  friendly error, not a crash; balances screen (Phase 3) reflects the debit after submit.
- **Verify:** ensure the test employee's team has a `team_owner_id`; submit; check the balances
  screen for the debit.

**Checkpoint 4** — full owner-side leave-request lifecycle including the cross-cutting balance
effect.

---

## Phase 5 — Leave Plan Requests (multi-date lifecycle)

Kept separate from Phase 4 because the create/edit form is materially different (a date *set*,
not a range), and Phase 6 depends on its create/detail screens directly.

### Task 5.1 — List + detail (detail = list of dates), owner-scoped
- **Depends on:** 4.3
- `data/remote/dto/LeavePlanRequestDto.kt` (`details: [{id, leave_date}]`,
  `amount = details.size`), repository calling `?owner_id=<me>`, list + detail (scrollable date
  chips, sorted).
- **Acceptance:** matches `/docs`; `amount == details.size`.
- **Verify:** seed a 3-date plan request via `/docs`.

### Task 5.2 — Create/edit (multi-date picker, duplicate guard) + delete
- **Depends on:** 5.1
- Create form: leave-type dropdown filtered to `is_allow_plan == true`; date picker that adds to
  a running removable chip list; **client-side duplicate-date rejection as a pure, directly
  unit-tested function**, not buried in a callback. Edit sends the full current date list
  (backend is full-replace, not partial). Delete with confirmation, draft-only.
- **Acceptance:** duplicate date blocked client-side before any network call; valid multi-date
  create succeeds; unit test exercises the dedupe function directly.
- **Verify:** try adding a duplicate date in-app, confirm no network call fires and a message
  appears; create a valid draft, confirm it in 5.1's list.

### Task 5.3 — Submit action
- **Depends on:** 5.2
- Mirrors 4.3's submit + friendly no-approver error. No balance-debit here (plan requests don't
  touch balances on submit).
- **Acceptance:** submit → `pending` + approver assigned; balances screen explicitly verified
  *unaffected*.
- **Verify:** submit, confirm status change, confirm balances unchanged before/after.

**Checkpoint 5.**

---

## Phase 6 — AI Recommendation Flow (headline feature)

### Task 6.1 — Fetch & display recommendations
- **Depends on:** 5.3, 3.1
- `data/remote/dto/LeaveRecommendationsDto.kt` (distinct shape: `{leave_type_id, year, data:
  [...]}`, **not** the `{data, count}` wrapper), `data/repository/RecommendsRepository.kt`.
  Screen: year selector, list of dates with `predicted_score`, `bridge_holiday`/`team_workload`
  badges. Both documented 404 cases ("no plannable leave type" vs "no remaining balance") return
  the same HTTP status — surface the backend's actual `detail` message, don't collapse both into
  one generic "not found."
- **Acceptance:** chronological order preserved (no client re-sort corrupting server order); both
  404 cases show distinguishable messages; year change re-fetches.
- **Verify:** normal case renders plausible scores; a zero-balance test user shows the specific
  empty state; a no-plannable-type case shows the other specific empty state.

### Task 6.2 — Selection UI → build plan-request draft
- **Depends on:** 6.1
- Multi-select checkboxes + "select all"; "Use selected dates" navigates into 5.2's create form
  pre-populated (still editable there — reuses 5.2's screen, not a fork).
- **Acceptance:** selecting N dates lands on the create form with exactly those N, removable;
  leave type pre-set from the recommendation.
- **Verify:** select 4, proceed, confirm 4 pre-filled, remove 1, confirm 3 remain.

### Task 6.3 — One-tap create & submit, success state
- **Depends on:** 6.2
- Pre-filled create form offers both "Save draft" (5.2 behavior) and "Submit now" (chains create
  then submit in one gesture); success state links to the new plan request's detail (5.1).
- **Acceptance:** "Submit now" → `pending` plan request matching selection; a mid-chain failure
  (create OK, submit fails for no-approver) leaves the draft intact and reachable, with a clear
  error — not silently lost.
- **Verify:** full run: select 3 → Submit now → confirm `pending`. Then simulate the failure
  branch (temporarily unset `team_owner_id`) and confirm the draft survives with a clear error.

**Checkpoint 6** — the full recommend→select→submit path works end-to-end. This is the scene to
record for the report/demo.

---

## Phase 7 — Approvals Queue (team-owner role)

### Task 7.1 — Approvals list (two tabs) + approve/reject
- **Depends on:** 4.3, 5.3, 2.2
- Reuse Phase 4/5's DTOs, calling `GET /leave-requests/?approver_id=<me>&status=pending` and the
  leave-plan equivalent (server-side filtered, not client-side). Two tabs, "Leave Requests" /
  "Leave Plans". Approve/Reject actions; reject requires a confirmation dialog.
- **Acceptance:** team-owner sees only pending rows where they're the assigned approver (not
  their own submissions, not other teams'); approve/reject transitions status and removes the
  row from the queue; **Leave Request** reject credits the balance back (cross-check Phase 3);
  **Leave Plan Request** reject does not touch balances (verify explicitly).
- **Verify (needs a second test account):** team with `team_owner_id` set; a second user on that
  team submits one of each request type; team-owner approves one, rejects the other; confirm the
  balance effects match.

**Checkpoint 7** — two-sided workflow (submitter + approver, two logged-in test accounts) fully
demoable.

---

## Phase 8 — Schedule (month calendar: holidays + team leave)

### Task 8.1 — Schedule screen backed by `GET /schedule/?year&month`
- **Depends on:** 2.1
- `data/remote/dto/ScheduleDto.kt` (`public_holidays`, `team_leave` with `source` discriminator),
  `data/repository/ScheduleRepository.kt`. Month-view calendar (Compose's own calendar grid, or a
  lightweight library — no `table_calendar` equivalent is required, this is a from-scratch build)
  with two marker colors distinguishing holidays vs. team leave, plus a legend; month
  navigation triggers a fresh `GET /schedule/` call (server-side month-scoped, not a local
  re-filter). Two pure functions, unit-tested directly: `groupByDay(entries) ->
  Map<LocalDate, List<T>>` (feeds calendar markers) and a month-filter helper feeding the two
  list sections below the calendar ("Holidays in `<Month>`" / "Team Leave in `<Month>`" —
  team-leave entries show the teammate's name and leave type).
- **Acceptance:** calendar renders both marker types distinctly; month navigation re-fetches;
  list sections match the visible month; empty team (no `team_id`) shows an empty `team_leave`
  section gracefully, not an error; unit tests cover a Dec 31/Jan 1 year-boundary case.
- **Verify:** as a logged-in employee with a team, confirm markers/navigation/lists agree;
  confirm a user with no team sees holidays but an empty (not broken) team-leave section.

**Checkpoint 8.**

---

## Phase 9 — Notifications

### Task 9.1 — Notifications data layer, global unread badge, list screen
- **Depends on:** 2.1 (shell), 1.2 (session lifecycle)
- `data/remote/dto/NotificationDto.kt`, `data/repository/NotificationsRepository.kt`
  (`fetchNotifications(skip, limit, isRead)`, `fetchUnreadCount()`, `markRead(id)`,
  `markAllRead()`). A session-scoped `NotificationsViewModel` (Hilt `@ActivityRetainedScoped` or
  equivalent, not per-screen) starts a 30-second poll of `GET /notifications/unread-count` on
  login/session-bootstrap and stops it on logout — reactive to auth state. Badge-poll failures
  are silent (no error toast every 30s); the list screen's own fetch surfaces a normal
  error+retry state. Bell icon + unread-count badge lives in the shell's top bar on every
  authenticated screen (Task 2.1's shell), not opted into per-screen.
- Notifications list: standard loading/error/empty/paged pattern, unread rows visually distinct
  (bold + accent dot), "Mark all read" top-bar action, tapping a row marks it read and navigates
  by `entity_type` (`leave_request` → Leave Requests list, `leave_plan_request` → Leave Plan
  Requests list — the list screen, not a deep link to the specific record).
- **Acceptance:** submitting a leave request as one test account increments the assigned
  approver's badge within ~30s (or on next screen focus); approve/reject increments the
  submitter's badge; tapping a notification marks it read (badge decrements) and navigates
  correctly; "Mark all read" zeroes the badge.
- **Verify:** two logged-in test accounts (submitter + approver), full submit → approve loop,
  confirm both badge transitions and navigation.

**Checkpoint 9.**

---

## Phase 10 — Admin/Superuser Master Data CRUD

Sliced as pattern-then-apply across 6 resources, not 6 near-duplicate tasks — same approach as
the Flutter precedent.

### Task 10.1 — Generic CRUD scaffold, proven on Leave Types
- **Depends on:** 2.1
- Generic paged/searchable list Composable (reusing Task 4.1's paging pattern) + a generic
  form-dialog/screen driven by a small per-resource field-spec (label/type/validators). Applied
  first to Leave Types (`code, name, entitlement, description?, is_allow_plan, is_active` —
  simplest case, no relational fields).
- **Acceptance:** superuser full CRUD loop on Leave Types entirely in-app; a non-superuser can't
  reach the route; adding a second resource in 10.2 requires no changes to the generic
  components, only a new field-spec/repository.
- **Verify:** full CRUD loop as superuser; route unreachable as plain employee (rubric §8 full
  CRUD + search/sort).

### Task 10.2 — Apply pattern: Public Holidays, Policies
- **Depends on:** 10.1
- Public Holidays (`date` as a plain `"YYYY-MM-DD"` string on the wire — only the date-picker UI
  treats it as a date). Policies (`operation`/`value` as opaque free-form strings, no
  client-side enum validation).
- **Acceptance:** both resources full CRUD; holiday dates round-trip exactly (cross-check one
  record's raw JSON via `/docs`); policy free-form fields accept arbitrary strings.
- **Verify:** CRUD loop on each; cross-check via `/docs`.

### Task 10.3 — Apply pattern: Teams, Users (relational pickers)
- **Depends on:** 10.1
- Teams (`team_owner_id` needs a searchable user-picker, not raw UUID entry). Users (`password`
  required on create, optional on edit; `team_id` uses the same team-picker). This is where the
  pattern extends with a relational-picker field type.
- **Acceptance:** team creation lets you pick an existing user as owner via search; user creation
  requires a password, edit doesn't; assigning `team_id`/`team_owner_id` in-app is reflected on
  next login — **closes the loop**: an employee assigned as a team owner via this screen becomes
  eligible for the Phase 2.2 Approvals nav entry without touching `/docs`.
- **Verify:** create a team in-app, assign an existing employee as owner; log in as that
  employee, confirm Approvals now appears (no Swagger UI needed).

### Task 10.4 — Apply pattern: Leave Balances (admin)
- **Depends on:** 10.1
- `owner_id` and `leave_type_id` both use Task 10.3's relational-picker pattern (employee picker,
  leave-type picker); `year` is a plain 4-digit string field (matches the backend's `str`-typed
  column, not numeric); `balance` is decimal. `taken_balance`/`available_balance` are
  server-computed and not part of the create/update payload.
- **Acceptance:** superuser full CRUD loop on Leave Balances entirely in-app (create/edit/delete
  a balance for any employee); non-superuser can't reach the route.
- **Verify:** manual CRUD loop as superuser.

**Checkpoint 10** — all 6 admin resources CRUD-able in-app; team-owner assignment loop fully
closed in-app.

---

## Phase 11 — Profile & Identity

### Task 11.1 — Profile view/edit + Change Password
- **Depends on:** 1.2
- Profile screen: `GET /users/me` display, edit `full_name`/`email` via `PATCH /users/me`. Change
  Password screen: current password, new password, confirm new password (obscured, show/hide),
  client-side validation (confirm matches, min length 8) before `PATCH /users/me/password`. Wrong
  current-password (backend 400) surfaces as an inline form error.
- **Acceptance:** profile edits persist and reflect immediately; password change succeeds and the
  new password works on next login; wrong current-password shows a clear inline error.
- **Verify:** edit name/email, confirm persisted; change password, log out, confirm login
  succeeds with the new password and fails with the old one.

**Checkpoint 11.**

**Explicitly out of scope for this plan** (present in the Flutter app as later, course-specific
additions not in this app's SPEC.md §8 feature list): QR business-card/Telegram deep link,
admin-set usernames + username login, phone number field. Not needed for feature parity with the
*documented* SPEC — revisit only if the user asks to extend scope.

---

## Phase 12 — Hardening & Report-Readiness

### Task 12.1 — Targeted unit tests (per SPEC §6, not broad coverage)
- **Depends on:** all functional phases (can start incrementally, finalized here)
- Cover exactly: 401-triggers-logout (mock OkHttp response returning 401), available-balance DTO
  parsing (3.1), leave-plan duplicate-date validation (5.2), 422 error-message flattening (0.2),
  Schedule's day-grouping/month-filter functions incl. year-boundary case (8.1). No broad
  Compose UI test suite — SPEC doesn't mandate it.
- **Acceptance:** `./gradlew testDebugUnitTest` passes; each test targets a named risk area, not
  incidental/tautological coverage.
- **Verify:** tests green; spot-review each test's assertion actually exercises its claimed risk.

### Task 12.2 — Lint-clean pass + empty/error/loading state audit + feature checklist walkthrough
- **Depends on:** 12.1
- `./gradlew lint` to zero issues repo-wide; walk every list/detail/form screen from Phases 3–11
  confirming explicit loading/empty/error states; final pass against SPEC §8's feature list and
  §11's Success Criteria as literal checklists.
- **Acceptance:** lint clean; every screen demonstrably handles all three states (verify by
  stopping the backend mid-session); SPEC §8/§11 checklists fully checked or explicitly annotated
  as a known gap.
- **Verify:** `docker compose stop backend` mid-session, confirm no screen shows a
  blank/unhandled-error page (rubric §6 "app must not crash under normal use"); restart backend,
  confirm recovery without app relaunch.

### Task 12.3 — App icon, display name, screenshots for the report
- **Depends on:** 12.2
- App icon (adaptive icon, brand-crimson background per SPEC §7) and display name ("HR Leave",
  matching the Flutter app's naming for product consistency). Capture screenshots of every major
  screen (per rubric §13's required Project Report content) on the emulator or a physical device.
- **Acceptance:** launcher shows the correct icon/name; a screenshot exists for every screen in
  SPEC §8's feature list.
- **Verify:** manual — confirm launcher icon/name; confirm screenshot set is complete against the
  feature checklist.

**Checkpoint 12 (final)** — `./gradlew lint` clean, `./gradlew testDebugUnitTest` green, installed
and exercised end-to-end on an emulator/device with no crashes; report materials (screenshots,
feature list, architecture explanation, installation instructions) ready per rubric §13.

---

## Phase 13 — UI/UX Consistency Revamp (match the Flutter client's actual design system)

Added post-Checkpoint-12, after the user tested the app on a physical device and found the UI
inconsistent with the Flutter sibling app. SPEC.md §7's design tokens are **stale** — the Flutter
app's implemented UI (`../hr-leave-management-flutter`) has drifted from that doc over a
redesign, and the Flutter app is the real consistency target now, not the spec doc. Ground truth
for every task below is a fork analysis of the actual Flutter Dart source
(`lib/app/theme/app_theme.dart`, `lib/widgets/`, and one representative screen per archetype),
not SPEC.md.

**Verified Flutter design tokens** (supersede SPEC.md §7 for this phase and should be written back
into SPEC.md §7 once this phase lands):

| Token | Value |
|---|---|
| Primary / primaryDark | `#E23744` / `#C01F2B` |
| Danger / warning / success / info | `#EF4444` / `#F5A623` / `#22A659` / `#4C8DFF` |
| Light background / surface / field fill / border | `#FFFFFF` / `#FFFFFF` / `#F7F7F9` / `#EAEAEE` |
| Dark background / surface | `#121212` / `#1E1E1E` (unchanged from SPEC) |
| Button / card / field / pill radius | 14dp / 18dp / 12dp / 999dp (full pill) |
| Spacing scale | xs 4, sm 8, md 12, lg 16, xl 24 |
| Button min height | 54dp |
| Font | Poppins (unchanged from SPEC) |
| Text fields | **filled** (`lightFieldFill` bg), not outlined-transparent; border only up/focused |
| AppBar | flat, elevation 0, `centerTitle:false`, no drawer hamburger |

**Navigation shell** (replaces the drawer entirely): persistent bottom-tab bar — **Home, Leaves,
Calendar, Profile** — with per-tab state preserved across switches (Flutter uses `IndexedStack`;
Android equivalent is a saveable nested nav-graph per tab or manual state hoisting, not literal
`IndexedStack`). Center-docked FAB (+, opens a bottom sheet: "Request Leave" / "Plan Leave").
Notification bell = circular chip icon button with a small badge, top-right of the app bar. The
**Leaves** tab hosts a pill-shaped segmented control switching **Requests / Plans** (not two
separate nav destinations). Approvals, Notifications, all 6 Admin resources, Recommendations, and
every detail/create/edit form are pushed as back-arrow secondary screens *outside* the 4 tabs.
Approvals is reached only via a dashboard stat card (isApprover-gated) — no drawer/menu entry.
**Admin CRUD entries and Change Password move under the Profile tab** (Profile becomes the
settings/admin hub), not a drawer section.

### Task 13.1 — Design tokens overhaul
- **Depends on:** nothing (foundational; everything else in this phase builds on it)
- Rewrite `core/theme/Color.kt` to the verified token table above (add `LightFieldFill`,
  `InfoColor`; correct `BrandPrimary`/`DangerColor`/`WarningColor`/`SuccessColor`/`LightBorder`).
  `core/theme/Shape.kt`: button/card/field/pill radii (14/18/12/999). Add an `AppSpacing` object
  (xs/sm/md/lg/xl) alongside `Type.kt`. Update `Theme.kt`'s Material3 `ColorScheme` mapping and
  the global `OutlinedTextField`→filled-field default (via a shared `TextFieldDefaults` object or
  a small `AppTextField` wrapper composable used everywhere instead of raw `OutlinedTextField`).
  Update every button usage's min height to 54dp (a shared `AppButton`/`AppOutlinedButton`
  wrapper, or a `ButtonDefaults` object, so this isn't hand-applied per call site).
- **Acceptance:** one source of truth for every token; no screen references a raw hex or
  ad-hoc corner-radius value; `./gradlew lint`/`assembleDebug` clean.

### Task 13.2 — Navigation shell rewrite (bottom tabs + center FAB + bottom sheet)
- **Depends on:** 13.1
- Replace `AuthenticatedShell`'s `ModalNavigationDrawer` with a `Scaffold` using
  `NavigationBar`/`BottomAppBar` + `FloatingActionButton` (`centerDocked` equivalent — Compose
  `FabPosition.Center` with a cutout `BottomAppBar`), 4 items (Home/Leaves/Calendar/Profile) with
  outlined↔filled icon swap on selection. Each tab's own back stack survives tab switches (nested
  `NavHost` per tab, or `rememberSaveable`-backed state — pick one approach and document it).
  FAB opens a `ModalBottomSheet` with 2 options routing into the Leave Request / Leave Plan
  Request create forms. Notification bell moves into the flat AppBar's actions as a circular chip
  IconButton + badge. Build the **Leaves** tab as a pill `TabRow`/`SegmentedButton` switching
  between the existing `LeaveRequestsListScreen`/`LeavePlanRequestsListScreen` (reuse those
  screens unmodified — only the container around them changes). Move Approvals/Notifications/all
  6 Admin routes/Recommendations/every detail+form route to back-arrow-pushed screens reachable
  only via their existing entry points (dashboard stat card, Profile tab list, FAB sheet, list-row
  taps) — none of them get a persistent nav-bar slot.
- **Acceptance:** no drawer anywhere in the app; 4 bottom tabs preserve scroll/state across
  switches; FAB → bottom sheet → correct create form; Leaves tab's pill toggles Requests/Plans in
  place; every previously-drawer-only route (admin, approvals, notifications, recommendations) is
  still reachable, just relocated.

### Task 13.3 — Dashboard rebuild
- **Depends on:** 13.2
- Replace the `LazyVerticalGrid`/chunked-row quick-actions grid with Flutter's actual layout: a
  `LazyColumn` of (1) profile card with a pastel-primary circular avatar (initials) + name/email/
  team, (2) a 2-up row of pastel action tiles ("Request Leave" tinted primary, "Plan Leave" tinted
  warning) linking straight to the create forms, (3) a `StatCard` row — "Available Days" (info
  color, always visible, sum of available balances) + "Approvals" (warning color, isApprover-only,
  tappable → Approvals) — as equal-height cards, (4) "Leave Balances" section title + the existing
  balance rows, (5) a single "Quick actions" section with one row linking to Recommendations only
  (Schedule/Leave Requests/Leave Plan Requests quick-action tiles are removed — those are now
  bottom-tab destinations, not dashboard tiles).
- **Acceptance:** dashboard visually matches Flutter's structure; no leftover grid tiles for
  destinations that are now bottom tabs.

### Task 13.4 — Shared component library pass
- **Depends on:** 13.1
- New/updated `core/ui` components mirroring Flutter's `lib/widgets/`: `StatusChip` → pill shape
  everywhere (standardize on the pill, not Flutter's own inconsistent radius-8-vs-pill split —
  don't replicate that bug); a `StatCard` composable (icon, label, value, tint color); a
  `PastelActionTile` composable (tinted background + icon + label); an `ErrorStateView`/
  `EmptyStateView` pair used consistently instead of ad-hoc `Column`+`Text`+`Button` per screen;
  a `StickyBottomActionPanel` (top shadow, `SafeArea`-equivalent padding) for draft-only actions
  on detail screens; a `SplitActionButtons` composable (primary + outlined pair, equal width).
- **Acceptance:** these replace the inline per-screen state-handling code built across Phases
  3-11 without changing any ViewModel/UiState contracts — pure UI-layer swap.

### Task 13.5 — List/detail screen polish
- **Depends on:** 13.4
- Apply the Card→InkWell(clickable)→Padding(16)→header-row(title+badge)+icon-detail-rows pattern
  to Leave Requests, Leave Plan Requests, Approvals, Notifications, and Schedule's list sections.
  Detail screens (`LeaveRequestDetailScreen`, `LeavePlanRequestDetailScreen`) get the sectioned-
  card layout (info card, optional description card, timeline/workflow card with dot markers) and
  the sticky bottom action panel for draft actions (Submit primary, Edit/Delete outlined pair).
  Approvals gets pill type badges (leave-type name, primary-tinted) instead of a status badge
  (everything there is implicitly pending), and the Approve button explicitly overridden to
  `SuccessColor` (the one place the primary brand color is deliberately swapped).
- **Acceptance:** visually consistent card/badge/detail-row language across every list and detail
  screen; sticky action panel present on both Leave Request and Leave Plan Request detail screens.

### Task 13.6 — Form screen polish
- **Depends on:** 13.4
- Apply the bold-label-above-field convention (replace `OutlinedTextField`'s built-in floating
  label with a `Text` label + 8dp gap + filled field) across every form (Login, Forgot/Reset
  Password, Change Password, Leave Request form, Leave Plan Request form). Forms reached as a
  create/edit modal get a close "X" icon instead of a back arrow; read-only pushed screens keep
  the back arrow. Error banner: tinted danger container (10% bg, 30% border, `error_outline` icon
  + text) instead of a plain red `Text`. Date fields become a custom `InkWell`+`Container` styled
  like the filled input decoration (not a raw `DatePickerField` `OutlinedTextField`) — keep the
  existing `DatePickerField`'s dialog logic, only restyle the trigger surface. Leave Plan form's
  date chips: bordered container (min-height 80dp) holding a `FlowRow` of `InputChip`s with a
  calendar-icon avatar + delete icon, empty-state hint text when no dates added yet. Submit-button
  hierarchy: create mode = full-width primary "Submit" + outlined "Save as Draft" beneath it;
  edit mode = single full-width primary "Update".
- **Acceptance:** every form uses the same label/field/error/button conventions; no screen still
  uses a bare `OutlinedTextField` with a floating label.

### Task 13.7 — Admin CRUD + Profile hub restyle
- **Depends on:** 13.2, 13.4
- Move all 6 admin entries and Change Password out of the drawer (already gone per 13.2) and into
  a Profile-tab "Admin" section (superuser-only, bold section label + `ListTile`-style rows with
  leading icon + chevron), matching Flutter's Profile-as-settings-hub structure. Restyle
  `GenericCrudListScreen`/`GenericCrudFormDialog` with the new tokens (filled search field, pill
  row badges where applicable, 18dp card radius). **Keep** Android's search+sort toggle — Flutter
  has no sort control; that's a legitimate improvement over the Flutter client, not a
  divergence to fix. Leave `FieldSpec.required` defaulting as-is (Android defaults `true`,
  Flutter defaults `false`) — not a visual concern, no behavior change needed.
- **Acceptance:** Profile tab is the sole entry point for admin resources and Change Password;
  admin screens visually match the new token set; search+sort both still work.

### Task 13.8 — Auth screens polish
- **Depends on:** 13.6
- Two-tone wordmark (`Text` with two `TextSpan`-equivalent styles: "HR" bold primary + " Leave"
  bold default-color) replacing the current single-style "HR Leave" title on Login and a
  standalone splash/bootstrap screen (34dp, static, no spinner — distinct from the existing
  `LoadingSplash` which can keep its spinner for the actual bootstrap wait). Login headline:
  two-tone "Log In to your " + "Account" (primary), 28dp bold. Apply Task 13.6's label/field/
  button conventions to Login/Forgot/Reset Password (already covered by 13.6 but called out here
  as this task's specific verify target).
- **Acceptance:** Login/Forgot/Reset Password visually match Flutter's auth screens' branding and
  field conventions.

**Checkpoint 13 (final)** — every screen reviewed against the Flutter app's actual UI for visual
consistency (colors, shapes, spacing, nav shell, component patterns); `./gradlew lint` clean,
`./gradlew assembleDebug` clean; SPEC.md §7 updated to the verified token table so it stops being
stale for future work.

---

## Sequencing

```
Phase 0 (scaffold+infra)
  → Phase 1 (auth/session)
    → Phase 2 (shell/nav/dashboard)
      → Phase 3 (balances)
        → Phase 4 (leave requests)
          → Phase 5 (leave plan requests)
            → Phase 6 (AI recommendations)   ← headline feature, needs 3+5
            → Phase 7 (approvals)             ← needs 2.2+4+5
          → Phase 8 (schedule)                ← needs 2.1 only, can run parallel to 4-7
          → Phase 9 (notifications)           ← needs 2.1+1.2 only, can run parallel to 4-8
            → Phase 10 (admin CRUD)           ← needs 2.1 only, kept toward the end by decision
              → Phase 11 (profile & identity) ← needs 1.2 only, low-risk, any time after Phase 1
                → Phase 12 (hardening + report)
```

Phases 8 and 9 have no dependency on 4–7 and can be pulled earlier by a second contributor or
interleaved if working solo and a change of pace is useful — sequenced after Phase 7 here only to
match reading order with the SPEC's feature list.

## Critical Files

- `core/network/RetrofitClient.kt` / `AuthInterceptor.kt` — every repository depends on this
  being correct first.
- `core/errors/ApiError.kt` — FastAPI's dual error shape (`detail: string` vs `detail: [...]` for
  422) handled once here.
- `core/navigation/NavGraph.kt` — route table + auth-gated start destination, gates every
  authenticated screen.
- `feature/auth/AuthViewModel.kt` (or equivalent shared auth state holder) — owns
  login/logout/session state and the cached current user, read by nearly every other feature for
  role/owner-id checks.
- `data/repository/TeamsRepository.kt` — powers both the team-owner detection heuristic (Phase 2)
  and the Teams admin screen (Phase 10).

## Overall Verification

Each task above has its own verify step against the real running backend. End-to-end acceptance
for the whole plan: three test accounts (superuser, team-owner employee, plain employee) can each
complete their full respective workflows — plain employee submits a leave request and an
AI-recommended leave plan; team-owner approves/rejects both from a second account; superuser
manages all master data — entirely through the Android app with zero direct Swagger UI use once
Phase 10 lands. This mirrors the Flutter app's own overall verification, satisfying the "1 API, 2
apps" project structure with both clients independently exercising the full backend contract.
