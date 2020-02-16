package com.github.lehjr.mparecipecreator.client.gui;

import com.github.lehjr.modularpowerarmor.item.component.ItemComponent;
import com.github.lehjr.mpalib.client.gui.ContainerGui;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableRect;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mparecipecreator.basemod.Constants;
import com.github.lehjr.mparecipecreator.network.MPARC_Packets;
import com.github.lehjr.mparecipecreator.network.packets.ConditionsRequestPacket;
import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dries007
 */
public class MPARCGui extends ContainerGui<MTRMContainer> {
    protected DrawableRect backgroundRect;
    protected long creationTime;

    final int slotWidth = 18;
    final int spacer = 4;

    private final ExtInventoryFrame inventoryFrame;
    private final RecipeOptionsFrame recipeOptions;
    private final RecipeDisplayFrame recipeDisplayFrame;

    // text box
    public StackTextDisplayFrame tokenTxt;

    // separate frame for each slot
    private final SlotOptionsFrame slotOptions;

    protected final Colour gridColour = new Colour(0.1F, 0.3F, 0.4F, 0.7F);
    protected final Colour gridBorderColour = Colour.LIGHTBLUE.withAlpha(0.8);
    protected final Colour gridBackGound = new Colour(0.545D, 0.545D, 0.545D, 1);

    public MPARCGui(MTRMContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        rescale();

        backgroundRect = new DrawableRect(absX(-1), absY(-1), absX(1), absY(1), true,
                new Colour(0.0F, 0.2F, 0.0F, 0.8F),
                new Colour(0.1F, 0.9F, 0.1F, 0.8F));

        inventoryFrame = new ExtInventoryFrame(
                new Point2D(0, 0),
                new Point2D(0, 0),
                container,
                Colour.DARKBLUE,
                gridBorderColour,
                gridBackGound,
                gridBorderColour,
                gridColour,
                this);
        inventoryFrame.enable();
        inventoryFrame.show();
        frames.add(inventoryFrame);

        recipeOptions = new RecipeOptionsFrame(
                new Point2D(0, 0),
                new Point2D(0, 0),
                Colour.DARKBLUE,
                gridBorderColour,
                gridBackGound,
                this
        );
        frames.add(recipeOptions);

        // display for stack string in slot
        tokenTxt = new StackTextDisplayFrame();
        frames.add(tokenTxt);

        slotOptions = new SlotOptionsFrame(
                new Point2D(0, 0),
                new Point2D(0, 0),
                tokenTxt,
                container,
                Colour.DARKBLUE,
                gridBorderColour,
                Colour.DARKGREY,
                Colour.LIGHTGREY,
                Colour.BLACK);
        frames.add(slotOptions);

        recipeDisplayFrame = new RecipeDisplayFrame(
                new Point2D(0, 0),
                new Point2D(0, 0),
                Colour.DARKBLUE,
                gridBorderColour);
        frames.add(recipeDisplayFrame);

        sendConditionRequest();
    }

    Point2D getULShift() {
        return new Point2D(this.guiLeft, this.guiTop).plus(8, 8);
    }

    @Override
    public void init() {
        super.init();
        Minecraft.getInstance().keyboardListener.enableRepeatEvents(true);
        creationTime = System.currentTimeMillis();
        this.minecraft.player.openContainer = this.container;
        rescale();

        backgroundRect.setTargetDimensions(absX(-1), absY(-1), absX(1), absY(1));

        // left side of inventory slots
        double inventoryLeft = backgroundRect.finalRight() - spacer * 2 - 9 * slotWidth;

        // set the ulShift before setting init, since ulshift is set in init
        inventoryFrame.setULShift(getULShift());
        inventoryFrame.init(
                inventoryLeft - spacer,
                backgroundRect.finalTop() + spacer,
                backgroundRect.finalRight() - spacer,
                backgroundRect.finalTop() + spacer + 188);

        recipeOptions.init(
                backgroundRect.finalLeft() + spacer,
                backgroundRect.finalTop() + spacer,
                inventoryLeft - spacer * 2,
                backgroundRect.finalTop() + spacer + 120
        );

        slotOptions.init(
                backgroundRect.finalLeft() + spacer,
                backgroundRect.finalTop() + spacer * 2 + 120,
                inventoryLeft - spacer * 2,
                backgroundRect.finalTop() + spacer + 188);

        tokenTxt.init(
                backgroundRect.finalLeft() + spacer,
                backgroundRect.finalTop() + spacer * 2 + 188,
                backgroundRect.finalRight() - spacer,
                backgroundRect.finalTop() + spacer * 2 + 188 + 20
        );
        tokenTxt.setVisible(true);

        recipeDisplayFrame.init(
                backgroundRect.finalLeft() + spacer,
                backgroundRect.finalTop() + spacer * 2 + 212,
                backgroundRect.finalRight() - spacer,
                backgroundRect.finalBottom() - spacer);
    }

