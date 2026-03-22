package org.aurorus.seamlesslogin.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.aurorus.seamlesslogin.Config;

public class PasswordGenConfigScreen extends Screen {
    private final Screen previousScreen;

    // Local state
    private boolean passphrase;
    private int length;
    private int wordCount;
    private boolean caps;
    private boolean numbers;
    private boolean special;

    private EditBox lengthField;
    private EditBox wordCountField;
    private Component errorMessage;

    private static final int FIELD_WIDTH  = 200;
    private static final int FIELD_HEIGHT = 20;
    private static final int ROW          = 26;

    public PasswordGenConfigScreen(Screen previousScreen) {
        super(Component.translatable("screen.seamlesslogin.pwgen_title"));
        this.previousScreen = previousScreen;
        // Load current config values as initial state
        this.passphrase = Config.usePassphrase;
        this.length     = Config.passwordLength;
        this.wordCount  = Config.passphraseWordCount;
        this.caps       = Config.useCapitalLetters;
        this.numbers    = Config.useNumbers;
        this.special    = Config.useSpecialChars;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int y  = height / 2 - (ROW * 5) / 2;

        // Passphrase toggle
        addRenderableWidget(Button.builder(
                passphraseLabel(),
                btn -> {
                    passphrase = !passphrase;
                    btn.setMessage(passphraseLabel());
                    updateFieldStates();
                }
        ).bounds(cx - FIELD_WIDTH / 2, y, FIELD_WIDTH, FIELD_HEIGHT).build());

        // Password length
        lengthField = new EditBox(font, cx + 4, y + ROW, 60, FIELD_HEIGHT,
                Component.translatable("screen.seamlesslogin.pwgen_length"));
        lengthField.setMaxLength(2);
        lengthField.setValue(String.valueOf(length));
        lengthField.setFilter(s -> s.isEmpty() || s.matches("\\d+"));
        addRenderableWidget(lengthField);

        // Passphrase word count
        wordCountField = new EditBox(font, cx + 4, y + ROW * 2, 60, FIELD_HEIGHT,
                Component.translatable("screen.seamlesslogin.pwgen_words"));
        wordCountField.setMaxLength(1);
        wordCountField.setValue(String.valueOf(wordCount));
        wordCountField.setFilter(s -> s.isEmpty() || s.matches("\\d+"));
        addRenderableWidget(wordCountField);

        // Capital letters toggle
        addRenderableWidget(Button.builder(
                toggleLabel("screen.seamlesslogin.pwgen_caps", caps),
                btn -> {
                    caps = !caps;
                    btn.setMessage(toggleLabel("screen.seamlesslogin.pwgen_caps", caps));
                }
        ).bounds(cx - FIELD_WIDTH / 2, y + ROW * 3, FIELD_WIDTH, FIELD_HEIGHT).build());

        // Numbers toggle
        addRenderableWidget(Button.builder(
                toggleLabel("screen.seamlesslogin.pwgen_numbers", numbers),
                btn -> {
                    numbers = !numbers;
                    btn.setMessage(toggleLabel("screen.seamlesslogin.pwgen_numbers", numbers));
                }
        ).bounds(cx - FIELD_WIDTH / 2, y + ROW * 4, FIELD_WIDTH, FIELD_HEIGHT).build());

        // Special characters toggle
        addRenderableWidget(Button.builder(
                toggleLabel("screen.seamlesslogin.pwgen_special", special),
                btn -> {
                    special = !special;
                    btn.setMessage(toggleLabel("screen.seamlesslogin.pwgen_special", special));
                }
        ).bounds(cx - FIELD_WIDTH / 2, y + ROW * 5, FIELD_WIDTH, FIELD_HEIGHT).build());

        // Save / Cancel
        addRenderableWidget(Button.builder(
                Component.translatable("screen.seamlesslogin.save"),
                btn -> save()
        ).bounds(cx - 105, y + ROW * 6 + 8, 100, 20).build());

        addRenderableWidget(Button.builder(
                Component.translatable("gui.cancel"),
                btn -> minecraft.setScreen(previousScreen)
        ).bounds(cx + 5, y + ROW * 6 + 8, 100, 20).build());

        updateFieldStates();
    }

    private void updateFieldStates() {
        if (lengthField   != null) lengthField.setEditable(!passphrase);
        if (wordCountField != null) wordCountField.setEditable(passphrase);
    }

    private Component passphraseLabel() {
        return Component.translatable(passphrase
                ? "screen.seamlesslogin.pwgen_passphrase_on"
                : "screen.seamlesslogin.pwgen_passphrase_off");
    }

    private Component toggleLabel(String key, boolean on) {
        return Component.translatable(key).append(": ").append(
                Component.translatable(on ? "screen.seamlesslogin.on" : "screen.seamlesslogin.off"));
    }

    private void save() {
        String lenStr  = lengthField.getValue().trim();
        String wcStr   = wordCountField.getValue().trim();

        int newLength;
        int newWordCount;

        try {
            newLength = Integer.parseInt(lenStr);
            if (newLength < 8 || newLength > 64) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            errorMessage = Component.translatable("screen.seamlesslogin.pwgen_length_invalid");
            return;
        }

        try {
            newWordCount = Integer.parseInt(wcStr);
            if (newWordCount < 3 || newWordCount > 8) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            errorMessage = Component.translatable("screen.seamlesslogin.pwgen_words_invalid");
            return;
        }

        Config.setPasswordGenConfig(passphrase, newLength, newWordCount, caps, numbers, special);
        minecraft.setScreen(previousScreen);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        int cx = width / 2;
        int y  = height / 2 - (ROW * 5) / 2;

        graphics.drawCenteredString(font, title, cx, y - 24, 0xFFFFFFFF);

        // Labels for the EditBox rows
        int labelColor = 0xFFA0A0A0;
        graphics.drawString(font, Component.translatable("screen.seamlesslogin.pwgen_length"),
                cx - FIELD_WIDTH / 2, y + ROW + 4, passphrase ? 0xFF555555 : labelColor, false);
        graphics.drawString(font, Component.translatable("screen.seamlesslogin.pwgen_words"),
                cx - FIELD_WIDTH / 2, y + ROW * 2 + 4, passphrase ? labelColor : 0xFF555555, false);

        if (errorMessage != null) {
            graphics.drawCenteredString(font, errorMessage, cx, y + ROW * 6 + 32, 0xFFFF5555);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
