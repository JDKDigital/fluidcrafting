package cy.jdkdigital.fluidcrafting.init;

import cy.jdkdigital.fluidcrafting.FluidCrafting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags
{
    public static final TagKey<Item> FLUID_CONTAINERS = ItemTags.create(new ResourceLocation(FluidCrafting.MODID, "fluid_containers"));
}
