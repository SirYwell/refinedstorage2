package com.refinedmods.refinedstorage.api.autocrafting;

import com.refinedmods.refinedstorage.api.core.CoreValidations;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apiguardian.api.API;

/**
 * Represents a pattern layout. Multiple {@link Pattern}s can share the same layout.
 *
 * @param ingredients the ingredients
 * @param outputs     the outputs
 * @param byproducts  the byproducts
 * @param type        the type
 */
@API(status = API.Status.STABLE, since = "2.0.0-milestone.4.12")
public record PatternLayout(List<Ingredient> ingredients,
                            List<ResourceAmount> outputs,
                            List<ResourceAmount> byproducts,
                            PatternType type) {
    public PatternLayout(final List<Ingredient> ingredients,
                         final List<ResourceAmount> outputs,
                         final List<ResourceAmount> byproducts,
                         final PatternType type) {
        CoreValidations.validateNotEmpty(ingredients, "Ingredients cannot be empty");
        CoreValidations.validateNotEmpty(outputs, "Outputs cannot be empty");
        CoreValidations.validateNotNull(byproducts, "Byproducts cannot be null");
        CoreValidations.validateNotNull(type, "Type cannot be null");
        if (type == PatternType.EXTERNAL && !byproducts.isEmpty()) {
            throw new IllegalArgumentException("External patterns cannot have byproducts");
        }
        this.ingredients = List.copyOf(mergeByInputs(ingredients));
        this.outputs = List.copyOf(outputs);
        this.byproducts = List.copyOf(byproducts);
        this.type = type;
    }

    public static PatternLayout external(final List<Ingredient> ingredients, final List<ResourceAmount> outputs) {
        return new PatternLayout(ingredients, outputs, List.of(), PatternType.EXTERNAL);
    }

    public static PatternLayout internal(final List<Ingredient> ingredients,
                                         final List<ResourceAmount> outputs,
                                         final List<ResourceAmount> byproducts) {
        return new PatternLayout(ingredients, outputs, byproducts, PatternType.INTERNAL);
    }

    private static List<Ingredient> mergeByInputs(final List<Ingredient> ingredients) {
        Map<List<ResourceKey>, Long> amounts = new LinkedHashMap<>();
        for (Ingredient in : ingredients) {
            amounts.compute(in.inputs(), (k, v) -> v == null ? in.amount() : v + in.amount());
        }
        return amounts.entrySet().stream()
            .map(entry -> new Ingredient(entry.getValue(), entry.getKey()))
            .toList();
    }
}
