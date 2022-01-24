package cy.jdkdigital.fluidcrafting.common.crafting.conditions;

import com.google.gson.JsonObject;
import cy.jdkdigital.fluidcrafting.FluidCrafting;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class FluidTagEmptyCondition implements ICondition
{
    private static final ResourceLocation NAME = new ResourceLocation(FluidCrafting.MODID, "fluid_tag_empty");
    private final ResourceLocation tag_name;

    public FluidTagEmptyCondition(String location) {
        this(new ResourceLocation(location));
    }

    public FluidTagEmptyCondition(String namespace, String path) {
        this(new ResourceLocation(namespace, path));
    }

    public FluidTagEmptyCondition(ResourceLocation tag) {
        this.tag_name = tag;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test() {
        ITag<Fluid> tag = TagCollectionManager.getInstance().getFluids().getTag(tag_name);
        return tag == null || tag.getValues().isEmpty();
    }

    @Override
    public String toString() {
        return "fluid_tag_empty(\"" + tag_name + "\")";
    }

    public static class Serializer implements IConditionSerializer<FluidTagEmptyCondition>
    {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, FluidTagEmptyCondition value) {
            json.addProperty("tag", value.tag_name.toString());
        }

        @Override
        public FluidTagEmptyCondition read(JsonObject json) {
            return new FluidTagEmptyCondition(new ResourceLocation(JSONUtils.getAsString(json, "tag")));
        }

        @Override
        public ResourceLocation getID() {
            return FluidTagEmptyCondition.NAME;
        }
    }
}