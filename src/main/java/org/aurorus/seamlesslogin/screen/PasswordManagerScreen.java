package org.aurorus.seamlesslogin.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.aurorus.seamlesslogin.password.PasswordEntry;
import org.aurorus.seamlesslogin.password.PasswordManager;

import java.util.List;

public class PasswordManagerScreen extends Screen {
    private final Screen previousScreen;
    private ServerList serverList;

    public PasswordManagerScreen(Screen previousScreen) {
        super(Component.translatable("screen.seamlesslogin.title"));
        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        serverList = new ServerList(minecraft, width, height - 64, 32, 56);
        addRenderableWidget(serverList);

        addRenderableWidget(Button.builder(
                Component.translatable("screen.seamlesslogin.add"),
                btn -> minecraft.setScreen(new AddEditPasswordScreen(this, null))
        ).bounds(width / 2 - 155, height - 28, 150, 20).build());

        addRenderableWidget(Button.builder(
                Component.translatable("gui.done"),
                btn -> minecraft.setScreen(previousScreen)
        ).bounds(width / 2 + 5, height - 28, 150, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, 12, 0xFFFFFFFF);

        if (serverList.children().isEmpty()) {
            graphics.drawCenteredString(font,
                    Component.translatable("screen.seamlesslogin.no_entries"),
                    width / 2, height / 2 - 4, 0xFFAAAAAA);
        }
    }

    public void refresh() {
        init(minecraft, width, height);
    }

    void confirmDelete(PasswordEntry entry) {
        minecraft.setScreen(new ConfirmScreen(
                confirmed -> {
                    if (confirmed) {
                        PasswordManager.getInstance().removePassword(entry.server);
                    }
                    minecraft.setScreen(this);
                },
                Component.translatable("screen.seamlesslogin.delete_confirm_title"),
                Component.translatable("screen.seamlesslogin.delete_confirm_message", entry.server)
        ));
    }

    // ---- Inner list class ----

    class ServerList extends ObjectSelectionList<ServerList.Entry> {
        ServerList(Minecraft mc, int width, int height, int top, int itemHeight) {
            super(mc, width, height, top, itemHeight);
            refresh();
        }

        void refresh() {
            clearEntries();
            List<PasswordEntry> entries = PasswordManager.getInstance().getEntries();
            for (PasswordEntry entry : entries) {
                addEntry(new Entry(entry));
            }
        }

        @Override
        public int getRowWidth() {
            return Math.min(width - 20, 420);
        }

        @Override
        protected int scrollBarX() {
            return Math.min(width / 2 + getRowWidth() / 2 + 4, width - 6);
        }

        class Entry extends ObjectSelectionList.Entry<Entry> {
            private final PasswordEntry data;
            private final Button editButton;
            private final Button deleteButton;

            Entry(PasswordEntry data) {
                this.data = data;

                this.editButton = Button.builder(
                        Component.translatable("screen.seamlesslogin.edit"),
                        btn -> minecraft.setScreen(new AddEditPasswordScreen(PasswordManagerScreen.this, data))
                ).size(50, 20).build();

                this.deleteButton = Button.builder(
                        Component.translatable("screen.seamlesslogin.delete"),
                        btn -> PasswordManagerScreen.this.confirmDelete(data)
                ).size(50, 20).build();
            }

            @Override
            public void render(GuiGraphics graphics, int index, int top, int left, int width, int height,
                               int mouseX, int mouseY, boolean isHovered, float partialTick) {
                // Position buttons on the right, vertically centered
                int btnY = top + (height - 20) / 2;
                editButton.setPosition(left + width - 108, btnY);
                deleteButton.setPosition(left + width - 54, btnY);

                // Server name — first line, white
                graphics.drawString(minecraft.font, Component.literal(data.displayName()), left + 8, top + 7, 0xFFFFFFFF, false);

                // Server address — second line, grey
                graphics.drawString(minecraft.font, Component.literal(data.server), left + 8, top + 20, 0xFFAAAAAA, false);

                // Auto-login status — third line
                if (data.autoLogin) {
                    graphics.drawString(minecraft.font,
                            Component.translatable("screen.seamlesslogin.autologin_on"),
                            left + 8, top + 33, 0xFF55FF55, false);
                } else {
                    graphics.drawString(minecraft.font,
                            Component.translatable("screen.seamlesslogin.autologin_off"),
                            left + 8, top + 33, 0xFF888888, false);
                }

                // Minecraft-styled buttons
                editButton.render(graphics, mouseX, mouseY, partialTick);
                deleteButton.render(graphics, mouseX, mouseY, partialTick);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (editButton.mouseClicked(mouseX, mouseY, button)) return true;
                if (deleteButton.mouseClicked(mouseX, mouseY, button)) return true;
                return false;
            }

            @Override
            public Component getNarration() {
                return Component.translatable("screen.seamlesslogin.entry_narration",
                        data.server,
                        data.autoLogin
                                ? Component.translatable("screen.seamlesslogin.autologin_on")
                                : Component.translatable("screen.seamlesslogin.autologin_off"));
            }
        }
    }
}
