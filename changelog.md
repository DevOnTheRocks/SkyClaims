# Change Log

# Beta 28
### NOTE: Sponge Forge `1.12.2-2825-7.1.6-RC3697` adds entity and biome support for schematics
**REQUIRED: Sponge API 7.1 (SF 3682+); GP 1.12.2-4.3.0.622+; Permissions Plugin (ie. LuckPerms)**
**OPTIONAL: Nucleus 1.9.0-S7.1+**
- Added new schematic features:
  - `/is create` & `/is reset` now feature a chest UI when more than 1 schematic is available
  - `/is schematic` - schematic parent & list command
  - `/is schematic create <name>` - replaces `/isa createschematic <name>`
  - `/is schematic delete <schematic>` - deletes a schematic
  - `/is schematic command <schematic> <add|remove> <command>` - manages schematic commands
  - `/is schematic info <schematic>` - displays detailed information about a schematic
  - `/is schematic setbiome <schematic> <biome>` - set a default biome for a schematic that overrides the permission option
  - `/is schematic setheight <schematic> <height>` - set the generation height of a schematic
  - `/is schematic setname <schematic> <name>` - set an in-game name for a schematic that supports formatting code
  - `/is schematic seticon <schematic> <icon>` - set an icon for a schematic to be used by the chest GUI
  - `/is schematic setpreset <schematic> <preset>` - set a flat world preset for a schematic *see flat world preset support*
  - `skyclaims.default-schematic` now defaults to empty which will list valid schematics
  - Removed `Misc-List-Schematics` config
  - Added `Misc > Text-Schematic-List` config to disable new chest UI
- Removed `/isa` &`/is admin`
  - `/is transfer` replaces `/isa transfer`
  - `/is reload` replaces `/isa reload`
  - `Admin-Command-Alias` config removed
- Added `skyclaims.max-teammates` option to limit the number of players per island
- Added fine-grained keep/clear inventory control:
  - Player inventory keep inventory permissions:
    - `skyclaims.keepinv.player.create`
    - `skyclaims.keepinv.player.delete`
    - `skyclaims.keepinv.player.kick`
    - `skyclaims.keepinv.player.leave`
    - `skyclaims.keepinv.player.reset`
  - EnderChest inventory keep inventory permissions:    
    - `skyclaims.keepinv.enderchest.create`
    - `skyclaims.keepinv.enderchest.delete`
    - `skyclaims.keepinv.enderchest.kick`
    - `skyclaims.keepinv.enderchest.leave`
    - `skyclaims.keepinv.enderchest.reset`
- Added `/is entity` command for detailed entity information
- Added `/is setname [name]` command
- Added `/scplayerinfo` command for debugging permission options
- Added support for flat world preset codes (_block ID portion only_) for region generation
  - See https://minecraft.gamepedia.com/Superflat#Preset_code_format for more details
- Added new schematics:
  - Stoneblock 2
  - SkyFactory 4
- Added `World > Regen-On-Create` config option
- Reworked `/is list [island] [sort type] [sort order]`
  - Sort order has been separated from sort type
  - **Sort Types**: NAME, CREATED, ONLINE, ACTIVE, MEMBERS, SIZE, ENTITIES
  - **Sort Orders**: ASC, DESC
  - Added `Misc >  Primary-List-Sort` config option - sets a sort type that gets applied before the one provided from the command argument
- Added `Misc > Island-Commands` config option
  - Commands trigger on island creation, join, and reset
  - Removed `Misc > Create-Commands` & `Misc > Reset-Commands`
- Removed outdated schematics:
  - Garden of Glass
  - SkyExchange
- Added island `min-size` check when an island owner logs in and if necessary, the island will be expanded
- Fixed schematics sometimes not generating at the intended height. The height set will be the height the player is at when standing on the lowest block of a schematic.
- Fixed Nucleus Integration commands not registering after a reload
- Fixed admin island expansion (`/is info`) bug where old clickable text can be used to expand outside the region
- Fixed a bug where `/is lock` would not prevent entry to a locked island
- Fixed a bug when removing overlapping claims during `/is create`
- Updated bStats to 1.4

