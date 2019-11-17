# Schematics
Schematics are a tool use to save all the blocks in a selected area for later use. 
If you have use WorldEdit before, you are likely already familiar with schematics. 
**_SkyClaims only supports Sponge schematics (see below), not WorldEdit Schematics!_**

## Creating a Schematic
To create a sponge schematic using SkyClaims, you must have permission to use [`/is schematic create`](commands). 
A player with permission to create schematics can use a **Golden Axe** to select an area to later be saved as a schematic (right-click/left-click opposite corners). 
Once the proper area has been selected, **stand in the location that you want players to spawn** on a newly created island. 
Standing at the spawn point, use `is schematic create <name>` to automatically save the schematic to the SkyClaims schematic folder making it ready for use.

## Using Schematics

Commands such as `/is create` & `/is reset` will list all available schematics in a chest GUI if two or more exist unless a schematic argument is provided.  
The `Misc > Text-Schematic-List` config can be used to disable the chest GUI and force use of a text menu. 
Players may use this to choose a specific schematic if they have permission to use it. 

The schematic defined using the [option](options) is then used when no schematic is supplied.
A schematic argument permission is not checked when the default is used.
