# Column Quick Swap

A Minecraft Forge mod for 1.20.1 that lets you quickly swap items between your hotbar and the inventory column above it.

## Usage

1. Hold **V** (default) while hovering over a hotbar slot
2. A popup appears showing the 3 inventory slots directly above
3. Select a slot to swap with your hotbar:
   - **Mouse**: Hover and release V
   - **Keys**: Press 1, 2, or 3 (top to bottom)
   - **Scroll**: Change hotbar selection while picker is open

## Configuration

Config file: `.minecraft/config/column_quick_swap.client.json`

```json
{
  "pressTicks": 20
}
```

- `pressTicks`: Delay before popup opens (20 ticks = 1 second)

## Requirements

- Minecraft 1.20.1
- Forge 47+
- Kotlin for Forge 4+

## Installation

Drop the mod jar into your `.minecraft/mods` folder.

## License

MIT
