package org.aurorus.seamlesslogin.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.aurorus.seamlesslogin.Config;
import org.aurorus.seamlesslogin.client.SpriteIconButton;
import org.aurorus.seamlesslogin.password.PasswordEntry;
import org.aurorus.seamlesslogin.password.PasswordGenerator;
import org.aurorus.seamlesslogin.password.PasswordManager;

public class AddEditPasswordScreen extends Screen {
    private final PasswordManagerScreen parent;
    private final PasswordEntry existing;
    private final String prefillServer;

    private EditBox nameField;
    private EditBox serverField;
    private EditBox passwordField;
    private boolean autoLogin = true;
    private boolean showPassword = false;

    // Preserved across re-init (e.g. returning from confirm dialog)
    private String savedName;
    private String savedServer;
    private String savedPassword;

    private static final int FIELD_WIDTH = 200;
    private static final int FIELD_HEIGHT = 20;
    private static final int ROW = 30;

    private static final ResourceLocation ICON_SHOW     = ResourceLocation.fromNamespaceAndPath("seamlesslogin", "show_password");
    private static final ResourceLocation ICON_HIDE     = ResourceLocation.fromNamespaceAndPath("seamlesslogin", "hide_password");
    private static final ResourceLocation ICON_GENERATE = ResourceLocation.fromNamespaceAndPath("seamlesslogin", "generate_password");
    private static final ResourceLocation ICON_COPY     = ResourceLocation.fromNamespaceAndPath("seamlesslogin", "copy_password");

    private SpriteIconButton showHideButton;

    private Component feedbackMessage;
    private long feedbackUntil;

    public AddEditPasswordScreen(PasswordManagerScreen parent, PasswordEntry existing) {
        this(parent, existing, null);
    }

    public AddEditPasswordScreen(PasswordManagerScreen parent, PasswordEntry existing, String prefillServer) {
        super(existing == null
                ? Component.translatable("screen.seamlesslogin.add_title")
                : Component.translatable("screen.seamlesslogin.edit_title"));
        this.parent = parent;
        this.existing = existing;
        this.prefillServer = prefillServer;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int y = height / 2 - 75;

        // Server name field
        nameField = new EditBox(font, cx - FIELD_WIDTH / 2, y, FIELD_WIDTH, FIELD_HEIGHT,
                Component.translatable("screen.seamlesslogin.name"));
        nameField.setMaxLength(64);
        nameField.setHint(Component.translatable("screen.seamlesslogin.name_hint"));
        if (savedName != null) nameField.setValue(savedName);
        else if (existing != null && existing.name != null) nameField.setValue(existing.name);
        addRenderableWidget(nameField);

        // Server address field
        serverField = new EditBox(font, cx - FIELD_WIDTH / 2, y + ROW, FIELD_WIDTH, FIELD_HEIGHT,
                Component.translatable("screen.seamlesslogin.server"));
        serverField.setMaxLength(256);
        serverField.setHint(Component.translatable("screen.seamlesslogin.server_hint"));
        if (existing != null) {
            serverField.setValue(existing.server);
            serverField.setEditable(false);
        } else if (savedServer != null) {
            serverField.setValue(savedServer);
        } else if (prefillServer != null) {
            serverField.setValue(prefillServer);
        }
        addRenderableWidget(serverField);

        // Password field
        passwordField = new EditBox(font, cx - FIELD_WIDTH / 2, y + ROW * 2, FIELD_WIDTH, FIELD_HEIGHT,
                Component.translatable("screen.seamlesslogin.password"));
        passwordField.setMaxLength(128);
        passwordField.setHint(Component.translatable("screen.seamlesslogin.password_hint"));
        if (savedPassword != null) {
            passwordField.setValue(savedPassword);
        } else if (existing != null) {
            PasswordManager.getInstance().getPassword(existing.server).ifPresent(passwordField::setValue);
            autoLogin = existing.autoLogin;
        }
        updatePasswordFormatter();
        addRenderableWidget(passwordField);

        // Show/hide password button
        showHideButton = new SpriteIconButton(
                cx + FIELD_WIDTH / 2 + 4, y + ROW * 2,
                showPassword ? ICON_HIDE : ICON_SHOW,
                Component.translatable(showPassword ? "screen.seamlesslogin.hide_password" : "screen.seamlesslogin.show_password"),
                () -> {
                    showPassword = !showPassword;
                    updatePasswordFormatter();
                    showHideButton.setIcon(showPassword ? ICON_HIDE : ICON_SHOW);
                    Component label = Component.translatable(showPassword ? "screen.seamlesslogin.hide_password" : "screen.seamlesslogin.show_password");
                    showHideButton.setMessage(label);
                    showHideButton.setTooltip(Tooltip.create(label));
                });
        showHideButton.setTooltip(Tooltip.create(Component.translatable(
                showPassword ? "screen.seamlesslogin.hide_password" : "screen.seamlesslogin.show_password")));
        addRenderableWidget(showHideButton);

        // Generate password button
        SpriteIconButton generateButton = new SpriteIconButton(
                cx + FIELD_WIDTH / 2 + 28, y + ROW * 2,
                ICON_GENERATE,
                Component.translatable("screen.seamlesslogin.generate_password"),
                () -> {
                    passwordField.setValue(PasswordGenerator.generate());
                    showFeedback("screen.seamlesslogin.feedback_generated");
                });
        generateButton.setTooltip(Tooltip.create(Component.translatable("screen.seamlesslogin.generate_password")));
        addRenderableWidget(generateButton);

        // Copy password button
        SpriteIconButton copyButton = new SpriteIconButton(
                cx + FIELD_WIDTH / 2 + 52, y + ROW * 2,
                ICON_COPY,
                Component.translatable("screen.seamlesslogin.copy_password"),
                () -> {
                    minecraft.keyboardHandler.setClipboard(passwordField.getValue());
                    showFeedback("screen.seamlesslogin.feedback_copied");
                });
        copyButton.setTooltip(Tooltip.create(Component.translatable("screen.seamlesslogin.copy_password")));
        addRenderableWidget(copyButton);

        // Auto-login toggle
        addRenderableWidget(Button.builder(
                getAutoLoginLabel(),
                btn -> {
                    autoLogin = !autoLogin;
                    btn.setMessage(getAutoLoginLabel());
                }
        ).bounds(cx - FIELD_WIDTH / 2, y + ROW * 3, FIELD_WIDTH, FIELD_HEIGHT).build());

        // Save / Cancel
        addRenderableWidget(Button.builder(
                Component.translatable("screen.seamlesslogin.save"),
                btn -> save()
        ).bounds(cx - 105, y + ROW * 4, 100, 20).build());

        addRenderableWidget(Button.builder(
                Component.translatable("gui.cancel"),
                btn -> minecraft.setScreen(parent)
        ).bounds(cx + 5, y + ROW * 4, 100, 20).build());
    }

