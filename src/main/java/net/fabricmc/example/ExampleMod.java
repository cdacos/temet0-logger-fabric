package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

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

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

public class ExampleMod implements ModInitializer {
	private static final File dir = new File(FabricLoader.getInstance().getGameDir()+ "/temet0-logger-fabric");
    private static final File writeFile = new File(dir + "/log.txt");

	private String getCurrentTime()
	{
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
	}

	public void writeToFile(String logEntry) {
		String line = String.format("[%s] %s", getCurrentTime(), logEntry);

        try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(writeFile, true));
            out.append(line + "\n");
            out.close();
        }
		catch (IOException e)
		{
            e.printStackTrace();
        }
    }

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		if (!Files.exists(dir.toPath())) {
            try
			{
                Files.createDirectories(dir.toPath());
            }
			catch (IOException e) {
                e.printStackTrace();
            }
        }

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("log")
				.requires(source -> source.hasPermissionLevel(2))
				.then(literal("message")
					.then(argument("contents", StringArgumentType.greedyString())
						.executes(this::message)))
                .then(literal("position")
					.then(argument("players", EntityArgumentType.players())
                    	.executes(this::position)))
			);
        });
	}

    private int message(CommandContext<ServerCommandSource> context) {
		String contents = StringArgumentType.getString(context, "contents");
		writeToFile(String.format("{%s} : %s", context.getSource().getName(), contents));
        return 1;
    }

    private int position(CommandContext<ServerCommandSource> context) {
		try
		{
			Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "players");

			for (ServerPlayerEntity player : players)
			{
				String worldName = getWorldName(player);
				writeToFile(String.format("{%s} position: %s, %s", player.getEntityName(), worldName, player.getPos()));
			}
		}
		catch (Exception ex)
		{
   	    	return 0;
		}
		return 1;
    }

	private String getWorldName(ServerPlayerEntity player) {
		World world = player.getEntityWorld();
		Identifier worldId = world.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY).getId(world.getDimension());
		return worldId.getPath();
	}
}
