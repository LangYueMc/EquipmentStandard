package me.langyue.equipmentstandard.world.inventory;

import com.mojang.datafixers.util.Pair;
import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.ModifierUtils;
import me.langyue.equipmentstandard.api.ProficiencyAccessor;
import me.langyue.equipmentstandard.world.item.ReforgeScroll;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ReforgeMenu extends AbstractContainerMenu {
    static final ResourceLocation EMPTY_SLOT_SCROLL = new ResourceLocation("item/empty_slot_smithing_template_armor_trim");
    static final ResourceLocation EMPTY_SLOT_EQUIPMENT = new ResourceLocation("item/empty_slot_sword");

    private final ContainerLevelAccess access;
    private final Container reforgeSlots = new SimpleContainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
            ReforgeMenu.this.slotsChanged(this);
        }
    };
    private final DataSlot bonus = DataSlot.standalone();

    private final DataSlot cost = DataSlot.standalone();

    private static final int COST = 10;

    public static ReforgeMenu create(int id, Inventory inventory) {
        return new ReforgeMenu(id, inventory);
    }

    private ReforgeMenu(int id, Inventory inventory) {
        this(id, inventory, ContainerLevelAccess.NULL);
    }

    public static ReforgeMenu create(int id, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        return new ReforgeMenu(id, inventory, containerLevelAccess);
    }

    private ReforgeMenu(int id, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(ESMenu.REFORGE_TABLE_MENU, id);
        this.access = containerLevelAccess;
        // 装备格子
        this.addSlot(new Slot(this.reforgeSlots, 0, 80, 20) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return ModifierUtils.canModifyAttribute(itemStack);
            }

            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_SLOT_EQUIPMENT);
            }
        });
        // 卷轴格子
        this.addSlot(new Slot(this.reforgeSlots, 1, 26, 50) {

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.getItem() instanceof ReforgeScroll;
            }

            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_SLOT_SCROLL);
            }
        });
        createInventorySlots(inventory);
        this.addDataSlot(this.bonus);
        this.addDataSlot(this.cost);
    }

    private void createInventorySlots(Inventory inventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void slotsChanged(Container container) {
        if (container == this.reforgeSlots) {
            ItemStack itemStack = container.getItem(0);
            if (itemStack.isEmpty()) {
                this.bonus.set(0);
                this.cost.set(0);
            } else {
                this.access.execute((level, blockPos) -> {
                    if (container.getItem(1).getItem() instanceof ReforgeScroll scroll) {
                        this.bonus.set(scroll.getBonus());
                        this.cost.set(scroll.getLevel() * COST);
                    } else {
                        this.bonus.set(0);
                        this.cost.set(0);
                    }
                    this.broadcastChanges();
                });
            }
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int i) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(i);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (i == 0) {
                if (!this.moveItemStackTo(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (i == 1) {
                if (!this.moveItemStackTo(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (itemStack2.getItem() instanceof ReforgeScroll) {
                if (!this.moveItemStackTo(itemStack2, 1, 2, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.slots.get(0).hasItem() && this.slots.get(0).mayPlace(itemStack2)) {
                ItemStack itemStack3 = itemStack2.copyWithCount(1);
                itemStack2.shrink(1);
                try {
                    this.slots.get(0).setByPlayer(itemStack3);
                } catch (Throwable e) {
                    this.slots.get(0).set(itemStack3);
                }
            } else {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                try {
                    slot.setByPlayer(ItemStack.EMPTY);
                } catch (Throwable e) {
                    slot.set(ItemStack.EMPTY);
                }
            } else {
                slot.setChanged();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemStack2);
        }
        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.reforgeSlots.stillValid(player);
    }

    @Override
    public boolean clickMenuButton(Player player, int i) {
        if (i != 0) {
            Util.logAndPauseIfInIde(player.getName() + " pressed invalid button id: " + i);
            return false;
        }
        ItemStack itemStack = this.reforgeSlots.getItem(0);
        ItemStack scroll = this.reforgeSlots.getItem(1);
        if ((scroll.isEmpty() || scroll.getCount() < 1) && !player.getAbilities().instabuild) {
            return false;
        }

        if (player.getAbilities().instabuild || !itemStack.isEmpty()) {
            this.access.execute((level, blockPos) -> {
                double luck = player.getAttributeValue(Attributes.LUCK);
                int proficiency = ((ProficiencyAccessor) player).getProficiency();
                if (!player.getAbilities().instabuild) {
                    scroll.shrink(1);
                    if (scroll.isEmpty()) {
                        this.reforgeSlots.setItem(1, ItemStack.EMPTY);
                    }
                }
                ModifierUtils.setItemStackAttribute(itemStack, proficiency + this.bonus.get(), luck, true);
                this.reforgeSlots.setChanged();
                if (!player.isCreative()) {
                    player.giveExperiencePoints(-this.cost.get());
                }
                if (EquipmentStandard.RANDOM.nextInt(10 * COST) < this.cost.get()) {
                    // 每级增加 10% 概率增加熟练度
                    ((ProficiencyAccessor) player).incrementProficiency();
                }
                this.slotsChanged(this.reforgeSlots);
                level.playSound(null, blockPos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0f, level.random.nextFloat() * 0.1f + 0.9f);
            });
            return true;
        }
        return false;
    }

    public boolean canReforge(Player player) {
        return this.cost.get() > 0 && player != null && (player.isCreative() || player.totalExperience >= this.cost.get());
    }

    public int getCost() {
        return this.cost.get();
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, blockPos) -> this.clearContainer(player, this.reforgeSlots));
    }
}
