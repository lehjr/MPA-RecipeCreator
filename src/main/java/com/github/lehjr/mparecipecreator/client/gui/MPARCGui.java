package com.github.lehjr.mparecipecreator.client.gui;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.item.component.ItemComponent;
import com.github.lehjr.mpalib.client.gui.ContainerGui;
import com.github.lehjr.mpalib.client.gui.frame.IGuiFrame;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableRect;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.math.Colour;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dries007
 */
public class MPARCGui extends ContainerGui {
    protected List<IGuiFrame> frames = new ArrayList<>();
    protected DrawableRect backgroundRect;
    protected long creationTime;

    final int slotWidth = 18;
    final int spacer = 4;

    private ExtInventoryFrame inventoryFrame;
    private RecipeOptionsFrame recipeOptions;
    private RecipeDisplayFrame recipeDisplayFrame;

    // text box
    public GuiTextField tokenTxt;

    // separate frame for each slot
    private SlotOptionsFrame slotOptions;

    protected final Colour gridColour = new Colour(0.1F, 0.3F, 0.4F, 0.7F);
    protected final Colour gridBorderColour = Colour.LIGHTBLUE.withAlpha(0.8);
    protected final Colour gridBackGound = new Colour(0.545D, 0.545D, 0.545D, 1);

    public MPARCGui(MTRMContainer container) {
        super(container);
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
        tokenTxt = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer,
                0,
                0, 0, 20);
        tokenTxt.setMaxStringLength(Integer.MAX_VALUE);

        slotOptions = new SlotOptionsFrame(
                new Point2D(0, 0),
                new Point2D(0, 0),
                tokenTxt,
                (MTRMContainer) inventorySlots,
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
    }

    Point2D getULShift() {
        return new Point2D(this.guiLeft, this.guiTop).plus(8, 8);
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        creationTime = System.currentTimeMillis();
        this.mc.player.openContainer = this.inventorySlots;
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

        tokenTxt.x = (int) (backgroundRect.finalLeft() + spacer);
        tokenTxt.y = (int) (backgroundRect.finalTop() + spacer * 2 + 188);
        tokenTxt.width = (int) (backgroundRect.finalRight() - backgroundRect.finalLeft() - spacer * 2);
        tokenTxt.setVisible(true);

        recipeDisplayFrame.init(
                backgroundRect.finalLeft() + spacer,
                backgroundRect.finalTop() + spacer * 2 + 212,
                backgroundRect.finalRight() - spacer,
                backgroundRect.finalBottom() - spacer);
    }

    public void resetRecipes() {
        slotOptions.reset();
    }

