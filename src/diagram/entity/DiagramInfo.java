package diagram.entity;

import exportmodeltodoc.entity.StateFlow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DiagramInfo implements Serializable {
    private String diagramName;
    private int diagramType;
    private int wide;
    private List<String> diagramComposeList;
    private List<DiagramInfo> subDiagramList;
    private Object diagramEntity;
    private List<ReqDiagram> reqDiagram;
    private BaseDiagram bddDiagram;
    private List<ParaDiagram> paraDiagramList;
    private StateFlow stateFlow;
    private List<Lane> activityLaneList;
    private SequenceDiagram sequenceDiagram;

    public DiagramInfo() {
        this.diagramComposeList = new ArrayList<String>();
        this.subDiagramList = new ArrayList<DiagramInfo>();
        this.paraDiagramList = new ArrayList<ParaDiagram>();
        this.activityLaneList = new ArrayList<>();
    }

    public String getDiagramName() {
        return diagramName;
    }

    public void setDiagramName(String diagramName) {
        this.diagramName = diagramName;
    }

    public int getDiagramType() {
        return diagramType;
    }

    public void setDiagramType(int diagramType) {
        this.diagramType = diagramType;
    }

    public List<String> getDiagramComposeList() {
        return diagramComposeList;
    }

    public void setDiagramComposeList(List<String> diagramComposeList) {
        this.diagramComposeList = diagramComposeList;
    }

    public List<DiagramInfo> getSubDiagramList() {
        return subDiagramList;
    }

    public void setSubDiagramList(List<DiagramInfo> subDiagramList) {
        this.subDiagramList = subDiagramList;
    }

    public Object getDiagramEntity() {
        return diagramEntity;
    }

    public void setDiagramEntity(Object diagramEntity) {
        this.diagramEntity = diagramEntity;
    }

    public List<ReqDiagram> getReqDiagram() {
        return reqDiagram;
    }

    public void setReqDiagram(List<ReqDiagram> reqDiagram) {
        this.reqDiagram = reqDiagram;
    }

    public int getWide() {
        return wide;
    }

    public void setWide(int wide) {
        this.wide = wide;
    }

    public BaseDiagram getBddDiagram() {
        return bddDiagram;
    }

    public void setBddDiagram(BaseDiagram bddDiagram) {
        this.bddDiagram = bddDiagram;
    }

    public List<ParaDiagram> getParaDiagramList() {
        return paraDiagramList;
    }

    public void setParaDiagramList(List<ParaDiagram> paraDiagramList) {
        this.paraDiagramList = paraDiagramList;
    }

    public StateFlow getStateFlow() {
        return stateFlow;
    }

    public void setStateFlow(StateFlow stateFlow) {
        this.stateFlow = stateFlow;
    }

    public List<Lane> getActivityLaneList() {
        return activityLaneList;
    }

    public void setActivityLaneList(List<Lane> activityLaneList) {
        this.activityLaneList = activityLaneList;
    }

    public SequenceDiagram getSequenceDiagram() {
        return sequenceDiagram;
    }

    public void setSequenceDiagram(SequenceDiagram sequenceDiagram) {
        this.sequenceDiagram = sequenceDiagram;
    }
}
