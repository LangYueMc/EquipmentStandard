package me.langyue.equipmentstandard.api;

import com.google.common.collect.Sets;
import me.langyue.equipmentstandard.EquipmentStandard;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;

import java.util.Set;
import java.util.function.Predicate;

public class CustomTag {
    private static final Set<CustomTag> ALL = Sets.newHashSet();

    static {
        // 有耐久的
        create("damageable", item -> item.getMaxDamage() > 0);

        // 武器
        create("weapons", item -> item instanceof SwordItem || item instanceof TridentItem || item instanceof ProjectileWeaponItem);
        create("melee_weapons", item -> item instanceof SwordItem || item instanceof TridentItem);
        create("ranged_weapons", item -> item instanceof ProjectileWeaponItem || item instanceof TridentItem);
        create("swords", item -> item instanceof SwordItem);
        create("tridents", item -> item instanceof TridentItem);
        create("projectiles", item -> item instanceof ProjectileWeaponItem);
        create("bows", item -> item instanceof BowItem);
        create("crossbows", item -> item instanceof CrossbowItem);

        // 工具
        create("diggers", item -> item instanceof DiggerItem);
        create("axes", item -> item instanceof AxeItem);
        create("hoes", item -> item instanceof HoeItem);
        create("pickaxes", item -> item instanceof PickaxeItem);
        create("shovels", item -> item instanceof ShovelItem);
        create("fishing_rods", item -> item instanceof FishingRodItem);

        // 护甲
        create("armor", item -> item instanceof ArmorItem || item instanceof ElytraItem);
        create("helmets", item -> item instanceof ArmorItem armor && armor.getEquipmentSlot() == EquipmentSlot.HEAD);
        create("chestplates", item -> item instanceof ArmorItem armor && armor.getEquipmentSlot() == EquipmentSlot.CHEST);
        create("leggings", item -> item instanceof ArmorItem armor && armor.getEquipmentSlot() == EquipmentSlot.LEGS);
        create("boots", item -> item instanceof ArmorItem armor && armor.getEquipmentSlot() == EquipmentSlot.FEET);
        // 鞘翅
        create("elytra", item -> item instanceof ElytraItem);
        // 盾牌
        create("shields", item -> item instanceof ShieldItem);
        // 马铠
        create("horse_armor", item -> item instanceof HorseArmorItem);
    }

    private final TagKey<Item> tagKey;
    private final Predicate<Item> predicate;

    private CustomTag(TagKey<Item> tagKey, Predicate<Item> predicate) {
        this.tagKey = tagKey;
        this.predicate = predicate;
    }

    private static CustomTag create(String id, Predicate<Item> predicate) {
        CustomTag customTag = new CustomTag(TagKey.create(Registries.ITEM, EquipmentStandard.createResourceLocation(id)), predicate);
        ALL.add(customTag);
        return customTag;
    }

    public static Set<CustomTag> getAll() {
        return ALL;
    }

    public TagKey<Item> getTagKey() {
        return tagKey;
    }

    public boolean test(Item item) {
        return predicate.test(item);
    }
}
