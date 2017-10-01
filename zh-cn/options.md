# 设置
设置类似于配置信息，但是它们需要权限管理插件并且可以自定义参数给玩家或者队伍。
当权限管理插件支持时候，设置也可以在不同世界中配置不同参数。

## 可选设置

| 选项 | 描述 |
| ------ | ----------- |
| `skyclaims.default-schematic` | 没有给定指定模板时候使用的默认模板 |
| `skyclaims.default-biome` | 默认的[生物群落类别](/DevOnTheRocks/SkyClaims/wiki/%E7%94%9F%E7%89%A9%E7%BE%A4%E8%90%BD%E7%B1%BB%E5%88%AB%EF%BC%88Biome-Types%EF%BC%89)如果为空，则设定为岛屿生物类别 |
| `skyclaims.min-size` | 空岛方块宽度的**一半**用来声明玩家可用空间。<br /> _取值范围： 8 - 256_ |
| `skyclaims.max-size` | 空岛最大宽度的**一半**。 命令 `/is expand` 的范围超出这个值。<br /> _取值范围： **min-size** - 256_ |
| `skyclaims.max-spawns` | 生成生物的最大数量。<br /> _0到disable_ |
| `skyclaims.max-spawns.hostile` | 敌对生物生成最大数量。<br /> _0 to disable_ |
| `skyclaims.max-spawns.passive` | 半敌对生物生成最大数量<br /> _0 to disable_ |
| `skyclaims.expiration` | 不活跃空岛的删除时限（天）。<br /> _0 to disable_ |

### 默认值
SkyClaims配置文件中可能含有他们的默认设置。如果你希望所有的玩家和分组有相同的配置，你只需要修改配置文件中的值即可。

###例子(以LuckPerms为例)
- `lp group default meta set skyclaims.default-schematic sf3` **->** 空岛模板默认使用sf3.schematic
- `lp group default meta set skyclaims.default-biome plains` **->** 空岛以plains生物群系生成
- `lp group default meta set skyclaims.initial-size 128` **->** 空岛初始范围为 256 x 256 个区块
- `lp group default meta set skyclaims.max-size 240` **->** 空岛最大为 480 x 480 个区块