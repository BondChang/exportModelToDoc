package diagram.entity;

public class BDDChild {
    private String name;
    private String childName;
    private String type;

    public BDDChild(String name, String childName) {
        this.name = name;
        this.childName = childName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
