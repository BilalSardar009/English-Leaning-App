package com.saim.englishlearning.model;

public class Achievement {
    public final String key;
    public final String title;
    public final String detail;
    public final String emoji;
    public final int target;
    public final int progress;
    public final boolean unlocked;

    public Achievement(String key, String title, String detail, String emoji,
                       int target, int progress, boolean unlocked) {
        this.key = key;
        this.title = title;
        this.detail = detail;
        this.emoji = emoji;
        this.target = target;
        this.progress = progress;
        this.unlocked = unlocked;
    }

    public int percent() {
        if (target <= 0) return unlocked ? 100 : 0;
        int p = (int) ((progress * 100L) / target);
        return Math.min(100, Math.max(0, p));
    }
}
