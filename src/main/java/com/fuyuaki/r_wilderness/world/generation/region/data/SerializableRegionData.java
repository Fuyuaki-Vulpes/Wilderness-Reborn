package com.fuyuaki.r_wilderness.world.generation.region.data;

import com.fuyuaki.r_wilderness.world.generation.features.RegionNoisesRecord;
import com.fuyuaki.r_wilderness.world.generation.features.hydrology.RegionRivers;
import com.fuyuaki.r_wilderness.world.generation.features.hydrology.RegionWaterBodies;
import com.fuyuaki.r_wilderness.world.generation.features.plates.RegionPlates;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Optional;

public class SerializableRegionData {

    private final RegionPos position;
    private final RegionPlates plates;
    private final RegionRivers rivers;
    private final RegionWaterBodies waters;
    private RegionPhase status;

    public SerializableRegionData(
            RegionPos position,
            RegionPlates plates,
            RegionRivers rivers,
            RegionWaterBodies waters
    ){
        this.position = position;
        this.plates = plates;
        this.rivers = rivers;
        this.waters = waters;
    }
    public SerializableRegionData(
            RegionPhase status,
            RegionPos position,
            RegionPlates plates,
            RegionRivers rivers,
            RegionWaterBodies waters
    ){
        this.status = status;
        this.position = position;
        this.plates = plates;
        this.rivers = rivers;
        this.waters = waters;
    }

    public static SerializableRegionData makeNew(RegionPos pos, long seed, RegionNoisesRecord record) {
        return new SerializableRegionData(
                pos,
                new RegionPlates(pos, seed,record),
                new RegionRivers(record),
                new RegionWaterBodies(record)
        );
    }

    public static SerializableRegionData read(DataInputStream inputStream,RegionNoisesRecord record) throws IOException  {
        CompoundTag tag = NbtIo.read(inputStream);
        Optional<String> status = tag.getString("status");
        Optional<Integer> x = tag.getInt("pos_x");
        Optional<Integer> z = tag.getInt("pos_z");
        RegionPlates plates = RegionPlates.readFrom(tag,record);
        RegionRivers rivers = RegionRivers.readFrom(tag,record);
        RegionWaterBodies waters = RegionWaterBodies.readFrom(tag,record);
        return new SerializableRegionData(RegionPhase.fromString(status.orElse("empty")),new RegionPos(x.get(),z.get()),plates,rivers,waters);
    }

    public CompoundTag toNBT() throws Throwable{
        CompoundTag tag = new CompoundTag();
        tag.putString("status", this.status.getSerializedName());
        tag.putInt("pos_x", position.x());
        tag.putInt("pos_z", position.z());
        this.plates.put(tag);
        this.rivers.put(tag);
        this.waters.put(tag);
        return tag;
    }

    public void setStatus(RegionPhase status) {
        this.status = status;
    }

    public RegionPhase status() {
        return status;
    }

    public RegionPos position() {
        return position;
    }

    public RegionPlates plates() {
        return plates;
    }

    public RegionRivers rivers() {return rivers;}

    public RegionWaterBodies waters() {return waters;}

}
