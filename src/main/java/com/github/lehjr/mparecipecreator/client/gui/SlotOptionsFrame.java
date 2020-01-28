package com.github.lehjr.mparecipecreator.client.gui;

import com.github.lehjr.mpalib.client.gui.clickable.*;
import com.github.lehjr.mpalib.client.gui.frame.InventoryFrame;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableArrow;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableRect;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableFrame;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mparecipecreator.network.MessageSend;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * @author lehjr
 */
public class SlotOptionsFrame extends ScrollableFrame {
    /**
     * TODO: remove these when converting to Json output
     */
    public static final Pattern MATCH_ALL = Pattern.compile("<\\*>");
    public static final Pattern META_WILDCARD = Pattern.compile(":\\*>");
    public static final Pattern ORE_DICT = Pattern.compile("<ore:.*>");
    public static final Pattern REUSE = Pattern.compile("\\.reuse\\(\\)");
    public static final Pattern NO_RETURN = Pattern.compile("\\.noReturn\\(\\)");
    public static final Pattern ANY_DAMAGE = Pattern.compile("\\.anyDamage\\(\\)");
    public static final Pattern ONLY_DAMAGE = Pattern.compile("\\.onlyDamaged\\(\\)");
    public static final Pattern ONLY_DAMAGE_AT_MOST = Pattern.compile("\\.onlyDamageAtMost\\((\\d+)\\)");
    public static final Pattern ONLY_DAMAGE_AT_LEAST = Pattern.compile("\\.onlyDamageAtLeast\\((\\d+)\\)");
    public static final Pattern ONLY_DAMAGE_BETWEEN = Pattern.compile("\\.onlyDamageBetween\\((\\d+), ?(\\d+)\\)");
    public static final Pattern WITH_DAMAGE = Pattern.compile("\\.withDamage\\((\\d+)\\)");
    public static final Pattern TRANSFORM_DAMAGE = Pattern.compile("\\.transformDamage\\((\\d+)\\)");
    public static final Pattern GIVE_BACK = Pattern.compile("\\.giveBack\\(<(.*):(.*)(:\\d+)?>(?: ?\\* ?(\\d+))\\)");
    public static final Pattern TRANSFORM_REPLACE = Pattern.compile("\\.transformReplace\\(<(.*):(.*)(:\\d+)?>(?: ?\\* ?(\\d+))\\)");

    private ClickableLabel title;
    private ClickableLabel returnSlotLabel;
    final int slotID;
    MTRMContainer container;
    private MessageSend messageSend;

    private int lastOreId = 0;
    InventoryFrame returnSlot;
    protected DrawableRect extSlotOptionsRect;

    final int slotWidth = 18;
    final int slotHeight = 18;
    final int spacer = 4;

    Point2D ulShift = new Point2D(0,0);

    protected List<CheckBox> checkBoxes = new ArrayList<>();
    protected List<LabledButton> buttons = new ArrayList<>();
    private Map<CheckBox, RangedSliderExt[]> sliders = new HashMap<>();
    private Map<CheckBox, Pattern> patterns = new HashMap<>();
    private CheckBox[] allDamage;
    private CheckBox[] allTransform;
    private CheckBox[][] allGroups;

    private final RangedSlider.ISlider iSlider;

    // text box
    public GuiTextField tokenTxt;

    private ClickableArrow prevOredict, nextOredict;

    // Slot specific
    private CheckBox
            matchAll,
            oreDict,
            metaWildcard,
            anyDamage,
            onlyDamaged,
            withDamage,
            onlyDamageAtLeast,
            onlyDamageAtMost,
            onlyDamageBetween,
            transformDamage,
            transformReplace,
            reuse,
            noReturn,
            giveBack;

    private LabledButton
            returnOk,
            optionsOk,
            nextOreDict;

