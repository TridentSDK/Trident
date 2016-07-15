package net.tridentsdk.server;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

// WARNING: YOU NEED LOTS OF RAM!!!!

// Purposes of this class
// 1: Test different strategies of reading and writing files
// 2: Test buffer sizes
// 3: Test how file size affects performance
// 4: ByteBuffer vs ByteArrayOutputStream vs byte[]

// Backoffs

// The end goal of this is to find a good, performant, and
// stable method of reading and writing configuration files
// that are realistic in comparison between the test and the
// real world. Since we want a string in the end, we need
// to append all of the bytes in order to mimic string
// parsing, although parsing performance is out of scope of
// this test.

abstract class ByteDest {
    private final int len;

    public ByteDest(int len) {
        this.len = len;
    }

    public int len() {
        return this.len;
    }

    public abstract void write(byte[] bytes);

    public abstract byte[] finish();
}

class ByteBufferSource extends ByteDest {
    private final ByteBuffer buf;

    public ByteBufferSource(int len) {
        super(len);

        // Unfortunately we should not be using off-heap
        // memory because that would drastically reduce
        // the stability of Trident, despite how fun it is
        // to play with

        // You can change this to allocateDirect if you want
        // to really try
        buf = ByteBuffer.allocate(len());
    }

    @Override
    public void write(byte[] bytes) {
        buf.put(bytes);
    }

    @Override
    public byte[] finish() {
        return buf.array();
    }

}

class BaosBufferSource extends ByteDest {
    private final ByteArrayOutputStream stream = new ByteArrayOutputStream(50_000);

    public BaosBufferSource(int len) {
        super(len);
    }

    @Override
    public void write(byte[] bytes) {
        stream.write(bytes, 0, bytes.length);
    }

    @Override
    public byte[] finish() {
        return stream.toByteArray();
    }

}

class RawByteSource extends ByteDest {
    private final byte[] bytes;
    private int pos;

    public RawByteSource(int len) {
        super(len);

        this.bytes = new byte[len];
    }

    @Override
    public void write(byte[] src) {
        System.arraycopy(src, 0, this.bytes, pos, src.length);
        pos += src.length;
    }

    @Override
    public byte[] finish() {
        return bytes;
    }

}

abstract class IoStrat<R, W> {
    private final File file;
    private final int bufSize;

    public IoStrat(Path path, int bufSize) {
        this.file = path.toFile();
        this.bufSize = bufSize;
    }

    public File file() {
        return this.file;
    }

    public int bufSize() {
        return this.bufSize;
    }

    public abstract String name();

    // Note that throws causes minor bytecode additions to
    // exceptiontable in a method tag but eh whatever
    // Unless it causes a problem, it doesn't matter
    public abstract R setupRead() throws Exception;

    public abstract W setupWrite() throws Exception;

    public abstract void read(R r, ByteDest src) throws IOException;

    public abstract void write(W w, byte[] bytes) throws IOException;

    // Buffering only possible with classic, but whatever
    public abstract void writeBuffed(W w, byte[] bytes) throws IOException;

    // We need to clean up after each read and write stream
    // or channel is opened or java doesn't like it
    public abstract void cleanupRead(R r) throws IOException;

    public abstract void cleanupWrite(W w) throws IOException;
}

// Use the actual class instead of using the InputStream
// interface to avoid invokeinterface opcode overhead
class ClassicStrat extends IoStrat<FileInputStream, FileOutputStream> {
    private final byte[] buffer;

    public ClassicStrat(Path path, int bufSize) {
        super(path, bufSize);

        this.buffer = new byte[bufSize()];
    }

    @Override
    public String name() {
        return "Classic";
    }

    @Override
    public FileInputStream setupRead() throws FileNotFoundException {
        return new FileInputStream(file());
    }

    @Override
    public FileOutputStream setupWrite() throws FileNotFoundException {
        return new FileOutputStream(file());
    }

    @Override
    public void read(FileInputStream fileInputStream, ByteDest src) throws IOException {
        while (fileInputStream.read(this.buffer) != -1) {
            src.write(this.buffer);
        }
    }

    @Override
    public void write(FileOutputStream fileOutputStream, byte[] bytes) throws IOException {
        fileOutputStream.write(bytes);
    }

    @Override
    public void writeBuffed(FileOutputStream fileOutputStream, byte[] bytes) throws IOException {
        BufferedOutputStream stream = new BufferedOutputStream(fileOutputStream, bufSize());
        stream.write(bytes);
        stream.flush();
    }

    @Override
    public void cleanupRead(FileInputStream fileInputStream) throws IOException {
        fileInputStream.close();
    }

