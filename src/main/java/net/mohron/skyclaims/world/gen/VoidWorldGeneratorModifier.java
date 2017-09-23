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

package net.mohron.skyclaims.world.gen;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeGenerationSettings;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.gen.PopulatorTypes;
import org.spongepowered.api.world.gen.WorldGenerator;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.common.world.gen.InternalPopulatorTypes;

/**
 * A modifier that causes a {@link World} to generate with empty chunks.
 */
public class VoidWorldGeneratorModifier implements WorldGeneratorModifier {

    @Override
    public void modifyWorldGenerator(WorldProperties world, DataContainer settings, WorldGenerator worldGenerator) {
        if (world.getDimensionType().equals(DimensionTypes.NETHER)) {
            modifyNether(worldGenerator);
        } else if (world.getDimensionType().equals(DimensionTypes.THE_END)) {
            modifyEnd(worldGenerator);
        } else {
            modifySurface(worldGenerator);
        }
        worldGenerator.setBaseGenerationPopulator((world1, buffer, biomes) -> { });
    }

    private void modifySurface(WorldGenerator worldGenerator) {
        worldGenerator.getPopulators().clear();
        worldGenerator.getGenerationPopulators().clear();
        for (BiomeType biome : Sponge.getRegistry().getAllOf(BiomeType.class)) {
            BiomeGenerationSettings biomeSettings = worldGenerator.getBiomeSettings(biome);
            biomeSettings.getPopulators().clear();
            biomeSettings.getGenerationPopulators().clear();
            biomeSettings.getGroundCoverLayers().clear();
        }
    }

    private void modifyNether(WorldGenerator worldGenerator) {
        BiomeGenerationSettings biomeSettings = worldGenerator.getBiomeSettings(BiomeTypes.HELL);
        try {
            worldGenerator.getGenerationPopulators().remove(Class.forName("net.minecraft.world.gen.MapGenCavesHell"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        biomeSettings.getPopulators().remove(PopulatorTypes.NETHER_FIRE);
        biomeSettings.getPopulators().remove(PopulatorTypes.GLOWSTONE);
        biomeSettings.getPopulators().remove(PopulatorTypes.ORE);
        biomeSettings.getPopulators().remove(PopulatorTypes.MUSHROOM);
    }

    private void modifyEnd(WorldGenerator worldGenerator) {
        worldGenerator.getPopulators().add(new EndPortalFixPopulator());
        BiomeGenerationSettings biomeSettings = worldGenerator.getBiomeSettings(BiomeTypes.SKY);
        biomeSettings.getPopulators().remove(InternalPopulatorTypes.END_SPIKE);
        biomeSettings.getPopulators().remove(PopulatorTypes.END_ISLAND);
        biomeSettings.getPopulators().remove(PopulatorTypes.CHORUS_FLOWER);
        biomeSettings.getGenerationPopulators().clear();
    }

    @Override
    public String getId() {
        return "skyclaims:void";
    }

    @Override
    public String getName() {
        return "Enhanced Void Modifier";
    }
}