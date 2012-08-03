package eu.archivesportaleurope.portal.search.common;

import eu.apenet.commons.solr.SolrFields;

public enum Facet {
	COUNTRY(SolrFields.COUNTRY, SolrFields.COUNTRY_ID, SolrFields.COUNTRY),
	AI(SolrFields.AI, SolrFields.AI_ID, SolrFields.AI),
	TYPE(SolrFields.TYPE, SolrFields.TYPE, SolrFields.TYPE),
	DATE_TYPE(SolrFields.DATE_TYPE, SolrFields.DATE_TYPE, SolrFields.DATE_TYPE),
	ROLEDAO(SolrFields.ROLEDAO, SolrFields.ROLEDAO, SolrFields.ROLEDAO),
	DAO(SolrFields.DAO, SolrFields.DAO, SolrFields.DAO),
	FOND(SolrFields.TITLE_OF_FOND, SolrFields.FOND_ID, SolrFields.TITLE_OF_FOND)
	;
	
	private final String facetField;
	private final String refinementField;
	private final String label;

	private Facet(String facetField, String refinementField, String label){
		this.facetField = facetField;
		this.refinementField = refinementField;
		this.label = label;
	}
	public String getFacetFieldWithLabel() {
		return "{!ex="+ label +"}" +facetField;
	}
	public String getRefinementFieldWithLabel() {
		return "{!tag=" +label + "}" + refinementField;
	}
	
	public String getFacetField() {
		return facetField;
	}
	public String getRefinementField() {
		return refinementField;
	}
	public boolean isMultiSelect(){
		return label != null;
	}
    public static Facet getFacetByName(String facetName){
        for(Facet facet : Facet.values()){
            if(facet.facetField.equals(facetName))
                return facet;
        }
        return null;
    }
}