    public SlotOptionsFrame(Point2D topleft,
                            Point2D bottomright,
                            int slotID,
                            MTRMContainer container,
                            Colour backgroundColour,
                            Colour borderColour,
                            Colour arrowNormalBackGound,
                            Colour arrowHighlightedBackground,
                            Colour arrowBorderColour,
                            Colour gridBackGound,
                            Colour gridBorderColour,
                            Colour gridColour) {
        super(topleft, bottomright, backgroundColour, borderColour);
        this.slotID = slotID;
        this.container = container;
        messageSend = new MessageSend();


        Point2D starterPoint = this.getULFinal().copy().plus(4, 4);

        this.title = new ClickableLabel("Slot " + slotID + " Options", starterPoint.copy());
        title.setMode(0);

        this.returnSlotLabel = new ClickableLabel("", starterPoint);
        returnSlotLabel.setMode(0);

        extSlotOptionsRect = new DrawableRect(
                topleft,
                bottomright,
                backgroundColour,
                borderColour);

        returnSlot = new InventoryFrame(
                this.container,
                new Point2D(0,0), new Point2D(0, 0),
                gridBackGound,
                gridBorderColour,
                gridColour,
                9, 1, new ArrayList<Integer>(){{
            IntStream.range(container.getReturnSlotId(slotID), container.getReturnSlotId(slotID) + 1).forEach(i-> add(i));
        }});

        System.out.println("range: " + (new ArrayList<Integer>(){{
            IntStream.range(container.getReturnSlotId(slotID), container.getReturnSlotId(slotID) + 1).forEach(i-> add(i));
        }}));

        // display for stack string in slot
        tokenTxt = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer,
                0,
                0, 0, 20);
        tokenTxt.setMaxStringLength(Integer.MAX_VALUE);

        iSlider= slider -> tokenTxt.setText(getStackToken(false, container.getSlot(slotID).getStack()));

        int id = 0;
        metaWildcard = addCheckBox(new CheckBox(id++, starterPoint, "Match any metadata", false)); //ID_OPTION_META_WILDCARD
        anyDamage = addCheckBox(new CheckBox(id++, starterPoint,  "Any Damage", false)); //ID_OPTION_ANY_DAMAGE
        onlyDamaged = addCheckBox(new CheckBox(id++, starterPoint, "Only Damaged", false)); // ID_OPTION_ONLY_DAMAGED
        withDamage = addCheckBox(new CheckBox(id++, starterPoint, "With Damage X", false));//ID_OPTION_WITH_DAMAGE
        onlyDamageAtLeast = addCheckBox(new CheckBox(id++, starterPoint,  "With Damage >= X", false));//ID_OPTION_ONLY_DAMAGE_AT_LEAST
        onlyDamageAtMost = addCheckBox(new CheckBox(id++, starterPoint, "With Damage < X", false));//ID_OPTION_ONLY_DAMAGE_AT_MOST
        onlyDamageBetween = addCheckBox(new CheckBox(id++, starterPoint, "With X > Damage > Y", false));//ID_OPTION_ONLY_DAMAGE_BETWEEN
        transformDamage = addCheckBox(new CheckBox(id++, starterPoint, "Transform Damage", false));//ID_OPTION_ID_OPTION_TRANSFORM_DAMAGE
        transformReplace = addCheckBox(new CheckBox(id++, starterPoint, "Transform Replace", false));//ID_OPTION_ID_OPTION_TRANSFORM_REPLACE
        reuse = addCheckBox(new CheckBox(id++, starterPoint, "Reuse", false));//ID_OPTION_REUSE
        noReturn = addCheckBox(new CheckBox(id++, starterPoint, "No Return", false));//ID_OPTION_NO_RETURN
        giveBack = addCheckBox(new CheckBox(id++, starterPoint, "GiveBack", false));//ID_OPTION_GIVE_BACK

        nextOredict = new ClickableArrow(0, 0, 0, 0, true, arrowNormalBackGound, arrowHighlightedBackground, arrowBorderColour);
        nextOredict.setDrawShaft(false);

