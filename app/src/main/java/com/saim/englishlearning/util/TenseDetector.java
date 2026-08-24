package com.saim.englishlearning.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A rule based guess at which tense an English sentence uses. It returns an index
 * into {@link com.saim.englishlearning.data.TenseBank}, or -1 when nothing matches.
 */
public final class TenseDetector {

    private static final Pattern ING = Pattern.compile("\\b\\w+ing\\b");
    private static final Pattern ED = Pattern.compile("\\b\\w{3,}ed\\b");

    private static final Set<String> IRREGULAR_PAST = new HashSet<>(Arrays.asList(
            "went", "saw", "came", "took", "gave", "made", "found", "told", "wrote", "spoke",
            "broke", "chose", "drove", "ate", "fell", "felt", "got", "heard", "held", "kept",
            "knew", "left", "lost", "met", "paid", "put", "ran", "said", "sat", "sent", "sold",
            "stood", "taught", "thought", "understood", "won", "wore", "brought", "bought",
            "caught", "began", "drank", "grew", "led", "meant", "read", "rose", "sang", "slept",
            "spent", "stole", "swam", "threw", "woke", "built", "burnt", "dealt", "drew", "flew",
            "forgot", "froze", "hid", "hurt", "lay", "lent", "let", "lit", "rode", "rang", "shook",
            "shot", "shut", "sank", "spread", "struck", "swore", "tore", "wound"
    ));

    private static final Set<String> PARTICIPLES = new HashSet<>(Arrays.asList(
            "been", "gone", "done", "seen", "taken", "given", "written", "spoken", "broken",
            "chosen", "driven", "eaten", "fallen", "forgotten", "known", "shown", "thrown",
            "worn", "begun", "drunk", "grown", "hidden", "ridden", "risen", "sung", "sunk",
            "stolen", "swum", "torn", "woken", "made", "found", "told", "kept", "left", "lost",
            "met", "paid", "put", "said", "sat", "sent", "sold", "taught", "thought", "won",
            "brought", "bought", "caught", "built", "heard", "held", "read", "run", "come"
    ));

    private TenseDetector() {
    }

    public static int detect(String sentence) {
        if (sentence == null) return -1;
        String s = " " + sentence.toLowerCase(Locale.ROOT).replaceAll("[^a-z' ]", " ")
                .replaceAll("\\s+", " ").trim() + " ";
        if (s.trim().isEmpty()) return -1;

        boolean hasIng = ING.matcher(s).find();

        // Future family first, because "will have been" also contains "have been".
        if (s.contains(" will have been ") && hasIng) return 11;
        if (s.contains(" will have ")) return 10;
        if (s.contains(" will be ") && hasIng) return 9;
        if (s.contains(" will ") || s.contains(" shall ")) return 8;

        if (s.contains(" had been ") && hasIng) return 7;
        if (s.contains(" had ")) return 6;

        if ((s.contains(" has been ") || s.contains(" have been ")) && hasIng) return 3;
        if (s.contains(" has ") || s.contains(" have ")) {
            if (containsAny(s, PARTICIPLES)) return 2;
            return 2;
        }

        if (s.contains(" was ") || s.contains(" were ")) {
            if (hasIng) return 5;
            return 4;
        }

        if ((s.contains(" am ") || s.contains(" is ") || s.contains(" are ")) && hasIng) return 1;

        if (containsAny(s, IRREGULAR_PAST)) return 4;
        if (ED.matcher(s).find()) return 4;

        return 0;
    }

    private static boolean containsAny(String paddedSentence, Set<String> words) {
        for (String w : words) {
            if (paddedSentence.contains(" " + w + " ")) return true;
        }
        return false;
    }
}
