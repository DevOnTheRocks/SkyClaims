# SkyClaims 命令# SkyClaims Commands

## 用户命令
| 命令 (快速指令) | 描述 | 权限 |
| --------------- | ----------- | ---------- |
| `skyclaims`<br />`island`<br />`is` | 执行其他命令或显示帮助的主要命令 | `无` |
| `is help` | 显示帮助 | `skyclaims.command.help` |
| `is create [schematic]` | 创建一个空岛 | `skyclaims.command.create`<br />_*supports [argument perms](#command-arguments)_ |
| `is reset [schematic]` | 创建空岛 <br />(**注意！**：重建空岛会清空所有物品且不可逆) | `skyclaims.command.reset`<br />_*supports [argument perms](#command-arguments)_ |
| `is regen [schematic]` | 恢复用户的岛屿 | `skyclaims.command.regen`<br />_*supports [argument perms](#command-arguments)_ |
| `is expand [blocks]` | 扩大空岛面积 | `skyclaims.command.expand` |
| `is list` | 显示所有空岛列表 | `skyclaims.command.list`<br />others: `skyclaims.admin.list` |
| `is lock`<br />`is unlock` | 空岛是否上锁，<br />注意：如果上锁，游客将不可访问你的空岛 | `skyclaims.command.lock` |
| `is info [player]` | 显示空岛信息，或者指定玩家的空岛信息 | `skyclaims.command.info`<br />others: `skyclaims.admin.info` |
| `is setspawn` | 设置空岛出生点 | `skyclaims.command.setspawn` |
| `is setbiome <biome> [target]` | 设置空岛生物群系，可以指定单独区域 (默认：全岛) | `skyclaims.command.setbiome`<br />_*supports [argument perms](#command-arguments)_ |
| `is spawn [player]`<br />`is tp [player]` | 传送到自己的或者指定玩家的空岛出生点 | `skyclaims.command.spawn`<br />其他人： `skyclaims.admin.spawn` |

### 可扩展指令

| 命令 (简写) | 描述 | 权限 |
| --------------- | ----------- | ---------- |
| `is sethome` | 设置空岛中的home点. **\*需要插件Nucleus支持** | `skyclaims.command.sethome` |
| `is home` | 传送到空岛的home点 **\*需要插件Nucleus支持** | `skyclaims.command.home` |

## 命令参数
**以下命令参数的权限检查可以通过配置SkyClaims的配置文件来打开/关闭**

| 参数 | 描述 | 权限 |
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
| `isa reload` | 重新加载插件  | `skyclaims.admin.reload` |