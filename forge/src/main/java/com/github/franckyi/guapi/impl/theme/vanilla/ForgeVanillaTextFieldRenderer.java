package com.github.franckyi.guapi.impl.theme.vanilla;

import com.github.franckyi.guapi.api.node.TextField;
import com.github.franckyi.guapi.api.theme.vanilla.ForgeVanillaDelegateRenderer;
import com.github.franckyi.minecraft.api.client.render.Matrices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;

import java.util.Objects;

public class ForgeVanillaTextFieldRenderer extends TextFieldWidget implements ForgeVanillaDelegateRenderer {
    private final TextField node;

    public ForgeVanillaTextFieldRenderer(TextField node) {
        super(Minecraft.getInstance().font, node.getX(), node.getY(), node.getWidth(), node.getHeight(), node.getLabel().get());
        this.node = node;
        initLabeled(node, this);
        setMaxLength(node.getMaxLength());
        setValue(node.getText());
        moveCursorToStart(); // fix in order to render text
        setFocused(node.isFocused());
        setResponder(node::setText);
        node.maxLengthProperty().addListener(this::setMaxLength);
        node.textProperty().addListener(newVal -> {
            int cursor = getCursorPosition();
            setValue(newVal);
            setCursorPosition(cursor);
        });
        node.focusedProperty().addListener(this::setFocused);
        node.validatorProperty().addListener(this::updateValidator);
        node.validationForcedProperty().addListener(this::updateValidator);
        node.textRendererProperty().addListener(this::updateRenderer);
        node.cursorPositionProperty().addListener(super::setCursorPosition);
        node.highlightPositionProperty().addListener(super::setHighlightPos);
        updateValidator();
        updateRenderer();
    }

    private void updateValidator() {
        if (node.isValidationForced()) {
            if (node.getValidator() == null) {
                setFilter(Objects::nonNull);
            } else {
                setFilter(node.getValidator());
            }
        } else {
            setFilter(Objects::nonNull);
        }
    }

    private void updateRenderer() {
        if (node.getTextRenderer() == null) {
            setFormatter((string, integer) -> IReorderingProcessor.forward(string, Style.EMPTY));
        } else {
            setFormatter((string, integer) -> ((ITextComponent) node.getTextRenderer().render(string, integer).get()).getVisualOrderText());
        }
        moveCursorToStart(); // fix in order to render text
    }

    @Override
    public void render(Matrices matrices, int mouseX, int mouseY, float partialTicks) {
        ForgeVanillaDelegateRenderer.super.render(matrices, mouseX, mouseY, partialTicks);

    }

    @Override
    public void setCursorPosition(int value) {
        super.setCursorPosition(value);
        node.setCursorPosition(getCursorPosition());
    }

    @Override
    public void setHighlightPos(int value) {
        super.setHighlightPos(value);
        node.setHighlightPosition(MathHelper.clamp(value, 0, getValue().length()));
    }
}
