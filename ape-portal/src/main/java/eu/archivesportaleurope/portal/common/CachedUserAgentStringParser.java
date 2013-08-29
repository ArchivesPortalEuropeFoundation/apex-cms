package eu.archivesportaleurope.portal.common;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;
import eu.apenet.commons.utils.Cache;

public final class CachedUserAgentStringParser implements UserAgentStringParser {

	private final UserAgentStringParser parser = UADetectorServiceFactory.getCachingAndUpdatingParser();
	private final Cache<String, ReadableUserAgent> cache = new Cache<String, ReadableUserAgent>();

	private static CachedUserAgentStringParser instance;
	private CachedUserAgentStringParser(){
		
	}
	public static CachedUserAgentStringParser getInstance(){
		if (instance ==null){
			instance = new CachedUserAgentStringParser();
		}
		return instance;
	}
	@Override
	public String getDataVersion() {
		return parser.getDataVersion();
	}

	@Override
	public ReadableUserAgent parse(final String userAgentString) {
		ReadableUserAgent result = cache.get(userAgentString);
		if (result == null) {
			result = parser.parse(userAgentString);
			cache.put(userAgentString, result);
		}
		return result;
	}

	@Override
	public void shutdown() {
		parser.shutdown();
	}

}