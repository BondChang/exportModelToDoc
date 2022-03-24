package diagram.entity;

import java.util.ArrayList;
import java.util.List;

public class SequenceDiagram {
    private List<String> lifeLineList;
    private List<String> messageList;

    public SequenceDiagram() {
        this.lifeLineList = new ArrayList<>();
        this.messageList = new ArrayList<>();
    }

    public List<String> getLifeLineList() {
        return lifeLineList;
    }

    public void setLifeLineList(List<String> lifeLineList) {
        this.lifeLineList = lifeLineList;
    }

    public List<String> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<String> messageList) {
        this.messageList = messageList;
    }
}
