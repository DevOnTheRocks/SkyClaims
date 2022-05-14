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

import java.util.List;
import java.util.stream.Collectors;
import net.mohron.skyclaims.command.CommandBase;
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

public class CommandSchematicList extends CommandBase {

  public static final String HELP_TEXT = "used to list available island schematics";

  public static CommandSpec commandSpec = CommandSpec.builder()
      .permission(Permissions.COMMAND_SCHEMATIC_LIST)
      .arguments(GenericArguments.flags().permissionFlag(Permissions.COMMAND_SCHEMATIC_LIST_ALL, "a", "-all").buildWith(GenericArguments.none()))
      .executor(new CommandSchematicList())
      .build();

  public static void register() {
    try {
      Sponge.getCommandManager().register(PLUGIN, commandSpec);
      PLUGIN.getLogger().debug("Registered command: CommandSchematicList");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandSchematicList", e);
    }
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    boolean canDelete = src.hasPermission(Permissions.COMMAND_SCHEMATIC_DELETE);
    boolean checkPerms = !args.<Boolean>getOne("a").orElse(false) && PLUGIN.getConfig().getPermissionConfig().isSeparateSchematicPerms();
    List<Text> schematics = PLUGIN.getSchematicManager().getSchematics().stream()
        .filter(s -> !checkPerms || src.hasPermission(Permissions.COMMAND_ARGUMENTS_SCHEMATICS + "." + s.getName()))
        .map(s -> LinearComponents.linear(
            canDelete ? deleteSchematicButton(s) : Text.EMPTY,
            s.getText().toBuilder()
                .onHover(TextActions.showText(LinearComponents.linear("Click to view schematic info")))
                .onClick(TextActions.runCommand("/is schematic info " + s.getName())),
            NamedTextColor.WHITE, " - ",
            NamedTextColor.GRAY, s.getBiomeType().isPresent() ? s.getBiomeType().get().getName() : "none", NamedTextColor.WHITE, " - ",
            NamedTextColor.LIGHT_PURPLE, s.getCommands().size(), NamedTextColor.GRAY, " command", s.getCommands().size() != 1 ? "s" : ""
        ))
        .collect(Collectors.toList());

    PaginationList.builder()
        .title(LinearComponents.linear(NamedTextColor.AQUA, "Schematics"))
        .header(LinearComponents.linear(TextStyles.BOLD, "NAME", " - ", "BIOME", " - ", "COMMANDS"))
        .padding(LinearComponents.linear(NamedTextColor.AQUA, TextStyles.STRIKETHROUGH, "-"))
        .contents(schematics)
        .sendTo(src);

    return CommandResult.success();
  }

  private Text deleteSchematicButton(IslandSchematic schematic) {
    return LinearComponents.linear(NamedTextColor.WHITE, "[",
        Text.builder("✗")
            .color(NamedTextColor.RED)
            .onHover(TextActions.showText(LinearComponents.linear(NamedTextColor.RED, "Click to delete")))
            .onClick(TextActions.executeCallback(src -> src.sendMessage(LinearComponents.linear(
                NamedTextColor.WHITE, "Are you sure you want to delete ", schematic.getText(), NamedTextColor.WHITE, "?", Component.newline(),
                NamedTextColor.WHITE, "[",
                Text.builder("YES")
                    .color(NamedTextColor.GREEN)
                    .onHover(TextActions.showText(LinearComponents.linear("Click to delete")))
                    .onClick(TextActions.executeCallback(source -> {
                      if (PLUGIN.getSchematicManager().delete(schematic)) {
                        src.sendMessage(LinearComponents.linear(NamedTextColor.GREEN, "Successfully deleted ", NamedTextColor.WHITE, schematic.getText(), NamedTextColor.GREEN, "."));
                      } else {
                        src.sendMessage(LinearComponents.linear(NamedTextColor.RED, "Failed to delete ", NamedTextColor.WHITE, schematic.getText(), NamedTextColor.RED, "."));
                      }
                    })),
                NamedTextColor.WHITE, "] [",
                Text.builder("NO")
                    .color(NamedTextColor.RED)
                    .onHover(TextActions.showText(LinearComponents.linear("Click to cancel")))
                    .onClick(TextActions.executeCallback(s -> s.sendMessage(LinearComponents.linear("Schematic deletion canceled!")))),
                NamedTextColor.WHITE, "]"
            )))),
        NamedTextColor.WHITE, "] ");
  }
}
