package eu.archivesportaleurope.portal.search.eag;


import eu.archivesportaleurope.portal.search.common.AbstractPortalSearcher;

public final class EagSearcher extends AbstractPortalSearcher {

	@Override
	protected String getCore() {
		return "eags";
	}
	
}
