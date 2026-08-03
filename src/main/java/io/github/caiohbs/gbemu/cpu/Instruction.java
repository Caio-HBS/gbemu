package io.github.caiohbs.gbemu.cpu;

import io.github.caiohbs.gbemu.cpu.enums.AddressMode;
import io.github.caiohbs.gbemu.cpu.enums.ConditionType;
import io.github.caiohbs.gbemu.cpu.enums.InstructionType;
import io.github.caiohbs.gbemu.cpu.enums.RegisterType;

public class Instruction {

    private final InstructionType instructionType;
    private final AddressMode addressMode;
    private final RegisterType registerType1;
    private final RegisterType registerType2;
    private final ConditionType conditionType;
    private final int param;

    public Instruction(
            InstructionType instructionType, AddressMode addressMode, RegisterType registerType1,
            RegisterType registerType2, ConditionType conditionType, int param
    ) {
        this.instructionType = instructionType;
        this.addressMode = addressMode;
        this.registerType1 = registerType1;
        this.registerType2 = registerType2;
        this.conditionType = conditionType;
        this.param = param;
    }

    public InstructionType getInstructionType() {
        return instructionType;
    }

    public AddressMode getAddressMode() {
        return addressMode;
    }

    public RegisterType getRegisterType1() {
        return registerType1;
    }

    public RegisterType getRegisterType2() {
        return registerType2;
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public int getParam() {
        return param;
    }

}
