package exportmodeltodoc.entity;

public class InstanceContent {
    /* ʵ������ */
    private String name;
    /* ʵ��ֵ */
    private String realValue;
    /* Ҫ��ֵ */
    private String requireValue;
    /* �������� */
    private String attachedName;

    public InstanceContent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealValue() {
        return realValue;
    }

    public void setRealValue(String realValue) {
        this.realValue = realValue;
    }

    public String getRequireValue() {
        return requireValue;
    }

    public void setRequireValue(String requireValue) {
        this.requireValue = requireValue;
    }

    public String getAttachedName() {
        return attachedName;
    }

    public void setAttachedName(String attachedName) {
        this.attachedName = attachedName;
    }
}
