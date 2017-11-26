# SkyClaims 命令# SkyClaims Commands

## 用戶命令
| 命令 (快速指令) | 描述 | 權限 |
| --------------- | ----------- | ---------- |
| `skyclaims`<br />`island`<br />`is` | 執行其他命令或顯示幫助的主要命令 | `無` |
| `is help` | 顯示幫助 | `skyclaims.command.help` |
| `is create [schematic]` | 創建一個空島 | `skyclaims.command.create`<br />_*参数支持见 [argument perms](#command-arguments)_ |
| `is expand [blocks]` | 擴大空島面積 | `skyclaims.command.expand` |
| `is demote <user>` | 用來降低某個空島玩家的權限 | `skyclaims.command.demote` |
| `is info [player]` | 顯示空島信息，或者指定玩家的空島信息 | `skyclaims.command.info`<br />管理员权限：`skyclaims.admin.info` |
| `is invite [user] [privilege]` | 將某個玩家邀請進空島 | `skyclaims.command.invite` |
| `is kick <user>` | 將某個被邀請玩家踢出 | `skyclaims.command.kick` |
| `is leave` | 離開某個被邀請的空島 | `skyclaims.command.leave` |
| `is list [user] [sort]` | 顯示空島列表，基礎權限僅僅允許玩家列出他們所參與的所有空島列表。此外，你可以允許玩家列出所有的未上鎖空島或者使用排序指令。 | 基礎權限：`skyclaims.command.list.base`<br />列出未上鎖權限：`skyclaims.command.list.unlocked`<br />是否可以使用排序權限：`skyclaims.command.list.sort`<br />管理員權限：`skyclaims.admin.list` |
| `is lock`<br />`is unlock` | 空島是否上鎖，<br />注意：如果上鎖，遊客將不可訪問你的空島 | `skyclaims.command.lock`<br />管理員權限：`skyclaims.admin.lock.others` |
| `is promote [user]` | 用來提升某個空島玩家權限 | `skyclaims.command.promote` |
| `is reset [schematic] [keepinv]` | 重建空島 <br />(**注意！**：重建空島會默認清空物品欄與背包) | `skyclaims.command.reset`<br/>`skyclaims.admin.reset.keepinv`<br/>_*参数支持見 [argument perms](#command-arguments)_ |
| `is setbiome <biome> [target]` | 設置空島生物群系，可以指定單獨區域 （默認：全島) | `skyclaims.command.setbiome`<br />_*參數主持見 [argument perms](#command-arguments)_ |
| `is setspawn` | 設置空島出生點 | `skyclaims.command.setspawn`<br /管理員權限：`skyclaims.admin.setspawn` |
| `is spawn [player]`<br />`is tp [player]` | 传送到自己的或者指定玩家的空岛出生点 | `skyclaims.command.spawn`<br />管理員權限： `skyclaims.admin.spawn` |

### 可拓展指令

| 命令 (簡寫) | 描述 | 權限 |
| --------------- | ----------- | ---------- |
| `is sethome` | 設置空島中的home. **\*需要插件Nucleus支持** | `skyclaims.command.sethome` |
| `is home` | 傳送到空島的home **\*需要插件Nucleus支持** | `skyclaims.command.home` |

## 命令參數
**以下命令參數的權限檢查可以通過配置SkyClaims的配置文件來打開/關閉**

| 命令 - 參數 | 描述 | 權限 |
| --------------- | ----------- | ---------- |
| _create/reset_ - `[schematic]` | 建立非默認空島模板指令 | `skyclaims.arguments.schematics.<schematic>` |
| _setbiome_ - `<biome>` | 限制生物群系的更改<br />（譯者注：這裡設置哪種生物群系，哪种生物群系才能設置） | `skyclaims.arguments.biomes.<biome>` |
| _setbiome_ - `[target]` | 限制生物群系大小 | `skyclaims.arguments.block`<br />`skyclaims.arguments.chunk` |

## 管理員指令
| 指令(快速指令) | 描述 | 權限 |
| --------------- | ----------- | ---------- |
| `isa`<br />`is admin` | 運行管理員指令或者管理員幫助 | `skyclaims.admin.base` |
| `isa cs <name>`| 新建空島模板<br />(選擇工具為金斧) | `skyclaims.admin.schematic.create` |
| `isa transfer [owner] <newowner>` | 將某個空島贈送給另一個玩家 | `skyclaims.admin.transfer` |
| `isa delete <player> [regen]` | 刪除某玩家島嶼， 接受布爾值參數來決定是否重建 | `skyclaims.admin.delete` |
| `isa reload` | 重新加载插件的所有文件  | `skyclaims.admin.reload` |

### 除錯指令
\* _即使SkyClaims在插件加載階段報錯而未加載的情況下也會工作_

| 指令(快速指令) | 描述 | 權限 |
| --------------- | ----------- | ---------- |
| `scversion` | 用於顯示SkyClaims的版本信息  | `skyclaims.admin.version` |
