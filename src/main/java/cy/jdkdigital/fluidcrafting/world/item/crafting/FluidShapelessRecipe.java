package cy.jdkdigital.fluidcrafting.world.item.crafting;

import com.google.gson.JsonObject;
import cy.jdkdigital.fluidcrafting.common.crafting.FluidContainerIngredient;
import cy.jdkdigital.fluidcrafting.init.ModRecipeTypes;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidShapelessRecipe extends ShapelessRecipe
{
    public FluidShapelessRecipe(ShapelessRecipe recipe) {
        super(recipe.getId(), recipe.getGroup(), recipe.getResultItem(), recipe.getIngredients());
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CRAFTING_SHAPELESS.get();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory container) {
        NonNullList<ItemStack> stacks = super.getRemainingItems(container);
        for (int i = 0; i < container.getWidth(); ++i) {
            for (int j = 0; j < container.getHeight() ; ++j) {
                int itemIndex = i + j * container.getWidth();
                ItemStack item = container.getItem(itemIndex);
                for (Ingredient ingredient: this.getIngredients()) {
                    if (ingredient instanceof FluidContainerIngredient && item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
                        // Check that the item has fluid and that it's the correct type and amount
                        item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler -> {
                            // Remove fluid used in recipe
                            handler.drain(((FluidContainerIngredient) ingredient).getAmount(), IFluidHandler.FluidAction.EXECUTE);
                        });
                        stacks.set(itemIndex, item.copy());
                    }
                }
            }
        }
        return stacks;
    }

    public static class Serializer extends ShapelessRecipe.Serializer
    {
        public FluidShapelessRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new FluidShapelessRecipe(super.fromJson(id, json));
        }

        public FluidShapelessRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer) {
            return new FluidShapelessRecipe(super.fromNetwork(id, buffer));
        }
    }
}
