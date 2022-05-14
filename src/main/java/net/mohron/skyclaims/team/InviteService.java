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

package net.mohron.skyclaims.team;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.world.IslandManager;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class InviteService {

  private static final SkyClaims PLUGIN = SkyClaims.getInstance();

  private Table<User, User, Invite> invites = HashBasedTable.create();

  public InviteService() {

  }

  private enum Type {
    INCOMING, OUTGOING
  }

  void addInvite(Invite invite) {
    invites.put(invite.getReceiver(), invite.getSender(), invite);
  }

  void removeInvite(Invite invite) {
    invites.remove(invite.getReceiver(), invite.getSender());
  }

  public boolean inviteExists(Invite invite) {
    return invites.contains(invite.getReceiver(), invite.getSender());
  }

  public int getInviteCount(User user) {
    return invites.row(user).values().size();
  }

  public Consumer<CommandSource> listIncomingInvites() {
    return listInvites(Type.INCOMING);
  }

  public Consumer<CommandSource> listOutgoingInvites() {
    return listInvites(Type.OUTGOING);
  }

  private List<Text> getIncomingInviteText(User user) {
    SimpleDateFormat sdf = PLUGIN.getConfig().getMiscConfig().getDateFormat();
    return invites.row(user).values().stream()
        .map(invite -> LinearComponents.linear(
            NamedTextColor.WHITE, "[",
            Text.builder("✓")
                .color(NamedTextColor.GREEN)
                .onHover(TextActions.showText(LinearComponents.linear(NamedTextColor.GREEN, "Accept")))
                .onClick(TextActions.executeCallback(acceptInvite(invite))),
            NamedTextColor.WHITE, "] [",
            Text.builder("✗")
                .color(NamedTextColor.RED)
                .onHover(TextActions.showText(LinearComponents.linear(NamedTextColor.RED, "Deny")))
                .onClick(TextActions.executeCallback(src -> invite.deny())),
            NamedTextColor.WHITE, "] ",
            invite.getPrivilegeType().toText(),
            NamedTextColor.WHITE, " : ",
            invite.getIsland().getName().toBuilder()
                .onHover(TextActions.showText(LinearComponents.linear(
                    NamedTextColor.YELLOW, "Invited by", NamedTextColor.WHITE, " : ", NamedTextColor.GOLD,
                    invite.getSender().getName(), Component.newline(),
                    NamedTextColor.YELLOW, "Sent on", NamedTextColor.WHITE, " : ", NamedTextColor.GRAY,
                    sdf.format(Date.from(invite.getSent()))
                )))
        ))
        .collect(Collectors.toList());
  }

  Consumer<CommandSource> acceptInvite(Invite invite) {
    return src -> {
      int maxIslands = Options.getMaxIslands(invite.getReceiver().getUniqueId());
      int maxTeammates = Options.getMaxTeammates(invite.getIsland().getOwnerUniqueId());

      // Check if the receiver can accept this invite
      if (!inviteExists(invite)) {
        src.sendMessage(LinearComponents.linear(NamedTextColor.RED, "This invite has expired!"));
      } else if (maxIslands > 0 && maxIslands - IslandManager.countByMember(invite.getReceiver()) < 1) {
        src.sendMessage(LinearComponents.linear(NamedTextColor.RED, "You have reached your maximum number of islands!"));
      } else if (maxTeammates > 0 && invite.getIsland().getTotalMembers() >= maxTeammates) {
        src.sendMessage(LinearComponents.linear(invite.getIsland().getName(), NamedTextColor.RED, " has reached its maximum team size!"));
      } else {
        invite.accept();
        src.sendMessage(LinearComponents.linear(
            NamedTextColor.GREEN, "You are now a ",
            invite.getPrivilegeType().toText(),
            NamedTextColor.GREEN, " on ",
            invite.getIsland().getName(), NamedTextColor.GREEN, "!"
        ));
      }
    };
  }

  private List<Text> getOutgoingInviteText(User user) {
    SimpleDateFormat sdf = PLUGIN.getConfig().getMiscConfig().getDateFormat();
    return invites.column(user).values().stream()
        .map(invite -> LinearComponents.linear(
            NamedTextColor.WHITE, "[",
            Text.builder("✗")
                .color(NamedTextColor.RED)
                .onHover(TextActions.showText(LinearComponents.linear(NamedTextColor.RED, "Cancel")))
                .onClick(TextActions.executeCallback(src -> {
                  invite.deny();
                  src.sendMessage(LinearComponents.linear(
                      NamedTextColor.GREEN, "Invite to ",
                      invite.getPrivilegeType().format(invite.getReceiver().getName()),
                      NamedTextColor.GREEN, " has been canceled."
                  ));
                })),
            NamedTextColor.WHITE, "] ",
            invite.getPrivilegeType().format(invite.getReceiver().getName()),
            NamedTextColor.WHITE, " : ",
            invite.getIsland().getName(),
            NamedTextColor.WHITE, " : ",
            NamedTextColor.GRAY, sdf.format(Date.from(invite.getSent()))
        ))
        .collect(Collectors.toList());
  }

  private Consumer<CommandSource> listInvites(Type type) {
    return src -> {
      if (src instanceof Player) {
        Player player = (Player) src;

        Text title = LinearComponents.linear(
            NamedTextColor.AQUA, "Invites : ",
            NamedTextColor.AQUA, (type == Type.INCOMING) ? "[" : Text.EMPTY,
            Text.builder("Incoming")
                .color((type == Type.INCOMING) ? NamedTextColor.GREEN : NamedTextColor.GRAY)
                .onHover(TextActions.showText(LinearComponents.linear("Click here to show ", NamedTextColor.GREEN, "incoming", NamedTextColor.RESET, " invites")))
                .onClick(TextActions.executeCallback(listIncomingInvites())),
            NamedTextColor.AQUA, (type == Type.INCOMING) ? "] " : " [",
            Text.builder("Outgoing")
                .color((type == Type.OUTGOING) ? NamedTextColor.YELLOW : NamedTextColor.GRAY)
                .onHover(TextActions.showText(LinearComponents.linear("Click here to show ", NamedTextColor.YELLOW, "outgoing", NamedTextColor.RESET, " invites")))
                .onClick(TextActions.executeCallback(listOutgoingInvites())),
            NamedTextColor.AQUA, (type == Type.OUTGOING) ? "]" : Text.EMPTY
        );

        List<Text> contents = (type == Type.INCOMING)
            ? PLUGIN.getInviteService().getIncomingInviteText(player)
            : PLUGIN.getInviteService().getOutgoingInviteText(player);
        if (contents.isEmpty()) {
          contents = ImmutableList.of(LinearComponents.linear(NamedTextColor.RED, "You have no ", type.toString().toLowerCase(), " invites!"));
        }

        int limit = Options.getMaxIslands(player.getUniqueId());
        Text header = LinearComponents.linear(
            NamedTextColor.GRAY, "You currently have ",
            NamedTextColor.LIGHT_PURPLE, IslandManager.countByMember(player),
            NamedTextColor.GRAY, " of ",
            NamedTextColor.LIGHT_PURPLE, limit, 
            NamedTextColor.GRAY, " maximum island",
            limit > 1 ? "s" : Text.EMPTY, "."
        );

        PaginationList.builder()
            .title(title)
            .header(limit < 1 ? null : header)
            .padding(LinearComponents.linear(NamedTextColor.AQUA, TextStyles.STRIKETHROUGH, "-"))
            .contents(contents)
            .sendTo(player);
      }
    };
  }
}
