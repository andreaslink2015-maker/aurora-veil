# Aurora Veil

A client-side Fabric mod for **Minecraft 26.3** that fills the game with northern-lights visuals.
It works on any server, because nothing is sent over the network.

## What it does

Press **V** in-game to cycle through four levels (each includes the previous):

| Level | Effect |
|---|---|
| Ribbon trail | Glowing green / cyan / violet / pink particles trail behind you. The faster you move, the denser it gets. Elytra flight looks the best. |
| + Spiral aura | Two streams of light wind up around your body like a double helix, with a small pulsing halo above your head. |
| + Aurora sky | Animated aurora curtains ripple across the top of your screen and fade in and out smoothly. |
| Off | Nothing. |

The key is rebindable under *Options > Controls > Key Binds > Aurora Veil*.

## Build and run

1. Install **JDK 25** (Minecraft 26.x requires it) and **IntelliJ IDEA**.
2. Open this folder in IntelliJ as a Gradle project.
3. The Gradle wrapper jar is not included. Generate it once from the project folder:
   `gradle wrapper --gradle-version 9.6.0` (or copy `gradlew`, `gradlew.bat` and `gradle/wrapper/` from the
   official template at https://fabricmc.net/develop/template/).
4. Run `./gradlew runClient` to launch Minecraft with the mod, or `./gradlew build` and take the jar from `build/libs/`.

## Project layout

```
src/main/java/dev/auroraveil/AuroraVeil.java          mod id + Identifier helper
src/client/java/dev/auroraveil/
  AuroraVeilClient.java    entrypoint: keybind, tick hook, HUD registration
  AuroraParticles.java     registers the 4 particle types
  AuroraEffects.java       ribbon, spiral and halo spawning
  AuroraHud.java           animated screen curtains
  AuroraMode.java          the effect levels
src/main/resources/assets/aurora_veil/
  particles/*.json         particle -> texture frames
  textures/particle/*.png  generated glow frames (4 per colour)
  lang/en_us.json          keybind names
```

## Tweaking it

- **Colours:** change the RGB values in the texture generator, or just paint over the PNGs in
  `textures/particle/`. Each colour has 4 frames that play from spawn to fade-out.
- **Density and speed:** the numbers in `AuroraEffects.java` (`14.0` speed multiplier, `0.28` spiral speed, `ticks % 8` halo rate).
- **Sky look:** the sine-wave constants and the `0.10 + 0.22 * wave` height in `AuroraHud.java`.

## If the build complains

This was written against the Fabric docs for 26.2/26.3 but could not be compiled where it was written
(the build environment could not reach Fabric's Maven repository). Minecraft 26.x renames things often, so if
a class or import is not found:

- Put the cursor on the red symbol in IntelliJ and press **Alt+Enter** to pick the current import.
- If Gradle can't resolve a version, check https://fabricmc.net/develop/ and update `gradle.properties`
  (`loom_version`, `loader_version`, `fabric_api_version`).
- The most likely spots are the Fabric API imports at the top of `AuroraVeilClient.java` and `AuroraParticles.java`.
