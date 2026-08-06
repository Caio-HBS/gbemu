package io.github.caiohbs.gbemu.cpu;

import io.github.caiohbs.gbemu.memory.Bus;

public class Stack {

    private final Bus bus;
    private final CPURegisters registers;

    public Stack(Bus bus, CPURegisters registers) {
        this.bus = bus;
        this.registers = registers;
    }

    public void push(int data) {
        registers.setStackPointer(registers.getStackPointer() - 1);
        bus.write(registers.getStackPointer(), data & 0xFF);
    }

    public int pop() {
        int val = bus.read(registers.getStackPointer());
        registers.setStackPointer(registers.getStackPointer() + 1);
        return val & 0xFF;
    }

    public void push16(int data) {
        push((data >> 8) & 0xFF);
        push(data & 0xFF);
    }

    public int pop16() {
        int low = pop();
        int high = pop();
        return (high << 8) | low;
    }

}
