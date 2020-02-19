package com.github.lehjr.mparecipecreator.network;

import com.github.lehjr.mparecipecreator.basemod.Constants;
import com.github.lehjr.mparecipecreator.network.packets.ConditionsRequestPacket;
import com.github.lehjr.mparecipecreator.network.packets.ConditionsResponsePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class MPARC_Packets {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL_INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Constants.MOD_ID, "data"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerPackets() {
        int i = 0;

        CHANNEL_INSTANCE.registerMessage(
                i++,
                ConditionsRequestPacket.class,
                ConditionsRequestPacket::encode,
                ConditionsRequestPacket::decode,
                ConditionsRequestPacket::handle);

        CHANNEL_INSTANCE.registerMessage(
                i++,
                ConditionsResponsePacket.class,
                ConditionsResponsePacket::encode,
                ConditionsResponsePacket::decode,
                ConditionsResponsePacket::handle);
    }
}