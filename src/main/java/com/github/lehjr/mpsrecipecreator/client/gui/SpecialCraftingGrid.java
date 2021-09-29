package com.github.lehjr.mpsrecipecreator.client.gui;

import com.github.lehjr.numina.config.NuminaSettings;
import com.github.lehjr.numina.util.client.gui.IContainerULOffSet;
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
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Spaced crafting grid with result and corresponding buttons
 */
public class SpecialCraftingGrid extends DrawableRelativeRect implements IGuiFrame, IContainerULOffSet {
    Container container;
    Colour backgroundColour;
    Colour gridColour;
    public final int slotWidth = 18;
    public final int slotHeight = 18;
    final int spacing = 14;
    MPARCGui mparcGui;
    boolean isEnabled = true;
    boolean isVisible = true;
    float zLevel;
    MusePoint2D slot_ulShift = new MusePoint2D(0, 0);
    IContainerULOffSet.ulGetter ulGetter;

    List<BoxHolder> boxes = new ArrayList<>();
    final MusePoint2D borderWH = new MusePoint2D(160, 96);

    public SpecialCraftingGrid(Container containerIn,
                               MusePoint2D topleft,
                               float zLevel,
                               Colour backgroundColour,
                               Colour borderColour,
                               Colour gridColourIn,
                               MPARCGui mparcGui,
                               IContainerULOffSet.ulGetter ulGetter) {

        // (MusePoint2D ul, MusePoint2D br, Colour backgroundColour, Colour borderColour)
        super(topleft, topleft.plus(new MusePoint2D(160, 96)), backgroundColour, gridColourIn);


        this.container = containerIn;
        this.zLevel = zLevel;
//        this.border = new DrawableRelativeRect(topleft, topleft.copy().plus(borderWH), /*backgroundColour*/ Colour.DARKBLUE, borderColour);
        this.backgroundColour = backgroundColour;
        this.gridColour = gridColourIn;
        this.mparcGui = mparcGui;
        this.ulGetter=ulGetter;
    }

