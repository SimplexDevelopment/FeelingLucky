package io.github.simplex.luck.util;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CooldownTimer {
    public static final long DEFAULT_COOLDOWN = 30L;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public void setCooldown(UUID playerUUID, long time) {
        if (time < 1) {
            cooldowns.remove(playerUUID);
        } else {
            cooldowns.put(playerUUID, time);
        }
    }

    public long getCooldown(UUID uuid) {
        return cooldowns.getOrDefault(uuid, 0L);
    }

    public long remaining(Player player) {
        long timeLeft = System.currentTimeMillis() - getCooldown(player.getUniqueId());
        return TimeUnit.MILLISECONDS.toSeconds(timeLeft) - DEFAULT_COOLDOWN;
    }

    public boolean onCooldown(Player player) {
        long remaining = System.currentTimeMillis() - getCooldown(player.getUniqueId());
        return (!(TimeUnit.MILLISECONDS.toSeconds(remaining) >= DEFAULT_COOLDOWN));
    }
}
