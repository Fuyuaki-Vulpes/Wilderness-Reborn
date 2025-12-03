package com.fuyuaki.r_wilderness.world.generation.hydrology.river.old_backup;

import com.fuyuaki.r_wilderness.util.ModUtil;
import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.fuyuaki.r_wilderness.world.generation.terrain.TerrainParameters;
import com.fuyuaki.r_wilderness.world.level.levelgen.util.DistortionSpline;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RiverData extends SavedData {

    private static final double ENDPOINT_WIDTH = 128;
    private static final boolean DEBUGGING = false;
    private static final double SUB_DEBUG_WIDTH = 16;
    private static final double MIN_RIVER_SIZE = 2;
    private static final int RIVER_PART_SAMPLER_CHECK_ROUGH = 30;
    private static final int RIVER_PART_ZONE_SIZE = 32;
    private static final float CHANCE_TO_FORK = 0.03F;
    private static final double MIN_FORK_ENERGY = 1024;
    private static final double MIN_RIVER_BIOME_WIDTH = 12;
    private static final int MIN_FORK_LENGTH = 4;
    public final int index;
    public static final String FORKING_RIVER = "Forking River Data {}, {}, at {}, {}";
    public final int x;
    public final int z;
    public final double startingEnergy;
    public final WaterBasinGenerator.WaterRegionPos region;
    private RiverData parent;
    public WaterBasinGenerator.WaterRegionPos parentRegion;
    public final WaterBasinGenerator basin;
    private final TerrainParameters terrain;
    public double energy;
    public boolean forked = false;
    public RiverStatus riverStatus;
    public RiverType riverType;
    private List<RiverSection> parts = new ArrayList<>();
    private List<RiverSection> nodes = new ArrayList<>();
    private List<RiverData> branches = new ArrayList<>();

    public RiverData(RandomSource source, WaterBasinGenerator.WaterRegionPos pos,  WaterBasinGenerator basin, TerrainParameters parameters) {
        this.index = 0;
        this.riverStatus = RiverStatus.STARTED;
        this.riverType = RiverType.MAIN;
        this.basin = basin;
        this.region = pos;
        this.x = source.nextInt(0,127);
        this.z = source.nextInt(0,127);

        if (RWildernessMod.LOGGER.isDebugEnabled()) {
            RWildernessMod.LOGGER.debug("Began Generating River Data {}, {}.", this.getCanonicalX(), this.getCanonicalZ());
        }
        this.startingEnergy = source.nextIntBetweenInclusive(2048,8192);
        this.terrain = parameters;
        this.energy = this.startingEnergy;
        int startX = blockFromRiverSectionCoordCenter(this.getCanonicalX());
        int startZ = blockFromRiverSectionCoordCenter(this.getCanonicalZ());
        Vec2 flow = calculateFlow(pos, startX, startZ, source);
        double size = sizeFrom(this.energy);
        makeRiverStart(startX, startZ, flow, size);
        this.extend(source);
    }

    public RiverData(int index, RiverData parent,RiverSection section, double energy, WaterBasinGenerator.WaterRegionPos pos,Vec2 direction, WaterBasinGenerator basin, TerrainParameters parameters, RandomSource source) {
        this.index = index;
        this.riverStatus = RiverStatus.STARTED;
        this.riverType = RiverType.FORK;
        this.basin = basin;
        this.region = new WaterBasinGenerator.WaterRegionPos(
                WaterBasinGenerator.WaterRegionPos.blockToWaterRegionCoord(section.getPos().x),
                WaterBasinGenerator.WaterRegionPos.blockToWaterRegionCoord(section.getPos().z)
        );
        this.parentRegion = pos;
        this.parent = parent;
        this.x = blockToRiverSectionCoord(section.getPos().x);
        this.z = blockToRiverSectionCoord(section.getPos().z);
        this.terrain = parameters;
        this.startingEnergy = -1;
        this.energy = energy;
        this.extendForked(source,section, direction);
    }

    private void extendForked(RandomSource source, RiverSection startingSection,Vec2 direction) {
        this.parts.add(
                new RiverSection(startingSection.getBlockLikeRiverPos(),startingSection.y,startingSection.depth,direction,startingSection.size)
        );
        while (this.energy > 0) {
            RiverSection last = this.parts.getLast();
            TerrainParameters.Sampled sampled = this.terrain.samplerAt(last.getBlockLikeRiverPos().intX(), last.getBlockLikeRiverPos().intZ());
            if (sampled.continentalness() < -0.15) {
                markDestroyed();
                return;
            }
            double y = this.terrain.yLevelAt(last.getBlockLikeRiverPos().intX(), last.getBlockLikeRiverPos().intZ(), sampled.terrainTypeA() > 0.5);
            if (y < WildernessConstants.SEA_LEVEL){
                markDestroyed();
                return;
            }
            double energySpending = energyFromSampled(sampled);
            double size = Math.max(sizeFrom(this.energy), MIN_RIVER_SIZE);
            double sampleLength = sampleLength(sampled);
            this.energy -= energySpending * ((Math.min(size,60) / 60) + 0.5) / 1.5;

            Vec2 flow = calculateFlow(last.flow, source).scale(0.5F).normalized().scale(1 + (float) ((y - last.y) / 32));


            BlockLikeRiverPos offset = new BlockLikeRiverPos((flow.normalized().x * sampleLength), (flow.normalized().y * sampleLength));
            BlockLikeRiverPos pointPos = last.getBlockLikeRiverPos().offset(offset);

            for (int i = 0; i < this.parts.size(); i++) {
                if (y < this.parts.get(i).getY()) {
                    RiverSection section = this.parts.get(i);
                    this.energy -= (section.y - y) * 0.5;
                    this.parts.set(i, new RiverSection(section.getBlockLikeRiverPos(),y, section.depth(), section.getFlow(), section.size));
                }
            }



            double range = blockToRiverSectionCoord((int) (size + 1)) + 1;
            double gap = size / range;
            List<RiverData> rData= new ArrayList<>();
            for (double xR = -range; xR < range; xR++) {
                for (double zR = -range; zR < range; zR++) {
                    rData.addAll(basin.riverAllBranchesAt((pointPos.offset(gap * xR, gap * zR)).toChunkPos()));

                }
            }
            if (!rData.isEmpty()) {
                for (RiverData data : rData) {
                    if (data.getCanonicalX() != this.getCanonicalX() && data.getCanonicalZ() != this.getCanonicalZ()) {
                        markDestroyed();
                        return;
                    }

                }
            }

            RiverSection section = new RiverSection(
                    pointPos,
                    y,
                    calculateDepth(flow, size),
                    flow,
                    size
            );
            this.parts.add(section);

            if (source.nextFloat() < CHANCE_TO_FORK && this.energy > MIN_FORK_ENERGY && this.parts.size() > MIN_FORK_LENGTH){
                double e = this.energy;

                this.forked = true;
                this.nodes = this.parts;
                double lastY = this.parts.getLast().y;
                this.parts = this.postProcessAll();
                int mainPartsSize = this.parts.size();

                float ratio = Math.max(source.nextFloat(),0.25F) * 0.75F;
                if (RWildernessMod.LOGGER.isDebugEnabled()) {
                    RWildernessMod.LOGGER.debug(FORKING_RIVER,
                            this.parent.getCanonicalX(), this.parent.getCanonicalZ(),
                            section.getBlockLikeRiverPos().x,section.getBlockLikeRiverPos().z);
                }
                this.fork(ratio,e, section,source);

                int branchCount = 0;

                for (RiverData data : this.branches){
                    if (!data.markedForRemoval()) {
                        Optional<RiverSection> part = data.parts.stream().findFirst();
                        if (part.isPresent()) {
                            double y1 = part.get().y;
                            if (y1 < lastY) {
                                for (int i = 0; i < mainPartsSize; i++) {
                                    if (y1 < this.parts.get(i).getY()) {
                                        RiverSection s = this.parts.get(i);
                                        this.energy -= (s.y - y1);
                                        this.parts.set(i, new RiverSection(s.getBlockLikeRiverPos(), y1, s.depth(), s.getFlow(), s.size));
                                    }
                                }
                            }
                            branchCount++;
                        }
                        this.nodes.addAll(data.nodes);
                        this.parts.addAll(data.parts);
                    }
                }
                if (branchCount == 0){
                    this.markDestroyed();
                    return;
                }
                this.energy = 0;
            }
        }
        if (!forked){
            this.nodes = this.parts;
            this.parts = this.postProcessAll();
        }
    }

    private static double calculateSampleCount(RiverSection first, RiverSection second) {
        double sizer = first.size;
        double dist = first.distanceTo(second.getBlockLikeRiverPos());
        return dist / sizer * 3;
    }

    private static double calculateSampleCountRough(RiverSection first, RiverSection second) {
        double sizer = Math.pow(first.size,1.5);
        double dist = first.distanceTo(second.getBlockLikeRiverPos());
        return Math.min(dist / sizer, dist / 12);
    }

    private void makeRiverStart(int startX, int startZ, Vec2 flow, double size) {
        this.parts.add(
                new RiverSection(
                        QuartPos.fromBlock(startX),
                        Mth.floor(this.terrain.yLevelAtWithCache(startX, startZ)),
                        QuartPos.fromBlock(startZ),
                        calculateDepth(flow, size),
                        flow,
                        size
                )
        );
    }

    private int calculateDepth(Vec2 flow, double size) {
        double flowSize = flow.length();
        double sizeModifier = 5 - (2*size);
        double depth = Math.sqrt(Math.abs((3*flowSize) * sizeModifier)) + 3;
        return Mth.floor(depth);
    }

    private Vec2 calculateFlow(WaterBasinGenerator.WaterRegionPos pos, int startX, int startZ, RandomSource source) {
        int posX = pos.getCenterX();
        int posZ = pos.getCenterZ();
        int xDir = posX - startX;
        int zDir = posZ - startZ;
        float xFlow = source.nextFloat() * 2 - 1;
        float zFlow = source.nextFloat() * 2 - 1;
        Vec2 posDir = new Vec2(xDir,zDir).normalized().scale(source.nextFloat() * 0.5F + 0.25F);

        return new Vec2(xFlow,zFlow).add(posDir);
    }

    private static Vec2 calculateFlow(Vec2 flow,  RandomSource source) {
        int degreeRange = 12;
        int rot = source.nextIntBetweenInclusive(-degreeRange,degreeRange);
        Vec3 flow3 = new Vec3(flow.x,0,flow.y).yRot(rot);

        return new Vec2((float) flow3.x, (float) flow3.z);
    }
    private static Vec2 rotateFlow(Vec2 flow, int angle) {
        Vec3 flow3 = new Vec3(flow.x,0,flow.y).yRot(angle);

        return new Vec2((float) flow3.x, (float) flow3.z);
    }

    private void extend(RandomSource source) {

        while (this.energy > 0) {
            RiverSection last = this.parts.getLast();
            TerrainParameters.Sampled sampled = this.terrain.samplerAt(last.getBlockLikeRiverPos().intX(), last.getBlockLikeRiverPos().intZ());
            if (sampled.continentalness() < -0.15) {
                markDestroyed();
                return;
            }
            double y = this.terrain.yLevelAt(last.getBlockLikeRiverPos().intX(), last.getBlockLikeRiverPos().intZ(), sampled.terrainTypeA() > 0.5);
            if (y < WildernessConstants.SEA_LEVEL){
                markDestroyed();
                return;
            }
            double energySpending = energyFromSampled(sampled);
            double sampleLength = sampleLength(sampled);
            this.energy -= energySpending;

            Vec2 flow = calculateFlow(last.flow, source).scale(0.5F).normalized().scale(1 + (float) ((y - last.y) / 32));

            BlockLikeRiverPos offset = new BlockLikeRiverPos((flow.normalized().x * sampleLength), (flow.normalized().y * sampleLength));

            BlockLikeRiverPos pointPos = last.getBlockLikeRiverPos().offset(offset);
            for (int i = 0; i < this.parts.size(); i++) {
                if (y < this.parts.getLast().getY()) {
                    RiverSection section = this.parts.get(i);
                    this.energy -= (section.y - y) * 0.5;
                    this.parts.set(i, new RiverSection(section.getBlockLikeRiverPos(),y, section.depth(), section.getFlow(), section.size));
                }
            }

            double size = Math.max(sizeFrom(this.energy), MIN_RIVER_SIZE);

            double range = blockToRiverSectionCoord((int) (size + 1)) + 1;
            double gap = size / range;
            List<RiverData> rData= new ArrayList<>();
            for (double xR = -range; xR < range; xR++) {
                for (double zR = -range; zR < range; zR++) {
                    rData.addAll(basin.riverAllBranchesAt((pointPos.offset(gap * xR, gap * zR)).toChunkPos()));
                }
            }
            if (!rData.isEmpty()) {
                for (RiverData data : rData) {
                        if (data.getCanonicalX() != this.getCanonicalX() && data.getCanonicalZ() != this.getCanonicalZ()) {
                            markDestroyed();
                            return;
                        }

                }
            }


            RiverSection section = new RiverSection(
                    pointPos,
                    y,
                    calculateDepth(flow, size),
                    flow,
                    size
            );
            this.parts.add(section);

            if (source.nextFloat() < CHANCE_TO_FORK && this.energy > MIN_FORK_ENERGY && this.parts.size() > MIN_FORK_LENGTH){
                double e = this.energy;
                this.forked = true;
                this.nodes = this.parts;
                double lastY = this.parts.getLast().y;
                this.parts = this.postProcessAll();
                int mainPartsSize = this.parts.size();

                float ratio = Math.max(source.nextFloat(),0.25F) * 0.75F;
                if (RWildernessMod.LOGGER.isDebugEnabled()) {
                    RWildernessMod.LOGGER.debug(FORKING_RIVER,
                            this.getCanonicalX(), this.getCanonicalZ(),
                            section.getBlockLikeRiverPos().x,section.getBlockLikeRiverPos().z);
                }

                this.fork(ratio,e, section,source);
                int branchCount = 0;

                for (RiverData data : this.branches){
                    if (!data.markedForRemoval()) {
                        Optional<RiverSection> part = data.parts.stream().findFirst();
                        if (part.isPresent()) {
                            double y1 = part.get().y;
                            if (y1 < lastY) {
                                for (int i = 0; i < mainPartsSize; i++) {
                                    if (y1 < this.parts.get(i).getY()) {
                                        RiverSection s = this.parts.get(i);
                                        this.energy -= (s.y - y1);
                                        this.parts.set(i, new RiverSection(s.getBlockLikeRiverPos(), y1, s.depth(), s.getFlow(), s.size));
                                    }
                                }
                            }
                            branchCount++;
                        }
                        this.nodes.addAll(data.nodes);
                        this.parts.addAll(data.parts);
                    }
                }

                if (branchCount == 0){
                    this.markDestroyed();
                    return;
                }
                this.energy = 0;
            }

        }

        if (!forked){
            this.nodes = this.parts;
            this.parts = this.postProcessAll();
        }

        if (this.riverStatus != RiverStatus.MARKED_FOR_REMOVAL) {
            if (RWildernessMod.LOGGER.isDebugEnabled()) {
                RWildernessMod.LOGGER.debug("Generated River Data {}, {}. Parts = {}.", this.getCanonicalX(), this.getCanonicalZ(),this.parts.size());
            }
            this.riverStatus = RiverStatus.COMPLETE;
        }
    }

    private List<RiverSection> postProcessAll() {
        List<RiverSection> newParts = new ArrayList<>();
        for (int p = 0; p < this.nodes.size(); p++) {

            RiverSection previous2 = null;
            RiverSection previous = null;
            RiverSection target = null;
            RiverSection next = null;
            RiverSection section = this.nodes.get(p);
            target = section;
            if (p > 1) {
                previous2 = this.nodes.get(p - 2);
                previous = this.nodes.get(p - 1);

            } else {
                previous2 = this.nodes.getFirst();
                previous = this.nodes.getFirst();

            }
            if (p == this.nodes.size() - 1) {
                next = section;
            } else {
                next = this.nodes.get(p + 1);
            }


            double pX = previous2.getBlockLikeRiverPos().x;
            double pZ = previous2.getBlockLikeRiverPos().z;

            double fX = previous.getBlockLikeRiverPos().x;
            double fZ = previous.getBlockLikeRiverPos().z;

            double sX = target.getBlockLikeRiverPos().x;
            double sZ = target.getBlockLikeRiverPos().z;

            double nX = next.getBlockLikeRiverPos().x;
            double nZ = next.getBlockLikeRiverPos().z;

            double deltaSize = calculateSampleCount(previous, target);

            for (double d = 0; d < deltaSize; d ++) {


                double delta = Math.min(d / deltaSize, 1);

                double sizeAt = Mth.lerp(delta, previous.getSize(), target.getSize());
                double tX = ModUtil.catmullrom(delta, pX, fX, sX, nX);
                double tZ = ModUtil.catmullrom(delta, pZ, fZ, sZ, nZ);

                Vec2 flowAt = new Vec2((float) Mth.lerp(delta, previous.getFlow().x, target.getFlow().x), (float) Mth.lerp(delta, previous.getFlow().y, target.getFlow().y));
                TerrainParameters.Sampled sampled = this.terrain.samplerAt((int) tX, (int) tZ);

                double yLocal = this.terrain.yLevelAt((int) tX, (int) tZ, sampled.terrainTypeA() > 0.5);
                double yAt = Math.min(Mth.lerp(delta, previous.getY(), target.getY()), yLocal);
                int depth = (int) Mth.lerp(delta, previous.getDepth(), target.getDepth());

                newParts.add(new RiverSection(new BlockLikeRiverPos(tX, tZ), yAt, depth, flowAt, sizeAt));

            }

        }
        return newParts;
    }

    private void fork(float ratio, double energy, RiverSection section, RandomSource source) {

        double e1 = energy * ratio;
        double e2 = energy * (1-ratio);
        Vec2 flow = section.flow;
        int degreeRange = 12;
        int rot1 = source.nextIntBetweenInclusive(-degreeRange,0);
        int rot2 = source.nextIntBetweenInclusive(0,degreeRange);
        Vec2 flow1 = rotateFlow(flow,rot1);
        Vec2 flow2 = rotateFlow(flow,rot2);

        RiverData fork1 = new RiverData(1,this,section,e1,this.region,flow1,basin,this.terrain,source);
        RiverData fork2 = new RiverData(2,this,section,e2,this.region,flow2,basin,this.terrain,source);

        this.branches.add(fork1);
        this.branches.add(fork2);
    }

    private void markDestroyed() {
        this.riverStatus = RiverStatus.MARKED_FOR_REMOVAL;
        this.energy = 0;
        if(riverType == RiverType.MAIN){
            if (RWildernessMod.LOGGER.isDebugEnabled()) {
                RWildernessMod.LOGGER.debug("Cancelled River Generation at {}, {} due to poor placement.", this.x, this.z);
            }
        }
    }

    private double sizeFrom(double energy) {
        double e = energy / 8192;
        return Math.max(
                Math.pow(e,1.6) * 128,
                MIN_RIVER_SIZE);
    }

    private double sampleLength(TerrainParameters.Sampled sampled) {
        double energyScaled = Math.clamp(this.energy / 512,1,4);

        double tectonicTerrain = Math.clamp(sampled.tectonicActivity(),0,1);
        double plateauMap = sampled.highlandsMap();

        boolean mountainous = sampled.terrainTypeB() > 0.5;

        double l = 128;
        if (mountainous && tectonicTerrain > 0.0){
            l *= 0.5 - tectonicTerrain * 0.25;
        }
        if (plateauMap > 0.15){
            l *= 0.75;
        }
        return l * energyScaled;
    }

    private double energyFromSampled(TerrainParameters.Sampled sampled) {
        double tectonicTerrain = sampled.tectonicActivity();
        double plateauMap = Math.clamp(sampled.highlandsMap(),0,1);

        boolean mountainous = sampled.terrainTypeB() > 0.5;

        double e = 8;
        if (tectonicTerrain > 0.0){
            if (mountainous) {
                e += (32 * tectonicTerrain) + 16;
            }
            else {
                e += (12 * tectonicTerrain) + 12;

            }
        }
        if (plateauMap > 0.15){
            e += (plateauMap * 12);
        }
        if (forked){
            e *= 2;
        }
        return e;
    }

    private int getCanonicalX() {
        return this.x + region.getRiverX();
    }

    private int getCanonicalZ() {
        return this.z + region.getRiverZ();
    }

    public static BlockState debugBlockState(double influence) {
        if (influence > 1.0){
            return Blocks.RED_WOOL.defaultBlockState();
        }
        return Blocks.PINK_WOOL.defaultBlockState();
    }

    public static BlockState calculateAt(double influence,int thisY,int yLevel, double riverY, int riverDepth, BlockState defaultState) {
        DistortionSpline spline = new DistortionSpline(
                new DistortionSpline.Spline()
                        .addPoint(-1,yLevel)
                        .addPoint(0.1,yLevel)
                .addPoint(0.85,riverY + 2)
                .addPoint(1,riverY)
                .addPoint(1.3, riverY - ((double) riverDepth / 2))
                .addPoint(1.65,riverY - riverDepth)
                .addPoint(2.5,riverY - riverDepth)
                ,yLevel,riverY - riverDepth);
        double heightAt = spline.at(influence);
        if ( Math.floor(heightAt) > thisY){
            return defaultState;
        }
        if (thisY < Math.floor(riverY)){
            return Blocks.WATER.defaultBlockState();
        }
        return Blocks.AIR.defaultBlockState();

    }


    public boolean insideRiver(int xPos, int zPos){
        double x = (xPos - blockFromRiverSectionCoordCenter(this.getCanonicalX()));
        double z = (zPos - blockFromRiverSectionCoordCenter(this.getCanonicalX()));
        boolean insideCircle = Math.pow(x,2) + Math.pow(z,2) <= Math.pow(ENDPOINT_WIDTH,2);

        return insideCircle || insideRiverNode(xPos,zPos);

    }

    private boolean insideRiverNode(int xPos, int zPos) {
        int samplerGap = 2;
        for (int p = 0; p < this.parts.size(); p += samplerGap) {

            RiverSection s = this.parts.get(Math.min(p, this.parts.size() - 1));
            if (s == null){
                samplerGap = 1;
                continue;
            }
            if (s.size <= 32){
                if (s.size <= 16){
                    if (s.size <= 8){
                        if (s.size <= 4){
                            samplerGap = 32;
                        }else {
                            samplerGap = 16;
                        }
                    }else {
                        samplerGap = 8;
                    }
                }else {
                    samplerGap = 4;
                }
            }
            if (getInfluenceNode(xPos, zPos, s) > 0) {
                return true;
            }
        }

        return false;
    }


    public static int blockToRiverSectionCoord(int pos) {
        return pos >> 6;
    }
    public static int blockFromRiverSectionCoord(int pos) {
        return pos << 6;
    }

    public static int blockFromRiverSectionCoordCenter(int pos) {
        return (pos << 6) + ((1 << 6 ) / 2);
    }

    public boolean markedForRemoval() {
        return this.riverStatus == RiverStatus.MARKED_FOR_REMOVAL;
    }


    private static double getInfluence(int x, int z, RiverSection section) {
        return 1 - (section.distanceTo(x, z) / (section.size));
    }

    private static double getInfluenceSized(int x, int z, RiverSection section) {
        return 1- (section.distanceTo(x, z) / (RIVER_PART_ZONE_SIZE + section.size)* 2);
    }


    private static double getInfluenceNode(int x, int z, RiverSection section) {
        return 1 - (section.distanceTo(x, z) / ((section.size + RIVER_PART_ZONE_SIZE) * 3));
    }

    public Pair<Double, RiverSection> influenceAt(int x, int z) {
        double i = 0;
        RiverSection section = null;
        for (RiverSection part : this.parts) {
            double sI = getInfluenceTotal(x, z, part);
            if (sI > i){
                i = sI;
                section = part;
            }
        }
        return Pair.of(i, section);
    }

    private static double getInfluenceTotal(int x, int z, RiverSection part) {
        return getInfluence(x, z, part) * 2;

    }

    public double influenceAtForBiome(int x, int z) {
        double i = 0;
        List<RiverSection> list = this.parts;
        list.removeIf(section -> section.size < MIN_RIVER_BIOME_WIDTH);
        int samplerGap = 1;
        for (int p = 0; p < list.size(); p+= samplerGap) {

            RiverSection s = list.get(Math.min(p,list.size()-1));

            if (s.size <= 32){
                if (s.size <= 16){
                    if (s.size <= 8){
                        if (s.size <= 4){
                            samplerGap = 16;
                        }else {
                            samplerGap = 8;
                        }
                    }else {
                        samplerGap = 6;
                    }
                }else {
                    samplerGap = 4;
                }
            }
            i = getInfluenceTotal(x, z, s);
        }
        return i;
    }


    public class RiverSection implements WaterBodyOld {

        private final RiverPosition pos;
        private double y;
        private int depth;
        private double size;
        private double tolerance = 0.0;
        private Vec2 flow;

        public RiverSection(int x, double y, int z, int depth, Vec2 flow, double size) {
            this.pos = new RiverPosition(x, z);
            this.y = y;
            this.depth = depth;
            this.size = size;
            this.flow = flow;
        }

        public RiverSection(BlockLikeRiverPos pos, double yLevel, int depth, Vec2 flow, double size){
            this(QuartPos.fromBlock(pos.intX()),yLevel,QuartPos.fromBlock(pos.intZ()),depth,flow,size);
        }


        public Vec2 getFlow() {
            return flow;
        }

        public double getFlowLength() {
            return flow.length();
        }

        public int getDepth() {
            return depth;
        }

        public double getSize() {
            return size;
        }

        public double getTolerance() {
            return tolerance;
        }

        public double getY() {
            return y;
        }

        public RiverPosition getPos() {
            return pos;
        }

        public BlockLikeRiverPos getBlockLikeRiverPos(){
            return new BlockLikeRiverPos(this.pos.blockX(),this.pos.blockZ());
        }

        public double postProcessPosition(BlockLikeRiverPos block,int yLevel){
            Vec3 pos = block.toVec3();
            Vec3 center = this.getBlockLikeRiverPos().toVec3();

            double dist = center.distanceTo(pos);

            double influence = 1 - (dist / this.size);
            if (influence > 0){
                double depthMultiplier = (Math.cos(influence * Math.PI) + 1)/2;
                double depth = this.depth * depthMultiplier;
                return this.y - depth;
            }
            double terrainDeformation = -Math.min(dist,0) / this.getFlowLength() * 5;
            double deformationPoint = (1+(Math.sin(terrainDeformation - 0.5) * Math.PI)) / 2;
            return Mth.lerp(deformationPoint,this.y,yLevel);
        }


        public double distanceTo(BlockLikeRiverPos block){
            Vec3 pos = block.toVec3();
            Vec3 center = this.getBlockLikeRiverPos().toVec3();
            return  center.distanceTo(pos);
        }

        public double distanceTo(int x, int z){
            Vec2 pos = new Vec2(x,z);
            Vec2 center = new Vec2((float) this.getBlockLikeRiverPos().x(), (float) this.getBlockLikeRiverPos().z());
            return Math.sqrt(center.distanceToSqr(pos));
        }

        @Override
        public BodyType bodyType() {
            return BodyType.RIVER;
        }

        @Override
        public int depth() {
            return this.depth;
        }

        @Override
        public int width() {
            return Mth.floor(this.size);
        }

        @Override
        public double deformation() {
            return tolerance;
        }

        @Override
        public Codec<? extends WaterBodyOld> codec() {
            return null;
        }

        public record RiverPosition(int x, int z){

            public static int fromRiverSectionCoord(int pos) {
                return pos << 4;
            }
            public static int toRiverSectionCoord(int pos) {
                return pos >> 4;
            }

            public double blockX(){
                return QuartPos.toBlock(x) + ((double) QuartPos.toBlock(1) / 2);
            }
            public double blockZ(){
                return QuartPos.toBlock(z) + ((double) QuartPos.toBlock(1) / 2);
            }

        }
    }
    public enum RiverStatus{
        COMPLETE,
        MARKED_FOR_REMOVAL,
        STARTED
    }
    public enum RiverType{
        MAIN,FORK
    }


    public record BlockLikeRiverPos(double x, double z){

        public int intX() {
            return (int) x;
        }

        public int intZ() {
            return (int) z;
        }

        public Vec2 toVec2(){
            return new Vec2((float) x, (float) z);
        }
        public Vec3 toVec3(){
            return new Vec3(x,0,z);
        }

        public static int toRiverSectionCoord(int pos) {
            return pos >> 6;
        }
        public static int fromRiverSectionCoord(int pos) {
            return pos << 6;
        }

        public BlockLikeRiverPos offset(double xOff, double zOff) {
            return new BlockLikeRiverPos(this.x + xOff,this.z+zOff);
        }

        public BlockLikeRiverPos offset(BlockLikeRiverPos offset) {
            return new BlockLikeRiverPos(this.x + offset.x,this.z+offset.z);
        }

        public ChunkPos toChunkPos() {
            return new ChunkPos(SectionPos.blockToSectionCoord(x),SectionPos.blockToSectionCoord(z));
        }
    }
}
