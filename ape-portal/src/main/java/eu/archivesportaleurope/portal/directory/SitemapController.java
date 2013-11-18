package eu.archivesportaleurope.portal.directory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.apenet.commons.types.XmlType;
import eu.apenet.persistence.dao.ArchivalInstitutionDAO;
import eu.apenet.persistence.dao.CLevelDAO;
import eu.apenet.persistence.dao.EadDAO;
import eu.apenet.persistence.dao.EadSearchOptions;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.CLevel;
import eu.apenet.persistence.vo.Ead;
import eu.apenet.persistence.vo.FindingAid;
import eu.archivesportaleurope.portal.common.FriendlyUrlUtil;

@Controller(value = "SitemapController")
@RequestMapping(value = "VIEW")
public class SitemapController {
	private static final String PRIORITY = "priority";
	private static final String URL = "url";
	private static final String LASTMOD = "lastmod";
	private static final String LOC = "loc";
	private static final String SITEMAP = "sitemap";
	private static final double PAGESIZE = 100;
	private static final String APPLICATION_XML = "application/xml";
	private static final String UTF8 = "UTF-8";
	private static final String SITEMAP_NAMESPACE = "http://www.sitemaps.org/schemas/sitemap/0.9";
	private static final QName SITEMAP_INDEX_ELEMENT = new QName(SITEMAP_NAMESPACE, "sitemapindex");
	private static final QName URLSET_ELEMENT = new QName(SITEMAP_NAMESPACE, "urlset");
	private final static Logger LOGGER = Logger.getLogger(SitemapController.class);
	private static SimpleDateFormat XML_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private ArchivalInstitutionDAO archivalInstitutionDAO;
	private EadDAO eadDAO;
	private CLevelDAO cLevelDAO;

	public void setArchivalInstitutionDAO(ArchivalInstitutionDAO archivalInstitutionDAO) {
		this.archivalInstitutionDAO = archivalInstitutionDAO;
	}

	public void setEadDAO(EadDAO eadDAO) {
		this.eadDAO = eadDAO;
	}


	public void setCLevelDAO(CLevelDAO cLevelDAO) {
		this.cLevelDAO = cLevelDAO;
	}

