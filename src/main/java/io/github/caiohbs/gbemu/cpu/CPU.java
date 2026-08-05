package io.github.caiohbs.gbemu.cpu;

import io.github.caiohbs.gbemu.cpu.enums.RegisterType;
import io.github.caiohbs.gbemu.cpu.mappers.InstructionsByOpcode;
import io.github.caiohbs.gbemu.emulator.Emulator;
import io.github.caiohbs.gbemu.memory.Bus;

import static io.github.caiohbs.gbemu.cpu.enums.AddressMode.*;
import static io.github.caiohbs.gbemu.cpu.enums.RegisterType.*;

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
        currOpcode = bus.read(cpuRegisters.getProgramCounter());
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
            case AM_NONE, AM_IMP:
                return;

            case AM_MR_R:
                fetchedData = readRegister(currInstruction.getRegisterType2());
                memoryDestination = readRegister(currInstruction.getRegisterType1());
                isDestMemory = true;

                if (currInstruction.getRegisterType1() == RT_HL) {
                    memoryDestination |= 0xFF00;
                }

                return;

            case AM_R_MR:
                int address = readRegister(currInstruction.getRegisterType2());

                if (currInstruction.getRegisterType2() == RT_C) {
                    address |= 0xFF00;
                }

                fetchedData = bus.read(address);
                emulator.cycle(1);

                return;

            case AM_R_HLI:
                fetchedData = readRegister(currInstruction.getRegisterType2());
                emulator.cycle(1);
                setRegister(RT_HL, readRegister(RT_HL) + 1);

                return;

            case AM_R_HLD:
                fetchedData = readRegister(currInstruction.getRegisterType2());
                emulator.cycle(1);
                setRegister(RT_HL, readRegister(RT_HL) - 1);

                return;

            case AM_HLI_R:
                fetchedData = readRegister(currInstruction.getRegisterType2());
                memoryDestination = readRegister(currInstruction.getRegisterType1());
                isDestMemory = true;
                setRegister(RT_HL, readRegister(RT_HL) + 1);

                return;

            case AM_HLD_R:
                fetchedData = readRegister(currInstruction.getRegisterType2());
                memoryDestination = readRegister(currInstruction.getRegisterType1());
                isDestMemory = true;
                setRegister(RT_HL, readRegister(RT_HL) - 1);

                return;

            case AM_R_A8, AM_HL_SPR, AM_R_D8, AM_D8:
                fetchedData = bus.read(cpuRegisters.getProgramCounter());
                emulator.cycle(1);
                cpuRegisters.setProgramCounter(cpuRegisters.getProgramCounter() + 1);

                return;

            case AM_A8_R:
                memoryDestination = bus.read(cpuRegisters.getProgramCounter()) | 0xFF00;
                isDestMemory = true;
                emulator.cycle(1);
                cpuRegisters.setProgramCounter(cpuRegisters.getProgramCounter() + 1);

                return;

            case AM_R_R, AM_R:
                fetchedData = readRegister(currInstruction.getRegisterType1());
                return;

            case AM_R_D16, AM_D16:
                int low = bus.read(cpuRegisters.getProgramCounter());
                emulator.cycle(1);
                int high = bus.read(cpuRegisters.getProgramCounter() + 1);
                emulator.cycle(1);
                fetchedData = low | (high << 8);
                cpuRegisters.setProgramCounter(cpuRegisters.getProgramCounter() + 2);
                return;

            case AM_D16_R, AM_A16_R:
                int low2 = bus.read(cpuRegisters.getProgramCounter());
                emulator.cycle(1);

                int high2 = bus.read(cpuRegisters.getProgramCounter() + 1);
                emulator.cycle(1);
                memoryDestination = low2 | (high2 << 8);
                isDestMemory = true;

                cpuRegisters.setProgramCounter(cpuRegisters.getProgramCounter() + 2);
                fetchedData = readRegister(currInstruction.getRegisterType2());

                return;

            case AM_MR_D8:
                fetchedData = bus.read(cpuRegisters.getProgramCounter());
                emulator.cycle(1);
                cpuRegisters.setProgramCounter(cpuRegisters.getProgramCounter() + 1);
                memoryDestination = readRegister(currInstruction.getRegisterType1());
                isDestMemory = true;

                return;

            case AM_MR:
                memoryDestination = readRegister(currInstruction.getRegisterType1());
                isDestMemory = true;
                fetchedData = bus.read(readRegister(currInstruction.getRegisterType1()));
                emulator.cycle(1);

                return;

            case AM_R_A16:
                int low3 = bus.read(cpuRegisters.getProgramCounter());
                emulator.cycle(1);

                int high3 = bus.read(cpuRegisters.getProgramCounter() + 1);
                emulator.cycle(1);

                int address3 = low3 | (high3 << 8);

                cpuRegisters.setProgramCounter(cpuRegisters.getProgramCounter() + 2);
                fetchedData = bus.read(address3);
                emulator.cycle(1);

                return;

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
                if (isDestMemory) {
                    if (
                            currInstruction.getRegisterType2() == RT_AF ||
                            currInstruction.getRegisterType2() == RT_BC ||
                            currInstruction.getRegisterType2() == RT_DE ||
                            currInstruction.getRegisterType2() == RT_HL ||
                            currInstruction.getRegisterType2() == RT_SP ||
                            currInstruction.getRegisterType2() == RT_PC
                    ) {
                        emulator.cycle(1);
                        bus.write16(memoryDestination, fetchedData);
                    } else {
                        bus.write(memoryDestination, fetchedData);
                    }
                    emulator.cycle(1);
                    return;
                }
                if (currInstruction.getAddressMode() == AM_HL_SPR) {
                    int hflag = (
                                        readRegister(currInstruction.getRegisterType2()) & 0xF) + (fetchedData & 0xF
                                ) >= 0x10 ? 1 : 0;
                    int cflag = (
                                        readRegister(currInstruction.getRegisterType2()) & 0xFF) + (fetchedData & 0xFF
                                ) >= 0x100 ? 1 : 0;

                    setFlags(0, 0, hflag, cflag);
                    setRegister(
                            currInstruction.getRegisterType1(),
                            readRegister(currInstruction.getRegisterType2()) + fetchedData
                    );
                    return;
                }

                setRegister(currInstruction.getRegisterType1(), fetchedData);
                break;

            case IN_JP:
                if (checkCondition()) {
                    cpuRegisters.setProgramCounter(fetchedData);
                    emulator.cycle(1);
                }
                break;

            case IN_LDH:
                if (currInstruction.getRegisterType1() == RT_A) {
                    setRegister(currInstruction.getRegisterType1(), bus.read(0xFF00 | fetchedData));
                } else {
                    bus.write(0xFF00 | fetchedData, cpuRegisters.getA());
                }
                emulator.cycle(1);

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
                System.out.printf("%04X: %-7s (%02X %02X %02X) A: %02X | BC: %02X%02X | DE: %02X%02X | HL: %02X%02X\n",
                        pc, "<NONE>", currOpcode, bus.read(pc + 1), bus.read(pc + 2), cpuRegisters.getA(),
                        cpuRegisters.getB(), cpuRegisters.getC(), cpuRegisters.getD(), cpuRegisters.getE(),
                        cpuRegisters.getH(), cpuRegisters.getC()
                );
                System.out.printf("    Unknown Instruction! %02X%n", currOpcode);
                System.exit(-7);
            }

            System.out.printf("%04X: %-7s (%02X %02X %02X) A: %02X | BC: %02X%02X | DE: %02X%02X | HL: %02X%02X\n", pc,
                    currInstruction.getInstructionType().getName(), currOpcode, bus.read(pc + 1),
                    bus.read(pc + 2), cpuRegisters.getA(), cpuRegisters.getB(), cpuRegisters.getC(),
                    cpuRegisters.getD(), cpuRegisters.getE(), cpuRegisters.getH(), cpuRegisters.getC()
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

    private void setRegister(RegisterType registerType, int value) {
        switch (registerType) {
            case RT_NONE -> {
            }
            case RT_A -> cpuRegisters.setA(value & 0xFF);
            case RT_F -> cpuRegisters.setF(value & 0xFF);
            case RT_B -> cpuRegisters.setB(value & 0xFF);
            case RT_C -> cpuRegisters.setC(value & 0xFF);
            case RT_D -> cpuRegisters.setD(value & 0xFF);
            case RT_E -> cpuRegisters.setE(value & 0xFF);
            case RT_H -> cpuRegisters.setH(value & 0xFF);
            case RT_L -> cpuRegisters.setL(value & 0xFF);
            case RT_AF -> {
                cpuRegisters.setA((value >> 8) & 0xFF);
                cpuRegisters.setF(value & 0xFF);
            }
            case RT_BC -> {
                cpuRegisters.setB((value >> 8) & 0xFF);
                cpuRegisters.setC(value & 0xFF);
            }
            case RT_DE -> {
                cpuRegisters.setD((value >> 8) & 0xFF);
                cpuRegisters.setE(value & 0xFF);
            }
            case RT_HL -> {
                cpuRegisters.setH((value >> 8) & 0xFF);
                cpuRegisters.setL(value & 0xFF);
            }
            case RT_PC -> cpuRegisters.setProgramCounter(value);
            case RT_SP -> cpuRegisters.setStackPointer(value);
            default -> System.out.println("Invalid register type: " + registerType);
        }
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
