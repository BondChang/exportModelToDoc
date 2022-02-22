package diagram.entity;

import java.util.ArrayList;
import java.util.List;

public class Lane {
    private String laneName;
    private List<String> nodeList;

    public Lane(String laneName) {
        this.laneName = laneName;
        this.nodeList = new ArrayList<>();
    }

    public String getLaneName() {
        return laneName;
    }

    public void setLaneName(String laneName) {
        this.laneName = laneName;
    }

    public List<String> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<String> nodeList) {
        this.nodeList = nodeList;
    }
}
