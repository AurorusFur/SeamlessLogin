package org.aurorus.seamlesslogin.password;

public class PasswordEntry {
    public String name;
    public String server;
    public String encryptedPassword;
    public boolean autoLogin;

    public PasswordEntry() {}

    public PasswordEntry(String name, String server, String encryptedPassword, boolean autoLogin) {
        this.name = name;
        this.server = server;
        this.encryptedPassword = encryptedPassword;
        this.autoLogin = autoLogin;
    }

    /** Returns the display name, falling back to the server address if name is blank. */
    public String displayName() {
        return (name != null && !name.isBlank()) ? name : server;
    }
}
