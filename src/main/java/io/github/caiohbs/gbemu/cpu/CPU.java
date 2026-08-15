package io.github.caiohbs.gbemu.cpu;

import io.github.caiohbs.gbemu.cpu.enums.RegisterType;
import io.github.caiohbs.gbemu.cpu.mappers.InstructionsByOpcode;
import io.github.caiohbs.gbemu.emulator.Emulator;
import io.github.caiohbs.gbemu.memory.Bus;

import java.util.Map;

import static io.github.caiohbs.gbemu.cpu.enums.AddressMode.*;
import static io.github.caiohbs.gbemu.cpu.enums.ConditionType.*;
import static io.github.caiohbs.gbemu.cpu.enums.RegisterType.*;

public class CPU {

    private final Bus bus;
    private final Emulator emulator;
    private final Stack stack;

    private final CPURegisters cpuRegisters;
    int fetchedData;
    int memoryDestination;
    boolean isDestMemory;
    int currOpcode;
    Instruction currInstruction;
    boolean isHalted;
    boolean isStepping;
    boolean isInterruptMasterEnabled;

    public CPU(Bus bus, Emulator emulator, CPURegisters cpuRegisters, Stack stack) {
        this.bus = bus;
        this.emulator = emulator;
        this.cpuRegisters = cpuRegisters;
        this.stack = stack;
    }

