# V1.2.0

2023-11-15

## 变更

1. 使用 mixinextras 重构 mixin，理论兼容更多其他 mod； 
2. 微调暴击爆伤数值和概率。 
3. 兼容 AdditionalEntityAttributes

    当 AdditionalEntityAttributes 加载时暴击伤害和挖掘速度将使用 AdditionalEntityAttributes 的；

# V1.1.6

2023-11-11

## 变更

1. 重构 `equipmentstandard` 的 tag 到 `equipment_standard`；
2. 重构数据包读取，现在只会读取 `equipment_standard` 下的数据了；
3. 添加 README。

# V1.1.5

2023-11-6

## 变更

1. 微调真伤属性概率。

## 修复

1. 修复 server 端由于注册了客户端事件导致启动失败的问题。

# V1.1.4

2023-11-5

## 修复

1. 修复真伤受护甲影响的问题，现在真伤属性将无 CD，不受护甲减伤。
2. 修复 forge 配置文件中的 minecraft 版本范围不正确，导致 1.20.1 启动崩溃。

# V1.1.3

2023-11-2

## 变更

1. 调整一些格式

# V1.1.2

2023-11-2

## 修复

1. 修复魔法物品标签未生效问题

# V1.1.1

2023-11-2

## 修复

1. 修复代码错误导致的合成刷物品bug

# V1.1.0

2023-11-2

> 1.1.0 与 1.0.0 不兼容，请重开世界后升级，否则装备属性将会异常.

## 新增

1. 重构评分计算
2. 添加台阶高度属性
3. fabric 兼容 spell attribute

# V1.0.0

2023-10-31

## 初版