	@ResourceMapping(value = "generateGlobalSitemapIndex")
	public void generateGlobalSitemapIndex(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws Exception {
		resourceResponse.setCharacterEncoding(UTF8);
		resourceResponse.setContentType(APPLICATION_XML);
		XMLStreamWriter xmlWriter = (XMLOutputFactory.newInstance()).createXMLStreamWriter(
				resourceResponse.getPortletOutputStream(), UTF8);
		writeIndexStartElement(xmlWriter);
		List<ArchivalInstitution> archivalInstitutions = archivalInstitutionDAO
				.getArchivalInstitutionsWithoutGroupsWithSearchableItems();
		for (ArchivalInstitution archivalInstitution : archivalInstitutions) {
			xmlWriter.writeStartElement(SITEMAP);
			xmlWriter.writeStartElement(LOC);
			xmlWriter.writeCharacters(FriendlyUrlUtil.getUrl(resourceRequest, FriendlyUrlUtil.DIRECTORY_SITEMAP) + "/ai/"
					+ archivalInstitution.getAiId());
			xmlWriter.writeEndElement();
			if (archivalInstitution.getContentLastModifiedDate() != null){
				xmlWriter.writeStartElement(LASTMOD);
				xmlWriter.writeCharacters(XML_DATETIME_FORMAT.format(archivalInstitution.getContentLastModifiedDate()));
				xmlWriter.writeEndElement();	
			}
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
		ArchivalInstitution archivalInstitution = archivalInstitutionDAO.getArchivalInstitution(aiId);
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
			writeIndexStartElement(xmlWriter);
			for (int pageNumber = 1; pageNumber <= numberOfPages; pageNumber++) {
				String url = FriendlyUrlUtil.getUrl(resourceRequest, FriendlyUrlUtil.DIRECTORY_SITEMAP)
						+ "/ai/" + aiId + "/" + pageNumber;
				writeIndexElement(xmlWriter, url, archivalInstitution.getContentLastModifiedDate());
			}
			xmlWriter.writeEndElement();
			xmlWriter.writeEndDocument();
			xmlWriter.flush();
			xmlWriter.close();
		}else {
			generateAiContent(resourceRequest, resourceResponse,aiId, 1);
		}

	}

	@ResourceMapping(value = "generateAiSitemap")
	public void generateAiSitemap(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws Exception {
		Integer aiId = Integer.parseInt(resourceRequest.getParameter("aiId"));
		Integer pageNumber = Integer.parseInt(resourceRequest.getParameter("pageNumber"));
		generateAiContent(resourceRequest, resourceResponse,aiId, pageNumber);

	}

	public void generateAiContent(ResourceRequest resourceRequest, ResourceResponse resourceResponse, int aiId, int pageNumber)
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
			writeIndexStartElement(xmlWriter);
			for (Ead ead : eads) {
				String url = FriendlyUrlUtil.getUrl(resourceRequest, FriendlyUrlUtil.DIRECTORY_SITEMAP)
						+ "/ead/" + XmlType.getEadType(ead).getResourceName() +  "/" + ead.getId();
				writeIndexElement(xmlWriter, url, ead.getPublishDate());
			}
			xmlWriter.writeEndElement();
			xmlWriter.writeEndDocument();
			xmlWriter.flush();
			xmlWriter.close();
		}else {
			resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, "404");
		}

	}


	@ResourceMapping(value = "generateEadSitemapIndex")
	public void generateEadSitemapIndex(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws Exception {
		
		Integer eadId = Integer.parseInt(resourceRequest.getParameter("id"));
		XmlType xmlType = XmlType.getTypeByResourceName(resourceRequest.getParameter("xmlTypeName"));
		long numberOfItems = 0;
		EadSearchOptions eadSearchOptions = new EadSearchOptions();
		eadSearchOptions.setPublished(true);
		eadSearchOptions.setEadClass(xmlType.getClazz());
		eadSearchOptions.setId(eadId);
		Ead ead = eadDAO.getEads(eadSearchOptions).get(0);
		numberOfItems = cLevelDAO.countCLevels(xmlType.getClazz(), eadId);
		if (numberOfItems > PAGESIZE) {
			int numberOfPages = (int) Math.ceil((double) numberOfItems / PAGESIZE);
			resourceResponse.setCharacterEncoding(UTF8);
			resourceResponse.setContentType(APPLICATION_XML);
			XMLStreamWriter xmlWriter = (XMLOutputFactory.newInstance()).createXMLStreamWriter(
					resourceResponse.getPortletOutputStream(), UTF8);
			writeIndexStartElement(xmlWriter);
			for (int pageNumber = 1; pageNumber <= numberOfPages; pageNumber++) {
				String url = FriendlyUrlUtil.getUrl(resourceRequest, FriendlyUrlUtil.DIRECTORY_SITEMAP)
						+ "/ead/" + XmlType.getEadType(ead).getResourceName() +  "/" + ead.getId() + "/" + pageNumber;
				writeIndexElement(xmlWriter, url, ead.getPublishDate());
			}
			xmlWriter.writeEndElement();
			xmlWriter.writeEndDocument();
			xmlWriter.flush();
			xmlWriter.close();
		}else {
			generateEadContent(resourceRequest, resourceResponse,xmlType, eadId, 1);
		}

	}
	@ResourceMapping(value = "generateEadSitemap")
	public void generateEadSitemap(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws Exception {
		Integer id = Integer.parseInt(resourceRequest.getParameter("id"));
		Integer pageNumber = Integer.parseInt(resourceRequest.getParameter("pageNumber"));
		XmlType xmlType = XmlType.getTypeByResourceName(resourceRequest.getParameter("xmlTypeName"));
		generateEadContent(resourceRequest, resourceResponse,xmlType, id, pageNumber);

	}

	public void generateEadContent(ResourceRequest resourceRequest, ResourceResponse resourceResponse, XmlType xmlType, int eadId, int pageNumber)
			throws Exception {
		EadSearchOptions eadSearchOptions = new EadSearchOptions();
		eadSearchOptions.setPublished(true);
		eadSearchOptions.setEadClass(xmlType.getClazz());
		eadSearchOptions.setId(eadId);
		Ead ead = eadDAO.getEads(eadSearchOptions).get(0);
		List<CLevel> clevels = cLevelDAO.getCLevels(xmlType.getClazz(), eadId, pageNumber, (int) PAGESIZE);
		if (clevels.size() > 0) {
			resourceResponse.setCharacterEncoding(UTF8);
			resourceResponse.setContentType(APPLICATION_XML);
			XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(
					resourceResponse.getPortletOutputStream(), UTF8);
			writeSitemapStartElement(xmlWriter);
			if (pageNumber == 1){
				String url = FriendlyUrlUtil.getUrl(resourceRequest, FriendlyUrlUtil.EAD_DISPLAY_FRONTPAGE) + "/" +ead.getArchivalInstitution().getRepositorycodeForUrl() + "/" + xmlType.getResourceName() + "/" + ead.getEadid();
				writeSitemapElement(xmlWriter, url, ead.getPublishDate(), "0.7");
			}
			for (CLevel cLevel : clevels) {
				String url = FriendlyUrlUtil.getUrl(resourceRequest, FriendlyUrlUtil.EAD_DISPLAY_SEARCH) + "/C"+ cLevel.getClId();
				writeSitemapElement(xmlWriter, url, ead.getPublishDate(), null);
			}
			xmlWriter.writeEndElement();
			xmlWriter.writeEndDocument();
			xmlWriter.flush();
			xmlWriter.close();
		}else {
			resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, "404");
		}

	}
	
	private static void writeIndexStartElement(XMLStreamWriter xmlWriter) throws XMLStreamException {
		if (xmlWriter != null) {
			xmlWriter.writeStartElement(SITEMAP_INDEX_ELEMENT.getPrefix(), SITEMAP_INDEX_ELEMENT.getLocalPart(),
					SITEMAP_INDEX_ELEMENT.getNamespaceURI());
			xmlWriter.writeDefaultNamespace(SITEMAP_NAMESPACE);
		}
	}
	
	private static void writeIndexElement(XMLStreamWriter xmlWriter, String url, Date lastModDate) throws XMLStreamException {
		if (xmlWriter != null) {
			xmlWriter.writeStartElement(URL);
			xmlWriter.writeStartElement(LOC);
			xmlWriter.writeCharacters(url);
			xmlWriter.writeEndElement();
			if (lastModDate != null){
				xmlWriter.writeStartElement(LASTMOD);
				xmlWriter.writeCharacters(XML_DATETIME_FORMAT.format(lastModDate));
				xmlWriter.writeEndElement();	
			}
			xmlWriter.writeEndElement();
		}
	}
	private static void writeSitemapElement(XMLStreamWriter xmlWriter, String url, Date lastModDate, String priority) throws XMLStreamException {
		if (xmlWriter != null) {
			xmlWriter.writeStartElement(SITEMAP);
			xmlWriter.writeStartElement(LOC);
			xmlWriter.writeCharacters(url);
			xmlWriter.writeEndElement();
			if (lastModDate != null){
				xmlWriter.writeStartElement(LASTMOD);
				xmlWriter.writeCharacters(XML_DATETIME_FORMAT.format(lastModDate));
				xmlWriter.writeEndElement();	
			}
			if (priority != null){
				xmlWriter.writeStartElement(PRIORITY);
				xmlWriter.writeCharacters(priority);
				xmlWriter.writeEndElement();	
			}
			xmlWriter.writeEndElement();
		}
	}
	private static void writeSitemapStartElement(XMLStreamWriter xmlWriter) throws XMLStreamException {
		if (xmlWriter != null) {
			xmlWriter.writeStartElement(URLSET_ELEMENT.getPrefix(), URLSET_ELEMENT.getLocalPart(),
					URLSET_ELEMENT.getNamespaceURI());
			xmlWriter.writeDefaultNamespace(SITEMAP_NAMESPACE);
		}
	}	
	
}
