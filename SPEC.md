# HR Leave Management — Native Android Client — SPEC

## 1. Objective

A native Android (Kotlin) client for the existing HR Leave Management system, built as the
"Android Application Development" master's final exam project. It consumes the same, already-built
FastAPI backend from `../hr-leave-management/backend` (API contract documented in
`../hr-leave-management/PROJECT_FEATURES.md`) that the sibling `../hr-leave-management-flutter`
app consumes — this is a second, independent client against one backend, not a new backend.

**Target users**, single app, role-adaptive UI (mirrors the Flutter client's role model exactly):
- **Employee** (default role): view profile, leave balances, submit/track leave requests and leave
  plan requests, get AI-recommended leave dates, view team/holiday schedule, receive notifications.
- **Team owner / approver** (`current_user.team.team_owner`): everything an employee can do, plus
  an approvals queue (approve/reject pending requests from their team).
- **Superuser**: everything above, plus admin management of users, teams, leave types, public
  holidays, and policies.

The AI leave-plan recommender (`GET /recommends/leave-plan`) is a headline feature — give it a
dedicated dashboard entry point and a recommendation → selection → submit flow, per the Flutter
client's precedent.

Success looks like: a Kotlin/Compose app that satisfies the course's Android Application
Development rubric (see §11), presents the same brand and information architecture as the Flutter
app for product consistency, and demonstrates independent understanding of native Android
fundamentals rather than a straight transliteration of the Flutter codebase.

## 2. Tech Stack

- **Kotlin**, Jetpack Compose (Material Design 3)
- **Architecture**: MVVM — `ViewModel` + `StateFlow`/`UiState` sealed classes per screen; no
  business logic in Composables or Activities (rubric §3)
- **Navigation**: Navigation-Compose
- **DI**: Hilt
- **Networking**: Retrofit + OkHttp (auth interceptor, logging interceptor debug-only) +
  kotlinx.serialization for JSON
- **Secure token storage**: `androidx.security:security-crypto` (EncryptedSharedPreferences) for
  the JWT access token
- **Lists**: `LazyColumn`/`LazyVerticalGrid` with stable, keyed item composables — the Compose
  equivalent of the rubric's mandatory RecyclerView/ViewHolder pattern (see §11.1 for the
  documented rationale)
- **Auth**: OAuth2 password flow against `POST /api/v1/login/access-token` (form-encoded body,
  same as Flutter client)
- Min SDK 21, target SDK latest stable, per rubric §1

Backend base URL is configurable per build variant/environment (e.g. `http://10.0.2.2:8000` for the
Android emulator talking to a backend on the host machine); default local backend runs on port 8000
with API prefix `/api/v1`.

## 3. Commands

- `./gradlew assembleDebug` — build debug APK
- `./gradlew installDebug` — install on connected device/emulator
- `./gradlew testDebugUnitTest` — run unit tests
- `./gradlew lint` — static analysis (must be clean before considering a task done)
- `./gradlew ktlintFormat` — formatting (if ktlint is added; otherwise rely on Android Studio's
  Kotlin formatter)

## 4. Project Structure

```
app/src/main/java/com/mitclass/hrleave/
  App.kt                        # Application class, Hilt entry point
  MainActivity.kt                # single-Activity host, sets up NavHost
  core/
    network/                      # Retrofit client, auth interceptor, 401 handling
    storage/                       # EncryptedSharedPreferences token store, DataStore prefs
    di/                             # Hilt modules (network, storage, repositories)
    theme/                           # Color.kt, Type.kt, Shape.kt, Theme.kt (MaterialTheme)
    navigation/                       # NavGraph, Routes/Destinations
    errors/                            # exception -> user-facing message mapping
    common/                             # shared composables (buttons, cards, chips, empty states)
  data/
    remote/dto/                          # DTOs matching backend *Public/*Create/*Update shapes
    remote/api/                           # Retrofit service interfaces, one per backend resource
    repository/                            # one per resource (auth, users, teams, leave_types,
                                            # public_holidays, policies, leave_balances,
                                            # leave_requests, leave_plan_requests, recommends,
                                            # approvals, schedule, notifications)
  feature/
    auth/                                  # login, forgot/reset password
    dashboard/                              # role-adaptive home
    leaverequests/                           # list/detail/create/submit
    leaveplanrequests/                        # list/detail/create/submit
    recommendations/                           # AI recommender flow
    approvals/                                  # team-owner approve/reject queue
    schedule/                                    # month calendar (holidays + team leave)
    notifications/                                # in-app list + unread badge + local notif
    admin/                                          # superuser: users/teams/leave-types/
                                                     # holidays/policies/leave-balances
    profile/
app/src/test/java/com/mitclass/hrleave/         # unit tests, mirrors main/ package structure
```

