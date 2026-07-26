package io.github.caiohbs.gbemu.cartridge.mappers;

import java.util.Map;


public final class RomSizes {

    public static final Map<Integer, String> TYPES = Map.<Integer, String>ofEntries(
            Map.entry(0x00, "32 KiB (2 ROM banks)"),
            Map.entry(0x01, "64 KiB (4 ROM banks)"),
            Map.entry(0x02, "128 KiB (8 ROM banks)"),
            Map.entry(0x03, "256 KiB (16 ROM banks)"),
            Map.entry(0x04, "512 KiB (32 ROM banks)"),
            Map.entry(0x05, "1 MiB (64 ROM banks)"),
            Map.entry(0x06, "2 MiB (128 ROM banks)"),
            Map.entry(0x07, "4 MiB (256 ROM banks)"),
            Map.entry(0x08, "8 MiB (512 ROM banks)")
    );

    private RomSizes() {
    }

}

