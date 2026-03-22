package org.aurorus.seamlesslogin.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.aurorus.seamlesslogin.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

public class PatternEditScreen extends Screen {
    private final PatternConfigScreen parent;
    private final String existing;
    private final int existingIndex;

    private EditBox patternField;
    private Component errorMessage;

    private static final int FIELD_WIDTH = 300;
    private static final int FIELD_HEIGHT = 20;

    public PatternEditScreen(PatternConfigScreen parent, String existing, int existingIndex) {
        super(existingIndex < 0
                ? Component.translatable("screen.seamlesslogin.pattern_add_title")
                : Component.translatable("screen.seamlesslogin.pattern_edit_title"));
        this.parent = parent;
        this.existing = existing;
        this.existingIndex = existingIndex;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int y = height / 2 - 20;

        patternField = new EditBox(font, cx - FIELD_WIDTH / 2, y, FIELD_WIDTH, FIELD_HEIGHT,
                Component.translatable("screen.seamlesslogin.pattern_label"));
        patternField.setMaxLength(512);
        patternField.setHint(Component.translatable("screen.seamlesslogin.pattern_hint"));
        if (existing != null) patternField.setValue(existing);
        addRenderableWidget(patternField);

        addRenderableWidget(Button.builder(
                Component.translatable("screen.seamlesslogin.save"),
                btn -> save()
        ).bounds(cx - 105, y + 30, 100, 20).build());

        addRenderableWidget(Button.builder(
                Component.translatable("gui.cancel"),
                btn -> minecraft.setScreen(parent)
        ).bounds(cx + 5, y + 30, 100, 20).build());
    }

    private void save() {
        String pattern = patternField.getValue().trim();

        if (pattern.isEmpty()) {
            errorMessage = Component.translatable("screen.seamlesslogin.pattern_required");
            return;
        }

        try {
            java.util.regex.Pattern.compile(pattern);
        } catch (PatternSyntaxException e) {
            errorMessage = Component.translatable("screen.seamlesslogin.pattern_invalid");
            return;
        }

        List<String> patterns = new ArrayList<>(Config.loginPatterns);
        if (existingIndex >= 0) {
            patterns.set(existingIndex, pattern);
        } else {
            patterns.add(pattern);
        }
        Config.setLoginPatterns(patterns);
        parent.refresh();
        minecraft.setScreen(parent);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        int cx = width / 2;
        int y = height / 2 - 20;

        graphics.drawCenteredString(font, title, cx, y - 24, 0xFFFFFFFF);

        graphics.drawString(font, Component.translatable("screen.seamlesslogin.pattern_label"),
                cx - FIELD_WIDTH / 2, y - 9, 0xFFA0A0A0, false);

        if (errorMessage != null) {
            graphics.drawCenteredString(font, errorMessage, cx, y + 56, 0xFFFF5555);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
