# HR Leave — Android Client

A native Android app for the HR Leave Management system, built for the "Android Application
Development" master's final exam project. It is a second, independent client (alongside a sibling
Flutter app) against the same existing FastAPI backend — this repo only consumes that API, it does
not implement or modify it.

## Overview & Objective

Leave requests, approvals, and team schedules are normally juggled over email/spreadsheets. This
app gives every employee a single mobile surface to submit and track leave, see their team's
schedule and public holidays, and get AI-assisted leave-date recommendations — while giving team
owners and superusers the management tools (approvals queue, master-data CRUD) they need without
a desktop session.

The app is role-adaptive from a single codebase and login flow:

- **Employee** (default): profile, leave balances, submit/track leave requests and leave plan
  requests, AI-recommended leave dates, team/holiday schedule, notifications.
- **Team owner / approver**: everything above, plus an approvals queue for their team's pending
  requests.
- **Superuser**: everything above, plus admin CRUD over users, teams, leave types, public holidays,
  and policies.

## Feature List

- **Authentication** — OAuth2 password-flow login, logout, forgot password, reset password. No
  refresh token endpoint exists on the backend; the 8-day access token is the session lifetime, and
  the app re-prompts login on a 401.
- **Dashboard** — role-adaptive home: profile card, Request Leave / Plan Leave action tiles,
  Available Days / Approvals stat row, leave balances, and a Recommendations entry point.
- **Leave Requests** — list, detail, create/submit, draft → submit → approve/reject lifecycle.
- **Leave Plan Requests** — same lifecycle as above, for multi-date leave plans.
- **AI Recommendations** — fetches suggested leave dates, lets the user select from them, and
  submits the result as a leave plan request.
- **Schedule** — month calendar showing public holidays and the caller's team's approved leave.
- **Approvals queue** (team owner) — approve/reject pending requests from their team.
- **Notifications** — in-app list with unread-count badge, polled while logged in; also posts a
  local system notification on new items, gated behind the runtime `POST_NOTIFICATIONS` permission
  (Android 13+) and degrading gracefully (badge-only) if denied.
- **Master Data admin** (superuser) — full CRUD + search + sort over Users, Teams, Leave Types,
  Public Holidays, Policies, and Leave Balances, through one reusable generic CRUD screen.
- **Profile** — view/edit own profile, change password.

## Architecture

**MVVM**, feature-by-package:

```
core/       cross-cutting: network (Retrofit/OkHttp), storage (encrypted token store),
            navigation, theme, error mapping, shared UI, notifications
data/       remote/api (Retrofit service interfaces), remote/dto, repository (one per
            backend resource — the only thing ViewModels talk to)
feature/    one package per screen area (auth, dashboard, leaverequests, leaveplanrequests,
            recommendations, approvals, schedule, notifications, admin/*, profile), each
            holding a ViewModel + UiState + Composable screen(s)
```

- Every screen has a `ViewModel` exposing a `StateFlow<UiState>`; Composables only read state and
  forward user actions — no business logic in Composables or in `MainActivity`, which is a single
  ~15-line Activity that just applies the theme and hosts the nav graph.
- Repositories are the only layer that calls the network layer, and always return a uniform
  `AppResult<T>` (`Success`/`Failure`) so every ViewModel handles loading/error state the same way.
- Dependency injection via Hilt (network clients, repositories, and Retrofit services are all
  provided, not constructed ad hoc).
- Networking via Retrofit + OkHttp, with kotlinx.serialization for JSON (not Gson/Moshi).
- Navigation via Navigation-Compose, single-Activity/single-NavHost.
- Secure local storage: the JWT access token lives in `EncryptedSharedPreferences`
  (`androidx.security:security-crypto`), never in plaintext prefs or source. No local database — the
  backend REST API is the app's data source of record, satisfying the "at least one advanced data
  source" requirement without a local cache.

### On RecyclerView

This app is built entirely in Jetpack Compose, which has no `RecyclerView`, `RecyclerView.Adapter`,
or `ViewHolder` — Compose's own recommended replacement for that pattern is a lazy list
(`LazyColumn`/`LazyRow`/`LazyVerticalGrid`) rendering keyed item composables. Every list in this app
(admin CRUD lists, leave request lists, notifications, schedule) is built this way. It is the same
underlying idea as RecyclerView — efficient, recycled rendering of a data list — expressed through
Compose's declarative primitives instead of the imperative View-system classes, and it is Google's
own documented migration path off RecyclerView, not a workaround.

## Tech Stack

- Kotlin, Jetpack Compose (Material Design 3), dark mode via an explicit in-app toggle
- Hilt (dependency injection)
- Retrofit + OkHttp + kotlinx.serialization
- Navigation-Compose
- androidx.security-crypto (encrypted token storage)
- JUnit4 + MockK + kotlinx-coroutines-test (unit tests)

## Installation & Running

Prerequisites: Android Studio (latest stable), a running instance of the backend from
`../hr-leave-management/backend` (see that repo's README).

1. Clone this repository and open it in Android Studio.
2. Create `local.properties` in the project root (or add to the existing one) with the backend's
   base URL:
   ```
   API_BASE_URL=http://10.0.2.2:8000/api/v1
   ```
   `10.0.2.2` is the Android emulator's alias for the host machine's `localhost`; use the backend's
   actual LAN address instead if running on a physical device. If omitted, this value is the
   default fallback.
3. Sync Gradle, then build and run:
   ```
   ./gradlew assembleDebug
   ./gradlew installDebug   # with a device/emulator connected
   ```
4. Run unit tests / lint:
   ```
   ./gradlew testDebugUnitTest
   ./gradlew lint
   ```

## Screenshots

_These are the Flutter sibling app's screens (`ui/` folder) — the exact design reference this
Android client's UI was built and polished to match (see Phase 13/14 in `tasks/todo.md` and
`CONTRIBUTING.md`). They are not screenshots of this Android app itself; this app's own
screenshots are still to be added._

| Login | Home / Dashboard | Leave Requests |
|:---:|:---:|:---:|
| ![Login](ui/login.jpg) | ![Home](ui/home.jpg) | ![Leave Requests](ui/leave-list.jpg) |

| Request Detail | Leave Request Form | Schedule |
|:---:|:---:|:---:|
| ![Request Detail](ui/detail_page.jpg) | ![Leave Request Form](ui/leave-form.jpg) | ![Schedule](ui/schedule.jpg) |

| Notifications | Profile |
|:---:|:---:|
| ![Notifications](ui/notification.jpg) | ![Profile](ui/profile.jpg) |
