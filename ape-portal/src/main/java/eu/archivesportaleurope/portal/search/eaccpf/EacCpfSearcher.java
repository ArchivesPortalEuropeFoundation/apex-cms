package eu.archivesportaleurope.portal.search.eaccpf;


import eu.archivesportaleurope.portal.search.common.AbstractSearcher;

public final class EacCpfSearcher extends AbstractSearcher {

	@Override
	protected String getCore() {
		return "eac-cpfs";
	}
	
}
