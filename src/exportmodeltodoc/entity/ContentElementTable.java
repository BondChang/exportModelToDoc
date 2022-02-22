package exportmodeltodoc.entity;

import java.util.ArrayList;
import java.util.List;

public class ContentElementTable {
	private int colNum = 0;
	private List<ContentElementRow> contents = new ArrayList<ContentElementRow>();

	public List<ContentElementRow> getContents() {
		return contents;
	}

	public void setContents(List<ContentElementRow> contents) {
		this.contents = contents;
	}

	public int getColNum() {
		return colNum;
	}

	public void setColNum(int colNum) {
		this.colNum = colNum;
	}

}
