package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
		System.out.println(line);
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

		System.out.println("Hello Fabric world!");

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
                    .executes(this::position))
			);
        });
	}

    private int message(CommandContext<ServerCommandSource> context) {
		String contents = StringArgumentType.getString(context, "contents");
		writeToFile(String.format("{%s} : %s", context.getSource().getName(), contents));
        return 0;
    }

    private int position(CommandContext<ServerCommandSource> context) {
		ServerCommandSource src = context.getSource();
		writeToFile(String.format("{%s} position: %s, %s", src.getName(), src.getWorld(), src.getPosition()));
        return 0;
    }
}
