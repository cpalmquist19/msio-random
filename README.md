# Minesharesio Random

Minesharesio Random adds the Cragbeast to Minecraft, together with a hatchable
Cragbeast Egg and a Cragbeast Spawn Egg.

## Compatibility

- Minecraft 1.20.1
- Fabric Loader 0.15.11 or later
- Fabric API 0.92.2+1.20.1 or compatible
- GeckoLib 4.4.9 or compatible
- Java 17 for development

## Installation

1. Install Fabric Loader for Minecraft 1.20.1.
2. Download compatible versions of Fabric API and GeckoLib.
3. Download Minesharesio Random from CurseForge.
4. Place all three JAR files in your Minecraft `mods` folder.

## Features

- Cragbeast hostile entity with GeckoLib animations.
- Cragbeast Spawn Egg for spawning a Cragbeast.
- Hatchable Cragbeast Egg block.

## Building from source

```powershell
.\gradlew.bat clean build
```

Upload `build/libs/minesharesiorandom-<version>.jar`, not the sources JAR.

## License and credits

This project is licensed under the [MIT License](LICENSE). It depends on
[Fabric API](https://modrinth.com/mod/fabric-api) and
[GeckoLib](https://modrinth.com/mod/geckolib); install both as required
dependencies.
