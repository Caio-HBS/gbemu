package io.github.caiohbs.gbemu.memory;

import io.github.caiohbs.gbemu.cartridge.Cartridge;

// 0x0000 - 0x3FFF: ROM Bank 0
// 0x4000 - 0x7FFF: ROM Bank 1 - Switchable
// 0x8000 - 0x97FF: CHR RAM
// 0x9800 - 0x9BFF: BG Map 1
// 0x9C00 - 0x9FFF: BG Map 2
// 0xA000 - 0xBFFF: Cartridge RAM
// 0xC000 - 0xCFFF: RAM Bank 0
// 0xD000 - 0xDFFF: RAM Bank 1-7 - switchable - Color only
// 0xE000 - 0xFDFF: Reserved - Echo RAM
// 0xFE00 - 0xFE9F: Object Attribute Memory
// 0xFEA0 - 0xFEFF: Reserved - Unusable
// 0xFF00 - 0xFF7F: I/O Registers
// 0xFF80 - 0xFFFE: Zero Page

public class Bus {

    public final Cartridge cartridge;
    public final RAM ram;
    private int ieRegister; // Note: declaring this here might change in the future.

    public Bus(Cartridge cartridge, RAM ram) {
        this.cartridge = cartridge;
        this.ram = ram;
    }

    public int read(int address) {
        if (address < 0x8000) {
            return cartridge.read(address);
        } else if (address < 0xA000) {
            // TODO
            System.out.printf("UNSUPPORTED bus_read(%04X)\n", address);
            System.exit(-3);
        } else if (address < 0xC000) {
            return cartridge.read(address);
        } else if (address < 0xE000) {
            return ram.wramRead(address);
        } else if (address < 0xFE00) {
            // Unusable (reserved)
            return 0;
        } else if (address < 0xFEA0) {
            // TODO
            System.out.printf("UNSUPPORTED bus_read(%04X)\n", address);
            System.exit(-3);
        } else if (address < 0xFF00) {
            // Unusable (reserved)
            return 0;
        } else if (address < 0xFF80) {
            // TODO
            System.out.printf("UNSUPPORTED bus_read(%04X)\n", address);
            System.exit(-3);
        } else if (address == 0xFFFF) {
            // TODO
            return ieRegister;
        }

        return ram.hramRead(address);
    }

    public void write(int address, int value) {
        if (address < 0x8000) {
            cartridge.write(address, value);
        } else if (address < 0xA000) {
            // TODO
            System.out.printf("UNSUPPORTED bus_write(%04X)\n", address);
            System.exit(-3);
        } else if (address < 0xC000) {
            cartridge.write(address, value);
        } else if (address < 0xE000) {
            ram.wramWrite(address, value);
        } else if (address < 0xFE00) {
            // Unusable (reserved)
        } else if (address < 0xFEA0) {
            // TODO
            System.out.printf("UNSUPPORTED bus_write(%04X)\n", address);
            System.exit(-3);
        } else if (address < 0xFF00) {
            // Unusable (reserved)
        } else if (address < 0xFF80) {
            // TODO
            System.out.printf("UNSUPPORTED bus_write(%04X)\n", address);
        } else if (address == 0xFFFF) {
            ieRegister = value & 0xFF;
        } else {
            ram.hramWrite(address, value);
        }
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
