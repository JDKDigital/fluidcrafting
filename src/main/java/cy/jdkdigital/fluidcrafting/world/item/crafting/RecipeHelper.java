package cy.jdkdigital.fluidcrafting.world.item.crafting;

import cy.jdkdigital.fluidcrafting.common.crafting.FluidContainerIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import java.util.HashMap;
import java.util.Map;

public class RecipeHelper
{
    public static ItemStack applyFluidToResult(CraftingContainer container, ItemStack result) {
        LazyOptional<IFluidHandlerItem> resultCap = FluidUtil.getFluidHandler(result);
        if (resultCap.isPresent()) {
            boolean hasConsumedOutputItem = false;
            Map<Integer, ItemStack> stacks = getInputs(container);

            for (Map.Entry<Integer, ItemStack> entry : stacks.entrySet()) {
                LazyOptional<IFluidHandlerItem> cap = FluidUtil.getFluidHandler(entry.getValue());
                if (cap.isPresent()) {
                    if (!hasConsumedOutputItem && result.getItem().equals(entry.getValue().getItem())) {
                        cap.ifPresent(handler -> {
                            resultCap.ifPresent(resultHandler -> {
                                resultHandler.fill(handler.getFluidInTank(0), IFluidHandler.FluidAction.EXECUTE);
                            });
                        });
                        hasConsumedOutputItem = true;
                    }
                }
            }
        }
        return result;
    }

    public static Map<Integer, ItemStack> getInputs(CraftingContainer container) {
        Map<Integer, ItemStack> stacks = new HashMap<>();
        for (int j = 0; j < container.getContainerSize(); ++j) {
            ItemStack itemstack = container.getItem(j);
            if (!itemstack.isEmpty()) {
                stacks.put(j, itemstack);
            }
        }
        return stacks;
    }

    public static NonNullList<ItemStack> getRemainingItems(CraftingContainer container, NonNullList<ItemStack> remainingItems, NonNullList<Ingredient> ingredients, ItemStack resultItem) {
        boolean hasConsumedOutputItem = false;
        Map<Integer, ItemStack> stacks = getInputs(container);

        // Iterate ingredients, if they match with an input, drain that input
        for (Ingredient ingredient: ingredients) {
            if (ingredient instanceof FluidContainerIngredient fluidContainerIngredient) {
                for (Map.Entry<Integer, ItemStack> entry : stacks.entrySet()) {
                    ItemStack itemStack = entry.getValue();

                    if (!hasConsumedOutputItem && !resultItem.getItem().equals(itemStack.getItem())) {
                        // if the output item is one of the input containers, consume it instead of draining it
                        hasConsumedOutputItem = true;
                    } else if (ingredient.test(itemStack)) {
                        LazyOptional<IFluidHandlerItem> cap = FluidUtil.getFluidHandler(itemStack);
                        if (cap.isPresent()) {
                            cap.ifPresent(handler -> {
                                handler.drain(fluidContainerIngredient.getAmount(), IFluidHandler.FluidAction.EXECUTE);
                            });
                            remainingItems.set(entry.getKey(), itemStack.copy());
                            stacks.remove(entry.getKey());
                            break;
                        }
                    }
                }
            }
        }
        return remainingItems;
    }

    public static NonNullList<ItemStack> getRemainingItems(boolean a, CraftingContainer container, NonNullList<ItemStack> remainingItems, NonNullList<Ingredient> ingredients, ItemStack resultItem) {
        boolean hasConsumedOutputItem = false;
        for (int i = 0; i < container.getWidth(); ++i) {
            for (int j = 0; j < container.getHeight() ; ++j) {
                int itemIndex = i + j * container.getWidth();
                ItemStack item = container.getItem(itemIndex);
                for (Ingredient ingredient: ingredients) {
                    LazyOptional<IFluidHandlerItem> cap = FluidUtil.getFluidHandler(item);
                    if (ingredient instanceof FluidContainerIngredient fluidContainerIngredient && cap.isPresent()) {
                        if (hasConsumedOutputItem || !resultItem.getItem().equals(item.getItem())) {
                            // Check that the item has fluid and that it's the correct type and amount
                            cap.ifPresent(handler -> {
                                // Remove fluid used in recipe
                                handler.drain(fluidContainerIngredient.getAmount(), IFluidHandler.FluidAction.EXECUTE);
                            });
                            remainingItems.set(itemIndex, item.copy());
                        } else {
                            // if the output item is one of the input containers, consume it instead of draining it
                            hasConsumedOutputItem = true;
                        }
                    }
                }
            }
        }
        return remainingItems;
    }
}
