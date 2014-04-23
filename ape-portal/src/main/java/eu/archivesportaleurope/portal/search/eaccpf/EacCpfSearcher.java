package eu.archivesportaleurope.portal.search.eaccpf;


import eu.apenet.commons.utils.APEnetUtilities;
import eu.archivesportaleurope.portal.search.common.AbstractSearcher;

public final class EacCpfSearcher extends AbstractSearcher {

	@Override
	protected String getSolrSearchUrl() {
		return APEnetUtilities.getApePortalConfig().getBaseSolrSearchUrl() + "/eac-cpfs";
	}
	
}
