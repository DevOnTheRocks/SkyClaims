# Change Log

## Upcoming/Unreleased Features, Changes & Bugfixes
**REQUIRED: SF build 2558+ & GP build 4.0.0.415+ OPTIONAL: Nucleus version 1.1.3+**
- Added Void World Generation capabilities
- Added Nether portal fix

# Beta 25 - S7.0
**REQUIRED: SF build 2624+ & GP build 4.2.0.418+ OPTIONAL: Nucleus version 1.2.0+**
- Added island invite & rank system for working together with other players
    - Added `/is invite` - `skyclaims.command.invite`
    - Added `/is kick` - `skyclaims.command.kick`
    - Added` /is leave` - `skyclaims.command.leave`
    - Added` /is promote` - `skyclaims.command.promote`
    - Added` /is demote` - `skyclaims.command.demote`
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

# Beta 24 - S7.0
**REQUIRED: SF build 2524+ & GP build 4.2.0.334+ OPTIONAL: Nucleus version 1.0.1+**
- Managers can now use `/is setspawn`, `/is setbiome`, & `/is expand`
- Added enhanced timings support
- Added Garden of Glass schematic to prepackaged schematics
- Fixed `/isa reload` breaking `/is home` &`/is sethome` commands
- Fixed entity limit feature preventing spawning in other dimensions
- Updated to latest Sponge API 7.0 snapshot

# Beta 23.1 - S7.0
**REQUIRED: SF build 2524+ & GP build 4.2.0.334+ OPTIONAL: Nucleus version 1.0.1+**
- Minimum Sponge Forge build now 2524 & minimum GriefPrevention build 334.
- Island claims now bypass size restrictions
- Fixed a bug where is list showed duplicate messages when there were no islands

## Beta 23
**REQUIRED: SF build 2497+ & GP build 4.0.0.319+ OPTIONAL: Nucleus version 1.0.1+**
- Updated GriefPrevention API. GP 4.0.0.319 is now the minimum required version.
- Block-break/block-place flags in the wilderness of the SkyClaim's world are set to false automatically.
- `/is expand` now cost 256 times more blocks due to GP's change to include y in claim block costs
- `/is home` & `/is sethome` are now exclusive to Nucleus integration 
- Cleaned up Nucleus Integration (mostly behind the scenes)

## Beta 22
**REQUIRED: SF 2096+ & GP 295+ OPTIONAL: Nucleus 1.0.1+**
- Fixes some entities still not being cleared during island reset/regen
- Added entity spawn limit capability to islands (disabled by default)
- Added island expiration and cleanup capability (disabled by default)
- New options: `skyclaims.max-spawns`, `skyclaims.max-spawns.hostile`, `skyclaims.max-spawns.passive` ,`skyclaims.expiration`
- New configs: Entity [Limit-Spawning, Max-Hostile, Max-Passives, Max-Spawns], Island-Expiration [Enabled, Interval, Threshold]
- Added island entity counts (living, item, & tile) to `/is info` 
- Added new sort types to `/is list`: entities-, entities+, tile-, tile+

## Beta 21
**REQUIRED: SF 2096+ & GP 292+ OPTIONAL: Nucleus 0.29.0+**
- Added `/is regen` (`skyclaims.command.regen`) to allow regenerating an island using a schematic.
- Added integration config to turn off/on optional integration features
- Fixed `/is reset` not clearing entities

## Beta 20
**REQUIRED: SF 2096+ & GP 292+ OPTIONAL: Nucleus 0.26.0+**
- Island locks now prevent players from entering another players island if they're not trusted
- Changed `skyclaims.admin.lock` to `skyclaims.admin.lock.others` 
- Added `skyclaims.admin.lock.bypass` to allow bypassing of island locks

## Beta 19.1 - Hotfix
**REQUIRED: SF 2096+ & GP 260+ OPTIONAL: Nucleus 0.26.0+**
- Fixed `/is home` checking the permissions of the island it is run on. Fixes #34
- Fixed `/is sethome` not checking permission of the island it is run on.

