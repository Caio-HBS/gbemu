package io.github.caiohbs.gbemu.cartridge.mappers;

import java.util.Map;


public final class RomTypes {

    public static final Map<Integer, String> TYPES = Map.<Integer, String>ofEntries(
            Map.entry(0x00, "ROM ONLY"),
            Map.entry(0x01, "MBC1"),
            Map.entry(0x02, "MBC1+RAM"),
            Map.entry(0x03, "MBC1+RAM+BATTERY"),
            Map.entry(0x05, "MBC2"),
            Map.entry(0x06, "MBC2+BATTERY"),
            Map.entry(0x08, "ROM+RAM 11"),
            Map.entry(0x09, "ROM+RAM+BATTERY 11"),
            Map.entry(0x0B, "MMM01"),
            Map.entry(0x0C, "MMM01+RAM"),
            Map.entry(0x0D, "MMM01+RAM+BATTERY"),
            Map.entry(0x0F, "MBC3+TIMER+BATTERY"),
            Map.entry(0x10, "MBC3+TIMER+RAM+BATTERY 12"),
            Map.entry(0x11, "MBC3"),
            Map.entry(0x12, "MBC3+RAM 12"),
            Map.entry(0x13, "MBC3+RAM+BATTERY 12"),
            Map.entry(0x19, "MBC5"),
            Map.entry(0x1A, "MBC5+RAM"),
            Map.entry(0x1B, "MBC5+RAM+BATTERY"),
            Map.entry(0x1C, "MBC5+RUMBLE"),
            Map.entry(0x1D, "MBC5+RUMBLE+RAM"),
            Map.entry(0x1E, "MBC5+RUMBLE+RAM+BATTERY"),
            Map.entry(0x20, "MBC6"),
            Map.entry(0x22, "MBC7+SENSOR+RUMBLE+RAM+BATTERY"),
            Map.entry(0xFC, "POCKET CAMERA"),
            Map.entry(0xFD, "BANDAI TAMA5"),
            Map.entry(0xFE, "HuC3"),
            Map.entry(0xFF, "HuC1+RAM+BATTERY")
    );

    private RomTypes() {
    }

}

