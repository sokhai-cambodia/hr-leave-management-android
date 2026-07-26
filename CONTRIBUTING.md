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

## Workflow

- One feature branch per phase, branched off `main`.
- One commit per task within that phase; keep commits scoped (don't bundle unrelated tasks).
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
- Merge the phase branch back to `main` once its tasks are done and verification is clean.

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

## Who to Ask

- Product/scope decisions, exam requirements, shared backend access → the project owner.
- Backend API contract/behavior questions → `../hr-leave-management/PROJECT_FEATURES.md` and that
  repo's own README first, project owner if still unclear.
