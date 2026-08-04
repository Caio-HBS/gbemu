package io.github.caiohbs.gbemu.cpu.mappers;

import io.github.caiohbs.gbemu.cpu.Instruction;

import static io.github.caiohbs.gbemu.cpu.enums.ConditionType.*;
import static io.github.caiohbs.gbemu.cpu.enums.InstructionType.*;
import static io.github.caiohbs.gbemu.cpu.enums.AddressMode.*;
import static io.github.caiohbs.gbemu.cpu.enums.RegisterType.*;

import java.util.Map;

public final class InstructionsByOpcode {

    public static final Map<Integer, Instruction> INSTRUCTIONS = Map.<Integer, Instruction>ofEntries(
            Map.entry(0x00, new Instruction(IN_NOP, AM_IMP,  RT_NONE, RT_NONE, CT_NONE, 0)),
            Map.entry(0x05, new Instruction(IN_DEC, AM_R,    RT_B,    RT_NONE, CT_NONE, 0)),
            Map.entry(0x0E, new Instruction(IN_LD,  AM_R_D8, RT_C,    RT_NONE, CT_NONE, 0)),
            Map.entry(0xAF, new Instruction(IN_XOR, AM_R,    RT_A,    RT_NONE, CT_NONE, 0)),
            Map.entry(0x21, new Instruction(IN_LD,  AM_D16,  RT_HL,   RT_NONE, CT_NONE, 0)),
            Map.entry(0xC3, new Instruction(IN_JP,  AM_D16,  RT_NONE, RT_NONE, CT_NONE, 0)),
            Map.entry(0xF3, new Instruction(IN_DI,  AM_NONE, RT_NONE, RT_NONE, CT_NONE, 0))
    );

    public static Instruction get(int opcode) {
        return INSTRUCTIONS.get(opcode);
    }

    private InstructionsByOpcode() {
    }

}
