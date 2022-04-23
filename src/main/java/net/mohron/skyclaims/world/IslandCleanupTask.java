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

package net.mohron.skyclaims.world;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.SkyClaimsTimings;
import net.mohron.skyclaims.permissions.Options;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.SpongeExecutorService;

public class IslandCleanupTask implements Runnable {

  private static final SkyClaims PLUGIN = SkyClaims.getInstance();
  private final ImmutableList<Island> islands;

  public IslandCleanupTask(Collection<Island> islands) {
    this.islands = ImmutableList.copyOf(islands);
  }

  @Override
  public void run() {
    SkyClaimsTimings.ISLAND_CLEANUP.startTimingIfSync();

    PLUGIN.getLogger().info("Starting island cleanup check.");
    Stopwatch sw = Stopwatch.createStarted();
    SpongeExecutorService asyncExecutor = Sponge.getScheduler().createAsyncExecutor(PLUGIN);
    SpongeExecutorService syncExecutor = Sponge.getScheduler().createSyncExecutor(PLUGIN);

    islands.forEach(i -> {
      int age = (int) Duration.between(i.getDateLastActive().toInstant(), Instant.now()).toDays();
      int threshold = Options.getExpiration(i.getOwnerUniqueId());
      if (threshold <= 0 || age < threshold) {
        return;
      }
      PLUGIN.getLogger().info("{} ({},{}) was inactive for {} days and is being removed.",
          i.getName().toPlain(), i.getRegion().getX(), i.getRegion().getZ(), age
      );
      CompletableFuture
          .runAsync(RegenerateRegionTask.clear(i.getRegion(), i.getWorld()), asyncExecutor)
          .thenRunAsync(i::delete, syncExecutor)
          .thenRun(() -> PLUGIN.getLogger().info("{} has been successfully removed.", i.getName().toPlain()));
    });

    sw.stop();
    PLUGIN.getLogger().info("Finished island cleanup check in {}ms.", sw.elapsed(TimeUnit.MILLISECONDS));

    SkyClaimsTimings.ISLAND_CLEANUP.stopTimingIfSync();
  }
}
