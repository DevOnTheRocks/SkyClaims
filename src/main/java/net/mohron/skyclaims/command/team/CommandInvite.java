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

package net.mohron.skyclaims.command.team;

import com.google.common.collect.ImmutableList;
import net.mohron.skyclaims.command.CommandBase;
import net.mohron.skyclaims.command.CommandIsland;
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.team.Invite;
import net.mohron.skyclaims.team.PrivilegeType;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import javax.annotation.Nonnull;

public class CommandInvite extends CommandBase.IslandCommand {

    public static final String HELP_TEXT = "used to invite players to your island or list your pending invites.";
    private static final Text LIST = Text.of("list");
    private static final Text USER = Text.of("user");
    private static final Text PRIVILEGE = Text.of("privilege");

    public static CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_INVITE)
        .arguments(
            GenericArguments.optional(GenericArguments.seq(
                GenericArguments.user(USER),
                GenericArguments.optional(GenericArguments.enumValue(PRIVILEGE, PrivilegeType.class))
            )),
            GenericArguments.optional(GenericArguments.literal(LIST, "list"))
        )
        .description(Text.of(HELP_TEXT))
        .executor(new CommandInvite())
        .build();

    public static void register() {
        try {
            CommandIsland.addSubCommand(commandSpec, "invite");
            PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
            PLUGIN.getLogger().debug("Registered command: CommandInvite");
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            PLUGIN.getLogger().error("Failed to register command: CommandInvite");
        }
    }

    @Override public CommandResult execute(@Nonnull Player player, @Nonnull Island island, @Nonnull CommandContext args) throws CommandException {
        User user = args.<User>getOne(USER).orElse(null);
        PrivilegeType type = args.<PrivilegeType>getOne(PRIVILEGE).orElse(PrivilegeType.MEMBER);

        if (type == PrivilegeType.NONE) {
            throw new CommandException(
                Text.of(TextStyles.ITALIC, "What kind of invite is ", TextStyles.RESET, type.toText(), TextStyles.ITALIC, "?"));
        }

        if (user == null || args.hasAny(LIST)) {
            PaginationList.builder()
                .title(Text.of(TextColors.AQUA, "Invite List"))
                .header(getIslandLimit(player))
                .padding(Text.of(TextColors.AQUA, TextStyles.STRIKETHROUGH, "-"))
                .contents(PLUGIN.getInviteService().getInviteText(player).isEmpty()
                    ? ImmutableList.of(Text.of(TextColors.RED, "You have no pending invites!"))
                    : PLUGIN.getInviteService().getInviteText(player)
                )
                .sendTo(player);
        } else if (player.equals(user)) {
            throw new CommandException(Text.of(TextColors.RED, "You cannot invite yourself!"));
        } else if (island.getPrivilegeType(user) == type) {
            throw new CommandException(Text.of(
                island.getPrivilegeType(user).format(user.getName()), TextColors.RED, " is already a ", type.toText(), TextColors.RED, "!"
            ));
        } else if (type == PrivilegeType.MEMBER && !island.isManager(player) || !island.getOwnerUniqueId().equals(player.getUniqueId())) {
            throw new CommandException(Text.of(
                TextColors.RED, "You do not have permission to send ", type == PrivilegeType.OWNER ? "an " : "a ",
                type.toText(),
                TextColors.RED, " invite for ",
                island.getName(),
                TextColors.RED, "!"
            ));
        } else if (island.getPrivilegeType(user) == PrivilegeType.MEMBER || island.getPrivilegeType(user) == PrivilegeType.MANAGER) {
            player.sendMessage(Text.of(
                type.format(user.getName()),
                TextColors.GREEN, " has been changed from a ",
                island.getPrivilegeType(user).toText(),
                " to a ",
                type.toText(), "."
            ));
            island.removeMember(user);
            island.addMember(user, type);
        } else {
            Invite invite = Invite.builder()
                .island(island)
                .sender(player)
                .receiver(user)
                .privilegeType(type)
                .build();
            if (PLUGIN.getInviteService().inviteExists(invite)) {
                throw new CommandException(Text.of(TextColors.RED, "Invite already exists!"));
            } else {
                invite.send();
                player.sendMessage(Text.of(TextColors.GREEN, "Island invite sent to ", type.format(user.getName()), TextColors.GREEN, "."));
            }
        }

        return CommandResult.success();
    }

    private Text getIslandLimit(Player player) {
        int limit = Options.getMaxIslands(player.getUniqueId());
        return (limit < 1) ? null : Text.of(TextColors.GRAY, "You may join up to ", TextColors.LIGHT_PURPLE, limit, TextColors.GRAY, " islands.");
    }
}
