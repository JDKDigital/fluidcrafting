package cy.jdkdigital.fluidcrafting.world.item.crafting;

import com.google.gson.JsonObject;
import cy.jdkdigital.fluidcrafting.common.crafting.FluidContainerIngredient;
import cy.jdkdigital.fluidcrafting.init.ModRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class FluidShapedRecipe extends ShapedRecipe
{
    public FluidShapedRecipe(ShapedRecipe recipe) {
        super(recipe.getId(), recipe.getGroup(), recipe.getWidth(), recipe.getHeight(), recipe.getIngredients(), recipe.getResultItem());
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return RecipeHelper.applyFluid(super.getIngredients());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CRAFTING_SHAPED.get();
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> stacks = super.getRemainingItems(container);
        for (int i = 0; i < container.getWidth(); ++i) {
            for (int j = 0; j < container.getHeight() ; ++j) {
                int itemIndex = i + j * container.getWidth();
                ItemStack item = container.getItem(itemIndex);
                Ingredient ingredient = this.getIngredients().get(itemIndex);
                if (ingredient instanceof FluidContainerIngredient fluidContainerIngredient && item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
                    // Remove fluid used in recipe
                    item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler -> {
                        handler.drain(fluidContainerIngredient.getAmount(), IFluidHandler.FluidAction.EXECUTE);
                    });
                    stacks.set(itemIndex, item.copy());
                }
            }
        }
        return stacks;
    }

    public static class Serializer extends ShapedRecipe.Serializer
    {
        public FluidShapedRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new FluidShapedRecipe(super.fromJson(id, json));
        }

        public FluidShapedRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new FluidShapedRecipe(super.fromNetwork(id, buffer));
        }
    }
}
