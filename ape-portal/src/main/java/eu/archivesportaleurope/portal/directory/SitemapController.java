package eu.archivesportaleurope.portal.directory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.apenet.commons.exceptions.APEnetException;
import eu.apenet.persistence.dao.ArchivalInstitutionDAO;
import eu.apenet.persistence.dao.CountryDAO;
import eu.apenet.persistence.dao.EadDAO;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.archivesportaleurope.portal.common.FriendlyUrlUtil;

@Controller(value = "SitemapController")
@RequestMapping(value = "VIEW")
public class SitemapController {
	private static final String APPLICATION_XML = "application/xml";
	private static final String UTF8 = "UTF-8";
	private static final String SITEMAP = "http://www.sitemaps.org/schemas/sitemap/0.9";
	private static final QName SITEMAP_INDEX_ELEMENT = new QName(SITEMAP, "sitemapindex");
	private final static Logger LOGGER = Logger.getLogger(SitemapController.class);
	private ArchivalInstitutionDAO archivalInstitutionDAO;
	private EadDAO eadDAO;
	private CountryDAO countryDAO;
	private MessageSource messageSource;
	public void setArchivalInstitutionDAO(ArchivalInstitutionDAO archivalInstitutionDAO) {
		this.archivalInstitutionDAO = archivalInstitutionDAO;
	}

	public void setEadDAO(EadDAO eadDAO) {
		this.eadDAO = eadDAO;
	}
	

	public void setCountryDAO(CountryDAO countryDAO) {
		this.countryDAO = countryDAO;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}


	@ResourceMapping(value = "generateIndexSitemap")
	public void generateIndexSitemap(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws APEnetException, XMLStreamException, FactoryConfigurationError, IOException {
			resourceResponse.setCharacterEncoding(UTF8);
			resourceResponse.setContentType(APPLICATION_XML);
	    	XMLStreamWriter xmlWriter = (XMLOutputFactory.newInstance()).createXMLStreamWriter(resourceResponse.getPortletOutputStream(), UTF8);
	    	writeSitemapIndex(xmlWriter);
	    	List<ArchivalInstitution> archivalInstitutions = archivalInstitutionDAO.getArchivalInstitutionsWithoutGroupsWithSearchableItems();
	    	for (ArchivalInstitution archivalInstitution: archivalInstitutions){
	    		xmlWriter.writeStartElement("sitemap");
	    		xmlWriter.writeStartElement("loc");
	    		xmlWriter.writeCharacters(FriendlyUrlUtil.getUrl(resourceRequest, FriendlyUrlUtil.DIRECTORY_SITEMAP)+"/"+archivalInstitution.getRepositorycodeForUrl());
	    		xmlWriter.writeEndElement();
	    		xmlWriter.writeEndElement();
	    	}
	    	xmlWriter.writeEndElement();
	    	xmlWriter.writeEndDocument();
	    	xmlWriter.flush();
	    	xmlWriter.close();

	}
    protected void writeSitemapIndex(XMLStreamWriter xmlWriter) throws XMLStreamException {
		if (xmlWriter != null) {
			xmlWriter.writeStartElement(SITEMAP_INDEX_ELEMENT.getPrefix(), SITEMAP_INDEX_ELEMENT.getLocalPart(), SITEMAP_INDEX_ELEMENT.getNamespaceURI());
			xmlWriter.writeDefaultNamespace(SITEMAP);
		}
	}
}
