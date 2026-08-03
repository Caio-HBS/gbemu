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
        cpuRegisters.setA(0x01);               // Default value for A
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
            case AM_NONE:
                return;
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
                System.out.println("Unknown Address Mode! " + currInstruction.getAddressMode() + " (" + currOpcode + ")");
                System.exit(-7);
        }

    }

    private void execute() {
        switch (currInstruction.getInstructionType()) {
            case IN_NONE:
                System.out.println("INVALID INSTRUCTION!");
                System.exit(-7);
            case IN_NOP:
                break;
            case IN_LD:
                // TODO
                break;
            case IN_JP:
                if (checkCondition()) {
                    cpuRegisters.setProgramCounter(fetchedData);
                    emulator.cycle(1);
                }
                break;
            case IN_DI:
                isInterruptMasterEnabled = false;
                break;
            case IN_XOR:
                cpuRegisters.setA(cpuRegisters.getA() ^ (fetchedData & 0xFF));
                setFlags((cpuRegisters.getA() == 0 ? 1 : 0), 0, 0, 0);
                break;

            default:
                System.out.println("Instruction not implemented: " + currInstruction.getInstructionType());
                System.exit(-7);
        }
    }


    public boolean step() {
        if (!isHalted) {
            int pc = cpuRegisters.getProgramCounter();

            fetchInstruction();
            fetchData();

            if (currInstruction == null) {
                System.out.printf("%04X: %-7s (%02X %02X %02X) A: %02X | B: %02X | C: %02X\n", pc,
                        "<UNKNOWN>", currOpcode, bus.cartridge.read(pc + 1),
                        bus.cartridge.read(pc + 2), cpuRegisters.getA(), cpuRegisters.getB(), cpuRegisters.getC()
                );
                System.out.printf("    Unknown Instruction! %02X%n", currOpcode);
                System.exit(-7);
            }

            System.out.printf("%04X: %-7s (%02X %02X %02X) A: %02X | B: %02X | C: %02X\n", pc,
                    currInstruction.getInstructionType().getName(), currOpcode, bus.cartridge.read(pc + 1),
                    bus.cartridge.read(pc + 2), cpuRegisters.getA(), cpuRegisters.getB(), cpuRegisters.getC()
            );

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

    private boolean checkCondition() {
        boolean z = (cpuRegisters.getF() & 0x80) != 0;
        boolean c = (cpuRegisters.getF() & 0x10) != 0;

        return switch (currInstruction.getConditionType()) {
            case CT_NONE -> true;
            case CT_C -> c;
            case CT_NC -> !c;
            case CT_Z -> z;
            case CT_NZ -> !z;
        };
    }

    private void setFlags(int z, int n, int h, int c) {
        if (z != -1) {
            if (z != 0) {
                cpuRegisters.setF(cpuRegisters.getF() | (1 << 7));
            } else {
                cpuRegisters.setF(cpuRegisters.getF() & ~(1 << 7));
            }
        }
        if (n != -1) {
            if (n != 0) {
                cpuRegisters.setF(cpuRegisters.getF() | (1 << 6));
            } else {
                cpuRegisters.setF(cpuRegisters.getF() & ~(1 << 6));
            }
        }
        if (h != -1) {
            if (h != 0) {
                cpuRegisters.setF(cpuRegisters.getF() | (1 << 5));
            } else {
                cpuRegisters.setF(cpuRegisters.getF() & ~(1 << 5));
            }
        }
        if (c != -1) {
            if (c != 0) {
                cpuRegisters.setF(cpuRegisters.getF() | (1 << 4));
            } else {
                cpuRegisters.setF(cpuRegisters.getF() & ~(1 << 4));
            }
        }
    }

}
