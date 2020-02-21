//package com.github.lehjr.mparecipecreator.network.packets;
//
//import com.github.lehjr.mparecipecreator.network.MPARC_Packets;
//import com.google.common.collect.Lists;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonParser;
//import net.minecraft.entity.player.ServerPlayerEntity;
//import net.minecraft.network.PacketBuffer;
//import net.minecraft.resources.IResourcePack;
//import net.minecraft.resources.ResourcePackInfo;
//import net.minecraft.resources.ResourcePackList;
//import net.minecraft.resources.ResourcePackType;
//import net.minecraft.server.MinecraftServer;
//import net.minecraftforge.fml.network.NetworkEvent;
//import net.minecraftforge.fml.packs.ModFileResourcePack;
//import org.apache.commons.io.IOUtils;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Path;
//import java.util.List;
//import java.util.Set;
//import java.util.function.Supplier;
//
//public class ConditionsRequestPacket {
//    public ConditionsRequestPacket() {
//    }
//
//    public static void encode(ConditionsRequestPacket msg, PacketBuffer packetBuffer) {
//    }
//
//    public static ConditionsRequestPacket decode(PacketBuffer packetBuffer) {
//        return new ConditionsRequestPacket();
//    }
//
//    public static void handle(ConditionsRequestPacket message, Supplier<NetworkEvent.Context> ctx) {
//        ctx.get().enqueueWork(() -> {
//            final ServerPlayerEntity player = ctx.get().getSender();
//            MinecraftServer server = player.world.getServer();
//            ResourcePackList<ResourcePackInfo> resourcepacklist = server.getResourcePacks();
//            List<ResourcePackInfo> list = Lists.newArrayList(resourcepacklist.getEnabledPacks());
//
//            for (ResourcePackInfo info : list) {
//                String name = info.getName();
//                if (name.equals("mod:modularpowerarmor")) {
//                    IResourcePack pack = info.getResourcePack();
//                    Set<String> set = pack.getResourceNamespaces(ResourcePackType.SERVER_DATA);
//                    if (set.contains("modularpowerarmor") && pack instanceof ModFileResourcePack) {
//                        try {
//                            ModFileResourcePack modFilePack = (ModFileResourcePack) pack;
//                            Path absolutePath = modFilePack.getModFile().getFilePath().toAbsolutePath();
//                            InputStream stream = absolutePath.getClass().getResourceAsStream("/data/modularpowerarmor/recipes/_factories.json");
//                            final String jsonString = IOUtils.toString(stream, StandardCharsets.UTF_8);
//                            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
//                            JsonParser jp = new JsonParser();
//                            JsonElement je = jp.parse(jsonString);
//                            String prettyJsonString = gson.toJson(je);
//                            MPARC_Packets.CHANNEL_INSTANCE.reply(new ConditionsResponsePacket(prettyJsonString, server.getDataDirectory().getAbsolutePath()), ctx.get());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    break;
//                }
//            }
//        });
//        ctx.get().setPacketHandled(true);
//    }
//}
