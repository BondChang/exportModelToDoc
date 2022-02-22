package exportmodeltodoc.entity;

import java.util.ArrayList;
import java.util.List;

public class StateFlow {
   private List<StateItem> stateItemList;

    public StateFlow() {
        this.stateItemList=new ArrayList<>();
    }

    public List<StateItem> getStateItemList() {
        return stateItemList;
    }

    public void setStateItemList(List<StateItem> stateItemList) {
        this.stateItemList = stateItemList;
    }
}
