package com.github.lehjr.mparecipecreator.block;

import com.github.lehjr.modularpowerarmor.basemod.ModularPowerArmor;
import com.github.lehjr.mparecipecreator.basemod.Constants;
import com.github.lehjr.mparecipecreator.basemod.MPA_RecipeCreator;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author lehjr
 */
public class RecipeWorkbench extends Block {
    public static final String translationKey = new StringBuilder(Constants.MODID).append(".").append("tinkerTable").toString();

    public RecipeWorkbench(ResourceLocation regName) {
        super(Material.IRON);
        setRegistryName(regName);
        setTranslationKey(translationKey);
        setHardness(1.5F);
        setResistance(1000.0F);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(MPA_RecipeCreator.getInstance().creativeTab);
        setSoundType(SoundType.METAL);
        setLightOpacity(0);
        setLightLevel(0.4f);
        setTickRandomly(false);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (playerIn.isSneaking()) {
            return false;
        }
        if (!worldIn.isRemote) {
            playerIn.openGui(MPA_RecipeCreator.getInstance(), 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
}