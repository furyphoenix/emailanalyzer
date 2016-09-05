package neos.app.email.bean;

public class EmailAttachmentMD5Bean {
	private String fileName;
	private String fileMd5;
	private long fileLen;
	private int cnt;

	public EmailAttachmentMD5Bean() {

	}

	public EmailAttachmentMD5Bean(String fileName, String fileMd5,
			long fileLen, int cnt) {
		this.fileName = fileName;
		this.fileMd5 = fileMd5;
		this.fileLen = fileLen;
		this.cnt = cnt;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileMd5() {
		return fileMd5;
	}

	public void setFileMd5(String fileMd5) {
		this.fileMd5 = fileMd5;
	}

	public long getFileLen() {
		return fileLen;
	}

	public void setFileLen(long fileLen) {
		this.fileLen = fileLen;
	}

	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}

}
