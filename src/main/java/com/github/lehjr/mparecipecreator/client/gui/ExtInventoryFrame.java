package com.github.lehjr.mparecipecreator.client.gui;

import com.github.lehjr.mpalib.client.gui.frame.IGuiFrame;
import com.github.lehjr.mpalib.client.gui.frame.InventoryFrame;
import com.github.lehjr.mpalib.client.gui.frame.ScrollableFrame;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.math.Colour;
import net.minecraft.inventory.container.Container;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author lehjr
 */
public class ExtInventoryFrame extends ScrollableFrame {
    InventoryFrame mainInventory, hotbar;
    SpecialCraftingGrid craftingGrid;
    Container container;
    Point2D ulShift = new Point2D(0, 0);
    final int spacer = 4;
    final int slotHeight = 18;
    List<IGuiFrame> frames = new ArrayList<>();

    public ExtInventoryFrame(
            Point2D topleft,
            Point2D bottomright,
            Container container,
            Colour backgroundColour,
            Colour borderColour,
            Colour gridBackGound,
            Colour gridBorderColour,
            Colour gridColour,
            MPARCGui mparcGui) {
        super(topleft, bottomright, backgroundColour, borderColour);

        this.container = container;

        // slots 0 - 9
        craftingGrid = new SpecialCraftingGrid(
                container,
                new Point2D(0, 0),
                gridBackGound,
                gridBorderColour,
                gridColour,
                mparcGui
        );
        frames.add(craftingGrid);

        // slot 10-36
        mainInventory = new InventoryFrame(this.container,
                new Point2D(0,0), new Point2D(0, 0),
                gridBackGound, gridBorderColour, gridColour,
                9, 3, new ArrayList<Integer>(){{
            IntStream.range(10, 37).forEach(i-> add(i));
        }});
        frames.add(mainInventory);

        // slot 0-9
        hotbar = new InventoryFrame(this.container,
                new Point2D(0,0), new Point2D(0, 0),
                gridBackGound, gridBorderColour, gridColour,
                9, 1, new ArrayList<Integer>(){{
            IntStream.range(37, 46).forEach(i-> add(i));
        }});
        frames.add(hotbar);
    }

    public void setULShift(Point2D ulShift) {
        this.ulShift = ulShift;
    }

    public Point2D getULShift() {
        return ulShift;
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        super.init(left, top, right, bottom);

        hotbar.init(
                left + spacer,
                bottom - spacer - slotHeight,
                right - spacer,
                bottom - spacer);
        hotbar.setUlShift(ulShift);

        mainInventory.init(
                left + spacer,
                hotbar.finalTop() - spacer - 3 * slotHeight,
                right - spacer,
                hotbar.finalTop() - spacer);
        mainInventory.setUlShift(getULShift());

        craftingGrid.init(
                left + spacer,
                mainInventory.finalTop() - spacer * 2 - 96,

                0, // ignored
                0); // ignored
        craftingGrid.setUlShift(getULShift());
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        if (this.isVisible()) {
            for (IGuiFrame frame : frames) {
                frame.render(mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isVisible()) {
            super.mouseClicked(mouseX, mouseY, button);
            for (IGuiFrame frame : frames) {
                if (frame.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.isVisible() && this.isEnabled()) {
            super.mouseReleased(mouseX, mouseY, button);
            for (IGuiFrame frame : frames) {
                if (frame.mouseReleased(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void update(double x, double y) {
        if (this.isVisible() && this.isEnabled()) {
            super.update(x, y);
            for (IGuiFrame frame : frames) {
                frame.update(x, y);
            }
        }
    }
}
