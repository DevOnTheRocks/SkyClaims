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

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.SkyClaimsTimings;
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.util.CommandUtil;
import net.mohron.skyclaims.util.WorldUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.ArchetypeVolume;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

public class GenerateIslandTask implements Runnable {

    private static final SkyClaims PLUGIN = SkyClaims.getInstance();

    private UUID owner;
    private Island island;
    private String schematic;

    public GenerateIslandTask(UUID owner, Island island, String schematic) {
        this.owner = owner;
        this.island = island;
        this.schematic = schematic;
    }

    @Override
    public void run() {
        SkyClaimsTimings.GENERATE_ISLAND.startTimingIfSync();
        World world = PLUGIN.getConfig().getWorldConfig().getWorld();
        File inputFile = new File(PLUGIN.getConfigDir().toString(), String.format("schematics%s%s.schematic", File.separator, schematic));

        DataContainer schematicData;
        try {
            schematicData = DataFormats.NBT.readFrom(new GZIPInputStream(new FileInputStream(inputFile)));
        } catch (Exception e) {
            e.printStackTrace();
            PLUGIN.getLogger().error("Error loading schematic: " + e.getMessage());
            SkyClaimsTimings.GENERATE_ISLAND.abort();
            return;
        }

        ArchetypeVolume volume;
        try {
            volume = DataTranslators.SCHEMATIC.translate(schematicData);
        } catch (Exception e) {
            try {
                volume = DataTranslators.LEGACY_SCHEMATIC.translate(schematicData);
                PLUGIN.getLogger().warn("Loaded legacy schematic: {}", e.getMessage());
            } catch (Exception e2) {
                PLUGIN.getLogger().error("Invalid schematic file ({})!\n{}", schematic, e2);
                SkyClaimsTimings.GENERATE_ISLAND.abort();
                return;
            }
        }

        Location<World> centerBlock = island.getRegion().getCenter();
        // Loads center chunks
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                world.loadChunk(
                    centerBlock.getChunkPosition().getX() + x,
                    centerBlock.getChunkPosition().getY(),
                    centerBlock.getChunkPosition().getZ() + z,
                    true
                );
            }
        }

        Location<World> spawn = new Location<>(
            island.getWorld(),
            centerBlock.getX(),
            centerBlock.getY() + volume.getBlockSize().getY() - 1,
            centerBlock.getZ()
        );
        island.setSpawn(new Transform<>(spawn.getExtent(), spawn.getPosition()));
        volume.apply(spawn, BlockChangeFlag.NONE);

        // Set the region's BiomeType using the default biome option if set
        Options.getDefaultBiome(owner).ifPresent(biomeType -> {
            WorldUtil.setRegionBiome(island, biomeType);
        });

        Sponge.getServer().getPlayer(owner).ifPresent(p1 -> {
            PLUGIN.getGame().getScheduler().createTaskBuilder()
                .delayTicks(20)
                .execute(CommandUtil.createTeleportConsumer(p1, spawn))
                .submit(PLUGIN);
        });
        SkyClaimsTimings.GENERATE_ISLAND.stopTimingIfSync();
    }
}