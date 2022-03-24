package diagram.entity;

import java.io.Serializable;
import java.util.List;

public class BaseDiagram implements Serializable {
	private String rootName;
	private List<BaseChild> childList;

	public BaseDiagram(String rootName, List<BaseChild> childList) {
		super();
		this.rootName = rootName;
		this.childList = childList;
	}

	public String getRootName() {
		return rootName;
	}

	public void setRootName(String rootName) {
		this.rootName = rootName;
	}

	public List<BaseChild> getChildList() {
		return childList;
	}

	public void setChildList(List<BaseChild> childList) {
		this.childList = childList;
	}
}