    @Override
    public void cleanupWrite(FileOutputStream fileOutputStream) throws IOException {
        fileOutputStream.close();
    }
}

class BufferedFileChannel extends IoStrat<FileChannel, FileChannel> {
    private final ByteBuffer buffer;

    public BufferedFileChannel(Path path, int bufSize) {
        super(path, bufSize);

        this.buffer = ByteBuffer.wrap(new byte[bufSize()]);
    }

    @Override
    public String name() {
        return "Buffered FileChannel";
    }

    @Override
    public FileChannel setupRead() throws Exception {
        return new FileInputStream(file()).getChannel();
    }

    @Override
    public FileChannel setupWrite() throws Exception {
        return new FileOutputStream(file()).getChannel();
    }

    @Override
    public void read(FileChannel fileChannel, ByteDest src) throws IOException {
        while (fileChannel.read(buffer) > 0) {
            buffer.flip();
            src.write(buffer.array());
            buffer.clear();
        }
    }

    @Override
    public void write(FileChannel fileChannel, byte[] bytes) throws IOException {
        ByteBuffer wrap = ByteBuffer.wrap(bytes);
        fileChannel.write(wrap);
    }

    // WARNING OS LEVEL BUFFERING POSSIBLE

    @Override
    public void writeBuffed(FileChannel fileChannel, byte[] bytes) throws IOException {
        ByteBuffer wrap = ByteBuffer.wrap(bytes);
        fileChannel.write(wrap);
    }

    @Override
    public void cleanupRead(FileChannel fileChannel) throws IOException {
        fileChannel.close();
    }

    @Override
    public void cleanupWrite(FileChannel fileChannel) throws IOException {
        fileChannel.close();
    }
}

class MemMappedChannel extends IoStrat<FileChannel, FileChannel> {
    private final byte[] buffer;

    public MemMappedChannel(Path path, int bufSize) {
        super(path, bufSize);

        this.buffer = new byte[bufSize()];
    }

    @Override
    public String name() {
        return "Memmapped FileChannel";
    }

    @Override
    public FileChannel setupRead() throws Exception {
        return new FileInputStream(file()).getChannel();
    }

    @Override
    public FileChannel setupWrite() throws Exception {
        return new RandomAccessFile(file(), "rw").getChannel();
    }

    @Override
    public void read(FileChannel fileChannel, ByteDest src) throws IOException {
        MappedByteBuffer map = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
        while (map.hasRemaining()) {
            map.get(buffer, 0, buffer.length);
            src.write(buffer);
        }
    }

    @Override
    public void write(FileChannel fileChannel, byte[] bytes) throws IOException {
        MappedByteBuffer map = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, bytes.length);
        map.put(bytes);
    }

    // Who knows

    @Override
    public void writeBuffed(FileChannel fileChannel, byte[] bytes) throws IOException {
        MappedByteBuffer map = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, bytes.length);
        map.put(bytes);
    }

    @Override
    public void cleanupRead(FileChannel fileChannel) throws IOException {
        fileChannel.close();
    }

    @Override
    public void cleanupWrite(FileChannel fileChannel) throws IOException {
        fileChannel.close();
    }
}

class Raf extends IoStrat<RandomAccessFile, RandomAccessFile> {
    private final byte[] buffer;

    public Raf(Path path, int bufSize) {
        super(path, bufSize);

        this.buffer = new byte[bufSize()];
    }

    @Override
    public String name() {
        return "RandomAccessFile";
    }

    @Override
    public RandomAccessFile setupRead() throws Exception {
        return new RandomAccessFile(file(), "r");
    }

    @Override
    public RandomAccessFile setupWrite() throws Exception {
        return new RandomAccessFile(file(), "rw");
    }

    @Override
    public void read(RandomAccessFile randomAccessFile, ByteDest src) throws IOException {
        while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
            randomAccessFile.readFully(buffer);
            src.write(buffer);
        }
    }

    @Override
    public void write(RandomAccessFile randomAccessFile, byte[] bytes) throws IOException {
        randomAccessFile.write(bytes, 0, bytes.length);
    }

    // Whatever might as well go for a second time

    @Override
    public void writeBuffed(RandomAccessFile randomAccessFile, byte[] bytes) throws IOException {
        randomAccessFile.write(bytes, 0, bytes.length);
    }

    @Override
    public void cleanupRead(RandomAccessFile randomAccessFile) throws IOException {
        randomAccessFile.close();
    }

    @Override
    public void cleanupWrite(RandomAccessFile randomAccessFile) throws IOException {
        randomAccessFile.close();
    }
}

