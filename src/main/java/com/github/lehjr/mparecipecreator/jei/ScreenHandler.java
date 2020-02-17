package com.github.lehjr.mparecipecreator.jei;

import com.github.lehjr.mparecipecreator.client.gui.MPARCGui;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.gui.handlers.IScreenHandler;

import javax.annotation.Nullable;

public class ScreenHandler implements IScreenHandler<MPARCGui> {

    @Nullable
    @Override
    public IGuiProperties apply(MPARCGui mparcGui) {
        return MPARCGuiProperties.create(mparcGui);
    }
}