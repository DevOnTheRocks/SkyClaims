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

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import net.mohron.skyclaims.command.CommandBase;
import net.mohron.skyclaims.command.argument.Arguments;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.schematic.IslandSchematic;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class CommandSchematicInfo extends CommandBase {

  public static final String HELP_TEXT = "used to view detailed schematic info";
  private static final Text SCHEMATIC = Text.of("schematic");

  private enum Category {
    DETAILS, COMMANDS, RESET_COMMANDS
  }

  public static CommandSpec commandSpec = CommandSpec.builder()
      .permission(Permissions.COMMAND_SCHEMATIC_INFO)
      .description(Text.of(HELP_TEXT))
      .arguments(Arguments.schematic(SCHEMATIC))
      .executor(new CommandSchematicInfo())
      .build();

  public static void register() {
    try {
      PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
      PLUGIN.getLogger().debug("Registered command: CommandSchematicInfo");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandSchematicInfo", e);
    }
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    IslandSchematic schematic = args.<IslandSchematic>getOne(SCHEMATIC)
        .orElseThrow(() -> new CommandException(Text.of(TextColors.RED, "You must provide a schematic to use this command!")));

    getPaginationList(schematic, Category.DETAILS, src).sendTo(src);

    return CommandResult.success();
  }

  private PaginationList getPaginationList(IslandSchematic schematic, Category category, CommandSource src) {
    Text title = Text.of(
        schematic.getText(), TextColors.AQUA, " : ",
        category == Category.DETAILS ? TextColors.AQUA : TextColors.GRAY, "[",
        Text.builder("Details")
            .color(category == Category.DETAILS ? TextColors.GREEN : TextColors.GRAY)
            .onHover(TextActions.showText(Text.of("Click here to show details")))
            .onClick(TextActions.executeCallback(s -> getPaginationList(schematic, Category.DETAILS, s).sendTo(s))),
        category == Category.DETAILS ? TextColors.AQUA : TextColors.GRAY, "] ",
        category == Category.COMMANDS ? TextColors.AQUA : TextColors.GRAY, "[",
        Text.builder("Commands")
            .color(category == Category.COMMANDS ? TextColors.GREEN : TextColors.GRAY)
            .onHover(TextActions.showText(Text.of("Click here to show commands")))
            .onClick(TextActions.executeCallback(s -> getPaginationList(schematic, Category.COMMANDS, s).sendTo(s))),
        category == Category.COMMANDS ? TextColors.AQUA : TextColors.GRAY, "] "
    );

    List<Text> contents = Lists.newArrayList();
    switch (category) {
      case DETAILS:
        contents = getDetails(schematic);
        break;
      case COMMANDS:
        contents = getCommands(schematic, src.hasPermission(Permissions.COMMAND_SCHEMATIC_COMMAND));
        break;
    }

    return PaginationList.builder()
        .title(title)
        .padding(Text.of(TextColors.AQUA, TextStyles.STRIKETHROUGH, "-"))
        .contents(contents)
        .build();
  }

  private List<Text> getDetails(IslandSchematic schematic) {
    List<Text> contents = Lists.newArrayList();
    contents.add(Text.of(TextColors.YELLOW, "Author", TextColors.WHITE, " : ", TextColors.GRAY, schematic.getAuthor()));
    contents.add(Text.of(TextColors.YELLOW, "Date", TextColors.WHITE, " : ", TextColors.GRAY, schematic.getDate()));
    contents.add(Text.of(TextColors.YELLOW, "Filename", TextColors.WHITE, " : ", TextColors.GRAY, schematic.getName()));
    contents.add(Text.of(TextColors.YELLOW, "Biome", TextColors.WHITE, " : ", TextColors.GRAY, schematic.getBiomeType().map(CatalogType::getName).orElse("none")));
    contents.add(Text.of(TextColors.YELLOW, "Height", TextColors.WHITE, " : ", TextColors.LIGHT_PURPLE, schematic.getHeight().map(Text::of).orElse(Text.of("none"))));
    return contents;
  }

  private List<Text> getCommands(IslandSchematic schematic, boolean canEdit) {
    List<Text> contents = schematic.getCommands().stream()
        .map(s -> Text.of(canEdit ? deleteCommandButton(schematic, s) : Text.EMPTY, "/", s))
        .collect(Collectors.toList());
    if (contents.isEmpty()) {
      contents.add(Text.of(TextColors.RED, "No commands set"));
    }
    if (canEdit) {
      contents.add(
          Text.of(TextColors.WHITE, "[", TextColors.GREEN, "+", TextColors.WHITE, "]", TextColors.GREEN, " Add new command").toBuilder()
              .onHover(TextActions.showText(Text.of("Click to add")))
              .onClick(TextActions.suggestCommand("/is schematic command " + schematic.getName() + " add "))
              .build());
    }
    return contents;
  }

  private Text deleteCommandButton(IslandSchematic schematic, String command) {
    return Text.of(TextColors.WHITE, "[",
        Text.builder("✗")
            .color(TextColors.RED)
            .onHover(TextActions.showText(Text.of(TextColors.RED, "Click to delete")))
            .onClick(TextActions.executeCallback(src -> {
              List<String> commands = schematic.getCommands();
              commands.remove(command);
              schematic.setCommands(commands);
              if (PLUGIN.getSchematicManager().save(schematic)) {
                src.sendMessage(Text.of(TextColors.GREEN, "Successfully removed ", TextColors.WHITE, command, TextColors.GREEN, "."));
              } else {
                src.sendMessage(Text.of(TextColors.RED, "Failed to remove ", TextColors.WHITE, command, TextColors.RED, "."));
              }
            })),
        TextColors.WHITE, "] ");
  }
}
