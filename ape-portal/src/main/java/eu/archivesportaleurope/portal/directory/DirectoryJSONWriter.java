package eu.archivesportaleurope.portal.directory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderGeometry;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.GeocoderStatus;
import com.google.code.geocoder.model.LatLng;
import com.google.code.geocoder.model.LatLngBounds;
import com.liferay.portal.kernel.util.PropsUtil;

import eu.apenet.commons.infraestructure.ArchivalInstitutionUnit;
import eu.apenet.commons.infraestructure.CountryUnit;
import eu.apenet.commons.infraestructure.NavigationTree;
import eu.apenet.commons.utils.APEnetUtilities;
import eu.apenet.persistence.factory.DAOFactory;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.Coordinates;
import eu.apenet.persistence.vo.Country;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.portal.common.tree.AbstractJSONWriter;

/**
 * JSON Writer for the directory tree
 * 
 * @author bastiaan
 * 
 */
@Controller(value = "directoryJSONWriter")
@RequestMapping(value = "VIEW")
public class DirectoryJSONWriter extends AbstractJSONWriter {
	private static final String FOLDER_LAZY = "\"isFolder\": true, \"isLazy\": true";
	private static final String FOLDER_NOT_LAZY = "\"isFolder\": true";
	private static final String NO_LINK = "\"noLink\": true";
	
	@ResourceMapping(value = "directoryTree")
	public void writeCountriesJSON(ResourceRequest resourceRequest, ResourceResponse resourceResponse) {
		long startTime = System.currentTimeMillis();
		try {
			SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
					resourceRequest.getLocale());
			NavigationTree navigationTree = new NavigationTree(source);
			List<CountryUnit> countryList = navigationTree.getALCountriesWithArchivalInstitutionsWithEAG();

			Collections.sort(countryList);
			writeToResponseAndClose(generateDirectoryJSON(navigationTree, countryList), resourceResponse);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.debug("Context search time: " + (System.currentTimeMillis() - startTime));
	}

	private StringBuilder generateCountriesTreeJSON(NavigationTree navigationTree, List<CountryUnit> countryList) {
		CountryUnit countryUnit = null;
		StringBuilder builder = new StringBuilder();
		builder.append(START_ARRAY);
		for (int i = 0; i < countryList.size(); i++) {
			// It is necessary to build a JSON response to display all the
			// countries in Directory Tree
			countryUnit = countryList.get(i);
			builder.append(START_ITEM);
			addTitle(builder, countryUnit.getLocalizedName(), navigationTree.getResourceBundleSource().getLocale());
			builder.append(COMMA);
			builder.append(FOLDER_LAZY);
			builder.append(COMMA);
			addKey(builder, countryUnit.getCountry().getId(), "country");
			addGoogleMapsAddress(builder, countryUnit.getCountry().getCname());
			addCountryCode(builder, countryUnit.getCountry().getIsoname());
			builder.append(END_ITEM);
			if (i != countryList.size() - 1) {
				builder.append(COMMA);
			}
		}

		builder.append(END_ARRAY);
		countryUnit = null;
		return builder;

	}

	private StringBuilder generateDirectoryJSON(NavigationTree navigationTree, List<CountryUnit> countryList) {
		StringBuilder builder = new StringBuilder();
		builder.append(START_ARRAY);
		builder.append(START_ITEM);
		addTitle(builder, navigationTree.getResourceBundleSource().getString("directory.text.directory"),
				navigationTree.getResourceBundleSource().getLocale());
		addGoogleMapsAddress(builder, "Europe");
		builder.append(COMMA);
		addExpand(builder);
		builder.append(COMMA);
		builder.append(FOLDER_WITH_CHILDREN);
		builder.append(generateCountriesTreeJSON(navigationTree, countryList));
		builder.append(END_ITEM);
		builder.append(END_ARRAY);
		return builder;
	}

