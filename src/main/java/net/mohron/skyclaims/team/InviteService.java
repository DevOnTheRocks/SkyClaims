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
import net.mohron.skyclaims.world.Island;
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
    boolean canJoin = Options.getMaxIslands(user.getUniqueId()) < 1 || Options.getMaxIslands(user.getUniqueId()) - Island.getTotalIslands(user) > 0;
    return invites.row(user).values().stream()
        .map(invite -> Text.of(
            TextColors.WHITE, "[",
            Text.builder("✓")
                .color(TextColors.GREEN)
                .onHover(TextActions.showText(Text.of(TextColors.GREEN, "Accept")))
                .onClick(TextActions.executeCallback(acceptInvite(invite, canJoin))),
            TextColors.WHITE, "] [",
            Text.builder("✗")
                .color(TextColors.RED)
                .onHover(TextActions.showText(Text.of(TextColors.RED, "Deny")))
                .onClick(TextActions.executeCallback(src -> invite.deny())),
            TextColors.WHITE, "] ",
            invite.getPrivilegeType().toText(),
            TextColors.WHITE, " : ",
            invite.getIsland().getName().toBuilder()
                .onHover(TextActions.showText(Text.of(
                    TextColors.YELLOW, "Invited by", TextColors.WHITE, " : ", TextColors.GOLD,
                    invite.getSender().getName(), Text.NEW_LINE,
                    TextColors.YELLOW, "Sent on", TextColors.WHITE, " : ", TextColors.GRAY,
                    sdf.format(Date.from(invite.getSent()))
                )))
        ))
        .collect(Collectors.toList());
  }

  private Consumer<CommandSource> acceptInvite(Invite invite, boolean canJoin) {
    return src -> {
      if (canJoin) {
        invite.accept();
      } else {
        src.sendMessage(Text.of(TextColors.RED, "You have reached your maximum number of islands!"));
      }
    };
  }

  private List<Text> getOutgoingInviteText(User user) {
    SimpleDateFormat sdf = PLUGIN.getConfig().getMiscConfig().getDateFormat();
    return invites.column(user).values().stream()
        .map(invite -> Text.of(
            TextColors.WHITE, "[",
            Text.builder("✗")
                .color(TextColors.RED)
                .onHover(TextActions.showText(Text.of(TextColors.RED, "Cancel")))
                .onClick(TextActions.executeCallback(src -> {
                  invite.deny();
                  src.sendMessage(Text.of(
                      TextColors.GREEN, "Invite to ",
                      invite.getPrivilegeType().format(invite.getReceiver().getName()),
                      TextColors.GREEN, " has been canceled."
                  ));
                })),
            TextColors.WHITE, "] ",
            invite.getPrivilegeType().format(invite.getReceiver().getName()),
            TextColors.WHITE, " : ",
            invite.getIsland().getName(),
            TextColors.WHITE, " : ",
            TextColors.GRAY, sdf.format(Date.from(invite.getSent()))
        ))
        .collect(Collectors.toList());
  }

  private Consumer<CommandSource> listInvites(Type type) {
    return src -> {
      if (src instanceof Player) {
        Player player = (Player) src;

        Text title = Text.of(
            TextColors.AQUA, "Invites : ",
            TextColors.AQUA, (type == Type.INCOMING) ? "[" : Text.EMPTY,
            Text.builder("Incoming")
                .color((type == Type.INCOMING) ? TextColors.GREEN : TextColors.GRAY)
                .onHover(TextActions.showText(
                    Text.of("Click here to show ", TextColors.GREEN, "incoming", TextColors.RESET,
                        " invites")))
                .onClick(TextActions.executeCallback(listIncomingInvites())),
            TextColors.AQUA, (type == Type.INCOMING) ? "] " : " [",
            Text.builder("Outgoing")
                .color((type == Type.OUTGOING) ? TextColors.YELLOW : TextColors.GRAY)
                .onHover(TextActions.showText(
                    Text.of("Click here to show ", TextColors.YELLOW, "outgoing", TextColors.RESET,
                        " invites")))
                .onClick(TextActions.executeCallback(listOutgoingInvites())),
            TextColors.AQUA, (type == Type.OUTGOING) ? "]" : Text.EMPTY
        );

        List<Text> contents = (type == Type.INCOMING)
            ? PLUGIN.getInviteService().getIncomingInviteText(player)
            : PLUGIN.getInviteService().getOutgoingInviteText(player);
        if (contents.isEmpty()) {
          contents = ImmutableList.of(Text
              .of(TextColors.RED, "You have no ", type.toString().toLowerCase(), " invites!"));
        }

        int limit = Options.getMaxIslands(player.getUniqueId());
        Text header = limit < 1
            ? null
            : Text.of(TextColors.GRAY, "You currently have ", TextColors.LIGHT_PURPLE, Island.getTotalIslands(player), TextColors.GRAY, " of ",
                TextColors.LIGHT_PURPLE, limit, TextColors.GRAY, " maximum island", limit > 1 ? "s" : Text.EMPTY, ".");

        PaginationList.builder()
            .title(title)
            .header(header)
            .padding(Text.of(TextColors.AQUA, TextStyles.STRIKETHROUGH, "-"))
            .contents(contents)
            .sendTo(player);
      }
    };
  }
}