# Beta 27.1
**REQUIRED: SF build 2800+ & GP build 4.3.0.509+ OPTIONAL: Nucleus version 1.2.0+**
- Fixed `/is home` not using safe teleport
- Fixed `/is promote` & `/is demote` success messages
- Fixed `/is expand` using more claim blocks than the owner has available
- Improved safe teleport failure messages
- Reworked the biome argument to _theoretically_ support modded biomes
- Removed `/isa config`
- Removed Options Config - **Use permissions options instead**

# Beta 27.0
**REQUIRED: SF build 2800+ & GP build 4.3.0.509+ OPTIONAL: Nucleus version 1.2.0+**
- Subcommands now provide useful errors
- Updated for breaking API change in Sponge Forge 1.12.2-2555-7.0.0-BETA-2764
- Fixed `/is expand` erroring when an island argument was not provided
- Fixed `/is delete` requiring an island argument
- Fixed `/is delete` not removing players from the island before deletion
- Fixed wilderness flags applying to the default world when configured to use a different world
- Added `skyclaims.max-islands` option
- Added confirmation dialogs to `/is delete`
- Added configurable root command alias - the first alias is used for help text

# Beta 26.2 Hotfix
**Sponge API 5/6 - REQUIRES: SF build 2637+ & GP build 4.0.1.474+ OPTIONAL: Nucleus version 1.1.3+**
**Sponge API 7 - REQUIRES: SF build 2688+ & GP build 4.3.0.473+ OPTIONAL: Nucleus version 1.2.0+**
- Fixed a bug with `is lock` & `is unlock` where the permission check returns incorrectly
- Fixed a typo in the `is delete` help text
- Added a wilderness flag config setting to allows customizing the flags set automatically by SkyClaims

# Beta 26.1
**Sponge API 5/6 - REQUIRES: SF build 2637+ & GP build 4.0.1.474+ OPTIONAL: Nucleus version 1.1.3+**
**Sponge API 7 - REQUIRES: SF build 2688+ & GP build 4.3.0.473+ OPTIONAL: Nucleus version 1.2.0+**
- `/isa delete` is now `/is delete` and may be used by an island owner to delete their island permanently
    - `skyclaims.command.delete` allows use of the command
    - `skyclaims.admin.delete` allows deleting of other player's islands & use of the clear argument to delete an island without clearing
- `/is lock` & `/is unlock` can now be used by island managers
- Locking an island will now kick non-members from the island
- Added `skyclaims.admin.kick.exempt` - prevents being removed from an island with `/is kick` and `/is lock`
- the "island" command argument will now only tab complete island's where a user has the required privilege level
to run the command unless they have `skyclaims.admin.list`
- Fixed an issue with `/is expand` where it would repeatedly display a confirmation message and not expand the island
- Fixed automated island cleanup not clearing the region
- Fixed `/is leave` & `/is kick` not using World/Spawn-World when configured
- **[S7.0]** Fixed a NoSuchElementException in the Options class
- Fixed clickable text objects missing hover text

# Beta 26
**Sponge API 5/6 - REQUIRES: SF build 2637+ & GP build 4.0.1.474+ OPTIONAL: Nucleus version 1.1.3+**
**Sponge API 7 - REQUIRES: SF build 2688+ & GP build 4.3.0.473+ OPTIONAL: Nucleus version 1.2.0+**
- Updated for latest GP API changes
- Islands now use the TOWN claim type - this makes all Town features available for use
- Added Sky Exchange island schematic _(must enable command blocks & recommend set World/Island-Height config to 1)_
- Replaced `/is regen` with `/is reset [keepinv]` argument _(requires_ `skyclaims.admin.reset.keepinv`)
- Fixed a rare NPE that would occur when using `/is info` on an island with a missing claim
- Islands **should** support the new min/max level options in GP

# Beta 25.1 - Hotfix
**Sponge API 5/6 - REQUIRES: SF build 2558+ & GP build 4.0.0.415+ OPTIONAL: Nucleus version 1.1.3+**
**Sponge API 7 - REQUIRES: SF build 2624+ & GP build 4.2.0.418+ OPTIONAL: Nucleus version 1.2.0+**
- Fixed islands being created at Y 1 instead of the configured height.
- Added configurable spawn world
- Added `/scversion` - `skyclaims.admin.version` to aid in debugging dependency issues

