package io.github.caiohbs.gbemu.cpu;

public class CPUState {

    private boolean isHalted;
    private boolean isStepping;
    private boolean isInterruptMasterEnabled;

    public boolean isHalted() {
        return isHalted;
    }

    public void setHalted(boolean halted) {
        this.isHalted = halted;
    }

    public boolean isStepping() {
        return isStepping;
    }

    public void setStepping(boolean stepping) {
        this.isStepping = stepping;
    }

    public boolean isInterruptMasterEnabled() {
        return isInterruptMasterEnabled;
    }

    public void setInterruptMasterEnabled(boolean enabled) {
        this.isInterruptMasterEnabled = enabled;
    }
}
