package cy.jdkdigital.fluidcrafting.world.item.crafting;

import cy.jdkdigital.fluidcrafting.init.ModRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class FluidBlastingRecipe extends BlastingRecipe
{
    public FluidBlastingRecipe(ResourceLocation id, String group, Ingredient ingredient, ItemStack resultItem, float experience, int cookingTime) {
        super(id, group, ingredient, resultItem, experience, cookingTime);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return RecipeHelper.applyFluid(super.getIngredients());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.BLASTING.get();
    }
}
