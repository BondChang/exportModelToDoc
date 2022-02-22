package exportmodeltodoc.entity;

public class Branch {
    private String guard;
    private String targetName;
    private StateFlow stateFlow;
    private int wide;

    public Branch(String guard, String targetName) {
        this.guard = guard;
        this.targetName = targetName;
    }

    public String getGuard() {
        return guard;
    }

    public void setGuard(String guard) {
        this.guard = guard;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public StateFlow getStateFlow() {
        return stateFlow;
    }

    public void setStateFlow(StateFlow stateFlow) {
        this.stateFlow = stateFlow;
    }

    public int getWide() {
        return wide;
    }

    public void setWide(int wide) {
        this.wide = wide;
    }
}
