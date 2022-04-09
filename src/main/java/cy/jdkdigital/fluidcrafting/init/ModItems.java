package cy.jdkdigital.fluidcrafting.init;

import cy.jdkdigital.fluidcrafting.FluidCrafting;
import cy.jdkdigital.fluidcrafting.common.item.Thermos;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, FluidCrafting.MODID);

    public static final RegistryObject<Item> THERMOS = ITEMS.register("thermos", () -> new Thermos((new Item.Properties()).stacksTo(1).tab(CreativeModeTab.TAB_MISC)));
}
