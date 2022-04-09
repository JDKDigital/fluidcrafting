package cy.jdkdigital.fluidcrafting.common.item;

import com.supermartijn642.core.TextComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Thermos extends Item {
    final int CAPACITY = 4000;

    public Thermos(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag advanced) {
        FluidStack fluidStack = FluidStack.EMPTY;
        if (stack.getOrCreateTag().contains("fluid")) {
            fluidStack = FluidStack.loadFluidStackFromNBT(stack.getOrCreateTag().getCompound("fluid"));
        }
        Component capacity = TextComponents.string(Integer.toString(CAPACITY)).color(ChatFormatting.GOLD).get();
        if (fluidStack.isEmpty()) {
            list.add(TextComponents.translation("fluidcrafting.thermos.info.capacity", capacity).color(ChatFormatting.GRAY).get());
        } else {
            Component fluidName = TextComponents.fromTextComponent(fluidStack.getDisplayName()).color(ChatFormatting.GOLD).get();
            Component amount = TextComponents.string(Integer.toString(fluidStack.getAmount())).color(ChatFormatting.GOLD).get();
            list.add(TextComponents.translation("fluidcrafting.thermos.info.stored", fluidName, amount, capacity).color(ChatFormatting.GRAY).get());
        }
        super.appendHoverText(stack, level, list, advanced);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ItemFluidHandler(stack, CAPACITY);
    }

    public static class ItemFluidHandler implements ICapabilityProvider, IFluidHandlerItem {

        private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

        private final ItemStack stack;
        private final int capacity;

        public ItemFluidHandler(ItemStack stack, int capacity) {
            this.stack = stack;
            this.capacity = capacity;
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return this.getFluid().copy();
        }

        @Override
        public int getTankCapacity(int tank) {
            return capacity;
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return true;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (resource == null || resource.isEmpty()) {
                return 0;
            }
            FluidStack current = this.getFluid();
            if (!current.isEmpty() && !current.isFluidEqual(resource)) {
                return 0;
            }
            int amount = Math.min(resource.getAmount(), this.getTankCapacity(0) - current.getAmount());
            if (action.execute()) {
                FluidStack newStack = resource.copy();
                newStack.setAmount(current.getAmount() + amount);
                this.setFluid(newStack);
            }
            return amount;
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            if (resource == null || resource.isEmpty()) {
                return FluidStack.EMPTY;
            }
            FluidStack current = this.getFluid();
            if (current.isEmpty() || !current.isFluidEqual(resource)) {
                return FluidStack.EMPTY;
            }
            int amount = Math.min(current.getAmount(), resource.getAmount());
            if (action.execute()) {
                FluidStack newStack = current.copy();
                newStack.shrink(amount);
                this.setFluid(newStack);
            }
            current.setAmount(amount);
            return current;
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            if (maxDrain == 0) {
                return FluidStack.EMPTY;
            }
            FluidStack current = this.getFluid();
            if (current.isEmpty()) {
                return FluidStack.EMPTY;
            }
            int amount = Math.min(current.getAmount(), maxDrain);
            if (action.execute()) {
                FluidStack newStack = current.copy();
                newStack.shrink(amount);
                this.setFluid(newStack);
            }
            current.setAmount(amount);
            return current;
        }

        @Override
        public @NotNull ItemStack getContainer() {
            return this.stack;
        }

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(cap, this.holder);
        }

        private FluidStack getFluid() {
            CompoundTag compound = this.stack.getOrCreateTag();
            return compound.contains("fluid") ? FluidStack.loadFluidStackFromNBT(compound.getCompound("fluid")) : FluidStack.EMPTY;
        }

        private void setFluid(FluidStack fluid) {
            this.stack.getOrCreateTag().put("fluid", fluid.writeToNBT(new CompoundTag()));
        }
    }
}
