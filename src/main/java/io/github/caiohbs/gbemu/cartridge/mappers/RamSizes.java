package io.github.caiohbs.gbemu.cartridge.mappers;

import java.util.Map;


public final class RamSizes {

    public static final Map<Integer, String> TYPES = Map.<Integer, String>ofEntries(
            Map.entry(0x00, "0 (No RAM)"),
            Map.entry(0x02, "8 KiB (1 bank)"),
            Map.entry(0x03, "32 KiB (4 banks of 8 KiB each)"),
            Map.entry(0x04, "128 KiB (16 banks of 8 KiB each)"),
            Map.entry(0x05, "64 KiB (8 banks of 8 KiB each)")
    );

    private RamSizes() {
    }

}

