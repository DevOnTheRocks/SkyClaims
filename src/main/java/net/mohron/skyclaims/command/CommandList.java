package net.mohron.skyclaims.command;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.lib.Permissions;
import net.mohron.skyclaims.util.CommandUtil;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.function.Consumer;

public class CommandList implements CommandExecutor {

	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static String helpText = "display a list of the current islands";

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_LIST)
			.description(Text.of(helpText))
			.executor(new CommandList())
			.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec, "islandlist");
			PLUGIN.getLogger().debug("Registered command: CommandList");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandList");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (SkyClaims.islands.isEmpty())
			src.sendMessage(Text.of("There are currently no islands!"));
		Text listText = Text.EMPTY;
		boolean newline = false;

		for (Island island : SkyClaims.islands.values()) {
			if (!island.isLocked() && !src.hasPermission(Permissions.COMMAND_LIST_ALL)) continue;
			Text name = Text.of((island.isLocked()) ? TextColors.DARK_PURPLE : TextColors.AQUA, island.getName());
			Text coords = Text.of(TextColors.GRAY, " (", TextColors.LIGHT_PURPLE, island.getRegion().getX(), TextColors.GRAY, ", ", TextColors.LIGHT_PURPLE, island.getRegion().getZ(), TextColors.GRAY, ") - ");

			listText = Text.join(listText, Text.of(
					(newline) ? "\n" : "",
					name.toBuilder()
							.onHover(TextActions.showText(Text.of("Click here to view island info")))
							.onClick(TextActions.executeCallback(CommandUtil.createCommandConsumer(src, "islandinfo", island.getUniqueId().toString(), createReturnConsumer(src)))),
					coords.toBuilder()
							.onHover(TextActions.showText(Text.of("Click here to teleport to this island.")))
							.onClick(TextActions.executeCallback(CommandUtil.createTeleportConsumer(src, island.getSpawn()))),
					TextColors.GREEN, Text.builder("Claim").
							onClick(TextActions.executeCallback(CommandUtil.createCommandConsumer(src, "claiminfo", island.getClaim().getUniqueId().toString(), createReturnConsumer(src))))
							.onHover(TextActions.showText(Text.of("Click here to view claim info.")))
			));
			newline = true;
		}

		PaginationList.Builder paginationBuilder = PaginationList.builder().title(Text.of(TextColors.AQUA, "Island List")).padding(Text.of(TextColors.AQUA, TextStyles.STRIKETHROUGH, "-")).contents(listText);
		paginationBuilder.sendTo(src);

		return CommandResult.success();
	}

	private static Consumer<CommandSource> createReturnConsumer(CommandSource src) {
		return consumer -> {
			Text returnCommand = Text.builder().append(Text.of(
					TextColors.WHITE, "\n[", TextColors.AQUA, "Return to Island List", TextColors.WHITE, "]\n"))
					.onClick(TextActions.executeCallback(CommandUtil.createCommandConsumer(src, "islandlist", "", null))).build();
			src.sendMessage(returnCommand);
		};
	}
}