# HR Leave Management — Native Android Client — Task Checklist

Full detail (acceptance criteria, verify steps) in `tasks/plan.md`.

## Phase 0 — Environment & Scaffolding
- [ ] 0.1 Project scaffold, dependencies, folder skeleton, lint
- [ ] 0.2 Core network/storage infra + connectivity smoke test
- [ ] **Checkpoint 0**

## Phase 1 — Auth & Session
- [ ] 1.1 Login + token storage + current-user fetch + minimal authenticated screen
- [ ] 1.2 Session bootstrap + global 401 handling
- [ ] 1.3 Forgot password + reset password
- [ ] **Checkpoint 1**

## Phase 2 — App Shell, Role-Adaptive Navigation, Dashboard
- [ ] 2.1 App shell + Navigation-Compose graph + static role-based nav (superuser branch)
- [ ] 2.2 Team-owner detection + Approvals nav entry + Pending Approvals card
- [ ] **Checkpoint 2**

## Phase 3 — Leave Balances
- [ ] 3.1 Leave balances list, wired into dashboard
- [ ] **Checkpoint 3**

## Phase 4 — Leave Requests
- [ ] 4.1 List + detail (read-only), owner-scoped from the start
- [ ] 4.2 Create draft, edit, delete (draft-only)
- [ ] 4.3 Submit action + balance-debit visibility
- [ ] **Checkpoint 4**

## Phase 5 — Leave Plan Requests
- [ ] 5.1 List + detail (detail = list of dates), owner-scoped
- [ ] 5.2 Create/edit (multi-date picker, duplicate guard) + delete
- [ ] 5.3 Submit action
- [ ] **Checkpoint 5**

## Phase 6 — AI Recommendation Flow (headline feature)
- [ ] 6.1 Fetch & display recommendations
- [ ] 6.2 Selection UI → build plan-request draft
- [ ] 6.3 One-tap create & submit, success state
- [ ] **Checkpoint 6**

## Phase 7 — Approvals Queue
- [ ] 7.1 Approvals list (two tabs) + approve/reject
- [ ] **Checkpoint 7**

## Phase 8 — Schedule (month calendar)
- [ ] 8.1 Schedule screen backed by `GET /schedule/?year&month`
- [ ] **Checkpoint 8**

## Phase 9 — Notifications
- [ ] 9.1 Notifications data layer, global unread badge, list screen
- [ ] **Checkpoint 9**

## Phase 10 — Admin/Superuser Master Data CRUD
- [ ] 10.1 Generic CRUD scaffold, proven on Leave Types
- [ ] 10.2 Apply pattern: Public Holidays, Policies
- [ ] 10.3 Apply pattern: Teams, Users (relational pickers)
- [ ] 10.4 Apply pattern: Leave Balances (admin)
- [ ] **Checkpoint 10**

## Phase 11 — Profile & Identity
- [ ] 11.1 Profile view/edit + Change Password
- [ ] **Checkpoint 11**
- Out of scope (not in SPEC §8): QR business card, username login, phone number field.

## Phase 12 — Hardening & Report-Readiness
- [ ] 12.1 Targeted unit tests
- [ ] 12.2 Lint-clean pass + empty/error/loading state audit + feature checklist walkthrough
- [ ] 12.3 App icon, display name, screenshots for the report
- [ ] **Checkpoint 12 (final)**
