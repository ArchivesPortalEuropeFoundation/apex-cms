package eu.archivesportaleurope.portal.eaccpf.display;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.liferay.portal.util.PortalUtil;

import eu.apenet.commons.types.XmlType;
import eu.apenet.commons.utils.APEnetUtilities;
import eu.apenet.commons.utils.DisplayUtils;
import eu.apenet.persistence.dao.CollectionContentDAO;
import eu.apenet.persistence.dao.CollectionDAO;
import eu.apenet.persistence.dao.EacCpfDAO;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.EacCpf;
import eu.archivesportaleurope.persistence.jpa.dao.SavedBookmarksJpaDAO;
import eu.archivesportaleurope.portal.common.NotExistInDatabaseException;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.common.PropertiesKeys;
import eu.archivesportaleurope.portal.common.PropertiesUtil;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.portal.common.urls.EacCpfPersistentUrl;
import eu.archivesportaleurope.portal.display.eac.jsp.EacTag;
import eu.archivesportaleurope.util.ApeUtil;
/**
 * 
 * This is display eac cpf controller
 * 
 * @author bverhoef
 * 
 */
@Controller(value = "displayEacCpfContoller")
@RequestMapping(value = "VIEW")
public class DisplayEacCpfContoller {
	private final static Logger LOGGER = Logger.getLogger(DisplayEacCpfContoller.class);
	private MessageSource messageSource;
	private EacCpfDAO eacCpfDAO;
	private CollectionDAO collectionDAO;
	private static final String COLLECTION_IN = "collectionToAdd_";
    private final static int PAGESIZE  = 20;
    private SavedBookmarksJpaDAO savedBookmarksDAO;
	private CollectionContentDAO collectionContentDAO;

	public void setCollectionDAO(CollectionDAO collectionDAO) {
		this.collectionDAO = collectionDAO;
	}
		
	public MessageSource getMessageSource() {
		return messageSource;
	}
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public EacCpfDAO getEacCpfDAO() {
		return this.eacCpfDAO;
	}
	
	public void setEacCpfDAO(EacCpfDAO eacCpfDAO) {
		this.eacCpfDAO = eacCpfDAO;
	}
	
	public void setSavedBookmarksDAO(SavedBookmarksJpaDAO savedBookmarksDAO) {
		this.savedBookmarksDAO = savedBookmarksDAO;
	}
	
	public void setCollectionContentDAO(CollectionContentDAO collectionContentDAO) {
		this.collectionContentDAO = collectionContentDAO;
	}

	@RenderMapping
	public ModelAndView displayEacCpf(RenderRequest renderRequest, @ModelAttribute(value = "eacParams") EacCpfParams eacParams) {
		ModelAndView modelAndView = null;

		try {
			modelAndView = displayDetails(renderRequest, eacParams);
		}catch (NotExistInDatabaseException e) {
			//LOGGER.error("SOLRID NOT IN DB:" + e.getId());
		}catch (Exception e) {
			LOGGER.error("Error in EAC-CPF display process:" + e.getMessage(),e);

		}
		if (modelAndView == null){
			modelAndView = new ModelAndView();
			modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notexist");
			modelAndView.setViewName("indexError");
		}
		return modelAndView;
	}
	
	@RenderMapping(params = "myaction=printEacDetails")
	public ModelAndView printDetails(@ModelAttribute(value = "eacParams") EacCpfParams eacParams, RenderRequest renderRequest){
		ModelAndView modelAndView = null;
		try {
			modelAndView = displayDetails(renderRequest, eacParams);
			modelAndView.setViewName("printEacdetails");
		}catch (NotExistInDatabaseException e) {
		}catch (Exception e) {
			LOGGER.error("Error in EAC-CPF display process:" + e.getMessage(),e);
		}
		if (modelAndView == null){
			modelAndView = new ModelAndView();
			modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notexist");
			modelAndView.setViewName("indexError");
		}
		return modelAndView;
	}

