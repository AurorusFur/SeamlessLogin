package org.aurorus.seamlesslogin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.aurorus.seamlesslogin.screen.PasswordManagerScreen;

public class ClientEventHandler {

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) return;

        while (KeyBindings.OPEN_PASSWORD_MANAGER.consumeClick()) {
            mc.setScreen(new PasswordManagerScreen(null));
        }
    }

    @SubscribeEvent
    public void onScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof JoinMultiplayerScreen screen)) return;

        int btnWidth = 130;
        int x = screen.width - 4 - btnWidth;
        int y = screen.height - 28;

        event.addListener(Button.builder(
                Component.translatable("screen.seamlesslogin.title_short"),
                btn -> Minecraft.getInstance().setScreen(new PasswordManagerScreen(screen))
        ).bounds(x, y, btnWidth, 20).build());
    }
}
