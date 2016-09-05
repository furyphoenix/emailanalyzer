package neos.app.util;

public class MimeUtil {
	/**
	 * 文件类型
	 * @author phoenix
	 *
	 */
	public enum MimeType{
		/**
		 * 文本文档
		 */
		DOCUMENT,
		
		/**
		 * 设计
		 */
		DESIGN,
		
		/**
		 * 图像
		 */
		IMAGE,
		
		/**
		 * 音频
		 */
		AUDIO,
		
		/**
		 * 视频
		 */
		VIDEO,
		
		/**
		 * 可执行程序
		 */
		APPLICATION,
		
		/**
		 * 源代码
		 */
		SOURCECODE,
		
		/**
		 * 压缩包
		 */
		ACHIEVE,
		
		/**
		 * 一般
		 */
		GENERAL
	};
	
	private final static String[] documentExts={"txt","rtf","doc","docx","ppt","pptx",""};
}
