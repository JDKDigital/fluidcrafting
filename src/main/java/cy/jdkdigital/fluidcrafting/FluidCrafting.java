package cy.jdkdigital.fluidcrafting;

import cy.jdkdigital.fluidcrafting.common.crafting.FluidContainerIngredient;
import cy.jdkdigital.fluidcrafting.common.crafting.conditions.FluidTagEmptyCondition;
import cy.jdkdigital.fluidcrafting.init.ModRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("fluidcrafting")
public class FluidCrafting
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "fluidcrafting";

    public FluidCrafting() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
//        ModItems.ITEMS.register(modEventBus);
//        ModBlocks.BLOCKS.register(modEventBus);
        ModRecipeTypes.RECIPE_SERIALIZERS.register(modEventBus);

        modEventBus.addGenericListener(RecipeSerializer.class, this::registerRecipeSerializers);

        CraftingHelper.register(FluidTagEmptyCondition.Serializer.INSTANCE);

        ForgeMod.enableMilkFluid();
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    }

    public void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
        CraftingHelper.register(new ResourceLocation(MODID, "fluid_container"), FluidContainerIngredient.Serializer.INSTANCE);
    }
}
