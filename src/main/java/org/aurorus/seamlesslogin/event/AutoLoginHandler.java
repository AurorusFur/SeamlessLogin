package org.aurorus.seamlesslogin.event;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import org.aurorus.seamlesslogin.Config;
import org.aurorus.seamlesslogin.SeamlessLogin;
import org.aurorus.seamlesslogin.password.PasswordManager;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;

public class AutoLoginHandler {
    private long lastLoginAttemptMs = 0;
    private static final long COOLDOWN_MS = 5000;

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (!Config.autoLoginEnabled) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.getCurrentServer() == null) return;

        long now = System.currentTimeMillis();
        if (now - lastLoginAttemptMs < COOLDOWN_MS) return;

        String serverAddress = mc.getCurrentServer().ip;
        PasswordManager pm = PasswordManager.getInstance();

        if (!pm.hasAutoLogin(serverAddress)) return;

        String message = event.getMessage().getString();
        List<? extends String> patterns = Config.loginPatterns;

        for (String patternStr : patterns) {
            try {
                if (Pattern.compile(patternStr, Pattern.DOTALL).matcher(message).matches()) {
                    Optional<String> password = pm.getPassword(serverAddress);
                    if (password.isPresent()) {
                        lastLoginAttemptMs = now;
                        String pwd = password.get();
                        int delayTicks = Config.loginDelayTicks;
                        String command = String.format(Config.loginCommandFormat, pwd);

                        if (delayTicks <= 0) {
                            sendLoginCommand(mc, command);
                        } else {
                            scheduleLoginCommand(mc, command, delayTicks);
                        }
                    }
                    break;
                }
            } catch (Exception e) {
                SeamlessLogin.LOGGER.warn("SeamlessLogin: Invalid login pattern: {}", patternStr);
            }
        }
    }

    private void sendLoginCommand(Minecraft mc, String command) {
        mc.execute(() -> {
            if (mc.getConnection() != null) {
                mc.getConnection().sendCommand(command);
                SeamlessLogin.LOGGER.debug("SeamlessLogin: Sent login command");
            }
        });
    }

    private void scheduleLoginCommand(Minecraft mc, String command, int delayTicks) {
        new Thread(() -> {
            try {
                Thread.sleep(delayTicks * 50L);
            } catch (InterruptedException ignored) {}
            sendLoginCommand(mc, command);
        }, "SeamlessLogin-AutoLogin").start();
    }
}
