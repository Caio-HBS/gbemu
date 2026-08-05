package io.github.caiohbs.gbemu.memory;

public class RAM {

    private final int[] wram = new int[0x2000];
    private final int[] hram = new int[0x80];

    public int wramRead(int address) {
        address -= 0xC000;

        return wram[address];
    }

    public void wramWrite(int address, int value) {
        address -= 0xC000;

        wram[address] = value;
    }

    public int hramRead(int address) {
        address -= 0xFF80;

        return hram[address];
    }

    public void hramWrite(int address, int value) {
        address -= 0xFF80;

        hram[address] = value;
    }

}
