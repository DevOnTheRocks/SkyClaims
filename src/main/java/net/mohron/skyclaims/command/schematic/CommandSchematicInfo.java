/*
 * SkyClaims - A Skyblock plugin made for Sponge
 * Copyright (C) 2022 Mohron
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
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class CommandSchematicInfo extends CommandBase {

  public static final String HELP_TEXT = "used to view detailed schematic info";
  private static final Text SCHEMATIC = LinearComponents.linear("schematic");
  private static final LiteralText NONE = LinearComponents.linear("none");

  private enum Category {
    DETAILS, COMMANDS, RESET_COMMANDS
  }

  public static final CommandSpec commandSpec = CommandSpec.builder()
      .permission(Permissions.COMMAND_SCHEMATIC_INFO)
      .description(LinearComponents.linear(HELP_TEXT))
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
        .orElseThrow(() -> new CommandException(LinearComponents.linear(NamedTextColor.RED, "You must provide a schematic to use this command!")));

    getPaginationList(schematic, Category.DETAILS, src).sendTo(src);

    return CommandResult.success();
  }

  private PaginationList getPaginationList(IslandSchematic schematic, Category category, CommandSource src) {
    Text title = LinearComponents.linear(
        schematic.getText(), NamedTextColor.AQUA, " : ",
        category == Category.DETAILS ? NamedTextColor.AQUA : NamedTextColor.GRAY, "[",
        Text.builder("Details")
            .color(category == Category.DETAILS ? NamedTextColor.GREEN : NamedTextColor.GRAY)
            .onHover(TextActions.showText(LinearComponents.linear("Click here to show details")))
            .onClick(TextActions.executeCallback(s -> getPaginationList(schematic, Category.DETAILS, s).sendTo(s))),
        category == Category.DETAILS ? NamedTextColor.AQUA : NamedTextColor.GRAY, "] ",
        category == Category.COMMANDS ? NamedTextColor.AQUA : NamedTextColor.GRAY, "[",
        Text.builder("Commands")
            .color(category == Category.COMMANDS ? NamedTextColor.GREEN : NamedTextColor.GRAY)
            .onHover(TextActions.showText(LinearComponents.linear("Click here to show commands")))
            .onClick(TextActions.executeCallback(s -> getPaginationList(schematic, Category.COMMANDS, s).sendTo(s))),
        category == Category.COMMANDS ? NamedTextColor.AQUA : NamedTextColor.GRAY, "] "
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
        .padding(LinearComponents.linear(NamedTextColor.AQUA, TextStyles.STRIKETHROUGH, "-"))
        .contents(contents)
        .build();
  }

  private List<Text> getDetails(IslandSchematic schematic) {
    List<Text> contents = Lists.newArrayList();
    contents.add(LinearComponents.linear(NamedTextColor.YELLOW, "Author", NamedTextColor.WHITE, " : ", NamedTextColor.GRAY, schematic.getAuthor()));
    contents.add(LinearComponents.linear(NamedTextColor.YELLOW, "Date", NamedTextColor.WHITE, " : ", NamedTextColor.GRAY, schematic.getDate()));
    contents.add(LinearComponents.linear(NamedTextColor.YELLOW, "Filename", NamedTextColor.WHITE, " : ", NamedTextColor.GRAY, schematic.getName()));
    contents.add(LinearComponents.linear(NamedTextColor.YELLOW, "Description", NamedTextColor.WHITE, " : ", NamedTextColor.GRAY, schematic.getDescriptionText()));
    contents.add(LinearComponents.linear(NamedTextColor.YELLOW, "Biome", NamedTextColor.WHITE, " : ", NamedTextColor.GRAY, schematic.getBiomeType().map(CatalogType::getName).orElse("none")));
    contents.add(LinearComponents.linear(NamedTextColor.YELLOW, "Height", NamedTextColor.WHITE, " : ", NamedTextColor.LIGHT_PURPLE, schematic.getHeight().map(Text::of).orElse(NONE)));
    contents.add(LinearComponents.linear(NamedTextColor.YELLOW, "Icon", NamedTextColor.WHITE, " : ", NamedTextColor.GRAY, schematic.getIcon().map(i -> LinearComponents.linear(i).toText()).orElse(NONE)));
    contents.add(LinearComponents.linear(NamedTextColor.YELLOW, "Preset", NamedTextColor.WHITE, " : ", NamedTextColor.GRAY, schematic.getPreset().map(Text::of).orElse(NONE)));
    return contents;
  }

  private List<Text> getCommands(IslandSchematic schematic, boolean canEdit) {
    List<Text> contents = schematic.getCommands().stream()
        .map(s -> LinearComponents.linear(canEdit ? deleteCommandButton(schematic, s) : Text.EMPTY, "/", s))
        .collect(Collectors.toList());
    if (contents.isEmpty()) {
      contents.add(LinearComponents.linear(NamedTextColor.RED, "No commands set"));
    }
    if (canEdit) {
      contents.add(
          LinearComponents.linear(NamedTextColor.WHITE, "[", NamedTextColor.GREEN, "+", NamedTextColor.WHITE, "]", NamedTextColor.GREEN, " Add new command").toBuilder()
              .onHover(TextActions.showText(LinearComponents.linear("Click to add")))
              .onClick(TextActions.suggestCommand("/is schematic command " + schematic.getName() + " add "))
              .build());
    }
    return contents;
  }

  private Text deleteCommandButton(IslandSchematic schematic, String command) {
    return LinearComponents.linear(NamedTextColor.WHITE, "[",
        Text.builder("✗")
            .color(NamedTextColor.RED)
            .onHover(TextActions.showText(LinearComponents.linear(NamedTextColor.RED, "Click to delete")))
            .onClick(TextActions.executeCallback(src -> {
              List<String> commands = schematic.getCommands();
              commands.remove(command);
              schematic.setCommands(commands);
              if (PLUGIN.getSchematicManager().save(schematic)) {
                src.sendMessage(LinearComponents.linear(NamedTextColor.GREEN, "Successfully removed ", NamedTextColor.WHITE, command, NamedTextColor.GREEN, "."));
              } else {
                src.sendMessage(LinearComponents.linear(NamedTextColor.RED, "Failed to remove ", NamedTextColor.WHITE, command, NamedTextColor.RED, "."));
              }
            })),
        NamedTextColor.WHITE, "] ");
  }
}
