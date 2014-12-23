package eu.archivesportaleurope.portal.display.ead.xslt;

import java.io.Writer;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

import org.apache.log4j.Logger;

import eu.apenet.commons.ResourceBundleSource;
import eu.apenet.commons.solr.SolrField;
import eu.apenet.commons.xslt.extensions.EadidCheckerExtension;
import eu.apenet.commons.xslt.extensions.HighlighterExtension;
import eu.apenet.commons.xslt.extensions.ResourcebundleExtension;
import eu.apenet.commons.xslt.extensions.SpecialCharactersEncoderExtension;
import eu.apenet.commons.xslt.extensions.TypeOfDisplayExtension;
import eu.archivesportaleurope.portal.common.xslt.ClasspathURIResolver;

public final class EadXslt {
    private static final Logger LOG = Logger.getLogger(EadXslt.class);

    
	private static XsltExecutable getXsltExecutable(String xslUrl, String searchTerms, List<SolrField> highlightFields, ResourceBundleSource resourceBundleSource, Integer aiId, boolean isDashboard, String solrStopwordsUrl, String typeOfDisplay) throws SaxonApiException{
        ClassLoader classLoader = (ClassLoader) Thread.currentThread().getContextClassLoader();
        Source xsltSource = new StreamSource(classLoader.getResourceAsStream(xslUrl));	
        Processor processor = new Processor(false);
        HighlighterExtension highLighter = new HighlighterExtension (solrStopwordsUrl, searchTerms, highlightFields);
        ResourcebundleExtension  resourcebundleRetriever = new ResourcebundleExtension (resourceBundleSource);
        EadidCheckerExtension  eadidChecker = new EadidCheckerExtension (aiId, isDashboard);
        processor.registerExtensionFunction(highLighter);
        processor.registerExtensionFunction(resourcebundleRetriever);
        processor.registerExtensionFunction(eadidChecker);
        processor.registerExtensionFunction(new SpecialCharactersEncoderExtension());
        processor.registerExtensionFunction(new TypeOfDisplayExtension(typeOfDisplay));
        XsltCompiler compiler = processor.newXsltCompiler();
        compiler.setURIResolver(new ClasspathURIResolver(xslUrl));
        return compiler.compile(xsltSource);
	}

    public static void convertEadToHtml(String xslUrl, Writer writer, Source xmlSource, String searchTerms, List<SolrField> highlightFields, ResourceBundleSource resourceBundleSource,String secondDisplayUrl, Integer aiId,boolean isDashboard, String solrStopwordsUrl, String typeOfDisplay) throws SaxonApiException{
    	XsltExecutable executable = getXsltExecutable(xslUrl, searchTerms, highlightFields,resourceBundleSource, aiId, isDashboard, solrStopwordsUrl,typeOfDisplay);
        XsltTransformer transformer = executable.load();
        transformer.setParameter(new QName("eadcontent.extref.prefix"), new XdmAtomicValue(secondDisplayUrl));
        transformer.setSource(xmlSource);
        Serializer serializer = new Serializer();
        serializer.setOutputWriter(writer);
        transformer.setDestination(serializer);
        transformer.transform();
    }
    
}