	@ResourceMapping(value = "directoryTreeAi")
	public void writeAiJSON(ResourceRequest resourceRequest, ResourceResponse resourceResponse) {
		String nodeId = resourceRequest.getParameter("nodeId");
		String countryCode = resourceRequest.getParameter("countryCode");
		try {
			if (StringUtils.isBlank(nodeId) || StringUtils.isBlank(countryCode)) {
				StringBuilder builder = new StringBuilder();
				builder.append(START_ARRAY);
				builder.append(END_ARRAY);
				writeToResponseAndClose(builder, resourceResponse);
			} else {
				SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
						resourceRequest.getLocale());
				NavigationTree navigationTree = new NavigationTree(source);
				List<ArchivalInstitutionUnit> archivalInstitutionList = navigationTree
						.getArchivalInstitutionsByParentAiId(nodeId);

				// This filter has been added to display only those final
				// archival institutions or groups which have eag files uploaded
				// to the System
				// Remove it if the user wants to display again all the
				// institutions even if they doesn't eag files uploaded
				archivalInstitutionList = navigationTree.filterArchivalInstitutionsWithEAG(archivalInstitutionList);

				Collections.sort(archivalInstitutionList);
				writeToResponseAndClose(
						generateArchivalInstitutionsTreeJSON(navigationTree, archivalInstitutionList, countryCode),
						resourceResponse);
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	private StringBuilder generateArchivalInstitutionsTreeJSON(NavigationTree navigationTree,
			List<ArchivalInstitutionUnit> archivalInstitutionList, String countryCode) {
		Locale locale = navigationTree.getResourceBundleSource().getLocale();
		StringBuilder buffer = new StringBuilder();
		ArchivalInstitutionUnit archivalInstitutionUnit = null;

		buffer.append(START_ARRAY);
		for (int i = 0; i < archivalInstitutionList.size(); i++) {
			// It is necessary to build a JSON response to display all the
			// archival institutions in Directory Tree
			archivalInstitutionUnit = archivalInstitutionList.get(i);
			if (archivalInstitutionUnit.getIsgroup() && archivalInstitutionUnit.isHasArchivalInstitutions()) {
				// The Archival Institution is a group and it has archival
				// institutions within it
				buffer.append(START_ITEM);
				addTitle(buffer, archivalInstitutionUnit.getAiname(), locale);
				buffer.append(COMMA);
				buffer.append(FOLDER_LAZY);
				buffer.append(COMMA);
				addKey(buffer, archivalInstitutionUnit.getAiId(), "archival_institution_group");
				addCountryCode(buffer, countryCode);
				buffer.append(END_ITEM);
			} else if (archivalInstitutionUnit.getIsgroup() && !archivalInstitutionUnit.isHasArchivalInstitutions()) {
				// The Archival Institution is a group but it doesn't have any
				// archival institutions within it
				buffer.append(START_ITEM);
				addTitle(buffer, archivalInstitutionUnit.getAiname(), locale);
				buffer.append(COMMA);
				buffer.append(FOLDER_NOT_LAZY);
				buffer.append(COMMA);
				buffer.append(NO_LINK);
				buffer.append(COMMA);
				addKey(buffer, archivalInstitutionUnit.getAiId(), "archival_institution_group");
				addCountryCode(buffer, countryCode);
				buffer.append(END_ITEM);
			} else if (!archivalInstitutionUnit.getIsgroup()) {
				// The Archival Institution is a leaf
				buffer.append(START_ITEM);
				addTitle(buffer, archivalInstitutionUnit.getAiname(), locale);
				buffer.append(COMMA);
				if (archivalInstitutionUnit.getPathEAG() != null && !archivalInstitutionUnit.getPathEAG().equals("")) {
					// The archival institution has EAG
					addKey(buffer, archivalInstitutionUnit.getAiId(), "archival_institution_eag");
				} else {
					addKey(buffer, archivalInstitutionUnit.getAiId(), "archival_institution_no_eag");
					buffer.append(COMMA);
					buffer.append(NO_LINK);
				}
				addCountryCode(buffer, countryCode);
				buffer.append(END_ITEM);
			}
			if (i != archivalInstitutionList.size() - 1) {
				buffer.append(COMMA);
			}
		}

		buffer.append(END_ARRAY);
		archivalInstitutionUnit = null;
		return buffer;

	}

	private void addTitle(StringBuilder buffer, String title, Locale locale) {
		addTitle(null, buffer, title, locale);
	}

	private static void addGoogleMapsAddress(StringBuilder buffer, String address) {
		buffer.append(COMMA);
		buffer.append("\"googleMapsAddress\":\"" + address + "\"");
	}

	private static void addCountryCode(StringBuilder buffer, String countryCode) {
		buffer.append(COMMA);
		buffer.append("\"countryCode\":\"" + countryCode + "\"");
	}

	private static void addKey(StringBuilder buffer, Number key, String nodeType) {

		if (nodeType.equals("country")) {
			buffer.append("\"key\":" + "\"country_" + key + "\"");
		} else if (nodeType.equals("archival_institution_group")) {
			buffer.append("\"key\":" + "\"aigroup_" + key + "\"");
		} else if (nodeType.equals("archival_institution_eag")) {
			buffer.append("\"aiId\":");
			buffer.append(" \"" + key);
			buffer.append("\" ");
			buffer.append(COMMA);
			buffer.append("\"key\":" + "\"aieag_" + key + "\"");

		} else if (nodeType.equals("archival_institution_no_eag")) {
			buffer.append("\"key\":" + "\"ainoeag_" + key + "\"");
		}
	}
	
	@ResourceMapping(value = "directoryTreeArchivalInstitution")
	public void displayArchivalInstitutionParentsTree(ResourceRequest resourceRequest, ResourceResponse resourceResponse) {
		String aiId = null;
		if(resourceRequest.getParameter("aiId")!=null){
			aiId = (String)resourceRequest.getParameter("aiId");
		}
		if(aiId!=null){
			StringBuilder builder = new StringBuilder();
			builder.append(START_ARRAY);
			try{
				ArchivalInstitution ai = DAOFactory.instance().getArchivalInstitutionDAO().getArchivalInstitution(new Integer(aiId));
				Integer countryId = ai.getCountryId();
				builder.append(START_ITEM);
				Number key = ai.getAiId();
				addKey(builder,key,(ai.getEagPath()!=null)?"archival_institution_eag":"archival_institution_no_eag");
				builder.append(END_ITEM);
				builder.append(COMMA);
				boolean loop = ai.getParent()!=null;
				do{
					builder.append(START_ITEM);
					ai = ai.getParent();
					key = (ai!=null)?ai.getAiId():countryId;
					addKey(builder,key,(ai!=null)?"archival_institution_group":"country");
					builder.append(END_ITEM);
					if(loop){
						builder.append(COMMA);
					}
					if(ai.getParent()==null){
						builder.append(START_ITEM);
						addKey(builder,countryId,"country");
						builder.append(END_ITEM);
						loop = false;
					}
				}while(loop);
			}catch (Exception e) {
				log.error(APEnetUtilities.generateThrowableLog(e));
			}finally{
				builder.append(END_ARRAY);
				try {
					writeToResponseAndClose(builder, resourceResponse);
				} catch (UnsupportedEncodingException e) {
					log.error(APEnetUtilities.generateThrowableLog(e));
				} catch (IOException e) {
					log.error(APEnetUtilities.generateThrowableLog(e));
				}
			}
		}
	}
	
/***
 * This method recovers the coordinates and other data from the database, In case of the selected item on the tree is a country this method call geocoder and gets coordinates for that country. Also This method prints only coordinates "inside the world" limits avoiding the use of non valid coordinates 
 * @param resourceRequest The ResourceRequest interface represents the request send to the portlet for rendering resources. It 
 extends the ClientDataRequest interface and provides resource request information to portlets. 
The portlet container creates an ResourceRequest object and passes it as argument to the portlet's 
 serveResource method. 
The ResourceRequest is provided with the current portlet mode, window state, and render parameters 
 that the portlet can access via the PortletResourceRequest with getPortletMode and, getWindowState, or 
 one of the getParameter methods. ResourceURLs cannot change the current portlet mode, window state 
 or render parameters. Parameters set on a resource URL are not render parameters but parameters for 
 serving this resource and will last only for only this the current serveResource request.
 * @param resourceResponse The ResourceResponse defines an object to assist a portlet for rendering a resource. 
The difference between the RenderResponse is that for the ResourceResponse the output of this 
 response is delivered directly to the client without any additional markup added by the portal. It is 
 therefore allowed for the portlet to return binary content in this response. 
A portlet can set HTTP headers for the response via the setProperty or addProperty call in the 
 ResourceResponse. To be successfully transmitted back to the client, headers must be set before the 
 response is committed. Headers set after the response is committed will be ignored by the portlet 
 container. 
The portlet container creates a ResourceResponse object and passes it as argument to the portlet's 
 */
	@ResourceMapping(value = "directoryTreeGMaps")
	public void displayGmaps(ResourceRequest resourceRequest, ResourceResponse resourceResponse) {
		long startTime = System.currentTimeMillis();
		try {
			SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
					resourceRequest.getLocale());
			NavigationTree navigationTree = new NavigationTree(source);

			String countryCode = resourceRequest.getParameter("countryCode");
			String institutionID = resourceRequest.getParameter("institutionID");
			String repositoryName = resourceRequest.getParameter("repositoryName");
			
			//parse bad params (if jquery parse a null to "null", it has to be reconverted
			countryCode = (countryCode!=null && countryCode.equalsIgnoreCase("null"))?null:countryCode;
			institutionID = (institutionID!=null && institutionID.equalsIgnoreCase("null"))?null:institutionID;
			repositoryName = (repositoryName!=null && repositoryName.equalsIgnoreCase("null"))?null:repositoryName;

			// Always recovers all the coordinates.
			List<Coordinates> reposList = DAOFactory.instance().getCoordinatesDAO().getCoordinates();

			// Remove coordinates with values (0, 0).
			if (reposList != null && !reposList.isEmpty()) {
				// New list without (0, 0) values.
				List<Coordinates> cleanReposList = new ArrayList<Coordinates>();
				Iterator<Coordinates> reposIt = reposList.iterator();
				while (reposIt.hasNext()) {
					Coordinates coordinates = reposIt.next();
					if (coordinates.getLat() != 0 || coordinates.getLon() != 0) {
						//control elements outside the printable earth coordinates (-77 to 82) and (-177 to 178)
						if ((coordinates.getLat() >=-77 && coordinates.getLat() <= 82) && (coordinates.getLon() >=-177 && coordinates.getLon() <= 178)) {
							cleanReposList.add(coordinates);
						}
					}
				}
				// Pass the clean array to the existing one.
				reposList = cleanReposList;
			}

			// Check the part to center.
			if (repositoryName != null && !repositoryName.isEmpty()
					&& institutionID != null && !institutionID.isEmpty()) {
				writeToResponseAndClose(generateGmapsJSON(navigationTree, reposList, null, institutionID, repositoryName), resourceResponse);
			} else if (institutionID != null && !institutionID.isEmpty()) {
				writeToResponseAndClose(generateGmapsJSON(navigationTree, reposList, null, institutionID, null), resourceResponse);
			} else if (countryCode != null && !countryCode.isEmpty()) {
				writeToResponseAndClose(generateGmapsJSON(navigationTree, reposList, countryCode, null, null), resourceResponse);
			} else {
				writeToResponseAndClose(generateGmapsJSON(navigationTree, reposList, null, null, null), resourceResponse);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.debug("Context search time: " + (System.currentTimeMillis() - startTime));
	}
	
	private StringBuilder generateGmapsJSON(NavigationTree navigationTree, List<Coordinates> repoList, String countryCode, String institutionID, String repositoryName) {
		StringBuilder builder = new StringBuilder();
		builder.append(START_ITEM);
		builder.append("\"count\":" + repoList.size());
		builder.append(COMMA);
		builder.append("\"repos\":");
		
		builder.append(START_ARRAY);
		if(repoList!=null){
			Iterator<Coordinates> itRepoList = repoList.iterator();
			while(itRepoList.hasNext()){
				Coordinates repo = itRepoList.next();
				builder.append(buildNode(repo));
				if(itRepoList.hasNext()){
					builder.append(COMMA);
				}
			}
		}
		builder.append(END_ARRAY);

		// Add the center values.
		if (repositoryName != null && !repositoryName.isEmpty()
				&& institutionID != null && !institutionID.isEmpty()) {
			// Call the method to add the bounds for the repository of the institution.
			builder.append(this.buildInstitutionBounds(institutionID, repositoryName));
		} else if (institutionID != null && !institutionID.isEmpty()) {
			// Call the method to add the bounds for the institution.
			builder.append(this.buildInstitutionBounds(institutionID, null));
		} else if (countryCode != null && !countryCode.isEmpty()) {
			// Call the method to add the bounds for the country.
			builder.append(this.buildCountryBounds(countryCode));
		}
		
		builder.append(END_ITEM);
		return builder;
	}
	
	private StringBuilder buildNode(Coordinates repo){
		StringBuilder builder = new StringBuilder();
		builder.append(START_ITEM);
		builder.append("\"latitude\":\""+repo.getLat()+"\"");
		builder.append(COMMA);
		builder.append("\"longitude\":\""+repo.getLon()+"\"");
		builder.append(COMMA);
		//this escapes " in field
		builder.append("\"name\":\""+PortalDisplayUtil.replaceQuotesAndReturns(repo.getNameInstitution())+"\"");
		ArchivalInstitution ai = repo.getArchivalInstitution();
		if(ai!=null){
			builder.append(COMMA);
			builder.append("\"aiId\":\""+ai.getAiId()+"\"");
		}
		//Parse street, postalCity and country 
		builder.append(COMMA);
		builder.append("\"street\":\""+repo.getStreet()+"\"");
		builder.append(COMMA);
		builder.append("\"postalcity\":\""+repo.getPostalCity()+"\"");
		builder.append(COMMA);
		builder.append("\"country\":\""+repo.getCountry()+"\"");
		builder.append(END_ITEM);
		return builder;
	}

	/**
	 * Method to build the data for the institution bounds.
	 *
	 * @param institutionID Institution for recover the bounds.
	 * @return Element with the bounds for the institution passed.
	 */
	private StringBuilder buildInstitutionBounds(String institutionID, String repositoryName) {
		StringBuilder builder = new StringBuilder();
		// Recover the list of coordinates for the current institution.
		ArchivalInstitution archivalInstitution = DAOFactory.instance().getArchivalInstitutionDAO().findById(Integer.parseInt(institutionID));
		List<Coordinates> repoCoordinatesList = DAOFactory.instance().getCoordinatesDAO().findCoordinatesByArchivalInstitution(archivalInstitution);

		// If the list contains more than one elemet, find the proper element.
		if (repoCoordinatesList != null && !repoCoordinatesList.isEmpty()) {
			Coordinates coordinates = null;
			if (repoCoordinatesList.size() > 1) {
				Iterator<Coordinates> repoCoordinatesIt = repoCoordinatesList.iterator();
				if (repositoryName != null && !repositoryName.isEmpty()) {
					// Select the proper element from database.
					while (repoCoordinatesIt.hasNext()) {
						Coordinates coordinatesTest = repoCoordinatesIt.next();
						if (repositoryName.startsWith(coordinatesTest.getNameInstitution())) {
							coordinates = coordinatesTest;
						}
					}
				} else {
					// First element in database (main institution)
					while (repoCoordinatesIt.hasNext()) {
						Coordinates coordinatesTest = repoCoordinatesIt.next();
						if (coordinates != null) {
							if (coordinates.getId() > coordinatesTest.getId()) {
								coordinates = coordinatesTest;
							}
						} else {
							coordinates = coordinatesTest;
						}
					}
				}
			}

			// At this point, if coordinates still null, set the value of the
			// first element of the list.
			if (coordinates == null) {
				coordinates = repoCoordinatesList.get(0);
			}

			// if coords=0,0 or null call to show the country
			if(coordinates.getLat()==0.0 && coordinates.getLon()==0.0){
				builder.append(buildCountryBounds(archivalInstitution.getCountry().getIsoname()));
			}
			else{	
				// Build bounds node.
				builder.append(COMMA);
				builder.append("\"bounds\":");
	
				// Build coordinates node.
				builder.append(START_ARRAY);
				builder.append(START_ITEM);
				builder.append("\"latitude\":\"" + coordinates.getLat() + "\"");
				builder.append(COMMA);
				builder.append("\"longitude\":\"" + coordinates.getLon() + "\"");
				builder.append(END_ITEM);
				builder.append(END_ARRAY);
			}
		}
		else{
			builder.append(buildCountryBounds(archivalInstitution.getCountry().getIsoname()));
		}

		return builder;
	}

	/**
	 * Method to build the data for the country bounds.
	 *
	 * @param countryCode Country code for recover the bounds.
	 * @return Element with the bounds for the country code passed.
	 */
	private StringBuilder buildCountryBounds(String countryCode) {
		StringBuilder builder = new StringBuilder();
		// Recover the name of the current country by country code.
		List<Country> countriesList = DAOFactory.instance().getCountryDAO().getCountries(countryCode);
		
		if (countriesList != null && !countriesList.isEmpty()) {
			String selectedCountryName = countriesList.get(0).getCname();

			// Try to recover the coordinates to bound.
			Geocoder geocoder = new Geocoder();

			GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(selectedCountryName).getGeocoderRequest();
			GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);

			if (geocoderResponse.getStatus().equals(GeocoderStatus.OK)) {
				List<GeocoderResult> geocoderResultList = geocoderResponse.getResults();

				// Always recover the first result.
				if (geocoderResultList.size() > 0) {
					GeocoderResult geocoderResult = geocoderResultList.get(0);

					// Get Geometry Object.
					GeocoderGeometry geocoderGeometry = geocoderResult.getGeometry();
					// Get Bounds Object.
					LatLngBounds latLngBounds = geocoderGeometry.getBounds();

					// Get southwest bound.
					LatLng southwestLatLng = latLngBounds.getSouthwest();
					// Get southwest latitude.
					Double southwestLatitude = southwestLatLng.getLat().doubleValue();
					// Get southwest longitude.
					Double southwestLongitude = southwestLatLng.getLng().doubleValue();

					// Get northeast bound.
					LatLng northeastLatLng = latLngBounds.getNortheast();
					// Get northeast latitude.
					Double northeastLatitude = northeastLatLng.getLat().doubleValue();
					// Get northeast longitude.
					Double northeastLongitude = northeastLatLng.getLng().doubleValue();

					// Build bounds node.
					builder.append(COMMA);
					builder.append("\"bounds\":");

					builder.append(START_ARRAY);
					// Build southwest node.
					builder.append(START_ITEM);
					builder.append("\"latitude\":\"" + southwestLatitude + "\"");
					builder.append(COMMA);
					builder.append("\"longitude\":\"" + southwestLongitude + "\"");
					builder.append(END_ITEM);

					// Build northeast node.
					builder.append(COMMA);
					builder.append(START_ITEM);
					builder.append("\"latitude\":\"" + northeastLatitude + "\"");
					builder.append(COMMA);
					builder.append("\"longitude\":\"" + northeastLongitude + "\"");
					builder.append(END_ITEM);
					builder.append(END_ARRAY);
				}
			}
		}
		return builder;
	}
}
