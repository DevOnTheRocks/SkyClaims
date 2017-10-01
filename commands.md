# SkyClaims Commands

## User Commands
| Command (Alias) | Description | Permission |
| --------------- | ----------- | ---------- |
| `skyclaims`<br />`island`<br />`is` | Main command used to run other commands or display help. | `none` |
| `is help` | Displays info on command usage, if player has permission to use a command. | `skyclaims.command.help` |
| `is create [schematic]` | Used by a player to create an island. | `skyclaims.command.create`<br />_*supports [argument perms](#command-arguments)_ |
| `is reset [schematic]` | Used by a player to reset their island. (Note: **Resets player's inventory!**) | `skyclaims.command.reset`<br />_*supports [argument perms](#command-arguments)_ |
| `is regen [schematic]` | Used by a player to regen their island. | `skyclaims.command.regen`<br />_*supports [argument perms](#command-arguments)_ |
| `is expand [blocks]` | Used by a Player to expand their Island's size. | `skyclaims.command.expand` |
| `is list` | Displays a list of Islands. | `skyclaims.command.list`<br />others: `skyclaims.admin.list` |
| `is lock`<br />`is unlock` | Used to lock/unlock an island to visitors. | `skyclaims.command.lock` |
| `is info [player]` | Displays information about your island or the designated player's island. | `skyclaims.command.info`<br />others: `skyclaims.admin.info` |
| `is setspawn` | Used by a player to set their island's spawn point. | `skyclaims.command.setspawn`<br />others: `skyclaims.admin.setspawn` |
| `is spawn [player]`<br />`is tp [player]` | Used to telport to the spawn of your or the player specified's island. | `skyclaims.command.spawn`<br />others: `skyclaims.admin.spawn` |
| `is setbiome <biome> [target]` | Used by a player to set the biome of a block, chunk, or island. (default: island) | `skyclaims.command.setbiome`<br />_*supports [argument perms](#command-arguments)_ |

### Integrated Commands

| Command (Alias) | Description | Permission |
| --------------- | ----------- | ---------- |
| `is sethome` | Used to set a home island location. **\*Requires Nucleus** | `skyclaims.command.sethome` |
| `is home` | Used to teleport to your home island location. **\*Requires Nucleus** | `skyclaims.command.home` |

## Command Arguments
**Command argument permission checking can be turned on via the SkyClaims config for the following:**

| Command Argument | Description | Permission |
| --------------- | ----------- | ---------- |
| _create/reset_ - `[schematic]` | use to give access to specific schematics beyond the default | `skyclaims.arguments.schematics.<schematic>` |
| _setbiome_ - `<biome>` | use to limit access to specific biome types | `skyclaims.arguments.biomes.<biome>` |
| _setbiome_ - `[target]` | use to limit access to different sized targets | `skyclaims.arguments.block`<br />`skyclaims.arguments.chunk` |

## Admin Commands
| Command (Alias) | Description | Permission |
| --------------- | ----------- | ---------- |
| `isa`<br />`is admin` | Used to run admin commands or display admin help | `skyclaims.admin.base` |
| `isa cs <name>`| Used to create a schematic to use with is create<br />(Use a Golden Axe as a selection tool) | `skyclaims.admin.schematic.create` |
| `isa transfer [owner] <newowner>` | Transfer an island to another player | `skyclaims.admin.transfer` |
| `isa delete <player> [regen]` | Used to delete the specified player's island, accepts optional true/false to disable region regeneration | `skyclaims.admin.delete` |
| `isa reload` | Used to reload the config, schematics directory, & database  | `skyclaims.admin.reload` |