package io.github.caiohbs.gbemu.emulator;

import io.github.caiohbs.gbemu.cartridge.Cartridge;
import io.github.caiohbs.gbemu.cpu.CPU;

public class Emulator {

    private final Cartridge cartridge = new Cartridge();
    private final CPU cpu = new CPU();

    public void emuRun(String[] args) {
        if (args.length < 2) {
            System.out.println("Correct usage: java -jar emulator.jar <path_to_rom> <path_to_save_state>");
            System.exit(-1);
        }
        if (!cartridge.load(args[0])) {
            System.out.println("Failed to load ROM: " + args[0]);
            System.exit(-2);
        }

        cpu.init();

        boolean running = true;
        boolean paused = false;
        long ticks = 0;

        while (running) {
            if (paused) {
                delay(1000);
                continue;
            }
            if (!cpu.step()) {
                System.out.println("CPU halted!");
                System.exit(-3);
            }
            ticks++;
        }

        System.out.println("Goodbye :)");
        System.exit(0);

    }

    public void delay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
