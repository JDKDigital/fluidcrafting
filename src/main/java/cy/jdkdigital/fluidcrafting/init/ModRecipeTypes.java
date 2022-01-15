package cy.jdkdigital.fluidcrafting.init;

import cy.jdkdigital.fluidcrafting.FluidCrafting;
import cy.jdkdigital.fluidcrafting.world.item.crafting.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = FluidCrafting.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModRecipeTypes
{
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, FluidCrafting.MODID);

    public static final RegistryObject<RecipeSerializer<?>> CRAFTING_SHAPED = RECIPE_SERIALIZERS.register("shaped", FluidShapedRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<?>> CRAFTING_SHAPELESS = RECIPE_SERIALIZERS.register("shapeless", FluidShapelessRecipe.Serializer::new);
    public static final RegistryObject<SimpleCookingSerializer<?>> SMELTING = RECIPE_SERIALIZERS.register("smelting", () -> new SimpleCookingSerializer<>(FluidSmeltingRecipe::new, 200));
    public static final RegistryObject<SimpleCookingSerializer<?>> BLASTING = RECIPE_SERIALIZERS.register("blasting", () -> new SimpleCookingSerializer<>(FluidBlastingRecipe::new, 100));
    public static final RegistryObject<SimpleCookingSerializer<?>> SMOKING = RECIPE_SERIALIZERS.register("smoking", () -> new SimpleCookingSerializer<>(FluidSmokingRecipe::new, 200));
}
