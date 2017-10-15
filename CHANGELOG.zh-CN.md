# 更新日志

<a href="https://github.com/DevOnTheRocks/SkyClaims/blob/sponge/api-5/CHANGELOG.md"><img src="https://github.com/DevOnTheRocks/SkyClaims/wiki/images/united_states.png" title="en-US" height="20"> English</a>
| <img src="https://github.com/DevOnTheRocks/SkyClaims/wiki/images/china.png" title="zh-CN" height="20"> <b>Chinese</b>

## 即将到来的更新
**必须插件：SF 2637+ & GP 4.0.0.451+ 可选插件：Nucleus 1.1.3+**
- 添加空白世界生成的能力
- 修复地狱门

# Beta 26 - S5.1
**必须插件：SF 2637+ & GP 4.0.0.451+ 可选插件：Nucleus 1.1.3+**
- 更新插件最低版本需求
- 空岛现在使用村庄生物类别 - 这个改动可以让所有村庄的特性得以实现
- 添加 Sky Exchange island 预设模板 _(需要开启命令方块 & 建议设置世界/岛屿生成高度为1)_
- 用`/is reset [keepinv]`命令代替`/is regen` (_权限需求_ `skyclaims.admin.reset.keepinv`)
- 修复了一个不常见的当使用`/is info`命令时候的空指针异常
- 空岛现在 **应该** 支持GP中新的最大/最小高度的设定

# Beta 25.1 - S5.1 Hotfix
**必须插件：SF 2558+ & GP 4.0.0.415+ 可选插件：Nucleus 1.1.3+**
- Fixed islands being created at Y 1 instead of the configured height.
- Added configurable spawn world
- Added `/scversion` - `skyclaims.admin.version` to aid in debugging dependency issues

# Beta 25 - S5.1
**必须插件：SF 2558+ & GP 4.0.0.415+ 可选插件：Nucleus 1.1.3+**
- 添加空岛邀请以及排名系统
    - 添加命令 `/is invite` - `skyclaims.command.invite`
    - 添加命令 `/is kick` - `skyclaims.command.kick`
    - 添加命令 `/is leave` - `skyclaims.command.leave`
    - 添加命令 `/is promote` - `skyclaims.command.promote`
    - 添加命令 `/is demote` - `skyclaims.command.demote`
    - `/is invite`命令现在自动执行GP的 `/trust` & `/permissiontrust` 命令
- 强化并重写命令`/is list`：
    - 现在以如下几种方式来代替以前的**[O]** - 拥有者，**[M]** - 管理者，或者**[T]**  信任者，在设置后会出现[L]或[T]
    - `skyclaims.command.list.base` - 允许玩家使用列表命令（显示玩家的岛屿，即所有者，管理者或受信任者）
    - `skyclaims.command.list.unlocked` - 允许玩家列出所有未上锁的空岛
    - `skyclaims.command.list.sort` - 允许玩家使用列表排序功能
    - `skyclaims.admin.list` - 完整的list权限
- `/is info` 命令显示现在对于所有者和成员有了不同的颜色和编码
- 添加_Misc/Teleport-on-Creation_ 配置，使得可以在创建空岛以后禁用自动传送（默认自动传送）
- `/is create` & `/is reset` 现在支持点击菜单
- 添加Void Island Control 地图到默认地图列表
- 修正了具有实体限制功能的IndexOutOfBoundsException报错
