package com.github.lehjr.mpsrecipecreator.client.gui;

import com.github.lehjr.numina.util.client.gui.frame.IGuiFrame;
import com.github.lehjr.numina.util.client.gui.frame.InventoryFrame;
import com.github.lehjr.numina.util.client.gui.frame.ScrollableFrame;
import com.github.lehjr.numina.util.client.gui.gemoetry.MusePoint2D;
import com.github.lehjr.numina.util.math.Colour;
import com.mojang.blaze3d.matrix.MatrixStack;
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
    MusePoint2D ulShift = new MusePoint2D(0, 0);
    final int spacer = 4;
    final int slotHeight = 18;
    List<IGuiFrame> frames = new ArrayList<>();

    public ExtInventoryFrame(
            MusePoint2D topleft,
            MusePoint2D bottomright,
            float zLevel,
            Container container,
            Colour backgroundColour,
            Colour borderColour,
            Colour gridBackGound,
            Colour gridBorderColour,
            Colour gridColour,
            MPARCGui mparcGui) {
        super(topleft, bottomright, zLevel, backgroundColour, borderColour);

        this.container = container;

        // slots 0 - 9
        craftingGrid = new SpecialCraftingGrid(
                container,
                new MusePoint2D(0, 0),
                zLevel,
                gridBackGound,
                gridBorderColour,
                gridColour,
                mparcGui
        );
        frames.add(craftingGrid);

        // slot 10-36
        mainInventory = new InventoryFrame(this.container,
                new MusePoint2D(0,0), new MusePoint2D(0, 0),
                zLevel,
                gridBackGound, gridBorderColour, gridColour,
                9, 3, new ArrayList<Integer>(){{
            IntStream.range(10, 37).forEach(i-> add(i));
        }});
        frames.add(mainInventory);

        // slot 0-9
        hotbar = new InventoryFrame(this.container,
                new MusePoint2D(0,0), new MusePoint2D(0, 0),
                zLevel,
                gridBackGound, gridBorderColour, gridColour,
                9, 1, new ArrayList<Integer>(){{
            IntStream.range(37, 46).forEach(i-> add(i));
        }});
        frames.add(hotbar);
    }

    public void setULShift(MusePoint2D ulShift) {
        this.ulShift = ulShift;
    }

    public MusePoint2D getULShift() {
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
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        if (this.isVisible()) {
            for (IGuiFrame frame : frames) {
                frame.render(matrixStack, mouseX, mouseY, partialTicks);
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