# Beta 25
**Sponge API 5/6 - REQUIRES: SF build 2558+ & GP build 4.0.0.415+ OPTIONAL: Nucleus version 1.1.3+**
**Sponge API 7 - REQUIRES: SF build 2624+ & GP build 4.2.0.418+ OPTIONAL: Nucleus version 1.2.0+**
- Added island invite & rank system for working together with other players
    - Added `/is invite` - `skyclaims.command.invite`
    - Added `/is kick` - `skyclaims.command.kick`
    - Added `/is leave` - `skyclaims.command.leave`
    - Added `/is promote` - `skyclaims.command.promote`
    - Added `/is demote` - `skyclaims.command.demote`
    - Use of GriefPrevention's `/trust` & `/permissiontrust` on an island is handled by `/is invite`
- Enhanced `/is list` and reworked permissions:
    - **[O]** - owner, **[M]** - manager, or **[T]** - trusted (member) now appear in place of **[L]** or **[T]** when applicable
    - `skyclaims.command.list.base` - allows a player to use the list command _(shows a player's islands ie. **owner**, **manager**, or **trusted**)_
    - `skyclaims.command.list.unlocked` - allows a player to list all unlocked islands
    - `skyclaims.command.list.sort` - allows a player to use the list sorting features
    - `skyclaims.admin.list` - allows a player to list all islands
- Combined `/is info` owner & member sections & color coded entries
- Added _Misc/Teleport-on-Creation_ config to disable automatic teleportation after island creation
- `/is create` & `/is reset` now support generating a clickable list from available schematics
- Added Void Island Control Schematics to prepackaged schematics
- Fixed an IndexOutOfBoundsException with the entity limits feature

# Beta 24 - S5.1
**REQUIRED: SF build 2519+ & GP build 4.0.0.332+ OPTIONAL: Nucleus version 1.0.1+**
- Managers can now use `/is setspawn`, `/is setbiome`, & `/is expand`
- Added enhanced timings support
- Added Garden of Glass schematic to prepackaged schematics
- Fixed `/isa reload` breaking `/is home` &`/is sethome` commands
- Fixed known incompatibility with API 6.0 in entity limit feature

# Beta 23.1 - S5.1
**REQUIRED: SF build 2519+ & GP build 4.0.0.332+ OPTIONAL: Nucleus version 1.0.1+**
- Minimum Sponge Forge build now 2519 & minimum GriefPrevention build 332.
- Island claims now bypass size restrictions
- Fixed a bug where is list showed duplicate messages when there were no islands

# Beta 23
**REQUIRED: SF build 2497+ & GP build 4.0.0.319+ OPTIONAL: Nucleus version 1.0.1+**
- Updated GriefPrevention API. GP 4.0.0.319 is now the minimum required version.
- Block-break/block-place flags in the wilderness of the SkyClaim's world are set to false automatically.
- `/is expand` now cost 256 times more blocks due to GP's change to include y in claim block costs
- `/is home` & `/is sethome` are now exclusive to Nucleus integration
- Cleaned up Nucleus Integration (mostly behind the scenes)

# Beta 22
**REQUIRED: SF 2096+ & GP 295+ OPTIONAL: Nucleus 1.0.1+**
- Fixes some entities still not being cleared during island reset/regen
- Added entity spawn limit capability to islands (disabled by default)
- Added island expiration and cleanup capability (disabled by default)
- New options: `skyclaims.max-spawns`, `skyclaims.max-spawns.hostile`, `skyclaims.max-spawns.passive` ,`skyclaims.expiration`
- New configs: Entity [Limit-Spawning, Max-Hostile, Max-Passives, Max-Spawns], Island-Expiration [Enabled, Interval, Threshold]
- Added island entity counts (living, item, & tile) to `/is info`
- Added new sort types to `/is list`: entities-, entities+, tile-, tile+

# Beta 21
**REQUIRED: SF 2096+ & GP 292+ OPTIONAL: Nucleus 0.29.0+**
- Added `/is regen` (`skyclaims.command.regen`) to allow regenerating an island using a schematic.
- Added integration config to turn off/on optional integration features
- Fixed `/is reset` not clearing entities

# Beta 20
**REQUIRED: SF 2096+ & GP 292+ OPTIONAL: Nucleus 0.26.0+**
- Island locks now prevent players from entering another players island if they're not trusted
- Changed `skyclaims.admin.lock` to `skyclaims.admin.lock.others`
- Added `skyclaims.admin.lock.bypass` to allow bypassing of island locks

# Beta 19.1 - Hotfix
**REQUIRED: SF 2096+ & GP 260+ OPTIONAL: Nucleus 0.26.0+**
- Fixed `/is home` checking the permissions of the island it is run on. Fixes #34
- Fixed `/is sethome` not checking permission of the island it is run on.

# Beta 19
**REQUIRED: SF 2096+ & GP 260+ OPTIONAL: Nucleus 0.24.1+**
- **(BREAKING)** Renamed `skyclaims.initial-size` to `skyclaims.min-size` to match its modified behavior
- **GP 260+** Fixed bug causing `/isa transfer` to fail transferring the claim if the target player had too few claim blocks
- Fixed commands not finding current island due to Y coordinate check failing
- Fixed `/is help` showing extra line breaks
- Added sorting to `/is list` (name, created, last active, team size & island size)
- Added Options config
- Added `/isa config` a debug command that shows most of SkyClaims' currently loaded config values
- Added support for `/sponge plugins reload`
- Added `/is list` to `/is help`
- Nucleus Integration: `/is sethome` now requires the player to be on a trusted island

# Beta 18
### * Manual SQLite file migration required if upgrading from Beta 13 or earlier!
**REQUIRED: SF 2096+ & GP 255+ OPTIONAL: Nucleus 0.24.1+**
- **Removed deprecated SQLite config and auto file migration**
- Fixed `/isa transfer` not working when supplied with an owner
- Fixed `/is expand` not being able to expand an island to `max-size`
- Fixed `/is setspawn`'s message formatting to not display decimal numbers
- Fixed `/is setspawn` not being restricted to an island's claimed area
- Fixed system-dependent file separators causing NPE when moving config to different OS
- Added `/is lock [island|all]` & `/is unlock [island|all]` arguments - requires `skyclaims.admin.lock`
- Added island lock indicator to `is list` & `is info`
- Added expand _(no cost)_ to admin shortcuts in `/is info` _requires_ `skyclaims.admin.expand`
- Added custom island command argument that accepts a player name or island id
- Changed `/is info` to accept an island argument
- Changed default island lock to true
- `/is spawn` can now be used to visit unlocked islands
- `/is list` no longer hides locked islands - teleporting now checks lock status & permissions
- `/is list` is now sorted alphabetically
- `/is list` & `/is info` now displays "[L]" or "[U]" based on the island's lock setting _(click to toggle)_
- Added Nucleus integration
    - `/is home` & `/is sethome` now works as a player configurable home when installed

# Beta 17
**REQUIRES: SF 2096+ & GP 255+ (latest versions are highly recommended)**
- Fixed island width calculation being off by 2 on `/is info`
- Added `/is expand [blocks]` to allow player to expand their islands using claim blocks _(subtracted from bonus claim blocks - will regen)_
- Added `skyclaims.max-size` option to limit how large players can expand their islands
- `skyclaims.initial-size` now supports being set to 256
- Added a confirmation message to the admin delete shortcut in `/is info`
- Removed hard island limit from `isa transfer` _(multiple islands per player is still unsupported officially)_

# Beta 16
**REQUIRES: SF 2096+ & GP 255+ (latest versions are highly recommended)**
- Added admin command shortcuts to `/is info`
- Removed shorthand targets `i, c, & b` from tab completion
- Removed `skyclaims.arguments.island` permission
- Updated `/is reset` warning message to include the inventory reset
- Fixed a bug where the deprecated SQlite db name was used to name the migrated file

# Beta 15 - Hotfix
**REQUIRES: SF 2096+ & GP 251+ (latest versions are highly recommended)**
- Fixed islands being created on server join for players with islands and when config is set to false

# Beta 14
### WARNING: The SQLite DB file location has been moved! Ensure that you verify your database is successfully moved to the new location!
**REQUIRES: SF 2096+ & GP 251+ (latest versions are highly recommended)**
- Moved SQLite DB to `SPONGE_CONFIG_DIR/skyclaims/data` and deprecated SQLite configs
- Added Storage location config to be used for all file based SkyClaims data
- Fixed island commands returning an island outside the configured dimension
- Added config options to enable Schematic, Biome Type and Target permission checks (disabled by default)
- Schematic, Biome Type and Target arguments now only tab complete values that the player has permission to use
- Claim size is automatically expanded during startup if it is less than the owner's `initial-size`
- Default initial-size option increased from `32` to `48`
- A default schematic (SF3) is now automatically included
- SQLite now creates a backup automatically on migration

# Beta 13
**REQUIRES: SF 2096+ & GP 251+ (latest versions are highly recommended)**
- Fixed `is reset` not generating a new island
- Changed `is setbiome`'s default target to island

# Beta 12
**REQUIRES: SF 2096+ & GP 251+ (latest versions are highly recommended)**
- Updated to GP API v0.2
- Fixed bug affecting all commands designed to detect current island
- Added `/isa transfer` command to transfer island ownership
- Island claims no longer use a player's claim blocks
- Using `/deleteclaim` on an island claim requires confirmation to delete the island
- Island spawns are now set with the player's current rotation (runtime only)
- `/isa delete <user> [regen]` now accepts a optional boolean to disable region regeneration

# Beta 11
**REQUIRES: SF 2022+ & GP 229-249**
- Fixes saving new claim ids, if required, after DB loading completes
- More changes to prevent claim related errors from breaking the plugin (`/is create` will still fail, by design)
- Create DB connection on initialization instead of on each query
- Replaced "+" in biome names with "plus"
- Region centers are calculated with "double" accuracy

# Beta 10
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Added log on successful DB load
- Fixed new claims failing to generate on DB load (which prevented successful data loading)
- Removed claim from `/is list`
- Fix possible NPE fro `/is info`
- Improved username resolution (less "somebody")
- Save islands that required a new claim on startup

# Beta 9
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Renamed option `skyclaims.min-radius` to `skyclaims.initial-size`
- Replaced default biome config with `skyclaims.default-biome` option
- Fixed NPE when claim creation fails due to reaching max attempts
- Fixed exception caused by expired users
- Prevented plugin from initializing if GP API fails to load
- Database Type config setting implemented (MySQL still WIP)

# Beta 8
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Fix possible NPE on database load
- Allow admins to `/is setspawn`
- Prevent island spawn from being set at an negative y-level

# Beta 7
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Fixes `is list` not creating separate pages and causing IndexOutOfBoundsException

# Beta 6
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Removed redundant DB save on server stopping
- Renamed Database Config to Storage
- Fixed using the default world instead of the configured world

# Beta 5
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Fix DB backup on migrate
- Added Island count to Metrics
- Plugin now checks GP API version before initializing
- No longer runs Reset Commands on `/is delete`

# Beta 4
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Check radius option for valid int before applying
- Fix using `/is help` & `/isa help` to view help
- Removed unneeded check when restoring claims on restart

# Beta 3
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Actually fix is delete removing db entry

# Beta 2
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Fixes default options
- Should prevent creation of basic claims in island world
- Should prevent deletion or resizing of island claims
- Should respawn a player without a bed on their island
- Should fix island deletion

# Beta 1
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Added Support for the GP API. Requires GP 228+
- New Database Schema supports island ids and locks (automatic migration and backup)
- Improved claim handling
- Added island lock capability
- Split user & admin help commands
- Added new permissions
- Added MySQL support
- Added comments the the config
- Added protection for claim creation/deletion/resizing in SkyClaims world
- Added Options (default schematic & min island radius)
- Spawn admin-claim created automatically with first island
- General bug fixes and optimizations
