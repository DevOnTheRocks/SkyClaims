# 更新日志

<a href="https://github.com/DevOnTheRocks/SkyClaims/blob/sponge/api-7/CHANGELOG.md"><img src="https://github.com/DevOnTheRocks/SkyClaims/wiki/images/united_states.png" title="en-US" height="20"> English</a>
| <img src="https://github.com/DevOnTheRocks/SkyClaims/wiki/images/china.png" title="zh-CN" height="20"> <b>Chinese</b>

## 即将到来的更新
**必须插件：SF 2558+ & GP 4.2.0.418+ 可选插件：Nucleus 1.2.0+**
- 添加空白世界生成的能力
- 修复地狱门

# Beta 26.2 - S7.0 紧急修复版本
**必须插件：SF 2688+ & GP 4.3.0.473+ 可选插件：Nucleus 1.2.0+**
- 修复了一个当使用命令 `is lock` & `is unlock` 时，因为权限检查返回非法数值时产生的bug
- 修复 了使用命令 `is delete` 的帮助文本问题
- 添加了一个独立的GP flag到非空岛区域来允许玩家自定义空白区域的设置

# Beta 26.1 - S7.0
**必须插件：SF 2688+ & GP 4.3.0.473+ 可选插件：Nucleus 1.2.0+**
- 将命令`/isa delete` 修改为 `/is delete` 并且允许空岛所有者将他们的空岛永久删除
    - `skyclaims.command.delete` 基础命令使用权限
    - `skyclaims.admin.delete` 允许删除任意的空岛
- `/is lock` & `/is unlock` 现在空岛管理者也可以使用了
- 上锁的空岛现在会自动踢出非本岛的成员
- 添加权限 `skyclaims.admin.kick.exempt` - 防止某人被命令 `/is kick` and `/is lock` 踢出
- 现在 `island` 命令仅仅会补全他们有权限的部分，除非他们拥有 `skyclaims.admin.list` 权限 （感谢@luckyu19帮忙理顺这部分的翻译）
- 修复了当使用 `/is expand` 命令时重复弹出确认消息却不扩展空岛的问题
- 修复了自动删除空岛不清除区域的问题
- 修复了命令 `/is leave` & `/is kick` 在当配置文件修改后无法使用的问题
- 修复了可点击文本的一些显示问题

# Beta 26 - S5.1
**必须插件：SF 2637+ & GP 4.0.0.451+ 可选插件：Nucleus 1.1.3+**
- 更新插件最低版本需求
- 空岛现在使用村庄生物类别 - 这个改动可以让所有村庄的特性得以实现
- 添加 Sky Exchange island 预设模板 _(需要开启命令方块 & 建议设置世界/岛屿生成高度为1)_
- 用`/is reset [keepinv]`命令代替`/is regen` (_权限需求_ `skyclaims.admin.reset.keepinv`)
- 修复了一个不常见的当使用`/is info`命令时候的空指针异常
- 空岛现在 **应该** 支持GP中新的最大/最小高度的设定

# Beta 25.1 - S7.0 Hotfix 
**必须插件：SF 2624+ & GP 4.2.0.418+ 可选插件：Nucleus 1.2.0+**
- Fixed islands being created at Y 1 instead of the configured height.
- Added configurable spawn world
- Added `/scversion` - `skyclaims.admin.version` to aid in debugging dependency issues

# Beta 25 - S7.0
**必须插件：SF 2624+ & GP 4.2.0.418+ 可选插件：Nucleus 1.2.0+**
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
