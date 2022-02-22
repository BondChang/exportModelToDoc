package exportmodeltodoc.entity;

import java.awt.image.BufferedImage;

public class ContentElement {
	private int contentType = -1; // 1:文字�?2:图片�?3：表�?
	private String textVal = "";
	private BufferedImage mImage;
	private byte[] bImage;
	private ContentElementTable contentTb;

	public static final int textType = 1;
	public static final int imgType = 2;
	public static final int tableType = 3;

	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

	public String getTextVal() {
		return textVal;
	}

	public void setTextVal(String textVal) {
		this.textVal = textVal;
	}

	public BufferedImage getmImage() {
		return mImage;
	}

	public void setmImage(BufferedImage mImage) {
		this.mImage = mImage;
	}

	public ContentElementTable getContentTb() {
		return contentTb;
	}

	public void setContentTb(ContentElementTable contentTb) {
		this.contentTb = contentTb;
	}

	public byte[] getbImage() {
		return bImage;
	}

	public void setbImage(byte[] bImage) {
		this.bImage = bImage;
	}

}
