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

package net.mohron.skyclaims.world;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.type.WorldConfig;
import net.mohron.skyclaims.schematic.IslandSchematic;
import net.mohron.skyclaims.world.region.Region;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class RegenerateRegionTask implements Runnable {

  private static final SkyClaims PLUGIN = SkyClaims.getInstance();

  private Region region;
  private Island island;
  private IslandSchematic schematic;
  private boolean commands;

  public RegenerateRegionTask(Region region) {
    this.region = region;
    this.island = null;
    this.commands = false;
  }

  public RegenerateRegionTask(Island island, IslandSchematic schematic, boolean commands) {
    this.region = island.getRegion();
    this.island = island;
    this.schematic = schematic;
    this.commands = commands;
  }

  @Override
  public void run() {
    WorldConfig config = PLUGIN.getConfig().getWorldConfig();

    PLUGIN.getLogger().info("Begin regenerating region ({}, {})", region.getX(), region.getZ());

    Stopwatch sw = Stopwatch.createStarted();

    PLUGIN.getLogger().info("Using preset code '{}' to regenerate region.", config.getPresetCode());
    final BlockState[] blocks = FlatWorldUtil.getBlocksSafely(config.getPresetCode());

    regenerateChunks(blocks, config.getSpawn());

    sw.stop();

    PLUGIN.getLogger().info("Finished regenerating region ({}, {}) in {}s.", region.getX(), region.getZ(),
        sw.elapsed(TimeUnit.SECONDS));

    if (island != null) {
      if (commands) {
        Sponge.getScheduler().createTaskBuilder()
            .execute(processCommands())
            .submit(PLUGIN);
      }

      Sponge.getScheduler().createTaskBuilder()
          .delay(1, TimeUnit.SECONDS)
          .execute(new GenerateIslandTask(island.getOwnerUniqueId(), island, schematic))
          .submit(PLUGIN);
    }
  }

  private void regenerateChunks(BlockState[] blocks, Location<World> spawn) {
    SpongeExecutorService executor = Sponge.getScheduler().createSyncExecutor(PLUGIN);
    int progress = 0;
    for (int x = region.getLesserBoundary().getX(); x < region.getGreaterBoundary().getX(); x += 16) {
      List<CompletableFuture<Void>> tasks = Lists.newArrayList();
      for (int z = region.getLesserBoundary().getZ(); z < region.getGreaterBoundary().getZ(); z += 16) {
        tasks
            .add(CompletableFuture.runAsync(new RegenerateChunkTask(island.getWorld(), x, z, blocks, spawn), executor));
      }
      try {
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[32])).join();
        PLUGIN.getLogger().info("Regenerating region {}, {} {}% complete", region.getX(), region.getZ(),
            Math.round(++progress / 32f * 100));
      } catch (RuntimeException e) {
        PLUGIN.getLogger().error("Could not regenerate chunk.", e);
      }
    }
  }

  private Consumer<Task> processCommands() {
    return task -> {
      final CommandSource console = Sponge.getServer().getConsole();
      // Run reset commands
      for (String command : PLUGIN.getConfig().getMiscConfig().getResetCommands()) {
        Sponge.getCommandManager().process(console, command.replace("@p", island.getOwnerName()));
      }
      for (String command : schematic.getCommands()) {
        Sponge.getCommandManager().process(console, command.replace("@p", island.getOwnerName()));
      }
    };
  }
}
