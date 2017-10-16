# Frequently Asked Questions


## 我如何阻止玩家在岛外放置方块
>~~在GriefPrevention（后文用GP代替）权限管理中，默认玩家可以在野外（即领地外）放置方块。所以如果要防止玩家在领地外放置方块，你需要用[GP flags](https://github.com/MinecraftPortCentral/GriefPrevention/wiki/Flags)来设置玩家在领地外的`place-block`权限为`denied`
并且由于这个不是GP插件API的设置，你需要手动来完成这个设定。~~**升级到最新的Beta23版本自带这些设置，不必玩家自行设置。**

> 具体操作如下：站在领地外（用`/claiminfo`确认是否在领地外），使用命令：`/cf block-break any false`。

## 我如何来创建一个完全空白的世界？

> （译者：个人建议使用YUNoMakeGoodMap里面的那个空白世界设定）

> SkyClaims不能够影响世界生成，所以你需要利用sponge或者其他世界管理插件或者mod来生成世界。如：
> 1.sponge提供了一个`sponge:void`的选项在world.conf文件配置里面。
> 2.其他世界管理插件如 Nucleus 或者 Project Worlds。forge mod如 YUNoMakeGoodMap 或者 Garden of Glass 也可以生成空白世界。
