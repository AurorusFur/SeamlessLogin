package org.aurorus.seamlesslogin.password;

import org.aurorus.seamlesslogin.Config;

import java.security.SecureRandom;

public class PasswordGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS    = "0123456789";
    private static final String SPECIAL   = "!@#$%^&*-_=+";

    private static final String[] WORDS = {
        "able","acid","aged","also","alto","arch","area","army","atom","aunt",
        "back","bake","ball","band","bark","barn","base","bath","bear","beat",
        "bell","belt","best","bird","bite","black","blade","blame","blank","blast",
        "blaze","blend","blink","block","bloom","blow","blue","blur","bold","bolt",
        "bone","book","boom","boot","born","both","bowl","brag","bran","brave",
        "bred","brew","brim","brook","brow","burn","bush","byte","cage","cake",
        "calm","camp","cane","card","care","cart","cast","cave","cell","chat",
        "chip","clam","clap","clay","clip","club","clue","coal","coat","coil",
        "cold","cone","cord","core","corn","cost","coup","cozy","crab","crew",
        "crop","crow","cube","curl","cute","cyan","dark","dart","dash","data",
        "dawn","dear","deck","deep","dell","dew","dial","dice","diet","dime",
        "dirt","disk","dock","dome","door","dork","dose","dove","down","drab",
        "drag","draw","drew","drip","drop","drum","duel","duke","dune","dusk",
        "dust","each","earl","earn","ease","east","edge","emit","epic","even",
        "exam","face","fact","fade","fail","fair","fall","fame","farm","fast",
        "fate","fawn","feed","feel","fell","fend","fern","file","find","fine",
        "fire","firm","fish","fist","flag","flaw","flew","flip","flow","foam",
        "fold","folk","fond","font","ford","fore","fork","form","fort","foul",
        "fowl","free","frog","fuel","full","fund","fuse","game","gaze","gist",
        "glad","glow","glue","goal","gust","hand","hang","hard","harm","harp",
        "haze","heal","heap","heat","heel","helm","help","herd","hill","hint",
        "hive","hold","hole","home","hone","hood","hope","horn","host","howl",
        "hulk","idle","iris","isle","jade","jolt","keen","kept","kind","king",
        "kite","knob","lace","lake","lamp","land","lane","lark","lash","last",
        "late","lawn","lead","leaf","lean","leap","left","lend","life","like",
        "limp","link","list","live","loft","lone","loom","lord","lose","loud",
        "love","luck","lure","main","mast","melt","mild","mill","mind","mint",
        "mist","moat","mole","monk","moon","moor","more","morn","moss","moth",
        "mule","must","mute","myth","nail","name","neat","neck","need","nest",
        "news","next","node","noon","norm","nose","note","oath","once","open",
        "oral","oval","oven","pace","pack","pact","paid","pave","peak","peck",
        "peel","peer","pelt","perk","pest","pine","pipe","plot","plow","plug",
        "plum","plus","poke","pole","pond","pool","pore","port","post","pour",
        "prey","prod","prop","pull","pump","pure","push","rain","rake","rank",
        "rate","rave","real","reed","reef","reel","rely","rend","rent","rest",
        "ride","rift","ring","risk","roam","robe","role","roll","roof","root",
        "rope","rose","ruin","rule","rush","safe","sail","salt","sand","sane",
        "sash","save","seal","seam","seat","self","sell","send","shed","ship",
        "shot","show","sift","silk","sing","sink","skip","slam","slim","slip",
        "slow","snap","snow","soak","soil","sole","song","soul","span","spin",
        "stir","stop","suit","surf","swan","swim","tale","tall","tame","tape",
        "task","team","tear","tell","tend","term","test","tide","time","toll",
        "tome","tool","tore","torn","toss","tote","tour","town","trap","trek",
        "trim","trip","trot","true","tube","tune","turf","twin","type","upon",
        "vary","vast","veil","vent","vibe","wade","wage","wake","walk","wall",
        "wand","want","ward","warm","warn","warp","wary","wave","weld","well",
        "whet","wide","wild","will","wind","wine","wing","wire","wise","wish",
        "wolf","womb","work","worm","worn","wrap","yarn","yell","zone","zoom"
    };

    public static String generate() {
        return Config.usePassphrase ? generatePassphrase() : generatePassword();
    }

    private static String generatePassword() {
        StringBuilder charset = new StringBuilder(LOWERCASE);
        if (Config.useCapitalLetters) charset.append(UPPERCASE);
        if (Config.useNumbers)        charset.append(DIGITS);
        if (Config.useSpecialChars)   charset.append(SPECIAL);

        String chars = charset.toString();
        int length = Config.passwordLength;
        char[] password = new char[length];

        // Guarantee at least one char of each required type in the first slots
        int slot = 0;
        if (Config.useCapitalLetters && slot < length)
            password[slot++] = UPPERCASE.charAt(RANDOM.nextInt(UPPERCASE.length()));
        if (Config.useNumbers && slot < length)
            password[slot++] = DIGITS.charAt(RANDOM.nextInt(DIGITS.length()));
        if (Config.useSpecialChars && slot < length)
            password[slot++] = SPECIAL.charAt(RANDOM.nextInt(SPECIAL.length()));

        for (int i = slot; i < length; i++)
            password[i] = chars.charAt(RANDOM.nextInt(chars.length()));

        // Shuffle to avoid predictable prefix
        for (int i = length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char tmp = password[i];
            password[i] = password[j];
            password[j] = tmp;
        }

        return new String(password);
    }

    private static String generatePassphrase() {
        int count = Config.passphraseWordCount;
        String[] selected = new String[count];
        for (int i = 0; i < count; i++)
            selected[i] = WORDS[RANDOM.nextInt(WORDS.length)];
        return String.join("-", selected);
    }
}