// We don't need anymore boilerplate, just get rid of getter
// methods please >.<
class UnitSettings {
    public final int fileBytes;
    public final int bufSize;
    public final Path path;

    public UnitSettings(int fileBytes, int bufSize, Path path) {
        this.fileBytes = fileBytes;
        this.bufSize = bufSize;
        this.path = path;
    }
}

class AvgMap {
    private final Map<String, Long> avg = Maps.newTreeMap();

    public void add(String s, long t) {
        Long l = avg.get(s);
        if (l == null) {
            avg.put(s, t);
        } else {
            Preconditions.checkState(avg.replace(s, l, (l + t) / 2));
        }
    }

    public void print(PrintStream o) {
        for (String s : avg.keySet()) {
            o.println(s + " - " + avg.get(s));
        }
    }
}

class FreqMap {
    private final Map<String, Integer> freq = Maps.newTreeMap();

    public void incr(String s) {
        Integer i = freq.get(s);
        if (i == null) {
            freq.put(s, 1);
        } else {
            Preconditions.checkState(freq.replace(s, i, i + 1));
        }
    }

    public void print(PrintStream o) {
        for (String s : freq.keySet()) {
            o.println(s + " - " + freq.get(s));
        }
    }
}

public class IoTest {
    public static final int TEST_TRIALS = 100;
    public static final int MAX_SANITY_SIZE = 4096;
    public static final int WARM_TRIALS = 100;

    // Kek 400 lines of boiler plate

    public long checkTimer() {
        // Warmup
        for (int i = 0; i < 10_000; i++) {
            long time = System.nanoTime();
            while (System.nanoTime() == time) {
            }
        }

        long time = System.nanoTime();
        long start = System.nanoTime();
        for (int i = 0; i < 30_000_000; i++) {
            long current;
            do {
                current = System.nanoTime();
            } while (current == time);
            time = current;
        }
        long end = System.nanoTime();
        return (end - start) / 30_000_000;
    }

    public void checkWrite(byte[] content, Path path) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(path.toFile(), "r");
        Preconditions.checkState(raf.length() == content.length);

        // Read center third of the file
        long start = (long) (content.length * 0.333);
        long end = start + (long) Math.min(content.length * 0.666, MAX_SANITY_SIZE);

