package com.fuyuaki.r_wilderness.world.block;

import com.fuyuaki.r_wilderness.data.worldgen.tree.ModTreeGrower;
import com.fuyuaki.r_wilderness.world.block.soils.ModFarmBlock;
import com.fuyuaki.r_wilderness.world.block.soils.ModSoilBlock;
import com.fuyuaki.r_wilderness.world.block.tree.BranchBlock;
import com.fuyuaki.r_wilderness.world.block.woods.*;
import com.fuyuaki.r_wilderness.world.item.RItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class RBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

    //Blocks

    //Tree Stuff
    public static final DeferredBlock<Block> BRANCH = registerBlock("branch", BranchBlock::new, logProperties(MapColor.WOOD, SoundType.WOOD).dynamicShape().noLootTable());


    //Stones

    public static final DeferredBlock<Block> CHALK = registerBlock("chalk", BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_CONCRETE));
    public static final DeferredBlock<Block> LIMESTONE = registerBlock("limestone", BlockBehaviour.Properties.ofFullCopy(Blocks.STONE));
    public static final DeferredBlock<Block> MUD_STONE = registerBlock("mud_stone", BlockBehaviour.Properties.ofFullCopy(Blocks.MUD_BRICKS));

    private static final BlockBehaviour.Properties SCHINITE_PROPERTIES =
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(4.5F, 7.0F);
    public static final DeferredBlock<Block> SCHINITE = registerBlock("schinite",
            SCHINITE_PROPERTIES);
    public static final DeferredBlock<StairBlock> SCHINITE_STAIRS = registerStair(
            "schinite_stairs",
            SCHINITE,
            SCHINITE_PROPERTIES
    );
    public static final DeferredBlock<Block> SCHINITE_SLAB = registerBlock(
            "schinite_slab",
            SlabBlock::new,
            SCHINITE_PROPERTIES
    );
    public static final DeferredBlock<Block> SCHINITE_WALL = registerBlock(
            "schinite_wall",
            WallBlock::new,
            SCHINITE_PROPERTIES
    );
    private static final BlockBehaviour.Properties MAGNEISS_PROPERTIES =
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(5.5F, 5.0F);

    public static final DeferredBlock<Block> MAGNEISS = registerBlock("magneiss",
            MAGNEISS_PROPERTIES);
    public static final DeferredBlock<StairBlock> MAGNEISS_STAIRS = registerStair(
            "magneiss_stairs",
            MAGNEISS,
            MAGNEISS_PROPERTIES
    );
    public static final DeferredBlock<Block> MAGNEISS_SLAB = registerBlock(
            "magneiss_slab",
            SlabBlock::new,
            MAGNEISS_PROPERTIES
    );
    public static final DeferredBlock<Block> MAGNEISS_WALL = registerBlock(
            "magneiss_wall",
            WallBlock::new,
            MAGNEISS_PROPERTIES
    );
    private static final BlockBehaviour.Properties MALATITE_PROPERTIES =
            BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_BLACK)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .strength(7.0F, 4.0F);
    public static final DeferredBlock<Block> MALATITE = registerBlock("malatite",
            MALATITE_PROPERTIES);
    public static final DeferredBlock<StairBlock> MALATITE_STAIRS = registerStair(
            "malatite_stairs",
            MALATITE,
            MALATITE_PROPERTIES
    );

    public static final DeferredBlock<Block> MALATITE_SLAB = registerBlock(
            "malatite_slab",
            SlabBlock::new,
            MALATITE_PROPERTIES
    );
    public static final DeferredBlock<Block> MALATITE_WALL = registerBlock(
            "malatite_wall",
            WallBlock::new,
            MALATITE_PROPERTIES
    );

    public static final DeferredBlock<Block> COBBLED_SCHINITE = registerBlock("cobbled_schinite",
            SCHINITE_PROPERTIES
    );
    public static final DeferredBlock<StairBlock> COBBLED_SCHINITE_STAIRS = registerStair("cobbled_schinite_stairs",
            COBBLED_SCHINITE,SCHINITE_PROPERTIES
    );
    public static final DeferredBlock<Block> COBBLED_SCHINITE_SLAB = registerBlock("cobbled_schinite_slab",
            SlabBlock::new, SCHINITE_PROPERTIES
    );
    public static final DeferredBlock<Block> COBBLED_SCHINITE_WALL = registerBlock("cobbled_schinite_wall",
            WallBlock::new, SCHINITE_PROPERTIES
    );

    public static final DeferredBlock<Block> COBBLED_MAGNEISS = registerBlock("cobbled_magneiss",
            MAGNEISS_PROPERTIES
    );
    public static final DeferredBlock<StairBlock> COBBLED_MAGNEISS_STAIRS = registerStair("cobbled_magneiss_stairs",
            COBBLED_MAGNEISS,MAGNEISS_PROPERTIES
    );
    public static final DeferredBlock<Block> COBBLED_MAGNEISS_SLAB = registerBlock("cobbled_magneiss_slab",
            SlabBlock::new, MAGNEISS_PROPERTIES
    );
    public static final DeferredBlock<Block> COBBLED_MAGNEISS_WALL = registerBlock("cobbled_magneiss_wall",
            WallBlock::new, MAGNEISS_PROPERTIES
    );

    public static final DeferredBlock<Block> COBBLED_MALATITE = registerBlock("cobbled_malatite",
            MALATITE_PROPERTIES
    );
    public static final DeferredBlock<StairBlock> COBBLED_MALATITE_STAIRS = registerStair("cobbled_malatite_stairs",
            COBBLED_MALATITE,MALATITE_PROPERTIES
    );
    public static final DeferredBlock<Block> COBBLED_MALATITE_SLAB = registerBlock("cobbled_malatite_slab",
            SlabBlock::new, MALATITE_PROPERTIES
    );
    public static final DeferredBlock<Block> COBBLED_MALATITE_WALL = registerBlock("cobbled_malatite_wall",
            WallBlock::new, MALATITE_PROPERTIES
    );



    //Soils

    public static final DeferredBlock<Block> CHALKY_SOIL = registerBlock("chalky_soil",
            ModSoilBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL));
    public static final DeferredBlock<Block> CHALKY_FARMLAND = registerBlock("chalky_farmland",
            ModFarmBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL)
    );
    public static final DeferredBlock<Block> CLAY_SOIL = registerBlock("clay_soil",
            ModSoilBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CLAY));
    public static final DeferredBlock<Block> CLAY_FARMLAND = registerBlock("clay_farmland",
            ModFarmBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CLAY)
    );
    public static final DeferredBlock<Block> PEAT = registerBlock("peat",
            ModSoilBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT));
    public static final DeferredBlock<Block> PEAT_FARMLAND = registerBlock("peat_farmland",
            ModFarmBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT)
    );
    public static final DeferredBlock<Block> SANDY_SOIL = registerBlock("sandy_soil",
            ModSoilBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.SAND));
    public static final DeferredBlock<Block> SANDY_FARMLAND = registerBlock("sandy_farmland",
            ModFarmBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.SAND)
    );
    public static final DeferredBlock<Block> SILT = registerBlock("silt",
            ModSoilBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.MUD));
    public static final DeferredBlock<Block> SILT_FARMLAND = registerBlock("silt_farmland",
            ModFarmBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.MUD)
    );

    //Trees & Wood

    public static final DeferredBlock<Block> ALPINE_LOG = registerBlock("alpine_log",  RotatedPillarBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG));
    public static final DeferredBlock<Block> STRIPPED_ALPINE_LOG = registerBlock("stripped_alpine_log",  RotatedPillarBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG));
    public static final DeferredBlock<Block> ALPINE_WOOD = registerBlock("alpine_wood",  RotatedPillarBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD));
    public static final DeferredBlock<Block> STRIPPED_ALPINE_WOOD = registerBlock("stripped_alpine_wood",  RotatedPillarBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD));

    public static final DeferredBlock<Block> ALPINE_PLANKS = registerBlock("alpine_planks",  Block::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS));
    public static final DeferredBlock<Block> ALPINE_LEAVES = registerBlock("alpine_leaves", (properties) -> {
        return new TintedParticleLeavesBlock(0.1F, properties);
    }, leavesProperties(SoundType.GRASS));

    public static final DeferredBlock<Block> ALPINE_SAPLING = registerBlock("alpine_sapling",
            properties -> new SaplingBlock(ModTreeGrower.ALPINE_TREE,properties), BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SAPLING));
    public static final DeferredBlock<Block> POTTED_ALPINE_SAPLING = registerBlock("potted_alpine_sapling",
            properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, ALPINE_SAPLING::value, properties),flowerPotProperties());
    public static final DeferredBlock<Block> ALPINE_STAIRS = registerBlock("alpine_stairs",
            properties -> new StairBlock(Blocks.OAK_STAIRS.defaultBlockState(),properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS));

    public static final DeferredBlock<Block> ALPINE_SLAB = registerBlock("alpine_slab",
            SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB));

    public static final DeferredBlock<Block> ALPINE_PRESSURE_PLATE = registerBlock("alpine_pressure_plate",
            properties -> new PressurePlateBlock(ModBlockSetTypes.ALPINE,properties), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE));
    public static final DeferredBlock<Block> ALPINE_BUTTON = registerBlock("alpine_button",
            properties -> new ButtonBlock(ModBlockSetTypes.ALPINE, 10,properties), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON));

    public static final DeferredBlock<Block> ALPINE_FENCE = registerBlock("alpine_fence",
            FenceBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE));
    public static final DeferredBlock<Block> ALPINE_FENCE_GATE = registerBlock("alpine_fence_gate",
            properties -> new FenceGateBlock(ModWoodTypes.ALPINE,properties), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE));

    public static final DeferredBlock<Block> ALPINE_DOOR = registerBlock("alpine_door",
            properties -> new DoorBlock(ModBlockSetTypes.ALPINE,properties), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR));
    public static final DeferredBlock<Block> ALPINE_TRAPDOOR = registerBlock("alpine_trapdoor",
            properties -> new TrapDoorBlock(ModBlockSetTypes.ALPINE,properties), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR));

    public static final DeferredBlock<Block> ALPINE_SIGN = registerNoItemBlock("alpine_sign",
            properties -> new ModStandingSignBlock(ModWoodTypes.ALPINE, properties), BlockBehaviour.Properties.ofFullCopy(Blocks.PALE_OAK_SIGN)
    );
    public static final DeferredBlock<Block> ALPINE_WALL_SIGN = registerNoItemBlock("alpine_wall_sign",
            properties -> new ModWallSignBlock(ModWoodTypes.ALPINE, properties), BlockBehaviour.Properties.ofFullCopy(Blocks.PALE_OAK_SIGN)
    );
    public static final DeferredBlock<Block> ALPINE_HANGING_SIGN = registerNoItemBlock("alpine_hanging_sign",
            properties -> new ModCeilingHangingSignBlock(ModWoodTypes.ALPINE, properties), BlockBehaviour.Properties.ofFullCopy(Blocks.PALE_OAK_HANGING_SIGN)
    );
    public static final DeferredBlock<Block> ALPINE_HANGING_WALL_SIGN = registerNoItemBlock("alpine_hanging_wall_sign",
            properties -> new ModWallHangingSignBlock(ModWoodTypes.ALPINE, properties), BlockBehaviour.Properties.ofFullCopy(Blocks.PALE_OAK_HANGING_SIGN)
    );

    //Flowers

    public static final DeferredBlock<Block> BELLFLOWER = registerBlock(
            "bellflower",
            properties -> new FlowerBlock(MobEffects.GLOWING, 5.0F, properties),
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollision()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY)
    );
    public static final DeferredBlock<Block> POTTED_BELLFLOWER = register("potted_bellflower", properties -> new FlowerPotBlock(BELLFLOWER.get(), properties), flowerPotProperties());
    public static final DeferredBlock<Block> MOSS_COMPANION = registerBlock(
            "moss_companion",
            properties -> new FlowerBlock(MobEffects.OOZING, 5.0F, properties),
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollision()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY)
    );
    public static final DeferredBlock<Block> POTTED_MOSS_COMPANION = register("potted_moss_companion", properties -> new FlowerPotBlock(MOSS_COMPANION.get(), properties), flowerPotProperties());
    public static final DeferredBlock<Block> MOUNTAIN_HEATHER = registerBlock(
            "mountain_heather",
            properties -> new FlowerBlock(MobEffects.WEAVING, 5.0F, properties),
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollision()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY)
    );
    public static final DeferredBlock<Block> POTTED_MOUNTAIN_HEATHER = register("potted_mountain_heather", properties -> new FlowerPotBlock(MOUNTAIN_HEATHER.get(), properties), flowerPotProperties());
    public static final DeferredBlock<Block> SNOWBELL = registerBlock(
            "snowbell",
            properties -> new FlowerBlock(MobEffects.FIRE_RESISTANCE, 5.0F, properties),
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollision()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY)
    );
    public static final DeferredBlock<Block> POTTED_SNOWBELL = register("potted_snowbell", properties -> new FlowerPotBlock(SNOWBELL.get(), properties), flowerPotProperties());


    //Methods



    private static  <B extends Block> DeferredBlock<B> register(String name, Function<BlockBehaviour.Properties, B> factory, BlockBehaviour.Properties properties) {

        return BLOCKS.registerBlock(name,factory,() -> properties);
    }

    private static <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, B> factory, BlockBehaviour.Properties properties) {
        DeferredBlock<B> BLOCK = register(name,factory,properties);
        RItems.registerItem(name, (p) -> new BlockItem(BLOCK.get(), p),new Item.Properties());
        return BLOCK;
    }
    private static <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, B> factory, Supplier<BlockBehaviour.Properties> properties) {
        DeferredBlock<B> BLOCK = BLOCKS.registerBlock(name,factory,properties);
        RItems.registerItem(name, (p) -> new BlockItem(BLOCK.get(), p),new Item.Properties());
        return BLOCK;
    }


    private static <B extends Block> DeferredBlock<?> registerBlockWithItemCustomProperties(String name, Function<BlockBehaviour.Properties, B> factory, BlockBehaviour.Properties properties, Item.Properties iProperties) {
        DeferredBlock<B> BLOCK = register(name,factory,properties);
        RItems.registerItem(name, (p) -> new BlockItem(BLOCK.get(), p), iProperties);
        return BLOCK;
    }

    private static  <B extends Block> DeferredBlock<B>  registerNoItemBlock(String name, Function<BlockBehaviour.Properties, B> factory, BlockBehaviour.Properties properties) {
        return register(name, factory, properties);
    }

    private static  DeferredBlock<Block> registerBlock(String name, BlockBehaviour.Properties properties) {
        return registerBlock(name, Block::new, properties);
    }

    private static DeferredBlock<StairBlock> registerStair(String name, DeferredBlock<Block> baseBlock,BlockBehaviour.Properties properties) {
        return registerBlock(name, p -> new StairBlock(baseBlock.get().defaultBlockState(), p), () -> properties);
    }

    private static BlockBehaviour.Properties signWallVariant(Block baseBlock, boolean overrideDescription) {
        BlockBehaviour.Properties blockbehaviour$properties = baseBlock.properties();
        BlockBehaviour.Properties blockbehaviour$properties1 = BlockBehaviour.Properties.of().overrideLootTable(baseBlock.getLootTable());
        if (overrideDescription) {
            blockbehaviour$properties1 = blockbehaviour$properties1.overrideDescription(baseBlock.getDescriptionId());
        }

        return blockbehaviour$properties1;
    }

    private static ToIntFunction<BlockState> litBlockEmission(int lightValue) {
        return p_50763_ -> p_50763_.getValue(BlockStateProperties.LIT) ? lightValue : 0;
    }

    private static Function<BlockState, MapColor> waterloggedMapColor(MapColor unwaterloggedMapColor) {
        return p_341578_ -> p_341578_.getValue(BlockStateProperties.WATERLOGGED) ? MapColor.WATER : unwaterloggedMapColor;
    }

    public static Boolean never(BlockState state, BlockGetter level, BlockPos pos, EntityType<?> entity) {
        return false;
    }

    public static Boolean always(BlockState state, BlockGetter level, BlockPos pos, EntityType<?> entity) {
        return true;
    }

    public static Boolean ocelotOrParrot(BlockState state, BlockGetter level, BlockPos pos, EntityType<?> entity) {
        return entity == EntityType.OCELOT || entity == EntityType.PARROT;
    }

    private static DeferredBlock<BedBlock> registerBed(String name, DyeColor color) {
        return registerBlock(
                name,
                p_368367_ -> new BedBlock(color, p_368367_),
                BlockBehaviour.Properties.of()
                        .mapColor(p_284863_ -> p_284863_.getValue(BedBlock.PART) == BedPart.FOOT ? color.getMapColor() : MapColor.WOOL)
                        .sound(SoundType.WOOD)
                        .strength(0.2F)
                        .noOcclusion()
                        .ignitedByLava()
                        .pushReaction(PushReaction.DESTROY)
        );
    }

    private static BlockBehaviour.Properties logProperties(MapColor sideColor, MapColor topColor, SoundType sound) {
        return BlockBehaviour.Properties.of()
                .mapColor(p_152624_ -> p_152624_.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? sideColor : topColor)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F)
                .sound(sound)
                .ignitedByLava();
    }
    private static BlockBehaviour.Properties logProperties(MapColor color, SoundType sound) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F)
                .sound(sound)
                .ignitedByLava();
    }

    private static BlockBehaviour.Properties netherStemProperties(MapColor color) {
        return BlockBehaviour.Properties.of().mapColor(p_152620_ -> color).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.STEM);
    }

    private static boolean always(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return true;
    }

    private static boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return false;
    }

    private static DeferredBlock<StainedGlassBlock> registerStainedGlass(String name, DyeColor color) {
        return registerBlock(
                name,
                p_368166_ -> new StainedGlassBlock(color, p_368166_),
                BlockBehaviour.Properties.of()
                        .mapColor(color)
                        .instrument(NoteBlockInstrument.HAT)
                        .strength(0.3F)
                        .sound(SoundType.GLASS)
                        .noOcclusion()
                        .isValidSpawn(RBlocks::never)
                        .isRedstoneConductor(RBlocks::never)
                        .isSuffocating(RBlocks::never)
                        .isViewBlocking(RBlocks::never)
        );
    }

    private static BlockBehaviour.Properties leavesProperties(SoundType sound) {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .strength(0.2F)
                .randomTicks()
                .sound(sound)
                .noOcclusion()
                .isValidSpawn(RBlocks::ocelotOrParrot)
                .isSuffocating(RBlocks::never)
                .isViewBlocking(RBlocks::never)
                .ignitedByLava()
                .pushReaction(PushReaction.DESTROY)
                .isRedstoneConductor(RBlocks::never);
    }

    private static BlockBehaviour.Properties shulkerBoxProperties(MapColor mapColor) {
        return BlockBehaviour.Properties.of()
                .mapColor(mapColor)
                .forceSolidOn()
                .strength(2.0F)
                .dynamicShape()
                .noOcclusion()
                .isSuffocating(NOT_CLOSED_SHULKER)
                .isViewBlocking(NOT_CLOSED_SHULKER)
                .pushReaction(PushReaction.DESTROY);
    }

    private static BlockBehaviour.Properties pistonProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(1.5F)
                .isRedstoneConductor(RBlocks::never)
                .isSuffocating(NOT_EXTENDED_PISTON)
                .isViewBlocking(NOT_EXTENDED_PISTON)
                .pushReaction(PushReaction.BLOCK);
    }
    private static final BlockBehaviour.StatePredicate NOT_CLOSED_SHULKER = (p_304352_, p_304353_, p_304354_) -> p_304353_.getBlockEntity(p_304354_) instanceof ShulkerBoxBlockEntity shulkerboxblockentity
            ? shulkerboxblockentity.isClosed()
            : true;
    private static final BlockBehaviour.StatePredicate NOT_EXTENDED_PISTON = (p_152641_, p_152642_, p_152643_) -> !p_152641_.getValue(PistonBaseBlock.EXTENDED);


    private static BlockBehaviour.Properties buttonProperties() {
        return BlockBehaviour.Properties.of().noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY);
    }

    private static BlockBehaviour.Properties flowerPotProperties() {
        return BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY);
    }

    private static BlockBehaviour.Properties candleProperties(MapColor mapColor) {
        return BlockBehaviour.Properties.of()
                .mapColor(mapColor)
                .noOcclusion()
                .strength(0.1F)
                .sound(SoundType.CANDLE)
                .lightLevel(CandleBlock.LIGHT_EMISSION)
                .pushReaction(PushReaction.DESTROY);
    }

    public static void init(IEventBus bus){
        BLOCKS.register(bus);
    }
}
