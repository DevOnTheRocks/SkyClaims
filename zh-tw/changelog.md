# 更新日誌

# 即將到來的更新
- 增加空白世界的修復能力
- 修復地獄門

# Beta 26.2 修復補丁
- 修正一個使用命令 `is lock` & `is unlock` 时，因為檢查權限而返回不正確数值時產生的錯誤
- 修正了“is delete”幫助文本中的拼寫錯誤
- 增加了一個GP flag到非空島區域來允許玩家自定義空白區域的設置 
 
# Beta 26.1
- 將命令`/isa delete` 修改為 `/is delete` ，島主可以使用它永久刪除它的空島 
    - `skyclaims.command.delete` 基礎命令使用權限 
    - `skyclaims.admin.delete` 允許刪除其他玩家的島嶼並使用明確的參數刪除島嶼而不清除
- `/is lock` & `/is unlock` 現在可以被島嶼管理者使用了 
- 上鎖的空島現在回會自動踢出非空島成員 
- 增加權限 `skyclaims.admin.kick.exempt` - 防止某人被命令 `/is kick` and `/is lock` 踢出
- 現在 `island` 命令只會補全他們所有權限的部分，除非他們擁有 `skyclaims.admin.list` 權限 （感谢@luckyu19帮忙理顺这部分的翻译） 
- 修正了當使用 `/is expand` 命令時重複彈出信息卻不拓展空島的問題 
- 修正了自動刪除島嶼不清除區域的问题 
- 修正了命令 `/is leave` & `/is kick` 在修改了配置文件后無法使用的問題
- 修正了可點擊問題的一些顯示問題 
 
# Beta 26
- 更新插件的最低版本需求
- 空島現在使用村莊生物類別 - 這個改動可以讓村莊的所有特性得以實現 
- 增加 Sky Exchange island 預設模板 _(需要開啟命令方塊 & 建議設置世界/島嶼生成高度為1)_ 
- 使用`/is reset [keepinv]`命令替代`/is regen` (_权限需求_ `skyclaims.admin.reset.keepinv`) 
- 修正了一個罕見的當使用`/is info`命令時候的空指針錯誤 
- 空島現在 **應該** 支持GP中新的最大/最小高度的設定
 

- `/is info` 命令显示现在对于所有者和成员有了不同的颜色和编码
- 添加_Misc/Teleport-on-Creation_ 配置，使得可以在创建空岛以后禁用自动传送（默认自动传送）
- `/is create` & `/is reset` 现在支持点击菜单
- 添加Void Island Control 地图到默认地图列表
- 修正了具有实体限制功能的IndexOutOfBoundsException报错
