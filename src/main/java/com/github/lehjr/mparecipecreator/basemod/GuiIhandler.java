//package com.github.lehjr.mparecipecreator.basemod;
//
//import com.github.lehjr.mparecipecreator.client.gui.MPARCGui;
//import com.github.lehjr.mparecipecreator.client.gui.MTRMContainer;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.world.World;
//import net.minecraftforge.fml.common.network.IGuiHandler;
//
///**
// * @author lehjr
// */
//public class GuiIhandler implements IGuiHandler {
//    @Override
//    public Object getServerGuiElement(int ID, PlayerEntity player, World world, int x, int y, int z) {
//        return new MTRMContainer(player.inventory);
//    }
//
//    @Override
//    public Object getClientGuiElement(int ID, PlayerEntity player, World world, int x, int y, int z) {
//        return new MPARCGui(new MTRMContainer(player.inventory));
//    }
//}