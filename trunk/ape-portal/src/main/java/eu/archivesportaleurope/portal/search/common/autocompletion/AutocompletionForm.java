package eu.archivesportaleurope.portal.search.common.autocompletion;

import eu.apenet.commons.solr.facet.ListFacetSettings;
import java.util.List;

import eu.archivesportaleurope.portal.search.common.AbstractSearchForm;

public class AutocompletionForm extends AbstractSearchForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8803394179924516731L;
	public static final String EAD = "ead";
	public static final String EAC_CPF = "eaccpf";
	public static final String EAG = "eag";
	private String sourceType;
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	@Override
	protected List<ListFacetSettings> getDefaultListFacetSettings() {
		return null;
	}
	
}
