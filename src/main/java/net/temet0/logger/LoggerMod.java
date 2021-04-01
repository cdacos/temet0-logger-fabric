package net.temet0.logger;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.temet0.logger.loggers.LoggerManager;
import net.temet0.logger.loggers.SqliteLogger;

public class LoggerMod implements ModInitializer {
	private static final File dir = new File(FabricLoader.getInstance().getGameDir() + "/temet0-logger-fabric");

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

		LoggerManager.INSTANCE.Add(new SqliteLogger(dir));
		// LoggerManager.INSTANCE.Add(new FileLogger(dir));

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

		ServerTickEvents.END_SERVER_TICK.register(LoggerManager::serverTick);
	}

	private int message(CommandContext<ServerCommandSource> context) {
		try {
			String message = StringArgumentType.getString(context, "message");
			LoggerManager.INSTANCE.logMessage(message);
			if (!message.equals("blbllb")) throw new Exception("Test");
		} catch (Exception ex) {
			System.out.println(String.format("ERROR [%s]: Command 'message' -> %s", LoggerMod.class.getName(), ex.getMessage()));
			ex.printStackTrace();
			return 0;
		}
		return 1;
	}

	private int position(CommandContext<ServerCommandSource> context, String message) {
		try {
			Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "players");
			for (ServerPlayerEntity player : players) {
				LoggerManager.INSTANCE.logPosition(player, message);
			}
		} catch (Exception ex) {
			System.out.println(String.format("ERROR [%s]: Command 'position' -> %s", LoggerMod.class.getName(), ex.getMessage()));
			ex.printStackTrace();
			return 0;
		}
		return 1;
	}
}
