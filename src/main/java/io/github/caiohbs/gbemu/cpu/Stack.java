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
        int sp = (registers.getStackPointer() - 1) & 0xFFFF;
        registers.setStackPointer(sp);
        bus.write(sp, data & 0xFF);
    }

    public int pop() {
        int sp = registers.getStackPointer() & 0xFFFF;
        int val = bus.read(sp);
        registers.setStackPointer((sp + 1) & 0xFFFF);
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
