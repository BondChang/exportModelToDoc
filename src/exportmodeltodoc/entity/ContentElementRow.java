package exportmodeltodoc.entity;

import java.util.ArrayList;
import java.util.List;

public class ContentElementRow {
	private List<String> rowdata = new ArrayList<String>();

	public List<String> getRowdata() {
		return rowdata;
	}

	public void setRowdata(List<String> rowdata) {
		this.rowdata = rowdata;
	}

	public String getValueByColNum(int colnum) {
		if (colnum > rowdata.size()) {
			return "";
		} else {
			return rowdata.get(colnum - 1);
		}

	}

}
