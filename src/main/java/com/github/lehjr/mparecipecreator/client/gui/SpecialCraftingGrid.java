package com.github.lehjr.mparecipecreator.client.gui;

import com.github.lehjr.modularpowerarmor.client.sound.SoundDictionary;
import com.github.lehjr.mpalib.client.gui.clickable.Button;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableArrow;
import com.github.lehjr.mpalib.client.gui.frame.IGuiFrame;
import com.github.lehjr.mpalib.client.gui.geometry.*;
import com.github.lehjr.mpalib.client.gui.slot.UniversalSlot;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.math.Colour;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Spaced crafting grid with result and corresponding buttons
 */
public class SpecialCraftingGrid implements IGuiFrame {
    Container container;
    protected DrawableRect border;
    Colour backgroundColour;
    Colour gridColour;
    public final int gridWidth = 18;
    public final int gridHeight = 18;
    final int spacing = 14;
    MPARCGui mtrmGui;
    boolean isEnabled = true;
    boolean isVisible = true;


    List<BoxHolder> boxes = new ArrayList<>();
    final Point2D borderWH = new Point2D(160, 96);
    Point2D slot_ulShift = new Point2D(0, 0);

    public SpecialCraftingGrid(Container containerIn,
                               Point2D topleft,
                               Colour backgroundColour,
                               Colour borderColour,
                               Colour gridColourIn,
                               MPARCGui mtrmGuiIn) {
        this.container = containerIn;
        this.border = new DrawableRect(topleft, topleft.copy().plus(borderWH), /*backgroundColour*/ Colour.DARKBLUE, borderColour);
        this.backgroundColour = backgroundColour;
        this.gridColour = gridColourIn;
        this.mtrmGui = mtrmGuiIn;
    }

    public void loadSlots() {
        Point2D wh = new Point2D(gridWidth + spacing, gridHeight + spacing);
        Point2D ul = new Point2D(this.border.left(), this.border.top());
        this.boxes = new ArrayList();
        int i = 0;

        for(int row = 0; row < 3; ++row) {
            for(int col = 0; col < 5; ++col) {

                RelativeRect rect = new RelativeRect(ul, ul.plus(wh));
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

                Point2D position = rect.center().copy().minus(this.slot_ulShift);

                // standard grid slot
                if (col < 3) {
                    Slot slot = this.container.getSlot((col + row * 3 + 1));
                    if (slot instanceof UniversalSlot) {
                        ((UniversalSlot)slot).setPosition(position);
                    } else {
                        slot.xPos = (int)position.getX();
                        slot.yPos = (int)position.getY();
                    }
                    // result
                } else if (row == 1 && col == 4) {
                    Slot slot = container.getSlot(0);
                    if (slot instanceof UniversalSlot) {
                        ((UniversalSlot)slot).setPosition(position);
                    } else {
                        slot.xPos = (int)position.getX();
                        slot.yPos = (int)position.getY();
                    }
                    // spacer
                }
                ++i;
            }
        }
    }

    class BoxHolder {
        RelativeRect rect;
        public BoxHolder(RelativeRect rectIn) {
            rect = rectIn;
        }

        public void render(int mouseX, int mouseY, float partialTicks) {

        }
    }

    class DrawableBoxHolder extends BoxHolder {
        DrawableTile tile;
        Button button;

        public DrawableBoxHolder(RelativeRect rectIn, int index) {
            super(rectIn);
            this.button = new Button(getButtonUL(), getButtonUL().plus(gridWidth + 10, gridHeight + 10), Colour.DARKGREY, Colour.RED, Colour.BLACK, Colour.BLACK);
            this.button.enableAndShow();
            this.button.setOnPressed(onPressed-> {
                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, 1);
                mtrmGui.selectSlot(index);
            });
            this.button.setOnReleased(onReleased-> {
                mtrmGui.selectSlot(index);
            });

            this.tile = new DrawableTile(getTileUL(), getTileUL().plus(gridWidth, gridHeight), backgroundColour, gridColour);
        }

        Point2D getTileUL () {
            return rect.center().minus(gridWidth * 0.5, gridHeight * 0.5);
        }

        Point2D getButtonUL() {
            return rect.center().minus(gridWidth * 0.5 + 5, gridHeight * 0.5 + 5);
        }

        @Override
        public void render(int mouseX, int mouseY, float partialTicks) {
            if (button.getPosition() != rect.center()) {
                button.setPosition(rect.center());
            }
            button.render(mouseX, mouseY, partialTicks);

            if (tile.center() != rect.center()) {
                tile.setPosition(rect.center());
            }
            tile.draw();
        }
    }

    class DrawableArrowHolder extends BoxHolder {
        ClickableArrow arrow;

        public DrawableArrowHolder(RelativeRect rectIn) {
            super(rectIn);
            Point2D ul = rectIn.center().minus(gridWidth * 0.5, gridHeight * 0.5);
            arrow = new ClickableArrow(ul, ul.plus(18, 18), backgroundColour, Colour.WHITE, gridColour);
        }

        @Override
        public void render(int mouseX, int mouseY, float partialTicks) {
            if (arrow.center() != rect.center()) {
                arrow.setLeft(rect.centerx() - gridWidth * 0.5);
                arrow.setTop(rect.centery() - gridHeight * 0.5);
            }
            arrow.draw();
        }
    }

    @Override
    public boolean mouseScrolled(double v, double v1, double v2) {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.border.containsPoint(mouseX, mouseY)) {
            for (BoxHolder holder : boxes) {
                if (holder instanceof DrawableBoxHolder) {
                    if(((DrawableBoxHolder) holder).button.hitBox(mouseX, mouseY)) {
                        ((DrawableBoxHolder) holder).button.onPressed();
                        return true;
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
                    if(((DrawableBoxHolder) holder).button.hitBox(mouseX, mouseY)) {
                        ((DrawableBoxHolder) holder).button.onReleased();
                        return true;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public Point2D getUlShift() {
        return slot_ulShift;
    }

    public void setUlShift(Point2D ulShift) {
        this.slot_ulShift = ulShift;
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        this.border.setTargetDimensions(left, top, left +  borderWH.getX(), top + borderWH.getY());
        loadSlots();
    }

    @Override
    public void update(double v, double v1) {
        loadSlots();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.border.preDraw();
        this.border.drawBackground();
        if (this.boxes != null && !this.boxes.isEmpty()) {
            for (BoxHolder thing : boxes) {
                thing.render(mouseX, mouseY, partialTicks);
            }
        }
        this.border.drawBorder();
        this.border.postDraw();

        // SLOT NUMBER LABELS
        IntStream.range(0, 11).forEach(i-> {
            Renderer.drawString(
                    String.valueOf(i),
                    container.getSlot(i).xPos + 5 + slot_ulShift.getX() - 8,
                    container.getSlot(i).yPos + 4 + slot_ulShift.getY() - 8,
                    Colour.WHITE);
        });

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
    public void setTargetDimensions(double left, double top, double right, double bottom) {
        border.setTargetDimensions(left, top, right, bottom);
        loadSlots();
    }

    @Override
    public void setTargetDimensions(Point2D ul, Point2D wh) {
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
    public void move(Point2D moveAmount) {
        border.move(moveAmount);
        loadSlots();
    }

    @Override
    public void move(double x, double y) {
        border.move(x, y);
        loadSlots();
    }

    @Override
    public void setPosition(Point2D position) {
        border.setPosition(position);
        loadSlots();
    }
}