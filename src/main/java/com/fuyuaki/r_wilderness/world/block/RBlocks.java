package com.fuyuaki.r_wilderness.world.block;

import com.fuyuaki.r_wilderness.data.worldgen.tree.ModTreeGrower;
import com.fuyuaki.r_wilderness.world.item.RItems;
import com.fuyuaki.r_wilderness.world.block.soils.ModFarmBlock;
import com.fuyuaki.r_wilderness.world.block.soils.ModSoilBlock;
import com.fuyuaki.r_wilderness.world.block.woods.*;
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
import java.util.function.ToIntFunction;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;
import static net.minecraft.world.item.Items.registerBlock;

public class RBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

    //Blocks

    //Stones

    public static final DeferredBlock<Block> CHALK = registerBlock("chalk", BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_CONCRETE));
    public static final DeferredBlock<Block> LIMESTONE = registerBlock("limestone", BlockBehaviour.Properties.ofFullCopy(Blocks.STONE));
    public static final DeferredBlock<Block> MUD_STONE = registerBlock("mud_stone", BlockBehaviour.Properties.ofFullCopy(Blocks.MUD_BRICKS));

    public static final DeferredBlock<Block> SCHINITE = registerBlock("schinite",
            BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE)
                    .strength(4.5F, 7.0F)
    );
    public static final DeferredBlock<Block> SCHINITE_SLAB = registerBlockWithItem("schinite_slab",
            SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICK_SLAB)
                    .strength(4.5F, 7.0F)
    );
    public static final DeferredBlock<Block> SCHINITE_STAIRS = registerBlockWithItem("schinite_stairs",
            properties -> new StairBlock(Blocks.DEEPSLATE_BRICK_STAIRS.defaultBlockState(),properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICK_STAIRS)
                    .strength(4.5F, 7.0F)
    );
    public static final DeferredBlock<Block> SCHINITE_WALL = registerBlockWithItem("schinite_wall",
            WallBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICK_SLAB)
                    .strength(4.5F, 7.0F)
    );
    public static final DeferredBlock<Block> MAGNEISS = registerBlock("magneiss",
            BlockBehaviour.Properties.ofFullCopy(Blocks.BASALT)
                    .strength(5.5F, 5.0F)
    );
    public static final DeferredBlock<Block> MAGNEISS_SLAB = registerBlockWithItem("magneiss_slab",
            SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.BASALT)
                    .strength(5.5F, 5.0F)
    );
    public static final DeferredBlock<Block> MAGNEISS_STAIRS = registerBlockWithItem("magneiss_stairs",
            properties -> new StairBlock(Blocks.BASALT.defaultBlockState(),properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.BASALT)
                    .strength(5.5F, 5.0F)
    );
    public static final DeferredBlock<Block> MAGNEISS_WALL = registerBlockWithItem("magneiss_wall",
            WallBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.BASALT)
                    .strength(5.5F, 5.0F)
    );
    public static final DeferredBlock<Block> MALATITE = registerBlock("malatite",
            BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)
                    .strength(7.0F, 4.0F)
    );
    public static final DeferredBlock<Block> MALATITE_SLAB = registerBlockWithItem("malatite_slab",
            SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)
                    .strength(7.0F, 4.0F)
    );
    public static final DeferredBlock<Block> MALATITE_STAIRS = registerBlockWithItem("malatite_stairs",
            properties -> new StairBlock(Blocks.OBSIDIAN.defaultBlockState(),properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)
                    .strength(7.0F, 4.0F)
    );
    public static final DeferredBlock<Block> MALATITE_WALL = registerBlockWithItem("malatite_wall",
            WallBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)
                    .strength(7.0F, 4.0F)
    );
    public static final DeferredBlock<Block> COBBLED_SCHINITE = registerBlock("cobbled_schinite",
            BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE)
                    .strength(4.5F, 7.0F)
    );
    public static final DeferredBlock<Block> COBBLED_SCHINITE_SLAB = registerBlockWithItem("cobbled_schinite_slab",
            SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICK_SLAB)
                    .strength(4.5F, 7.0F)
    );
    public static final DeferredBlock<Block> COBBLED_SCHINITE_STAIRS = registerBlockWithItem("cobbled_schinite_stairs",
            properties -> new StairBlock(Blocks.DEEPSLATE_BRICK_STAIRS.defaultBlockState(),properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICK_STAIRS)
                    .strength(4.5F, 7.0F)
    );
    public static final DeferredBlock<Block> COBBLED_SCHINITE_WALL = registerBlockWithItem("cobbled_schinite_wall",
            WallBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICK_SLAB)
                    .strength(4.5F, 7.0F)
    );
    public static final DeferredBlock<Block> COBBLED_MAGNEISS = registerBlock("cobbled_magneiss",
            BlockBehaviour.Properties.ofFullCopy(Blocks.BASALT)
                    .strength(5.5F, 5.0F)
    );
    public static final DeferredBlock<Block> COBBLED_MAGNEISS_SLAB = registerBlockWithItem("cobbled_magneiss_slab",
            SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.BASALT)
                    .strength(5.5F, 5.0F)
    );
    public static final DeferredBlock<Block> COBBLED_MAGNEISS_STAIRS = registerBlockWithItem("cobbled_magneiss_stairs",
            properties -> new StairBlock(Blocks.BASALT.defaultBlockState(),properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.BASALT)
                    .strength(5.5F, 5.0F)
    );
    public static final DeferredBlock<Block> COBBLED_MAGNEISS_WALL = registerBlockWithItem("cobbled_magneiss_wall",
            WallBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.BASALT)
                    .strength(5.5F, 5.0F)
    );
    public static final DeferredBlock<Block> COBBLED_MALATITE = registerBlock("cobbled_malatite",
            BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)
                    .strength(7.0F, 4.0F)
    );
    public static final DeferredBlock<Block> COBBLED_MALATITE_SLAB = registerBlockWithItem("cobbled_malatite_slab",
            SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)
                    .strength(7.0F, 4.0F)
    );
    public static final DeferredBlock<Block> COBBLED_MALATITE_STAIRS = registerBlockWithItem("cobbled_malatite_stairs",
            properties -> new StairBlock(Blocks.OBSIDIAN.defaultBlockState(),properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)
                    .strength(7.0F, 4.0F)
    );
    public static final DeferredBlock<Block> COBBLED_MALATITE_WALL = registerBlockWithItem("cobbled_malatite_wall",
            WallBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)
                    .strength(7.0F, 4.0F)
    );

    //Soils

    public static final DeferredBlock<Block> CHALKY_SOIL = registerBlockWithItem("chalky_soil",
            ModSoilBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL)
    );
    public static final DeferredBlock<Block> CHALKY_FARMLAND = registerBlockWithItem("chalky_farmland",
            ModFarmBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL)
    );
    public static final DeferredBlock<Block> CLAY_SOIL = registerBlockWithItem("clay_soil",
            ModSoilBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CLAY));
    public static final DeferredBlock<Block> CLAY_FARMLAND = registerBlockWithItem("clay_farmland",
            ModFarmBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CLAY)
    );
    public static final DeferredBlock<Block> PEAT = registerBlockWithItem("peat",
            ModSoilBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT));
    public static final DeferredBlock<Block> PEAT_FARMLAND = registerBlockWithItem("peat_farmland",
            ModFarmBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT)
    );
    public static final DeferredBlock<Block> SANDY_SOIL = registerBlockWithItem("sandy_soil",
            ModSoilBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.SAND));
    public static final DeferredBlock<Block> SANDY_FARMLAND = registerBlockWithItem("sandy_farmland",
            ModFarmBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.SAND)
    );
    public static final DeferredBlock<Block> SILT = registerBlockWithItem("silt",
            ModSoilBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.MUD));
    public static final DeferredBlock<Block> SILT_FARMLAND = registerBlockWithItem("silt_farmland",
            ModFarmBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.MUD)
    );

    //Trees & Wood

    public static final DeferredBlock<Block> ALPINE_LOG = registerBlockWithItem("alpine_log",  RotatedPillarBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG));
    public static final DeferredBlock<Block> STRIPPED_ALPINE_LOG = registerBlockWithItem("stripped_alpine_log",  RotatedPillarBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG));
    public static final DeferredBlock<Block> ALPINE_WOOD = registerBlockWithItem("alpine_wood",  RotatedPillarBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD));
    public static final DeferredBlock<Block> STRIPPED_ALPINE_WOOD = registerBlockWithItem("stripped_alpine_wood",  RotatedPillarBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD));

    public static final DeferredBlock<Block> ALPINE_PLANKS = registerBlockWithItem("alpine_planks",  Block::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS));
    public static final DeferredBlock<Block> ALPINE_LEAVES = registerBlockWithItem("alpine_leaves", (properties) -> {
        return new TintedParticleLeavesBlock(0.1F, properties);
    }, leavesProperties(SoundType.GRASS));

    public static final DeferredBlock<Block> ALPINE_SAPLING = registerBlockWithItem("alpine_sapling",
            properties -> new SaplingBlock(ModTreeGrower.ALPINE_TREE,properties), BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SAPLING));
    public static final DeferredBlock<Block> POTTED_ALPINE_SAPLING = registerBlockWithItem("potted_alpine_sapling",
            properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, ALPINE_SAPLING::value, properties),flowerPotProperties());
    public static final DeferredBlock<Block> ALPINE_STAIRS = registerBlockWithItem("alpine_stairs",
            properties -> new StairBlock(Blocks.OAK_STAIRS.defaultBlockState(),properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS));

    public static final DeferredBlock<Block> ALPINE_SLAB = registerBlockWithItem("alpine_slab",
            SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB));

    public static final DeferredBlock<Block> ALPINE_PRESSURE_PLATE = registerBlockWithItem("alpine_pressure_plate",
            properties -> new PressurePlateBlock(ModBlockSetTypes.ALPINE,properties), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE));
    public static final DeferredBlock<Block> ALPINE_BUTTON = registerBlockWithItem("alpine_button",
            properties -> new ButtonBlock(ModBlockSetTypes.ALPINE, 10,properties), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON));

    public static final DeferredBlock<Block> ALPINE_FENCE = registerBlockWithItem("alpine_fence",
            FenceBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE));
    public static final DeferredBlock<Block> ALPINE_FENCE_GATE = registerBlockWithItem("alpine_fence_gate",
            properties -> new FenceGateBlock(ModWoodTypes.ALPINE,properties), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE));

    public static final DeferredBlock<Block> ALPINE_DOOR = registerBlockWithItem("alpine_door",
            properties -> new DoorBlock(ModBlockSetTypes.ALPINE,properties), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR));
    public static final DeferredBlock<Block> ALPINE_TRAPDOOR = registerBlockWithItem("alpine_trapdoor",
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

    public static final DeferredBlock<Block> BELLFLOWER = registerBlockWithItem(
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
    public static final DeferredBlock<Block> MOSS_COMPANION = registerBlockWithItem(
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
    public static final DeferredBlock<Block> MOUNTAIN_HEATHER = registerBlockWithItem(
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
    public static final DeferredBlock<Block> SNOWBELL = registerBlockWithItem(
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

    private static <B extends Block> DeferredBlock<B> registerBlockWithItem(String name, Function<BlockBehaviour.Properties, B> factory, BlockBehaviour.Properties properties) {
        DeferredBlock<B> BLOCK = register(name,factory,properties);
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
        return registerBlockWithItem(name, Block::new, properties);
    }

    private static DeferredBlock<StairBlock> registerStair(String name, Block baseBlock) {
        return registerBlockWithItem(name, p_368009_ -> new StairBlock(baseBlock.defaultBlockState(), p_368009_), BlockBehaviour.Properties.ofFullCopy(baseBlock));
    }

    private static BlockBehaviour.Properties wallVariant(Block baseBlock, boolean overrideDescription) {
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
        return registerBlockWithItem(
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
        return registerBlockWithItem(
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
