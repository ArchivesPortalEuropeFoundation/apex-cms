package eu.archivesportaleurope.portal.search.common;


import org.apache.log4j.Logger;

import eu.apenet.commons.utils.APEnetUtilities;

public final class EacCpfSearcher extends AbstractSearcher {



	private final static Logger LOGGER = Logger.getLogger(EacCpfSearcher.class);

	
	@Override
	protected String getSolrSearchUrl() {
		return APEnetUtilities.getApePortalConfig().getBaseSolrSearchUrl() + "/eac-cpfs";
	}





	
}
