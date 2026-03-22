package org.aurorus.seamlesslogin.password;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.neoforged.fml.loading.FMLPaths;
import org.aurorus.seamlesslogin.SeamlessLogin;
import org.aurorus.seamlesslogin.encryption.EncryptionUtil;

import javax.crypto.SecretKey;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PasswordManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static PasswordManager instance;

    private final Path configDir;
    private final Path passwordFile;
    private final Path keyFile;

    private SecretKey encryptionKey;
    private List<PasswordEntry> entries = new ArrayList<>();

    public static PasswordManager getInstance() {
        if (instance == null) {
            instance = new PasswordManager();
        }
        return instance;
    }

    private PasswordManager() {
        configDir = FMLPaths.CONFIGDIR.get().resolve("seamlesslogin");
        passwordFile = configDir.resolve("passwords.json");
        keyFile = configDir.resolve("key.dat");

        try {
            Files.createDirectories(configDir);
            loadOrGenerateKey();
            loadPasswords();
        } catch (Exception e) {
            SeamlessLogin.LOGGER.error("SeamlessLogin: Failed to initialize PasswordManager", e);
        }
    }

    private void loadOrGenerateKey() throws Exception {
        if (Files.exists(keyFile)) {
            String base64 = Files.readString(keyFile).trim();
            encryptionKey = EncryptionUtil.keyFromBase64(base64);
        } else {
            encryptionKey = EncryptionUtil.generateKey();
            Files.writeString(keyFile, EncryptionUtil.keyToBase64(encryptionKey));
            SeamlessLogin.LOGGER.info("SeamlessLogin: Generated new encryption key at {}", keyFile);
        }
    }

    private void loadPasswords() {
        if (!Files.exists(passwordFile)) {
            entries = new ArrayList<>();
            return;
        }
        try {
            String json = Files.readString(passwordFile);
            Type listType = new TypeToken<List<PasswordEntry>>() {}.getType();
            List<PasswordEntry> loaded = GSON.fromJson(json, listType);
            entries = loaded != null ? loaded : new ArrayList<>();
        } catch (Exception e) {
            SeamlessLogin.LOGGER.error("SeamlessLogin: Failed to load passwords", e);
            entries = new ArrayList<>();
        }
    }

    private void savePasswords() {
        try {
            Files.writeString(passwordFile, GSON.toJson(entries));
        } catch (Exception e) {
            SeamlessLogin.LOGGER.error("SeamlessLogin: Failed to save passwords", e);
        }
    }

    public void savePassword(String server, String name, String plainPassword, boolean autoLogin) {
        try {
            String key = normalizeServer(server);
            String encrypted = EncryptionUtil.encrypt(plainPassword, encryptionKey);

            Optional<PasswordEntry> existing = entries.stream()
                    .filter(e -> e.server.equals(key))
                    .findFirst();

            if (existing.isPresent()) {
                existing.get().name = name;
                existing.get().encryptedPassword = encrypted;
                existing.get().autoLogin = autoLogin;
            } else {
                entries.add(new PasswordEntry(name, key, encrypted, autoLogin));
            }
            savePasswords();
        } catch (Exception e) {
            SeamlessLogin.LOGGER.error("SeamlessLogin: Failed to save password for {}", server, e);
        }
    }

    public Optional<String> getPassword(String server) {
        String key = normalizeServer(server);
        return entries.stream()
                .filter(e -> e.server.equals(key))
                .findFirst()
                .flatMap(e -> {
                    try {
                        return Optional.of(EncryptionUtil.decrypt(e.encryptedPassword, encryptionKey));
                    } catch (Exception ex) {
                        SeamlessLogin.LOGGER.error("SeamlessLogin: Failed to decrypt password for {}", server, ex);
                        return Optional.empty();
                    }
                });
    }

    public boolean hasAutoLogin(String server) {
        String key = normalizeServer(server);
        return entries.stream()
                .filter(e -> e.server.equals(key))
                .findFirst()
                .map(e -> e.autoLogin)
                .orElse(false);
    }

    public boolean hasPassword(String server) {
        String key = normalizeServer(server);
        return entries.stream().anyMatch(e -> e.server.equals(key));
    }

    public void removePassword(String server) {
        String key = normalizeServer(server);
        entries.removeIf(e -> e.server.equals(key));
        savePasswords();
    }

    public List<PasswordEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    /** Returns true if {@code plainPassword} is already stored for a different server. */
    public boolean isPasswordReused(String server, String plainPassword) {
        String key = normalizeServer(server);
        for (PasswordEntry entry : entries) {
            if (entry.server.equals(key)) continue;
            try {
                if (EncryptionUtil.decrypt(entry.encryptedPassword, encryptionKey).equals(plainPassword)) {
                    return true;
                }
            } catch (Exception ignored) {}
        }
        return false;
    }

    /**
     * Normalises a server address to the canonical form "host:port" (lowercase).
     *
     * Rules:
     *  - Plain hostname / IPv4 with no port  →  append ":25565"
     *  - hostname:port or IPv4:port          →  keep as-is
     *  - Bare IPv6 (e.g. "::1", "fe80::1")  →  wrap and append default port → "[::1]:25565"
     *  - Bracketed IPv6 with port "[::1]:port" →  keep as-is
     */
    public static String normalizeServer(String server) {
        if (server == null) return "";
        server = server.trim().toLowerCase();

        if (server.startsWith("[")) {
            // Already in bracketed IPv6 form: [addr]:port  or  [addr]  (no port)
            int closeBracket = server.indexOf(']');
            if (closeBracket == -1) {
                // Malformed – treat the whole string as host, append default port
                return server + ":25565";
            }
            // Check whether ":port" follows the closing bracket
            if (closeBracket + 1 >= server.length()) {
                // "[addr]" with no port
                return server + ":25565";
            }
            // "[addr]:port" – already fully qualified
            return server;
        }

        // Count colons to distinguish IPv4/hostname from bare IPv6
        long colonCount = server.chars().filter(c -> c == ':').count();

        if (colonCount == 0) {
            // Plain hostname or IPv4 with no port
            return server + ":25565";
        } else if (colonCount == 1) {
            // hostname:port or IPv4:port – already fully qualified
            return server;
        } else {
            // Bare IPv6 address (multiple colons, not bracketed) – wrap it
            return "[" + server + "]:25565";
        }
    }
}
