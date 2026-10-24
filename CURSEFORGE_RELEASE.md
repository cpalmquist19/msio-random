# CurseForge Release Checklist

## Project

- Game: Minecraft
- Mod loader: Fabric
- Minecraft version: 1.20.1
- Project name: Minesharesio Random
- Release type: Release

Use the Cragbeast Spawn Egg image from
`src/main/resources/assets/minesharesiorandom/textures/item/cragbeast_spawn_egg.png`
as the in-mod icon. Upload a dedicated, high-resolution square logo and at
least one in-game screenshot to the CurseForge project page.

## File upload

1. Run `.\gradlew.bat clean build`.
2. Upload `build/libs/minesharesiorandom-1.0.0.jar`.
3. Do not upload `-sources.jar` or an intermediate development JAR.
4. Select Fabric and Minecraft 1.20.1 for the uploaded file.
5. Paste the `1.0.0` section of `CHANGELOG.md` into the file changelog.

## Required relations

Mark these as required dependencies for the uploaded file:

- Fabric API, version 0.92.2+1.20.1 or compatible
- GeckoLib, version 4.4.9 or compatible

## Pre-upload verification

- Start a clean Fabric 1.20.1 profile with Fabric API and GeckoLib installed.
- Confirm the Cragbeast, its baby variant, the Cragbeast Egg, and the
  Cragbeast Spawn Egg render without missing-texture placeholders.
- Confirm the egg hatches and the mod starts without dependency errors.