## Beta 19
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

## Beta 18
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

## Beta 17
**REQUIRES: SF 2096+ & GP 255+ (latest versions are highly recommended)**
- Fixed island width calculation being off by 2 on `/is info`
- Added `/is expand [blocks]` to allow player to expand their islands using claim blocks _(subtracted from bonus claim blocks - will regen)_
- Added `skyclaims.max-size` option to limit how large players can expand their islands
- `skyclaims.initial-size` now supports being set to 256
- Added a confirmation message to the admin delete shortcut in `/is info`
- Removed hard island limit from `isa transfer` _(multiple islands per player is still unsupported officially)_

## Beta 16
**REQUIRES: SF 2096+ & GP 255+ (latest versions are highly recommended)**
- Added admin command shortcuts to `/is info`
- Removed shorthand targets `i, c, & b` from tab completion
- Removed `skyclaims.arguments.island` permission
- Updated `/is reset` warning message to include the inventory reset
- Fixed a bug where the deprecated SQlite db name was used to name the migrated file

## Beta 15 - Hotfix
**REQUIRES: SF 2096+ & GP 251+ (latest versions are highly recommended)**
- Fixed islands being created on server join for players with islands and when config is set to false

## Beta 14
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

## Beta 13
**REQUIRES: SF 2096+ & GP 251+ (latest versions are highly recommended)**
- Fixed `is reset` not generating a new island
- Changed `is setbiome`'s default target to island

## Beta 12
**REQUIRES: SF 2096+ & GP 251+ (latest versions are highly recommended)**
- Updated to GP API v0.2
- Fixed bug affecting all commands designed to detect current island
- Added `/isa transfer` command to transfer island ownership
- Island claims no longer use a player's claim blocks
- Using `/deleteclaim` on an island claim requires confirmation to delete the island
- Island spawns are now set with the player's current rotation (runtime only)
- `/isa delete <user> [regen]` now accepts a optional boolean to disable region regeneration

## Beta 11
**REQUIRES: SF 2022+ & GP 229-249**
- Fixes saving new claim ids, if required, after DB loading completes
- More changes to prevent claim related errors from breaking the plugin (`/is create` will still fail, by design)
- Create DB connection on initialization instead of on each query
- Replaced "+" in biome names with "plus"
- Region centers are calculated with "double" accuracy

## Beta 10
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Added log on successful DB load
- Fixed new claims failing to generate on DB load (which prevented successful data loading)
- Removed claim from `/is list`
- Fix possible NPE fro `/is info`
- Improved username resolution (less "somebody")
- Save islands that required a new claim on startup

## Beta 9
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Renamed option `skyclaims.min-radius` to `skyclaims.initial-size`
- Replaced default biome config with `skyclaims.default-biome` option
- Fixed NPE when claim creation fails due to reaching max attempts
- Fixed exception caused by expired users
- Prevented plugin from initializing if GP API fails to load
- Database Type config setting implemented (MySQL still WIP)

## Beta 8
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Fix possible NPE on database load
- Allow admins to `/is setspawn`
- Prevent island spawn from being set at an negative y-level

## Beta 7
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Fixes `is list` not creating separate pages and causing IndexOutOfBoundsException

## Beta 6
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Removed redundant DB save on server stopping
- Renamed Database Config to Storage
- Fixed using the default world instead of the configured world

## Beta 5
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Fix DB backup on migrate
- Added Island count to Metrics
- Plugin now checks GP API version before initializing
- No longer runs Reset Commands on `/is delete`

## Beta 4
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Check radius option for valid int before applying
- Fix using `/is help` & `/isa help` to view help
- Removed unneeded check when restoring claims on restart

## Beta 3
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Actually fix is delete removing db entry

## Beta 2
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Fixes default options
- Should prevent creation of basic claims in island world
- Should prevent deletion or resizing of island claims
- Should respawn a player without a bed on their island
- Should fix island deletion

## Beta 1
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