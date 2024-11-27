# 装备标准 `Equipment Standard`

大家好！我在工作之余创建了这个模组，但现在没有时间精力维护了，所以停止更新！

代码包含了全面文档和注释，使任何人都可以轻松上手。


如果你对 Minecraft 模组充满热情，并且有 Java 的经验，我鼓励你联系我！

---

## Seeking New Maintainer

Hey everyone!  In my spare time, I created this mod, and I'm really proud of it. Unfortunately, my current workload doesn't allow me to dedicate the time it needs. That's why I'm reaching out to see if anyone would be interested in taking over as the maintainer!

The codebase is written in Java and  I've included comprehensive documentation and comments to make it as easy as possible for someone to pick up. 

If you're passionate about Minecraft modding and have experience with Java, I encourage you to reach out!

*This description was written by Gemma, an AI assistant.* 

---

[English](README_EN.md)

受 [Tiered](https://github.com/Draylar/tiered) 启发，可通过数据包定制装备属性，但本模组可以使用随机模式，也可使用类似 Tiered 的固定值模式。

## 数据格式

### 物品标签 (Tags)

#### 有耐久的
* `#equipment_standard:damageable`       -- create("damageable", item -> item.getMaxDamage() > 0);
#### 武器
* `#equipment_standard:weapons`          -- create("weapons", item -> item instanceof SwordItem || item instanceof TridentItem || item instanceof ProjectileWeaponItem);
* `#equipment_standard:melee_weapons`    -- create("melee_weapons", item -> item instanceof SwordItem || item instanceof TridentItem);
* `#equipment_standard:ranged_weapons`   -- create("ranged_weapons", item -> item instanceof ProjectileWeaponItem || item instanceof TridentItem);
* `#equipment_standard:swords`           -- create("swords", item -> item instanceof SwordItem);
* `#equipment_standard:tridents`         -- create("tridents", item -> item instanceof TridentItem);
* `#equipment_standard:projectiles`      -- create("projectiles", item -> item instanceof ProjectileWeaponItem);
* `#equipment_standard:bows`             -- create("bows", item -> item instanceof BowItem);
* `#equipment_standard:crossbows`        -- create("crossbows", item -> item instanceof CrossbowItem);
#### 工具
* `#equipment_standard:diggers`          -- create("diggers", item -> item instanceof DiggerItem);
* `#equipment_standard:axes`             -- create("axes", item -> item instanceof AxeItem);
* `#equipment_standard:hoes`             -- create("hoes", item -> item instanceof HoeItem);
* `#equipment_standard:pickaxes`         -- create("pickaxes", item -> item instanceof PickaxeItem);
* `#equipment_standard:shovels`          -- create("shovels", item -> item instanceof ShovelItem);
* `#equipment_standard:fishing_rods`     -- create("fishing_rods", item -> item instanceof FishingRodItem);
#### 护甲
* `#equipment_standard:armor`            -- create("armor", item -> item instanceof ArmorItem || item instanceof ElytraItem);
* `#equipment_standard:helmets`          -- create("helmets", item -> item instanceof ArmorItem armor && armor.getEquipmentSlot() == EquipmentSlot.HEAD);
* `#equipment_standard:chestplates`      -- create("chestplates", item -> item instanceof ArmorItem armor && armor.getEquipmentSlot() == EquipmentSlot.CHEST);
* `#equipment_standard:leggings`         -- create("leggings", item -> item instanceof ArmorItem armor && armor.getEquipmentSlot() == EquipmentSlot.LEGS);
* `#equipment_standard:boots`            -- create("boots", item -> item instanceof ArmorItem armor && armor.getEquipmentSlot() == EquipmentSlot.FEET);
#### 鞘翅
* `#equipment_standard:elytra`           -- create("elytra", item -> item instanceof ElytraItem);
#### 盾牌
* `#equipment_standard:shields`          -- create("shields", item -> item instanceof ShieldItem);
#### 马铠
* `#equipment_standard:horse_armor`      -- create("horse_armor", item -> item instanceof HorseArmorItem);
#### 魔法物品(可以自行定义，目前仅内置了 spell engine 的一些物品)
* `#equipment_standard:magic`            -- 默认是 `#equipment_standard:magic/armor` 和 `#equipment_standard:magic/weapons` 的总和，可以不用改这个
* `#equipment_standard:magic/armor`      -- 根据实际安装的魔法类模组添加模组的护甲到这个标签下
* `#equipment_standard:magic/weapons`    -- 根据实际安装的魔法类模组添加模组的武器到这个标签下

### 属性模板
需位于 `数据包名(.zip)?/data/equipment_standard/template` 下，可以使用文件夹

数据格式 `example.json`
```json5
// 本示例中添加了注释，但为防止 minecraft 不支持 json5 格式，不支持注释，请实际编写时删除注释。
// 说实话我没验证过，你可以试试，但加上大量注释总归会增加数据包大小，还是删除掉吧。
{
  // 通过本列表验证的物品将会应用本模板
  // 必选
  // minecraft:elytra 这种格式代表物品 ID
  // #equipment_standard:elytra #开头代表物品标签
  // @equipment_standard 代表物品所属名命空间（各 Mod 定义）
  "verifiers": [
    "#equipment_standard:elytra",
    "#equipment_standard:horse_armor"
  ],
  // 通过本列表验证的物品将不会会应用本模板
  // 可选
  // minecraft:elytra 这种格式代表物品 ID
  // #equipment_standard:elytra #开头代表物品标签
  // @equipment_standard 代表物品所属名命空间（各 Mod 定义）
  // #equipment_standard:magic 是自定义的标签，fabric 和 forge 的不同的魔法物品可以设置此标签防止出现物理攻击属性
  "excludes": [
    "#equipment_standard:magic"
  ],
  // 属性生效的格子
  // 可选，这里设置代表是公共的，本模板所有 attributes 均是这个格子的，也可在 attributes 中单独设置
  // ANY: 任意格子
  // DEFAULT: 代表装备物品的默认格子，如原版游戏未定义，则默认格子是主手，一般用这个就行
  // MAINHAND: 代表主手
  // OFFHAND: 代表副手
  // HEAD: 头部
  // CHEST: 胸部
  // LEGS: 腿
  // FEET: 脚
  "slots": ["DEFAULT"],
  // 属性列表，可以有多个，本示例有两个： armor 和 armor_toughness
  "attributes": [
    {
      // 属性类型，属可以在游戏里通过 `/attribute` 命令查看，minecraft 可以省略
      // 必选
      "type": "generic.armor",
      // 属性生效的格子
      // 同公共属性的 slots，此处会覆盖公共属性
      "slots": ["DEFAULT"],
      // 随机命中这个属性的几率，最大 1
      // 必选
      "chance": 1,
      // 如果属性值时范围内随机时，每次随机的步进，比如 0-3 随机，将只会随机出 0,0.5, 1, 0.5, 2, 2.5
      // 可选，这里设置时是公共的，也可在 modifiers 中单独设置
      "step": 0.5,
      // 是否合并原版属性，比如胸甲默认有护甲值，随机后的属性将会跟原版属性合并为一条
      // 可选，默认为 false，若无特殊需求可以不用理会该属性
      "merge": false,
      "modifiers": [
        {
          // 本条权重, 不是百分比，最终概率 = 权重 / 本属性所有 modifier 权重值之和
          // 必选
          "weights": 30,
          // 权重加成
          // 可选
          "bonus": {
            // 每点幸运加成
            "lucky": -1,
            // 每点熟练度加成
            "proficiency": -0.01
          },
          // 加成类型
          // 必选
          // ADDITION           代表增加固定值
          // MULTIPLY_BASE      代表增加实体基础属性值的倍率
          // MULTIPLY_TOTAL     代表增加实体总属性值的倍率
          // MULTIPLY_ADDITION  代表增加物品基础属性值的倍率，如果本物品无此属性，则不增加
          "operation": "ADDITION",
          // 随机范围最小值，跟 amount 互斥
          "min": -2,
          // 随机范围最大值，跟 amount 互斥
          "max": 0
        },
        {
          "weights": 40,
          "operation": "ADDITION",
          // 固定值
          // 跟 min 和 max 互斥，若均设置了值，以此值为准
          "amount": 0,
          "bonus": {
            "lucky": -0.1,
            "proficiency": -0.002
          }
        },
        {
          "weights": 50,
          "operation": "ADDITION",
          "min": 0,
          "max": 3,
          "bonus": {
            "lucky": 0.1,
            "proficiency": 0.002
          }
        },
        {
          "weights": 1,
          "operation": "ADDITION",
          "min": 3,
          "max": 5,
          "bonus": {
            "lucky": 0.5,
            "proficiency": 0.01
          }
        }
      ]
    },
    {
      "type": "generic.armor_toughness",
      "chance": 1,
      "modifiers": [
        {
          "weights": 40,
          "operation": "ADDITION",
          "amount": 1
        }
      ]
    }
  ]
}
```

### 属性分值
需位于 `数据包名(.zip)?/data/equipment_standard/attr_score` 下，可以使用文件夹

所有属性均有默认分值，此处没有 `MULTIPLY_ADDITION`， 因为已计算为 `ADDITION`

`ADDITION`: 100分

`MULTIPLY_BASE`: 500分

`MULTIPLY_TOTAL`: 550分

数据格式 `example.json`
```json5
{
  // 属性类型 可以在游戏里通过 `/attribute` 命令查看
  "type": "equipment_standard:generic.crit_chance",
  // 不同的 operation 的分数
  // 可仅覆盖单个 operation 的默认值
  "scores": [
    {
      // 属性修改器类型
      // 必选
      "operation": "ADDITION",
      // 分值
      // 必选
      "score": 1000
    },
    {
      "operation": "MULTIPLY_BASE",
      "score": 1000
    },
    {
      "operation": "MULTIPLY_TOTAL",
      "score": 1000
    }
  ]
}
```

### 装备品阶
需位于 `数据包名(.zip)?/data/equipment_standard/rarity` 下，可以使用文件夹

已内置默认品阶，若无特殊需求，可以不必配置此项，仅需配置语言文件 `item.es.rarity.(Rarity)` 的翻译
```java
new ItemRarity.Rarity("Defective", Integer.MIN_VALUE, Component.translatable("item.es.rarity.defective"), ChatFormatting.GRAY);
new ItemRarity.Rarity("Common", -20, Component.empty(), ChatFormatting.WHITE);
new ItemRarity.Rarity("Uncommon", 100, Component.translatable("item.es.rarity.uncommon"), ChatFormatting.GREEN);
new ItemRarity.Rarity("Rare", 300, Component.translatable("item.es.rarity.rare"), ChatFormatting.BLUE);
new ItemRarity.Rarity("Epic", 600, Component.translatable("item.es.rarity.epic"), ChatFormatting.LIGHT_PURPLE);
new ItemRarity.Rarity("Legendary", 900, Component.translatable("item.es.rarity.legendary"), ChatFormatting.GOLD);
new ItemRarity.Rarity("Unique", 1200, Component.translatable("item.es.rarity.unique"), ChatFormatting.RED);
```

数据格式 `example.json`
```json5
{
  // 通过本列表验证的物品将会应用本品阶列表
  // 必选
  // minecraft:elytra 这种格式代表物品 ID
  // #equipment_standard:elytra #开头代表物品标签
  // @equipment_standard 代表物品所属名命空间（各 Mod 定义）
  "verifiers": [
    "#equipment_standard:horse_armor"
  ],
  // 通过本列表验证的物品将不会会应用本品阶列表
  // 可选
  // minecraft:elytra 这种格式代表物品 ID
  // #equipment_standard:elytra #开头代表物品标签
  // @equipment_standard 代表物品所属名命空间（各 Mod 定义）
  "excludes": [
    "#equipment_standard:magic"
  ],
  "rarities": [
    {
      // 品阶名称，将会存到物品 NBT 中，可以自行配置与 传说工具提示 Legendary Tooltips 的兼容
      // 必选
      "name": "Customize",
      // 此品阶的最低装备分值
      // 必选
      "score": 1000,
      // 物品名称的前缀
      // 可选，建议用 translatable
      // 参见 https://minecraft.fandom.com/zh/wiki/%E6%95%99%E7%A8%8B/%E5%8E%9F%E5%A7%8BJSON%E6%96%87%E6%9C%AC
      "prefix": {
        "translate": "item.es.rarity.customize"
      },
      // 物品名称的格式化
      // 详见 ChatFormatting
      "formatting": [
        "RED"
      ]
    }
  ]
}
```
