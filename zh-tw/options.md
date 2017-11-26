# 設置
設置類似於配置信息，但是它們需要權限管理插件並且可以自定義參數給玩家或者隊伍。
當權限管理插件支持時候，設置也可以在不同世界中配置不同參數。

## 可選設置

| 選項 | 描述 |
| ------ | ----------- |
| `skyclaims.default-schematic` | 沒有給定指定模板時候使用的默認模板 |
| `skyclaims.default-biome` | 默認的[生物群落類別](/DevOnTheRocks/SkyClaims/wiki/%E7%94%9F%E7%89%A9%E7%BE%A4%E8%90%BD%E7 %B1%BB%E5%88%AB%EF%BC%88Biome-Types%EF%BC%89)如果為空，則設定為島嶼生物類別 |
| `skyclaims.min-size` | 空島方塊寬度的**一半**用來聲明玩家可用空間。 <br /> _取值範圍： 8 - 256_ |
| `skyclaims.max-size` | 空島最大寬度的**一半**。命令 `/is expand` 的範圍超出這個值。 <br /> _取值範圍： **min-size** - 256_ |
| `skyclaims.max-spawns` | 生成生物的最大數量。 <br /> _0到disable_ |
| `skyclaims.max-spawns.hostile` | 敵對生物生成最大數量。 <br /> _0 to disable_ |
| `skyclaims.max-spawns.passive` | 半敵對生物生成最大數量<br /> _0 to disable_ |
| `skyclaims.expiration` | 不活躍空島的刪除時限（天）。 <br /> _0 to disable_ |

### 默認值
SkyClaims配置文件中可能含有他們的默認設置。如果你希望所有的玩家和分組有相同的配置，你只需要修改配置文件中的值即可。

###例子(以LuckPerms為例)
- `lp group default meta set skyclaims.default-schematic sf3` **->** 空島模板默認使用sf3.schematic
- `lp group default meta set skyclaims.default-biome plains` **->** 空島以plains生物群系生成
- `lp group default meta set skyclaims.initial-size 128` **->** 空島初始範圍為 256 x 256 個區塊
- `lp group default meta set skyclaims.max-size 240` **->** 空島最大為 480 x 480 個區塊