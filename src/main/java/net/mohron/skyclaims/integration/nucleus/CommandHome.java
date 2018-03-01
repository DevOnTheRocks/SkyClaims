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

package net.mohron.skyclaims.integration.nucleus;

import io.github.nucleuspowered.nucleus.api.nucleusdata.Home;
import io.github.nucleuspowered.nucleus.api.nucleusdata.NamedLocation;
import java.util.Optional;
import net.mohron.skyclaims.command.CommandBase;
import net.mohron.skyclaims.command.CommandIsland;
import net.mohron.skyclaims.permissions.Permissions;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

public class CommandHome extends CommandBase {

  public static final String HELP_TEXT = "teleport to your home island.";

  public static CommandSpec commandSpec = CommandSpec.builder()
      .permission(Permissions.COMMAND_HOME)
      .description(Text.of(HELP_TEXT))
      .executor(new CommandHome())
      .build();

  public static void register() {
    try {
      CommandIsland.addSubCommand(commandSpec, "home");
      PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
      PLUGIN.getLogger().debug("Registered command: CommandHome");
    } catch (UnsupportedOperationException e) {
      e.printStackTrace();
      PLUGIN.getLogger().error("Failed to register command: CommandHome");
    }
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (!(src instanceof Player)) {
      throw new CommandException(Text.of("You must be a player to use this command!"));
    }

    Player player = (Player) src;
    Transform<World> transform = getHome(player)
        .orElseThrow(() -> new CommandException(
            Text.of(TextColors.RED, "You must set a home before using this command!")));

    player.setTransformSafely(transform);

    return CommandResult.success();
  }

  private Optional<Transform<World>> getHome(User user) {
    Optional<Home> oHome = NucleusIntegration.getHomeService().getHome(user, "Island");
    return oHome.flatMap(NamedLocation::getTransform);
  }
}
