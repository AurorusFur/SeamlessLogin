package org.aurorus.seamlesslogin;

import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue AUTO_LOGIN_ENABLED = BUILDER
            .comment("Enable automatic login when a login prompt is detected")
            .define("autoLoginEnabled", true);

    private static final ModConfigSpec.ConfigValue<String> LOGIN_COMMAND_FORMAT = BUILDER
            .comment("Command format for login. Use %s as placeholder for the password. Do not include the leading slash.")
            .define("loginCommandFormat", "login %s");

    private static final ModConfigSpec.IntValue LOGIN_DELAY_TICKS = BUILDER
            .comment("Delay in ticks before sending the login command after detecting a prompt (1 tick = 50ms)")
            .defineInRange("loginDelayTicks", 10, 0, 100);

    private static final ModConfigSpec.ConfigValue<List<? extends String>> LOGIN_PATTERNS = BUILDER
            .comment("Regex patterns used to detect login prompts in chat messages (case-insensitive)")
            .defineListAllowEmpty("loginPatterns", List.of(
                    "(?i).*please.*log.?in.*",
                    "(?i).*/login.*password.*",
                    "(?i).*use /login.*",
                    "(?i).*you (need|must) to login.*",
                    "(?i).*please authenticate.*",
                    "(?i).*not logged in.*"
            ), obj -> obj instanceof String);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean autoLoginEnabled;
    public static String loginCommandFormat;
    public static int loginDelayTicks;
    public static List<? extends String> loginPatterns;

    static void onLoad(final ModConfigEvent event) {
        autoLoginEnabled = AUTO_LOGIN_ENABLED.get();
        loginCommandFormat = LOGIN_COMMAND_FORMAT.get();
        loginDelayTicks = LOGIN_DELAY_TICKS.get();
        loginPatterns = LOGIN_PATTERNS.get();
    }
}
