package me.langyue.equipmentstandard.world.level.block;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import me.langyue.equipmentstandard.EquipmentStandard;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class ESBlocks {
    public static final DeferredRegister<Block> BLOCK = DeferredRegister.create(EquipmentStandard.MOD_ID, Registries.BLOCK);

    public static final RegistrySupplier<ReforgeTableBlock> REFORGE_TABLE_BLOCK = BLOCK.register(ReforgeTableBlock.getID(),
            () -> new ReforgeTableBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .requiresCorrectToolForDrops()
                            .strength(5.0F, 1200.0F)
            ));

    public static void register() {
        BLOCK.register();
    }
}
