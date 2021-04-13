package com.github.lehjr.mpsrecipecreator.client.gui;

import com.github.lehjr.numina.util.client.gui.clickable.Button;
import com.github.lehjr.numina.util.client.gui.clickable.ClickableArrow;
import com.github.lehjr.numina.util.client.gui.frame.IGuiFrame;
import com.github.lehjr.numina.util.client.gui.gemoetry.*;
import com.github.lehjr.numina.util.client.gui.slot.IHideableSlot;
import com.github.lehjr.numina.util.client.gui.slot.UniversalSlot;
import com.github.lehjr.numina.util.client.render.MuseRenderer;
import com.github.lehjr.numina.util.client.sound.Musique;
import com.github.lehjr.numina.util.client.sound.SoundDictionary;
import com.github.lehjr.numina.util.math.Colour;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Spaced crafting grid with result and corresponding buttons
 */
public class SpecialCraftingGrid implements IGuiFrame {
    Container container;
    protected DrawableMuseRect border;
    Colour backgroundColour;
    Colour gridColour;
    public final int gridWidth = 18;
    public final int gridHeight = 18;
    final int spacing = 14;
    MPARCGui mparcGui;
    boolean isEnabled = true;
    boolean isVisible = true;
    float zLevel;

    List<BoxHolder> boxes = new ArrayList<>();
    final MusePoint2D borderWH = new MusePoint2D(160, 96);
    MusePoint2D slot_ulShift = new MusePoint2D(0, 0);

    public SpecialCraftingGrid(Container containerIn,
                               MusePoint2D topleft,
                               float zLevel,
                               Colour backgroundColour,
                               Colour borderColour,
                               Colour gridColourIn,
                               MPARCGui mparcGui) {
        this.container = containerIn;
        this.zLevel = zLevel;
        this.border = new DrawableMuseRect(topleft, topleft.copy().plus(borderWH), /*backgroundColour*/ Colour.DARKBLUE, borderColour);
        this.backgroundColour = backgroundColour;
        this.gridColour = gridColourIn;
        this.mparcGui = mparcGui;
    }

    public void loadSlots() {
        MusePoint2D wh = new MusePoint2D(gridWidth + spacing, gridHeight + spacing);
        MusePoint2D ul = new MusePoint2D(this.border.finalLeft(), this.border.finalTop());
        this.boxes.clear();;
        int i = 0;

        for(int row = 0; row < 3; ++row) {
            for(int col = 0; col < 5; ++col) {
                MuseRelativeRect rect = new MuseRelativeRect(ul, ul.plus(wh));
                if (col < 3) {
                    this.boxes.add(new DrawableBoxHolder(rect, (col + row * 3 + 1)));
                } else if (col == 4 && row == 1) {
                    this.boxes.add(new DrawableBoxHolder(rect, 0));
                } else if (col == 3 && row == 1) {
                    this.boxes.add(new DrawableArrowHolder(rect));
                } else {
                    this.boxes.add(new BoxHolder(rect));
                }

                if (i > 0) {
                    if (col > 0) {
                        this.boxes.get(i).rect.setMeRightOf(this.boxes.get(i - 1).rect);
                    }

                    if (row > 0) {
                        this.boxes.get(i).rect.setMeBelow(this.boxes.get(i - 5).rect);
                    }
                }

                MusePoint2D position = rect.center().copy().minus(this.slot_ulShift);

                // standard grid slot
                if (col < 3) {
                    Slot slot = this.container.getSlot((col + row * 3 + 1));
                    if (slot instanceof UniversalSlot) {
                        ((UniversalSlot)slot).setPosition(position);
                    } else if (slot instanceof IHideableSlot) {
                        ((IHideableSlot) slot).setPosition(position);
                    } else {
                        slot.xPos = (int) position.getX();
                        slot.yPos = (int) position.getY();
                    }

                // result
                } else if (row == 1 && col == 4) {
                    // position here: x: 366.0, y: 48.0
                    // actual position here: x: 387.0, y: 11.0
                    Slot slot = container.getSlot(0);
                    if (slot instanceof UniversalSlot) {
                        ((UniversalSlot)slot).setPosition(position);
                    } else if (slot instanceof IHideableSlot) {
                        ((IHideableSlot) slot).setPosition(position);
                    } else {
                        slot.xPos = (int) position.getX();
                        slot.yPos = (int) position.getY();
                    }
                    // spacer
                }
                ++i;
            }
        }
    }

    @Override
    public boolean mouseScrolled(double v, double v1, double v2) {
        return false;
    }

