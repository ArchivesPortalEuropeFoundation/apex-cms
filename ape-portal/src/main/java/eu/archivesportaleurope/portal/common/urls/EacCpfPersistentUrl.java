package eu.archivesportaleurope.portal.common.urls;

import org.apache.commons.lang.StringUtils;

import eu.apenet.commons.types.XmlType;
import eu.archivesportaleurope.util.ApeUtil;

public class EacCpfPersistentUrl extends AbstractContentUrl{


	public static final String PARAMETER_RELATION = "/relation/";

	private String relation;



	public EacCpfPersistentUrl(String repoCode, String identifier){
		super(repoCode, XmlType.EAC_CPF.getResourceName(), identifier);
	}



	public void setRelation(String relation) {
		this.relation = relation;
	}



	@Override
	public String toString() {
		String url= super.toString();
		if (StringUtils.isNotBlank(relation)) {
			url+= PARAMETER_RELATION+ ApeUtil.encodeSpecialCharacters(relation);
		}
		url += this.getSearchSuffix();

		return url;
	}

	
}
