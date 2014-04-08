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

import net.sf.uadetector.ReadableUserAgent;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.apenet.commons.types.XmlType;
import eu.apenet.persistence.dao.ArchivalInstitutionDAO;
import eu.apenet.persistence.dao.CLevelDAO;
import eu.apenet.persistence.dao.EadDAO;
import eu.apenet.persistence.dao.ContentSearchOptions;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.CLevel;
import eu.apenet.persistence.vo.Ead;
import eu.apenet.persistence.vo.FindingAid;
import eu.apenet.persistence.vo.HoldingsGuide;
import eu.apenet.persistence.vo.SourceGuide;
import eu.archivesportaleurope.portal.common.FriendlyUrlUtil;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;

@Controller(value = "SitemapController")
@RequestMapping(value = "VIEW")
public class SitemapController {
	private static final String HTTP_NOT_FOUND = "404";
	private static final String EAD = "/ead/";
	private static final String SEPARATOR = "/";
	private static final String AI = "/ai/";
	private static final String PRIORITY = "priority";
	private static final String URL = "url";
	private static final String LASTMOD = "lastmod";
	private static final String LOC = "loc";
	private static final String SITEMAP = "sitemap";
	private static final double PAGESIZE = 10000;
	private static final String APPLICATION_XML = "application/xml";
	private static final String UTF8 = "UTF-8";
	private static final String SITEMAP_NAMESPACE = "http://www.sitemaps.org/schemas/sitemap/0.9";
	private static final QName SITEMAP_INDEX_ELEMENT = new QName(SITEMAP_NAMESPACE, "sitemapindex");
	private static final QName URLSET_ELEMENT = new QName(SITEMAP_NAMESPACE, "urlset");
	private final static Logger LOGGER = Logger.getLogger("SITEMAP");
	private static SimpleDateFormat W3C_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'");
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
	public void generateGlobalSitemapIndex(ResourceRequest resourceRequest, ResourceResponse resourceResponse) {
		LOGGER.debug(getUserAgent(resourceRequest) + ": GLOBAL");
		try {
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
				xmlWriter.writeCharacters(FriendlyUrlUtil.getUrl(resourceRequest, FriendlyUrlUtil.DIRECTORY_SITEMAP)
						+ AI + archivalInstitution.getAiId());
				xmlWriter.writeEndElement();
				if (archivalInstitution.getContentLastModifiedDate() != null) {
					xmlWriter.writeStartElement(LASTMOD);
					xmlWriter.writeCharacters(W3C_DATETIME_FORMAT.format(archivalInstitution
							.getContentLastModifiedDate()));
					xmlWriter.writeEndElement();
				}
				xmlWriter.writeEndElement();
			}
			xmlWriter.writeEndElement();
			xmlWriter.writeEndDocument();
			xmlWriter.flush();
			xmlWriter.close();
		} catch (Exception e) {
			LOGGER.error("Unable to generate global sitemap: " + e.getMessage());
			resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, HTTP_NOT_FOUND);
		}
	}

	@ResourceMapping(value = "generateAiSitemapIndex")
	public void generateAiSitemapIndex(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws Exception {
		try {
			Integer aiId = Integer.parseInt(resourceRequest.getParameter("aiId"));
			long numberOfItems = 0;
			ArchivalInstitution archivalInstitution = archivalInstitutionDAO.getArchivalInstitution(aiId);
			ContentSearchOptions eadSearchOptions = new ContentSearchOptions();
			eadSearchOptions.setPublished(true);
			eadSearchOptions.setContentClass(FindingAid.class);
			eadSearchOptions.setArchivalInstitionId(aiId);
			numberOfItems = eadDAO.countEads(eadSearchOptions);
			if (numberOfItems > PAGESIZE) {
				int numberOfPages = (int) Math.ceil((double) numberOfItems / PAGESIZE);
				LOGGER.debug(getUserAgent(resourceRequest) + ": AI-index:" + archivalInstitution.getRepositorycode() + " #p:" + numberOfPages);
				resourceResponse.setCharacterEncoding(UTF8);
				resourceResponse.setContentType(APPLICATION_XML);
				XMLStreamWriter xmlWriter = (XMLOutputFactory.newInstance()).createXMLStreamWriter(
						resourceResponse.getPortletOutputStream(), UTF8);
				writeIndexStartElement(xmlWriter);
				for (int pageNumber = 1; pageNumber <= numberOfPages; pageNumber++) {
					String url = FriendlyUrlUtil.getUrl(resourceRequest, FriendlyUrlUtil.DIRECTORY_SITEMAP) + AI + aiId
							+ SEPARATOR + pageNumber;
					writeIndexElement(xmlWriter, url, archivalInstitution.getContentLastModifiedDate());
				}
				xmlWriter.writeEndElement();
				xmlWriter.writeEndDocument();
				xmlWriter.flush();
				xmlWriter.close();
			} else {
				generateAiContent(resourceRequest, resourceResponse, aiId, 1);
			}
		} catch (Exception e) {
			LOGGER.error("Unable to generate ai index: " + e.getMessage());
			resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, HTTP_NOT_FOUND);
		}
	}

	@ResourceMapping(value = "generateAiSitemap")
	public void generateAiSitemap(ResourceRequest resourceRequest, ResourceResponse resourceResponse) {
		try {
			Integer aiId = Integer.parseInt(resourceRequest.getParameter("aiId"));
			Integer pageNumber = Integer.parseInt(resourceRequest.getParameter("pageNumber"));
			generateAiContent(resourceRequest, resourceResponse, aiId, pageNumber);
		} catch (Exception e) {
			LOGGER.error("Unable to generate ai sitemap: " + e.getMessage());
			resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, HTTP_NOT_FOUND);
		}

	}

	public void generateAiContent(ResourceRequest resourceRequest, ResourceResponse resourceResponse, int aiId,
			int pageNumber) {
		try {
			ArchivalInstitution archivalInstitution = archivalInstitutionDAO.getArchivalInstitution(aiId);
			LOGGER.debug(getUserAgent(resourceRequest) + ": AI-content:" + archivalInstitution.getRepositorycode() + " pn:" + pageNumber);
			ContentSearchOptions eadSearchOptions = new ContentSearchOptions();
			eadSearchOptions.setPublished(true);
			eadSearchOptions.setContentClass(FindingAid.class);
			eadSearchOptions.setArchivalInstitionId(aiId);
			eadSearchOptions.setPageNumber(pageNumber);
			eadSearchOptions.setPageSize((int) PAGESIZE);
			List<Ead> eads = eadDAO.getEads(eadSearchOptions);
			if (pageNumber == 1) {
				eadSearchOptions.setContentClass(HoldingsGuide.class);
				eadSearchOptions.setPageSize(0);
				eads.addAll(eadDAO.getEads(eadSearchOptions));
				eadSearchOptions.setContentClass(SourceGuide.class);
				eads.addAll(eadDAO.getEads(eadSearchOptions));
			}
			if (eads.size() > 0) {
				resourceResponse.setCharacterEncoding(UTF8);
				resourceResponse.setContentType(APPLICATION_XML);
				XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(
						resourceResponse.getPortletOutputStream(), UTF8);
				writeIndexStartElement(xmlWriter);
				for (Ead ead : eads) {
					String url = FriendlyUrlUtil.getUrl(resourceRequest, FriendlyUrlUtil.DIRECTORY_SITEMAP) + EAD
							+ XmlType.getContentType(ead).getResourceName() + SEPARATOR + ead.getId();
					writeIndexElement(xmlWriter, url, ead.getPublishDate());
				}
				xmlWriter.writeEndElement();
				xmlWriter.writeEndDocument();
				xmlWriter.flush();
				xmlWriter.close();
			} else {
				resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, HTTP_NOT_FOUND);
			}
		} catch (Exception e) {
			LOGGER.error("Unable to generate ai content: " + e.getMessage());
			resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, HTTP_NOT_FOUND);
		}
	}

	@ResourceMapping(value = "generateEadSitemapIndex")
	public void generateEadSitemapIndex(ResourceRequest resourceRequest, ResourceResponse resourceResponse) {
		try {
			Integer eadId = Integer.parseInt(resourceRequest.getParameter("id"));
			XmlType xmlType = XmlType.getTypeByResourceName(resourceRequest.getParameter("xmlTypeName"));
			long numberOfItems = 0;
			ContentSearchOptions eadSearchOptions = new ContentSearchOptions();
			eadSearchOptions.setPublished(true);
			eadSearchOptions.setContentClass(xmlType.getClazz());
			eadSearchOptions.setId(eadId);
			Ead ead = eadDAO.getEads(eadSearchOptions).get(0);
			numberOfItems = cLevelDAO.countCLevels(xmlType.getEadClazz(), eadId);
			if (numberOfItems > PAGESIZE) {
				int numberOfPages = (int) Math.ceil((double) numberOfItems / PAGESIZE);
				LOGGER.debug(getUserAgent(resourceRequest) + ": EAD-index:" + ead.getArchivalInstitution().getRepositorycode() + " -" + ead.getEadid() + " #p:" + numberOfPages);
				resourceResponse.setCharacterEncoding(UTF8);
				resourceResponse.setContentType(APPLICATION_XML);
				XMLStreamWriter xmlWriter = (XMLOutputFactory.newInstance()).createXMLStreamWriter(
						resourceResponse.getPortletOutputStream(), UTF8);
				writeIndexStartElement(xmlWriter);
				for (int pageNumber = 1; pageNumber <= numberOfPages; pageNumber++) {
					String url = FriendlyUrlUtil.getUrl(resourceRequest, FriendlyUrlUtil.DIRECTORY_SITEMAP) + EAD
							+ XmlType.getContentType(ead).getResourceName() + SEPARATOR + ead.getId() + SEPARATOR
							+ pageNumber;
					writeIndexElement(xmlWriter, url, ead.getPublishDate());
				}
				xmlWriter.writeEndElement();
				xmlWriter.writeEndDocument();
				xmlWriter.flush();
				xmlWriter.close();
			} else {
				generateEadContent(resourceRequest, resourceResponse, xmlType, eadId, 1);
			}
		} catch (Exception e) {
			LOGGER.error("Unable to generate ead index: " + e.getMessage());
			resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, HTTP_NOT_FOUND);
		}
	}

	@ResourceMapping(value = "generateEadSitemap")
	public void generateEadSitemap(ResourceRequest resourceRequest, ResourceResponse resourceResponse) {
		try {
			Integer id = Integer.parseInt(resourceRequest.getParameter("id"));
			Integer pageNumber = Integer.parseInt(resourceRequest.getParameter("pageNumber"));
			XmlType xmlType = XmlType.getTypeByResourceName(resourceRequest.getParameter("xmlTypeName"));
			generateEadContent(resourceRequest, resourceResponse, xmlType, id, pageNumber);
		} catch (Exception e) {
			LOGGER.error("Unable to generate ead sitemap: " + e.getMessage());
			resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, HTTP_NOT_FOUND);
		}

	}

	public void generateEadContent(ResourceRequest resourceRequest, ResourceResponse resourceResponse, XmlType xmlType,
			int eadId, int pageNumber) {
		try {
			ContentSearchOptions eadSearchOptions = new ContentSearchOptions();
			eadSearchOptions.setPublished(true);
			eadSearchOptions.setContentClass(xmlType.getClazz());
			eadSearchOptions.setId(eadId);
			Ead ead = eadDAO.getEads(eadSearchOptions).get(0);
			LOGGER.debug(getUserAgent(resourceRequest) + ": EAD-content:" + ead.getArchivalInstitution().getRepositorycode() + " -" + ead.getEadid() + " pn:" + pageNumber);
			List<CLevel> clevels = cLevelDAO.getCLevels(xmlType.getEadClazz(), eadId, pageNumber, (int) PAGESIZE);
			if (clevels.size() >= 0) {
				resourceResponse.setCharacterEncoding(UTF8);
				resourceResponse.setContentType(APPLICATION_XML);
				XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(
						resourceResponse.getPortletOutputStream(), UTF8);
				writeSitemapStartElement(xmlWriter);
				if (pageNumber == 1) {
					String url = FriendlyUrlUtil.getUrl(resourceRequest, FriendlyUrlUtil.EAD_DISPLAY_FRONTPAGE)
							+ SEPARATOR + ead.getArchivalInstitution().getEncodedRepositorycode() + SEPARATOR
							+ xmlType.getResourceName() + SEPARATOR + ead.getEncodedEadid();
					writeSitemapElement(xmlWriter, url, ead.getPublishDate(), "0.7");
				}
				for (CLevel cLevel : clevels) {
					String url = FriendlyUrlUtil.getUrl(resourceRequest, FriendlyUrlUtil.EAD_DISPLAY_SEARCH) + "/C"
							+ cLevel.getClId();
					writeSitemapElement(xmlWriter, url, ead.getPublishDate(), null);
				}
				xmlWriter.writeEndElement();
				xmlWriter.writeEndDocument();
				xmlWriter.flush();
				xmlWriter.close();
			} else {
				resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, HTTP_NOT_FOUND);
			}
		} catch (Exception e) {
			LOGGER.error("Unable to generate ead sitemap: " + e.getMessage());
			resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, HTTP_NOT_FOUND);
		}
	}

	private static void writeIndexStartElement(XMLStreamWriter xmlWriter) throws XMLStreamException {
		if (xmlWriter != null) {
			xmlWriter.writeStartElement(SITEMAP_INDEX_ELEMENT.getPrefix(), SITEMAP_INDEX_ELEMENT.getLocalPart(),
					SITEMAP_INDEX_ELEMENT.getNamespaceURI());
			xmlWriter.writeDefaultNamespace(SITEMAP_NAMESPACE);
		}
	}

	private static void writeIndexElement(XMLStreamWriter xmlWriter, String url, Date lastModDate)
			throws XMLStreamException {
		if (xmlWriter != null) {
			xmlWriter.writeStartElement(SITEMAP);
			xmlWriter.writeStartElement(LOC);
			xmlWriter.writeCharacters(url);
			xmlWriter.writeEndElement();
			if (lastModDate != null) {
				xmlWriter.writeStartElement(LASTMOD);
				xmlWriter.writeCharacters(W3C_DATETIME_FORMAT.format(lastModDate));
				xmlWriter.writeEndElement();
			}
			xmlWriter.writeEndElement();
		}
	}

	private static void writeSitemapElement(XMLStreamWriter xmlWriter, String url, Date lastModDate, String priority)
			throws XMLStreamException {
		if (xmlWriter != null) {
			xmlWriter.writeStartElement(URL);
			xmlWriter.writeStartElement(LOC);
			xmlWriter.writeCharacters(url);
			xmlWriter.writeEndElement();
			if (lastModDate != null) {
				xmlWriter.writeStartElement(LASTMOD);
				xmlWriter.writeCharacters(W3C_DATETIME_FORMAT.format(lastModDate));
				xmlWriter.writeEndElement();
			}
			if (priority != null) {
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

	private static String getUserAgent(ResourceRequest resourceRequest) {
		ReadableUserAgent userAgent = PortalDisplayUtil.getUserAgent(resourceRequest);
		if (userAgent == null) {
			return "unknown";
		} else {
			return userAgent.getName();
		}
	}
}
