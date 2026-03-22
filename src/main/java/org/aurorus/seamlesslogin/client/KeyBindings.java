package org.aurorus.seamlesslogin.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final String CATEGORY = "key.categories.seamlesslogin";

    public static final KeyMapping OPEN_PASSWORD_MANAGER = new KeyMapping(
            "key.seamlesslogin.open_password_manager",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_KP_ADD),
            CATEGORY
    );

    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_PASSWORD_MANAGER);
    }
}
