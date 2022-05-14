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

package net.mohron.skyclaims.command.debug;

import com.google.common.collect.Lists;
import com.griefdefender.api.GriefDefender;
import java.util.List;
import java.util.Optional;
import net.mohron.skyclaims.PluginInfo;
import net.mohron.skyclaims.command.CommandBase;
import net.mohron.skyclaims.permissions.Permissions;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CommandVersion extends CommandBase {

  public static final String HELP_TEXT = "used to view loaded config settings.";

  public static void register() {
    CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_VERSION)
        .description(LinearComponents.linear(HELP_TEXT))
        .executor(new CommandVersion())
        .build();

    try {
      PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec, "scversion");
      PLUGIN.getLogger().debug("Registered command: CommandVersion");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandVersion", e);
    }
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    List<Text> texts = Lists.newArrayList();

    // Minecraft
    texts.add(LinearComponents.linear(
        NamedTextColor.DARK_AQUA, "Minecraft", NamedTextColor.WHITE, " : ",
        NamedTextColor.YELLOW, Sponge.getPlatform().getMinecraftVersion().getName()
    ));
    // Sponge
    PluginContainer sponge = Sponge.getPlatform().getContainer(Platform.Component.IMPLEMENTATION);
    texts.add(LinearComponents.linear(
        NamedTextColor.DARK_AQUA, "Sponge", NamedTextColor.WHITE, " : ",
        NamedTextColor.YELLOW, sponge.getName(), " ", sponge.getVersion().orElse("Unknown")
    ));
    // SkyClaims
    texts.add(LinearComponents.linear(NamedTextColor.DARK_AQUA, "SkyClaims", NamedTextColor.WHITE, " : ", NamedTextColor.YELLOW, PluginInfo.VERSION));
    // GriefDefender
    String gd = "Error/Missing";
    try {
      gd = GriefDefender.getVersion().getImplementationVersion();
    } catch (Exception e) {
      PLUGIN.getLogger().error("Error getting Grief Defender version.", e);
    }
    texts.add(LinearComponents.linear(NamedTextColor.DARK_AQUA, "Grief Defender", NamedTextColor.WHITE, " : ", NamedTextColor.YELLOW, gd));
    // Permissions
    PluginContainer perms = Sponge.getServiceManager().getRegistration(PermissionService.class).get().getPlugin();
    texts.add(LinearComponents.linear(
        NamedTextColor.DARK_AQUA, "Permissions", NamedTextColor.WHITE, " : ",
        NamedTextColor.YELLOW, perms.getName(), " ", perms.getVersion().orElse("Unknown")
    ));
    // Nucleus
    Optional<PluginContainer> nucleus = Sponge.getPluginManager().getPlugin("nucleus");
    texts.add(LinearComponents.linear(
        NamedTextColor.DARK_AQUA, "Nucleus", NamedTextColor.WHITE, " : ",
        NamedTextColor.YELLOW,
        nucleus.isPresent() ? nucleus.get().getVersion().orElse("Unknown") : "Not Installed"
    ));

    texts.forEach(src::sendMessage);

    return CommandResult.success();
  }
}