    /** Note: returning false here even when handled here so slot handling code still runs */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.border.containsPoint(mouseX, mouseY)) {
            for (BoxHolder holder : boxes) {
                if (holder instanceof DrawableBoxHolder) {
                    if(((DrawableBoxHolder) holder).button.hitBox((float)mouseX, (float) mouseY)) {
                        ((DrawableBoxHolder) holder).button.onPressed();
//                        return true; // testing
                        break;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.border.containsPoint(mouseX, mouseY)) {
            for (BoxHolder holder : boxes) {
                if (holder instanceof DrawableBoxHolder) {
                    if(((DrawableBoxHolder) holder).button.hitBox((float)mouseX, (float) mouseY)) {
                        ((DrawableBoxHolder) holder).button.onReleased();
                        break;
                    }
                }
            }
        }
        return false;
    }

    public MusePoint2D getUlShift() {
        return slot_ulShift;
    }

    public void setUlShift(MusePoint2D ulShift) {
        this.slot_ulShift = ulShift;
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        this.border.setTargetDimensions(left, top, left +  borderWH.getX(), top + borderWH.getY());
        loadSlots();
    }

    @Override
    public void update(double mouseX, double mouseY) {
        loadSlots();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.border.draw(matrixStack, zLevel);
        if (this.boxes != null && !this.boxes.isEmpty()) {
            for (BoxHolder boxHolder : boxes) {
                boxHolder.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        }

        // SLOT NUMBER LABELS
        IntStream.range(0, 11).forEach(i->
            MuseRenderer.drawString(matrixStack,
                    String.valueOf(i),
                    container.getSlot(i).xPos + 5 + slot_ulShift.getX() - 8,
                    container.getSlot(i).yPos + 4 + slot_ulShift.getY() - 8,
                    Colour.WHITE));
    }



    @Override
    public void setTargetDimensions(double left, double top, double right, double bottom) {
        border.setTargetDimensions(left, top, right, bottom);
        loadSlots();
    }

    class BoxHolder {
        MuseRelativeRect rect;
        public BoxHolder(MuseRelativeRect rectIn) {
            rect = rectIn;
        }

        public void render(MatrixStack matrixStackIn, int mouseX, int mouseY, float partialTicks) {

        }
    }

    class DrawableBoxHolder extends BoxHolder {
        DrawableTile tile;
        Button button;

        public DrawableBoxHolder(MuseRelativeRect rectIn, int index) {
            super(rectIn);
            this.button = new Button(getButtonUL(), getButtonUL().plus(gridWidth + 10, gridHeight + 10), Colour.DARK_GREY, Colour.RED, Colour.BLACK, Colour.BLACK);
            this.button.enableAndShow();
            this.button.setOnPressed(onPressed-> {
                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, 1);
                mparcGui.selectSlot(index);
            });
            this.button.setOnReleased(onReleased-> {
                mparcGui.selectSlot(index);
            });

            this.tile = new DrawableTile(getTileUL(), getTileUL().plus(gridWidth, gridHeight)).setBackgroundColour(backgroundColour).setBottomBorderColour(gridColour);
        }

        MusePoint2D getTileUL () {
            return rect.center().minus(gridWidth * 0.5F, gridHeight * 0.5F);
        }

        MusePoint2D getButtonUL() {
            return rect.center().minus(gridWidth * 0.5F + 5, gridHeight * 0.5F + 5);
        }

        @Override
        public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            if (button.getPosition() != rect.center()) {
                button.setPosition(rect.center());
            }
            button.render(matrixStack, mouseX, mouseY, partialTicks, zLevel);

            if (tile.center() != rect.center()) {
                tile.setPosition(rect.center());
            }
            tile.draw(matrixStack, zLevel);
        }
    }

    class DrawableArrowHolder extends BoxHolder {
        ClickableArrow arrow;

        public DrawableArrowHolder(MuseRelativeRect rectIn) {
            super(rectIn);
            MusePoint2D ul = rectIn.center().minus(gridWidth * 0.5F, gridHeight * 0.5F);
            arrow = new ClickableArrow(ul, ul.plus(18, 18), backgroundColour, Colour.WHITE, gridColour);
        }

        @Override
        public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            if (arrow.center() != rect.center()) {
                arrow.setLeft(rect.centerx() - gridWidth * 0.5F);
                arrow.setTop(rect.centery() - gridHeight * 0.5F);
            }
            arrow.draw(matrixStack,zLevel + 3);
        }
    }

    @Override
    public List<ITextComponent> getToolTip(int i, int i1) {
        return null;
    }

    @Override
    public IRect getBorder() {
        return this.border;
    }

    @Override
    public void setEnabled(boolean b) {
        this.isEnabled = b;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setVisible(boolean b) {
        this.isVisible = b;
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public void setTargetDimensions(MusePoint2D ul, MusePoint2D wh) {
        border.setTargetDimensions(ul, wh);
        loadSlots();
    }

    @Override
    public IRect setLeft(double value) {
        setLeft(value);
        loadSlots();
        return this;
    }

    @Override
    public IRect setRight(double value) {
        setRight(value);
        loadSlots();
        return this;
    }

    @Override
    public IRect setTop(double value) {
        setTop(value);
        loadSlots();
        return this;
    }

    @Override
    public IRect setBottom(double value) {
        setBottom(value);
        loadSlots();
        return this;
    }

    @Override
    public IRect setWidth(double value) {
        border.setWidth(value);
        loadSlots();
        return this;
    }

    @Override
    public IRect setHeight(double value) {
        border.setHeight(value);
        loadSlots();
        return this;
    }

    @Override
    public void move(MusePoint2D moveAmount) {
        border.move(moveAmount);
        loadSlots();
    }

    @Override
    public void move(double x, double y) {
        border.move(x, y);
        loadSlots();
    }

    @Override
    public void setPosition(MusePoint2D position) {
        border.setPosition(position);
        loadSlots();
    }
}