package diagram.entity;

import java.io.Serializable;

public class ParaDiagram implements Serializable {
	private String name;
	private String condition;

	public ParaDiagram(String name, String condition) {
		super();
		this.name = name;
		this.condition = condition;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

}
