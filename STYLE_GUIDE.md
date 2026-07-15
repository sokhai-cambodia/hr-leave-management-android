# HR Leave Management — Shared Brand Style Guide

Source of truth: `lib/app/theme/app_theme.dart` in the Flutter client
(`hr-leave-management-flutter`). This is the **current, live** palette
(supersedes any older `#CA282C` references you may see elsewhere). These
tokens should be mirrored in the Android/Jetpack Compose app's
`MaterialTheme` so both apps present one consistent product.

Direction: clean white canvas, single confident red/coral brand accent,
rounded cards and pill-ish buttons, bottom-tab-bar shell. Light-first
(dark mode available as a toggle, not the default).

## Colors

| Token | Hex | Compose | Use |
|---|---|---|---|
| Primary / accent | `#E23744` | `Color(0xFFE23744)` | Buttons, active tab, focus border, links |
| Primary dark | `#C01F2B` | `Color(0xFFC01F2B)` | Pressed state / gradient end |
| Danger | `#EF4444` | `Color(0xFFEF4444)` | Errors, rejected status |
| Warning | `#F5A623` | `Color(0xFFF5A623)` | Pending / amber status |
| Success | `#22A659` | `Color(0xFF22A659)` | Approved status |
| Info | `#4C8DFF` | `Color(0xFF4C8DFF)` | Stat-card accent (soft blue) |
| Dark background | `#121212` | `Color(0xFF121212)` | Scaffold bg, dark mode |
| Dark surface | `#1E1E1E` | `Color(0xFF1E1E1E)` | Cards, dark mode |
| Light background | `#FFFFFF` | `Color(0xFFFFFFFF)` | Scaffold bg, light mode |
| Light surface | `#FFFFFF` | `Color(0xFFFFFFFF)` | Cards, light mode |
| Light field fill | `#F7F7F9` | `Color(0xFFF7F7F9)` | Input backgrounds (light) |
| Light border | `#EAEAEE` | `Color(0xFFEAEAEE)` | Input/chip borders (light) |
| Dark border | white @ 24% alpha | `Color.White.copy(alpha = 0.24f)` | Input/chip borders (dark) |

### Status pastel formula (badges, stat cards)

Don't hand-pick separate bg/fg pairs per status. Derive the background by
blending the status color at **14% alpha over white**, and use the full
saturated color as the foreground/text:

```
background = blend(statusColor, alpha=0.14, over=white)
foreground = statusColor
```

Example — success badge: bg = `#22A659` @ 14% on white, text = `#22A659`.
Same formula for danger/warning/info.

## Shape (corner radius)

| Element | Radius |
|---|---|
| Buttons | 14dp |
| Cards | 18dp |
| Text fields | 12dp |
| Pills / tab indicator | full pill (`RoundedCornerShape(percent = 50)` or `CircleShape`) |

Compose `Shapes()` bucket mapping: small = 12dp (fields), medium = 14dp
(buttons), large = 18dp (cards).

## Spacing scale

Use these instead of ad-hoc padding/gap values:

| Token | Value |
|---|---|
| `xs` | 4dp |
| `sm` | 8dp |
| `md` | 12dp |
| `lg` | 16dp |
| `xl` | 24dp |

## Typography

**Poppins** — use the actual bundled/downloadable Poppins font family, not
a system font that merely looks similar.

- App bar title: bold
- Buttons / labels: semibold (`w600`)
- Body text: regular

## Component specs

- **Buttons**: 54dp min height, no elevation, 14dp radius, white text on
  primary red fill. Outlined variant is the *same size/shape*, swapping
  fill for a primary-colored border + primary text — so a primary +
  secondary pair (e.g. "Submit" + "Save as Draft") reads as one
  consistent set, not two mismatched buttons.
- **Cards**: elevation 1 (subtle), shadow at black @ 8% alpha, 18dp
  radius.
- **Text fields**: filled background (`#F7F7F9` light / dark surface
  color), 12dp radius, **outlined/bordered** (not borderless), 1.5dp
  primary-colored border on focus, hint text at 35% opacity.
- **Chips**: 14dp radius, bordered — used for segmented choice toggles
  (e.g. Full day / AM / PM, Yes / No) instead of radio buttons/dropdowns
  for small binary/enum choices.
- **Tab bar**: pill-shaped indicator in primary color, white selected
  label, unselected label at 60% opacity.
- **Bottom nav**: surface-colored background, primary color for selected
  icon/label, unselected at 45% opacity, elevation 8, fixed type (labels
  always shown).
- **App bar**: flat (elevation 0), background matches scaffold, title
  left-aligned (not centered).
- **Dashboard navigation**: 2-column card grid of icon+label tiles
  rather than a plain list.
- **Icons**: Material *outlined* icon variants only — no third-party icon
  packs.

## Theming rules

- Default to **light theme**; dark mode is an explicit user toggle, not
  system-default-driven.
- Style tokens are centralized (one theme file) — don't hardcode colors,
  radii, or spacing per-screen; reference the shared tokens above so a
  brand change stays a one-file edit.
