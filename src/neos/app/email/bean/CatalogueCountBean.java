package neos.app.email.bean;

/**
 * 类别统计值Bean
 * 
 * @author phoenix
 * 
 */

public class CatalogueCountBean {
	private String catalogue;
	private int count;

	public CatalogueCountBean() {

	}
	
	public CatalogueCountBean(String catalogue, int count){
		this.catalogue=catalogue;
		this.count=count;
	}

	/**
	 * 获取类别名
	 * 
	 * @return 类别名称
	 */
	public String getCatalogue() {
		return catalogue;
	}

	/**
	 * 设置类别名
	 * 
	 * @param catalogue
	 *            类别名称
	 */
	public void setCatalogue(String catalogue) {
		this.catalogue = catalogue;
	}

	/**
	 * 获取统计值
	 * 
	 * @return 统计值
	 */
	public int getCount() {
		return count;
	}

	/**
	 * 设置统计值
	 * 
	 * @param count
	 *            统计值
	 */
	public void setCount(int count) {
		this.count = count;
	}

}
