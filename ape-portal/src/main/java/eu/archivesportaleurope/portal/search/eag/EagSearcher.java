package eu.archivesportaleurope.portal.search.eag;


import eu.archivesportaleurope.portal.search.common.AbstractSearcher;

public final class EagSearcher extends AbstractSearcher {

	@Override
	protected String getCore() {
		return "eags";
	}
	
}
