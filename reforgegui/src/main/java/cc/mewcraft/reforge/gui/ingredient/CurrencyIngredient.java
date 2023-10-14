package cc.mewcraft.reforge.gui.ingredient;

import cc.mewcraft.spatula.message.Translations;
import me.xanium.gemseconomy.api.Currency;
import me.xanium.gemseconomy.api.GemsEconomy;
import me.xanium.gemseconomy.api.GemsEconomyProvider;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

public class CurrencyIngredient extends Ingredient {
    final String identifier; // Identifier of currency
    final double amount; // Required amount

    public CurrencyIngredient(
            final Translations translations,
            final String identifier,
            final double amount
    ) {
        super(translations);
        this.identifier = identifier;
        this.amount = amount;
    }

    @Override public @NotNull Result check(@NotNull final IngredientParams params) {
        GemsEconomy econ = GemsEconomyProvider.get();
        Currency currency = Objects.requireNonNull(econ.getCurrency(identifier));
        if (econ.getBalance(params.player().getUniqueId(), currency) >= amount) {
            return checkNext(params); // OK, pass to next
        } else {
            return new ReforgeResult(
                    Result.State.FAILURE,
                    translations.of("msg_insufficient_currency_ingredient")
                            .replace("currency", fancyFormat())
                            .component()
            );
        }
    }

    @Override public @NotNull Result consume(@NotNull final IngredientParams params) {
        GemsEconomy econ = GemsEconomyProvider.get();
        Currency currency = Objects.requireNonNull(econ.getCurrency(identifier));
        econ.withdraw(params.player().getUniqueId(), amount, currency);
        return consumeNext(params); // OK, pass to next
    }

    public String simpleFormat() {
        GemsEconomy econ = GemsEconomyProvider.get();
        Currency currency = Objects.requireNonNull(econ.getCurrency(identifier));
        return currency.simpleFormat(amount);
    }

    public String fancyFormat() {
        GemsEconomy econ = GemsEconomyProvider.get();
        Currency currency = Objects.requireNonNull(econ.getCurrency(identifier));
        return currency.fancyFormat(amount);
    }
}
