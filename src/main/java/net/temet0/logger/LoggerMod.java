package net.temet0.logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.temet0.logger.data.EventData;
import net.temet0.logger.data.PlayerData;
import net.temet0.logger.data.PositionData;
import net.temet0.logger.data.WorldData;
import net.temet0.logger.models.Player;
import net.temet0.logger.models.World;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

public class LoggerMod implements ModInitializer {
	private static final File dir = new File(FabricLoader.getInstance().getGameDir() + "/temet0-logger-fabric");
	private static final File writeFile = new File(dir + "/log.txt");
	private static final String dbConnectionString = "jdbc:sqlite:" + dir + "/log.db";

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		if (!Files.exists(dir.toPath())) {
			try {
				Files.createDirectories(dir.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		createNewDatabase("log.db");

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			dispatcher.register(literal("log").requires(source -> source.hasPermissionLevel(2))
					.then(literal("message")
							.then(argument("message", StringArgumentType.greedyString()).executes(this::message)))
					.then(literal("position").then(
							argument("players", EntityArgumentType.players()).executes(ctx -> position(ctx, null))))
					.then(literal("position").then(argument("players", EntityArgumentType.players())
							.then(argument("message", StringArgumentType.greedyString())
									.executes(ctx -> position(ctx, StringArgumentType.getString(ctx, "message")))))));
		});
	}

	private int message(CommandContext<ServerCommandSource> context) {
		try {
			String message = StringArgumentType.getString(context, "message");
			logMessage(message);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return 0;
		}
		return 1;
	}

	private int position(CommandContext<ServerCommandSource> context, String message) {
		try {
			Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "players");
			for (ServerPlayerEntity player : players) {
				logPosition(player, message);
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return 0;
		}
		return 1;
	}

	public void logMessage(String message) {
		writeToFile("MESSAGE", null, message);
		logEventToDB(message);
	}

	public void logPosition(ServerPlayerEntity player, String message) {
		writeToFile("POSITION", player, message);
		logPositionToDB(player, message);
	}

	private void writeToFile(String event, ServerPlayerEntity player, String message) {
		// Disable for now
		// try {
		// 	String line;

		// 	if (player != null) {
		// 		String playerName = player.getEntityName();
		// 		String worldName = getWorldName(player);
		// 		Vec3d position = player.getPos();
		// 		message = message != null ? message : "";
		// 		line = String.format("[%s] %s {%s} %s %s %s", getCurrentTime(), event, playerName, worldName, position, message);
		// 	} else {
		// 		line = String.format("[%s] %s %s", getCurrentTime(), event, message);
		// 	}

		// 	BufferedWriter out = new BufferedWriter(new FileWriter(writeFile, true));
		// 	out.append(line + "\n");
		// 	out.close();
		// } catch (IOException e) {
		// 	e.printStackTrace();
		// }
	}

	private void logEventToDB(String note) {
		try (Connection connection = DriverManager.getConnection(dbConnectionString)) {
			if (connection != null) {
				new EventData().Create(connection, note);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private void logPositionToDB(ServerPlayerEntity playerEntity, String note) {
		try (Connection connection = DriverManager.getConnection(dbConnectionString)) {
			if (connection != null) {
				String uuid = playerEntity.getUuid().toString();
				Player player = new PlayerData().GetByUUID(connection, uuid, playerEntity.getEntityName());
				World world = new WorldData().GetByName(connection, getWorldName(playerEntity));
				Vec3d position = playerEntity.getPos();
				new PositionData().Create(connection, player, world, (int)position.x, (int)position.y, (int)position.z, note);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private String getCurrentTime() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	private String getWorldName(ServerPlayerEntity playerEntity) {
		net.minecraft.world.World world = playerEntity.getEntityWorld();
		Identifier worldId = world.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY).getId(world.getDimension());
		return worldId.getPath();
	}

	private void createNewDatabase(String fileName) {
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
