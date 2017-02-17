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

import net.mohron.skyclaims.SkyClaims;
import org.spongepowered.api.Sponge;

import java.util.Optional;

public class Integration {
	private Nucleus nucleus = null;

	public Integration() {
		if (isPresent("io.github.nucleuspowered.nucleus.api.service.NucleusHomeService")) {
			String version = Sponge.getPluginManager().getPlugin("nucleus").get().getVersion().orElse("0");
			SkyClaims.getInstance().getLogger().info("Found Nucleus " + version.substring(0, version.indexOf("-")));
			nucleus = new Nucleus();
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
