package exportmodeltodoc.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StateItem implements Serializable {
    private String name;
    private String stateId;
    private String type;
    private boolean edit;
    private String typeName;
    List<Branch> branchList;

    public StateItem(String name) {
        this.name = name;
        this.branchList = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
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

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
