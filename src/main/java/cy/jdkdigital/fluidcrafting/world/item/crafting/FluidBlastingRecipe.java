package cy.jdkdigital.fluidcrafting.world.item.crafting;

import cy.jdkdigital.fluidcrafting.init.ModRecipeTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.BlastingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class FluidBlastingRecipe extends BlastingRecipe
{
    public FluidBlastingRecipe(ResourceLocation id, String group, Ingredient ingredient, ItemStack resultItem, float experience, int cookingTime) {
        super(id, group, ingredient, resultItem, experience, cookingTime);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.BLASTING.get();
    }
}
