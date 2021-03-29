package net.temet0.logger.loggers;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.temet0.logger.data.EventData;
import net.temet0.logger.data.PlayerData;
import net.temet0.logger.data.PositionData;
import net.temet0.logger.data.WorldData;
import net.temet0.logger.models.Player;
import net.temet0.logger.models.World;
import net.temet0.logger.utils.LoggerUtils;

public class SqliteLogger implements ILogger {
	private String dbConnectionString;

	public SqliteLogger(File dir) {
		dbConnectionString = "jdbc:sqlite:" + dir + "/log.db";
		createNewDatabaseIfNotExists();
	}

	@Override
    public void logMessage(String message) {
		try (Connection connection = DriverManager.getConnection(dbConnectionString)) {
			if (connection != null) {
				new EventData().Create(connection, message);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void logPosition(ServerPlayerEntity playerEntity, String message) {
		try (Connection connection = DriverManager.getConnection(dbConnectionString)) {
			if (connection != null) {
				String uuid = playerEntity.getUuid().toString();
				Player player = new PlayerData().GetByUUID(connection, uuid, playerEntity.getEntityName());
				World world = new WorldData().GetByName(connection, LoggerUtils.getWorldName(playerEntity));
				Vec3d position = playerEntity.getPos();
				new PositionData().Create(connection, player, world, (int)position.x, (int)position.y, (int)position.z, message);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private void createNewDatabaseIfNotExists() {
		try (Connection connection = DriverManager.getConnection(dbConnectionString)) {
			if (connection != null) {
				new WorldData().Schema(connection);
				new PlayerData().Schema(connection);
				new EventData().Schema(connection);
				new PositionData().Schema(connection);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}
