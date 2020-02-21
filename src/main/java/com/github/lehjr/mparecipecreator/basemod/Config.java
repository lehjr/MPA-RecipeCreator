package com.github.lehjr.mparecipecreator.basemod;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {
    public static final ClientConfig CLIENT_CONFIG;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static {
        {
            final Pair<ClientConfig, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
            CLIENT_SPEC = clientSpecPair.getRight();
            CLIENT_CONFIG = clientSpecPair.getLeft();
        }
    }

    static File setupConfigFile(String fileName) {
        Path configFile = Paths.get("config/lehjr").resolve(Constants.MOD_ID).resolve(fileName);
        File cfgFile = configFile.toFile();
        try {
            if (!cfgFile.getParentFile().exists())
                cfgFile.getParentFile().mkdirs();
            if (!cfgFile.exists())
                cfgFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cfgFile;
    }

    public static ForgeConfigSpec.ConfigValue<List<String>> conditionsList;

    public static class ClientConfig {
        ClientConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("General settings").push("General");

            conditionsList = builder
                    .comment("The colors for ores used when rendering their result bounding box.\n" +
                            "Each entry must be a key-value pair separated by a `=`, with the.\n" +
                            "key being the ore dictionary name and the value being the hexadecimal\n" +
                            "RGB value of the color.")
                    .define("conditionsList", Arrays.asList(
                            "enderio_recipes_enabled",
                            "gregtech_recipes_enabled",
                            "ic2_recipes_enabled",
                            "ic2_classic_recipes_enabled",
                            "tech_reborn_recipes_enabled",
                            "thermal_expansion_recipes_enabled",
                            "vanilla_recipes_enabled"));
        }
    }

    public static List<String> getConditions() {
        if (conditionsList != null) {
            return conditionsList.get();
        } else {
            return new ArrayList<>();
        }
    }
}