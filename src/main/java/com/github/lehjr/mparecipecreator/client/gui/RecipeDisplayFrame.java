package com.github.lehjr.mparecipecreator.client.gui;

import com.github.lehjr.mpalib.client.gui.clickable.ClickableLabel;
import com.github.lehjr.mpalib.client.gui.frame.ScrollableFrame;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.math.Colour;

public class RecipeDisplayFrame extends ScrollableFrame {
    String recipe;
    private ClickableLabel title;;

    public RecipeDisplayFrame(Point2D topleft, Point2D bottomright, Colour backgroundColour, Colour borderColour) {
        super(topleft, bottomright, backgroundColour, borderColour);
        this.title = new ClickableLabel("FileName: ", new Point2D(topleft).copy().plus(4, 4));
        reset();
    }

    public void setFileName(String fileName) {
        title.setLabel("FileName: " + fileName);
    }

    public void setRecipe(String recipe) {

    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        super.init(left, top, right, bottom);
        title.setPosition(border.getUL().copy().plus(24, 10));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        title.render(mouseX, mouseY, partialTicks);
    }

    public void reset() {
//        recipe = "";
//        filename = "";
    }

//                this.totalsize = checkBoxList.size() * 15;




}