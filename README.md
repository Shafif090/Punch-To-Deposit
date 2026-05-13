# Punch To Deposit

A Forge 1.8.9 client-side quality-of-life mod that lets you punch a nearby chest or ender chest to quickly deposit the item in your hand — inspired by Hypixel BedWars.

## Features

- **Quick Deposit**: Left-click any reachable chest or ender chest to instantly deposit your held item.
- **Works Everywhere**: Singleplayer and multiplayer servers — uses only standard inventory interactions.
- **Tool Preservation**: Tools are never deposited, so they stay safe in your inventory.
- **Vanilla Compatible**: No server-side installation needed.
- **Settings UI**: Press `P` to open an in-game settings screen.
- **Hover Label**: Show or hide the `PUNCH TO DEPOSIT` hover text while looking at chests.

## How It Works

1. Left-click a chest or ender chest while holding a depositable item.
2. The mod opens the chest via normal interaction.
3. Your selected hotbar slot is shift-clicked into the chest.
4. The chest GUI closes automatically.

## What Gets Deposited

Everything except tools (pickaxes, swords, axes, shovels, hoes, etc.). Diamonds, emeralds, fireballs, potions, and other items all deposit normally.

## Settings

Open the settings screen with `P` to toggle:

- the whole mod on or off
- the chest hover label on or off

## Installation

1. Install [Forge 1.8.9](https://files.minecraftforge.net) for Minecraft 1.8.9.
2. Download the latest release from [Modrinth](https://modrinth.com/mod/punch-to-deposit-client) or the [releases page](https://github.com/Shafif090/PunchToDeposit/releases).
3. Place the `.jar` file in your `.minecraft/mods` folder.
4. Launch Minecraft.

## Links

- GitHub: [Shafif090](https://github.com/Shafif090)
- Modrinth: [Shafif090](https://modrinth.com/user/Shafif090)

## Building

This project requires Java 8 and Gradle 2.14.1 (included locally).

```bash
./gradlew build
```

The compiled mod will be in `build/libs/`.

## License

MIT License — see [LICENSE](LICENSE) for details.