    private void updatePasswordFormatter() {
        if (showPassword) {
            passwordField.setFormatter((text, pos) ->
                    FormattedCharSequence.forward(text, Style.EMPTY));
        } else {
            passwordField.setFormatter((text, pos) ->
                    FormattedCharSequence.forward("*".repeat(text.length()), Style.EMPTY));
        }
    }

    private void showFeedback(String key) {
        feedbackMessage = Component.translatable(key);
        feedbackUntil   = System.currentTimeMillis() + 2000;
    }

    private Component getAutoLoginLabel() {
        return autoLogin
                ? Component.translatable("screen.seamlesslogin.autologin_on")
                : Component.translatable("screen.seamlesslogin.autologin_off");
    }

    private void save() {
        String name = nameField.getValue().trim();
        String server = serverField.getValue().trim();
        String password = passwordField.getValue();

        if (server.isEmpty()) {
            serverField.setHint(Component.translatable("screen.seamlesslogin.server_required"));
            return;
        }
        if (existing == null && PasswordManager.getInstance().hasPassword(server)) {
            savedName = name;
            savedServer = server;
            savedPassword = password;
            minecraft.setScreen(new ConfirmScreen(
                    confirmed -> {
                        if (confirmed) {
                            PasswordManager.getInstance().savePassword(server, name, password, autoLogin);
                            parent.refresh();
                            minecraft.setScreen(parent);
                        } else {
                            minecraft.setScreen(this);
                        }
                    },
                    Component.translatable("screen.seamlesslogin.overwrite_title"),
                    Component.translatable("screen.seamlesslogin.overwrite_message", server)
            ));
            return;
        }
        if (password.isEmpty()) {
            passwordField.setHint(Component.translatable("screen.seamlesslogin.password_required"));
            return;
        }

        if (!Config.skipPasswordReuseWarning && PasswordManager.getInstance().isPasswordReused(server, password)) {
            minecraft.setScreen(new PasswordReuseWarningScreen(this, () -> {
                PasswordManager.getInstance().savePassword(server, name, password, autoLogin);
                parent.refresh();
                minecraft.setScreen(parent);
            }));
            return;
        }

        PasswordManager.getInstance().savePassword(server, name, password, autoLogin);
        parent.refresh();
        minecraft.setScreen(parent);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        int cx = width / 2;
        int y = height / 2 - 75;

        graphics.drawCenteredString(font, title, cx, y - 24, 0xFFFFFFFF);

        graphics.drawString(font, Component.translatable("screen.seamlesslogin.name"),
                cx - FIELD_WIDTH / 2, y - 9, 0xFFA0A0A0, false);
        graphics.drawString(font, Component.translatable("screen.seamlesslogin.server"),
                cx - FIELD_WIDTH / 2, y + ROW - 9, 0xFFA0A0A0, false);
        graphics.drawString(font, Component.translatable("screen.seamlesslogin.password"),
                cx - FIELD_WIDTH / 2, y + ROW * 2 - 9, 0xFFA0A0A0, false);

        if (feedbackMessage != null) {
            long remaining = feedbackUntil - System.currentTimeMillis();
            if (remaining > 0) {
                int alpha = remaining < 800 ? (int) (remaining * 255 / 800) : 255;
                graphics.drawCenteredString(font, feedbackMessage, cx, y + ROW * 4 + 26,
                        (alpha << 24) | 0x0055FF55);
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
