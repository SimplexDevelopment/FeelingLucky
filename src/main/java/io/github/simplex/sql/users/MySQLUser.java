package io.github.simplex.sql.users;

import io.github.simplex.luck.player.DynamicConfig;
import io.github.simplex.luck.util.Logs;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLUser {

    private final Connection connection;
    private final String playerUUID;

    public MySQLUser(Connection connection, UUID playerUUID) {
        this.connection = connection;
        this.playerUUID = playerUUID.toString();
    }

    public DynamicConfig loadUserConfig() {
        DynamicConfig config = new DynamicConfig();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM PlayerConfig WHERE playerUUID = ?"
            );
            statement.setString(1, playerUUID);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                config.setPlayerUUID(UUID.fromString(resultSet.getString("uuid")));
                config.setUsername(Bukkit.getOfflinePlayer(UUID.fromString(resultSet.getString("uuid"))).getName());
                config.setLuckValue(resultSet.getInt("luckValue"));
                config.setVerbose(resultSet.getBoolean("isVerbose"));
                config.setMultiplier(resultSet.getDouble("multiplier"));
            }
        } catch (SQLException e) {
            Logs.error(e);
        }
        return config;
    }

    public void saveUserConfig(DynamicConfig config) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO PlayerConfig (playerUUID, username, luckValue, isVerbose, multiplier) VALUES (?, ?, ?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE username = ?, luckValue = ?, isVerbose = ?, multiplier = ?"
            );
            statement.setString(1, playerUUID);
            statement.setString(2, config.getUsername());
            statement.setDouble(3, config.getLuckValue());
            statement.setBoolean(4, config.isVerbose());
            statement.setDouble(5, config.getMultiplier());
            statement.setString(6, config.getUsername());
            statement.setDouble(7, config.getLuckValue());
            statement.setBoolean(8, config.isVerbose());
            statement.setDouble(9, config.getMultiplier());

            statement.executeUpdate();
        } catch (SQLException e) {
            Logs.error(e);
        }
    }
}