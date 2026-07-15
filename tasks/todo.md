# HR Leave Management — Native Android Client — Task Checklist

Full detail (acceptance criteria, verify steps) in `tasks/plan.md`.

## Phase 0 — Environment & Scaffolding
- [x] 0.1 Project scaffold, dependencies, folder skeleton, lint
- [x] 0.2 Core network/storage infra + connectivity smoke test
- [x] **Checkpoint 0**

## Phase 1 — Auth & Session
- [x] 1.1 Login + token storage + current-user fetch + minimal authenticated screen
- [x] 1.2 Session bootstrap + global 401 handling
- [x] 1.3 Forgot password + reset password
- [x] **Checkpoint 1**

## Phase 2 — App Shell, Role-Adaptive Navigation, Dashboard
- [x] 2.1 App shell + Navigation-Compose graph + static role-based nav (superuser branch)
- [x] 2.2 Team-owner detection + Approvals nav entry + Pending Approvals card
- [x] **Checkpoint 2**

## Phase 3 — Leave Balances
- [x] 3.1 Leave balances list, wired into dashboard
- [x] **Checkpoint 3**

## Phase 4 — Leave Requests
- [x] 4.1 List + detail (read-only), owner-scoped from the start
- [x] 4.2 Create draft, edit, delete (draft-only)
- [x] 4.3 Submit action + balance-debit visibility
- [x] **Checkpoint 4**

## Phase 5 — Leave Plan Requests
- [x] 5.1 List + detail (detail = list of dates), owner-scoped
- [x] 5.2 Create/edit (multi-date picker, duplicate guard) + delete
- [x] 5.3 Submit action
- [x] **Checkpoint 5**

## Phase 6 — AI Recommendation Flow (headline feature)
- [x] 6.1 Fetch & display recommendations
- [x] 6.2 Selection UI → build plan-request draft
- [x] 6.3 One-tap create & submit, success state
- [x] **Checkpoint 6**

## Phase 7 — Approvals Queue
- [x] 7.1 Approvals list (two tabs) + approve/reject
- [x] **Checkpoint 7**

## Phase 8 — Schedule (month calendar)
- [x] 8.1 Schedule screen backed by `GET /schedule/?year&month`
- [x] **Checkpoint 8**

## Phase 9 — Notifications
- [x] 9.1 Notifications data layer, global unread badge, list screen
- [x] **Checkpoint 9**

## Phase 10 — Admin/Superuser Master Data CRUD
- [x] 10.1 Generic CRUD scaffold, proven on Leave Types
- [x] 10.2 Apply pattern: Public Holidays, Policies
- [x] 10.3 Apply pattern: Teams, Users (relational pickers)
- [x] 10.4 Apply pattern: Leave Balances (admin)
- [x] **Checkpoint 10**

## Phase 11 — Profile & Identity
- [x] 11.1 Profile view/edit + Change Password
- [x] **Checkpoint 11**
- Out of scope (not in SPEC §8): QR business card, username login, phone number field.

## Phase 12 — Hardening & Report-Readiness
- [x] 12.1 Targeted unit tests
- [x] 12.2 Lint-clean pass + empty/error/loading state audit + feature checklist walkthrough
- [x] 12.3 App icon, display name, screenshots for the report
- [x] **Checkpoint 12 (final)**

## Phase 13 — UI/UX Consistency Revamp (match the Flutter client's actual design system)
Added post-Checkpoint-12 after on-device testing found the UI inconsistent with the Flutter
sibling app. SPEC.md §7 is stale — ground truth is the Flutter app's actual Dart source.
- [x] 13.1 Design tokens overhaul (colors, shapes, spacing, filled fields, button height)
- [ ] 13.2 Navigation shell rewrite (bottom tabs + center FAB + bottom sheet, drop the drawer)
- [ ] 13.3 Dashboard rebuild (avatar card, pastel action tiles, stat cards, no grid)
- [ ] 13.4 Shared component library pass (pill StatusChip, StatCard, error/empty states, sticky
      bottom action panel, split action buttons)
- [ ] 13.5 List/detail screen polish (Leave Requests, Leave Plan Requests, Approvals,
      Notifications, Schedule)
- [ ] 13.6 Form screen polish (bold-label fields, error banner, styled date field, chip picker,
      submit-button hierarchy)
- [ ] 13.7 Admin CRUD + Profile hub restyle (admin entries move under Profile tab)
- [ ] 13.8 Auth screens polish (two-tone wordmark, headline, splash)
- [ ] **Checkpoint 13 (final)** — visual consistency pass complete; SPEC.md §7 updated to the
      verified token table
