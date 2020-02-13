package com.github.lehjr.mparecipecreator.basemod;

import com.github.lehjr.mparecipecreator.block.RecipeWorkbench;
import com.github.lehjr.mparecipecreator.client.gui.MTRMContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ObjectHolder;

/**
 * @author lehjr
 */
public enum ModObjects {
    INSTANCE;

    /**
     * Blocks -------------------------------------------------------------------------------------
     */
    @ObjectHolder(Constants.RECIPE_WORKBENCH__REGNAME)
    public static final RecipeWorkbench recipeWorkBench = null;

    @ObjectHolder(Constants.RECIPE_WORKBENCH_TYPE__REG_NAME)
    public static final ContainerType<MTRMContainer> RECIPE_WORKBENCH_CONTAINER_TYPE = null;
}