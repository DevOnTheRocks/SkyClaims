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
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import net.mohron.skyclaims.SkyClaims;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.schematic.Schematic;

public class IslandSchematic {

  private static final DataQuery BIOME_TYPE = DataQuery.of("SkyClaims", "BiomeType");
  private static final DataQuery COMMANDS = DataQuery.of("SkyClaims", "Command");
  private static final DataQuery HEIGHT = DataQuery.of("SkyClaims", "Height");
  private static final DataQuery ICON = DataQuery.of("SkyClaims", "Icon");
  private static final DataQuery TEXT = DataQuery.of("SkyClaims", "Text");
  private static final DataQuery PRESET = DataQuery.of("SkyClaims", "Preset");

  private final Schematic schematic;
  private final String name;

  public IslandSchematic(Schematic schematic, String name) {
    this.schematic = schematic;
    this.name = name;
  }

  public Schematic getSchematic() {
    return schematic;
  }

  public String getName() {
    return name;
  }

  public String getFileName() {
    return String.format("%s.schematic", name);
  }

  public String getAuthor() {
    return schematic.getMetadata().getString(DataQuery.of("Author")).orElse("Unknown");
  }

  public String getDate() {
    String date = "Unknown";
    try {
      Instant instant = Instant.parse(schematic.getMetadata().getString(DataQuery.of("Date")).orElse("Unknown"));
      date = SkyClaims.getInstance().getConfig().getMiscConfig().getDateFormat().format(Date.from(instant));
    } catch (Exception ignored) {
    }
    return date;
  }

  public Optional<BiomeType> getBiomeType() {
    String biomeId = schematic.getMetadata().getString(BIOME_TYPE).orElse(null);
    return biomeId != null ?
        Sponge.getRegistry().getAllOf(BiomeType.class).stream().filter(b -> b.getId().equalsIgnoreCase(biomeId)).findAny() :
        Optional.empty();
  }

  public void setBiomeType(BiomeType type) {
    setMetadata(BIOME_TYPE, type.getId());
  }

  public List<String> getCommands() {
    return schematic.getMetadata().getStringList(COMMANDS).orElse(Lists.newArrayList());
  }

  public void setCommands(List<String> commands) {
    setMetadata(COMMANDS, commands);
  }

  public Optional<Integer> getHeight(){
    Integer height;
    if (schematic.getMetadata().getInt(HEIGHT).isPresent()) {
      height = Math.max(0, Math.min(255, schematic.getMetadata().getInt(HEIGHT).get()));
    } else {
      height = null;
    }
    return Optional.ofNullable(height);
  }

  public void setHeight(Integer height) {
    setMetadata(HEIGHT, height);
  }

  public Text getText() {
    String rawText = schematic.getMetadata().getString(TEXT).orElse(getName());
    return TextSerializers.FORMATTING_CODE.deserialize(rawText);
  }

  public void setText(Text text) {
    setMetadata(TEXT, TextSerializers.FORMATTING_CODE.serialize(text));
  }

  public Optional<ItemType> getIcon() {
    String type = schematic.getMetadata().getString(ICON).orElse("");
    return Sponge.getRegistry().getType(ItemType.class, type);
  }

  public void setIcon(ItemType icon) {
    setMetadata(ICON, icon);
  }

  public void setPreset(String preset) {
    setMetadata(PRESET, preset);
  }

  public Optional<String> getPreset() {
    return schematic.getMetadata().getString(PRESET);
  }

  private void setMetadata(DataQuery path, Object value) {
    if (value != null) {
      schematic.getMetadata().set(path, value);
    } else {
      schematic.getMetadata().remove(path);
    }
  }
}
