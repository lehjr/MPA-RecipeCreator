package com.github.lehjr.mparecipecreator.network.packets;

import com.github.lehjr.mparecipecreator.client.gui.MPARCGui;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ConditionsResponsePacket {
    String conditions;

    public ConditionsResponsePacket() {
    }

    public ConditionsResponsePacket(String conditionsIn) {
        this.conditions = conditionsIn;
    }

    public static void encode(ConditionsResponsePacket msg, PacketBuffer packetBuffer) {
        packetBuffer.writeString(msg.conditions);
    }

    public static ConditionsResponsePacket decode(PacketBuffer packetBuffer) {
        return new ConditionsResponsePacket(packetBuffer.readString());
    }

    public static void handle(ConditionsResponsePacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
             if (ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_CLIENT)) {
                String jsonString = message.conditions;
                if (Minecraft.getInstance().currentScreen instanceof MPARCGui) {
                    if (jsonString != null && !jsonString.isEmpty()) {
                        JsonParser jp = new JsonParser();
                        ((MPARCGui) Minecraft.getInstance().currentScreen).setConditionsJson(jp.parse(jsonString).getAsJsonObject());
                    }
                }
             }
        });
        ctx.get().setPacketHandled(true);
    }
}