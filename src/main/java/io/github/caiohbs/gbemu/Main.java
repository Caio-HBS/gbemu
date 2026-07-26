package io.github.caiohbs.gbemu;

import io.github.caiohbs.gbemu.emulator.Emulator;

public class Main {
    public static void main(String[] args) {
        Emulator emulator = new Emulator();
        emulator.emuRun(args);
    }
}
