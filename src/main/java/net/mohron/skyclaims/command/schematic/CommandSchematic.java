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

package net.mohron.skyclaims.command.schematic;

import java.util.List;
import java.util.stream.Collectors;
import net.mohron.skyclaims.command.CommandBase;
import net.mohron.skyclaims.command.CommandIsland;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.schematic.IslandSchematic;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class CommandSchematic extends CommandBase {

  public static final String HELP_TEXT = "used to manage island schematics";
  private static final Text LIST = Text.of("list");

  public static CommandSpec commandSpec = CommandSpec.builder()
      .permission(Permissions.COMMAND_SCHEMATIC)
      .description(Text.of(HELP_TEXT))
      .child(CommandSchematicCommand.commandSpec, "command")
      .child(CommandSchematicCreate.commandSpec, "create")
      .child(CommandSchematicDelete.commandSpec, "delete")
      .child(CommandSchematicInfo.commandSpec, "info")
      .child(CommandSchematicSetBiome.commandSpec, "setbiome")
      .child(CommandSchematicSetName.commandSpec, "setname")
      .childArgumentParseExceptionFallback(false)
      .arguments(GenericArguments.optionalWeak(GenericArguments.onlyOne(GenericArguments.literal(LIST, "list"))))
      .executor(new CommandSchematic())
      .build();

  public static void register() {
    try {
      registerSubCommands();
      CommandIsland.addSubCommand(commandSpec, "schematic");
      Sponge.getCommandManager().register(PLUGIN, commandSpec);
      PLUGIN.getLogger().debug("Registered command: CommandSchematic");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandSchematic", e);
    }
  }

  private static void registerSubCommands() {
    CommandSchematicCommand.register();
    CommandSchematicCreate.register();
    CommandSchematicDelete.register();
    CommandSchematicInfo.register();
    CommandSchematicSetBiome.register();
    CommandSchematicSetName.register();
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    boolean canDelete = src.hasPermission(Permissions.COMMAND_SCHEMATIC_DELETE);
    List<Text> schematics = PLUGIN.getSchematicManager().getSchematics().stream()
        .map(s -> Text.of(
            canDelete ? deleteSchematicButton(s) : Text.EMPTY,
            s.getText().toBuilder()
                .onHover(TextActions.showText(Text.of("Click to view schematic info")))
                .onClick(TextActions.runCommand("/is schematic info " + s.getName())),
            TextColors.WHITE, " - ",
            TextColors.GRAY, s.getBiomeType().isPresent() ? s.getBiomeType().get().getName() : "none", TextColors.WHITE, " - ",
            TextColors.LIGHT_PURPLE, s.getCommands().size(), TextColors.GRAY, " command", s.getCommands().size() != 1 ? "s" : ""
        ))
        .collect(Collectors.toList());

    PaginationList.builder()
        .title(Text.of(TextColors.AQUA, "Schematics"))
        .header(Text.of(TextStyles.BOLD, "NAME", " - ", "BIOME", " - ", "COMMANDS"))
        .padding(Text.of(TextColors.AQUA, TextStyles.STRIKETHROUGH, "-"))
        .contents(schematics)
        .sendTo(src);

    return CommandResult.success();
  }

  private Text deleteSchematicButton(IslandSchematic schematic) {
    return Text.of(TextColors.WHITE, "[",
        Text.builder("✗")
            .color(TextColors.RED)
            .onHover(TextActions.showText(Text.of(TextColors.RED, "Click to delete")))
            .onClick(TextActions.executeCallback(src -> {
              if (PLUGIN.getSchematicManager().delete(schematic)) {
                src.sendMessage(Text.of(TextColors.GREEN, "Successfully deleted ", TextColors.WHITE, schematic.getName(), TextColors.GREEN, "."));
              } else {
                src.sendMessage(Text.of(TextColors.RED, "Failed to delete ", TextColors.WHITE, schematic.getName(), TextColors.RED, "."));
              }
            })),
        TextColors.WHITE, "] ");
  }
}
