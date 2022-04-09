package cy.jdkdigital.fluidcrafting.world.item.crafting;

import com.google.gson.JsonObject;
import cy.jdkdigital.fluidcrafting.common.crafting.FluidContainerIngredient;
import cy.jdkdigital.fluidcrafting.init.ModRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class FluidShapelessRecipe extends ShapelessRecipe
{
    ItemStack result;

    public FluidShapelessRecipe(ShapelessRecipe recipe) {
        super(recipe.getId(), recipe.getGroup(), recipe.getResultItem(), recipe.getIngredients());

        result = recipe.getResultItem();
        LazyOptional<IFluidHandlerItem> resultCap = FluidUtil.getFluidHandler(result);
        if (resultCap.isPresent()) {
            recipe.getIngredients().forEach(ingredient -> {
                if (ingredient.test(recipe.getResultItem())) {
                    Arrays.stream(ingredient.getItems()).forEach(itemStack -> {
                        LazyOptional<IFluidHandlerItem> cap = FluidUtil.getFluidHandler(itemStack);
                        if (cap.isPresent()) {
                            cap.ifPresent(h -> {
                                resultCap.ifPresent(resultHandler -> {
                                    resultHandler.fill(h.getFluidInTank(0), IFluidHandler.FluidAction.EXECUTE);
                                });
                            });
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        boolean matches = super.matches(container, level);
        LazyOptional<IFluidHandlerItem> resultCap = FluidUtil.getFluidHandler(result);
        if (matches && resultCap.isPresent()) {
            matches = resultCap.map(h -> h.getFluidInTank(0).getAmount() < h.getTankCapacity(0)).orElse(true);
        }

        return matches;
    }

    @Override
    public ItemStack getResultItem() {
        return result;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CRAFTING_SHAPELESS.get();
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        return RecipeHelper.applyFluidToResult(container, this.result);
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        return RecipeHelper.getRemainingItems(container, super.getRemainingItems(container), getIngredients(), result);
    }

    public static class Serializer extends ShapelessRecipe.Serializer
    {
        public FluidShapelessRecipe fromJson(ResourceLocation id, JsonObject json) {
            ShapelessRecipe recipe = super.fromJson(id, json);

            // Special handling of fluid container outputs
            JsonObject obj = GsonHelper.getAsJsonObject(json, "result");
            if (obj.has("type") && obj.get("type").getAsString().equals(FluidContainerIngredient.TYPE.toString())) {
                FluidContainerIngredient result = FluidContainerIngredient.Serializer.INSTANCE.parse(obj);
                LazyOptional<IFluidHandlerItem> ingredientCap = FluidUtil.getFluidHandler(result.getItems()[0]);
                if (ingredientCap.isPresent()) {
                    ingredientCap.ifPresent(ingredientHandler -> {
                        LazyOptional<IFluidHandlerItem> outputCap = FluidUtil.getFluidHandler(recipe.getResultItem());
                        if (outputCap.isPresent()) {
                            outputCap.ifPresent(outputHandler -> {
                                outputHandler.fill(ingredientHandler.getFluidInTank(0), IFluidHandler.FluidAction.EXECUTE);
                            });
                        }
                    });
                }
            }

            return new FluidShapelessRecipe(recipe);
        }

        public FluidShapelessRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new FluidShapelessRecipe(super.fromNetwork(id, buffer));
        }
    }
}
