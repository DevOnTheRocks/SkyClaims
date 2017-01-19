# Change Log
### Beta 10
- Added log on successful DB load
- Fixed new claims failing to generate on DB load (which prevented successful data loading)
- Removed claim from `/is list`
- Fix possible NPE fro `/is info`
- Improved username resolution (less "somebody")
- Save islands that required a new claim on startup
### Beta 9
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Renamed option `skyclaims.min-radius` to `skyclaims.initial-size`
- Replaced default biome config with `skyclaims.default-biome` option
- Fixed NPE when claim creation fails due to reaching max attempts
- Fixed exception caused by expired users
- Prevented plugin from initializing if GP API fails to load
- Database Type config setting implemented (MySQL still WIP)
### Beta 8
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Fix possible NPE on database load
- Allow admins to `/is setspawn`
- Prevent island spawn from being set at an negative y-level
### Beta 7
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Fixes `is list` not creating pages and causing IndexOutOfBoundsException
### Beta 6
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Removed redundant DB save on server stopping
- Renamed Database Config to Storage
- Fixed using the default world instead of the configured world
### Beta 5
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Fix DB backup on migrate
- Added Island count to Metrics
- Plugin now checks GP API version before initializing
- No longer runs Reset Commands on `/is delete`
### Beta 4
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Check radius option for valid int before applying
- Fix using `/is help` & `/isa help` to view help
- Removed unneeded check when restoring claims on restart
### Beta 3
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Actually fix is delete removing db entry
### Beta 2
**REQUIRES: SF 2022+ & GP 229+ (latest versions are highly recommended)**
- Fixes default options
- Should prevent creation of basic claims in island world
- Should prevent deletion or resizing of island claims
- Should respawn a player without a bed on their island
- Should fix island deletion
### Beta 1
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