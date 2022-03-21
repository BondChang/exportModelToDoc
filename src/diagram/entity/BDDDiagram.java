package diagram.entity;

import java.io.Serializable;
import java.util.List;

public class BDDDiagram implements Serializable {
	private String rootName;
	private List<BDDChild> childList;

	public BDDDiagram(String rootName, List<BDDChild> childList) {
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

	public List<BDDChild> getChildList() {
		return childList;
	}

	public void setChildList(List<BDDChild> childList) {
		this.childList = childList;
	}
}
