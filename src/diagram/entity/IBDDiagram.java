package diagram.entity;

import java.util.List;

public class IBDDiagram extends BaseDiagram{
    public IBDDiagram(String rootName, List<BaseChild> childList) {
        super(rootName, childList);
    }
}
