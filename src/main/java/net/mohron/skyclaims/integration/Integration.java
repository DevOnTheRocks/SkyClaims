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

package net.mohron.skyclaims.integration;

import static net.mohron.skyclaims.PluginInfo.NUCLEUS_VERSION;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.type.IntegrationConfig;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginManager;

import java.util.Optional;

public class Integration {

    private final SkyClaims PLUGIN = SkyClaims.getInstance();
    private Nucleus nucleus = null;

    public Integration() {
        IntegrationConfig config = PLUGIN.getConfig().getIntegrationConfig();
        PluginManager pluginManager = Sponge.getPluginManager();
        if (config.getNucleus().isEnabled() && pluginManager.getPlugin("nucleus").isPresent()) {
            Version version = new Version(pluginManager.getPlugin("nucleus").get().getVersion().orElse("0.0.0"));
            PLUGIN.getLogger().info("Found Nucleus " + version);
            if (version.compareTo(new Version(NUCLEUS_VERSION)) >= 0) {
                nucleus = new Nucleus();
            }
        }
    }

    private static boolean isPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public Optional<Nucleus> getNucleus() {
        return Optional.ofNullable(nucleus);
    }
}