	/**
	 * Method to call the page that loads the content of the apeEAC-CPF in the selected language by the user.
	 *
	 * @param eacParams
	 * @param renderRequest
	 *
	 * @return
	 */
	@RenderMapping(params = "myaction=translateEacDetails")
	public ModelAndView translatedEacDetails(@ModelAttribute(value = "eacParams") EacCpfParams eacParams, RenderRequest renderRequest) {
		ModelAndView modelAndView = null;

		try {
			modelAndView = displayDetails(renderRequest, eacParams);
		}catch (NotExistInDatabaseException e) {
			//LOGGER.error("SOLRID NOT IN DB:" + e.getId());
		}catch (Exception e) {
			LOGGER.error("Error in EAC-CPF display process:" + e.getMessage(),e);

		}
		if (modelAndView == null){
			modelAndView = new ModelAndView();
			modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notexist");
			modelAndView.setViewName("indexError");
		}
		return modelAndView;
	}

	public ModelAndView displayDetails(RenderRequest renderRequest, EacCpfParams eacParams) throws NotExistInDatabaseException{
		ModelAndView modelAndView = new ModelAndView();
		EacCpf eaccpf = null;
		
		// Recover the current apeEAC-CPF from identifier and repositoryCode.
		if (eacParams.getEaccpfIdentifier() != null
				&& eacParams.getRepositoryCode() != null){
			eaccpf = eacCpfDAO.getEacCpfByIdentifier(eacParams.getRepositoryCode(), eacParams.getEaccpfIdentifier());
		}
		
		if (eaccpf != null) {
			PortalDisplayUtil.setPageTitle(renderRequest,
					PortalDisplayUtil.getEacCpfDisplayTitle(eaccpf));
				return displayEacDetails(renderRequest, eacParams, modelAndView, eaccpf);

		}else {
			return null;
		}
	}
	
