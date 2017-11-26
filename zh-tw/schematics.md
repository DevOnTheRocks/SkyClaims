# Schematics
Schematics是一個可以存儲所選區域所有有方塊最後編輯狀態的工具。
如果你之前用過WorldEdit，那麼你應該對於這個工具很熟悉了。
**_SkyClaims僅僅支持Sponge生成的Schematics(詳情見下文)而不是WorldEdit生成的Schematics._**

## 創建一個Schematic
你必須擁有使用[`isa cs`](命令（Commands）)的權限，然後用金斧來選定一個區域。 （左鍵/右鍵所選區域對角線上的點）。
選中後，**站在你想讓空島玩家出生的位置**，使用`isa cs <name>`命令來存儲一個schematic文件在\config\skyclaims\schematics文件夾裡面。

## 使用空島模板
類似於`is create` & `is reset`指令可以接受一個空島名稱的參數。
有使用這兩個指令的玩家可以選擇一個指定的空島模板進行生存。
當沒有指定參數時候，可以參考[設置](設置（Options）)來設置一個默認的模板。
當你設置了默認值時，可以不必去設置空島模板的權限。