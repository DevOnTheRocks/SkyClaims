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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.time.Instant;
import javax.annotation.Nonnull;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.world.Island;
import net.mohron.skyclaims.world.IslandManager;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

public class Invite {

  public static final SkyClaims PLUGIN = SkyClaims.getInstance();

  private final Island island;
  private final User sender;
  private final User receiver;
  private final PrivilegeType privilegeType;
  private final Instant sent;

  private Invite(Island island, User sender, User receiver, PrivilegeType privilegeType) {
    this.island = island;
    this.sender = sender;
    this.receiver = receiver;
    this.privilegeType = privilegeType;
    this.sent = Instant.now();
    PLUGIN.getInviteService().addInvite(this);
  }

  public Island getIsland() {
    return island;
  }

  public User getSender() {
    return sender;
  }

  public User getReceiver() {
    return receiver;
  }

  public PrivilegeType getPrivilegeType() {
    return privilegeType;
  }

  public Instant getSent() {
    return sent;
  }

  public void send() {
    receiver.getPlayer().ifPresent(p -> p.sendMessage(Text.of(
        island.getPrivilegeType(sender).format(sender.getName()),
        TextColors.GRAY, " has invited you to be a ",
        privilegeType.toText(),
        TextColors.GRAY, " of ",
        island.getName(),
        TextColors.GRAY, "!", Text.NEW_LINE,
        TextColors.WHITE, "[",
        Text.builder("ACCEPT")
            .color(TextColors.GREEN)
            .onHover(TextActions.showText(Text.of(TextColors.GREEN, "Click to accept")))
            .onClick(TextActions.executeCallback(PLUGIN.getInviteService().acceptInvite(this))),
        TextColors.WHITE, "] [",
        Text.builder("DENY")
            .color(TextColors.RED)
            .onHover(TextActions.showText(Text.of(TextColors.RED, "Click to deny")))
            .onClick(TextActions.executeCallback(src -> {
              if (PLUGIN.getInviteService().inviteExists(this)) {
                src.sendMessage(Text.of(
                    TextColors.GREEN, "You have denied ",
                    island.getPrivilegeType(sender).format(sender.getName()),
                    TextColors.GREEN, "'s invite to ", island.getName(), TextColors.GREEN, "!"
                ));
                this.deny();
              }
            })),
        TextColors.WHITE, "]"
    )));
  }

  void accept() {
    Sponge.getCauseStackManager().pushCause(PLUGIN.getPluginContainer());
    island.addMember(receiver, privilegeType);
    Sponge.getCauseStackManager().popCause();
    Sponge.getScheduler().createTaskBuilder()
        .execute(IslandManager.processCommands(receiver.getName(), null))
        .submit(PLUGIN);
    PLUGIN.getInviteService().removeInvite(this);
  }

  void deny() {
    PLUGIN.getInviteService().removeInvite(this);
  }

  public static Invite.Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private Island island;
    private User sender;
    private User receiver;
    private PrivilegeType privilegeType;

    public Builder() {
      this.privilegeType = PrivilegeType.MEMBER;
    }

    public Builder island(@Nonnull Island island) {
      checkNotNull(island);
      this.island = island;
      return this;
    }

    public Builder sender(@Nonnull User sender) {
      checkNotNull(sender);
      this.sender = sender;
      return this;
    }

    public Builder receiver(@Nonnull User receiver) {
      checkNotNull(receiver);
      this.receiver = receiver;
      return this;
    }

    public Builder privilegeType(@Nonnull PrivilegeType privilegeType) {
      checkNotNull(privilegeType);
      this.privilegeType = privilegeType;
      return this;
    }

    public Invite build() {
      checkNotNull(island);
      checkNotNull(sender);
      checkNotNull(receiver);
      checkNotNull(privilegeType);
      checkArgument(!sender.equals(receiver));
      return new Invite(island, sender, receiver, privilegeType);
    }
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(island.getUniqueId())
        .append(sender.getUniqueId())
        .append(receiver.getUniqueId())
        .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof Invite)) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    Invite invite = (Invite) obj;
    return new EqualsBuilder()
        .append(island.getUniqueId(), invite.island.getUniqueId())
        .append(sender.getUniqueId(), invite.sender.getUniqueId())
        .append(receiver.getUniqueId(), invite.receiver.getUniqueId())
        .build();
  }
}
