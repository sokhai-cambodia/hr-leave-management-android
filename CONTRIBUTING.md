# Contributing — HR Leave Android Client

This is the how-to-work-on-it doc. For the what/why (features, architecture, tech stack), read
[`README.md`](./README.md) first — this file assumes you've already read that.

## Orientation

This repo is one of three:

- `hr-leave-management` — the FastAPI backend + web frontend. This Android app **only consumes**
  its REST API; never edit anything in that repo from here.
- `hr-leave-management-flutter` — a sibling client app against the same backend, built for a
  different course. Useful as a reference for how a feature/flow was already solved once, but not
  something to transliterate line-for-line (different toolkit, different rubric).
- `hr-leave-management-android` (this repo).

## Environment Setup

- Android Studio (latest stable), JDK 17.
- minSdk 26, targetSdk 35 — nothing older to worry about.
- **Backend**: the team shares one running backend instance rather than everyone standing up
  their own. Ask the project owner for:
  - the backend base URL
  - a test login (email/password) for each role you need (employee / team owner / superuser)

  <!-- TODO(owner): fill in the actual shared backend URL and test credentials here, or link to
       wherever the team keeps them (e.g. a pinned message, shared doc — not committed to git). -->

- Create (or edit) `local.properties` in the project root — it's gitignored, never committed:
  ```
  API_BASE_URL=<shared backend URL>/api/v1
  ```
  If you omit this, the app falls back to `http://10.0.2.2:8000/api/v1` (only correct if you're
  running the backend locally yourself, which is not the expected setup for this team).
- **Use the deployed HTTPS backend, not a local dev server, on a physical device.** This app has no
  network security config, and Android blocks plain-HTTP network calls by default on targetSdk 28+
  (this app targets 35). The shared deployed instance runs behind HTTPS (Traefik), so pointing at
  it just works — but if you ever point `API_BASE_URL` at a raw local `http://` dev server instead,
  every network call on a real phone will fail with a `CLEARTEXT communication ... not permitted`
  crash. That error message is the tell if this happens to you.
- **`local.properties` is read once at build time, not live-reloaded.** It's baked into
  `BuildConfig.API_BASE_URL` when Gradle configures the project. If you edit it after an initial
  sync, re-sync Gradle (or just re-run `./gradlew assembleDebug`) — otherwise the app keeps using
  the old value with no visible warning.
- First build: `./gradlew assembleDebug`.

## Why the Stack Looks Like This

A few choices that aren't obvious if you're newer to Android/Compose:

- **Hilt** provides every Retrofit service, repository, and the token store — you `@Inject` them
  into a `@HiltViewModel` rather than constructing anything by hand. If you add a new
  repository/API service, wire it into the relevant Hilt module rather than `new`-ing it up in a
  ViewModel.
- **No RecyclerView.** This app is 100% Jetpack Compose, and Compose's own recommended replacement
  for `RecyclerView`/`Adapter`/`ViewHolder` is a lazy list (`LazyColumn`/`LazyRow`) rendering keyed
  item composables — see `README.md`'s "On RecyclerView" section for the full reasoning. If you've
  only done View-system Android before, this is the one thing that'll feel most unfamiliar.
- **`AppResult<T>` + `safeApiCall {}`** (`core/network/AppResult.kt`) is the one place network
  exceptions get turned into a `Success`/`Failure` value. Every repository method returns
  `AppResult<T>`; don't add a second error-handling convention (try/catch scattered per call site)
  — route new network calls through `safeApiCall`.

## Where Things Live

```
core/       cross-cutting: network, storage (encrypted token), navigation, theme, error
            mapping, shared UI, notifications
data/       remote/api (Retrofit interfaces), remote/dto, repository/ (ViewModels only ever
            talk to repository/, never directly to remote/api/)
feature/    one package per screen area, each with a ViewModel + UiState + Screen composable(s)
```

Rule: no `feature/<x>/` package reaches into another `feature/<y>/` package directly — shared
state crosses through `data/repository/` or navigation arguments only.

Living reference docs — read these instead of asking, they're kept current:

