package com.xdandroid.cpuparts;

import androidx.annotation.WorkerThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public interface CpuParts {

    // https://github.com/torvalds/linux/blob/master/arch/arm64/include/asm/cputype.h
    int CORTEX_A510 = 0xD46;
    int CORTEX_A710 = 0xD47;
    int CORTEX_X2 = 0xD48;

    @WorkerThread
    static int[] load() throws IOException {
        final Process proc = Runtime.getRuntime().exec("cat /proc/cpuinfo");
        try (final InputStream is = proc.getInputStream()) {
            return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .filter(line -> line.contains("CPU part"))
                    .map(line -> line.substring(line.indexOf(':') + 2).trim())
                    .mapToInt(Integer::decode)
                    .toArray();
        } finally {
            proc.destroy();
        }
    }

    static int pure64BitCores(int[] parts) {
        return (int) Arrays.stream(parts).filter(part -> part == CORTEX_A510 || part == CORTEX_X2).count();
    }
}
