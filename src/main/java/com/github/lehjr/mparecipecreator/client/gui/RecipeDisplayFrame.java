package com.github.lehjr.mparecipecreator.client.gui;

import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableFrame;
import com.github.lehjr.mpalib.math.Colour;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RecipeDisplayFrame extends ScrollableFrame {
    Map<String, String> recipeKeys;
    String[] recipeGrid;
    String output;

    JSONObject jsonObject;

    public RecipeDisplayFrame(Point2D topleft, Point2D bottomright, Colour backgroundColour, Colour borderColour) {
        super(topleft, bottomright, backgroundColour, borderColour);

        reset();
    }

    public void reset() {
        recipeKeys = new HashMap<>();
        recipeGrid =  new String[9];
        output = "";
        jsonObject = new JSONObject();


    }

    /*
    required fields:

    conditions:
        type

    type: "modularpowerarmor:mpa_shaped"

    pattern:
        [list]

    key:
        string : value
        string : value

    result:
        value










    {
  "conditions": [
    {
      "type": "modularpowerarmor:vanilla_recipes_enabled"
    }
  ],
  "type": "modularpowerarmor:mpa_shaped",
  "pattern": [
    " W ",
    "WI ",
    " IW"
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
    "item": "modularpowerarmor:powerfist",
    "count: 1
  }
}



     */






}