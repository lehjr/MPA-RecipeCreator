package com.github.lehjr.mparecipecreator.basemod;

import com.github.lehjr.mparecipecreator.client.gui.MTRMContainer;
import com.github.lehjr.mparecipecreator.client.gui.MPARCGui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * @author lehjr
 */
public class GuiIhandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new MTRMContainer(player.inventory);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new MPARCGui(new MTRMContainer(player.inventory));
    }
}