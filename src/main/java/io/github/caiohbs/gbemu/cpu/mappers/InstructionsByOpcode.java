package io.github.caiohbs.gbemu.cpu.mappers;

import io.github.caiohbs.gbemu.cpu.Instruction;

import static io.github.caiohbs.gbemu.cpu.enums.ConditionType.*;
import static io.github.caiohbs.gbemu.cpu.enums.InstructionType.*;
import static io.github.caiohbs.gbemu.cpu.enums.AddressMode.*;
import static io.github.caiohbs.gbemu.cpu.enums.RegisterType.*;

import java.util.Map;

public final class InstructionsByOpcode {

    public static final Map<Integer, Instruction> INSTRUCTIONS = Map.<Integer, Instruction>ofEntries(
            // 0x0X
            Map.entry(0x00, new Instruction(IN_NOP,  AM_IMP,   RT_NONE, RT_NONE, CT_NONE, 0)),
            Map.entry(0x01, new Instruction(IN_LD,   AM_R_D16, RT_BC,   RT_NONE, CT_NONE, 0)),
            Map.entry(0x02, new Instruction(IN_LD,   AM_MR_R,  RT_BC,   RT_A,    CT_NONE, 0)),
            Map.entry(0x03, new Instruction(IN_INC,  AM_R,     RT_BC,   RT_NONE, CT_NONE, 0)),
            Map.entry(0x04, new Instruction(IN_INC,  AM_R,     RT_B,    RT_NONE, CT_NONE, 0)),
            Map.entry(0x05, new Instruction(IN_DEC,  AM_R,     RT_B,    RT_NONE, CT_NONE, 0)),
            Map.entry(0x06, new Instruction(IN_LD,   AM_R_D8,  RT_B,    RT_NONE, CT_NONE, 0)),
            Map.entry(0x07, new Instruction(IN_RLCA, AM_NONE,  RT_NONE, RT_NONE, CT_NONE, 0)),
            Map.entry(0x08, new Instruction(IN_LD,   AM_A16_R, RT_NONE, RT_SP,   CT_NONE, 0)),
            Map.entry(0x09, new Instruction(IN_ADD,  AM_R_R,   RT_HL,   RT_BC,   CT_NONE, 0)),
            Map.entry(0x0A, new Instruction(IN_LD,   AM_R_MR,  RT_A,    RT_BC,   CT_NONE, 0)),
            Map.entry(0x0B, new Instruction(IN_DEC,  AM_R,     RT_BC,   RT_NONE, CT_NONE, 0)),
            Map.entry(0x0C, new Instruction(IN_INC,  AM_R,     RT_C,    RT_NONE, CT_NONE, 0)),
            Map.entry(0x0D, new Instruction(IN_DEC,  AM_R,     RT_C,    RT_NONE, CT_NONE, 0)),
            Map.entry(0x0E, new Instruction(IN_LD,   AM_R_D8,  RT_C,    RT_NONE, CT_NONE, 0)),
            Map.entry(0x0F, new Instruction(IN_RRCA, AM_NONE,  RT_NONE, RT_NONE, CT_NONE, 0)),
            // 0x1X
            Map.entry(0x11, new Instruction(IN_LD,   AM_R_D16, RT_DE,   RT_NONE, CT_NONE, 0)),
            Map.entry(0x12, new Instruction(IN_LD,   AM_MR_R,  RT_DE,   RT_NONE, CT_NONE, 0)),
            Map.entry(0x13, new Instruction(IN_INC,  AM_R,     RT_DE,   RT_NONE, CT_NONE, 0)),
            Map.entry(0x14, new Instruction(IN_INC,  AM_R,     RT_D,    RT_NONE, CT_NONE, 0)),
            Map.entry(0x15, new Instruction(IN_DEC,  AM_R,     RT_D,    RT_NONE, CT_NONE, 0)),
            Map.entry(0x16, new Instruction(IN_LD,   AM_R_D8,  RT_D,    RT_NONE, CT_NONE, 0)),
            Map.entry(0x17, new Instruction(IN_RLA,  AM_NONE,  RT_NONE, RT_NONE, CT_NONE, 0)),
            Map.entry(0x18, new Instruction(IN_JR,   AM_D8,    RT_NONE, RT_NONE, CT_NONE, 0)),
            Map.entry(0x19, new Instruction(IN_ADD,  AM_R_R,   RT_HL,   RT_DE,   CT_NONE, 0)),
            Map.entry(0x1A, new Instruction(IN_LD,   AM_R_MR,  RT_A,    RT_DE,   CT_NONE, 0)),
            Map.entry(0x1B, new Instruction(IN_DEC,  AM_R,     RT_DE,   RT_NONE, CT_NONE, 0)),
            Map.entry(0x1C, new Instruction(IN_INC,  AM_R,     RT_E,    RT_NONE, CT_NONE, 0)),
            Map.entry(0x1D, new Instruction(IN_DEC,  AM_R,     RT_E,    RT_NONE, CT_NONE, 0)),
            Map.entry(0x1E, new Instruction( IN_LD,  AM_R_D8,  RT_E,    RT_NONE, CT_NONE, 0)),
            Map.entry(0x1F, new Instruction(IN_RRA,  AM_NONE,  RT_NONE, RT_NONE, CT_NONE, 0)),
            // 0x2X
            Map.entry(0x21, new Instruction(IN_LD,   AM_R_D16, RT_HL,   RT_NONE, CT_NONE, 0)),
            Map.entry(0x22, new Instruction(IN_LD,   AM_HLI_R, RT_HL,   RT_A,    CT_NONE, 0)),
            Map.entry(0x25, new Instruction(IN_DEC,  AM_R,     RT_H,    RT_NONE, CT_NONE, 0)),
            Map.entry(0x26, new Instruction(IN_LD,   AM_R_D8,  RT_H,    RT_NONE, CT_NONE, 0)),
            Map.entry(0x2A, new Instruction(IN_LD,   AM_R_HLI, RT_A,    RT_HL,   CT_NONE, 0)),
            Map.entry(0x2E, new Instruction(IN_LD,   AM_R_D8,  RT_L,    RT_NONE, CT_NONE, 0)),
            // 0x3X
            Map.entry(0x31, new Instruction(IN_LD,   AM_R_D16, RT_SP,   RT_NONE, CT_NONE, 0)),
            Map.entry(0x32, new Instruction(IN_LD,   AM_HLD_R, RT_HL,   RT_A,    CT_NONE, 0)),
            Map.entry(0x35, new Instruction(IN_DEC,  AM_R,     RT_HL,   RT_NONE, CT_NONE, 0)),
            Map.entry(0x36, new Instruction(IN_LD,   AM_MR_D8, RT_HL,   RT_NONE, CT_NONE, 0)),
            Map.entry(0x3A, new Instruction(IN_LD,   AM_R_HLD, RT_A,    RT_HL,   CT_NONE, 0)),
            Map.entry(0x3E, new Instruction(IN_LD,   AM_R_D8,  RT_A,    RT_NONE, CT_NONE, 0)),
            // 0x4X
            Map.entry(0x40, new Instruction(IN_LD,   AM_R_R,   RT_B,    RT_B,    CT_NONE, 0)),
            Map.entry(0x41, new Instruction(IN_LD,   AM_R_R,   RT_B,    RT_C,    CT_NONE, 0)),
            Map.entry(0x42, new Instruction(IN_LD,   AM_R_R,   RT_B,    RT_D,    CT_NONE, 0)),
            Map.entry(0x43, new Instruction(IN_LD,   AM_R_R,   RT_B,    RT_E,    CT_NONE, 0)),
            Map.entry(0x44, new Instruction(IN_LD,   AM_R_R,   RT_B,    RT_H,    CT_NONE, 0)),
            Map.entry(0x45, new Instruction(IN_LD,   AM_R_R,   RT_B,    RT_L,    CT_NONE, 0)),
            Map.entry(0x46, new Instruction(IN_LD,   AM_R_MR,  RT_B,    RT_HL,   CT_NONE, 0)),
            Map.entry(0x47, new Instruction(IN_LD,   AM_R_R,   RT_B,    RT_A,    CT_NONE, 0)),
            Map.entry(0x48, new Instruction(IN_LD,   AM_R_R,   RT_C,    RT_B,    CT_NONE, 0)),
            Map.entry(0x49, new Instruction(IN_LD,   AM_R_R,   RT_C,    RT_C,    CT_NONE, 0)),
            Map.entry(0x4A, new Instruction(IN_LD,   AM_R_R,   RT_C,    RT_D,    CT_NONE, 0)),
            Map.entry(0x4B, new Instruction(IN_LD,   AM_R_R,   RT_C,    RT_E,    CT_NONE, 0)),
            Map.entry(0x4C, new Instruction(IN_LD,   AM_R_R,   RT_C,    RT_H,    CT_NONE, 0)),
            Map.entry(0x4D, new Instruction(IN_LD,   AM_R_R,   RT_C,    RT_L,    CT_NONE, 0)),
            Map.entry(0x4E, new Instruction(IN_LD,   AM_R_MR,  RT_C,    RT_HL,   CT_NONE, 0)),
            Map.entry(0x4F, new Instruction(IN_LD,   AM_R_R,   RT_C,    RT_A,    CT_NONE, 0)),
            // 0x5X
            Map.entry(0x50, new Instruction(IN_LD,   AM_R_R,   RT_D,    RT_B,    CT_NONE, 0)),
            Map.entry(0x51, new Instruction(IN_LD,   AM_R_R,   RT_D,    RT_C,    CT_NONE, 0)),
            Map.entry(0x52, new Instruction(IN_LD,   AM_R_R,   RT_D,    RT_D,    CT_NONE, 0)),
            Map.entry(0x53, new Instruction(IN_LD,   AM_R_R,   RT_D,    RT_E,    CT_NONE, 0)),
            Map.entry(0x54, new Instruction(IN_LD,   AM_R_R,   RT_D,    RT_H,    CT_NONE, 0)),
            Map.entry(0x55, new Instruction(IN_LD,   AM_R_R,   RT_D,    RT_L,    CT_NONE, 0)),
            Map.entry(0x56, new Instruction(IN_LD,   AM_R_MR,  RT_D,    RT_HL,   CT_NONE, 0)),
            Map.entry(0x57, new Instruction(IN_LD,   AM_R_R,   RT_D,    RT_A,    CT_NONE, 0)),
            Map.entry(0x58, new Instruction(IN_LD,   AM_R_R,   RT_E,    RT_B,    CT_NONE, 0)),
            Map.entry(0x59, new Instruction(IN_LD,   AM_R_R,   RT_E,    RT_C,    CT_NONE, 0)),
            Map.entry(0x5A, new Instruction(IN_LD,   AM_R_R,   RT_E,    RT_D,    CT_NONE, 0)),
            Map.entry(0x5B, new Instruction(IN_LD,   AM_R_R,   RT_E,    RT_E,    CT_NONE, 0)),
            Map.entry(0x5C, new Instruction(IN_LD,   AM_R_R,   RT_E,    RT_H,    CT_NONE, 0)),
            Map.entry(0x5D, new Instruction(IN_LD,   AM_R_R,   RT_E,    RT_L,    CT_NONE, 0)),
            Map.entry(0x5E, new Instruction(IN_LD,   AM_R_MR,  RT_E,    RT_HL,   CT_NONE, 0)),
            Map.entry(0x5F, new Instruction(IN_LD,   AM_R_R,   RT_E,    RT_A,    CT_NONE, 0)),
            // 0x6X
            Map.entry(0x60, new Instruction(IN_LD,   AM_R_R,   RT_H,    RT_B,    CT_NONE, 0)),
            Map.entry(0x61, new Instruction(IN_LD,   AM_R_R,   RT_H,    RT_C,    CT_NONE, 0)),
            Map.entry(0x62, new Instruction(IN_LD,   AM_R_R,   RT_H,    RT_D,    CT_NONE, 0)),
            Map.entry(0x63, new Instruction(IN_LD,   AM_R_R,   RT_H,    RT_E,    CT_NONE, 0)),
            Map.entry(0x64, new Instruction(IN_LD,   AM_R_R,   RT_H,    RT_H,    CT_NONE, 0)),
            Map.entry(0x65, new Instruction(IN_LD,   AM_R_R,   RT_H,    RT_L,    CT_NONE, 0)),
            Map.entry(0x66, new Instruction(IN_LD,   AM_R_MR,  RT_H,    RT_HL,   CT_NONE, 0)),
            Map.entry(0x67, new Instruction(IN_LD,   AM_R_R,   RT_H,    RT_A,    CT_NONE, 0)),
            Map.entry(0x68, new Instruction(IN_LD,   AM_R_R,   RT_L,    RT_B,    CT_NONE, 0)),
            Map.entry(0x69, new Instruction(IN_LD,   AM_R_R,   RT_L,    RT_C,    CT_NONE, 0)),
            Map.entry(0x6A, new Instruction(IN_LD,   AM_R_R,   RT_L,    RT_D,    CT_NONE, 0)),
            Map.entry(0x6B, new Instruction(IN_LD,   AM_R_R,   RT_L,    RT_E,    CT_NONE, 0)),
            Map.entry(0x6C, new Instruction(IN_LD,   AM_R_R,   RT_L,    RT_H,    CT_NONE, 0)),
            Map.entry(0x6D, new Instruction(IN_LD,   AM_R_R,   RT_L,    RT_L,    CT_NONE, 0)),
            Map.entry(0x6E, new Instruction(IN_LD,   AM_R_MR,  RT_L,    RT_HL,   CT_NONE, 0)),
            Map.entry(0x6F, new Instruction(IN_LD,   AM_R_R,   RT_L,    RT_A,    CT_NONE, 0)),
            // 0x7X
            Map.entry(0x70, new Instruction(IN_LD,   AM_MR_R,  RT_HL,   RT_B,    CT_NONE, 0)),
            Map.entry(0x71, new Instruction(IN_LD,   AM_MR_R,  RT_HL,   RT_C,    CT_NONE, 0)),
            Map.entry(0x72, new Instruction(IN_LD,   AM_MR_R,  RT_HL,   RT_D,    CT_NONE, 0)),
            Map.entry(0x73, new Instruction(IN_LD,   AM_MR_R,  RT_HL,   RT_E,    CT_NONE, 0)),
            Map.entry(0x74, new Instruction(IN_LD,   AM_MR_R,  RT_HL,   RT_H,    CT_NONE, 0)),
            Map.entry(0x75, new Instruction(IN_LD,   AM_MR_R,  RT_HL,   RT_L,    CT_NONE, 0)),
            Map.entry(0x76, new Instruction(IN_HALT, AM_NONE,  RT_NONE, RT_NONE, CT_NONE, 0)),
            Map.entry(0x77, new Instruction(IN_LD,   AM_MR_R,  RT_HL,   RT_A,    CT_NONE, 0)),
            Map.entry(0x78, new Instruction(IN_LD,   AM_R_R,   RT_A,    RT_B,    CT_NONE, 0)),
            Map.entry(0x79, new Instruction(IN_LD,   AM_R_R,   RT_A,    RT_C,    CT_NONE, 0)),
            Map.entry(0x7A, new Instruction(IN_LD,   AM_R_R,   RT_A,    RT_D,    CT_NONE, 0)),
            Map.entry(0x7B, new Instruction(IN_LD,   AM_R_R,   RT_A,    RT_E,    CT_NONE, 0)),
            Map.entry(0x7C, new Instruction(IN_LD,   AM_R_R,   RT_A,    RT_H,    CT_NONE, 0)),
            Map.entry(0x7D, new Instruction(IN_LD,   AM_R_R,   RT_A,    RT_L,    CT_NONE, 0)),
            Map.entry(0x7E, new Instruction(IN_LD,   AM_R_MR,  RT_A,    RT_HL,   CT_NONE, 0)),
            Map.entry(0x7F, new Instruction(IN_LD,   AM_R_R,   RT_A,    RT_A,    CT_NONE, 0)),
            // 0x8X
            // 0x9X
            // 0xAX
            Map.entry(0xAF, new Instruction(IN_XOR,  AM_R,     RT_A,    RT_NONE, CT_NONE, 0)),
            // 0xBX
            // 0xCX
            Map.entry(0xC3, new Instruction(IN_JP,   AM_D16,   RT_NONE, RT_NONE, CT_NONE, 0)),
            // 0xDX
            // 0xEX
            Map.entry(0xE0, new Instruction(IN_LDH,  AM_A8_R,  RT_NONE, RT_A,    CT_NONE, 0)),
            Map.entry(0xE2, new Instruction(IN_LD,   AM_MR_R,  RT_C,    RT_A,    CT_NONE, 0)),
            Map.entry(0xEA, new Instruction(IN_LD,   AM_A16_R, RT_NONE, RT_A,    CT_NONE, 0)),
            // 0xFX
            Map.entry(0xF0, new Instruction(IN_LDH,  AM_R_A8,  RT_A,    RT_NONE, CT_NONE, 0)),
            Map.entry(0xF2, new Instruction(IN_LD,   AM_R_MR,  RT_A,    RT_C,    CT_NONE, 0)),
            Map.entry(0xF3, new Instruction(IN_DI,   AM_NONE,  RT_NONE, RT_NONE, CT_NONE, 0)),
            Map.entry(0xFA, new Instruction(IN_LD,   AM_R_A16, RT_A,    RT_NONE, CT_NONE, 0))
    );

    public static Instruction get(int opcode) {
        return INSTRUCTIONS.get(opcode);
    }

    private InstructionsByOpcode() {
    }

}
