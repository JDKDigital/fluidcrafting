package cy.jdkdigital.fluidcrafting.mixin;

import cy.jdkdigital.fluidcrafting.common.crafting.FluidContainerIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = AbstractFurnaceTileEntity.class)
public class MixinAbstractFurnaceBlockEntity
{
    @Shadow
    protected NonNullList<ItemStack> items;

    @Inject(
        at = {@At("TAIL")},
        method = {"burn(Lnet/minecraft/item/crafting/IRecipe;)V"}
    )
    public void retainFluidContainer(@Nullable IRecipe<?> p_214007_1_, CallbackInfo info) {
        ItemStack item = this.items.get(0);
        for (Ingredient ingredient: p_214007_1_.getIngredients()) {
            if (ingredient instanceof FluidContainerIngredient && item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
                // Check that the item has fluid and that it's the correct type and amount
                item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler -> {
                    handler.drain(((FluidContainerIngredient) ingredient).getAmount(), IFluidHandler.FluidAction.EXECUTE);
                });
                item.grow(1); // counter the shrink called in vanilla handler
            }
        }
    }
}
