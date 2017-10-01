# Schematics
Schematics are a tool use to save all the blocks in a selected area for later use. If you have use WorldEdit before, you are likely already familiar with schematics. **_SkyClaims only officially supports Sponge Schematics (see below), not WorldEdit Schematics._**

## Creating a Schematic
To create a schematic with SkyClaims, you must have permission to use [`/isa cs`](Commands). A player with permission to create schematics can use a Golden Axe to select an area to later be saved as a schematic (right-click/left-click opposite corners). Once the proper area has been selected, **stand in the location that you want players to spawn** on a newly created island. Standing at the spawn point, use `isa cs <name>` to automatically save the schematic to the SkyClaims schematic folder making it ready for use.

## Using Schematics
Commands such as `/is create` & `/is reset` accept an optional schematic argument. Players may use this to choose a specific schematic if they have permission to use it. The default schematic used, when no schematic is supplied, can be defined using an [option](Options). A schematic argument permission is not checked when the default is used.