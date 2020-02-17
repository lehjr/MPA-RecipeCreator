package com.github.lehjr.mparecipecreator.client.gui;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author lehjr
 */
public class RecipeGen {
    JsonObject recipe;
    ItemStack outputStack;

    public RecipeGen() {
        this(ItemStack.EMPTY);
    }

    public RecipeGen(@Nonnull ItemStack outputStackIn) {
        this.outputStack = outputStackIn;
        this.recipe = new JsonObject();
    }

    public void setConditions(Map<String, Boolean> conditions) {
        JsonObject conditionsJson = new JsonObject();

        for (String key : conditions.keySet()) {
            conditionsJson.addProperty(key, conditions.get(key));
        }
        recipe.add("conditions", conditionsJson);
    }

    // Set whether or not the recipe is shapeless
    public void setType(boolean shapeless) {
        recipe.addProperty("type", shapeless ? "tag" : "minecraft:crafting_shaped");
    }

    public void setMirrored(boolean mirrored) {
        recipe.addProperty("mirrored", mirrored);
    }








/*

  "recipes": {
    "minecraft:crafting_shaped": "com.github.lehjr.modularpowerarmor.recipe.RecipeFactory"
  },
  "conditions": {
    "enderio_recipes_enabled": "com.github.lehjr.modularpowerarmor.recipe.RecipeConditionFactory",
    "gregtech_recipes_enabled": "com.github.lehjr.modularpowerarmor.recipe.RecipeConditionFactory",
    "ic2_recipes_enabled": "com.github.lehjr.modularpowerarmor.recipe.RecipeConditionFactory",
    "ic2_classic_recipes_enabled": "com.github.lehjr.modularpowerarmor.recipe.RecipeConditionFactory",
    "tech_reborn_recipes_enabled": "com.github.lehjr.modularpowerarmor.recipe.RecipeConditionFactory",
    "thermal_expansion_recipes_enabled": "com.github.lehjr.modularpowerarmor.recipe.RecipeConditionFactory",
    "vanilla_recipes_enabled": "com.github.lehjr.modularpowerarmor.recipe.RecipeConditionFactory"
  }




    {
        "conditions": [
        {
            "type": "modularpowerarmor:vanilla_recipes_enabled"
        }
  ],
        "type": "modularpowerarmor:mpa_shaped",
            "pattern": [
        "III",
                "W W"
  ],
        "key": {
        "I": {
            "type": "forge:ore_dict",
                    "ore": "ingotIron"
        },
        "W": {
            "type": "forge:ore_dict",
                    "ore": "componentWiring"
        }
    },
        "result": {
        "item": "modularpowerarmor:powerarmor_head"
    }
    }

 */


}
