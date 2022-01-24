package cy.jdkdigital.fluidcrafting.world.item.crafting;

import cy.jdkdigital.fluidcrafting.init.ModRecipeTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class FluidSmeltingRecipe extends FurnaceRecipe
{
    public FluidSmeltingRecipe(ResourceLocation id, String group, Ingredient ingredient, ItemStack resultItem, float experience, int cookingTime) {
        super(id, group, ingredient, resultItem, experience, cookingTime);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.SMELTING.get();
    }
}
