# Privilege System <!-- @subtank needs translation. Remove comment once complete. -->

## About
The SkyClaims Privilege system is used to determine what a player may do on an island. Privilege levels were added in Beta 25 and closely resemble 
GriefPrevention's [trust types](https://github.com/MinecraftPortCentral/GriefPrevention/wiki/Trust-System).

## Levels
- Owner 
    - The highest privilege level. 
    - Only one owner per island. 
    - Equivalent to GriefPrevention's claim owner.
    - Can use all island features/commands provided they have permission to the command.
- Manager
    - Can only be added or promoted by the owner.
    - Equivalent to GriefPrevention's manager.
    - Has access to most island features/commands. Cannot reset or delete an island.
- Member
    - Can be added by the owner or a manager.
    - Equivalent to GriefPrevention's builder.
    - Has only basic island access. Cannot use any command that edits any island properties.
- None
    - The default privilege level.
    - Can only visit an island if it is unlocked or is directly teleported to it.
    - Prevented from interacting with blocks and items on the island