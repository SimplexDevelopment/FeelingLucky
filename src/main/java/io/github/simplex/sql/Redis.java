package io.github.simplex.sql;

import io.github.simplex.luck.Config;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.DynamicConfig;
import io.github.simplex.luck.player.PlayerConfig;
import io.github.simplex.sql.users.RedisUser;

import java.util.Set;
import java.util.UUID;

public class Redis {

    private final redis.clients.jedis.Jedis jedis;
    private final FeelingLucky plugin;

    public Redis(final FeelingLucky plugin) {
        this.plugin = plugin;

        final Config.RedisWrapper redisWrapper = plugin.getConfig().getRedis();
        final String host = redisWrapper.getHost();
        final String password = redisWrapper.getPassword();
        final String port = redisWrapper.getPort();
        final int database = redisWrapper.getDatabase();

        jedis = new redis.clients.jedis.Jedis(host, Integer.parseInt(port));
        jedis.auth(password);
        jedis.select(database);
    }

    public redis.clients.jedis.Jedis getJedis() {
        return jedis;
    }

    public void closeConnection() {
        jedis.close();
    }

    public void loadPlayers() {
        Set<String> playerUUIDs = jedis.keys("*");
        for (String playerUUID : playerUUIDs) {
            RedisUser redisUser = new RedisUser(jedis, UUID.fromString(playerUUID));
            DynamicConfig userConfig = redisUser.loadUserConfig();

            // Create a new PlayerConfig instance with the loaded data
            PlayerConfig playerConfig = PlayerConfig.fromDynamicConfig(plugin, userConfig);

            // Add the PlayerConfig instance to the map
            plugin.getConfigMap().put(UUID.fromString(playerUUID), playerConfig);
        }
    }

    public void savePlayers() {
        for (UUID playerUUID : plugin.getConfigMap().keySet()) {
            PlayerConfig playerConfig = plugin.getConfigMap().get(playerUUID);
            RedisUser redisUser = new RedisUser(jedis, playerUUID);
            redisUser.saveUserConfig(playerConfig.toDynamicConfig());
        }
    }

    public void loadPlayer(UUID playerUUID) {
        RedisUser redisUser = new RedisUser(jedis, playerUUID);
        DynamicConfig userConfig = redisUser.loadUserConfig();

        // Create a new PlayerConfig instance with the loaded data
        PlayerConfig playerConfig = PlayerConfig.fromDynamicConfig(plugin, userConfig);

        // Add the PlayerConfig instance to the map
        plugin.getConfigMap().put(playerUUID, playerConfig);
    }

    public void savePlayer(UUID playerUUID) {
        PlayerConfig playerConfig = plugin.getConfigMap().get(playerUUID);
        RedisUser redisUser = new RedisUser(jedis, playerUUID);
        redisUser.saveUserConfig(playerConfig.toDynamicConfig());
    }
}