        prevOredict = new ClickableArrow(0, 0, 0, 0, true, arrowNormalBackGound, arrowHighlightedBackground, arrowBorderColour);
        prevOredict.setDrawShaft(false);
        prevOredict.setDirection(DrawableArrow.ArrowDirection.LEFT);

        // Slot-specific checkboxes
        matchAll = addCheckBox(new CheckBox(id++, starterPoint, "Match not empty (*)", false)); // ID_OPTION_MATCHALL
        matchAll.setOnPressed(pressed->{
            radioBoxToggle(matchAll, allDamage);
            oreDict.setChecked(!matchAll.isChecked());
            oreDict.setEnabled(!matchAll.isChecked());
            nextOreDict.setEnabled(oreDict.isChecked());
            if (matchAll.isChecked()) {
                tokenTxt.setText("<*>");
            } else {
                tokenTxt.setText(getStackToken(false, container.getSlot(slotID).getStack()));
            }
        });

        oreDict = addCheckBox(new CheckBox(id++, starterPoint, "Use ore dictionary", true)); //ID_OPTION_OREDICT
        oreDict.setOnPressed(pressed->{
            nextOreDict.setEnabled(oreDict.isChecked());
        });

        returnOk = addButton(new LabledButton(starterPoint, starterPoint.plus(20, 18), Colour.DARKGREY, Colour.RED, Colour.BLACK, Colour.BLACK,  "OK"));

        //); //ID_OPTION_RETURN_OK
        returnOk.setOnPressed(pressed->{
            Musique.playClientSound(SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, Minecraft.getMinecraft().player.getPosition());
            tokenTxt.setText(getStackToken(true, container.getSlot(slotID).getStack()));
        });

        nextOreDict = addButton(new LabledButton(starterPoint, starterPoint.plus(110, 20), Colour.DARKGREY, Colour.RED, Colour.BLACK, Colour.BLACK,"Next oredict value"));
        nextOreDict.setOnReleased(pressed->{
            Musique.playClientSound(SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, Minecraft.getMinecraft().player.getPosition());
            tokenTxt.setText(getStackToken(true, container.getSlot(slotID).getStack()));
        });

        optionsOk = addButton(new LabledButton(starterPoint, starterPoint.plus(110, 20), Colour.DARKGREY, Colour.RED, Colour.BLACK, Colour.BLACK,"Save changes!"));
        optionsOk.setOnReleased(pressed-> {
            Musique.playClientSound(SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, Minecraft.getMinecraft().player.getPosition());
            System.out.println("fixme!!!!");
//            saveOptions();
        });

        allTransform = new CheckBox[]{transformDamage, transformReplace, reuse, noReturn, giveBack};
        for (CheckBox checkBox : allTransform) {
            checkBox.setOnPressed(press-> {
                toggleAllTransforms(checkBox);
            });
        }

        allDamage = new CheckBox[]{matchAll, metaWildcard, anyDamage, onlyDamaged, withDamage, onlyDamageAtLeast, onlyDamageAtMost, onlyDamageBetween};
        for (CheckBox checkBox : allDamage) {
            if (checkBox == matchAll) {
                continue;
            }
            checkBox.setOnPressed(press-> {
                toggleAllDamage(checkBox);
            });
        }

        allGroups = new CheckBox[][]{allDamage, allTransform};


        matchAll.setVisible(true);
        metaWildcard.setVisible(true);

        optionsOk.setVisible(true);
        oreDict.setVisible(true);
        nextOreDict.setVisible(true);

        anyDamage.setVisible(true);
        withDamage.setVisible(true);
        onlyDamaged.setVisible(true);
        reuse.setVisible(true);
        returnOk.setVisible(true);

        addPatterns(matchAll, MATCH_ALL);
        addPatterns(metaWildcard, META_WILDCARD);
        addPatterns(anyDamage, ANY_DAMAGE);
        addPatterns(onlyDamaged, ONLY_DAMAGE);

