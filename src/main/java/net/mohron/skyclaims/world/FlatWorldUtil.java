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

import java.util.Arrays;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.exception.SkyClaimsException;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.text.Text;

public final class FlatWorldUtil {

  private static final BlockState[] voidWorld = new BlockState[256];

  static {
    Arrays.fill(voidWorld, BlockTypes.AIR.getDefaultState());
  }

  private FlatWorldUtil() {
  }

  public static BlockState[] getBlocks(String code) throws SkyClaimsException {
    BlockState[] list = new BlockState[256];
    int l = 0;

    // Bank code is void
    if (StringUtils.isBlank(code)) {
      return voidWorld;
    }

    // Only the first part is used
    if (code.contains(";")) {
      code = code.split(";")[0];
    }

    String[] blockIds = code.split(",");

    for (String layer : blockIds) {
      int count = 1;
      String blockId;
      // If the block id has a quantity set, split the quantity and block id
      if (layer.contains("*")) {
        String[] s = layer.split("\\*");
        count = Integer.valueOf(s[0]);
        blockId = s[1];
      } else {
        blockId = layer;
      }

      BlockType type = Sponge.getRegistry().getType(BlockType.class, blockId)
          .orElseThrow(() -> new SkyClaimsException(Text.of("Unable to parse flat world preset code. Unknown Block ID: ", blockId)));
      for (int i = 0; i < count; i++) {
        list[l] = BlockState.builder().blockType(type).build();
        l++;
      }
    }

    // Fill the remaining layers with air
    for (; l < 255; l++) {
      list[l] = BlockTypes.AIR.getDefaultState();
    }

    return list;
  }

  public static BlockState[] getBlocksSafely(String code) {
    try {
      return getBlocks(code);
    } catch (SkyClaimsException e) {
      SkyClaims.getInstance().getLogger().error(e.getMessage(), e);
      return voidWorld;
    }
  }

  public static BlockState[] getVoidWorld() {
    return voidWorld;
  }
}