Each `feature/<x>/` contains `<X>ViewModel.kt`, `<X>UiState.kt`, and one or more
`<X>Screen.kt` composables. No `feature/` package reaches into another `feature/` package directly
— shared state crosses through `data/repository/` or navigation arguments only.

## 5. Code Style

- Kotlin official code style (Android Studio default formatter).
- DTOs are `@Serializable` data classes; field names mirror backend field names exactly (see
  `../hr-leave-management/PROJECT_FEATURES.md` §2–15) to avoid mapping bugs.
- ViewModels expose a single `StateFlow<UiState>` (sealed class: `Loading` / `Success` /
  `Error`) per screen; Composables are stateless functions of that state plus event callbacks.
- Meaningful names; no dead/commented-out code committed; no hardcoded strings in Composables —
  use `strings.xml` (rubric §11).

Example shape:
```kotlin
sealed interface LeaveRequestsUiState {
    data object Loading : LeaveRequestsUiState
    data class Success(val items: List<LeaveRequest>) : LeaveRequestsUiState
    data class Error(val message: String) : LeaveRequestsUiState
}

@HiltViewModel
class LeaveRequestsViewModel @Inject constructor(
    private val repository: LeaveRequestsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<LeaveRequestsUiState>(LeaveRequestsUiState.Loading)
    val uiState: StateFlow<LeaveRequestsUiState> = _uiState.asStateFlow()

    fun load() = viewModelScope.launch {
        _uiState.value = LeaveRequestsUiState.Loading
        _uiState.value = runCatching { repository.list() }
            .fold(LeaveRequestsUiState::Success, { LeaveRequestsUiState.Error(it.toUserMessage()) })
    }
}
```

## 6. Testing Strategy

Per rubric §12, testing is "recommended" (manual) with unit tests as bonus — not the primary
investment, matching the Flutter client's posture for consistency across both course reports.

- **Primary**: manual testing of all major flows during development and before demo/submission.
- **Minimal automated coverage**: unit tests only for logic that's easy to get subtly wrong and
  hard to eyeball — JWT expiry/session handling, leave-balance/available-balance display math,
  duplicate-date validation before submit (mirrors the Flutter client's test scope exactly, so
  both reports can cite the same tested edge cases).
- Edge cases to handle in UI regardless of automated coverage (rubric §12): empty lists, invalid
  input, network failure/offline.

## 7. Design Tokens (shared brand — mirrors the Flutter client for product consistency)

Reused verbatim from `../hr-leave-management-flutter/SPEC.md` §9, which itself was adopted from a
Figma Community HRMS reference (design direction only, no copied assets):

| Token | Value |
|---|---|
| Accent/primary | `#CA282C` (brick red) |
| Dark background / surface | `#121212` / `#1E1E1E` |
| Light background / surface | `#F7F7F8` / `#FFFFFF` |
| Light border | `#E1E1E4` |
| Danger / warning / success | `#CA282C` / `#FB8C00` / `#2E7D32` |
| Font | Poppins (bundled `.ttf` under `res/font/`, or Downloadable Fonts API) |
| Button shape | 12dp corner radius (not a full pill), 52dp min height |
| Card shape | 14dp corner radius, subtle elevation |
| Text field shape | 12dp corner radius, outlined (`OutlinedTextField`), border from `lightBorder`,
  primary-colored focus border |
| Default theme mode | Light-first (dark mode available as a toggle) |
| Icons | Material Symbols "outlined" variant |
| Dashboard navigation pattern | 2-column `LazyVerticalGrid` of icon+label tiles |
| Choice inputs | Segmented `FilterChip`/`SegmentedButton` toggles for binary/small-set choices
  (e.g. Full day/AM/PM, Yes/No) |

## 8. Feature List (maps to the Flutter client's feature list — see reference doc §17 in that repo)

- **Authentication**: login (OAuth2 password flow), logout (clear token), forgot password
  (`POST /password-recovery/{email}`), reset password (`POST /reset-password/`). No refresh token
  — the backend issues only an 8-day access token; re-prompt login on 401.
- **User Management**: self profile view/edit (`GET/PATCH /users/me`), change password; superuser
  CRUD on all users (`/users`).
- **Dashboard**: role-adaptive summary — leave balances, and (team owners only) a tappable
  "Pending Approvals" count opening the Approvals queue directly, entry point to AI
  recommendations. 2-column quick-actions grid; Profile and Approvals reachable via drawer/nav
  only, not duplicated as grid tiles.
