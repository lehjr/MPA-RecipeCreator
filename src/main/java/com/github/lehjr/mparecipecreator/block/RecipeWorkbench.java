package com.github.lehjr.mparecipecreator.block;

import com.github.lehjr.mparecipecreator.client.gui.MPARCContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

/**
 * @author lehjr
 */
public class RecipeWorkbench extends Block {
    public RecipeWorkbench(String regName) {
        this(new ResourceLocation(regName));
    }

    public RecipeWorkbench(ResourceLocation regName) {
        super(Block.Properties.create(Material.WOOD)
                .hardnessAndResistance(1.5F, 1000.0F)
                .sound(SoundType.METAL)
                .variableOpacity()
                .lightValue(1));
        setRegistryName(regName);
        setDefaultState(this.stateContainer.getBaseState());
    }

    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        player.openContainer(state.getContainer(worldIn, pos));
        return true;
    }

    private static final ITextComponent title = new TranslationTextComponent("container.crafting");
    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
        return new SimpleNamedContainerProvider((windowID, playerInventory, playerEntity) ->
                new MPARCContainer(windowID, playerInventory, IWorldPosCallable.of(worldIn, pos)), title);
    }
}