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
	LEVEL(SolrFields.LEVEL,false, true,"advancedsearch.facet.value.level."),
	DAO(SolrFields.DAO,false, true,"advancedsearch.facet.value.dao."),
	ROLEDAO(SolrFields.ROLEDAO,false, true,"advancedsearch.facet.value.roledao."),
	DATE_TYPE(SolrFields.DATE_TYPE,false, true,"advancedsearch.facet.value.datetype."),
	START_DATE(SolrFields.START_DATE, true),
	END_DATE(SolrFields.END_DATE, true),
	EAC_CPF_PLACES(SolrFields.EAC_CPF_FACET_PLACES,false),
	EAC_CPF_OCCUPATION(SolrFields.EAC_CPF_FACET_OCCUPATION,false);;
	private final String name;
	private final String refinementField;
	private final boolean multiSelect;
	private final boolean hasId;
	private final boolean valueIsKey;
	private final String prefix;
	private final boolean date;
	
	private FacetType(String name, boolean isDateType){
		this.name = name;
		this.refinementField = name;
		this.hasId = false;
		this.valueIsKey = false;
		this.prefix = null;
		this.date = isDateType;
		if (date){
			this.multiSelect = false;
		}else {
			this.multiSelect = true;
		}
	}
	
	private FacetType(String name, String refinementField, boolean hasId, boolean valueIsKey, String prefix){
		this.name = name;
		this.refinementField = refinementField;
		this.hasId = hasId;
		this.valueIsKey = valueIsKey;
		this.prefix = prefix;
		this.date = false;
		this.multiSelect = true;
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
    public static List<ListFacetSettings> getDefaultEadListFacetSettings(){
    	List<ListFacetSettings> result = new ArrayList<ListFacetSettings>();
    	result.add(new ListFacetSettings(FacetType.COUNTRY));
    	result.add(new ListFacetSettings(FacetType.AI));
    	result.add(new ListFacetSettings(FacetType.TYPE));
    	result.add(new ListFacetSettings(FacetType.DAO));
    	result.add(new ListFacetSettings(FacetType.ROLEDAO));
    	result.add(new ListFacetSettings(FacetType.DATE_TYPE));    	
    	result.add(new ListFacetSettings(FacetType.START_DATE));
    	result.add(new ListFacetSettings(FacetType.END_DATE));     	
        return result;
    }
    public static List<ListFacetSettings> getDefaultEacCPfListFacetSettings(){
    	List<ListFacetSettings> result = new ArrayList<ListFacetSettings>();
    	result.add(new ListFacetSettings(FacetType.COUNTRY));
    	result.add(new ListFacetSettings(FacetType.AI));    	
    	result.add(new ListFacetSettings(FacetType.EAC_CPF_PLACES));	
    	result.add(new ListFacetSettings(FacetType.EAC_CPF_OCCUPATION));	
    	result.add(new ListFacetSettings(FacetType.DATE_TYPE));  
    	result.add(new ListFacetSettings(FacetType.START_DATE));
    	result.add(new ListFacetSettings(FacetType.END_DATE));      	
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

	public boolean isDate() {
		return date;
	}
    
}
