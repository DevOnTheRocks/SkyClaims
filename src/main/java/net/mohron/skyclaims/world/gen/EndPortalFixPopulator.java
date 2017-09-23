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

import net.mohron.skyclaims.SkyClaims;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.api.world.gen.Populator;
import org.spongepowered.api.world.gen.PopulatorType;

import java.util.Locale;
import java.util.Random;

import javax.annotation.Nonnull;

public class EndPortalFixPopulator implements Populator {

    @Nonnull @Override public PopulatorType getType() {
        return new PopulatorType() {
            @Nonnull @Override public String getId() {
                return "endportalfix";
            }

            @Override public String getName() {
                return "End Portal Fix";
            }

            @Nonnull @Override public Translation getTranslation() {
                return new Translation() {
                    @Nonnull @Override public String getId() {
                        return getType().getId();
                    }

                    @Nonnull @Override public String get(@Nonnull Locale locale) {
                        return getName();
                    }

                    @Nonnull @Override public String get(@Nonnull Locale locale, @Nonnull Object... args) {
                        return getName();
                    }
                };
            }
        };
    }

    @Override public void populate(@Nonnull World world, @Nonnull Extent volume, @Nonnull Random random) {
        if (volume.containsBlock(0,64, 0) && volume.getBlockType(0, 64, 0).equals(BlockTypes.AIR)) {
            world.setBlock(0, 64, 0, BlockState.builder().blockType(BlockTypes.END_STONE).build(), SkyClaims.getInstance().getCause());
        }
    }
}