    public void sendConditionRequest() {
        MPARC_Packets.CHANNEL_INSTANCE.sendToServer(new ConditionsRequestPacket());
    }


    public void resetRecipes() {
        slotOptions.reset();
    }

    public void setConditionsJson(JsonObject conditionsJsonIn) {
        recipeOptions.setConditionsJson(conditionsJsonIn);
    }

    public JsonObject getRecipeJson() {
        String backupChars = "#@$%^&*(){}";
        JsonObject recipeJson = new JsonObject();
        recipeJson.add("result", slotOptions.getStackJson(0));
        JsonArray conditions = recipeOptions.conditionsFrame.getJson();

        ItemStack resultStack = slotOptions.getStack(0);

        if (resultStack.isEmpty()) {
            recipeDisplayFrame.setFileName("Recipe Invalid");
        } else {
            slotOptions.getStack(0);
            String filename = "";

            if(conditions.size() != 0) {
                for (Object line : conditions) {
                    if (line instanceof JsonObject && ((JsonObject) line).has("type")) {
                        String line1 = ((JsonObject) line).get("type").getAsString();
                        line1 = line1.replace("_enabled", "");

                        filename += line1;
                    } else {
                        System.out.println( line.getClass());
                    }
                }
            }

            String resultRegName = resultStack.getDisplayName()
                    .getFormattedText()
                    .replace(".tile", "")
                    .replace(".", "_")
                    .replace(" ", "_")
                    .toLowerCase();

            // 1.12.2 only
            if(resultStack.getItem().getRegistryName().getNamespace().toLowerCase().equals(/*MPA Constants */ Constants.MOD_ID.toLowerCase())
                    && resultStack.getItem() instanceof ItemComponent) {
                resultRegName = "component_" + resultRegName;
            }

            if (filename.isEmpty()) {
                recipeDisplayFrame.setFileName(resultRegName);
            } else {
                recipeDisplayFrame.setFileName(filename + "/" + resultRegName);
            }
        }

        if (conditions.size() > 0) {
            recipeJson.add("conditions", conditions);
        }
        // fixme: missing "data" (subtypes not present in 1.14.4), "nbt"

        if (recipeOptions.isShapeless()) {
            recipeJson.addProperty("type", "mpa_shapeless");

            // fixme: overlapping values and oredict issues from having individually set oredict settings?

            JsonArray ingredients = new JsonArray();
            for (int i = 1; i < 11; i++) {
                if (!this.container.getSlot(i).getStack().isEmpty()) {
                    JsonObject ingredient = slotOptions.getStackJson(i);

                    boolean match = false;

                    // add first ingredient without checking
                    if (ingredients.size() == 0) {
                        ingredients.add(slotOptions.getStackJson(i));

                        // check if ingredient is already in the list
                    } else {
                        for(int index = 0; i < ingredients.size(); index++) {
                            JsonObject jsonObject = ingredients.get(index).getAsJsonObject();
                            if (jsonObject.has("item") && ingredient.has("item")) {
                                if (jsonObject.getAsJsonObject().get("item").getAsString().equals(ingredient.get("item").getAsString())) {
                                    match = true;
                                }
                            } else if (jsonObject.getAsJsonObject().has("ore") && ingredient.has("ore")) {
                                if (jsonObject.get("ore").getAsString().equals(ingredient.get("ore").getAsString())) {
                                    match = true;
                                }
                            }
                            if (match) {
                                int count = 1;
                                if (jsonObject.getAsJsonObject().has("count")) {
                                    count = jsonObject.getAsJsonObject().get("count").getAsInt();
                                }

                                if (ingredient.has("count")) {
                                    count += ingredient.get("count").getAsInt();
                                } else {
                                    count += 1;
                                }

                                if (count != 1) {
                                    jsonObject.getAsJsonObject().addProperty("count", count);
                                    ingredients.set(index, jsonObject);
                                }
                                break;
                            } else {
                                ingredients.add(slotOptions.getStackJson(i));
                            }
                        }
                    }
                }
            }
            recipeJson.add("ingredients", ingredients);
        } else {
            if (slotOptions.getStackJson(0).has("item")) {
                recipeJson.addProperty("type", "mpa_shaped");
            } else {
                recipeJson.addProperty("type", "mpa_shaped_ore");
            }

            // only in shaped recipes
            if (recipeOptions.isMirrored()) {
                recipeJson.addProperty("mirrored", true);
            }

            Map<String, JsonObject> keys = new HashMap<>();
            String[] pattern = {"   ", "   ", "   "};

            char character = " ".charAt(0);
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    int i = row * 3 + col + 1;

                    if (!this.container.getSlot(i).getStack().isEmpty()) {
                        JsonObject ingredient = slotOptions.getStackJson(i);
                        String ingredientString = "";

                        if (ingredient.has("item")) {
                            String[] ingredientStringList = ingredient.get("item").getAsString().split(":");
                            ingredientString = ingredientStringList[ingredientStringList.length - 1].toUpperCase();
                        } else if (ingredient.has("ore")) {
                            ingredientString = ingredient.get("ore").getAsString().toUpperCase();
                        }

                        boolean keyFound = false;
                        for (int index = 0; i < ingredientString.length(); index++) {
                            character = ingredientString.charAt(index);
                            String key = String.valueOf(character);

                            // add key to map
                            if (keys.isEmpty() || !keys.containsKey(character)) {
                                keys.put(key, ingredient);
                                StringBuilder sb = new StringBuilder(pattern[row]);
                                sb.setCharAt(col, character);
                                pattern[row] = sb.toString();
                                keyFound = true;
                                break;

                                // check if key is already in map and json is a match, else new key needed
                            } else if (keys.containsKey(character)) {
                                JsonObject object = keys.get(key);
                                if (object.equals(ingredient)) {
                                    StringBuilder sb = new StringBuilder(pattern[row]);
                                    sb.setCharAt(col, character);
                                    pattern[row] = sb.toString();
                                    keyFound = true;
                                    break;
                                    // check next letter
                                } else {
                                    continue;
                                }
                            }
                        }

                        // fallback on a set of backup characters
                        if (!keyFound) {
                            for (int index = 0; i < backupChars.length(); index++) {
                                character = backupChars.charAt(index);
                                String key = String.valueOf(character);
                                keys.put(key, ingredient);
                                StringBuilder sb = new StringBuilder(pattern[row]);
                                sb.setCharAt(col, character);
                                pattern[row] = sb.toString();
                                break;
                            }
                        }
                    }
                }
            }

