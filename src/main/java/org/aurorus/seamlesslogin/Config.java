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

    private static final ModConfigSpec.BooleanValue USE_PASSPHRASE = BUILDER
            .comment("Generate passwords as passphrases (e.g. correct-horse-battery-staple)")
            .define("usePassphrase", false);

    private static final ModConfigSpec.IntValue PASSWORD_LENGTH = BUILDER
            .comment("Length of generated passwords when not using passphrase mode")
            .defineInRange("passwordLength", 16, 8, 64);

    private static final ModConfigSpec.IntValue PASSPHRASE_WORD_COUNT = BUILDER
            .comment("Number of words in generated passphrases")
            .defineInRange("passphraseWordCount", 4, 3, 8);

    private static final ModConfigSpec.BooleanValue USE_CAPITAL_LETTERS = BUILDER
            .comment("Include capital letters in generated passwords")
            .define("useCapitalLetters", true);

    private static final ModConfigSpec.BooleanValue USE_NUMBERS = BUILDER
            .comment("Include numbers in generated passwords")
            .define("useNumbers", true);

    private static final ModConfigSpec.BooleanValue USE_SPECIAL_CHARS = BUILDER
            .comment("Include special characters (!@#$%^&*-_=+) in generated passwords")
            .define("useSpecialChars", false);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean autoLoginEnabled;
    public static String loginCommandFormat;
    public static int loginDelayTicks;
    public static List<? extends String> loginPatterns;

    public static boolean usePassphrase;
    public static int passwordLength;
    public static int passphraseWordCount;
    public static boolean useCapitalLetters;
    public static boolean useNumbers;
    public static boolean useSpecialChars;

    public static void setLoginPatterns(List<String> patterns) {
        LOGIN_PATTERNS.set(patterns);
        SPEC.save();
    }

    public static void setPasswordGenConfig(boolean passphrase, int length, int wordCount,
                                            boolean caps, boolean numbers, boolean special) {
        USE_PASSPHRASE.set(passphrase);
        PASSWORD_LENGTH.set(length);
        PASSPHRASE_WORD_COUNT.set(wordCount);
        USE_CAPITAL_LETTERS.set(caps);
        USE_NUMBERS.set(numbers);
        USE_SPECIAL_CHARS.set(special);
        SPEC.save();
    }

    static void onLoad(final ModConfigEvent event) {
        autoLoginEnabled = AUTO_LOGIN_ENABLED.get();
        loginCommandFormat = LOGIN_COMMAND_FORMAT.get();
        loginDelayTicks = LOGIN_DELAY_TICKS.get();
        loginPatterns = LOGIN_PATTERNS.get();

        usePassphrase = USE_PASSPHRASE.get();
        passwordLength = PASSWORD_LENGTH.get();
        passphraseWordCount = PASSPHRASE_WORD_COUNT.get();
        useCapitalLetters = USE_CAPITAL_LETTERS.get();
        useNumbers = USE_NUMBERS.get();
        useSpecialChars = USE_SPECIAL_CHARS.get();
    }
}
