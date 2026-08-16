package io.github.caiohbs.gbemu.cpu;

public class CPURegisters {

    private int a;
    private int f;
    private int b;
    private int c;
    private int d;
    private int e;
    private int h;
    private int l;

    private int programCounter;
    private int stackPointer;

    private int interruptFlags;

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public int getD() {
        return d;
    }

    public void setD(int d) {
        this.d = d;
    }

    public int getE() {
        return e;
    }

    public void setE(int e) {
        this.e = e;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getL() {
        return l;
    }

    public void setL(int l) {
        this.l = l;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(int programCounter) {
        this.programCounter = programCounter;
    }

    public int getStackPointer() {
        return stackPointer;
    }

    public void setStackPointer(int stackPointer) {
        this.stackPointer = stackPointer;
    }

    public int getInterruptFlags() {
        return interruptFlags;
    }

    public void setInterruptFlags(int interruptFlags) {
        this.interruptFlags = interruptFlags;
    }

}
