# Equipment Standard

[中文](README.md)

Inspired by [Tiered](https://github.com/Draylar/tiered), equipment attributes can be customized through datapack, but this module can use random mode or fixed value mode similar to Tiered.

## Data Format

### Property Template
It needs to be located under `datapack name(.zip)?/data/equipment_standard/template`, you can use the folder.

example.json
```json5
// Comments are added in this example, but to prevent minecraft from not supporting json5 format and comments, please delete the comments when actually writing.
// To be honest, I haven't verified it. You can try it, but adding a lot of comments will always increase the data packet size, so delete it.
{
  // Items verified through this list will apply this template
  // required
  // minecraft:elytra This format represents the item ID
  // #equipment_standard:elytra #The beginning represents the item label
  // @equipment_standard represents the namespace to which the item belongs (each Mod definition)
  "verifiers": [
    "#equipment_standard:elytra",
    "#equipment_standard:horse_armor"
  ],
  // Items verified through this list will not apply this template
  // optional
  // minecraft:elytra This format represents the item ID
  // #equipment_standard:elytra #The beginning represents the item label
  // @equipment_standard represents the namespace to which the item belongs (each Mod definition)
  // #equipment_standard:magic is a custom tag. Different magic items of fabric and forge can set this tag to prevent physical attack attributes.
  "excludes": [
    "#equipment_standard:magic"
  ],
  // The slot where the attribute takes effect
  // optional，The settings here represent public. All attributes of this template belong to this slot. They can also be set individually in attributes.
  // ANY: Any slot
  // DEFAULT: Represents the default slot of equipment items. If it is not defined in the original game, the default slot is the main hand. Generally, this is enough.
  // MAINHAND
  // OFFHAND
  // HEAD
  // CHEST
  // LEGS
  // FEET
  "slots": ["DEFAULT"],
  //Attribute list, there can be multiple, this example has two: armor and armor_toughness
  "attributes": [
    {
      // Attribute type, which can be viewed through the `/attribute` command in the game. `minecraft:` can be omitted.
      // required
      "type": "generic.armor",
      // The slot where the attribute takes effect
      // Slots with the same public properties, the public properties will be overridden here.
      "slots": ["DEFAULT"],
      // The chance of randomly hitting this attribute, maximum 1
      // required
      "chance": 1,
      // If the attribute value is random within the range, each random step, such as 0-3 random, will only randomly produce 0, 0.5, 1, 0.5, 2, 2.5
      // optional, it is public when set here, or can be set separately in modifiers
      "step": 0.5,
      // Whether to merge the original attributes. For example, the breastplate has armor value by default. The randomized attributes will be merged into one with the original attributes.
      // optional, the default is false, you can ignore this attribute if you have no special needs
      "merge": false,
      "modifiers": [
        {
          // The weight of this article is not a percentage. The final probability = weight / the sum of the weight values of all modifiers of this attribute
          // required
          "weights": 30,
          // Weight bonus
          // optional
          "bonus": {
            // Lucky bonus per point
            "lucky": -1,
            // Proficiency bonus per point
            "proficiency": -0.01
          },
          // Modifier type
          // required
          // ADDITION           represents adding a fixed value
          // MULTIPLY_BASE      represents the multiplier for increasing the entity's basic attribute value
          // MULTIPLY_TOTAL     represents the multiplier for increasing the total attribute value of the entity
          // MULTIPLY_ADDITION  represents the multiplier for increasing the basic attribute value of the item. If the item does not have this attribute, it will not increase.
          "operation": "ADDITION",
          // The minimum value of the random range, mutually exclusive with amount
          "min": -2,
          // The maximum value of the random range, mutually exclusive with amount
          "max": 0
        },
        {
          "weights": 40,
          "operation": "ADDITION",
          // Fixed value
          // Mutually exclusive with min and max. If both values are set, this value shall prevail.
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

### Attribute Score
It needs to be located under `datapack name(.zip)?/data/equipment_standard/template`, you can use the folder.

All attributes have default scores, there is no `MULTIPLY_ADDITION` here as it is already calculated as `ADDITION`

`ADDITION`: 100

`MULTIPLY_BASE`: 500

`MULTIPLY_TOTAL`: 550

example.json
```json5
{
  // Attribute types can be viewed in the game through the `/attribute` command
  "type": "equipment_standard:generic.crit_chance",
  // Scores for different operations
  // Can only override the default value of a single operation
  "scores": [
    {
      // Property modifier type
      // required
      "operation": "ADDITION",
      // required
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

### Equipment Rarity
It needs to be located under `datapack name(.zip)?/data/equipment_standard/template`, you can use the folder.

The default grade has been built in. If there are no special requirements, you do not need to configure this item. You only need to configure the translation of the language file `item.es.rarity.(Rarity)`
```java
new ItemRarity.Rarity("Defective", Integer.MIN_VALUE, Component.translatable("item.es.rarity.defective"), ChatFormatting.GRAY);
new ItemRarity.Rarity("Common", -20, Component.empty(), ChatFormatting.WHITE);
new ItemRarity.Rarity("Uncommon", 100, Component.translatable("item.es.rarity.uncommon"), ChatFormatting.GREEN);
new ItemRarity.Rarity("Rare", 300, Component.translatable("item.es.rarity.rare"), ChatFormatting.BLUE);
new ItemRarity.Rarity("Epic", 600, Component.translatable("item.es.rarity.epic"), ChatFormatting.LIGHT_PURPLE);
new ItemRarity.Rarity("Legendary", 900, Component.translatable("item.es.rarity.legendary"), ChatFormatting.GOLD);
new ItemRarity.Rarity("Unique", 1200, Component.translatable("item.es.rarity.unique"), ChatFormatting.RED);
```

example.json
```json5
{
  // Items verified through this list will have this rarity list applied to them
  // required
  // minecraft:elytra This format represents the item ID
  // #equipment_standard:elytra #The beginning represents the item label
  // @equipment_standard represents the namespace to which the item belongs (each Mod definition)
  "verifiers": [
    "#equipment_standard:horse_armor"
  ],
  // Items verified through this list will not have this rarity list applied to them
  // optional
  // minecraft:elytra This format represents the item ID
  // #equipment_standard:elytra #The beginning represents the item label
  // @equipment_standard represents the namespace to which the item belongs (each Mod definition)
  "excludes": [
    "#equipment_standard:magic"
  ],
  "rarities": [
    {
      // Rarity name，It will be saved in the item NBT and can be configured to be compatible with Legendary Tooltips.
      // required
      "name": "Customize",
      // Minimum equipment score for this rarity
      // required
      "score": 1000,
      // Item name prefix
      // optional，It is recommended to use `translatable`
      "prefix": {
        "translate": "item.es.rarity.customize"
      },
      // Formatting of item names
      // See ChatFormatting
      "formatting": [
        "RED"
      ]
    }
  ]
}
```