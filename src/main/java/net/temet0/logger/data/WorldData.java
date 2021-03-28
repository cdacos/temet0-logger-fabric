package net.temet0.logger.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.temet0.logger.models.World;

public class WorldData {
    public World GetByName(Connection connection, String name) {
        return GetByName(connection, name, false);
    }

    private World GetByName(Connection connection, String name, boolean attemptedCreate)
    {
        World world = null;
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement("SELECT world_id, name FROM worlds WHERE name = ?;");
            stmt.setString(1, name);
            ResultSet row = stmt.executeQuery();
            if (row.next()) {
                world = new World();
                world.WorldId = row.getInt("world_id");
                world.Name = name;
            } else if (!attemptedCreate) {
                stmt.close();
                stmt = connection.prepareStatement("INSERT INTO worlds (name) VALUES(?);");
                stmt.setString(1, name);
                stmt.execute();
                world = GetByName(connection, name, true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return world;
    }

    public boolean Schema(Connection connection)
    {
        boolean result = false;
        try {
            PreparedStatement stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS worlds ( \n" +
            "    world_id INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
            "    name TEXT \n" +
            ");");
            result = stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
