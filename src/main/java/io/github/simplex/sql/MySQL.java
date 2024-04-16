package io.github.simplex.sql;

import io.github.simplex.luck.Config;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.DynamicConfig;
import io.github.simplex.luck.player.PlayerConfig;
import io.github.simplex.luck.util.Logs;
import io.github.simplex.sql.users.MySQLUser;

import java.sql.*;
import java.util.UUID;

public class MySQL {

    private final Connection connection;
    private final FeelingLucky plugin;

    public MySQL(FeelingLucky plugin) throws SQLException {
        this.plugin = plugin;

        Config.MySQLWrapper mySQLWrapper = plugin.getConfig().getMySQL();
        String host = mySQLWrapper.getHost();
        int port = mySQLWrapper.getPort();
        String database = mySQLWrapper.getDatabase();
        String username = mySQLWrapper.getUsername();
        String password = mySQLWrapper.getPassword();

        connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS PlayerConfig (playerUUID VARCHAR(36), username VARCHAR(16), luckValue DOUBLE, isVerbose BOOLEAN, multiplier DOUBLE)")) {
                statement.execute();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void loadPlayers() {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM PlayerConfig");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String playerUUID = resultSet.getString("playerUUID");
                MySQLUser mySQLUser = new MySQLUser(connection, UUID.fromString(playerUUID));
                DynamicConfig userConfig = mySQLUser.loadUserConfig();
                PlayerConfig playerConfig = PlayerConfig.fromDynamicConfig(plugin, userConfig);
                plugin.getConfigMap().put(UUID.fromString(playerUUID), playerConfig);
            }
        } catch (SQLException e) {
            Logs.error(e);
        }
    }

    public void savePlayer(final PlayerConfig playerConfig) {
        MySQLUser mySQLUser = new MySQLUser(connection, playerConfig.getPlayer().getUniqueId());
        mySQLUser.saveUserConfig(playerConfig.toDynamicConfig());
    }

    public void loadPlayer(final UUID playerUUID) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM PlayerConfig WHERE playerUUID = ?")) {
            statement.setString(1, playerUUID.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    MySQLUser mySQLUser = new MySQLUser(connection, playerUUID);
                    DynamicConfig userConfig = mySQLUser.loadUserConfig();
                    PlayerConfig playerConfig = PlayerConfig.fromDynamicConfig(plugin, userConfig);
                    plugin.getConfigMap().put(playerUUID, playerConfig);
                }
            }
        } catch (SQLException e) {
            Logs.error(e);
        }
    }

    public void savePlayers() {
        for (UUID playerUUID : plugin.getConfigMap().keySet()) {
            PlayerConfig playerConfig = plugin.getConfigMap().get(playerUUID);
            MySQLUser mySQLUser = new MySQLUser(connection, playerUUID);
            mySQLUser.saveUserConfig(playerConfig.toDynamicConfig());
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                Logs.error(e);
            }
        }
    }
}