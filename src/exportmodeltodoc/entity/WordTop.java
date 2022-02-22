package exportmodeltodoc.entity;

import java.util.ArrayList;
import java.util.List;

public class WordTop {
	private List<ChapterElement> chapterElements = new ArrayList<ChapterElement>();
	private String modelId = "";

	public List<ChapterElement> getChapterElements() {
		return chapterElements;
	}

	public void setChapterElements(List<ChapterElement> chapterElements) {
		this.chapterElements = chapterElements;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}
}
