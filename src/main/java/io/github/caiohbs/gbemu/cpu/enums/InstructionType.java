package io.github.caiohbs.gbemu.cpu.enums;

public enum InstructionType {
    IN_NONE("<NONE>"),
    IN_NOP("NOP"),
    IN_LD("LD"),
    IN_INC("INC"),
    IN_DEC("DEC"),
    IN_RLCA("RLCA"),
    IN_ADD("ADD"),
    IN_RRCA("RRCA"),
    IN_STOP("STOP"),
    IN_RLA("RLA"),
    IN_JR("JR"),
    IN_RRA("RRA"),
    IN_DAA("DAA"),
    IN_CPL("CPL"),
    IN_SCF("SCF"),
    IN_CCF("CCF"),
    IN_HALT("HALT"),
    IN_ADC("ADC"),
    IN_SUB("SUB"),
    IN_SBC("SBC"),
    IN_AND("AND"),
    IN_XOR("XOR"),
    IN_OR("OR"),
    IN_CP("CP"),
    IN_POP("POP"),
    IN_JP("JP"),
    IN_PUSH("PUSH"),
    IN_RET("RET"),
    IN_CB("CB"),
    IN_CALL("CALL"),
    IN_RETI("RETI"),
    IN_LDH("LDH"),
    IN_JPHL("JPHL"),
    IN_DI("DI"),
    IN_EI("EI"),
    IN_RST("RST"),
    IN_ERR("ERR"),
    //CB instructions...
    IN_RLC("RLC"),
    IN_RRC("RRC"),
    IN_RL("RL"),
    IN_RR("RR"),
    IN_SLA("SLA"),
    IN_SRA("SRA"),
    IN_SWAP("SWAP"),
    IN_SRL("SRL"),
    IN_BIT("BIT"),
    IN_RES("RES"),
    IN_SET("SET");

    private final String name;

    InstructionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
