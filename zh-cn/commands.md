# SkyClaims 命令# SkyClaims Commands

## 用户命令
| 命令 (快速指令) | 描述 | 权限 |
| --------------- | ----------- | ---------- |
| `skyclaims`<br />`island`<br />`is` | 执行其他命令或显示帮助的主要命令 | `无` |
| `is help` | 显示帮助 | `skyclaims.command.help` |
| `is create [schematic]` | 创建一个空岛 | `skyclaims.command.create`<br />_*参数支持见 [argument perms](#command-arguments)_ |
| `is expand [blocks]` | 扩大空岛面积 | `skyclaims.command.expand` |
| `is demote <user>` | 用来降低某个空岛玩家权限 | `skyclaims.command.demote` |
| `is info [player]` | 显示空岛信息，或者指定玩家的空岛信息 | `skyclaims.command.info`<br />管理员权限：`skyclaims.admin.info` |
| `is invite [user] [privilege]` | 将某个玩家邀请加入空岛 | `skyclaims.command.invite` |
| `is kick <user>` | 将某个被邀请玩家踢出 | `skyclaims.command.kick` |
| `is leave` | 离开某个被邀请的空岛 | `skyclaims.command.leave` |
| `is list [user] [sort]` | 显示空岛列表，基础权限仅仅允许玩家列出他们所参与的所有空岛列表。此外，你可以允许玩家列出所有的未上锁空岛或者使用排序指令。 || 基础权限：`skyclaims.command.list.base`<br />列出未上锁权限：`skyclaims.command.list.unlocked`<br />是否可以使用排序权限：`skyclaims.command.list.sort`<br />管理员权限：`skyclaims.admin.list` |
| `is lock`<br />`is unlock` | 空岛是否上锁，<br />注意：如果上锁，游客将不可访问你的空岛 | `skyclaims.command.lock`<br />管理员权限：`skyclaims.admin.lock.others` |
| `is promote [user]` | 用来提升某个空岛玩家权限 | `skyclaims.command.promote` |
| `is reset [schematic] [keepinv]` | 重建空岛 <br />(**注意！**：重建空岛会默认清空物品栏和背包) | `skyclaims.command.reset`<br/>`skyclaims.admin.reset.keepinv`<br/>_*参数支持见 [argument perms](#command-arguments)_ |
| `is setbiome <biome> [target]` | 设置空岛生物群系，可以指定单独区域 (默认：全岛) | `skyclaims.command.setbiome`<br />_*参数支持见 [argument perms](#command-arguments)_ |
| `is setspawn` | 设置空岛出生点 | `skyclaims.command.setspawn`<br /管理员权限：`skyclaims.admin.setspawn` |
| `is spawn [player]`<br />`is tp [player]` | 传送到自己的或者指定玩家的空岛出生点 | `skyclaims.command.spawn`<br />管理员权限： `skyclaims.admin.spawn` |

### 可扩展指令

| 命令 (简写) | 描述 | 权限 |
| --------------- | ----------- | ---------- |
| `is sethome` | 设置空岛中的home点. **\*需要插件Nucleus支持** | `skyclaims.command.sethome` |
| `is home` | 传送到空岛的home点 **\*需要插件Nucleus支持** | `skyclaims.command.home` |

## 命令参数
**以下命令参数的权限检查可以通过配置SkyClaims的配置文件来打开/关闭**

| 命令 - 参数 | 描述 | 权限 |
| --------------- | ----------- | ---------- |
| _create/reset_ - `[schematic]` | 建立非默认空岛模板指令 | `skyclaims.arguments.schematics.<schematic>` |
| _setbiome_ - `<biome>` | 限制生物群系的更改<br />（译者注：这里设置哪种生物群系，哪种生物群系才能设置） | `skyclaims.arguments.biomes.<biome>` |
| _setbiome_ - `[target]` | 限制生物群系大小 | `skyclaims.arguments.block`<br />`skyclaims.arguments.chunk` |

## 管理员指令
| 指令(快速指令) | 描述 | 权限 |
| --------------- | ----------- | ---------- |
| `isa`<br />`is admin` | 运行管理员指令或者管理员帮助 | `skyclaims.admin.base` |
| `isa cs <name>`| 新建空岛模板<br />(选择工具为金斧) | `skyclaims.admin.schematic.create` |
| `isa transfer [owner] <newowner>` | 将某个空岛转赠给另一个玩家 | `skyclaims.admin.transfer` |
| `isa delete <player> [regen]` | 删除某玩家岛屿， 接受布尔值参数来决定是否重建 | `skyclaims.admin.delete` |
| `isa reload` | 重新加载插件的所有文件  | `skyclaims.admin.reload` |

### 除错指令
\* _即使SkyClaims在插件加载阶段报错而未加载的情况下也会工作_

| 指令(快速指令) | 描述 | 权限 |
| --------------- | ----------- | ---------- |
| `scversion` | 用于显示SkyClaims的版本信息  | `skyclaims.admin.version` |
