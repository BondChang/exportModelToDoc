package exportmodeltodoc.entity;

import diagram.entity.DiagramInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WordElement implements Cloneable, Serializable {
    private static final long serialVersionUID = 1l;
    private String elementName;
    private String elementRequireValue;
    private String elementRealValue;
    private String elementAttachedName;
    private int elementType;
    private int wide;
    private String qualifiedName;
    private boolean select;
    private List<WordElement> wordElementList;
    private transient List<DiagramInfo> diagramInfoList;
    private boolean hasInstance;

    public java.lang.String getElementName() {
        return elementName;
    }

    public void setElementName(java.lang.String elementName) {
        this.elementName = elementName;
    }



    public int getWide() {
        return wide;
    }

    public void setWide(int wide) {
        this.wide = wide;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public List<WordElement> getWordElementList() {
        return wordElementList;
    }

    public void setWordElementList(List<WordElement> wordElementList) {
        this.wordElementList = wordElementList;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public void addWordElement(WordElement wordElement) {
        if (wordElementList == null) {
            wordElementList = new ArrayList<>();
        }
        wordElementList.add(wordElement);
    }

    @Override
    public Object clone() {
        WordElement wordElement = null;
        try {
            wordElement = (WordElement) super.clone();

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return wordElement;
    }

    public String getElementRequireValue() {
        return elementRequireValue;
    }

    public void setElementRequireValue(String elementRequireValue) {
        this.elementRequireValue = elementRequireValue;
    }

    public String getElementRealValue() {
        return elementRealValue;
    }

    public void setElementRealValue(String elementRealValue) {
        this.elementRealValue = elementRealValue;
    }

    public String getElementAttachedName() {
        return elementAttachedName;
    }

    public void setElementAttachedName(String elementAttachedName) {
        this.elementAttachedName = elementAttachedName;
    }

    public void addDiagramInfo(DiagramInfo diagramInfo) {
        if (diagramInfoList == null) {
            diagramInfoList = new ArrayList<>();
        }
        diagramInfoList.add(diagramInfo);
    }

    public List<DiagramInfo> getDiagramInfoList() {
        return diagramInfoList;
    }

    public void setDiagramInfoList(List<DiagramInfo> diagramInfoList) {
        this.diagramInfoList = diagramInfoList;
    }

    @Override
    public String toString() {
        return elementName;
    }

    public int getElementType() {
        return elementType;
    }

    public void setElementType(int elementType) {
        this.elementType = elementType;
    }

    public boolean isHasInstance() {
        return hasInstance;
    }

    public void setHasInstance(boolean hasInstance) {
        this.hasInstance = hasInstance;
    }
}
