package cy.jdkdigital.fluidcrafting.common.crafting;

import com.google.gson.JsonObject;
import cy.jdkdigital.fluidcrafting.FluidCrafting;
import cy.jdkdigital.fluidcrafting.init.ModTags;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.VanillaIngredientSerializer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.stream.Stream;

public class FluidContainerIngredient extends Ingredient
{
    private int amount;

    protected FluidContainerIngredient(FluidValue value) {
        super(Stream.of(value));
        this.amount = value.getAmount();
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        LazyOptional<IFluidHandlerItem> cap = FluidUtil.getFluidHandler(input);
        if (!cap.isPresent()) {
            return false;
        }

        FluidStack inputFluid = cap.map(h -> h.getFluidInTank(0)).orElse(FluidStack.EMPTY);

        if (inputFluid.equals(FluidStack.EMPTY)) {
            return false;
        }

        for (Ingredient.Value value : values) {
            for (ItemStack stack : value.getItems()) {
                return FluidUtil.getFluidHandler(stack).map(h -> inputFluid.getAmount() >= h.getFluidInTank(0).getAmount() && inputFluid.getFluid().isSame(h.getFluidInTank(0).getFluid())).orElse(false);
            }
        }

        return false;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer extends VanillaIngredientSerializer
    {
        public static final FluidContainerIngredient.Serializer INSTANCE = new FluidContainerIngredient.Serializer();

        @Override
        public FluidContainerIngredient parse(JsonObject json) {
            int amount = json.has("amount") ? json.get("amount").getAsInt() : 1000;
            if (json.has("tag")) {
                String fluidId = json.get("tag").getAsString();
                TagKey<Fluid> tag = TagKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(fluidId));
                return new FluidContainerIngredient(new FluidTagValue(tag, amount));
            } else {
                String fluidId = json.get("fluid").getAsString();
                ResourceLocation id = new ResourceLocation(fluidId);
                Fluid fluid = Fluids.WATER;
                if (ForgeRegistries.FLUIDS.containsKey(id)) {
                    fluid = ForgeRegistries.FLUIDS.getValue(id);
                } else {
                    FluidCrafting.LOGGER.warn("Fluid with id " + id + " does not exist. Unable to parse recipe ingredient.");
                }
                return new FluidContainerIngredient(new FluidStackValue(new FluidStack(fluid, amount)));
            }
        }
    }

    public static class FluidStackValue implements FluidValue
    {
        private FluidStack fluid;

        public FluidStackValue(FluidStack fluid) {
            this.fluid = fluid;
        }

        @Override
        public int getAmount() {
            return fluid.getAmount();
        }

        @Override
        public Collection<ItemStack> getItems() {
            Ingredient.TagValue tanks = new Ingredient.TagValue(ModTags.FLUID_CONTAINERS);

            Collection<ItemStack> items = tanks.getItems();
            for (ItemStack item : items) {
                LazyOptional<IFluidHandlerItem> cap = FluidUtil.getFluidHandler(item);
                if (cap.isPresent()) {
                    cap.ifPresent(handler -> {
                        handler.drain(handler.getTankCapacity(0), IFluidHandler.FluidAction.EXECUTE);
                        handler.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                    });
                }
            }
            return items;
        }

        @Override
        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("fluid", Registry.FLUID.getKey(this.fluid.getFluid()).toString());
            jsonobject.addProperty("amount", this.fluid.getAmount());
            return jsonobject;
        }
    }

    public static class FluidTagValue implements FluidValue
    {
        private final TagKey<Fluid> tag;
        private Collection<FluidStack> fluids;
        private int amount;

        public FluidTagValue(TagKey<Fluid> tag, int amount) {
            this.tag = tag;
            this.amount = amount;
        }

        @Override
        public int getAmount() {
            return amount;
        }

        @Override
        public Collection<ItemStack> getItems() {
            Ingredient.TagValue tanks = new Ingredient.TagValue(ModTags.FLUID_CONTAINERS);

            Collection<ItemStack> items = tanks.getItems();
            for (FluidStack fluid : getFluids()) {
                for (ItemStack item : items) {
                    LazyOptional<IFluidHandlerItem> cap = FluidUtil.getFluidHandler(item);
                    if (cap.isPresent()) {
                        cap.ifPresent(handler -> {
                            handler.drain(handler.getTankCapacity(0), IFluidHandler.FluidAction.EXECUTE);
                            handler.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                        });
                    }
                }
            }
            return items;
        }

        private Collection<FluidStack> getFluids() {
            if (fluids == null) {
                if (tag != null) {
                    Registry.FLUID.getTagOrEmpty(this.tag).forEach(fluidHolder -> {
                        Fluid fluid = fluidHolder.value();
                        if (fluid instanceof FlowingFluid flowingFluid) {
                            fluid = flowingFluid.getSource();
                        }
                        fluids.add(new FluidStack(fluid, this.getAmount()));
                    });
                }
            }
            return fluids;
        }

        @Override
        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("tag", this.tag.toString());
            jsonobject.addProperty("amount", this.getAmount());
            return jsonobject;
        }
    }

    interface FluidValue extends Ingredient.Value
    {
        int getAmount();
    }
}