    public JSONObject getRecipeJson() {
        String backupChars = "#@$%^&*(){}";
        JSONObject recipeJson = new JSONObject();
        recipeJson.put("result", slotOptions.getStackJson(0));
        JSONArray conditions = recipeOptions.conditionsFrame.getJson();

        ItemStack resultStack = slotOptions.getStack(0);

        if (resultStack.isEmpty()) {
            recipeDisplayFrame.setFileName("Recipe Invalid");
        } else {
            slotOptions.getStack(0);
            String filename = "";

            if(!conditions.isEmpty()) {
                for (Object line : conditions) {
                    if (line instanceof JSONObject && ((JSONObject) line).has("type")) {
                        String line1 = ((JSONObject) line).getString("type");
                        line1 = line1.replace("_enabled", "");

                        filename += line1;
                    } else {
                        System.out.println( line.getClass());
                    }
                }
            }

            String resultRegName = resultStack.getDisplayName()
                    .replace(".tile", "")
                    .replace(".", "_")
                    .replace(" ", "_")
                    .toLowerCase();

            // 1.12.2 only
            if(resultStack.getItem().getRegistryName().getNamespace().toLowerCase().equals(/*MPA Constants */ Constants.MODID.toLowerCase())
                    && resultStack.getItem() instanceof ItemComponent) {
                resultRegName = "component_" + resultRegName;
            }

            if (filename.isEmpty()) {
                recipeDisplayFrame.setFileName(resultRegName);
            } else {
                recipeDisplayFrame.setFileName(filename + "/" + resultRegName);
            }
        }

        if (!conditions.isEmpty()) {
            recipeJson.put("conditions", conditions);
        }
        // fixme: missing "data" (subtypes not present in 1.14.4), "nbt"

        if (recipeOptions.isShapeless()) {
            recipeJson.put("type", "mpa_shapeless");

            // fixme: overlapping values and oredict issues from having individually set oredict settings?
            List<JSONObject> ingredients = new ArrayList<>();

            for (int i = 1; i < 11; i++) {
                if (!this.inventorySlots.getSlot(i).getStack().isEmpty()) {
                    JSONObject ingredient = slotOptions.getStackJson(i);

                    boolean match = false;

                    if (ingredients.isEmpty()) {
                        ingredients.add(slotOptions.getStackJson(i));
                    } else {
                        for (JSONObject jsonObject : ingredients) {
                            if (jsonObject.has("item") && ingredient.has("item")) {
                                if (jsonObject.getString("item").equals(ingredient.getString("item"))) {
                                    match = true;
                                }
                            } else if (jsonObject.has("ore") && ingredient.has("ore")) {
                                if (jsonObject.getString("ore").equals(ingredient.getString("ore"))) {
                                    match = true;
                                }
                            }

                            if (match) {
                                int index = ingredients.indexOf(jsonObject);

                                int count = 1;
                                if (jsonObject.has("count")) {
                                    count = jsonObject.getInt("count");
                                }

                                if (ingredient.has("count")) {
                                    count += ingredient.getInt("count");
                                } else {
                                    count += 1;
                                }

                                if (count != 1) {
                                    jsonObject.put("count", count);
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
            recipeJson.put("ingredients", ingredients);
        } else {
            if (slotOptions.getStackJson(0).has("item")) {
                recipeJson.put("type", "mpa_shaped");
            } else {
                recipeJson.put("type", "mpa_shaped_ore");
            }

            // only in shaped recipes
            if (recipeOptions.isMirrored()) {
                recipeJson.put("mirrored", true);
            }

            Map<String, JSONObject> keys = new HashMap<>();
            String[] pattern = {"   ", "   ", "   "};

            char character = " ".charAt(0);
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    int i = row * 3 + col + 1;

                    if (!this.inventorySlots.getSlot(i).getStack().isEmpty()) {
                        JSONObject ingredient = slotOptions.getStackJson(i);
                        String ingredientString = "";

                        if (ingredient.has("item")) {
                            String[] ingredientStringList = ingredient.getString("item").split(":");
                            ingredientString = ingredientStringList[ingredientStringList.length - 1].toUpperCase();
                        } else if (ingredient.has("ore")) {
                            ingredientString = ingredient.getString("ore").toUpperCase();
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
                                JSONObject object = keys.get(key);
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

            recipeJson.put("pattern", pattern);
            recipeJson.put("keys", keys);
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
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        // slight precision boost by using doubles here
        double x = Mouse.getEventX() * this.width / (double) this.mc.displayWidth;
        double y = this.height - Mouse.getEventY() * this.height / (double) this.mc.displayHeight - 1;

        for (IGuiFrame frame : frames) {
            if (frame.onMouseDown(x, y, mouseButton)) {
                return;
            }
        }
    }

    /**
     * Called when the mouse is moved or a mouse button is released. Signature:
     * (mouseX, mouseY, which) which==-1 is mouseMove, which==0 or which==1 is
     * mouseUp
     */
    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        // slight precision boost by using doubles here
        double x = Mouse.getEventX() * this.width / (double) this.mc.displayWidth;
        double y = this.height - Mouse.getEventY() * this.height / (double) this.mc.displayHeight - 1;

        for (IGuiFrame frame : frames) {
            if (frame.onMouseUp(x, y, state)) {
                return;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        // slight precision boost by using doubles here
        double x = Mouse.getEventX() * this.width / (double) this.mc.displayWidth;
        double y = this.height - Mouse.getEventY() * this.height / (double) this.mc.displayHeight - 1;

        update(x, y);
        this.drawBackground();

        renderFrames(mouseX, mouseY, partialTicks);

        //-----------------
        super.drawScreen(mouseX, mouseY, partialTicks);

        tokenTxt.drawTextBox();

        // Title
        Renderer.drawCenteredString("MPA-RecipeCreator", backgroundRect.centerx(), backgroundRect.finalTop() - 20);

        //-------------------------------

        renderHoveredToolTip(mouseX, mouseY);
    }

    /**
     * Update frames
     *
     * @param x
     * @param y
     */
    public void update(double x, double y) {
        for (IGuiFrame frame : frames) {
            frame.update(x, y);
        }
    }

    /**
     * Render the frames
     *
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    public void renderFrames(int mouseX, int mouseY, float partialTicks) {
        for (IGuiFrame frame : frames) {
            frame.render(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void drawBackground() {
        this.drawDefaultBackground();
        backgroundRect.draw();
    }

    /**
     * Adds a frame to this gui's draw list.
     *
     * @param frame
     */
    public void addFrame(IGuiFrame frame) {
        frames.add(frame);
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