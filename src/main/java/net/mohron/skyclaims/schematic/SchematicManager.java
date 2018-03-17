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

package net.mohron.skyclaims.schematic;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.mohron.skyclaims.SkyClaims;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.world.schematic.Schematic;

public class SchematicManager {

  private final SkyClaims plugin;
  private final List<IslandSchematic> schematics;
  private final File directory;

  public SchematicManager(SkyClaims plugin) {
    this.plugin = plugin;
    this.schematics = Lists.newArrayList();
    this.directory = new File(plugin.getConfigDir() + File.separator + "schematics");
  }

  public List<IslandSchematic> getSchematics() {
    return schematics;
  }

  public IslandSchematic getRandomSchematic() {
    Random random = new Random();
    int r = random.nextInt(schematics.size());
    return schematics.get(r);
  }

  public boolean create(Schematic schematic) {
    final String name = schematic.getMetadata().getName().toLowerCase();

    try {
      DataContainer schematicData = DataTranslators.SCHEMATIC.translate(schematic);
      DataFormats.NBT.writeTo(new GZIPOutputStream(new FileOutputStream(new File(directory, String.format("%s.schematic", name)))), schematicData);
      plugin.getSchematicManager().getSchematics().add(new IslandSchematic(schematic, name));
      return true;
    } catch (Exception e) {
      plugin.getLogger().error("Error saving schematic: {}\n{}", name, e.getStackTrace());
      return false;
    }
  }

  public boolean delete(IslandSchematic schematic) {
    try {
      Files.delete(Paths.get(directory.getPath(), schematic.getFileName()));
      schematics.remove(schematic);
      return true;
    } catch (Exception e) {
      plugin.getLogger().error("Error deleting schematic: {}\n{}", schematic.getName(), e.getStackTrace());
      return false;
    }
  }

  public void load() {
    plugin.getLogger().debug("Started loading schematics.");
    schematics.clear();
    unpackDefaultSchematics();
    try {
      //noinspection ConstantConditions - exception will be caught
      for (File file : directory.listFiles()) {
        final String fileName = file.getName();
        if (fileName.endsWith(".schematic")) {
          try {
            DataContainer schematicData = DataFormats.NBT.readFrom(new GZIPInputStream(new FileInputStream(file)));
            Schematic schematic = DataTranslators.SCHEMATIC.translate(schematicData);
            schematics.add(new IslandSchematic(schematic, fileName.replace(".schematic", "").toLowerCase()));
          } catch (Exception e) {
            plugin.getLogger().error("Error loading schematic: {}", fileName);
            e.printStackTrace();
            continue;
          }
          plugin.getLogger().debug("Successfully loaded schematic: {}.", fileName);
        } else {
          plugin.getLogger().debug("Found non-schematic file {}. Ignoring.", fileName);
        }
      }
    } catch (Exception e) {
      plugin.getLogger().error("Failed to read schematics directory!", e);
    }
    plugin.getLogger().debug("Finished loading {} schematics.", schematics.size());
  }

  public boolean save(IslandSchematic schematic) {
    try {
      DataContainer schematicData = DataTranslators.SCHEMATIC.translate(schematic.getSchematic());
      DataFormats.NBT.writeTo(new GZIPOutputStream(new FileOutputStream(new File(directory, schematic.getName()))), schematicData);
      // schematics.add(schematic); TODO: is this even needed now?
    } catch (Exception e) {
      plugin.getLogger().error("Error saving schematic: ", schematic.getName());
      return false;
    }
    return true;
  }

  private void unpackDefaultSchematics() {
    String[] schematics = {"gardenofglass", "grass", "sand", "skyexchange", "skyfactory", "snow", "wood"};
    if (!directory.exists() || !directory.isDirectory()) {
      try {
        boolean success = directory.mkdir();
        if (!success) {
          throw new IOException();
        }
      } catch (SecurityException | IOException e) {
        plugin.getLogger().error(String.format("Failed to create schematics directory.\r\n %s", e.getMessage()));
      }
    }
    try {
      //noinspection ConstantConditions - schemDir.list() is being checked for null
      if (directory.list() == null || directory.list().length < 1) {
        for (String name : schematics) {
          File schematic = Paths.get(String.format("%s%s%s.schematic", directory.toPath(), File.separator, name)).toFile();
          //noinspection ConstantConditions - Resource will always be included
          Resources.asByteSource(this.getClass().getClassLoader()
              .getResource(name + ".schematic"))
              .copyTo(com.google.common.io.Files.asByteSink(schematic));
        }
      }
    } catch (SecurityException | IOException e) {
      plugin.getLogger().error(String.format("Failed to create default schematic.\r\n %s", e.getMessage()));
    }
  }
}
