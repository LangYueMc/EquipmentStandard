package me.langyue.equipmentstandard.world.level.block;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import me.langyue.equipmentstandard.EquipmentStandard;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class ESBlocks {
    public static final DeferredRegister<Block> BLOCK = DeferredRegister.create(EquipmentStandard.MOD_ID, Registries.BLOCK);

    public static final RegistrySupplier<ReforgeTableBlock> REFORGE_TABLE_BLOCK = BLOCK.register(ReforgeTableBlock.getID(), () -> new ReforgeTableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(5.0f, 1200.0f).sound(SoundType.ANVIL).pushReaction(PushReaction.BLOCK)));

    public static void register() {
        BLOCK.register();
    }
}