    public void loadSlots() {
        MusePoint2D wh = new MusePoint2D(slotWidth + spacing, slotHeight + spacing);
        MusePoint2D ul = new MusePoint2D(finalLeft(), finalTop());
        this.boxes.clear();;
        int i = 0;

        this.slot_ulShift = getULShift();

        for(int row = 0; row < 3; ++row) {
            for(int col = 0; col < 5; ++col) {
                if (col < 3) {
                    // crafting slots
                    this.boxes.add(new DrawableBoxHolder(ul, ul.plus(wh), backgroundColour, Colour.DARK_GREEN, (col + row * 3 + 1)));
                    // result
                } else if (col == 4 && row == 1) {
                    this.boxes.add(new DrawableBoxHolder(ul, ul.plus(wh), backgroundColour, Colour.DARK_GREEN, 0));
                    // arrow
                } else if (col == 3 && row == 1) {
                    this.boxes.add(new DrawableArrowHolder(ul, ul.plus(wh), backgroundColour, Colour.DARK_GREEN));
                    //
                } else {
                    this.boxes.add(new BoxHolder(ul, ul.plus(wh), backgroundColour, Colour.DARK_GREEN));
                }

                if (i > 0) {
                    if (col > 0) {
                        this.boxes.get(i).setMeRightOf(this.boxes.get(i - 1));
                    }

                    if (row > 0) {
                        this.boxes.get(i).setMeBelow(this.boxes.get(i - 5));
                    }
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
        if (this.containsPoint(mouseX, mouseY)) {
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
        if (this.containsPoint(mouseX, mouseY)) {
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

    @Override
    public RelativeRect init(double left, double top, double right, double bottom) {
        super.init(left, top, left +  borderWH.getX(), top + borderWH.getY());
        loadSlots();
        return this;
    }

    @Override
    public void update(double mouseX, double mouseY) {
        loadSlots();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float frameTime) {
        super.render(matrixStack, mouseX, mouseY, frameTime);
        if (this.boxes != null && !this.boxes.isEmpty()) {
            for (BoxHolder boxHolder : boxes) {
                boxHolder.render(matrixStack, mouseX, mouseY, frameTime);
            }
        }
    }

    @Override
    public float getZLevel() {
        return 0;
    }

    @Override
    public IDrawable setZLevel(float v) {
        return this;
    }

    public void setTargetDimensions(MusePoint2D ul, MusePoint2D wh) {
        super.init(ul.getX(), ul.getY(), ul.getX() + wh.getX(), ul.getY() + wh.getY());
        loadSlots();
    }

    @Override
    public void doThisOnChange() {
        super.doThisOnChange();
        loadSlots();
    }

    @Override
    public void setULGetter(IContainerULOffSet.ulGetter ulGetter) {
        this.ulGetter = ulGetter;
    }

    /**
     * returns the offset needed to compensate for the container GUI in the super class rendering the slots with an offset.
     * Also compensates for slot render sizes larger than vanilla
     * @return
     */
    @Override
    public MusePoint2D getULShift() {
        int offset = 16; // default vanilla slot size

        if (ulGetter == null) {
            return new MusePoint2D(0, 0).plus((offset - slotWidth) * 0.5, (offset - slotHeight) * 0.5);
        }
        return ulGetter.getULShift().plus((offset - slotWidth) * 0.5, (offset - slotHeight) * 0.5);
    }

    @Override
    public List<ITextComponent> getToolTip(int i, int i1) {
        return null;
    }

    @Nonnull
    @Override
    public RelativeRect getRect() {
        return this;
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

    /** ---------------------------------------------------------------------------------------- */
    class BoxHolder extends DrawableRelativeRect {
        public BoxHolder(MusePoint2D ul, MusePoint2D br, Colour backgroundColour, Colour borderColour) {
            super(ul, br, backgroundColour, borderColour);
        }

        public void render(MatrixStack matrixStackIn, int mouseX, int mouseY, float frameTime) {
            if (NuminaSettings.CLIENT_CONFIG.DRAW_GUI_SPACERS.get()) {
                super.render(matrixStackIn, mouseX, mouseY, frameTime);
            }
        }
    }

    class DrawableBoxHolder extends BoxHolder {
        DrawableTile tile;
        Button button;
        int index;

        public DrawableBoxHolder(MusePoint2D ul, MusePoint2D br, Colour backgroundColour, Colour borderColour, int index) {
            super(ul, br, backgroundColour, borderColour);
            this.index = index;
            this.button = new Button(getButtonUL(), getButtonUL().plus(slotWidth + 10, slotHeight + 10), Colour.DARK_GREY, Colour.RED, Colour.BLACK, Colour.BLACK);
            this.button.enableAndShow();
            this.button.setOnPressed(onPressed-> {
                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, 1);
                mparcGui.selectSlot(index);
            });
            this.button.setOnReleased(onReleased-> {
                mparcGui.selectSlot(index);
            });

            this.tile = new DrawableTile(getTileUL(), getTileUL().plus(slotWidth, slotHeight)).setBackgroundColour(backgroundColour).setBottomBorderColour(gridColour);
        }

        MusePoint2D getTileUL () {
            return center().minus(slotWidth * 0.5F, slotHeight * 0.5F);
        }

        MusePoint2D getButtonUL() {
            return center().minus(slotWidth * 0.5F + 5, slotHeight * 0.5F + 5);
        }

        void  fixSlotPos() {
            Slot slot = container.getSlot(index);
            if (slot instanceof UniversalSlot) {
                ((UniversalSlot)slot).setPosition(center().copy());
            } else if (slot instanceof IHideableSlot) {
                ((IHideableSlot) slot).setPosition(center().copy());
            } else {
                // Vanilla slots coordinates are UL rather than center
                slot.x = (int) tile.getUL().getX();
                slot.y = (int) tile.getUL().getY();
            }
        }

        @Override
        public void render(MatrixStack matrixStack, int mouseX, int mouseY, float frameTime) {
            if (button.getPosition() != center()) {
                button.setPosition(center().copy());
            }
            button.render(matrixStack, mouseX, mouseY, frameTime);

            if (tile.center() != center()) {
                tile.setPosition(center().copy());
            }
            tile.render(matrixStack, mouseX, mouseY, frameTime);
            fixSlotPos();
            MuseRenderer.drawShadowedStringCentered(matrixStack,
                    String.valueOf(index),
                    left() + 8,
                    bottom() - 8,
                    Colour.WHITE);
        }
    }

    class DrawableArrowHolder extends BoxHolder {
        ClickableArrow arrow;

        public DrawableArrowHolder(MusePoint2D ul, MusePoint2D br, Colour backgroundColour, Colour borderColour) {
            super(ul, br, backgroundColour, borderColour);
            arrow = new ClickableArrow(ul, ul.plus(18, 18), Colour.LIGHT_GREY, Colour.WHITE, backgroundColour);
            arrow.setDrawBorer(false);
        }

        @Override
        public void render(MatrixStack matrixStack, int mouseX, int mouseY, float frameTime) {
            if (arrow.center() != center()) {
                arrow.setPosition(center().copy());
            }
            arrow.render(matrixStack,mouseX, mouseY, frameTime);
        }
    }
}