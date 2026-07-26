package io.github.caiohbs.gbemu.cartridge;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class RomHeader {

    private static final int ENTRY_POINT_START = 0x0100;
    private static final int ENTRY_POINT_END = 0x0103;
    private static final int LOGO_START = 0x0104;
    private static final int LOGO_END = 0x0133;
    private static final int TITLE_START = 0x0134;
    private static final int TITLE_END = 0x0143;
    private static final int NEW_LIC_CODE_START = 0x0144;
    private static final int NEW_LIC_CODE_END = 0x0145;
    private static final int SGB_FLAG = 0x0146;
    private static final int CARTRIDGE_TYPE = 0x0147;
    private static final int ROM_SIZE = 0x0148;
    private static final int RAM_SIZE = 0x0149;
    private static final int DEST_CODE = 0x014A;
    private static final int OLD_LIC_CODE = 0x014B;
    private static final int VERSION = 0x014C;
    private static final int CHECKSUM = 0x014D;


    private final byte[] entrypoint;
    private final byte[] logo;
    private final String title;
    private final String newLicenseeCode;
    private final int sgbFlag;
    private final int cartridgeType;
    private final int romSizeCode;
    private final int ramSizeCode;
    private final int destCode;
    private final int oldLicenseeCode;
    private final int version;
    private final int checksum;
    private final boolean headerChecksumValid;

    public RomHeader(
            byte[] romData
    ) {

        this.entrypoint = readByteArray(romData, ENTRY_POINT_START, ENTRY_POINT_END);
        this.logo = readByteArray(romData, LOGO_START, LOGO_END);
        this.title = readString(romData, TITLE_START, TITLE_END);
        this.newLicenseeCode = readString(romData, NEW_LIC_CODE_START, NEW_LIC_CODE_END);
        this.sgbFlag = readByte(romData, SGB_FLAG);
        this.cartridgeType = readByte(romData, CARTRIDGE_TYPE);
        this.romSizeCode = readByte(romData, ROM_SIZE);
        this.ramSizeCode = readByte(romData, RAM_SIZE);
        this.destCode = readByte(romData, DEST_CODE);
        this.oldLicenseeCode = readByte(romData, OLD_LIC_CODE);
        this.version = readByte(romData, VERSION);
        this.checksum = readByte(romData, CHECKSUM);
        this.headerChecksumValid = validateHeaderChecksum(romData);

    }

    private int readByte(byte[] romData, int offset) {
        return romData[offset] & 0xFF;
    }

    private byte[] readByteArray(byte[] romData, int start, int endInclusive) {
        return Arrays.copyOfRange(romData, start, endInclusive + 1);
    }

    private String readString(byte[] romData, int start, int end) {
        String untreatedString = new String(romData, start, (end - start + 1), StandardCharsets.US_ASCII);
        return untreatedString.replace("\0", "").trim();
    }

    private boolean validateHeaderChecksum(byte[] romData) {
        int checksum = 0;

        for (int address = 0x0134; address <= 0x014C; address++) {
            checksum = (checksum - (romData[address] & 0xFF) - 1) & 0xFF;
        }

        return checksum == (romData[0x014D] & 0xFF);
    }

    public byte[] getEntrypoint() {
        return entrypoint;
    }

    public byte[] getLogo() {
        return logo;
    }

    public String getTitle() {
        return title;
    }

    public String getNewLicenseeCode() {
        return newLicenseeCode;
    }

    public int getSgbFlag() {
        return sgbFlag;
    }

    public int getCartridgeType() {
        return cartridgeType;
    }

    public int getRomSizeCode() {
        return romSizeCode;
    }

    public int getRamSizeCode() {
        return ramSizeCode;
    }

    public int getDestCode() {
        return destCode;
    }

    public int getOldLicenseeCode() {
        return oldLicenseeCode;
    }

    public int getVersion() {
        return version;
    }

    public int getChecksum() {
        return checksum;
    }

    public boolean isHeaderChecksumValid() {
        return headerChecksumValid;
    }

}
