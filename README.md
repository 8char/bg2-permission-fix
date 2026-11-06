# BG2 Permission Fix

Server-side-only Forge 1.20.1 mod that fixes
[Building Gadgets 2 issue #214](https://github.com/Direwolf20-MC/BuildingGadgets2/issues/214):
gadget paste/build permission is checked at the **template-relative** position
(near 0,0) instead of the actual placement position.

## Symptoms of the bug

- If the area around x=0 z=0 is claimed (FTB Chunks, OPAC, ...) or covered by
  vanilla `spawn-protection`, **nobody can paste anywhere in that dimension**.
- Conversely, if 0,0 is unprotected, pastes **bypass claim protection
  everywhere** (placement only; breaking already uses correct coordinates).

## What it does

One mixin, two `@Redirect`s, in
[`BuildingUtilsMixin.java`](src/main/java/me/atlas/bg2permfix/mixin/BuildingUtilsMixin.java):
the calls to `EventHelpers.mayPlace(...)` / `EventHelpers.mayExchange(...)` in
`BuildingUtils.build()` / `exchange()` are re-pointed from `statePos.pos`
(relative) to `statePos.pos.offset(lookingAt)` — the exact position the block
is actually placed at, the same expression BG2 itself uses on the next line to
place it. Nothing else is touched.

For regular (non-paste) gadget modes the position list is already absolute and
`lookingAt` is `BlockPos.ZERO`, so the offset is a no-op there.

## Install

Drop the jar in the **server's** `mods/` folder. Clients don't need it and are
unaffected (`displayTest = IGNORE_ALL_VERSION`). Requires Building Gadgets 2
1.0.6–1.0.8 on MC 1.20.1 Forge. If the installed BG2 version is incompatible,
the server fails at startup with a clear error instead of silently doing
nothing.

## Build it yourself

```
JAVA_HOME=<jdk17> ./gradlew build
```

Jar lands in `build/libs/`. The BG2 compile dependency is fetched from
CurseMaven (project 298187, file 6850495 = BuildingGadgets2-1.0.8).

## License

MIT, same as Building Gadgets 2.
