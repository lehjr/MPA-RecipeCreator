package com.github.lehjr.mparecipecreator.network.packets;

import com.github.lehjr.mparecipecreator.basemod.DataPackWriter;
import com.github.lehjr.mparecipecreator.basemod.config.Config;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class RecipeWriterPacket {
    private final String fileName;
    private final boolean isSinglePlayer;
    private final String recipe;

    public RecipeWriterPacket(boolean isSinglePlayer, String recipe, String fileName) {
        this.isSinglePlayer = isSinglePlayer;
        this.recipe = recipe;
        this.fileName = fileName;
    }

    public static void encode(RecipeWriterPacket msg, PacketBuffer packetBuffer) {
        packetBuffer.writeBoolean(msg.isSinglePlayer);

        int recipeSize = msg.recipe.length() + 32;
        packetBuffer.writeInt(recipeSize);
        packetBuffer.writeString(msg.recipe, recipeSize);

        packetBuffer.writeInt(msg.fileName.length() + 32);
        packetBuffer.writeString(msg.fileName);
    }

    public static RecipeWriterPacket decode(PacketBuffer packetBuffer) {
        return new RecipeWriterPacket(
                packetBuffer.readBoolean(),
                packetBuffer.readString(packetBuffer.readInt()),
                packetBuffer.readString(packetBuffer.readInt()));
    }

    public static void handle(RecipeWriterPacket message, Supplier<NetworkEvent.Context> ctx) {
        final ServerPlayerEntity player = ctx.get().getSender();
        ctx.get().enqueueWork(() -> {
            MinecraftServer server = player.getServer();

            URI gameFolderURI = server.getDataDirectory().toURI().normalize();
            Path datapackDir = null;

            // single player and player obviously owns the server
            if (message.isSinglePlayer) {
                datapackDir = Paths.get(gameFolderURI)
                        .resolve("saves")
                        .resolve(server.getFolderName())
                        .toAbsolutePath();

                // multiplayer and player owns server or has permission to create recipes
            } else if (server.isServerOwner(player.getGameProfile()) ||
                    (Config.allowOppedPlayersToCreateOnServer() &&
                            server.getPermissionLevel(player.getGameProfile()) >= Config.getOpLevelNeeded())) {

                if (server.isDedicatedServer()) {
                    System.out.println("dedicated server detected");
                    datapackDir = Paths.get(gameFolderURI)
                            .resolve(server.getFolderName())
                            .toAbsolutePath();

                } else {
                    System.out.println("multiplayer without dedicated server detected");

                    datapackDir = Paths.get(gameFolderURI)
                            .resolve("saves")
                            .resolve(server.getFolderName())
                            .toAbsolutePath();
                }
            } else {
                if (Config.allowOppedPlayersToCreateOnServer()) {
                    player.sendMessage(new StringTextComponent("You do not have permission to create recipes :P" +
                            "\nRequiredLevel: " + Config.getOpLevelNeeded() +
                            "\nYourLevel: " + server.getPermissionLevel(player.getGameProfile())));
                } else {
                    player.sendMessage(new StringTextComponent("Serve admin has disabled creating recipes on this server :P"));
                }
            }

            if(datapackDir != null && !message.recipe.isEmpty() && !message.fileName.isEmpty()) {
                DataPackWriter.INSTANCE.setDataFolder(datapackDir.toString());


                System.out.println(DataPackWriter.INSTANCE.packMetaFile.getAbsolutePath());


                if (!DataPackWriter.INSTANCE.packMetaFile.exists()) {
                    DataPackWriter.INSTANCE.fileWriter(DataPackWriter.INSTANCE.packMetaFile, DataPackWriter.INSTANCE.getPackMCMeta(), false);
                }

                File recipeFile = new File(DataPackWriter.INSTANCE.actualTargetDir.toAbsolutePath().toString(), message.fileName);
                DataPackWriter.INSTANCE.fileWriter(recipeFile, message.recipe, Config.overwriteRecipes());

                if (recipeFile.exists()) {
                    player.sendMessage(new StringTextComponent("Server reloading data :P"));

                    server.reload();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
