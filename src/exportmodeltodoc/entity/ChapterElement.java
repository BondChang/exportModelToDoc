package exportmodeltodoc.entity;

import java.util.ArrayList;
import java.util.List;

public class ChapterElement {
	private String title = "";
	private List<ContentElement> contents = new ArrayList<ContentElement>();
	private List<ChapterElement> children = new ArrayList<ChapterElement>();

	public List<ContentElement> getContents() {
		return contents;
	}

	public List<ChapterElement> getChildren() {
		return children;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
