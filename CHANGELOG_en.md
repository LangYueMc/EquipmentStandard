# V1.3.1

2023-11-17

## Fixed

1. Fixed the crash in 1.20.1 caused by calling the new method in 1.20.2

# V1.3.0

2023-11-17

## Changed
1. Added a reforge table to reforge equipment, which requires reforge scrolls. Please check the recipe for details;
2. Adjust the attribute scores again. Please ask someone to design a set of attribute values and scores.

# V1.2.0

2023-11-15

## Changed

1. Use `mixinextras` to reconstruct mixin, which is theoretically compatible with more other mods;
2. Fine-tune the critical damage value and probability.
3. Compatible `AdditionalEntityAttributes`

   Critical damage and mining speed will use `AdditionalEntityAttributes` when `AdditionalEntityAttributes` is loaded;

# V1.1.6

2023-11-11

## Changed

1. Reconstruct the tag of `equipmentstandard` to `equipment_standard`;
2. Reconstruct the data packet reading, now only the data under `equipment_standard` will be read;
3. Add README.

# V1.1.5

2023-11-6

## Changed

1. Fine-tuned the probability of real_damage attribute.

## Fixed

1. Fixed the problem of startup failure on the server side due to registration of client events.

# V1.1.4

2023-11-5

## Fixed

1. Fixed the problem of real_damage being affected by armor. Now the real_damage attribute will have no CD and will not be reduced by armor.
2. Fixed incorrect minecraft version range in forge config file causing 1.20.1 launch crash.

# V1.1.3

2023-11-2

## Changed

1. Adjust some formatting.

# V1.1.2

2023-11-2

## Fixed

1. Fixed the issue that magic item tags did not take effect.

# V1.1.1

2023-11-2

## Fixed

1. Fixed the bug of synthesizing items caused by code errors.

# V1.1.0

2023-11-2

> 1.1.0 is incompatible with 1.0.0. Please restart the world and upgrade, otherwise the equipment properties will be abnormal.

## Changed

1. Reconstruct score calculation
2. Add step height attribute
3. Fabric: compatible with spell attribute

# V1.0.0

2023-10-31

## First Edition