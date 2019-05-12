package net.mohron.skyclaims.schematic;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import net.mohron.skyclaims.SkyClaims;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public final class SchematicUI {

  private static final SkyClaims PLUGIN = SkyClaims.getInstance();

  private SchematicUI() {
  }

  public static Inventory of(List<IslandSchematic> schematics, Function<IslandSchematic, Consumer<CommandSource>> mapper) {
    Inventory inventory = Inventory.builder()
        .of(InventoryArchetypes.CHEST)
        .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(TextColors.AQUA, "Schematics")))
        .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, schematics.size() / 9 + 1))
        .listener(ClickInventoryEvent.class, handleClick(mapper))
        .build(PLUGIN);

    for (int i = 0; i < schematics.size(); i++) {
      IslandSchematic schematic = schematics.get(i);
      ItemStack itemStack = ItemStack.builder()
          .itemType(schematic.getIcon())
          .add(Keys.DISPLAY_NAME, schematic.getText())
          .quantity(1)
          .build();
      inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(i % 9, i / 9))).first().set(itemStack);
    }

    return inventory;
  }

  private static Consumer<ClickInventoryEvent> handleClick(Function<IslandSchematic, Consumer<CommandSource>> mapper) {
    return event -> {
      if (event.getCause().first(Player.class).isPresent()) {
        Player player = event.getCause().first(Player.class).get();
        getSchematic(event).ifPresent(s -> {
          mapper.apply(s).accept(player);
          player.closeInventory();
        });
        event.setCancelled(true);
      }
    };
  }

  private static Optional<IslandSchematic> getSchematic(ClickInventoryEvent event) {
    if (event.getCursorTransaction().isValid()) {
      ItemStackSnapshot itemStack = event.getCursorTransaction().getFinal();
      if (itemStack.get(Keys.DISPLAY_NAME).isPresent()) {
        Text name = itemStack.get(Keys.DISPLAY_NAME).get();
        return PLUGIN.getSchematicManager().getSchematics().stream()
            .filter(s -> s.getText().equals(name))
            .findAny();
      }
    }
    return Optional.empty();
  }
}