        addPatterns(reuse, REUSE);
        addPatterns(noReturn, NO_RETURN);

        transformReplace.setOnPressed(pressed->{
            if (transformReplace.isChecked()) {
                returnSlotLabel.setLabel("Transform Replace:");
            }
        });

        giveBack.setOnPressed(pressed->{
            if(giveBack.isChecked()) {
                returnSlotLabel.setLabel("Give Back:");
            }
        });

        int wOffset = (int) (this.centerx() - 200);
        int hOffset = (int) (this.centery() - 110);

//
        hOffset += 30;
        int hOffsetText = (int) (hOffset - this.finalTop() - 10);

        // supposed to render the slot rect if this enabled
        addPatterns(transformReplace, TRANSFORM_REPLACE);

        // supposed to render the slot rect if this enabled
        addPatterns(giveBack, GIVE_BACK);

        double nextSliderY = 40;
        Point2D nextSliderPoint = new Point2D(extSlotOptionsRect.centerx(), extSlotOptionsRect.finalTop());

        addSliders(withDamage, new RangedSliderExt(id++, nextSliderPoint.plus(0, nextSliderY), extSlotOptionsRect.finalWidth() - 40, "With Damage X", "X = ", "", 0, 0, 0, false, true, iSlider));
        addPatterns(withDamage, WITH_DAMAGE);

        addSliders(onlyDamageAtLeast, new RangedSliderExt(id++, nextSliderPoint.plus(0, nextSliderY), extSlotOptionsRect.finalWidth() - 40, "With Damage >= X", "X = ", "", 0, 0, 0, false, true, iSlider));
        addPatterns(onlyDamageAtLeast, ONLY_DAMAGE_AT_LEAST);

        addSliders(onlyDamageAtMost, new RangedSliderExt(id++, nextSliderPoint.plus(0, nextSliderY), extSlotOptionsRect.finalWidth() - 40, "With Damage < X", "X = ", "", 0, 0, 0, false, true, iSlider));
        addPatterns(onlyDamageAtMost, ONLY_DAMAGE_AT_MOST);

        addSliders(onlyDamageBetween, new RangedSliderExt(id++, nextSliderPoint.plus(0, nextSliderY), extSlotOptionsRect.finalWidth() - 40, "With X > Damage > Y", "X = ", "", 0, 0, 0, false, true, iSlider),
                new RangedSliderExt(id++, nextSliderPoint.plus(0, nextSliderY += 20), extSlotOptionsRect.finalWidth() - 40, "", "Y = ", "", 0, 0, 0, false, true, iSlider));
        addPatterns(onlyDamageBetween, ONLY_DAMAGE_BETWEEN);

