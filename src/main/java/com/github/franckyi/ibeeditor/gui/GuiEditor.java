package com.github.franckyi.ibeeditor.gui;

import com.github.franckyi.ibeeditor.IBEConfiguration;
import com.github.franckyi.ibeeditor.gui.child.GuiCategoryList;
import com.github.franckyi.ibeeditor.gui.child.GuiPropertyList;
import com.github.franckyi.ibeeditor.gui.property.base.PropertyCategory;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.franckyi.ibeeditor.IBEEditor.logger;

public abstract class GuiEditor extends GuiScreen {

    protected List<PropertyCategory> categories = new ArrayList<>();
    private int currentCategory;

    private final GuiScreen parentScreen;

    private GuiCategoryList guiCategories;
    private GuiPropertyList guiProperties;

    private GuiButton cancel, apply, done;

    protected GuiEditor(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    protected GuiEditor() {
        this(null);
    }

    protected void setCategories(List<PropertyCategory> categories) {
        this.categories = categories;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == cancel) {
            mc.displayGuiScreen(parentScreen);
        } else {
            apply();
            if (button == done) {
                mc.displayGuiScreen(parentScreen);
            }
        }
    }

    protected void apply() {
        logger.info("Applying...");
        categories.forEach(PropertyCategory::apply);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return IBEConfiguration.pauseGame;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground(0);
        guiCategories.drawScreen(mouseX, mouseY, partialTicks);
        if(guiProperties != null) guiProperties.drawScreen(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        guiCategories.handleMouseInput();
        if(guiProperties != null) guiProperties.handleMouseInput();
    }

    @Override
    public void initGui() {
        buttonList.add(cancel = new GuiButton(0, width / 2 - 85, height - 30, 50, 20, "§4Cancel"));
        buttonList.add(apply = new GuiButton(1, width / 2 - 25, height - 30, 50, 20, "§2Apply"));
        buttonList.add(done = new GuiButton(2, width / 2 + 35, height - 30, 50, 20, "§2Done"));
        refreshCategories();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if(guiProperties != null) guiProperties.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        guiCategories.mouseClicked(mouseX, mouseY, mouseButton);
        if(guiProperties != null) guiProperties.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if(guiProperties != null) guiProperties.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void updateScreen() {
        if(guiProperties != null) guiProperties.updateScreen();
    }

    public void refreshCategories() {
        guiCategories = new GuiCategoryList(this, mc, width / 3 - 20, height - 60, 20, height - 40, categories, currentCategory);
        guiCategories.setSlotXBoundsFromLeft(10);
        guiCategories.registerScrollButtons(7, 8);
        selectCategory(currentCategory);
    }

    public void selectCategory(int index) {
        currentCategory = index;
        if (index >= 0 && index < categories.size()) {
            guiProperties = categories.get(index).getGuiFactory().create(this, mc, categories.get(index).getProperties());
            guiProperties.setSlotXBoundsFromLeft(width / 3 + 10);
            guiProperties.registerScrollButtons(7, 8);
        }
    }
}
