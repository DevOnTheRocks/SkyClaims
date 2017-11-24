# SkyClaims Commands

## User Commands
| Command (Alias) | Description | Permission |
| --------------- | ----------- | ---------- |
| `skyclaims`<br />`island`<br />`is` | Main command used to run other commands or display help. | `none` |
| `is help` | Displays info on command usage, if player has permission to use a command. | `skyclaims.command.help` |
| `is create [schematic]` | Used by a player to create an island. | `skyclaims.command.create`<br />_*supports [argument perms](#command-arguments)_ |
| `is expand [blocks]` | Used by a player to expand their Island's size. | `skyclaims.command.expand` |
| `is demote <user>` | Used by a player to demote island members. | `skyclaims.command.demote` |
| `is info [user]` | Displays information about your island or the designated player's island. | `skyclaims.command.info`<br />others: `skyclaims.admin.info` |
| `is invite [user] [privilege]` | Used by a player to invite island members. | `skyclaims.command.invite` |
| `is kick <user>` | Used by a player to kick island members. | `skyclaims.command.kick` |
| `is leave` | Used by a player to leave an island. | `skyclaims.command.leave` |
| `is list [user] [sort]` | Displays a list of Islands. The base permission only allows users to see islands which they are a member of. Additionally, you may allow users to list all unlocked islands and allow the use of sorting options. | `skyclaims.command.list.base`<br />`skyclaims.command.list.unlocked`<br />`skyclaims.command.list.sort`<br />others: `skyclaims.admin.list` |
| <code>is lock [island&#124;all]</code><br /><code>is unlock [island&#124;all]</code> | Used to lock/unlock an island to visitors. | `skyclaims.command.lock`<br />others: `skyclaims.admin.lock.others` |
| `is promote [user]` | Used by a player to promote island members. | `skyclaims.command.promote` |
| `is reset [schematic] [keepinv]` | Used by a player to reset their island.</br>(Note: **Resets player's inventory by default!**) | `skyclaims.command.reset`<br/>`skyclaims.admin.reset.keepinv`<br/>_*supports [argument perms](#command-arguments)_ |
| `is setbiome <biome> [target]` | Used by a player to set the biome of a block, chunk, or island. (default: island) | `skyclaims.command.setbiome`<br />_*supports [argument perms](#command-arguments)_ |
| `is setspawn` | Used by a player to set their island's spawn point. | `skyclaims.command.setspawn`<br />others: `skyclaims.admin.setspawn` |
| `is spawn [player]`<br />`is tp [player]` | Used to telport to the spawn of your or the player specified's island. | `skyclaims.command.spawn`<br />others: `skyclaims.admin.spawn` |

### Integrated Commands

| Command (Alias) | Description | Permission |
| --------------- | ----------- | ---------- |
| `is sethome` | Used to set a home island location. **\*Requires Nucleus** | `skyclaims.command.sethome` |
| `is home` | Used to teleport to your home island location. **\*Requires Nucleus** | `skyclaims.command.home` |

## Command Arguments
**Command argument permission checking can be turned on via the SkyClaims config for the following:**

| Command - Argument | Description | Permission |
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
| ~~`isa delete <player> [regen]`~~ replaced by `is delete` in B26.1 | Used to delete the specified player's island, accepts optional true/false to disable region regeneration. (Note: **The island plot will be erased unless you choose to disable plot regen!**) | `skyclaims.command.delete` allows use of the command <br /> `skyclaims.admin.delete` allows deleting of other player's islands & use of the clear argument to delete an island without clearing |
| `isa reload` | Used to reload the config, schematics directory, & database  | `skyclaims.admin.reload` |

### Debug Commands
\* _Available even when SkyClaims was disabled during the loading process by an error._

| Command (Alias) | Description | Permission |
| --------------- | ----------- | ---------- |
| `scversion` | Used to display SkyClaims version information  | `skyclaims.admin.version` |