- [`SPEC.md`](./SPEC.md) — objective, tech stack, project structure, code style, boundaries
  (what's always/ask-first/never allowed), and a table of known/decided gaps vs. the course
  rubric (e.g. why there's no RecyclerView, no refresh token).
- [`STYLE_GUIDE.md`](./STYLE_GUIDE.md) — the shared brand's colors, spacing, corner radii, and
  typography tokens. Reuse these values; don't hand-roll a new color or spacing constant for one
  screen.
- [`tasks/plan.md`](./tasks/plan.md) — the phase-by-phase build plan and status. Check here first
  for "is this already built, and if so, where" before starting something that sounds new.
- [`ui/`](./ui/) — real screenshots of the **Flutter** sibling app (login, home, leave list/form,
  notifications, profile, schedule, business card), used as the ground-truth design reference for
  the Phase 13/14 visual-parity work. These are the other app, not this one — don't mistake them
  for screenshots of this Android client.

## Workflow

- **Planned phase work** (from `tasks/plan.md`): one feature branch per phase, branched off
  `main`, one commit per task within it — keep commits scoped, don't bundle unrelated tasks.
- **Anything else** (a bug fix, a small feature, something you noticed while testing that isn't on
  the phase plan): branch directly off `main` with a short descriptive name (e.g.
  `fix-dashboard-refresh-flicker`, `add-pull-to-refresh`) — don't wait for a "phase" to exist for
  it. One commit per distinct fix/change within that branch, same as phase work.
- Before calling any task done:
  ```
  ./gradlew assembleDebug
  ./gradlew lint
  ./gradlew testDebugUnitTest
  ```
  All three clean = done. The emulator is *not* required for routine verification — a teammate
  (or you) will check real on-device behavior separately; don't burn time booting it for every
  small change.
- This is a course submission: commit messages should read as normal engineering commits — no
  tool/assistant attribution, no mention of AI assistance, in commits, PR descriptions, or code
  comments.
- **No CI and no required review gate on this project.** Once the three commands above are clean
  and you've verified the actual behavior on-device, merge your branch back into `main` yourself
  and push — you don't need to wait for someone else to approve it. Opening a PR first is fine if
  you want a second pair of eyes on something risky, but it's your call, not a required step.
- Delete your branch (local and on origin) once it's merged — `git branch -d <name>` and
  `git push origin --delete <name>`. Keeps the branch list from accumulating merged history.

## Gotchas

- **No refresh token.** The backend only issues an 8-day access token. A 401 means "re-login,"
  not a bug — don't add retry/refresh logic for it.
- **No `is_team_owner` flag on `User`.** Approver status is computed client-side once per session:
  fetch `GET /teams` and check whether the current user is any team's `team_owner`.
- **No self-signup screen.** All accounts are provisioned by a superuser via the Admin Users
  screen — if you need a new test account/role, create it there (or ask whoever holds superuser
  credentials for the shared backend).
- **DTO field names mirror the backend exactly** (see
  `../hr-leave-management/PROJECT_FEATURES.md`) — don't rename fields for "nicer" Kotlin style,
  it breaks (de)serialization.

## Troubleshooting

- **Gradle sync fails / wrong JDK.** Android Studio bundles its own JDK (JetBrains Runtime) and
  uses it by default for Gradle — you shouldn't need to install JDK 17 separately. If Android
  Studio is using a different JDK, fix it under Settings → Build, Execution, Deployment → Build
  Tools → Gradle → Gradle JDK.
- **Android Studio prompts to install an SDK platform/build tools.** Expected on a fresh machine —
  accept the prompt (or open SDK Manager and install API 35). This isn't project-specific
  configuration, just a one-time IDE setup step.
- **Physical device shows as "unauthorized" or doesn't appear in the device dropdown.** Enable
  Developer Options (Settings → About Phone → tap Build Number 7 times) and USB Debugging inside
  it, then accept the "Allow USB debugging?" prompt that appears on the phone itself when you
  connect it — easy to miss since it only shows up once per machine.
- **App installs but nothing loads / spinner never resolves.** In order: confirm `local.properties`
  has `API_BASE_URL` set and you rebuilt after editing it (see Environment Setup above); confirm
  your device has internet access; confirm the backend URL actually resolves (open it in a mobile
  browser — you should get a response, not a timeout).
- **`CLEARTEXT communication ... not permitted` crash.** You're pointed at a plain-`http://`
  backend on a physical device — see the HTTPS note under Environment Setup. Point
  `API_BASE_URL` at the shared HTTPS instance instead.
- **Login fails with correct-looking credentials.** Accounts aren't self-service (see Gotchas
  below) — confirm you actually have a provisioned account for the role you're testing, rather than
  assuming one exists.

## Who to Ask

- Product/scope decisions, exam requirements, shared backend access → the project owner.
- Backend API contract/behavior questions → `../hr-leave-management/PROJECT_FEATURES.md` and that
  repo's own README first, project owner if still unclear.
