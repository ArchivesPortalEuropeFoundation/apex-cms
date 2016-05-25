package eu.archivesportaleurope.portal.search.eaccpf;


import eu.archivesportaleurope.portal.search.common.AbstractPortalSearcher;

public final class EacCpfSearcher extends AbstractPortalSearcher {

	@Override
	protected String getCore() {
		return "eac-cpfs";
	}
	
}
