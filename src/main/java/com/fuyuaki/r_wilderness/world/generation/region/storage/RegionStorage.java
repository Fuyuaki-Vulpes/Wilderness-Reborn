package com.fuyuaki.r_wilderness.world.generation.region.storage;

import com.fuyuaki.r_wilderness.world.generation.features.RegionNoisesRecord;
import com.fuyuaki.r_wilderness.world.generation.region.RegionManager;
import com.fuyuaki.r_wilderness.world.generation.region.WorldRegion;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionPos;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.io.FastBufferedOutputStream;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.FastBufferedInputStream;
import org.slf4j.Logger;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public record RegionStorage(Path path) implements AutoCloseable {
    public static final Logger LOGGER = LogUtils.getLogger();

    public void write(WorldRegion region) throws Throwable {
        try (DataOutputStream outputStream = outputStream(region)) {

            NbtIo.write(region.getNbtData().toNBT(), outputStream);
            region.writeData(outputStream);

        }catch (Throwable e) {
            LOGGER.error("Region Saving Failed for Region {}",region.getPos(), e);
            throw e;
        }
    }

    public WorldRegion read(RegionNoisesRecord record, RegionPos pos, RegionManager manager) throws IOException {
        Path location = makePath(this.path,pos);

        try (DataInputStream inputStream = this.inputStream(location)) {
            LOGGER.debug("Loading... \n Length : {} \nVersion: {}",inputStream.readInt(), inputStream.readShort());
            return WorldRegion.read(record,inputStream,manager,pos);
        }
    }

    private DataOutputStream outputStream(WorldRegion region) throws IOException {
        return new DataOutputStream(new FastBufferedOutputStream(new GZIPOutputStream(new RegionBuffer(region))));
    }

    private DataInputStream inputStream(Path path) throws IOException {
        return new DataInputStream(new BufferedInputStream(new GZIPInputStream(Files.newInputStream(path))));
    }

    private void writeRegion(RegionPos pos, ByteBuffer data) throws IOException {
        Path location = makePath(this.path,pos);

        try (FileChannel file = FileChannel.open(location, StandardOpenOption.CREATE, StandardOpenOption.WRITE,StandardOpenOption.TRUNCATE_EXISTING)) {
            data.position(5);
            file.write(data);
        }
    }

    private Path makePath(Path path, RegionPos pos) {
        String fileName = "r." + pos.x() + "." + pos.z() + ".rwreg";
        return path.resolve(fileName);
    }

    @Override
    public void close() throws Exception {

    }

    private class RegionBuffer extends ByteArrayOutputStream {

        private final RegionPos pos;

        public RegionBuffer(WorldRegion region) {
            super(2048);
            super.write(0);
            super.write(0);
            super.write(0);
            super.write(0);
            super.write(RegionVersion.LATEST_VERSION);
            this.pos = region.getPos();
        }

        @Override
        public void close() throws IOException {
            ByteBuffer result = ByteBuffer.wrap(this.buf, 0, this.count);
            int streamLength = this.count - 4;
            result.putInt(0, streamLength);
            RegionStorage.this.writeRegion(this.pos, result);
        }
    }
}
