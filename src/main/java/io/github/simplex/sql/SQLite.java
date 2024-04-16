package io.github.simplex.sql;

import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.DynamicConfig;
import io.github.simplex.luck.player.PlayerConfig;
import io.github.simplex.luck.util.Logs;
import io.github.simplex.sql.users.SQLiteUser;

import java.sql.*;
import java.util.UUID;

public class SQLite {

    private final Connection connection;
    private final FeelingLucky plugin;

    public SQLite(final FeelingLucky plugin) throws SQLException {
        this.plugin = plugin;
        final String databaseFilePath = plugin.getConfig().getSQLite().getPath();

        connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFilePath);
        if (connection != null) createTable();
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public void createTable() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS PlayerConfig (playerUUID VARCHAR(36), username VARCHAR(16), luckValue DOUBLE, isVerbose BOOLEAN, multiplier DOUBLE)")) {
            statement.execute();
        }
    }

    public void loadPlayers() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT playerUUID FROM PlayerConfig");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                UUID playerUUID = UUID.fromString(resultSet.getString("playerUUID"));
                SQLiteUser sqliteUser = new SQLiteUser(connection, playerUUID);
                DynamicConfig userConfig = sqliteUser.loadUserConfig();

                // Create a new PlayerConfig instance with the loaded data
                PlayerConfig playerConfig = PlayerConfig.fromDynamicConfig(plugin, userConfig);

                // Add the PlayerConfig instance to the map
                plugin.getConfigMap().put(playerUUID, playerConfig);
            }
        }
    }

    public void savePlayers() throws SQLException {
        for (UUID playerUUID : plugin.getConfigMap().keySet()) {
            PlayerConfig playerConfig = plugin.getConfigMap().get(playerUUID);
            SQLiteUser sqliteUser = new SQLiteUser(connection, playerUUID);
            sqliteUser.saveUserConfig(playerConfig.toDynamicConfig());
        }
    }

    public void loadPlayer(UUID playerUUID) throws SQLException {
        SQLiteUser sqliteUser = new SQLiteUser(connection, playerUUID);
        DynamicConfig userConfig = sqliteUser.loadUserConfig();

        // Create a new PlayerConfig instance with the loaded data
        PlayerConfig playerConfig = PlayerConfig.fromDynamicConfig(plugin, userConfig);

        // Add the PlayerConfig instance to the map
        plugin.getConfigMap().put(playerUUID, playerConfig);
    }

    public void savePlayer(UUID playerUUID) throws SQLException {
        PlayerConfig playerConfig = plugin.getConfigMap().get(playerUUID);
        SQLiteUser sqliteUser = new SQLiteUser(connection, playerUUID);
        sqliteUser.saveUserConfig(playerConfig.toDynamicConfig());
    }
}