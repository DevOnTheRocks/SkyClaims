/*
 * SkyClaims - A Skyblock plugin made for Sponge
 * Copyright (C) 2017 Mohron
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SkyClaims is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SkyClaims.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.mohron.skyclaims.command.admin;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.type.StorageType;
import net.mohron.skyclaims.permissions.Permissions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.function.Consumer;

public class CommandMigrate implements CommandExecutor {

	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	public static final String HELP_TEXT = "used to switch storage types.";
	private static final Text TYPE = Text.of("type");

	public static CommandSpec commandSpec = CommandSpec.builder()
		.permission(Permissions.COMMAND_MIGRATE)
		.description(Text.of(HELP_TEXT))
		.arguments(GenericArguments.enumValue(TYPE, StorageType.class))
		.executor(new CommandReload())
		.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().debug("Registered command: CommandMigrate");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandMigrate");
		}
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		StorageType type = args.<StorageType>getOne(TYPE).orElse(StorageType.SQLite);

		if (PLUGIN.getConfig().getStorageConfig().getType() == type) {
			src.sendMessage(Text.of(
				TextColors.RED, "The SkyClaims database is already set to ", TextColors.GOLD, type, TextColors.RED, "!"
			));
		} else {
			src.sendMessage(Text.of(
				TextColors.GREEN, "Please be patient while the SkyClaims database is migrated to ", TextColors.GOLD, type, TextColors.GREEN, "!"
			));
			Sponge.getScheduler().createTaskBuilder()
				.execute(migrateStorageType(type, src))
				.async()
				.submit(PLUGIN);
		}
		return CommandResult.success();
	}

	private Consumer<Task> migrateStorageType(StorageType type, CommandSource src) {
		return task -> {
			PLUGIN.setDatabase(type);
			PLUGIN.getDatabase().saveData(SkyClaims.islands);
			PLUGIN.getConfig().getStorageConfig().setType(type);
			PLUGIN.getConfigManager().save();
			src.sendMessage(Text.of(
				TextColors.GREEN, "The SkyClaims database has successfully migrated to ", TextColors.GOLD, type, TextColors.GREEN, "!"
			));
		};
	}
}
