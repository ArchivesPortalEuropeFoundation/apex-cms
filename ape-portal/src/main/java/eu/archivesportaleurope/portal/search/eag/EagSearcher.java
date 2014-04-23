package eu.archivesportaleurope.portal.search.eag;


import eu.apenet.commons.utils.APEnetUtilities;
import eu.archivesportaleurope.portal.search.common.AbstractSearcher;

public final class EagSearcher extends AbstractSearcher {

	@Override
	protected String getSolrSearchUrl() {
		return APEnetUtilities.getApePortalConfig().getBaseSolrSearchUrl() + "/eags";
	}
	
}
