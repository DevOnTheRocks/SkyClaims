# Options
Options are like configs but they are set using a permissions plugin and support custom values for players and/or groups.
Options can also be defined in specific contexts (_server, world, claim, etc_) if supported by your permissions plugin.

## Available Options

| Option | Description | Default Value |
| ------ | ----------- | ------------- |
| `skyclaims.default-schematic` | The schematic that will be used when `is create` is used without a supplied schematic. | `skyfactory` |
| `skyclaims.default-biome` | The [Biome Type](biome-types.md) to use, if any, to set an island to on creation. | `none` |
| `skyclaims.min-size` | **Half** of the width of an island in blocks used to claim the player's usable space.<br /> _Accepts 8 - 256_ | `48` |
| `skyclaims.max-size` | **Half** of the max width of an island. Limits use of `/is expand` beyond this value.<br /> _Accepts **min-size** - 256_ | `64` |
| `skyclaims.max-spawns` | The maximum number of living entities allowed to spawn on islands owned by the player.<br /> _0 to disable_ | `70` |
| `skyclaims.max-spawns.hostile` | The maximum number of hostile entities allowed to spawn on islands owned by the player.<br /> _0 to disable_ | `50` |
| `skyclaims.max-spawns.passive` | The maximum number of passive entities allowed to spawn on islands owned by the player.<br /> _0 to disable_ | `30` |
| `skyclaims.expiration` | The number of days an island must be inactive before it is removed, if enabled.<br /> _0 to disable_ | `30` |
| `skyclaims.max-islands` | The maximum number of islands a player may join.<br /> _0 to disable_ | `0` |
| `skyclaims.max-teammates` | The maximum number of players that may join an island (_the island owner's limit is used_).<br /> _0 to disable_ | `0` |

### Examples (LuckPerms)
- `lp group default meta set skyclaims.default-schematic sf3` **&#8594;** islands will be made using sf3.schematic
- `lp group default meta set skyclaims.default-biome plains` **&#8594;** islands will be created as plains
- `lp group default meta set skyclaims.min-size 128` **&#8594;** Minimum island size will be 256x256 blocks
- `lp group default meta set skyclaims.max-size 240` **&#8594;** Maximum island size will be 480x480 blocks