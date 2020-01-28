package com.github.lehjr.mparecipecreator.basemod;

import com.github.lehjr.mparecipecreator.block.RecipeWorkbench;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author lehjr
 */
public enum ModObjects {
    INSTANCE;

    /**
     * Blocks -------------------------------------------------------------------------------------
     */
    @GameRegistry.ObjectHolder(Constants.RECIPE_WORKBENCH__REGNAME)
    public static final RecipeWorkbench recipeWorkBench = null;
}
