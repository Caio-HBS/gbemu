package io.github.caiohbs.gbemu.memory;

import io.github.caiohbs.gbemu.cartridge.Cartridge;

public class Bus {

    public final Cartridge cartridge;

    public Bus(Cartridge cartridge) {
        this.cartridge = cartridge;
    }

}