- **Master Data** (admin/superuser CRUD, search + pagination): Teams, Leave Types, Public
  Holidays, Policies, Leave Balances (`owner_id` + `leave_type_id` pickers, `year` as 4-digit
  string, `balance` decimal; `taken_balance`/`available_balance` are server-computed).
- **Schedule (employee view)**: month-view calendar showing public holidays and the caller's
  team's approved leave, backed by `GET /schedule/?year&month`. Two visually distinct markers for
  holidays vs. team leave, with list sections beneath the calendar grid.
- **Business Module**: Leave Balances (self view; full CRUD for superusers), Leave Requests
  (draft/submit/approve/reject lifecycle), Leave Plan Requests (multi-date, same lifecycle), AI
  Recommendation flow (`GET /recommends/leave-plan` → user selects dates → `POST
  /leave-plan-requests` → `PUT /{id}/submit`).
- **Notifications**: in-app list + unread-count badge (`GET/PUT /notifications/*`), polled every
  30s while logged in. Additionally posts a local Android system notification when the unread
  count increases, gated behind the `POST_NOTIFICATIONS` runtime permission (API 33+) — this is
  the app's runtime-permission demonstration for rubric §9; degrades gracefully (badge-only, no
  system notification) if denied. Tapping a notification marks it read and opens the relevant
  list screen (by `entity_type`).
- **Audit Log**: out of scope — no audit-trail model/endpoint exists in the backend (matches the
  Flutter client's documented gap).

## 9. Known Gaps vs. Course Rubric (decided, documented — not open questions)

| Rubric requirement | This app's approach | Decision |
|---|---|---|
| RecyclerView (mandatory) + custom Adapters/ViewHolders | Jetpack Compose `LazyColumn`/`LazyVerticalGrid` with keyed item composables | Compose is the modern Android-recommended replacement for RecyclerView; it satisfies the same underlying intent (efficient, recycled list rendering) without the literal `RecyclerView.Adapter`/`ViewHolder` classes. Documented explicitly in the submitted report's Architecture section, same pattern as the Flutter client's documented Spring Boot→FastAPI substitution. |
| Refresh Token flow (implied by "session handling" best practice) | None — backend has no refresh endpoint | Same as Flutter client: treat the 8-day access token as the session lifetime, re-prompt login on 401. |
| Room / SQLite local storage | Not implemented in v1 (REST-only satisfies rubric §7's "at least ONE advanced data source") | Deferred as a stretch item; would need a repository-level cache + sync strategy if pursued later for the "Offline support" bonus line. |

## 10. Boundaries

- **Always**: run `./gradlew lint` and `./gradlew assembleDebug` clean before marking a task done;
  keep DTOs in sync with the documented API contract in
  `../hr-leave-management/PROJECT_FEATURES.md`; no business logic in Composables/Activities
  (ViewModels/repositories only).
- **Ask first**: before pushing to the `origin` remote of this repo; before modifying anything in
  the `hr-leave-management` (backend/frontend) repo — this Android repo should only *consume* that
  API, not change it, unless a real gap is found and confirmed with the user; before adding new
  Gradle dependencies not already listed in §2; before requesting any runtime permission beyond
  `POST_NOTIFICATIONS`.
- **Never**: commit secrets, `local.properties` values, or `.env`-equivalent config; hardcode the
  backend base URL in more than one place (use build-config/env, not scattered through the
  codebase); hardcode any sensitive data (rubric §9).

## 11. Success Criteria

- App builds and runs via `./gradlew assembleDebug` with zero Gradle errors (rubric §1).
- All roles (Employee, Team owner, Superuser) can complete their full flow end-to-end against a
  local backend instance without crashing (rubric §2, §6, §10).
- Minimum 4–5 distinct screens is trivially exceeded given the feature list in §8; architecture is
  MVVM throughout with no business logic in Composables (rubric §3).
- Full CRUD + search/sort on at least the admin Master Data resources, with data persisting via
  the backend across app restarts (rubric §8).
- `POST_NOTIFICATIONS` permission requested at runtime, and the app functions correctly (badge
  still works) if denied (rubric §9).
- Report/README includes app overview, feature list, architecture explanation, screenshots of all
  major screens, and installation instructions (rubric §13).

## 12. Open Questions

- Exact final package name (`com.mitclass.hrleave` assumed — confirm or change before scaffolding
  the Gradle project).
- Whether ktlint/detekt get added for automated formatting/lint beyond Android Studio's built-in
  formatter and `./gradlew lint`.
- Whether screenshots for the report are captured against the emulator or a physical device.
