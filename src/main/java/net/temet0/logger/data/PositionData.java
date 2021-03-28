package net.temet0.logger.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.temet0.logger.models.Player;
import net.temet0.logger.models.World;

public class PositionData {
    public boolean Create(Connection connection, Player player, World world, int x, int y, int z, String note) {
        boolean result = false;
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO positions (player_id, world_id, x, y, z, note) VALUES(?, ?, ?, ?, ?, ?);");
            stmt.setInt(1, player.PlayerId);
            stmt.setInt(2, world.WorldId);
            stmt.setInt(3, x);
            stmt.setInt(4, y);
            stmt.setInt(5, z);
            stmt.setString(6, note);
            stmt.execute();
            result = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean Schema(Connection connection)
    {
        boolean result = false;
        try {
            PreparedStatement stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS positions ( \n" +
            "    position_id INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
            "    player_id INTEGER, \n" +
            "    world_id INTEGER, \n" +
            "    x INTEGER, \n" +
            "    y INTEGER, \n" +
            "    z INTEGER, \n" +
            "    note TEXT NULL, \n" +
            "    created DATETIME DEFAULT CURRENT_TIMESTAMP, \n" +
            "    FOREIGN KEY(player_id) REFERENCES players(player_id), \n" +
            "    FOREIGN KEY(world_id) REFERENCES worlds(world_id) \n" +
            ");");
            result = stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
