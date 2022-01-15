package cy.jdkdigital.fluidcrafting.common.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import cy.jdkdigital.fluidcrafting.FluidCrafting;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class FluidContainerIngredient extends Ingredient
{
    private int amount;

    protected FluidContainerIngredient(Value values) {
        super(Stream.of(values));
        this.amount = values.getAmount();
    }

    protected FluidContainerIngredient(Stream<? extends Ingredient.Value> values, int amount) {
        super(values);
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        LazyOptional<IFluidHandlerItem> cap = input.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
        if (!cap.isPresent()) {
            return false;
        }
        return cap.map(handler -> {
            FluidStack tankFluid = handler.getFluidInTank(0);
            if (tankFluid.getAmount() < getAmount()) {
                return false;
            }
            for (Ingredient.Value value : values) {
                if (value instanceof FluidValue fluidValue && fluidValue.getFluid().isFluidEqual(tankFluid)) {
                    return true;
                } else if (value instanceof FluidTagValue fluidTagValue) {
                    for (FluidStack fluid : fluidTagValue.getFluids()) {
                        if (fluid.isFluidEqual(tankFluid)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }).orElse(false);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return FluidContainerIngredient.Serializer.INSTANCE;
    }

    public static class Serializer implements IIngredientSerializer<FluidContainerIngredient>
    {
        public static final FluidContainerIngredient.Serializer INSTANCE = new FluidContainerIngredient.Serializer();

        @Override
        public FluidContainerIngredient parse(JsonObject json) {
            int amount = json.has("amount") ? json.get("amount").getAsInt() : 1000;
            if (json.has("tag")) {
                String fluidId = json.get("tag").getAsString();
                Tag<Fluid> tag = SerializationTags.getInstance().getOrEmpty(Registry.FLUID_REGISTRY).getTag(new ResourceLocation(fluidId));
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
                return new FluidContainerIngredient(new FluidValue(new FluidStack(fluid, amount)));
            }
        }

        @Override
        public FluidContainerIngredient parse(FriendlyByteBuf buffer) {
            return new FluidContainerIngredient(Stream.generate(() -> new Ingredient.ItemValue(buffer.readItem())).limit(buffer.readVarInt()), buffer.readVarInt());
        }

        @Override
        public void write(FriendlyByteBuf buffer, FluidContainerIngredient ingredient) {
            ItemStack[] items = ingredient.getItems();
            buffer.writeVarInt(items.length);

            for (ItemStack stack : items) {
                buffer.writeItem(stack);
            }
            buffer.writeInt(ingredient.getAmount());
        }
    }

    public static class FluidValue implements Value
    {
        private FluidStack fluid;

        public FluidValue(FluidStack fluid) {
            this.fluid = fluid;
        }

        @Override
        public int getAmount() {
            return fluid.getAmount();
        }

        public FluidStack getFluid() {
            return fluid;
        }

        @Override
        public Collection<ItemStack> getItems() {
            Ingredient.TagValue tanks = new Ingredient.TagValue(SerializationTags.getInstance().getTagOrThrow(Registry.ITEM_REGISTRY, new ResourceLocation(FluidCrafting.MODID, "fluid_containers"), (tag) -> new JsonSyntaxException("Unknown item tag '" + tag + "'")));

            Collection<ItemStack> items = tanks.getItems();
            for (ItemStack item : items) {
                if (item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
                    item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler -> {
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

    public static class FluidTagValue implements Value
    {
        private final Tag<Fluid> tag;
        private int amount;

        public FluidTagValue(Tag<Fluid> tag, int amount) {
            this.tag = tag;
            this.amount = amount;
        }

        @Override
        public int getAmount() {
            return amount;
        }

        @Override
        public Collection<ItemStack> getItems() {
            Ingredient.TagValue tanks = new Ingredient.TagValue(SerializationTags.getInstance().getTagOrThrow(Registry.ITEM_REGISTRY, new ResourceLocation(FluidCrafting.MODID, "fluid_containers"), (tag) -> new JsonSyntaxException("Unknown item tag '" + tag + "'")));

            Collection<ItemStack> items = tanks.getItems();
            for (FluidStack fluid : getFluids()) {
                for (ItemStack item : items) {
                    if (item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
                        item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler -> {
                            handler.drain(handler.getTankCapacity(0), IFluidHandler.FluidAction.EXECUTE);
                            handler.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                        });
                    }
                }
            }
            return items;
        }

        public Collection<FluidStack> getFluids() {
            List<FluidStack> list = Lists.newArrayList();

            for (Fluid fluid : this.tag.getValues()) {
                list.add(new FluidStack(fluid, amount));
            }

            if (list.size() == 0 && !net.minecraftforge.common.ForgeConfig.SERVER.treatEmptyTagsAsAir.get()) {
                list.add(new FluidStack(Fluids.WATER, amount));
            }
            return list;
        }

        @Override
        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("tag", "#" + SerializationTags.getInstance().getIdOrThrow(Registry.FLUID_REGISTRY, this.tag, () -> {
                return new IllegalStateException("Unknown fluid tag");
            }));
            jsonobject.addProperty("amount", this.getAmount());
            return jsonobject;
        }
    }

    interface Value extends Ingredient.Value
    {
        int getAmount();
    }
}
