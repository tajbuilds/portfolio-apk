# Design Overhaul Notes

## Goal
Upgrade the app from functional MVP visuals to a modern, premium native experience while keeping current API/data architecture intact.

## Recommended Direction
1. Keep native Jetpack Compose and introduce a real design system (recommended).
2. Use UI kit patterns only as reference, not as final identity.
3. Avoid architecture migration right now (no cross-platform switch).

## What "Modern" Should Include
- Strong typography hierarchy (display/title/body cadence).
- Intentional color system for light and dark (not default Material look).
- Layered surfaces, subtle gradients, and controlled elevation.
- Better card composition (imagery, tags, metadata rhythm).
- Meaningful motion (screen transitions, staggered reveal, tactile press feedback).
- High contrast and readability checks in both themes.

## Proposed V1 Visual Overhaul (No Backend Risk)
1. New theme tokens + brand palette.
2. Refined Home and Work card layouts.
3. Improved bottom tab bar styling + transitions.
4. Motion pass (pull behavior, screen entry, card interactions).
5. Keep API/data/repository contracts unchanged.

## Product Decisions Already Agreed
- Bottom tabs are primary navigation and should stay visible on detail screens.
- Redundant hero CTAs on Home can remain removed in favor of tab navigation.
- Pull-to-refresh should visually follow finger movement.
- Work detail should support cover media (including SVG).

## Optional Next Steps (when ready)
1. Produce 2-3 visual directions (minimal / editorial / bold-tech).
2. Pick one and implement theme/token foundations first.
3. Apply selected language to Home -> Work -> Detail in order.
4. Run accessibility contrast and touch-target pass.
