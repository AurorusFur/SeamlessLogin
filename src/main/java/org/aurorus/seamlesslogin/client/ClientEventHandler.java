package org.aurorus.seamlesslogin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.aurorus.seamlesslogin.password.PasswordEntry;
import org.aurorus.seamlesslogin.password.PasswordManager;
import org.aurorus.seamlesslogin.screen.AddEditPasswordScreen;
import org.aurorus.seamlesslogin.screen.PasswordManagerScreen;

public class ClientEventHandler {

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) return;

        while (KeyBindings.OPEN_PASSWORD_MANAGER.consumeClick()) {
            PasswordManagerScreen manager = new PasswordManagerScreen(null);
            if (mc.getCurrentServer() != null) {
                String ip = mc.getCurrentServer().ip;
                PasswordEntry existing = PasswordManager.getInstance().getEntries().stream()
                        .filter(e -> e.server.equals(PasswordManager.normalizeServer(ip)))
                        .findFirst().orElse(null);
                if (existing != null) {
                    mc.setScreen(new AddEditPasswordScreen(manager, existing));
                } else {
                    mc.setScreen(new AddEditPasswordScreen(manager, null, ip));
                }
            } else {
                mc.setScreen(manager);
            }
        }
    }

    @SubscribeEvent
    public void onScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof JoinMultiplayerScreen screen)) return;

        int x = screen.width - 4 - 20;
        int y = 4;

        event.addListener(new PasswordManagerIconButton(x, y,
                () -> Minecraft.getInstance().setScreen(new PasswordManagerScreen(screen))));
    }
}
