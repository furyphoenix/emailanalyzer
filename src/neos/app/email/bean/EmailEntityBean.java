package neos.app.email.bean;

public class EmailEntityBean {
	EntityCatalogueBean entityCatalogues;
	GraphBean entityGraph;
	
	public EmailEntityBean(){
		
	}

	public EntityCatalogueBean getEntityCatalogues() {
		return entityCatalogues;
	}

	public void setEntityCatalogues(EntityCatalogueBean entityCatalogues) {
		this.entityCatalogues = entityCatalogues;
	}

	public GraphBean getEntityGraph() {
		return entityGraph;
	}

	public void setEntityGraph(GraphBean entityGraph) {
		this.entityGraph = entityGraph;
	}
	
	
}
