package cy.jdkdigital.fluidcrafting.init;

import cy.jdkdigital.fluidcrafting.FluidCrafting;
import cy.jdkdigital.fluidcrafting.world.item.crafting.*;
import net.minecraft.item.crafting.CookingRecipeSerializer;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = FluidCrafting.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModRecipeTypes
{
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, FluidCrafting.MODID);

    public static final RegistryObject<IRecipeSerializer<?>> CRAFTING_SHAPED = RECIPE_SERIALIZERS.register("shaped", FluidShapedRecipe.Serializer::new);
    public static final RegistryObject<IRecipeSerializer<?>> CRAFTING_SHAPELESS = RECIPE_SERIALIZERS.register("shapeless", FluidShapelessRecipe.Serializer::new);
    public static final RegistryObject<CookingRecipeSerializer<?>> SMELTING = RECIPE_SERIALIZERS.register("smelting", () -> new CookingRecipeSerializer<>(FluidSmeltingRecipe::new, 200));
    public static final RegistryObject<CookingRecipeSerializer<?>> BLASTING = RECIPE_SERIALIZERS.register("blasting", () -> new CookingRecipeSerializer<>(FluidBlastingRecipe::new, 100));
    public static final RegistryObject<CookingRecipeSerializer<?>> SMOKING = RECIPE_SERIALIZERS.register("smoking", () -> new CookingRecipeSerializer<>(FluidSmokingRecipe::new, 200));
}
