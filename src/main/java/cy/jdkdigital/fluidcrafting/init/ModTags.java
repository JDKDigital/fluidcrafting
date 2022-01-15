package cy.jdkdigital.fluidcrafting.init;

import cy.jdkdigital.fluidcrafting.FluidCrafting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

public class ModTags
{
    public static final Tag<Item> FLUID_CONTAINERS = ItemTags.createOptional(new ResourceLocation(FluidCrafting.MODID, "fluid_containers"));
}