        addSliders(transformDamage, new RangedSliderExt(id++, nextSliderPoint.plus(0, nextSliderY += 20), extSlotOptionsRect.finalWidth() - 40, "Transform with X damage", "X = ", "", 0, 0, 0, false, true, iSlider));
        addPatterns(transformDamage, TRANSFORM_DAMAGE);
    }

    /**
     * Call this instead of the normal frame init since this sets up the stuff in the extra frame on the bottom
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @param left2
     * @param top2
     * @param right2
     * @param bottom2
     */
    public void extInit(double left, double top, double right, double bottom, double left2, double top2, double right2, double bottom2) {
        this.init(left, top, right, bottom);
        this.extSlotOptionsRect.setTargetDimensions(left2, top2, right2, bottom2);

        tokenTxt.x = (int) (left2 + spacer);
        tokenTxt.y = (int) (top2 + spacer);
        tokenTxt.width = (int) (right2 - left2 - spacer * 2);

        Point2D nextSliderPoint = new Point2D(extSlotOptionsRect.centerx(), extSlotOptionsRect.finalTop());

        // "With Damage X"
        double nextSliderY = 20;
        for (RangedSliderExt slider : sliders.get(withDamage)) {
            slider.setPosition(nextSliderPoint.plus(0, nextSliderY += 20));
            slider.setWidth(right2 - left2 - 40);
        }

        // "With Damage >= X"
        nextSliderY = 20;
        for (RangedSliderExt slider : sliders.get(onlyDamageAtLeast)) {
            slider.setPosition(nextSliderPoint.plus(0, nextSliderY += 20));
            slider.setWidth(right2 - left2 - 40);
        }

        // "With Damage < X"
        nextSliderY = 20;
        for (RangedSliderExt slider : sliders.get(onlyDamageAtMost)) {
            slider.setPosition(nextSliderPoint.plus(0, nextSliderY += 20));
            slider.setWidth(right2 - left2 - 40);
        }

        // "With X > Damage > Y"
        nextSliderY = 20;
        for (RangedSliderExt slider : sliders.get(onlyDamageBetween)) {
            slider.setPosition(nextSliderPoint.plus(0, nextSliderY += 20));
            slider.setWidth(right2 - left2 - 40);
        }

        // "Transform with X damage"
        nextSliderY = 20;
        for (RangedSliderExt slider : sliders.get(transformDamage)) {
            slider.setPosition(nextSliderPoint.plus(0, nextSliderY += 20));
            slider.setWidth(right2 - left2 - 40);
        }
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        super.init(left, top, right, bottom);

        // Slot-specific controls
        Point2D slotSpecificCol = this.getULFinal().plus(spacer, spacer);
        double nextLineSC = 0;

        title.setPosition(slotSpecificCol.plus(0,spacer));

        matchAll.setPosition(slotSpecificCol.plus(0, nextLineSC+=10));
        oreDict.setPosition(slotSpecificCol.plus(0, nextLineSC+=10));
        metaWildcard.setPosition(slotSpecificCol.plus(0, nextLineSC+=10));
        anyDamage.setPosition(slotSpecificCol.plus(0, nextLineSC+=10));
        onlyDamaged.setPosition(slotSpecificCol.plus(0, nextLineSC+=10));
        withDamage.setPosition(slotSpecificCol.plus(0, nextLineSC+=10));
        onlyDamageAtLeast.setPosition(slotSpecificCol.plus(0, nextLineSC+=10));
        onlyDamageAtMost.setPosition(slotSpecificCol.plus(0, nextLineSC+=10));
        onlyDamageBetween.setPosition(slotSpecificCol.plus(0, nextLineSC+=10));
        transformDamage.setPosition(slotSpecificCol.plus(0, nextLineSC+=10));
        transformReplace.setPosition(slotSpecificCol.plus(0, nextLineSC+=10));
        reuse.setPosition(slotSpecificCol.plus(0, nextLineSC+=10));
        noReturn.setPosition(slotSpecificCol.plus(0, nextLineSC+=10));
        giveBack.setPosition(slotSpecificCol.plus(0, nextLineSC+=10));

        returnOk.setPosition(slotSpecificCol.plus(returnOk.finalWidth() * 0.5 + spacer + 20, nextLineSC += 35));

        returnSlotLabel.setPosition(new Point2D(this.finalLeft() + 4, returnOk.finalTop()-spacer * 2));

        returnSlot.init(this.finalLeft() + 4,
                returnOk.finalTop(),
                this.finalLeft() + 4 + slotWidth,
                returnOk.finalTop()  + slotHeight);
        returnSlot.setUlShift(getULShift());

        nextOreDict.setPosition(slotSpecificCol.plus(nextOreDict.finalWidth() * 0.5 + 50, nextLineSC+= 22));
        optionsOk.setPosition(slotSpecificCol.plus(optionsOk.finalWidth() * 0.5 + 50, nextLineSC += 22));
    }


    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (isVisible()) {
            super.render(mouseX, mouseY, partialTicks);
            title.render(mouseX, mouseY, partialTicks);
            returnSlotLabel.render(mouseX, mouseY, partialTicks);

            extSlotOptionsRect.draw();

            for (CheckBox checkBox : checkBoxes) {
                checkBox.render(mouseX, mouseY, partialTicks);
            }

            for (LabledButton button : buttons) {
                button.render(mouseX, mouseY, partialTicks);
            }

            tokenTxt.drawTextBox();

            returnSlot.render(mouseX, mouseY, partialTicks);

            for (CheckBox checkBox : sliders.keySet()) {
                for (RangedSliderExt slider : sliders.get(checkBox)) {
                    slider.render(mouseX, mouseY, partialTicks);
                }
            }
        }
    }

    @Override
    public void update(double x, double y) {
        if (isVisible()) {
            super.update(x, y);
            try {
                returnSlot.update(x, y);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (CheckBox checkBox : sliders.keySet()) {
                for (RangedSliderExt slider : sliders.get(checkBox)) {
                    slider.update(x, y);
                }
            }
        }
    }


    // FIXME: is this needed?
    public void setOptionsVisible(boolean visible) {

        System.out.println("visible: " + visible);




//        if (editing != slotID) {
//            lastOreId = 0;
//        }
//        editing = slotID;
        String token = messageSend.data[slotID];
        if (token == null) {
            tokenTxt.setText("empty");

            oreDict.setEnabled(slotID != 0);
            oreDict.setChecked(slotID != 0);
            nextOreDict.setEnabled(slotID != 0);

            for (CheckBox[] group : allGroups) {
                for (CheckBox checkBox : group) {
                    checkBox.setChecked(false);
                    if (sliders.containsKey(checkBox)) {
                        for (RangedSliderExt slider : sliders.get(checkBox)) {
                            slider.minValue = 0;
                            slider.maxValue = 0;
                            slider.setValue(0);
                        }
                    }
                }
            }
            container.getSlot(container.getReturnSlotId(slotID)).putStack(ItemStack.EMPTY);
        } else {
            tokenTxt.setText(token);

            oreDict.setEnabled(slotID != 0);
            oreDict.setChecked(ORE_DICT.matcher(token).find() && slotID != 0);
            nextOreDict.setEnabled(oreDict.isChecked() && slotID != 0);

            for (CheckBox[] group : allGroups) {
                for (CheckBox checkBox : group) {
                    if (patterns.containsKey(checkBox)) {
                        checkBox.setChecked(patterns.get(checkBox).matcher(token).find());
                        radioBoxToggle(checkBox, group);
                    }
                }
            }

            Matcher m = GIVE_BACK.matcher(token);
            if (m.find()) {
                int meta = m.group(3) != null ? Integer.parseInt(m.group(3)) : 0;
                int size = m.group(4) != null ? Integer.parseInt(m.group(4)) : 1;
                Item i = Item.REGISTRY.getObject(new ResourceLocation(m.group(1), m.group(2)));
                if (i != null) {
                    container.getSlot(container.getReturnSlotId(slotID)).putStack(new ItemStack(i, size, meta));
                }
            }
        }

        for (CheckBox[] group : allGroups) {
            for (CheckBox checkBox : group) {



                checkBox.setEnabled(slotID != 0);
                if (!sliders.containsKey(checkBox)) continue;
                RangedSliderExt[] sliderA = sliders.get(checkBox);
                for (RangedSliderExt slider : sliderA) {
                    slider.minValue = 0;
                    slider.maxValue = Integer.MAX_VALUE;
                    slider.setValue(0);
                    if (!oreDict.isChecked()) {
                        ItemStack stack = container.getSlot(slotID).getStack();
                        if (!stack.isEmpty()) {
                            slider.maxValue = stack.getMaxDamage();
                        }
                    }
                }
                if (token != null && patterns.containsKey(checkBox)) {
                    Matcher matcher = patterns.get(checkBox).matcher(token);
                    if (matcher.find()) {
                        for (int i = 0; i < matcher.groupCount() && i < sliderA.length; i++) {
                            sliderA[i].setValue(Integer.parseInt(matcher.group(i + 1)));
                        }
                    }
                }
            }
        }

        //------------------------------------------------------
        matchAll.setVisible(visible);
        metaWildcard.setVisible(visible);

        optionsOk.setVisible(visible);
        oreDict.setVisible(visible);
        nextOreDict.setVisible(visible);

        anyDamage.setVisible(visible);
        withDamage.setVisible(visible);
        onlyDamaged.setVisible(visible);
        reuse.setVisible(visible);
        returnOk.setVisible(visible);

        for (CheckBox[] group : allGroups) {
            for (CheckBox gui : group) {
                gui.setVisible(visible);
                if (sliders.containsKey(gui)) {
                    for (RangedSliderExt s : sliders.get(gui)) {
                        s.setVisible(visible && gui.isChecked());
                    }
                }
            }
        }

        tokenTxt.setVisible(visible);

        if (!visible) {
            this.container.getSlot(this.container.getReturnSlotId(slotID)).putStack(ItemStack.EMPTY);
        }
    }

    private void radioBoxToggle(CheckBox clicked, CheckBox... others) {
        for (CheckBox checkBox : others) {
            if (sliders.containsKey(checkBox)) {
                for (RangedSliderExt s : sliders.get(checkBox)) {
                    s.setVisible(checkBox.isChecked());
                }
            }
            if (checkBox == clicked) {
                continue;
            }
            if (clicked.isChecked()) {
                checkBox.setChecked(false);
            }
            checkBox.setEnabled(!clicked.isChecked());
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (visible) {
            // FIXME!! there should be conditions here since the slot is conditionally visible

            returnSlot.init(this.finalLeft() + 4,
                    returnOk.finalTop(),
                    this.finalLeft() + 4 + slotWidth,
                    returnOk.finalTop()  + slotHeight);
            returnSlot.setUlShift(getULShift());
        } else {
            returnSlot.setVisible(false);
            returnSlot.init(-1000,
                    -1000,
                    -1000,
                    -1000);
            returnSlot.setUlShift(new Point2D(-1000, -1000));
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        returnSlot.setEnabled(enabled);
    }

    /**
     * This entire section just gets the formatted string for display/use in the MineTweaker recipe.
     *
     * @param nextOreDict
     * @param stack
     * @return
     */
    public String getStackToken(boolean nextOreDict, ItemStack stack) {
        boolean metaWildcard = this.metaWildcard.isChecked();
        boolean oreDict = this.oreDict.isChecked();
        if (stack.isEmpty()){
            return "empty";
        }
        if (stack.getItem().getRegistryName() == null) {
            throw new IllegalStateException("PLEASE REPORT: Item not empty, but getRegistryName null? Debug info: " + stack);
        }
        String stackName = stack.getItem().getRegistryName().toString();
        StringBuilder builder = new StringBuilder("<");
        if (oreDict) {
            int[] ids = OreDictionary.getOreIDs(stack);
            if (ids.length != 0) {
                stackName = "ore:" + OreDictionary.getOreName(ids[lastOreId]);
                if (nextOreDict) {
                    lastOreId++;
                }
                if (lastOreId >= ids.length) {
                    lastOreId = 0;
                }
            } else oreDict = false;
        }
        builder.append(stackName);
        if (!oreDict && (metaWildcard || stack.getItemDamage() != 0)) {
            builder.append(':').append(metaWildcard || stack.getItemDamage() == OreDictionary.WILDCARD_VALUE ? "*" : stack.getItemDamage());
        }
        builder.append('>');

        if (stack.getCount() > 1) {
            builder.append(" * ").append(stack.getCount());
        }
        if (anyDamage.isChecked()) {
            builder.append(".anyDamage()");
        }
        if (onlyDamaged.isChecked()) {
            builder.append(".onlyDamaged()");
        }
        if (withDamage.isChecked()) {
            builder.append(".withDamage(").append(sliders.get(withDamage)[0].getValueInt()).append(')');
        }
        if (onlyDamageAtLeast.isChecked()) {
            builder.append(".onlyDamageAtLeast(").append(sliders.get(onlyDamageAtLeast)[0].getValueInt()).append(')');
        }
        if (onlyDamageAtMost.isChecked()) {
            builder.append(".onlyDamageAtMost(").append(sliders.get(onlyDamageAtMost)[0].getValueInt()).append(')');
        }
        if (onlyDamageBetween.isChecked()) {
            builder.append(".onlyDamageBetween(").append(sliders.get(onlyDamageBetween)[0].getValueInt()).append(", ").append(sliders.get(onlyDamageBetween)[1].getValueInt()).append(')');
        }
        if (reuse.isChecked()) {
            builder.append(".reuse()");
        }
        if (noReturn.isChecked()) {
            builder.append(".noReturn()");
        }
        if (transformDamage.isChecked()) {
            builder.append(".transformDamage(").append(sliders.get(transformDamage)[0].getValueInt()).append(')');
        }
        if (container.getSlot(container.getReturnSlotId(slotID)).getHasStack() && (giveBack.isChecked() || transformReplace.isChecked())) {
            ItemStack returnStack = container.getSlot(container.getReturnSlotId(slotID)).getStack();

            if (giveBack.isChecked()) {
                builder.append(".giveBack(<");
            } else if (transformReplace.isChecked()) {
                builder.append(".transformReplace(<");
            }

            builder.append(returnStack.getItem().getRegistryName());
            if (returnStack.getItemDamage() != 0) {
                builder.append(':').append(returnStack.getItemDamage());
            }
            builder.append('>');
            if (returnStack.getCount() > 1) {
                builder.append(" * ").append(returnStack.getCount());
            }
            builder.append(')');
        }
        return builder.toString();
    }










    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        if (isVisible()) {
            super.onMouseDown(mouseX, mouseY, button);

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

            for (CheckBox checkBox : sliders.keySet()) {
                for (RangedSliderExt slider : sliders.get(checkBox)) {
                    if (slider.mouseClicked(mouseX, mouseY, button)) {
                        return true;
                    }
                }
            }

            returnSlot.onMouseDown(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean onMouseUp(double mouseX, double mouseY, int button) {
        if (isVisible()) {
            super.onMouseUp(mouseX, mouseY, button);
            returnSlot.onMouseUp(mouseX, mouseY, button);
        }

        for (CheckBox checkBox : sliders.keySet()) {
            for (RangedSliderExt slider : sliders.get(checkBox)) {
                slider.mouseReleased(mouseX, mouseY, button);
            }
        }

        return false;
    }

    public void setULShift(Point2D ulShift) {
        this.ulShift = ulShift;
    }

    public Point2D getULShift() {
        return ulShift;
    }

    public void toggleAllDamage(CheckBox btn) {
        radioBoxToggle(btn, allDamage);
        tokenTxt.setText(getStackToken(false, container.getSlot(slotID).getStack()));
    }

    public void toggleAllTransforms(CheckBox btn) {
        radioBoxToggle(btn, allTransform);
        tokenTxt.setText(getStackToken(false, container.getSlot(slotID).getStack()));
    }

    void addSliders(CheckBox key, RangedSliderExt... values) {
        sliders.put(key, values);
    }

    LabledButton addButton(LabledButton button) {
        this.buttons.add(button);
        return button;
    }

    void addPatterns(CheckBox key, Pattern values) {
        patterns.put(key, values);
    }

    CheckBox addCheckBox(CheckBox checkBox) {
        checkBoxes.add(checkBox);
        return checkBox;
    }
}