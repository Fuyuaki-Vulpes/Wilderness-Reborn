package com.fuyuaki.r_wilderness.world.level.block;

import com.fuyuaki.r_wilderness.world.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public class ModBlocks{

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

    //Blocks

    //Stones

    public static final DeferredBlock<Block> CHALK = registerBlock("chalk", BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_CONCRETE));
    public static final DeferredBlock<Block> LIMESTONE = registerBlock("limestone", BlockBehaviour.Properties.ofFullCopy(Blocks.STONE));
    public static final DeferredBlock<Block> MUD_STONE = registerBlock("mud_stone", BlockBehaviour.Properties.ofFullCopy(Blocks.MUD_BRICKS));

    //Soils

    public static final DeferredBlock<Block> CHALKY_SOIL = registerBlockWithItem("chalky_soil",
            ModSoilBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_CONCRETE_POWDER));
    public static final DeferredBlock<Block> CHALKY_FARMLAND = registerBlockWithItem("chalky_farmland",
            ModFarmBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_CONCRETE_POWDER)
    );
    public static final DeferredBlock<Block> CLAY_SOIL = registerBlockWithItem("clay_soil",
            ModSoilBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CLAY));
    public static final DeferredBlock<Block> CLAY_FARMLAND = registerBlockWithItem("clay_farmland",
            ModFarmBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CLAY)
    );
    public static final DeferredBlock<Block> PEAT = registerBlockWithItem("peat",
            ModSoilBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK));
    public static final DeferredBlock<Block> PEAT_FARMLAND = registerBlockWithItem("peat_farmland",
            ModFarmBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK)
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



    //Methods



    private static  <B extends Block> DeferredBlock<B> register(String name, Function<BlockBehaviour.Properties, B> factory, BlockBehaviour.Properties properties) {

        return BLOCKS.registerBlock(name,factory,properties);
    }

    private static <B extends Block> DeferredBlock<B> registerBlockWithItem(String name, Function<BlockBehaviour.Properties, B> factory, BlockBehaviour.Properties properties) {
        DeferredBlock<B> BLOCK = register(name,factory,properties);
        ModItems.registerItem(name, (p) -> new BlockItem(BLOCK.get(), p),new Item.Properties());
        return BLOCK;
    }
    private static <B extends Block> DeferredBlock<?> registerBlockWithItemCustomProperties(String name, Function<BlockBehaviour.Properties, B> factory, BlockBehaviour.Properties properties, Item.Properties iProperties) {
        DeferredBlock<B> BLOCK = register(name,factory,properties);
        ModItems.registerItem(name, (p) -> new BlockItem(BLOCK.get(), p), iProperties);
        return BLOCK;
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
                        .isValidSpawn(ModBlocks::never)
                        .isRedstoneConductor(ModBlocks::never)
                        .isSuffocating(ModBlocks::never)
                        .isViewBlocking(ModBlocks::never)
        );
    }

    private static BlockBehaviour.Properties leavesProperties(SoundType sound) {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .strength(0.2F)
                .randomTicks()
                .sound(sound)
                .noOcclusion()
                .isValidSpawn(ModBlocks::ocelotOrParrot)
                .isSuffocating(ModBlocks::never)
                .isViewBlocking(ModBlocks::never)
                .ignitedByLava()
                .pushReaction(PushReaction.DESTROY)
                .isRedstoneConductor(ModBlocks::never);
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
                .isRedstoneConductor(ModBlocks::never)
                .isSuffocating(NOT_EXTENDED_PISTON)
                .isViewBlocking(NOT_EXTENDED_PISTON)
                .pushReaction(PushReaction.BLOCK);
    }
    private static final BlockBehaviour.StatePredicate NOT_CLOSED_SHULKER = (p_304352_, p_304353_, p_304354_) -> p_304353_.getBlockEntity(p_304354_) instanceof ShulkerBoxBlockEntity shulkerboxblockentity
            ? shulkerboxblockentity.isClosed()
            : true;
    private static final BlockBehaviour.StatePredicate NOT_EXTENDED_PISTON = (p_152641_, p_152642_, p_152643_) -> !p_152641_.getValue(PistonBaseBlock.EXTENDED);


    private static BlockBehaviour.Properties buttonProperties() {
        return BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY);
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