	public ModelAndView displayEacDetails(RenderRequest renderRequest, EacCpfParams eacParam, ModelAndView modelAndView,
			EacCpf eac) {
		ArchivalInstitution archivalInstitution = null;
		try {
			if (eac == null) {
				modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notexist");
				modelAndView.setViewName("indexError");
				return modelAndView;
			} else {
				String path = APEnetUtilities.getApePortalAndDashboardConfig().getRepoDirPath() + eac.getPath();
				File file= new File(path);
				if (file.exists()){
					if (eac.isPublished()) {
						archivalInstitution = eac.getArchivalInstitution();
					} else {
						// LOGGER.info("Found not indexed EAD in second display");
						modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notindexed");
						modelAndView.setViewName("indexError");
						return modelAndView;
					}
					modelAndView.getModelMap().addAttribute("type", EacTag.EACCPFDETAILS_XSLT);
					modelAndView.getModelMap().addAttribute("repositoryCode", eacParam.getRepositoryCode());
					modelAndView.getModelMap().addAttribute("eaccpfIdentifier", eacParam.getEaccpfIdentifier());
					modelAndView.getModelMap().addAttribute("eac", eac);
					modelAndView.getModelMap().addAttribute("xmlTypeId", XmlType.getContentType(eac).getIdentifier());
					modelAndView.getModelMap().addAttribute("archivalInstitution", archivalInstitution);
					modelAndView.getModelMap().addAttribute("aiId", archivalInstitution.getAiId());
					SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
							renderRequest.getLocale());
				    
					//navigator's lang
					HttpServletRequest request = PortalUtil.getHttpServletRequest(renderRequest);
					String lang = request.getHeader("Accept-Language").substring(0, 2);	
					modelAndView.getModelMap().addAttribute("langNavigator", lang);
					
					String localizedName = DisplayUtils.getLocalizedCountryName(source, archivalInstitution.getCountry());
					modelAndView.getModelMap().addAttribute("localizedCountryName", localizedName);
					modelAndView.getModelMap().addAttribute("element", eacParam.getElement());
					modelAndView.getModelMap().addAttribute("term", ApeUtil.decodeSpecialCharacters(eacParam.getTerm()));
					EacCpfPersistentUrl eaccpfPersistentUrl = new EacCpfPersistentUrl(eac.getArchivalInstitution().getRepositorycode(), eac.getIdentifier());
					eaccpfPersistentUrl.setSearchFieldsSelectionId(eacParam.getElement());
					eaccpfPersistentUrl.setSearchTerms(eacParam.getTerm());
					modelAndView.setViewName("index");
					modelAndView.getModel().put("recaptchaAjaxUrl",  PropertiesUtil.get(PropertiesKeys.APE_RECAPTCHA_AJAX_URL));
					modelAndView.getModelMap().addAttribute("recaptchaPubKey",  PropertiesUtil.get(PropertiesKeys.LIFERAY_RECAPTCHA_PUB_KEY));
					String documentTitle = eac.getTitle();
					String pageTitle = PortalDisplayUtil.getEacCpfDisplayPageTitle(eac);
					modelAndView.getModelMap().addAttribute("pageTitle",pageTitle);
					documentTitle = PortalDisplayUtil.getEacCpfDisplayTitle(eac);
					modelAndView.getModelMap().addAttribute("documentTitle",documentTitle);

					// Fill languages map.
					// First element, default value.
					eacParam.getLanguagesMap().put("default", source.getString("label.translations"));

					// Add languages from the file.
					Map<String, String> langsMap = recoverLanguagesInXML(path);
					eacParam.getLanguagesMap().putAll(langsMap);

					// Last element, all values.
					eacParam.getLanguagesMap().put("showAll", source.getString("label.show.all.translations"));

					// Add map as a parameter.
					modelAndView.getModelMap().addAttribute("languagesMap", eacParam.getLanguagesMap());

					// Add the selected translation language.
					String translationLanguage = "default";
					if (eacParam.getTranslationLanguage() != null) {
						translationLanguage = eacParam.getTranslationLanguage();
					}
					modelAndView.getModelMap().addAttribute("translationLanguage", translationLanguage);

					return modelAndView;
				}
				else{
					return null;
				}
			}

		} catch (Exception e) {
			LOGGER.error("Error in second display process: " + e.getMessage());
			modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notexist");
			modelAndView.setViewName("indexError");
			return modelAndView;
		}
	}

	/**
	 * Method to read the XML file and recover all the languages included in
	 * the XML file on section "<cpfDescription>".
	 *
	 * @param path Path in which is stored the file to parse.
	 * @return
	 */
	private Map<String, String> recoverLanguagesInXML(String path) {
		Map<String, String> resultMap = new LinkedHashMap<String, String>();
		Set<String> languagesSet = new LinkedHashSet<String>();

		try {
			// Create SAX parser factory.
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			// Create SAX parser.
			SAXParser saxParser = saxParserFactory.newSAXParser();
			// Call method to recover the values.
			SaxHandler saxHandler = new SaxHandler(languagesSet);
			// Parse the file.
			saxParser.parse(path, saxHandler);
		} catch (ParserConfigurationException e) {
			LOGGER.error("Error in XML SAX parser: " + e.getMessage());
		} catch (SAXException e) {
			LOGGER.error("SAX error: " + e.getMessage());
		} catch (IOException e) {
			LOGGER.error("Error accesing file (" + path + "): " + e.getMessage());
		}

		// Fill the map with the values from the set.
		if (languagesSet != null && !languagesSet.isEmpty()) {
			Map<String, String> unorderedMap = new LinkedHashMap<String, String>();
			Iterator<String> languagesIt = languagesSet.iterator();
			while (languagesIt.hasNext()) {
				String currentLang = languagesIt.next();

				// Checks the language.
				Map<String, Locale> localeMap = APEnetUtilities.getIso3ToIso2LanguageCodesMap();

				Locale language = localeMap.get(currentLang);
				if (language == null) {
					language = Locale.ENGLISH;
				}

				String localizedLang = language.getDisplayLanguage(language).toLowerCase();
				localizedLang = localizedLang.substring(0, 1).toUpperCase() + localizedLang.substring(1);

				unorderedMap.put(localizedLang, currentLang);
			}

			// Sort the values in the map.
			List<String> keysList = new LinkedList<String>(unorderedMap.keySet());
			Collections.sort(keysList);

			// Add the elements ordered to the map.
			for (int i = 0; i < keysList.size(); i++) {
				resultMap.put(unorderedMap.get(keysList.get(i)), keysList.get(i));
			}
		}

		return resultMap;
	}

	/**
	 * Class that handle the XML passed and recovers all the value of all the
	 * attributes "@xml:lang".
	 */
	private static class SaxHandler extends DefaultHandler {
		private Set<String> languagesSet;
		
		public SaxHandler(Set<String> languagesSet) {
			this.languagesSet = languagesSet;
		}

		public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXParseException, SAXException {
			// For each attribute, get the name and value.
			for (int i = 0; i < attrs.getLength(); i++) {
				String attrName = attrs.getQName(i);
				if (attrName.equalsIgnoreCase("xml:lang")) {
					String attrValue = attrs.getValue(i);
					this.languagesSet.add(attrValue);
				}
			}
		}
	}
}