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

package net.mohron.skyclaims.schematic;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.mohron.skyclaims.SkyClaims;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.world.schematic.Schematic;

public class SchematicManager {

  private static final String SCHEMATIC_FILE_EXT = ".schematic";

  private final SkyClaims plugin;
  private final Random random;
  private final List<IslandSchematic> schematics;
  private final File directory;

  public SchematicManager(SkyClaims plugin) {
    this.plugin = plugin;
    this.random = new Random();
    this.schematics = Lists.newArrayList();
    this.directory = new File(plugin.getConfigDir() + File.separator + "schematics");
  }

  public List<IslandSchematic> getSchematics() {
    return schematics;
  }

  public IslandSchematic getRandomSchematic() {
    int r = random.nextInt(schematics.size());
    return schematics.get(r);
  }

  public boolean create(Schematic schematic, String name) {
    try {
      DataContainer schematicData = DataTranslators.SCHEMATIC.translate(schematic);
      DataFormats.NBT.writeTo(new GZIPOutputStream(new FileOutputStream(
          new File(directory, String.format("%s.schematic", name))
      )), schematicData);
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
        if (fileName.endsWith(SCHEMATIC_FILE_EXT)) {
          try {
            DataContainer schematicData = DataFormats.NBT.readFrom(new GZIPInputStream(new FileInputStream(file)));
            List<String> missingMods = getMissingMods(schematicData);
            if (missingMods.isEmpty()) {
              Schematic schematic = DataTranslators.SCHEMATIC.translate(schematicData);
              schematics.add(new IslandSchematic(schematic, fileName.replace(SCHEMATIC_FILE_EXT, "").toLowerCase()));
            } else {
              plugin.getLogger().warn("Schematic \"{}\" is missing required mods: {}", fileName, missingMods);
            }
          } catch (Exception e) {
            plugin.getLogger().error("Error loading schematic: " + fileName, e);
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
      DataFormats.NBT.writeTo(new GZIPOutputStream(new FileOutputStream(
          new File(directory, schematic.getFileName())
      )), schematicData);
    } catch (Exception e) {
      plugin.getLogger().error("Error saving schematic: " + schematic.getName(), e);
      return false;
    }
    return true;
  }

  private void unpackDefaultSchematics() {
    String[] defaultSchematics = {"grass", "sand", "skyfactory", "skyfactory4", "snow", "stoneblock2", "wood"};
    if (!directory.exists() || !directory.isDirectory()) {
      try {
        boolean success = directory.mkdir();
        if (!success) {
          throw new IOException();
        }
      } catch (SecurityException | IOException e) {
        plugin.getLogger().error("Failed to create schematics directory.", e);
      }
    }
    try {
      String[] list = directory.list();
      ClassLoader classLoader = getClass().getClassLoader();
      if (classLoader != null && (list == null || list.length == 0)) {
        for (String name : defaultSchematics) {
          InputStream schematic = classLoader.getResourceAsStream(name + SCHEMATIC_FILE_EXT);
          Path target = Paths.get(String.format("%s%s%s.schematic", directory.toPath(), File.separator, name));
          if (schematic != null) {
            Files.copy(schematic, target);
          }
        }
      }
    } catch (SecurityException | IOException e) {
      plugin.getLogger().error("Failed to create default schematic.", e);
    }
  }

  private List<String> getMissingMods(DataContainer schematic) {
    List<String> requiredMods = schematic
        .getStringList(DataQuery.of("Metadata", Schematic.METADATA_REQUIRED_MODS))
        .orElse(new ArrayList<>());

    return requiredMods.stream()
        .filter(mod -> !Sponge.getPluginManager().isLoaded(mod))
        .collect(Collectors.toList());
  }
}
