# SeamlessLogin

A NeoForge client-side mod for Minecraft 1.21.8 that automatically logs you in to offline-mode servers. Passwords are stored encrypted on disk and sent when a login prompt is detected in chat.

---

## Features

### Password Manager
Save a password for each server you play on. Open the manager from the multiplayer server list (lock icon button, top-right corner) or with the keybind (default: **Numpad +**). Each entry stores:
- **Server name** — a label to identify the entry
- **Server address** — used as the unique key (normalised to `host:port`)
- **Password** — stored encrypted with AES-256-GCM
- **Auto-login toggle** — enable or disable automatic login per entry

### Auto-Login
When you join a server that has a saved password with auto-login enabled, SeamlessLogin detects the login prompt in chat (matched against configurable regex patterns) and automatically sends your password after a short delay. No manual typing required.

### Password Generator
Generate strong passwords directly from the Add/Edit screen with one click. Two modes, both configurable from the mod's config screen:

**Password mode**
- Configurable length (8–64 characters) via slider
- Optional character sets: capital letters, numbers, special characters (`!@#$%^&*-_=+`)
- Guarantees at least one character from each enabled set

**Passphrase mode**
- Generates an XKCD-style passphrase from a built-in 256-word list
- Configurable word count (3–8 words) via slider
- Words joined with hyphens (e.g. `flame-brook-vest-moon`)

### Password Safety
- **Show / Hide** — toggle password visibility in the input field
- **Copy** — copy the current password to clipboard with one click; a fading confirmation message confirms the action
- **Password reuse warning** — if you try to save a password already used for another server, a warning screen appears. Saving is locked for 5 seconds to discourage hasty dismissal. A checkbox lets you skip the warning permanently.

### Login Pattern Configuration
The list of chat message patterns that trigger auto-login is fully configurable. Patterns are regular expressions matched case-insensitively. Edit them from **Mods → SeamlessLogin → Config → Login Patterns**.

### Security
- Passwords are encrypted with **AES-256-GCM** before being written to disk
- The encryption key is generated once and stored separately (`seamlesslogin/key.dat` in your config folder)
- Passwords are never logged or transmitted anywhere other than the login command sent to the server

---

## Configuration

Open the config screen from the **Mods** list → select SeamlessLogin → **Config**.

| Setting | Description |
|---|---|
| Login Patterns | Regex patterns that match login prompts in chat |
| Mode | Password or Passphrase |
| Password Length | 8–64 characters (slider) |
| Word Count | 3–8 words for passphrases (slider) |
| Capital Letters | Include uppercase characters in generated passwords |
| Numbers | Include digits in generated passwords |
| Special Characters | Include special characters in generated passwords |

---

## Requirements

- Minecraft **1.21.8**
- NeoForge **21.8.52** or newer

---

## License

MIT
