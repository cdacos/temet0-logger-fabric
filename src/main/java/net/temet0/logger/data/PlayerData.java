package net.temet0.logger.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.temet0.logger.models.Player;

public class PlayerData {
    public Player GetByUUID(Connection connection, String uuid, String name) {
        return GetByUUID(connection, uuid, name, false);
    }

    private Player GetByUUID(Connection connection, String uuid, String name, boolean attemptedCreate) {
        Player player = null;
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT player_id, uuid, name FROM players WHERE uuid = ?;");
            stmt.setString(1, uuid);
            ResultSet row = stmt.executeQuery();
            if (row.next()) {
                player = new Player();
                player.PlayerId = row.getInt("player_id");
                player.UUID = uuid;
                player.Name = row.getString("name");
                if (!name.equals(player.Name)) {
                    stmt.close();
                    stmt = connection.prepareStatement("UPDATE players SET name = ? WHERE uuid = ?;");
                    stmt.setString(1, uuid);
                    stmt.setString(2, name);
                    stmt.executeUpdate();
                    stmt.close();
                }
            } else if (!attemptedCreate) {
                stmt.close();
                stmt = connection.prepareStatement("INSERT INTO players (uuid, name) VALUES(?, ?);");
                stmt.setString(1, uuid);
                stmt.setString(2, name);
                stmt.execute();
                player = GetByUUID(connection, uuid, name, true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return player;
    }

    public boolean Schema(Connection connection)
    {
        boolean result = false;
        try {
            PreparedStatement stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS players ( \n" +
            "    player_id INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
            "    uuid TEXT, \n" +
            "    name TEXT, \n" +
            "    created DATETIME DEFAULT CURRENT_TIMESTAMP \n" +
            ");");
            result = stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
