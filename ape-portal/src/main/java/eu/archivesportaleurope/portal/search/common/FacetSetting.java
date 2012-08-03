package eu.archivesportaleurope.portal.search.common;

public class FacetSetting {

	private final Facet facet;
	private final int limit;
	private final boolean expanded;
	public FacetSetting(String inputString){
		String[] values = inputString.split("|");
		String facetName = values[0];
		String limit = values[1];
		String expanded = values[2];
		facet = Facet.getFacetByName(facetName);
		this.limit = Integer.parseInt(limit);
		this.expanded = "true".equalsIgnoreCase(expanded);
		
	}
	public Facet getFacet() {
		return facet;
	}
	public int getLimit() {
		return limit;
	}
	public boolean isExpanded() {
		return expanded;
	}

	
}
