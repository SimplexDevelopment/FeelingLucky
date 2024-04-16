package io.github.simplex.sql.users;

import io.github.simplex.luck.player.DynamicConfig;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public class RedisUser {

    private final Jedis jedis;
    private final String playerUUID;

    public RedisUser(Jedis jedis, UUID playerUUID) {
        this.jedis = jedis;
        this.playerUUID = playerUUID.toString();
    }

    public DynamicConfig loadUserConfig() {
        DynamicConfig config = new DynamicConfig();
        config.setPlayerUUID(UUID.fromString(jedis.hget(playerUUID, "uuid")));
        config.setUsername(Bukkit.getOfflinePlayer(UUID.fromString(jedis.hget(playerUUID, "username"))).getName());
        config.setLuckValue(Integer.parseInt(jedis.hget(playerUUID, "luckValue")));
        config.setVerbose(Boolean.parseBoolean(jedis.hget(playerUUID, "isVerbose")));
        config.setMultiplier(Double.parseDouble(jedis.hget(playerUUID, "multiplier")));

        return config;
    }

    public void saveUserConfig(DynamicConfig config) {
        jedis.hset(playerUUID, "uuid", config.getPlayerUUID());
        jedis.hset(playerUUID, "username", config.getUsername());
        jedis.hset(playerUUID, "luckValue", String.valueOf(config.getLuckValue()));
        jedis.hset(playerUUID, "isVerbose", String.valueOf(config.isVerbose()));
        jedis.hset(playerUUID, "multiplier", String.valueOf(config.getMultiplier()));
    }
}