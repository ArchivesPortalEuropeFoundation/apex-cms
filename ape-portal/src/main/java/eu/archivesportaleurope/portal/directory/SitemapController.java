package eu.archivesportaleurope.portal.directory;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.apenet.persistence.dao.ArchivalInstitutionDAO;
import eu.apenet.persistence.dao.CountryDAO;
import eu.apenet.persistence.dao.EadDAO;
import eu.apenet.persistence.dao.EadSearchOptions;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.Ead;
import eu.apenet.persistence.vo.FindingAid;
import eu.archivesportaleurope.portal.common.FriendlyUrlUtil;

@Controller(value = "SitemapController")
@RequestMapping(value = "VIEW")
public class SitemapController {
	private static final double PAGESIZE = 100;
	private static final String APPLICATION_XML = "application/xml";
	private static final String UTF8 = "UTF-8";
	private static final String SITEMAP = "http://www.sitemaps.org/schemas/sitemap/0.9";
	private static final QName SITEMAP_INDEX_ELEMENT = new QName(SITEMAP, "sitemapindex");
	private final static Logger LOGGER = Logger.getLogger(SitemapController.class);
	private static SimpleDateFormat XML_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
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

	@ResourceMapping(value = "generateGlobalSitemapIndex")
	public void generateGlobalSitemapIndex(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws Exception {
		resourceResponse.setCharacterEncoding(UTF8);
		resourceResponse.setContentType(APPLICATION_XML);
		XMLStreamWriter xmlWriter = (XMLOutputFactory.newInstance()).createXMLStreamWriter(
				resourceResponse.getPortletOutputStream(), UTF8);
		writeSitemapIndex(xmlWriter);
		List<ArchivalInstitution> archivalInstitutions = archivalInstitutionDAO
				.getArchivalInstitutionsWithoutGroupsWithSearchableItems();
		for (ArchivalInstitution archivalInstitution : archivalInstitutions) {
			xmlWriter.writeStartElement("sitemap");
			xmlWriter.writeStartElement("loc");
			xmlWriter.writeCharacters(FriendlyUrlUtil.getUrl(resourceRequest, FriendlyUrlUtil.DIRECTORY_SITEMAP) + "/ai/"
					+ archivalInstitution.getAiId());
			xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
		}
		xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		xmlWriter.flush();
		xmlWriter.close();

	}

	@ResourceMapping(value = "generateAiSitemapIndex")
	public void generateAiSitemapIndex(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws Exception {

		Integer aiId = Integer.parseInt(resourceRequest.getParameter("aiId"));
		long numberOfItems = 0;
		EadSearchOptions eadSearchOptions = new EadSearchOptions();
		eadSearchOptions.setPublished(true);
		eadSearchOptions.setEadClass(FindingAid.class);
		eadSearchOptions.setArchivalInstitionId(aiId);
		numberOfItems = eadDAO.countEads(eadSearchOptions);
		if (numberOfItems > PAGESIZE) {
			int numberOfPages = (int) Math.ceil((double) numberOfItems / PAGESIZE);
			resourceResponse.setCharacterEncoding(UTF8);
			resourceResponse.setContentType(APPLICATION_XML);
			XMLStreamWriter xmlWriter = (XMLOutputFactory.newInstance()).createXMLStreamWriter(
					resourceResponse.getPortletOutputStream(), UTF8);
			writeSitemapIndex(xmlWriter);
			for (int pageNumber = 1; pageNumber <= numberOfPages; pageNumber++) {
				xmlWriter.writeStartElement("sitemap");
				xmlWriter.writeStartElement("loc");
				xmlWriter.writeCharacters(FriendlyUrlUtil.getUrl(resourceRequest, FriendlyUrlUtil.DIRECTORY_SITEMAP)
						+ "/ai/" + aiId + "/" + pageNumber);
				xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
			}
			xmlWriter.writeEndElement();
			xmlWriter.writeEndDocument();
			xmlWriter.flush();
			xmlWriter.close();
		}else {
			generateEadContent(resourceRequest, resourceResponse,aiId, 1);
		}

	}

	@ResourceMapping(value = "generateAiSitemap")
	public void generateAiSitemap(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws Exception {
		Integer aiId = Integer.parseInt(resourceRequest.getParameter("aiId"));
		Integer pageNumber = Integer.parseInt(resourceRequest.getParameter("pageNumber"));
		generateEadContent(resourceRequest, resourceResponse,aiId, pageNumber);
	

	}

	public void generateEadContent(ResourceRequest resourceRequest, ResourceResponse resourceResponse, int aiId, int pageNumber)
			throws Exception {

		EadSearchOptions eadSearchOptions = new EadSearchOptions();
		eadSearchOptions.setPublished(true);
		eadSearchOptions.setEadClass(FindingAid.class);
		eadSearchOptions.setArchivalInstitionId(aiId);
		eadSearchOptions.setPageNumber(pageNumber);
		eadSearchOptions.setPageSize((int) PAGESIZE);
		List<Ead> eads = eadDAO.getEads(eadSearchOptions);
		if (eads.size() > 0) {
			resourceResponse.setCharacterEncoding(UTF8);
			resourceResponse.setContentType(APPLICATION_XML);
			XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(
					resourceResponse.getPortletOutputStream(), UTF8);
			writeSitemapIndex(xmlWriter);
			for (Ead ead : eads) {
				xmlWriter.writeStartElement("sitemap");
				xmlWriter.writeStartElement("loc");
				xmlWriter.writeCharacters(FriendlyUrlUtil.getUrl(resourceRequest, FriendlyUrlUtil.DIRECTORY_SITEMAP)
						+ "/ead/fa/" + ead.getId());
				xmlWriter.writeEndElement();
				if (ead.getPublishDate() != null){
					xmlWriter.writeStartElement("lastmod");
					xmlWriter.writeCharacters(XML_DATETIME_FORMAT.format(ead.getPublishDate()));
					xmlWriter.writeEndElement();	
				}
				xmlWriter.writeEndElement();
			}
			xmlWriter.writeEndElement();
			xmlWriter.writeEndDocument();
			xmlWriter.flush();
			xmlWriter.close();
		}

	}

	protected void writeSitemapIndex(XMLStreamWriter xmlWriter) throws XMLStreamException {
		if (xmlWriter != null) {
			xmlWriter.writeStartElement(SITEMAP_INDEX_ELEMENT.getPrefix(), SITEMAP_INDEX_ELEMENT.getLocalPart(),
					SITEMAP_INDEX_ELEMENT.getNamespaceURI());
			xmlWriter.writeDefaultNamespace(SITEMAP);
		}
	}
//	@ResourceMapping(value = "generateEadSitemapIndex")
//	public void generateEadSitemapIndex(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
//			throws Exception {
//		
//		Integer eadId = Integer.parseInt(resourceRequest.getParameter("id"));
//		long numberOfItems = 0;
//		EadSearchOptions eadSearchOptions = new EadSearchOptions();
//		eadSearchOptions.setPublished(true);
//		eadSearchOptions.setEadClass(FindingAid.class);
//		eadSearchOptions.setArchivalInstitionId(aiId);
//		numberOfItems = eadDAO.countEads(eadSearchOptions);
//		if (numberOfItems > PAGESIZE) {
//			int numberOfPages = (int) Math.ceil((double) numberOfItems / PAGESIZE);
//			resourceResponse.setCharacterEncoding(UTF8);
//			resourceResponse.setContentType(APPLICATION_XML);
//			XMLStreamWriter xmlWriter = (XMLOutputFactory.newInstance()).createXMLStreamWriter(
//					resourceResponse.getPortletOutputStream(), UTF8);
//			writeSitemapIndex(xmlWriter);
//			for (int pageNumber = 1; pageNumber <= numberOfPages; pageNumber++) {
//				xmlWriter.writeStartElement("sitemap");
//				xmlWriter.writeStartElement("loc");
//				xmlWriter.writeCharacters(FriendlyUrlUtil.getUrl(resourceRequest, FriendlyUrlUtil.DIRECTORY_SITEMAP)
//						+ "/ai/" + aiId + "/" + pageNumber);
//				xmlWriter.writeEndElement();
//				xmlWriter.writeEndElement();
//			}
//			xmlWriter.writeEndElement();
//			xmlWriter.writeEndDocument();
//			xmlWriter.flush();
//			xmlWriter.close();
//		}else {
//			generateEadContent(resourceRequest, resourceResponse,aiId, 1);
//		}
//
//	}
}
