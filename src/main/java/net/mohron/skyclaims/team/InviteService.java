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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class InviteService {

    private Multimap<User, Invite> invites = HashMultimap.create();

    public InviteService() {

    }

    public Collection<Invite> getInvites(User user) {
        return invites.get(user);
    }

    public List<Text> getInviteText(User user) {
        return invites.get(user).stream()
            .map(invite -> Text.of(
                TextColors.WHITE, "[",
                Text.builder("✓")
                    .color(TextColors.GREEN)
                    .onHover(TextActions.showText(Text.of(TextColors.GREEN, "Accept")))
                    .onClick(TextActions.executeCallback(src -> invite.accept())),
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
                        TextColors.YELLOW, "Invited by", TextColors.WHITE, " : ", TextColors.GOLD, invite.getSender().getName(), Text.NEW_LINE,
                        TextColors.YELLOW, "Sent on", TextColors.WHITE, " : ", TextColors.GRAY, Date.from(invite.getSent())
                    )))
            ))
            .collect(Collectors.toList());
    }

    void addInvite(User user, Invite invite) {
        invites.put(user, invite);
    }

    void removeInvite(User user, Invite invite) {
        invites.remove(user, invite);
    }

    public boolean inviteExists(Invite invite) {
        return invites.containsValue(invite);
    }
}
