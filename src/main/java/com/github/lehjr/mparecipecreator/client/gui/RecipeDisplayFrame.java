package com.github.lehjr.mparecipecreator.client.gui;

import com.github.lehjr.mpalib.client.gui.clickable.ClickableLabel;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableFrame;
import com.github.lehjr.mpalib.math.Colour;

import java.util.HashMap;
import java.util.Map;

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

    public void setRecipe() {

    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        super.init(left, top, right, bottom);
        title.setPosition(new Point2D(left, top).plus(4, 4));

    }

    public void reset() {
//        recipe = "";
//        filename = "";
    }

//                this.totalsize = checkBoxList.size() * 15;




}