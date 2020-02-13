package com.github.lehjr.mparecipecreator.basemod;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author lehjr
 */
public class CreativeTab extends ItemGroup {
    public CreativeTab() {
        super("mparecipecreator");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModObjects.INSTANCE.recipeWorkBench);
    }
}