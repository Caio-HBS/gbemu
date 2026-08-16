package io.github.caiohbs.gbemu.cpu;

import io.github.caiohbs.gbemu.cpu.enums.InterruptType;
import io.github.caiohbs.gbemu.memory.Bus;

import static io.github.caiohbs.gbemu.cpu.enums.InterruptType.*;

public class Interrupt {

    private final Bus bus;
    private final CPURegisters cpuRegisters;
    private final Stack stack;
    private final CPUState cpuState;

    public Interrupt(Bus bus, CPURegisters cpuRegisters, Stack stack, CPUState cpuState) {
        this.bus = bus;
        this.cpuRegisters = cpuRegisters;
        this.stack = stack;
        this.cpuState = cpuState;
    }

    private void interruptHandler(int address) {
        stack.push16(cpuRegisters.getProgramCounter());
        cpuRegisters.setProgramCounter(address);
    }

    private boolean interruptCheck(int address, InterruptType interruptType) {
        if ((cpuRegisters.getInterruptFlags() & interruptType.getCode()) != 0
            && (bus.getIeRegister() & interruptType.getCode()) != 0
        ) {
            interruptHandler(address);
            cpuRegisters.setInterruptFlags(cpuRegisters.getInterruptFlags() & ~interruptType.getCode());
            cpuState.setHalted(false);
            cpuState.setInterruptMasterEnabled(false);

            return true;
        }
        return false;
    }

    public void handleInterrupt() {
        if (interruptCheck(0x40, IT_VBLANK)) {
            // TODO
        } else if (interruptCheck(0x48, IT_LCD_STAT)) {
            // TODO
        } else if (interruptCheck(0x50, IT_TIMER)) {
            // TODO
        }  else if (interruptCheck(0x58, IT_SERIAL)) {
            // TODO
        }  else if (interruptCheck(0x60, IT_JOYPAD)) {
            // TODO
        }
    }

}
