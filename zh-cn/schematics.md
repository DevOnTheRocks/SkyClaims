# Schematics
Schematics是一个可以存储所选区域所有有方块最后编辑状态的工具。 
如果你之前用过WorldEdit，那么你应该对于这个工具很熟悉了。
**_SkyClaims仅仅支持Sponge生成的Schematics(详情见下文)而不是WorldEdit生成的Schematics._**

## 创建一个Schematic
你必须拥有使用[`isa cs`](命令（Commands）)的权限，然后用金斧来选定一个区域。（左键/右键所选区域对角线上的点）。
选中后，**站在你想让空岛玩家出生的位置**，使用`isa cs <name>`命令来存储一个schematic文件在\config\skyclaims\schematics文件夹里面。

## 使用空岛模板
类似于`is create` & `is reset`指令可以接受一个空岛名称的参数。
有使用这两个指令的玩家可以选择一个指定的空岛模板进行生存。
当没有指定参数时候，可以参考[设置](设置（Options）)来设置一个默认的模板。
当你设置了默认值时，可以不必去设置空岛模板的权限。