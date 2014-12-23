package eu.archivesportaleurope.portal.common.xslt;

import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

public class ClasspathURIResolver implements URIResolver {
	
	private static final String SEPARATOR = "/";
	private String baseDir;
	
	public ClasspathURIResolver(String xslUrl){
		int lastSeparator = xslUrl.lastIndexOf(SEPARATOR);
		baseDir = xslUrl.substring(0,lastSeparator+1);
	}
	
	@Override
	public Source resolve(String href, String base) throws TransformerException {
		ClassLoader cl = this.getClass().getClassLoader();
		InputStream in = cl.getResourceAsStream(baseDir + href);
		return new StreamSource(in);
	}

}