        raf.seek(start);
        for (int i = 0; i < (end - start); i++) {
            Preconditions.checkState(content[i + (int) start] == raf.readByte(), "Index " + i + " is incorrectly written");
        }
    }

    public void checkRead(byte[] content, ByteDest dest) {
        byte[] finish = dest.finish();
        Preconditions.checkState(finish.length == content.length, content.length + " != " + finish.length);

        // Read center third of the file
        long start = (long) (content.length * 0.333);
        long end = start + (long) Math.min(content.length * 0.666, MAX_SANITY_SIZE);

        for (int i = 0; i < (end - start); i++) {
            int actualIdx = i + (int) start;
            Preconditions.checkState(content[actualIdx] == finish[actualIdx], "Index " + i + " is incorrectly read");
        }
    }

    // Basically from a long64 we want a byte from each 8
    // bit section (because byte = 8bits) so we have 8
    // sections that can generate some garbage number
    public byte[] generateGarbage(int length) {
        byte[] garbage = new byte[length];
        for (int i = 0; i < length; i += 8) {
            long rand = ThreadLocalRandom.current().nextLong();
            for (int j = 0; j < 8 && (i + j) < length; j++) {
                garbage[i + j] = (byte) (rand >>> (j * 8) & 255);
            }
        }

        return garbage;
    }

    public Path newTest(Path dir, String name) throws IOException {
        Path path = Paths.get(dir.toString(), "/" + name + ".bin");
        if (Files.exists(path)) {
            Files.delete(path);
        }

        Files.createFile(path);
        return path;
    }

    public void runGc() {
        Runtime r = Runtime.getRuntime();
        long startMem = r.freeMemory();

        while (r.freeMemory() - startMem < 500) {
            System.gc();
        }
    }

    public String getFastest(Map<Long, String> time) {
        long fastTime = 0;
        String fastest = null;
        for (long l : time.keySet()) {
            if (fastest == null || fastTime > l) {
                fastTime = l;
                fastest = time.get(l);
            }
        }

        return fastest;
    }

    public void memCheck(PrintStream o) {
        o.println("Memcheck - free memory: " + Runtime.getRuntime().freeMemory());
    }

    public void warmup(Path enclosing, PrintStream o) throws Exception {
        int fileLength = 24576; // 24 kb
        UnitSettings settings = new UnitSettings(fileLength, 8192,
                newTest(enclosing, "warmup"));
        Set<IoStrat> unit = createUnit(settings);

        byte[] garbage = generateGarbage(fileLength);
        FreqMap freq = new FreqMap();
        AvgMap avg = new AvgMap();

        o.println("Starting to warmup...");
        o.println("Writing files a few times...");
        for (int i = 0; i < WARM_TRIALS; i++) {
            Map<Long, String> time = Maps.newTreeMap();
            garbage = generateGarbage(fileLength);
            for (IoStrat strat : unit) {
                o.print("Warming up " + strat.name() + " (write) - ");
                Object resource = strat.setupWrite();

                long startMs = System.currentTimeMillis();
                long start = System.nanoTime();
                strat.write(resource, garbage);
                long end = System.nanoTime();
                long endMs = System.currentTimeMillis();

                long elapsed = end - start;
                o.println("Took " + elapsed + "ns; " + (endMs - startMs) + "ms");
                String name = strat.name() + " (write)";
                time.put(elapsed, name);
                avg.add(name, elapsed);

                strat.cleanupWrite(resource);
                checkWrite(garbage, settings.path);
            }

            String fastest = getFastest(time);
            o.println("Fastest this round: " + fastest);
            freq.incr(fastest);
            runGc();
        }
        o.println("Finished writing files.");

        o.println();

        o.println("Writing files a few times (force buffer)...");
        for (int i = 0; i < WARM_TRIALS; i++) {
            Map<Long, String> time = Maps.newTreeMap();
            garbage = generateGarbage(fileLength);
            for (IoStrat strat : unit) {
                o.println("Warming up " + strat.name() + " (buffered write)");
                o.print("Warming up " + strat.name() + " (buffered write) - ");
                Object resource = strat.setupWrite();

                long startMs = System.currentTimeMillis();
                long start = System.nanoTime();
                strat.writeBuffed(resource, garbage);
                long end = System.nanoTime();
                long endMs = System.currentTimeMillis();

                long elapsed = end - start;
                o.println("Took " + elapsed + "ns; " + (endMs - startMs) + "ms");
                String name = strat.name() + " (buffered write)";
                time.put(elapsed, name);
                avg.add(name, elapsed);

                strat.cleanupWrite(resource);
                checkWrite(garbage, settings.path);
            }

            String fastest = getFastest(time);
            o.println("Fastest this round: " + fastest);
            freq.incr(fastest);
            runGc();
        }
        o.println("Finished writing files.");

        o.println();

        o.println("Reading files a few times.");
        o.println("Allocating buffers, this might take a bit...");
        for (int i = 0; i < WARM_TRIALS; i++) {
            Map<Long, String> time = Maps.newTreeMap();
            for (IoStrat strat : unit) {
                o.println("Warming up " + strat.name() + " (read)");
                o.print("Warming up " + strat.name() + " (read) - ");
                Object resource = strat.setupRead();
                ByteDest dest = new BaosBufferSource(fileLength);

                long startMs = System.currentTimeMillis();
                long start = System.nanoTime();
                strat.read(resource, dest);
                long end = System.nanoTime();
                long endMs = System.currentTimeMillis();

                long elapsed = end - start;
                o.println("Took " + elapsed + "ns; " + (endMs - startMs) + "ms");
                String name = strat.name() + " (read)";
                time.put(elapsed, name);
                avg.add(name, elapsed);

                strat.cleanupRead(resource);
                checkRead(garbage, dest);
            }

            String fastest = getFastest(time);
            o.println("Fastest this round: " + fastest);
            freq.incr(fastest);
            runGc();
        }
        o.println("Finished reading.");

        o.println();

        o.println("Warmup times");
        avg.print(o);

        o.println();

        o.println("Frequency");
        freq.print(o);
    }

    public void runStratComparison(Path enclosing, PrintStream o, FreqMap freq, AvgMap avg) throws Exception {
        // We use typical settings
        // File bytes = 8192 bytes = most small configs
        // Buf = 8192 bytes = bufferedXYZ classes
        // Path = ...
        // dest = baos

        int fileLength = 8192;
        UnitSettings settings = new UnitSettings(fileLength, 8192,
                newTest(enclosing, "stratcomp"));
        Set<IoStrat> unit = createUnit(settings);

        byte[] garbage = generateGarbage(fileLength);

        o.println("Starting writing tests (no output)...");
        for (int i = 0; i < TEST_TRIALS; i++) {
            garbage = generateGarbage(fileLength);
            Map<Long, String> time = Maps.newTreeMap();
            for (IoStrat strat : unit) {
                // 1 setup
                String name = strat.name() + " (write)";
                Object resource = strat.setupWrite();

                // 2 run test
                long start = System.nanoTime();
                strat.write(resource, garbage);
                long end = System.nanoTime();
                long elapsed = end - start;

                // 3 log data
                time.put(elapsed, name);
                avg.add(name, elapsed);

                // 4 cleanup
                strat.cleanupWrite(resource);
                checkWrite(garbage, settings.path);
            }

            runGc();
            freq.incr(getFastest(time));
        }
        o.println("Finished write tests.");

        o.println();

        o.println("Starting force buffered write tests (no output)...");
        for (int i = 0; i < TEST_TRIALS; i++) {
            garbage = generateGarbage(fileLength);
            Map<Long, String> time = Maps.newTreeMap();
            for (IoStrat strat : unit) {
                // 1 setup
                String name = strat.name() + " (write - force buffer)";
                Object resource = strat.setupWrite();

                // 2 run test
                long start = System.nanoTime();
                strat.writeBuffed(resource, garbage);
                long end = System.nanoTime();
                long elapsed = end - start;

                // 3 log data
                time.put(elapsed, name);
                avg.add(name, elapsed);

                // 4 cleanup
                strat.cleanupWrite(resource);
                checkWrite(garbage, settings.path);
            }

            runGc();
            freq.incr(getFastest(time));
        }
        o.println("Finish force buffer writes");

        o.println();

        o.println("Starting read tests (no output)...");
        for (int i = 0; i < TEST_TRIALS; i++) {
            Map<Long, String> time = Maps.newTreeMap();
            for (IoStrat strat : unit) {
                // 1 setup
                String name = strat.name() + " (read)";
                Object resource = strat.setupRead();
                ByteDest dest = new BaosBufferSource(fileLength);

                // 2 run test
                long start = System.nanoTime();
                strat.read(resource, dest);
                long end = System.nanoTime();
                long elapsed = end - start;

                // 3 log data
                time.put(elapsed, name);
                avg.add(name, elapsed);

                // 4 cleanup
                strat.cleanupRead(resource);
                checkRead(garbage, dest);
            }

            runGc();
            freq.incr(getFastest(time));
        }
        o.println("Finished read test.");
        o.println();
    }

    public void runBufferSize(Path enclosing, PrintStream o, FreqMap freq, AvgMap avg) throws Exception {
        // We use typical settings
        // File bytes = 8192 bytes = most small configs
        // Buf = x { 1024, 4096, 8192 }
        // Path = ...
        // dest = baos

        int fileLength = 8192;
        Set<IoStrat> unit1024 = createUnit(new UnitSettings(fileLength, 1024,
                newTest(enclosing, "bufcomp1024")));
        Set<IoStrat> unit4096 = createUnit(new UnitSettings(fileLength, 4096,
                newTest(enclosing, "bufcomp4096")));
        Set<IoStrat> unit8192 = createUnit(new UnitSettings(fileLength, 8192,
                newTest(enclosing, "bufcomp8192")));

        byte[] garbage = generateGarbage(fileLength);

        // Buffered write
        o.println("Starting buffer size write test (no output)...");
        for (int i = 0; i < TEST_TRIALS; i++) {
            Map<Long, String> time = Maps.newTreeMap();
            garbage = generateGarbage(fileLength);

            for (IoStrat strat : unit1024) {
                // 1 setup
                String name = strat.name() + " (write buffer 1024)";
                Object resource = strat.setupWrite();

                // 2 run test
                long start = System.nanoTime();
                strat.writeBuffed(resource, garbage);
                long end = System.nanoTime();
                long elapsed = end - start;

                // 3 log data
                time.put(elapsed, name);
                avg.add(name, elapsed);

                // 4 cleanup
                strat.cleanupWrite(resource);
                checkWrite(garbage, enclosing.resolve("bufcomp1024.bin"));
            }

            runGc();

            for (IoStrat strat : unit4096) {
                // 1 setup
                String name = strat.name() + " (write buffer 4096)";
                Object resource = strat.setupWrite();

                // 2 run test
                long start = System.nanoTime();
                strat.writeBuffed(resource, garbage);
                long end = System.nanoTime();
                long elapsed = end - start;

                // 3 log data
                time.put(elapsed, name);
                avg.add(name, elapsed);

                // 4 cleanup
                strat.cleanupWrite(resource);
                checkWrite(garbage, enclosing.resolve("bufcomp4096.bin"));
            }

            runGc();

            for (IoStrat strat : unit8192) {
                // 1 setup
                String name = strat.name() + " (write buffer 8192)";
                Object resource = strat.setupWrite();

                // 2 run test
                long start = System.nanoTime();
                strat.writeBuffed(resource, garbage);
                long end = System.nanoTime();
                long elapsed = end - start;

                // 3 log data
                time.put(elapsed, name);
                avg.add(name, elapsed);

                // 4 cleanup
                strat.cleanupWrite(resource);
                checkWrite(garbage, enclosing.resolve("bufcomp8192.bin"));
            }

            runGc();
            freq.incr(getFastest(time));
        }
        o.println("Finished write test.");

        o.println();

        // Buffered read
        o.println("Starting buffer size read test (no output)...");
        for (int i = 0; i < TEST_TRIALS; i++) {
            Map<Long, String> time = Maps.newTreeMap();

            for (IoStrat strat : unit1024) {
                // 1 setup
                String name = strat.name() + " (read buffer 1024)";
                Object resource = strat.setupRead();
                ByteDest dest = new BaosBufferSource(fileLength);

                // 2 run test
                long start = System.nanoTime();
                strat.read(resource, dest);
                long end = System.nanoTime();
                long elapsed = end - start;

                // 3 log data
                time.put(elapsed, name);
                avg.add(name, elapsed);

                // 4 cleanup
                strat.cleanupRead(resource);
                checkRead(garbage, dest);
            }

            runGc();

            for (IoStrat strat : unit4096) {
                // 1 setup
                String name = strat.name() + " (read buffer 4096)";
                Object resource = strat.setupRead();
                ByteDest dest = new BaosBufferSource(fileLength);

                // 2 run test
                long start = System.nanoTime();
                strat.read(resource, dest);
                long end = System.nanoTime();
                long elapsed = end - start;

                // 3 log data
                time.put(elapsed, name);
                avg.add(name, elapsed);

                // 4 cleanup
                strat.cleanupRead(resource);
                checkRead(garbage, dest);
            }

            runGc();

            for (IoStrat strat : unit8192) {
                // 1 setup
                String name = strat.name() + " (read buffer 8192)";
                Object resource = strat.setupRead();
                ByteDest dest = new BaosBufferSource(fileLength);

                // 2 run test
                long start = System.nanoTime();
                strat.read(resource, dest);
                long end = System.nanoTime();
                long elapsed = end - start;

                // 3 log data
                time.put(elapsed, name);
                avg.add(name, elapsed);

                // 4 cleanup
                strat.cleanupRead(resource);
                checkRead(garbage, dest);
            }

            runGc();
            freq.incr(getFastest(time));
        }
        o.println("Finished read test.");
    }

    public void runBufferType(Path enclosing, PrintStream o, FreqMap freq, AvgMap avg) throws Exception {
        int fileLength = 8192;
        UnitSettings settings = new UnitSettings(fileLength, 8192,
                newTest(enclosing, "typecomp"));
        Set<IoStrat> unit = createUnit(settings);
        byte[] garbage = generateGarbage(fileLength);
        Files.write(settings.path, garbage);

        o.println("Starting buffer type read test (no output)...");
        for (int i = 0; i < TEST_TRIALS; i++) {
            Map<Long, String> time = Maps.newTreeMap();

            for (IoStrat strat : unit) {
                // 1 setup
                String name = strat.name() + " (read BAOS buffer)";
                Object resource = strat.setupRead();
                ByteDest dest = new BaosBufferSource(fileLength);

                // 2 run test
                long start = System.nanoTime();
                strat.read(resource, dest);
                long end = System.nanoTime();
                long elapsed = end - start;

                // 3 log data
                time.put(elapsed, name);
                avg.add(name, elapsed);

                // 4 cleanup
                strat.cleanupRead(resource);
                checkRead(garbage, dest);
            }

            runGc();

            for (IoStrat strat : unit) {
                // 1 setup
                String name = strat.name() + " (read ByteBuffer buffer)";
                Object resource = strat.setupRead();
                ByteDest dest = new ByteBufferSource(fileLength);

                // 2 run test
                long start = System.nanoTime();
                strat.read(resource, dest);
                long end = System.nanoTime();
                long elapsed = end - start;

                // 3 log data
                time.put(elapsed, name);
                avg.add(name, elapsed);

                // 4 cleanup
                strat.cleanupRead(resource);
                checkRead(garbage, dest);
            }

            runGc();

            for (IoStrat strat : unit) {
                // 1 setup
                String name = strat.name() + " (read byte[] buffer)";
                Object resource = strat.setupRead();
                ByteDest dest = new RawByteSource(fileLength);

                // 2 run test
                long start = System.nanoTime();
                strat.read(resource, dest);
                long end = System.nanoTime();
                long elapsed = end - start;

                // 3 log data
                time.put(elapsed, name);
                avg.add(name, elapsed);

                // 4 cleanup
                strat.cleanupRead(resource);
                checkRead(garbage, dest);
            }

            runGc();
            freq.incr(getFastest(time));
        }
        o.println("Finished buffer type read test.");
    }

    public void runFileSize(Path enclosing, PrintStream o, FreqMap freq, AvgMap avg) throws Exception {
        // We use typical settings
        // File bytes = x { 1024, 10000, 100000 }
        // Buf = 8192
        // Path = ...
        // dest = baos

        int fl1 = 1024; // 1kb
        int fl2 = 10_000; // 524288000; // 500mb
        int fl3 = 100_000; // 1073741824; // 1gb

        Set<IoStrat> unit1 = createUnit(new UnitSettings(fl1, 8192,
                newTest(enclosing, "sizecomp1kb")));
        Set<IoStrat> unit2 = createUnit(new UnitSettings(fl2, 8192,
                newTest(enclosing, "sizecomp500mb")));
        Set<IoStrat> unit3 = createUnit(new UnitSettings(fl3, 8192,
                newTest(enclosing, "sizecomp1gb")));

        byte[] garbage1 = generateGarbage(fl1);
        byte[] garbage2 = generateGarbage(fl2);
        byte[] garbage3 = generateGarbage(fl3);

        o.println("Starting file size write test (no output)...");
        for (int i = 0; i < TEST_TRIALS; i++) {
            Map<Long, String> time = Maps.newTreeMap();

            for (IoStrat strat : unit1) {
                // 1 setup
                String name = strat.name() + " (size 1kb)";
                Object resource = strat.setupWrite();

                // 2 run test
                long start = System.nanoTime();
                strat.write(resource, garbage1);
                long end = System.nanoTime();
                long elapsed = end - start;

                // 3 log data
                time.put(elapsed, name);
                avg.add(name, elapsed);

                // 4 cleanup
                strat.cleanupWrite(resource);
                checkWrite(garbage1, enclosing.resolve("sizecomp1kb.bin"));
            }

            runGc();

            for (IoStrat strat : unit2) {
                // 1 setup
                String name = strat.name() + " (size 500mb)";
                Object resource = strat.setupWrite();

                // 2 run test
                long start = System.nanoTime();
                strat.write(resource, garbage2);
                long end = System.nanoTime();
                long elapsed = end - start;

                // 3 log data
                time.put(elapsed, name);
                avg.add(name, elapsed);

                // 4 cleanup
                strat.cleanupWrite(resource);
                checkWrite(garbage2, enclosing.resolve("sizecomp500mb.bin"));
            }

            runGc();

            for (IoStrat strat : unit3) {
                // 1 setup
                String name = strat.name() + " (size 1gb)";
                Object resource = strat.setupWrite();

                // 2 run test
                long start = System.nanoTime();
                strat.write(resource, garbage3);
                long end = System.nanoTime();
                long elapsed = end - start;

                // 3 log data
                time.put(elapsed, name);
                avg.add(name, elapsed);

                // 4 cleanup
                strat.cleanupWrite(resource);
                checkWrite(garbage3, enclosing.resolve("sizecomp1gb.bin"));
            }

            runGc();
            freq.incr(getFastest(time));
        }
        o.println("Finished file size write test.");

        o.println();

        o.println("Starting file size read test (no output)...");
        for (int i = 0; i < TEST_TRIALS; i++) {
            Map<Long, String> time = Maps.newTreeMap();

            for (IoStrat strat : unit1) {
                // 1 setup
                String name = strat.name() + " (read size 1kb)";
                Object resource = strat.setupRead();
                ByteDest dest = new BaosBufferSource(fl1);

                // 2 run test
                long start = System.nanoTime();
                strat.read(resource, dest);
                long end = System.nanoTime();
                long elapsed = end - start;

                // 3 log data
                time.put(elapsed, name);
                avg.add(name, elapsed);

                // 4 cleanup
                strat.cleanupRead(resource);
                checkRead(garbage1, dest);
            }

            runGc();

            for (IoStrat strat : unit2) {
                // 1 setup
                String name = strat.name() + " (read size 500mb)";
                Object resource = strat.setupRead();
                ByteDest dest = new BaosBufferSource(fl2);

                // 2 run test
                long start = System.nanoTime();
                strat.read(resource, dest);
                long end = System.nanoTime();
                long elapsed = end - start;

                // 3 log data
                time.put(elapsed, name);
                avg.add(name, elapsed);

                // 4 cleanup
                strat.cleanupRead(resource);
                checkRead(garbage2, dest);
            }

            runGc();

            for (IoStrat strat : unit3) {
                // 1 setup
                String name = strat.name() + " (read size 1gb)";
                Object resource = strat.setupRead();
                ByteDest dest = new BaosBufferSource(fl3);

                // 2 run test
                long start = System.nanoTime();
                strat.read(resource, dest);
                long end = System.nanoTime();
                long elapsed = end - start;

                // 3 log data
                time.put(elapsed, name);
                avg.add(name, elapsed);

                // 4 cleanup
                strat.cleanupRead(resource);
                checkRead(garbage3, dest);
            }

            runGc();
            freq.incr(getFastest(time));
        }
        o.println("Finished file size read test.");
    }

    public Set<IoStrat> createUnit(UnitSettings s) {
        Path p = s.path;
        int bufSize = s.bufSize;

        return Sets.newHashSet(
                new ClassicStrat(p, bufSize),
                new BufferedFileChannel(p, bufSize),
                new MemMappedChannel(p, bufSize),
                new Raf(p, bufSize));
    }

    public static void main(String[] args) throws Exception {
        PrintStream o = System.out;

        o.println("Some performance test made by agenttroll");
        o.println("Checking for other files in folder...");

        Path currentDir = Paths.get(".");
        File[] files = currentDir.toFile().listFiles();
        // We're in a messy place, move to our own folder
        // Don't want to make these files impossible to find
        if (files == null || files.length > 1) {
            o.println("Looks like you have this in a random directory");
            o.println("Creating new directory for you...");
            currentDir = Files.createDirectory(Paths.get(UUID.randomUUID().toString()));
            o.println("Directory: " + currentDir.toString());
        } else {
            o.println("Good, this is a new folder. Nothing else needed.");
        }

        Path log = Files.createFile(currentDir.resolve("log.log"));
        System.setOut(new PrintStream(Files.newOutputStream(log)));

        o.println("Don't give me any of the .bin files please");
        o.println("I only want the log.log file");
        o.println();

        IoTest test = new IoTest();

        o.println("Preliminary setup, looking at system clock...");
        long gran = test.checkTimer();
        o.println("Timer granularity = " + gran + "ns");

        o.println();

        // Warm stuff up
        test.memCheck(o);
        o.println("Warming up the VM to get JIT to compile code...");
        test.warmup(currentDir, o);
        o.println("Finished warmup.");
        test.memCheck(o);

        o.println();

        // Measure 1
        test.memCheck(o);
        o.println("Starting measurement test...");
        FreqMap firstFreq = new FreqMap();
        AvgMap firstAvg = new AvgMap();
        test.runStratComparison(currentDir, o, firstFreq, firstAvg);
        o.println("Finished measurement.");
        test.memCheck(o);

        o.println();
        o.println("Average times");
        firstAvg.print(o);

        o.println();

        o.println("Frequency");
        firstFreq.print(o);
        o.println("Measured strategy comparisons.");

        o.println();

        // Measure 2 - Use classic method for simplicity
        test.memCheck(o);
        o.println("Starting buffer size test...");
        FreqMap freq2 = new FreqMap();
        AvgMap avg2 = new AvgMap();
        test.runBufferSize(currentDir, o, freq2, avg2);
        o.println("Finished buffer size test.");
        test.memCheck(o);

        o.println();
        o.println("Average times");
        avg2.print(o);

        o.println();

        o.println("Frequency");
        freq2.print(o);
        o.println("Measured buffer sizes.");

        o.println();

        // Measure 3
        test.memCheck(o);
        o.println("Starting buffer type test...");
        FreqMap freq3 = new FreqMap();
        AvgMap avg3 = new AvgMap();
        test.runBufferType(currentDir, o, freq3, avg3);
        o.println("Finished buffer type test.");
        test.memCheck(o);

        o.println();
        o.println("Average times");
        avg3.print(o);

        o.println();

        o.println("Frequency");
        freq3.print(o);
        o.println("Measured buffer types.");

        o.println();

        // Measure 4
        /* test.memCheck(o);
        o.println("Starting file size test...");
        FreqMap freq4 = new FreqMap();
        AvgMap avg4 = new AvgMap();
        test.runFileSize(currentDir, o, freq4, avg4);
        o.println("Finished file size test.");
        test.memCheck(o);

        o.println();
        o.println("Average times");
        avg4.print(o);

        o.println();

        o.println("Frequency");
        freq4.print(o);
        o.println("Measured file sizes."); */

        o.println();

        o.println("FINISHED TEST");
        o.println("PLEASE SEND LOG.LOG TO AGENTTROLL");

        // Cleanup
        for (File file : currentDir.toFile().listFiles()) {
            if (file.getAbsolutePath().endsWith(".bin")) {
                file.delete();
            }
        }
    }
}