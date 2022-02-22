package diagram.entity;

import java.util.List;

public class BDDDiagram {
	private String rootName;
	private List<String> childList;

	public BDDDiagram(String rootName, List<String> childList) {
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

	public List<String> getChildList() {
		return childList;
	}

	public void setChildList(List<String> childList) {
		this.childList = childList;
	}

}
