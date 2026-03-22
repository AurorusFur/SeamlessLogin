package org.aurorus.seamlesslogin.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.aurorus.seamlesslogin.Config;

import java.util.ArrayList;
import java.util.List;

public class PatternConfigScreen extends Screen {
    private final Screen previousScreen;
    private PatternList patternList;

    public PatternConfigScreen(Screen previousScreen) {
        super(Component.translatable("screen.seamlesslogin.patterns_title"));
        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        patternList = new PatternList(minecraft, width, height - 64, 32, 36);
        addRenderableWidget(patternList);

        addRenderableWidget(Button.builder(
                Component.translatable("screen.seamlesslogin.add"),
                btn -> minecraft.setScreen(new PatternEditScreen(this, null, -1))
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

        if (patternList.children().isEmpty()) {
            graphics.drawCenteredString(font,
                    Component.translatable("screen.seamlesslogin.no_patterns"),
                    width / 2, height / 2 - 4, 0xFFAAAAAA);
        }
    }

    public void refresh() {
        init(minecraft, width, height);
    }

    void confirmDelete(int index, String pattern) {
        minecraft.setScreen(new ConfirmScreen(
                confirmed -> {
                    if (confirmed) {
                        List<String> patterns = new ArrayList<>(Config.loginPatterns);
                        patterns.remove(index);
                        Config.setLoginPatterns(patterns);
                    }
                    minecraft.setScreen(this);
                },
                Component.translatable("screen.seamlesslogin.pattern_delete_title"),
                Component.translatable("screen.seamlesslogin.pattern_delete_message", pattern)
        ));
    }

    // ---- Inner list class ----

    class PatternList extends ObjectSelectionList<PatternList.Entry> {
        PatternList(Minecraft mc, int width, int height, int top, int itemHeight) {
            super(mc, width, height, top, itemHeight);
            refresh();
        }

        void refresh() {
            clearEntries();
            List<? extends String> patterns = Config.loginPatterns;
            for (int i = 0; i < patterns.size(); i++) {
                addEntry(new Entry(i, patterns.get(i)));
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
            private final int index;
            private final String pattern;
            private final Button editButton;
            private final Button deleteButton;

            Entry(int index, String pattern) {
                this.index = index;
                this.pattern = pattern;

                this.editButton = Button.builder(
                        Component.translatable("screen.seamlesslogin.edit"),
                        btn -> minecraft.setScreen(new PatternEditScreen(PatternConfigScreen.this, pattern, index))
                ).size(50, 20).build();

                this.deleteButton = Button.builder(
                        Component.translatable("screen.seamlesslogin.delete"),
                        btn -> PatternConfigScreen.this.confirmDelete(index, pattern)
                ).size(50, 20).build();
            }

            @Override
            public void render(GuiGraphics graphics, int index, int top, int left, int width, int height,
                               int mouseX, int mouseY, boolean isHovered, float partialTick) {
                int btnY = top + (height - 20) / 2;
                editButton.setPosition(left + width - 108, btnY);
                deleteButton.setPosition(left + width - 54, btnY);

                int maxTextWidth = width - 116;
                String display = minecraft.font.plainSubstrByWidth(pattern, maxTextWidth);
                if (display.length() < pattern.length()) display += "…";

                graphics.drawString(minecraft.font, display, left + 8, top + (height - 8) / 2, 0xFFFFFFFF, false);

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
                return Component.literal(pattern);
            }
        }
    }
}
