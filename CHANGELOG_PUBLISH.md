注意：1.2.0 的暴击伤害不兼容之前的版本，升级后暴击伤害会降低，是由于计算方式改变导致的，升级前请备份存档！

Note: The critical hit damage of 1.2.0 is not compatible with previous versions.   
After upgrading, the critical hit damage will be reduced due to the change in calculation method.   
Please back up the archive before upgrading!


## 变更

1. 使用 mixinextras 重构 mixin，理论兼容更多其他 mod；
2. 微调暴击爆伤数值和概率。
3. 兼容 AdditionalEntityAttributes

   当 AdditionalEntityAttributes 加载时暴击伤害和挖掘速度将使用 AdditionalEntityAttributes 的；

[完整更新日志](https://github.com/LangYueMc/AutoTranslation/blob/master/CHANGELOG.md)

## Changed

1. Use `mixinextras` to reconstruct mixin, which is theoretically compatible with more other mods;
2. Fine-tune the critical damage value and probability.
3. Compatible `AdditionalEntityAttributes`

   Critical damage and mining speed will use `AdditionalEntityAttributes` when `AdditionalEntityAttributes` is loaded;
 
[Full CHANGELOG](https://github.com/LangYueMc/AutoTranslation/blob/master/CHANGELOG_en.md)
