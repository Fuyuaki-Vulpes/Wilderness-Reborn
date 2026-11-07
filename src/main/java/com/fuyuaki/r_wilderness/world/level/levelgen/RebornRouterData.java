package com.fuyuaki.r_wilderness.world.level.levelgen;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.api.WildernessConstants;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class RebornRouterData {


    private static final ResourceKey<DensityFunction> SPAGHETTI_ROUGHNESS_FUNCTION = createKeyVanilla("overworld/caves/spaghetti_roughness_function");
    private static final ResourceKey<DensityFunction> ENTRANCES = createKeyVanilla("overworld/caves/entrances");
    private static final ResourceKey<DensityFunction> NOODLE = createKeyVanilla("overworld/caves/noodle");
    private static final ResourceKey<DensityFunction> PILLARS = createKeyVanilla("overworld/caves/pillars");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D_THICKNESS_MODULATOR = createKeyVanilla("overworld/caves/spaghetti_2d_thickness_modulator");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D = createKeyVanilla("overworld/caves/spaghetti_2d");
    private static final ResourceKey<DensityFunction> ORE_VEIN_MASK = createKeyVanilla("overworld/ore_vein/mask");
    private static final ResourceKey<DensityFunction> ORE_VEIN_TOGGLE = createKeyVanilla("overworld/ore_vein/toggle");

    public static final OverworldFunctionSet<ResourceKey<DensityFunction>> OVERWORLD_FUNCTIONS = new OverworldFunctionSet<>(
            createKeyVanilla("overworld/temperature"),
            createKeyVanilla("overworld/vegetation"),
            createKeyVanilla("overworld/continents"),
            createKeyVanilla("overworld/erosion"),
            createKeyVanilla("overworld/offset"),
            createKeyVanilla("overworld/factor"),
            createKeyVanilla("overworld/jaggedness"),
            createKeyVanilla("overworld/depth"),
            createKeyVanilla("overworld/sloped_cheese"),
            createKeyVanilla("overworld/preliminary_surface_level"),
            createKeyVanilla("overworld/chunk_surface_level"),
            createKey("overworld/final_density")
    );


    public static void bootstrap(BootstrapContext<DensityFunction> context) {
        HolderGetter<NormalNoise> noises = context.lookup(Registries.NOISE);
        HolderGetter<DensityFunction> functions = context.lookup(Registries.DENSITY_FUNCTION);
        registerTerrainNoises(context, functions, noises, OVERWORLD_FUNCTIONS);
    }

    private static void registerTerrainNoises(
            BootstrapContext<DensityFunction> context,
            HolderGetter<DensityFunction> functions,
            HolderGetter<NormalNoise> noises,
            OverworldFunctionSet<ResourceKey<DensityFunction>> names
    ) {
        DensityFunction caves = underground(functions, noises,DensityFunctions.constant(1.0f));
        context.register(
                names.finalDensity(),
                DensityFunctions.add(
                        DensityFunctions.min(postProcess(slideOverworld(caves), 4, 8),NoiseRouterData.getFunction(functions, NOODLE)), DensityFunctions.beardifier()
                )
        );
    }



    private static DensityFunction underground(HolderGetter<DensityFunction> functions, HolderGetter<NormalNoise> noises, DensityFunction slopedCheese) {
        DensityFunction spaghetti2DFunction = NoiseRouterData.getFunction(functions, SPAGHETTI_2D);
        DensityFunction spaghettiRoughnessFunction = NoiseRouterData.getFunction(functions, SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction layerNoiseSource = DensityFunctions.noise(noises.getOrThrow(Noises.CAVE_LAYER), 8.0);
        DensityFunction layerizedCavernsFunction = layerNoiseSource.square().mul(4.0F);
        DensityFunction cheese = DensityFunctions.noise(noises.getOrThrow(Noises.CAVE_CHEESE), 0.6666666666666666);
        DensityFunction solidifedCheeseWithTopSlide = DensityFunctions.add(
                cheese.add(0.27F).clamp(-1.0F, 1.0F), slopedCheese.mul(-0.64F).add(1.5F).clamp(0.0F, 0.5F)
        );
        DensityFunction baseCaveDensity = DensityFunctions.add(layerizedCavernsFunction, solidifedCheeseWithTopSlide);
        DensityFunction undergroundSubtractions = DensityFunctions.min(
                DensityFunctions.min(baseCaveDensity, NoiseRouterData.getFunction(functions, ENTRANCES)), DensityFunctions.add(spaghetti2DFunction, spaghettiRoughnessFunction)
        );
        DensityFunction pillarsWithoutCutoff = NoiseRouterData.getFunction(functions, PILLARS);
        DensityFunction pillars = DensityFunctions.rangeChoice(
                pillarsWithoutCutoff, -1000000.0F, 0.03F, DensityFunctions.constant(-1000000.0F), pillarsWithoutCutoff
        );
        return DensityFunctions.max(undergroundSubtractions, pillars);
    }

    private static DensityFunction postProcess(DensityFunction slide, int cellSizeXz, int cellSizeY) {
        DensityFunction blended = DensityFunctions.blendDensity(slide);
        return DensityFunctions.interpolated(DensityFunctions.mul(blended, DensityFunctions.constant(0.64F)), cellSizeXz, cellSizeY).squeeze();
    }

    protected static NoiseRouter overworld(HolderGetter<DensityFunction> functions, OverworldFunctionSet<ResourceKey<DensityFunction>> functionNames) {
        OverworldFunctionSet<DensityFunction> functionSet = functionNames.map(key -> NoiseRouterData.getFunction(functions, key));
        return new NoiseRouter(
                functionSet.temperature(),
                functionSet.vegetation(),
                functionSet.continents(),
                functionSet.erosion(),
                functionSet.depth(),
                NoiseRouterData.getFunction(functions, NoiseRouterData.RIDGES),
                functionSet.chunkSurfaceLevel(),
                functionSet.finalDensity()
        );
    }

    protected static Aquifer.Config overworldAquifers(
            HolderGetter<DensityFunction> functions, HolderGetter<NormalNoise> noises, OverworldFunctionSet<ResourceKey<DensityFunction>> names
    ) {
        DensityFunction barrierNoise = DensityFunctions.noise(noises.getOrThrow(Noises.AQUIFER_BARRIER), 0.5);
        DensityFunction fluidLevelFloodednessNoise = DensityFunctions.noise(noises.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67);
        DensityFunction fluidLevelSpreadNoise = DensityFunctions.noise(noises.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143);
        DensityFunction lavaNoise = DensityFunctions.noise(noises.getOrThrow(Noises.AQUIFER_LAVA));
        DensityFunction exclusion = OverworldBiomeBuilder.deepDarkRegion(NoiseRouterData.getFunction(functions, names.erosion()), NoiseRouterData.getFunction(functions, names.depth()));
        return new Aquifer.Config(
                barrierNoise, fluidLevelFloodednessNoise, fluidLevelSpreadNoise, lavaNoise, exclusion, NoiseRouterData.getFunction(functions, names.preliminarySurfaceLevel())
        );
    }


    private static DensityFunction slideOverworld(DensityFunction caves) {
        return slide(caves, WildernessConstants.WORLD_BOTTOM, 0, 24, 0.1171875F);
    }

    private static DensityFunction slide(
            DensityFunction caves, int minY, int bottomStartY, int bottomEndY, float bottomTarget
    ) {
        DensityFunction noiseValue = caves;
        DensityFunction bottomFactor = DensityFunctions.yClampedGradient(minY + bottomStartY, minY + bottomEndY, 0.0F, 1.0F);
        return DensityFunctions.lerp(bottomFactor, bottomTarget, noiseValue);
    }


    private static ResourceKey<DensityFunction> createKeyVanilla(String name) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, Identifier.withDefaultNamespace(name));
    }

    private static ResourceKey<DensityFunction> createKey(String name) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, RWildernessMod.identifier(name));
    }
}
