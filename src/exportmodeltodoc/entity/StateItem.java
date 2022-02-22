package exportmodeltodoc.entity;

import java.util.ArrayList;
import java.util.List;

public class StateItem {
    private String name;
    private String stateId;
    List<Branch> branchList;
    public StateItem(String name) {
        this.name=name;
        this.branchList=new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public List<Branch> getBranchList() {
        return branchList;
    }

    public void setBranchList(List<Branch> branchList) {
        this.branchList = branchList;
    }
}