            JsonArray patternArray = new JsonArray();
            for (String line : pattern) {
                patternArray.add(line);
            }
            recipeJson.add("pattern", patternArray);

            JsonObject keysJson = new JsonObject();
            for (String key : keys.keySet()) {
                keysJson.add(key, keys.get(key));
            }
            recipeJson.add("keys", keysJson);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(recipeJson.toString());
        String prettyJsonString = gson.toJson(je);

        System.out.println("json: " + recipeJson.toString());
        System.out.println("prettyJson: " + prettyJsonString);

        recipeDisplayFrame.setRecipe(prettyJsonString);

        System.out.println("lines: " + prettyJsonString.split("\n").length);

        return recipeJson;
    }

    public void selectSlot(int id) {
        slotOptions.selectSlot(id);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
//        super.render(mouseX, mouseY, partialTicks);

        this.renderBackground();
        backgroundRect.draw();
        super.render(mouseX, mouseY, partialTicks);

        // Title
        Renderer.drawCenteredString("MPA-RecipeCreator", backgroundRect.centerx(), backgroundRect.finalTop() - 20);
        renderHoveredToolTip(mouseX, mouseY);
    }

    /**
     * Returns absolute screen coordinates (int 0 to width) from a relative coordinate (double -1.0 to +1.0)
     *
     * @param relx Relative X coordinate
     * @return Absolute X coordinate
     */
    public int absX(double relx) {
        int absx = (int) ((relx + 1) * xSize / 2);
        int xpadding = (width - xSize) / 2;
        return absx + xpadding;
    }

    /**
     * Returns absolute screen coordinates (int 0 to width) from a relative coordinate (double -1.0 to +1.0)
     *
     * @param rely Relative Y coordinate
     * @return Absolute Y coordinate
     */
    public int absY(double rely) {
        int absy = (int) ((rely + 1) * ySize / 2);
        int ypadding = (height - ySize) / 2;
        return absy + ypadding;
    }

    public void rescale() {
        this.xSize = 400;
        this.ySize = 330;
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }
}