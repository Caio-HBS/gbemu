package io.github.caiohbs.gbemu.emulator;

import io.github.caiohbs.gbemu.cartridge.Cartridge;
import io.github.caiohbs.gbemu.cpu.*;
import io.github.caiohbs.gbemu.memory.Bus;
import io.github.caiohbs.gbemu.memory.RAM;

public class Emulator {

    private final Cartridge cartridge = new Cartridge();
    private final RAM ram = new RAM();
    private final Bus bus = new Bus(cartridge, ram);
    private final CPURegisters cpuRegisters = new CPURegisters();
    private final CPUState cpuState = new CPUState();
    private final Stack stack = new Stack(bus, cpuRegisters);
    private final Interrupt interrupt = new Interrupt(bus, cpuRegisters, stack, cpuState);
    private final CPU cpu = new CPU(bus, this, cpuRegisters, stack, interrupt, cpuState);

    private volatile boolean isRunning = false;
    private volatile boolean isPaused = false;
    public long ticks;

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
        isRunning = true;
        isPaused = false;
        ticks = 0;

        Thread cpuThread = new Thread(this::cpuLoop, "gbemu-cpu");
        cpuThread.start();

        while (isRunning) {
            if (isPaused) {
                delay(1000);
                continue;
            }
            delay(16);
        }

        System.out.println("Goodbye :)");
        System.exit(0);
    }

    private void cpuLoop() {
        try {
        while (isRunning) {
            if (isPaused) {
                delay(1000);
                continue;
            }

            if (!cpu.step()) {
                System.out.println("CPU halted!");
                isRunning = false;
                return;
            }
            ticks++;
        }
    } catch (Throwable t) {
            // TODO: better error handling and/or logging
            t.printStackTrace();
        isRunning = false;
        System.exit(-3);
    }
    }

    public void delay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void cycle(int cycle) {
    }

}