    public void init() {
        cpuRegisters.setProgramCounter(0x100); // Cartridge entrypoint
        cpuRegisters.setA(0x01);               // Default value for A
        cpuRegisters.setStackPointer(0xFFFE);  // Default Game Boy stack pointer
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

                if (currInstruction.getRegisterType1() == RT_C) {
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
                fetchedData = bus.read(cpuRegisters.getProgramCounter());
                memoryDestination = fetchedData | 0xFF00;
                isDestMemory = true;
                emulator.cycle(1);
                cpuRegisters.setProgramCounter(cpuRegisters.getProgramCounter() + 1);

                return;

            case AM_R_R, AM_R:
                fetchedData = readRegister(currInstruction.getRegisterType2());
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
                    if (is16Bit(currInstruction.getRegisterType2())) {
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

            case IN_INC:
                int val = readRegister(currInstruction.getRegisterType1()) + 1;

                if (is16Bit(currInstruction.getRegisterType1())) {
                    emulator.cycle(1);
                }

                if (currInstruction.getRegisterType1() == RT_HL && currInstruction.getAddressMode() == AM_MR) {
                    val = bus.read(readRegister(RT_HL) + 1);
                    val &= 0xFF;
                    bus.write(readRegister(RT_HL), val);
                } else {
                    setRegister(currInstruction.getRegisterType1(), val);
                    val = readRegister(currInstruction.getRegisterType1());
                }

                if ((currOpcode & 0x03) == 0x03) {
                    return;
                }

                setFlags(val == 0 ? 1 : 0, 0, (val & 0x0F) == 0 ? 1 : 0, -1);
                break;

            case IN_DEC:
                int val2 = readRegister(currInstruction.getRegisterType1()) - 1;

                if (is16Bit(currInstruction.getRegisterType1())) {
                    emulator.cycle(1);
                }

                if (currInstruction.getRegisterType1() == RT_HL && currInstruction.getAddressMode() == AM_MR) {
                    val2 = bus.read(readRegister(RT_HL)) - 1;
                    val2 &= 0xFF;
                    bus.write(readRegister(RT_HL), val2);
                } else {
                    setRegister(currInstruction.getRegisterType1(), val2);
                    val2 = readRegister(currInstruction.getRegisterType1());
                }

                if ((currOpcode & 0x0B) == 0x0B) {
                    return;
                }

                int hc = (readRegister(currInstruction.getRegisterType1()) & 0x0F) == 0 ? 1 : 0;
                setFlags(val2 == 0 ? 1 : 0, 0, hc, -1);
                break;

            case IN_ADD:
                int val3 = readRegister(currInstruction.getRegisterType1()) + fetchedData;
                boolean is16 = is16Bit(currInstruction.getRegisterType1());

                if (is16) {
                    emulator.cycle(1);
                }

                if (currInstruction.getRegisterType1() == RT_SP) {
                    val3 = readRegister(currInstruction.getRegisterType1()) + fetchedData;
                }

                int z = (val3 & 0xFF) == 0 ? 1 : 0;
                int h = (readRegister(currInstruction.getRegisterType1()) & 0xF) + (fetchedData & 0xF) >= 0x10 ? 1 : 0;
                int c = (readRegister(currInstruction.getRegisterType1()) + fetchedData) >= 0x100 ? 1 : 0;

                if (is16) {
                    z = -1;
                    h = (readRegister(currInstruction.getRegisterType1()) & 0xFFF) + (fetchedData & 0xFFF)
                        >= 0x1000 ? 1 : 0;
                    int n = (readRegister(currInstruction.getRegisterType1())) + (fetchedData) == 0 ? 1 : 0;
                    c = n >= 0x10000 ? 1 : 0;
                }

                if (currInstruction.getRegisterType1() == RT_SP) {
                    z = 0;
                    h = (readRegister(currInstruction.getRegisterType1()) & 0xF) + (fetchedData & 0xF) >= 0x10 ? 1 : 0;
                    c = (readRegister(currInstruction.getRegisterType1()) & 0xFF) + (fetchedData & 0xFF) > 0x100 ? 1 : 0;
                }

                setRegister(currInstruction.getRegisterType1(), val3 & 0xFFFF);
                setFlags(z, 0, h, c);

                break;

            case IN_JR:
                byte rel = (byte) this.fetchedData;
                int address = cpuRegisters.getProgramCounter() + rel;
                goToAddress(address, false);
                break;

            case IN_ADC:
                cpuRegisters.setA((cpuRegisters.getA() + fetchedData + (cpuRegisters.getF() & 0x10)) & 0xFF);
                setFlags(
                        cpuRegisters.getA() == 0 ? 1 : 0,
                        0,
                        (cpuRegisters.getA() & 0xF) + (fetchedData & 0xF) + (cpuRegisters.getF() & 0x10) == 0 ? 1 : 0,
                        (cpuRegisters.getA() + fetchedData + (cpuRegisters.getF() & 0x10)) == 0 ? 1 : 0
                );
                break;

            case IN_SUB:
                int val4 = readRegister(currInstruction.getRegisterType1()) - fetchedData;

                int z2 = val4 == 0 ? 1 : 0;
                int h2 = (readRegister(currInstruction.getRegisterType1()) & 0xF) - (fetchedData & 0xF) < 0 ? 1 : 0;
                int c2 = (readRegister(currInstruction.getRegisterType1())) - (fetchedData) < 0 ? 1 : 0;

                setRegister(currInstruction.getRegisterType1(), val4);
                setFlags(z2, 1, h2, c2);

                break;

            case IN_SBC:
                int val5 = fetchedData + (cpuRegisters.getF() & 0x10);

                int z3 = readRegister(currInstruction.getRegisterType1()) - val5 == 0 ? 1 : 0;
                int h3 = (readRegister(currInstruction.getRegisterType1()) & 0xF)
                         - (fetchedData & 0xF) - ((cpuRegisters.getF() & 0x10)) < 0 ? 1 : 0;
                int c3 = (readRegister(currInstruction.getRegisterType1()))
                         - (fetchedData) - ((cpuRegisters.getF() & 0x10)) < 0 ? 1 : 0;

                setRegister(currInstruction.getRegisterType1(), readRegister(currInstruction.getRegisterType1()) - val5);
                setFlags(z3, 1, h3, c3);

                break;

            case IN_POP:
                int low = stack.pop();
                emulator.cycle(1);
                int high = stack.pop();
                emulator.cycle(1);

                int value = (high << 8) | low;
                if (currInstruction.getRegisterType1() == RT_AF) {
                    value &= 0xFFF0;
                }

                setRegister(currInstruction.getRegisterType1(), value);
                break;

            case IN_JP:
                if (checkCondition()) {
                    cpuRegisters.setProgramCounter(fetchedData);
                    emulator.cycle(1);
                }
                break;

            case IN_PUSH:
                int high2 = (readRegister(currInstruction.getRegisterType1()) >> 8) & 0xFF;
                emulator.cycle(1);
                stack.push(high2);

                int low2 = readRegister(currInstruction.getRegisterType1()) & 0xFF;
                emulator.cycle(1);
                stack.push(low2);

                emulator.cycle(1);
                break;

            case IN_RET:
                if (currInstruction.getConditionType() != CT_NONE) {
                    emulator.cycle(1);
                }

                if (checkCondition()) {
                    int low3 = stack.pop();
                    emulator.cycle(1);
                    int high3 = stack.pop();
                    emulator.cycle(1);
                    cpuRegisters.setProgramCounter((high3 << 8) | low3);

                    emulator.cycle(1);
                }
                break;

            case IN_CB:
                int op = fetchedData;
                RegisterType reg = decodeRegister(op & 0b111);
                int bit = (op >> 3) & 0b111;
                int bitOp = (op >> 6) & 0b11;
                int registerValue = readRegister8(reg);

                emulator.cycle(1);

                if (reg == RT_HL) {
                    emulator.cycle(2);
                }

                switch (bitOp) {
                    case 1:
                        // BIT
                        setFlags((registerValue & (1 << bit)) == 0 ? 1 : 0, 0, 1, -1);
                        return;
                    case 2:
                        // RST
                        registerValue &= ~(1 << bit);
                        setRegister8(reg, registerValue);
                        return;
                    case 3:
                        // SET
                        registerValue |= (1 << bit);
                        setRegister8(reg, registerValue);
                        return;
                }

                boolean flagC = (cpuRegisters.getF() & 0x10) != 0;;

                switch (bit) {
                    case 0:
                        // RLC
                        boolean setC = false;
                        int result = (registerValue << 1) & 0xFF;

                        if ((registerValue & (1 << 7)) != 0) {
                            result |= 1;
                            setC = true;
                        }
                        setRegister8(reg, result);
                        setFlags(result == 0 ? 1 : 0, 0, 0, setC ? 1 : 0);
                        return;
                    case 1:
                        // RRC
                        int oldRRC = registerValue;
                        registerValue >>= 1;
                        registerValue |= (oldRRC << 7);

                        setRegister8(reg, registerValue);
                        setFlags(registerValue == 0 ? 1 : 0, 0, 0, oldRRC & 1);
                        return;
                    case 2:
                        // RL
                        int oldRL = registerValue;
                        registerValue <<= 1;
                        registerValue |= (flagC ? 1 : 0);

                        setRegister8(reg, registerValue);
                        setFlags(registerValue == 0 ? 1 : 0, 0, 0, (oldRL & 0x80) == 0 ? 1 : 0);
                        return;
                    case 3:
                        // RR
                        int oldRR = registerValue;
                        registerValue >>= 1;

                        registerValue |= (flagC ? 1 : 0) << 7;

                        setRegister8(reg, registerValue);
                        setFlags(registerValue == 0 ? 1 : 0, 0, 0, (oldRR & 0x80) == 0 ? 1 : 0);
                        return;
                    case 4:
                        // SLA
                        int oldSLA = registerValue;
                        registerValue <<= 1;

                        setRegister8(reg, registerValue);
                        setFlags(registerValue == 0 ? 1 : 0, 0, 0, (oldSLA & 0x80) == 0 ? 1 : 0);
                        return;
                    case 5:
                        // SRA
                        int uSRA = registerValue >> 1;
                        setRegister8(reg, uSRA);
                        setFlags(registerValue == 0 ? 1 : 0, 0, 0, (registerValue & 1) == 0 ? 1 : 0);
                        return;
                    case 6:
                        // SWAP
                        registerValue = (registerValue & 0xF0) >> 4 | (registerValue & 0xF) << 4;
                        setRegister8(reg, registerValue);
                        setFlags(registerValue == 0 ? 1 : 0, 0, 0, 0);
                        return;
                    case 7:
                        // SRL
                        int uSRL = registerValue >> 1;
                        setRegister8(reg, uSRL);
                        setFlags(uSRL == 0 ? 1 : 0, 0, 0, (registerValue & 1) == 0 ? 1 : 0);
                        return;
                }
                break;

            case IN_CALL:
                goToAddress(fetchedData, true);
                break;

            case IN_RETI:
                isInterruptMasterEnabled = true;
                if (currInstruction.getConditionType() != CT_NONE) {
                    emulator.cycle(1);
                }

                if (checkCondition()) {
                    int low4 = stack.pop();
                    emulator.cycle(1);
                    int high4 = stack.pop();
                    emulator.cycle(1);
                    cpuRegisters.setProgramCounter((high4 << 8) | low4);

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
                break;

            case IN_DI:
                isInterruptMasterEnabled = false;
                break;

            case IN_AND:
                cpuRegisters.setA(cpuRegisters.getA() & fetchedData);
                break;

            case IN_XOR:
                cpuRegisters.setA(cpuRegisters.getA() ^ (fetchedData & 0xFF));
                setFlags((cpuRegisters.getA() == 0 ? 1 : 0), 0, 0, 0);
                break;

            case IN_OR:
                cpuRegisters.setA(cpuRegisters.getA() | (fetchedData & 0xFF));
                setFlags((cpuRegisters.getA() == 0 ? 1 : 0), 0, 0, 0);
                break;

            case IN_CP:
                int n = cpuRegisters.getA() - fetchedData;

                setFlags(
                        n == 0 ? 1 : 0,
                        1,
                        (cpuRegisters.getA() & 0xFF) - (fetchedData & 0xFF) < 0 ? 1 : 0,
                        n < 0 ? 1 : 0
                );
                break;

            case IN_RST:
                goToAddress(currInstruction.getParam(), true);
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

            String flags = String.format("%c%c%c%c",
                    (cpuRegisters.getF() & 0x80) != 0 ? 'Z' : '-',
                    (cpuRegisters.getF() & 0x40) != 0 ? 'N' : '-',
                    (cpuRegisters.getF() & 0x20) != 0 ? 'H' : '-',
                    (cpuRegisters.getF() & 0x10) != 0 ? 'C' : '-'
            );

            if (currInstruction == null) {
                System.out.printf(
                        "%08X - %04X: %-7s (%02X %02X %02X) A: %02X | F: %s | BC: %02X%02X | DE: %02X%02X | HL: %02X%02X\n",
                        emulator.ticks, pc, "<NONE>", currOpcode, bus.read(pc + 1), bus.read(pc + 2),
                        cpuRegisters.getA(), flags, cpuRegisters.getB(), cpuRegisters.getC(), cpuRegisters.getD(),
                        cpuRegisters.getE(), cpuRegisters.getH(), cpuRegisters.getL()
                );
                System.exit(-7);
            }

            System.out.printf(
                    "%08X - %04X: %-7s (%02X %02X %02X) A: %02X | F: %s | BC: %02X%02X | DE: %02X%02X | HL: %02X%02X\n",
                    emulator.ticks, pc, currInstruction.getInstructionType().getName(), currOpcode, bus.read(pc + 1),
                    bus.read(pc + 2), cpuRegisters.getA(), flags, cpuRegisters.getB(), cpuRegisters.getC(),
                    cpuRegisters.getD(), cpuRegisters.getE(), cpuRegisters.getH(), cpuRegisters.getL()
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

    private int readRegister8(RegisterType rt) {
        switch (rt) {
            case RT_A: return cpuRegisters.getA();
            case RT_F: return cpuRegisters.getF();
            case RT_B: return cpuRegisters.getB();
            case RT_C: return cpuRegisters.getC();
            case RT_D: return cpuRegisters.getD();
            case RT_E: return cpuRegisters.getE();
            case RT_H: return cpuRegisters.getH();
            case RT_L: return cpuRegisters.getL();
            case RT_HL:
                return bus.read(readRegister(RT_HL));
            default:
                System.out.println("Invalid register8 type: " + rt);
                System.exit(-7);

        }
        return 0;
    }

    private void setRegister8(RegisterType registerType, int value) {
        switch (registerType) {
            case RT_A -> cpuRegisters.setA(value & 0xFF);
            case RT_F -> cpuRegisters.setF(value & 0xFF);
            case RT_B -> cpuRegisters.setB(value & 0xFF);
            case RT_C -> cpuRegisters.setC(value & 0xFF);
            case RT_D -> cpuRegisters.setD(value & 0xFF);
            case RT_E -> cpuRegisters.setE(value & 0xFF);
            case RT_H -> cpuRegisters.setH(value & 0xFF);
            case RT_L -> cpuRegisters.setL(value & 0xFF);
            case RT_HL -> bus.write(readRegister(RT_HL), value);
            default -> {
                System.out.println("Invalid register8 type: " + registerType);
                System.exit(-7);
            }
        }
    }

    private RegisterType decodeRegister(int reg) {
        if (reg > 0b111) {
            return RT_NONE;
        }

        Map<Integer, RegisterType> REGISTERS = Map.ofEntries(
                Map.entry(0, RegisterType.RT_B),
                Map.entry(1, RegisterType.RT_C),
                Map.entry(2, RegisterType.RT_D),
                Map.entry(3, RegisterType.RT_E),
                Map.entry(4, RegisterType.RT_H),
                Map.entry(5, RegisterType.RT_L),
                Map.entry(6, RegisterType.RT_HL),
                Map.entry(7, RegisterType.RT_A)
        );

        return REGISTERS.get(reg);
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

    private void goToAddress(int address, boolean isPushPC) {
        if (checkCondition()) {
            if (isPushPC) {
                emulator.cycle(2);
                stack.push16(cpuRegisters.getProgramCounter());
            }
            cpuRegisters.setProgramCounter(address);
            emulator.cycle(1);
        }
    }

    private boolean is16Bit(RegisterType registerType) {
        return registerType == RT_AF ||
               registerType == RT_BC ||
               registerType == RT_DE ||
               registerType == RT_HL ||
               registerType == RT_SP ||
               registerType == RT_PC;
    }

}
