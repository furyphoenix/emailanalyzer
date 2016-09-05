package neos.app.email.bean;

/**
 * 要素信息统计BEAN
 * @author phoenix
 *
 */
public class EntityCountBean {
	private String entity;
	private int count;
	private int emailCount;
	
	public EntityCountBean(String entity, int count, int emailCount){
		this.entity=entity;
		this.count=count;
		this.emailCount=emailCount;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getEmailCount() {
		return emailCount;
	}

	public void setEmailCount(int emailCount) {
		this.emailCount = emailCount;
	}
	
	public void increaseCount(){
		this.count++;
	}
	
	public void increaseEmailCount(){
		this.emailCount++;
	}
	
	@Override
	public int hashCode(){
		return entity.hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof EntityCountBean)){
			return false;
		}
		
		EntityCountBean ecb=(EntityCountBean)o;
		return entity.equals(ecb.entity);
	}
	
	
}
