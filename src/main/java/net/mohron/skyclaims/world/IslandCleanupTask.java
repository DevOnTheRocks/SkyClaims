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
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.SkyClaimsTimings;
import net.mohron.skyclaims.permissions.Options;
import org.spongepowered.api.Sponge;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class IslandCleanupTask implements Runnable {

    private static final SkyClaims PLUGIN = SkyClaims.getInstance();

    @Override
    public void run() {
        SkyClaimsTimings.ISLAND_CLEANUP.startTimingIfSync();
        PLUGIN.getLogger().info("Starting Island Cleanup.");
        Stopwatch sw = Stopwatch.createStarted();
        SkyClaims.islands.values()
            .forEach(i -> {
                if (i.getDateLastActive() == null) {
                    return; // TODO: Remove when replaced with SkyClaims data.
                }
                int age = (int) Duration.between(i.getDateLastActive().toInstant(), Instant.now()).toDays();
                int threshold = Options.getExpiration(i.getOwnerUniqueId());
                if (threshold <= 0 || age < threshold) {
                    return;
                }
                Sponge.getScheduler().createTaskBuilder().execute(i::clear).submit(PLUGIN);
                Sponge.getScheduler().createTaskBuilder().execute(i::delete).submit(PLUGIN);
                PLUGIN.getLogger().info(String.format("%s (%d,%d) was inactive for %d days and has been removed.",
                    i.getName().toPlain(), i.getRegion().getX(), i.getRegion().getZ(), age)
                );
            });
        sw.stop();
        PLUGIN.getLogger().info(String.format("Finished Island Cleanup in %dms.", sw.elapsed(TimeUnit.MILLISECONDS)));
        SkyClaimsTimings.ISLAND_CLEANUP.stopTimingIfSync();
    }
}
