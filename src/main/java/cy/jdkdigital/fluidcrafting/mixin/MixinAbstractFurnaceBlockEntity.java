package cy.jdkdigital.fluidcrafting.mixin;

import cy.jdkdigital.fluidcrafting.common.crafting.FluidContainerIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = AbstractFurnaceBlockEntity.class)
public class MixinAbstractFurnaceBlockEntity
{
    @Inject(
        at = {@At("RETURN")},
        method = {"burn(Lnet/minecraft/world/item/crafting/Recipe;Lnet/minecraft/core/NonNullList;I)Z"}
    )
    public void retainFluidContainer(@Nullable Recipe<?> recipe, NonNullList<ItemStack> stacks, int maxStackSize, CallbackInfoReturnable callbackInfo) {
        if (callbackInfo.getReturnValueZ()) {
            ItemStack item = stacks.get(0);
            for (Ingredient ingredient: recipe.getIngredients()) {
                if (ingredient instanceof FluidContainerIngredient fluidContainerIngredient && item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
                    // Check that the item has fluid and that it's the correct type and amount
                    item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler -> {
                        handler.drain(fluidContainerIngredient.getAmount(), IFluidHandler.FluidAction.EXECUTE);
                    });
                    item.grow(1); // counter the shrink called in vanilla handler
                    stacks.set(0, item.copy());
                }
            }
        }
    }
}
