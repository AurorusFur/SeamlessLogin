package org.aurorus.seamlesslogin;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.aurorus.seamlesslogin.client.ClientEventHandler;
import org.aurorus.seamlesslogin.client.KeyBindings;
import org.aurorus.seamlesslogin.event.AutoLoginHandler;
import org.aurorus.seamlesslogin.password.PasswordManager;
import org.aurorus.seamlesslogin.screen.SeamlessLoginConfigScreen;
import org.slf4j.Logger;

@Mod(SeamlessLogin.MODID)
public class SeamlessLogin {
    public static final String MODID = "seamlesslogin";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SeamlessLogin(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
        modEventBus.addListener(Config::onLoad);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(KeyBindings::onRegisterKeyMappings);
            NeoForge.EVENT_BUS.register(new AutoLoginHandler());
            NeoForge.EVENT_BUS.register(new ClientEventHandler());
            PasswordManager.getInstance();
            modContainer.registerExtensionPoint(IConfigScreenFactory.class,
                    (container, screen) -> new SeamlessLoginConfigScreen(screen));
        }
    }
}
