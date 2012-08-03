package eu.archivesportaleurope.portal.search.common;

import java.util.ArrayList;
import java.util.List;

import eu.apenet.commons.solr.SolrFields;
import eu.archivesportaleurope.portal.search.advanced.list.ListFacetSettings;

public enum FacetType {
	COUNTRY(SolrFields.COUNTRY, SolrFields.COUNTRY_ID,true,true,"country."),
	AI(SolrFields.AI, SolrFields.AI_ID, true),
	FOND(SolrFields.TITLE_OF_FOND, SolrFields.FOND_ID, true),
	TYPE(SolrFields.TYPE ,false, true,"advancedsearch.text."),
	DAO(SolrFields.DAO,false, true,"advancedsearch.facet.value.dao."),
	ROLEDAO(SolrFields.ROLEDAO,false, true,"advancedsearch.facet.value.roledao."),
	DATE_TYPE(SolrFields.DATE_TYPE,false, true,"advancedsearch.facet.value.datetype.");
	private final String name;
	private final String refinementField;
	private final boolean multiSelect = true;
	private final boolean hasId;
	private final boolean valueIsKey;
	private final String prefix;
	
	
	private FacetType(String name, String refinementField, boolean hasId, boolean valueIsKey, String prefix){
		this.name = name;
		this.refinementField = refinementField;
		this.hasId = hasId;
		this.valueIsKey = valueIsKey;
		this.prefix = prefix;
	}
	private FacetType(String name, boolean hasId, boolean valueIsKey, String prefix){
		this(name,name,hasId, valueIsKey, prefix);
	}
	private FacetType(String name, String refinementField, boolean hasId){
		this(name,refinementField,hasId, false, null);
	}
	public String getNameWithLabel() {
		if (multiSelect){
			return "{!ex="+ name +"}" +name;
		}else {
			return name;
		}
		
	}
	public String getRefinementFieldWithLabel() {
		if (multiSelect){
			return "{!tag=" +name + "}" + refinementField;
		}else {
			return refinementField;
		}
		
	}
	
	public String getName() {
		return name;
	}
	public String getRefinementField() {
		return refinementField;
	}
	public boolean isMultiSelect(){
		return multiSelect;
	}
    public static FacetType getFacetByName(String facetName){
        for(FacetType facet : FacetType.values()){
            if(facet.name.equals(facetName))
                return facet;
        }
        return null;
    }
    public static List<ListFacetSettings> getDefaultListFacetSettings(){
    	List<ListFacetSettings> result = new ArrayList<ListFacetSettings>();
        for(FacetType facet : FacetType.values()){
        	result.add(new ListFacetSettings(facet));
        } 	
        return result;
    }

	public boolean isHasId() {
		return hasId;
	}

	public boolean isValueIsKey() {
		return valueIsKey;
	}
	public String getPrefix() {
		return prefix;
	}
    
}
