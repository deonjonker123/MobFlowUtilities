package com.misterd.mobflowutilities.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class MFUExperienceUtils {

    public static int getTotalExperienceForLevel(int level) {
        if (level <= 16) {
            return level * level + 6 * level;
        } else if (level <= 31) {
            return (int) (2.5 * level * level - 40.5 * level + 360);
        } else {
            long totalXp = (long) (4.5 * level * level - 162.5 * level + 2220);
            return (int) Math.min(totalXp, Integer.MAX_VALUE);
        }
    }

    public static int getLevelFromTotalExperience(int totalExp) {
        if (totalExp < getTotalExperienceForLevel(16)) {
            return (int) Math.floor((-6 + Math.sqrt(36 + 4 * totalExp)) / 2);
        } else if (totalExp < getTotalExperienceForLevel(31)) {
            return (int) Math.floor((40.5 + Math.sqrt(-40.5 * -40.5 - 4 * 2.5 * (360 - totalExp))) / (2 * 2.5));
        } else {
            return (int) Math.floor((162.5 + Math.sqrt(-162.5 * -162.5 - 4 * 4.5 * (2220 - totalExp))) / (2 * 4.5));
        }
    }

    public static int getExperienceForNextLevel(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else {
            return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
        }
    }

    public static float getProgressToNextLevel(int totalExp) {
        int level = getLevelFromTotalExperience(totalExp);
        int expForCurrentLevel = getTotalExperienceForLevel(level);
        int expForNextLevel = getExperienceForNextLevel(level);
        int expAfterFullLevels = totalExp - expForCurrentLevel;
        return (float) expAfterFullLevels / (float) expForNextLevel;
    }

    public static int getPlayerTotalExperience(Player player) {
        int exp = getTotalExperienceForLevel(player.experienceLevel);
        long totalXp = (long) exp + (long) Math.round(player.experienceProgress * player.getXpNeededForNextLevel());
        return (int) Math.min(totalXp, Integer.MAX_VALUE);
    }

    public static void setPlayerTotalExperience(ServerPlayer player, int totalExp) {
        totalExp = Math.max(0, totalExp);

        int level = getLevelFromTotalExperience(totalExp);
        while (level > 0 && getTotalExperienceForLevel(level) > totalExp) {
            level--;
        }
        while (getTotalExperienceForLevel(level + 1) <= totalExp
                && getTotalExperienceForLevel(level + 1) < Integer.MAX_VALUE) {
            level++;
        }

        int remainder = totalExp - getTotalExperienceForLevel(level);

        player.setExperienceLevels(level);
        player.setExperiencePoints(remainder);
        player.totalExperience = totalExp;
    }

    private MFUExperienceUtils() {}
}