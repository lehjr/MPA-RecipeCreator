package com.github.lehjr.mparecipecreator.jei;

import com.github.lehjr.mparecipecreator.client.gui.MPARCGui;
import mezz.jei.api.gui.handlers.IGuiProperties;
import net.minecraft.client.gui.screen.Screen;

import javax.annotation.Nullable;

public class MPARCGuiProperties implements IGuiProperties {
    private final Class<? extends Screen> guiClass;
    private final int guiLeft;
    private final int guiTop;
    private final int guiXSize;
    private final int guiYSize;
    private final int screenWidth;
    private final int screenHeight;

    @Nullable
    public static MPARCGuiProperties create(MPARCGui guiContainer) {
        return guiContainer.width != 0 && guiContainer.height != 0 ? new MPARCGuiProperties(guiContainer.getClass(), guiContainer.getGuiLeft(), guiContainer.getGuiTop(), guiContainer.getXSize(), guiContainer.getYSize(), guiContainer.width, guiContainer.height) : null;
    }

    public static boolean areEqual(@Nullable IGuiProperties a, @Nullable IGuiProperties b) {
        if (a == b) {
            return true;
        } else {
            return a != null && b != null && a.getScreenClass().equals(b.getScreenClass()) && a.getGuiLeft() == b.getGuiLeft() && a.getGuiXSize() == b.getGuiXSize() && a.getScreenWidth() == b.getScreenWidth() && a.getScreenHeight() == b.getScreenHeight();
        }
    }

    private MPARCGuiProperties(Class<? extends Screen> guiClass, int guiLeft, int guiTop, int guiXSize, int guiYSize, int screenWidth, int screenHeight) {
        this.guiClass = guiClass;
        this.guiLeft = guiLeft;
        this.guiTop = guiTop;
        this.guiXSize = guiXSize;
        this.guiYSize = guiYSize;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public Class<? extends Screen> getScreenClass() {
        return this.guiClass;
    }

    public int getGuiLeft() {
        return this.guiLeft;
    }

    public int getGuiTop() {
        return this.guiTop;
    }

    public int getGuiXSize() {
        return this.guiXSize;
    }

    public int getGuiYSize() {
        return this.guiYSize;
    }

    public int getScreenWidth() {
        return this.screenWidth;
    }

    public int getScreenHeight() {
        return this.screenHeight;
    }
}
