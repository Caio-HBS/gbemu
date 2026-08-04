package io.github.caiohbs.gbemu.cartridge;

import io.github.caiohbs.gbemu.cartridge.mappers.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Cartridge {

    byte[] romData;

    public boolean load(String path) {
        Path rom = Path.of(path);
        try {
            if (!Files.exists(rom)) {
                System.out.println("File not found.");
                return false;
            }

            romData = Files.readAllBytes(rom);

            if (romData.length == 0) {
                System.out.println("File is empty.");
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        RomHeader header = new RomHeader(romData);

        String licCode = (
                (header.getOldLicenseeCode() == 33))
                ? NewLicenseeCodes.TYPES.get(header.getNewLicenseeCode())
                : OldLicenseeCodes.TYPES.get(header.getOldLicenseeCode()
        );

        System.out.println("\n====================================================");
        System.out.println("Cartridge Loaded successfully.");
        System.out.println("Game Title  : " + header.getTitle());
        System.out.println("Type        : " + RomTypes.TYPES.get(header.getCartridgeType()));
        System.out.println("ROM Size    : " + RomSizes.TYPES.get(header.getRomSizeCode()));
        System.out.println("RAM Size    : " + RamSizes.TYPES.get(header.getRamSizeCode()));
        System.out.println("LIC Code    : " + licCode);
        System.out.println("ROM Version : " + header.getVersion());
        System.out.println("Checksum    : " + ((header.isHeaderChecksumValid()) ? "VALID" : "INVALID"));
        System.out.println("====================================================\n");

        return true;
    }

    public int read(int address) {
        if (address > 0x8000) {
            System.out.printf("UNSUPPORTED BUS read(%04X)\n", address);
            System.exit(-3);
        }

        return romData[address] & 0xFF;
    }

    public void write(int address, int value) {
        if (address > 0x8000) {
            System.out.printf("UNSUPPORTED BUS write(%04X)\n", address);
            System.exit(-3);
        }

        System.out.printf("BUS write(%04X) -> %02X\n", address, value);

    }

    public int read16(int address) {
        int high = read(address);
        int low = read(address + 1);

        return low | (high << 8);
    }

    public void write16(int address, int value) {
        write(address + 1, (value >> 8) & 0xFF);
        write(address, value & 0xFF);
    }

}
