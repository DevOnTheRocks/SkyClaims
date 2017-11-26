# Frequently Asked Questions


## 我如何阻止玩家在島外放置方塊
>~~在GriefPrevention（後文用GP代替）權限管理中，默認玩家可以在野外（即領地外）放置方塊。所以如果要防止玩家在領地外放置方塊，你需要用[GP flags](https://github.com/MinecraftPortCentral/GriefPrevention/wiki/Flags)来设置玩家在领地外的`place-block`權限为`denied`
並且由於這個不是GP插件API的設置，你需要手動来完成這個设定。~~**升級到最新的Beta23+版本自带這些设置，不必玩家自行设置。**

> 具體操作如下：站在領地外（用`/claiminfo`確認是否在領地外），使用命令：`/cf block-break any false`。

## 我如何来創建一个完全空白的世界？

> （譯者：个人建議使用YUNoMakeGoodMap裡面的那個空白世界设定）

> SkyClaims不能夠影响世界生成，所以你需要利用sponge或者其他世界管理插件或者mod來生成世界。如：
> 1.sponge提供了一個`sponge:void`的選項在world.conf文件配置里面。
> 2.其他世界管理插件如 Nucleus 或者 Project Worlds。forge mod如 YUNoMakeGoodMap 或者 Garden of Glass 也可以生成空白世界。
