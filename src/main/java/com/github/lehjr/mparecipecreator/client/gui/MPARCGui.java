package com.github.lehjr.mparecipecreator.client.gui;

import com.github.lehjr.mpalib.client.gui.ContainerGui;
import com.github.lehjr.mpalib.client.gui.frame.IGuiFrame;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableRect;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.math.Colour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        slotOptions = new SlotOptionsFrame(
                new Point2D(0,0),
                new Point2D(0, 0),
                tokenTxt,
                (MTRMContainer) inventorySlots,
                Colour.DARKBLUE,
                gridBorderColour,
                Colour.DARKGREY,
                Colour.LIGHTGREY,
                Colour.BLACK);

        frames.add(slotOptions);

        // display for stack string in slot
        tokenTxt = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer,
                0,
                0, 0, 20);
        tokenTxt.setMaxStringLength(Integer.MAX_VALUE);

        recipeDisplayFrame = new RecipeDisplayFrame(
                new Point2D(0,0),
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
        // left side of inventory slots
        double inventoryLeft = backgroundRect.finalRight() - spacer * 2 - 9 * slotWidth;

        slotOptions.reset();
    }

    /**
     * Toggles the weird collection of checkboxes and buttons.
     *
     *
     * @param id
     */
    public void showOptionsFor(int id) {
//            slotOptions[i].setVisible(i==id);
//            slotOptions[i].setOptionsVisible(i==id);


        System.out.println("FIXME!!! not yet implemented");



//        if (editing != id) {
//            lastOreId = 0;
//        }
//        editing = id;
//        String token = messageSend.data[id];
//        if (token == null) {
//            tokenTxt.setText("empty");
//
//            oreDict.setEnabled(id != 0);
//            oreDict.setChecked(id != 0);
//            nextOreDict.setEnabled(id != 0);
//
//            for (CheckBox[] group : allGroups) {
//                for (CheckBox checkBox : group) {
//                    checkBox.setChecked(false);
//                    if (sliders.containsKey(checkBox)) {
//                        for (RangedSliderExt slider : sliders.get(checkBox)) {
//                            slider.minValue = 0;
//                            slider.maxValue = 0;
//                            slider.setValue(0);
//                        }
//                    }
//                    if (labels.containsKey(checkBox)) {
//                        for (GuiCustomLabel l : labels.get(checkBox)) {
//                            l.draw = false;
//                        }
//                    }
//                }
//            }
//            inventorySlots.getSlot(RETURN_SLOT_ID).putStack(ItemStack.EMPTY);
//        } else {
//            tokenTxt.setText(token);
//
//            oreDict.setEnabled(id != 0);
//            oreDict.setChecked(ORE_DICT.matcher(token).find() && id != 0);
//            nextOreDict.setEnabled(oreDict.isChecked() && id != 0);
//
//            for (CheckBox[] group : allGroups) {
//                for (CheckBox checkBox : group) {
//                    if (patterns.containsKey(checkBox)) {
//                        checkBox.setChecked(patterns.get(checkBox).matcher(token).find());
//                        radioBoxToggle(checkBox, group);
//                    }
//                }
//            }
//
//            Matcher m = GIVE_BACK.matcher(token);
//            if (m.find()) {
//                int meta = m.group(3) != null ? Integer.parseInt(m.group(3)) : 0;
//                int size = m.group(4) != null ? Integer.parseInt(m.group(4)) : 1;
//                Item i = Item.REGISTRY.getObject(new ResourceLocation(m.group(1), m.group(2)));
//                if (i != null) {
//                    inventorySlots.getSlot(RETURN_SLOT_ID).putStack(new ItemStack(i, size, meta));
//                }
//            }
//        }
//
//        for (CheckBox[] group : allGroups) {
//            for (CheckBox checkBox : group) {
//                checkBox.setEnabled(id != 0);
//                if (!sliders.containsKey(checkBox)) continue;
//                RangedSliderExt[] sliderA = sliders.get(checkBox);
//                for (RangedSliderExt slider : sliderA) {
//                    slider.minValue = 0;
//                    slider.maxValue = Integer.MAX_VALUE;
//                    slider.setValue(0);
//                    if (!oreDict.isChecked()) {
//                        ItemStack stack = inventorySlots.getSlot(editing).getStack();
//                        if (!stack.isEmpty()) {
//                            slider.maxValue = stack.getMaxDamage();
//                        }
//                    }
//                }
//                if (token != null && patterns.containsKey(checkBox)) {
//                    Matcher matcher = patterns.get(checkBox).matcher(token);
//                    if (matcher.find()) {
//                        for (int i = 0; i < matcher.groupCount() && i < sliderA.length; i++) {
//                            sliderA[i].setValue(Integer.parseInt(matcher.group(i + 1)));
//                        }
//                    }
//                }
//            }
//        }
//
//        setOptionsVisible(true);
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