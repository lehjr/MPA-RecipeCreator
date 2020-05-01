package com.github.lehjr.mparecipecreator.client.gui;

import com.github.lehjr.mpalib.client.gui.clickable.CheckBox;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableLabel;
import com.github.lehjr.mpalib.client.gui.clickable.LabledButton;
import com.github.lehjr.mpalib.client.gui.frame.ScrollableFrame;
import com.github.lehjr.mpalib.client.gui.geometry.Point2F;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.math.Colour;
import net.minecraft.util.SoundEvents;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lehjr
 */
public class RecipeOptionsFrame extends ScrollableFrame {
    protected List<CheckBox> checkBoxes = new ArrayList<>();
    protected List<LabledButton> buttons = new ArrayList<>();

    final int spacer = 4;
    private CheckBox
            shapeless,
            mirrored,
            conditions;
    private LabledButton save;
    private LabledButton reset;
    private ClickableLabel title;
    ConditionsFrame conditionsFrame;

    public RecipeOptionsFrame(
            Point2F topleft,
            Point2F bottomright,
            float zLevel,
            Colour backgroundColour,
            Colour borderColour,
            Colour conditionsBorder,
            MPARCGui mparcGui) {
        super(topleft, bottomright, zLevel, backgroundColour, borderColour);

        Point2F starterPoint = new Point2F(getULFinal());
        this.title = new ClickableLabel("Recipe Options", starterPoint);
        title.setMode(ClickableLabel.JustifyMode.LEFT);

        shapeless = addCheckBox(new CheckBox(starterPoint, "Shapeless", false));//ID_SHAPELESS
        mirrored = addCheckBox(new CheckBox(starterPoint, "Mirrored", true));//ID_MIRRORED
        conditions = addCheckBox(new CheckBox(starterPoint, "Conditions", false));//ID_CONDITIONS // fixme... not tied to anything yet

        shapeless.setOnPressed(press->{
            mirrored.setEnabled(!(shapeless.isChecked()));
            if (!mirrored.isEnabled()){
                mirrored.setChecked(false);
            }
        });

        conditions.setOnPressed(press->{
            if (conditions.isChecked()) {
                conditionsFrame.loadConditions();
                conditionsFrame.show();
                conditionsFrame.enable();
            } else {
                conditionsFrame.disable();
                conditionsFrame.hide();
            }
        });

        conditionsFrame = new ConditionsFrame(
                new Point2F(0,0), new Point2F(0, 0),
                zLevel,
                Colour.DARKBLUE,
                conditionsBorder
        );
        conditionsFrame.disable();
        conditionsFrame.hide();

        save = addButton(new LabledButton(starterPoint, starterPoint.plus(110, 20), Colour.DARKGREY, Colour.RED, Colour.BLACK, Colour.BLACK,"Save"));
        save.setOnPressed(pressed->{
            Musique.playClientSound(SoundEvents.UI_BUTTON_CLICK,1);
            mparcGui.save();
        });

        reset = addButton(new LabledButton(starterPoint, starterPoint.plus(110, 20), Colour.DARKGREY, Colour.RED, Colour.BLACK, Colour.BLACK,"Reset Recipe"));
        reset.setOnPressed(pressed-> {
            Musique.playClientSound(SoundEvents.UI_BUTTON_CLICK, 1);
            mparcGui.resetRecipes();
        });
    }

    public boolean isShapeless() {
        return shapeless.isChecked();
    }

    // fixme: should only be enabled if shaped recipe
    public boolean isMirrored() {
        return mirrored.isChecked();
    }

    @Override
    public void init(float left, float top, float right, float bottom) {
        super.init(left, top, right, bottom);

        float nextLineRC = 0;
        Point2F genericRecipeCol = new Point2F(left + 8, top + 8);
        title.setPosition(genericRecipeCol);
        shapeless.setPosition(genericRecipeCol.plus(0, nextLineRC+=12));
        mirrored.setPosition(genericRecipeCol.plus(0, nextLineRC+=12));
        conditions.setPosition(genericRecipeCol.plus(0, nextLineRC+=12));

        conditionsFrame.init(
                left + 3,
                conditions.getPosition().getY() + spacer * 2,
                right - 3,
                bottom - spacer
        );
        save.setPosition(new Point2F(right, top).copy().plus(-(spacer + save.finalWidth() * 0.5F), spacer + save.finalHeight() * 0.5F));
        reset.setPosition(save.getPosition().plus(0, 24F));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (isVisible()) {
            super.render(mouseX, mouseY, partialTicks);
            title.render(mouseX, mouseY, partialTicks, zLevel);

            for (CheckBox checkBox : checkBoxes) {
                checkBox.render(mouseX, mouseY, partialTicks, zLevel);
            }

            for (LabledButton button : buttons) {
                button.render(mouseX, mouseY, partialTicks, zLevel);
            }

            conditionsFrame.render(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void update(double x, double y) {
        super.update(x, y);
        if (conditionsFrame.isEnabled() && conditionsFrame.isVisible()) {
            conditionsFrame.update(x, y);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isVisible() && isEnabled()) {
            super.mouseClicked(mouseX, mouseY, button);

            for (CheckBox checkBox : checkBoxes) {
                if (checkBox.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }

            for (LabledButton lbutton : buttons) {
                if (lbutton.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
            conditionsFrame.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dWheel) {
        if (conditionsFrame.isVisible() && conditionsFrame.mouseScrolled(mouseX, mouseY, dWheel)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, dWheel);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isVisible() && isEnabled()) {
            super.mouseReleased(mouseX, mouseY, button);
            if (conditionsFrame.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    LabledButton addButton(LabledButton button) {
        this.buttons.add(button);
        return button;
    }

    CheckBox addCheckBox(CheckBox checkBox) {
        checkBoxes.add(checkBox);
        return checkBox;
    }
}