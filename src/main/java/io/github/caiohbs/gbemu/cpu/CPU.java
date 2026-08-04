package io.github.caiohbs.gbemu.cpu;

import io.github.caiohbs.gbemu.cpu.enums.RegisterType;
import io.github.caiohbs.gbemu.cpu.mappers.InstructionsByOpcode;
import io.github.caiohbs.gbemu.emulator.Emulator;
import io.github.caiohbs.gbemu.memory.Bus;

public class CPU {

    private final Bus bus;
    private final Emulator emulator;

    CPURegisters cpuRegisters = new CPURegisters();
    int fetchedData;
    int memoryDestination;
    boolean isDestMemory;
    int currOpcode;
    Instruction currInstruction;
    boolean isHalted;
    boolean isStepping;
    boolean isInterruptMasterEnabled;

    public CPU(Bus bus, Emulator emulator) {
        this.bus = bus;
        this.emulator = emulator;
    }

    public void init() {
        cpuRegisters.setProgramCounter(0x100); // Cartridge entrypoint
        cpuRegisters.setA(0x01);
    }

    private void fetchInstruction() {
        currOpcode = bus.cartridge.read(cpuRegisters.getProgramCounter());
        cpuRegisters.setProgramCounter(cpuRegisters.getProgramCounter() + 1);
        currInstruction = InstructionsByOpcode.get(currOpcode);
    }

    private void fetchData() {
        memoryDestination = 0;
        isDestMemory = false;

        if (currInstruction == null) {
            return;
        }

        switch (currInstruction.getAddressMode()) {
            case AM_IMP:
                return;
            case AM_R_D16:
                return; // TODO
            case AM_R_R:
                return; // TODO
            case AM_MR_R:
                return; // TODO
            case AM_R_MR:
                return; // TODO
            case AM_R_HLI:
                return; // TODO
            case AM_R_HLD:
                return; // TODO
            case AM_HLI_R:
                return; // TODO
            case AM_HLD_R:
                return; // TODO
            case AM_R_A8:
                return; // TODO
            case AM_A8_R:
                return; // TODO
            case AM_HL_SPR:
                return; // TODO
            case AM_R:
                fetchedData = readRegister(currInstruction.getRegisterType1());
                return;
            case AM_R_D8:
                fetchedData = bus.cartridge.read(cpuRegisters.getProgramCounter());
                emulator.cycle(1);
                cpuRegisters.setProgramCounter(cpuRegisters.getProgramCounter() + 1);
                return;
            case AM_D16:
                int low = bus.cartridge.read(cpuRegisters.getProgramCounter());
                emulator.cycle(1);
                int high = bus.cartridge.read(cpuRegisters.getProgramCounter() + 1);
                emulator.cycle(1);
                fetchedData = low | (high << 8);
                cpuRegisters.setProgramCounter(cpuRegisters.getProgramCounter() + 2);
                return;
            case AM_D8:
                return; // TODO
            case AM_D16_R:
                return; // TODO
            case AM_MR_D8:
                return; // TODO
            case AM_MR:
                return; // TODO
            case AM_A16_R:
                return; // TODO
            case AM_R_A16:
                return; // TODO
            default:
                System.out.println(
                        "Unknown Address Mode! " + currInstruction.getAddressMode() + " (" + currOpcode + ")"
                );
                System.exit(-7);
        }

    }

    private void execute() {
        System.out.println("    Not executing yet");
    }


    public boolean step() {
        if (!isHalted) {
            int pc = cpuRegisters.getProgramCounter();

            fetchInstruction();
            fetchData();

            System.out.printf("Executing instruction: %X    PC: %X%n", currOpcode, pc);

            if (currInstruction == null) {
                System.out.printf("    Unknown Instruction! %X%n", currOpcode);
                System.exit(-7);
            }

            execute();
        }

        return true;
    }

    private int readRegister(RegisterType rt) {
        return switch (rt) {
            case RT_A -> cpuRegisters.getA();
            case RT_F -> cpuRegisters.getF();
            case RT_B -> cpuRegisters.getB();
            case RT_C -> cpuRegisters.getC();
            case RT_D -> cpuRegisters.getD();
            case RT_E -> cpuRegisters.getE();
            case RT_H -> cpuRegisters.getH();
            case RT_L -> cpuRegisters.getL();
            case RT_AF -> (cpuRegisters.getA() << 8) | cpuRegisters.getF();
            case RT_BC -> (cpuRegisters.getB() << 8) | cpuRegisters.getC();
            case RT_DE -> (cpuRegisters.getD() << 8) | cpuRegisters.getE();
            case RT_HL -> (cpuRegisters.getH() << 8) | cpuRegisters.getL();
            case RT_SP -> cpuRegisters.getStackPointer();
            case RT_PC -> cpuRegisters.getProgramCounter();
            default -> 0;
        };
    }

}
