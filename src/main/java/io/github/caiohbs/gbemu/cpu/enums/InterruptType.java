package io.github.caiohbs.gbemu.cpu.enums;

public enum InterruptType {

    IT_VBLANK(1),
    IT_LCD_STAT(2),
    IT_TIMER(4),
    IT_SERIAL(8),
    IT_JOYPAD(16);

    private final int code;

    InterruptType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
