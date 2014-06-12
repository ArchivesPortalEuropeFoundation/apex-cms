package eu.archivesportaleurope.portal.common.urls;

public class SitemapUrl extends AbstractUrl{

	public SitemapUrl(String repoCode) {
		super(repoCode);
	}

	@Override
	public String toString() {
		return encodeUrl(super.toString() +  getPageNumberSuffix());
	}

}
