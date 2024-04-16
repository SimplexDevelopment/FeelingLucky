package io.github.simplex.luck.player;

import java.util.UUID;

public class DynamicConfig {
    private UUID playerUUID;
    private String username;
    private double luckValue;
    private boolean isVerbose;
    private double multiplier;

    public String getPlayerUUID() {
        return playerUUID.toString();
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getLuckValue() {
        return luckValue;
    }

    public void setLuckValue(double luckValue) {
        this.luckValue = luckValue;
    }

    public boolean isVerbose() {
        return isVerbose;
    }

    public void setVerbose(boolean isVerbose) {
        this.isVerbose = isVerbose;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }
}