package net.mohron.skyclaims.config.type;

import java.util.Optional;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;

@ConfigSerializable
public class EconomyConfig {

  @Setting(value = "use-claim-blocks", comment = "If set to true, claim blocks will be used as a currency.")
  private boolean useClaimBlocks = false;

  @Setting(value = "currency", comment = "The name of the currency to use when a Economy plugin is available.\n"
      + "The default currency will be used if not configured or an invalid currency is configured.")
  private String currency = "";

  @Setting(value = "cost-modifier", comment = "The cost of expanding is based on the number of blocks the expansion adds.\n"
      + "This will be multiplied by the number of blocks to calculate the final cost.")
  private double costModifier = 1.0;

  public boolean isUseClaimBlocks() {
    return useClaimBlocks;
  }

  public Optional<Currency> getCurrency() {
    return Sponge.getServiceManager().provide(EconomyService.class)
        .flatMap(economyService -> economyService.getCurrencies().stream()
            .filter(c -> c.getName().equalsIgnoreCase(currency))
            .findAny());
  }

  public double getCostModifier() {
    return Math.max(0, costModifier);
  }
}
