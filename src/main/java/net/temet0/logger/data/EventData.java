package net.temet0.logger.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EventData {
    public boolean Create(Connection connection, String note) {
        boolean result = false;
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO events (note) VALUES(?);");
            stmt.setString(1, note);
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
            PreparedStatement stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS events ( \n" +
            "    event_id INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
            "    note TEXT NULL, \n" +
            "    created DATETIME DEFAULT CURRENT_TIMESTAMP \n" +
            ");");
            result = stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
