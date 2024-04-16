package io.github.simplex.sql;

public enum SQLType {
    SQLITE,
    MYSQL,
    REDIS,
    NONE;

    public static SQLType fromString(String type) {
        return switch (type.toLowerCase()) {
            case "sqlite" -> SQLITE;
            case "mysql" -> MYSQL;
            case "redis" -> REDIS;
            default -> NONE;
        };
    }
}
