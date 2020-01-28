package com.github.lehjr.mparecipecreator.basemod;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author lehjr
 */
public class CreativeTab extends CreativeTabs {
    public CreativeTab() {
        super("mparecipecreator");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModObjects.INSTANCE.recipeWorkBench);
    }
}