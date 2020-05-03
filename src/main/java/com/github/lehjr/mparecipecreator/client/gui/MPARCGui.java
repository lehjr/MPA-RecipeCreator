package com.github.lehjr.mparecipecreator.client.gui;

import com.github.lehjr.mpalib.client.gui.ExtendedContainerScreen;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableRect;
import com.github.lehjr.mpalib.client.gui.geometry.Point2F;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mparecipecreator.basemod.DataPackWriter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

/**
 * @author Dries007
 */
public class MPARCGui extends ExtendedContainerScreen<MPARCContainer> {
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
    protected final Colour gridBorderColour = Colour.LIGHTBLUE.withAlpha(0.8F);
    protected final Colour gridBackGound = new Colour(0.545F, 0.545F, 0.545F, 1);
    public RecipeGen recipeGen;

    public MPARCGui(MPARCContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        rescale();
        float zLevel = getBlitOffset();

        backgroundRect = new DrawableRect(absX(-1), absY(-1), absX(1), absY(1), true,
                new Colour(0.0F, 0.2F, 0.0F, 0.8F),
                new Colour(0.1F, 0.9F, 0.1F, 0.8F));

        inventoryFrame = new ExtInventoryFrame(
                new Point2F(0, 0),
                new Point2F(0, 0),
                zLevel,
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
                new Point2F(0, 0),
                new Point2F(0, 0),
                zLevel,
                Colour.DARKBLUE,
                gridBorderColour,
                gridBackGound,
                this
        );
        frames.add(recipeOptions);
        recipeGen = new RecipeGen(container, recipeOptions);

        // display for stack string in slot
        tokenTxt = new StackTextDisplayFrame(zLevel);
        frames.add(tokenTxt);

        slotOptions = new SlotOptionsFrame(
                new Point2F(0, 0),
                new Point2F(0, 0),
                zLevel,
                recipeGen,
                container,
                Colour.DARKBLUE,
                gridBorderColour,
                Colour.DARKGREY,
                Colour.LIGHTGREY,
                Colour.BLACK);
        frames.add(slotOptions);

        recipeDisplayFrame = new RecipeDisplayFrame(
                new Point2F(0, 0),
                new Point2F(0, 0),
                zLevel,
                Colour.DARKBLUE,
                gridBorderColour);
        frames.add(recipeDisplayFrame);
    }

    Point2F getULShift() {
        return new Point2F(this.guiLeft, this.guiTop).plus(8, 8);
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
        float inventoryLeft = backgroundRect.finalRight() - spacer * 2 - 9 * slotWidth;

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
                backgroundRect.finalTop() + spacer + 150
        );

        slotOptions.init(
                backgroundRect.finalLeft() + spacer,
                backgroundRect.finalTop() + spacer * 2 + 150,
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

    public void resetRecipes() {
        slotOptions.reset();
        recipeGen.reset();
        container.craftMatrix.clear();
        container.craftResult.clear();
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        return super.mouseClicked(x, y, button);
    }

    public void save() {
        if(container.getSlot(0).getHasStack()) {
            DataPackWriter.writeRecipe(recipeGen.getRecipeJson(),recipeDisplayFrame.title + ".json");
        }
    }

    @Override
    public void update(double x, double y) {
        super.update(x, y);

        // reset's the slot's settings when teh contents changed.
        int slotChanged = container.getSlotChanged();
        if (slotChanged != -1) {
            recipeGen.useOredict.put(slotChanged, false);
            recipeGen.setOreTagIndex(slotChanged,0);
            // no oredict for result
            if (slotChanged < 0) {
                slotOptions.useOreDictCheckbox[slotChanged - 1].setChecked(false);
            }
        }

        int activeSlot = slotOptions.getActiveSlotID();

        if (activeSlot >= 0) {
            tokenTxt.setLabel(recipeGen.getStackToken(activeSlot));
        }

        recipeDisplayFrame.setFileName(recipeGen.getFileName());
        recipeDisplayFrame.setRecipe(recipeGen.getRecipeJson());
    }

    public void selectSlot(int index) {
        slotOptions.selectSlot(index);
        tokenTxt.setSlot(index);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        backgroundRect.draw(this.getBlitOffset());
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