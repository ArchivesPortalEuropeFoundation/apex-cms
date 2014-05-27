package eu.archivesportaleurope.portal.common.urls;

import org.apache.commons.lang.StringUtils;

import eu.apenet.commons.solr.SolrValues;
import eu.apenet.persistence.vo.CLevel;
import eu.archivesportaleurope.util.ApeUtil;

public class EadPersistentUrl extends AbstractContentUrl{


	public static final String PARAMETER_UNITID = "/unitid/";
	public static final String PARAMETER_NPID = "/dbid/";

	private String unitid;
	private String searchId;



	public EadPersistentUrl(String repoCode, String xmlTypeName, String identifier){
		super(repoCode, xmlTypeName, identifier);
	}
	public void setClevel(CLevel cLevel) {
		if (cLevel != null){
			setSearchIdAsLong(cLevel.getClId());
			if (cLevel.isDuplicateUnitid()){
				setUnitid(null);
			}else {
				setUnitid(cLevel.getUnitid());
			}
		}
	}

	public void setUnitid(String unitid) {
		this.unitid = unitid;
	}

	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}
	public void setSearchIdAsLong(Long searchId) {
		if (searchId != null){
			this.searchId = SolrValues.C_LEVEL_PREFIX + searchId;
		}
	}



	@Override
	public String toString(){
		String url= super.toString();
		if (StringUtils.isBlank(unitid)) {
			if (StringUtils.isNotBlank(searchId)) {
				String solrPrefix = searchId.substring(0, 1);
				if (SolrValues.C_LEVEL_PREFIX.equals(solrPrefix)) {
					url+= PARAMETER_NPID + searchId;
				}
			}
		}else {
			url+= PARAMETER_UNITID+ ApeUtil.encodeSpecialCharacters(unitid);
		}

		url += this.getPageNumberSuffix();
		url += this.getSearchSuffix();
		return url;
	}

	public boolean isPersistent(){
		return !(StringUtils.isBlank(unitid) && StringUtils.isNotBlank(searchId));
	}
}
