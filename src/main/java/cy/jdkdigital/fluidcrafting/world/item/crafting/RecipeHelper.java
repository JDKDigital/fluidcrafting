package cy.jdkdigital.fluidcrafting.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;

public class RecipeHelper
{
    public static NonNullList<Ingredient> applyFluid(NonNullList<Ingredient> recipeItems) {

//        for (Ingredient ingredient: recipeItems) {
//            if (ingredient instanceof FluidContainerIngredient fluidContainerIngredient) {
//                for (ItemStack item: ingredient.getItems()) {
//                    if (item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
//                        item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler -> {
//                            handler.drain(handler.getTankCapacity(0), IFluidHandler.FluidAction.EXECUTE);
//                            handler.fill(fluidContainerIngredient.getFluidStack(), IFluidHandler.FluidAction.EXECUTE);
//                        });
//                    }
//                }
//            }
//        }

        return recipeItems;
    }
}
