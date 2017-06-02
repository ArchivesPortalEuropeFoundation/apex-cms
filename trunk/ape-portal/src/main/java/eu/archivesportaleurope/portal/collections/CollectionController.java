package eu.archivesportaleurope.portal.collections;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;

import eu.apenet.persistence.dao.CollectionContentDAO;
import eu.apenet.persistence.dao.CollectionDAO;
import eu.apenet.persistence.dao.EadSavedSearchDAO;
import eu.apenet.persistence.dao.SavedBookmarksDAO;
import eu.apenet.persistence.vo.Collection;
import eu.apenet.persistence.vo.CollectionContent;
import eu.apenet.persistence.vo.EadSavedSearch;
import eu.apenet.persistence.vo.SavedBookmarks;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.util.ApeUtil;


/***
 * This is the collection controller class
 */
@Controller(value = "CollectionController")
@RequestMapping(value = "VIEW")
public class CollectionController {
	
	private final static Logger LOGGER = Logger.getLogger(CollectionController.class);
	private final static int PAGESIZE  = 10;
	private static final String SEARCH_IN = "hidden_selected_search_";
	private static final String SEARCH_OUT = "hidden_new_search_";
	private static final String BOOKMARK_IN = "hidden_collection_bookmark_";
	private static final String BOOKMARK_OUT = "hidden_new_bookmark_";
	private CollectionDAO collectionDAO;
	private CollectionContentDAO collectionContentDAO;
	private SavedBookmarksDAO savedBookmarksDAO;
	private EadSavedSearchDAO eadSavedSearchDAO;
	
	public void setCollectionContentDAO(CollectionContentDAO collectionContentDAO) {
		this.collectionContentDAO = collectionContentDAO;
	}

	public void setSavedBookmarksDAO(SavedBookmarksDAO savedBookmarksDAO) {
		this.savedBookmarksDAO = savedBookmarksDAO;
	}

	public void setEadSavedSearchDAO(EadSavedSearchDAO eadSavedSearchDAO) {
		this.eadSavedSearchDAO = eadSavedSearchDAO;
	}

	public void setCollectionDAO(CollectionDAO collectionDAO) {
		this.collectionDAO = collectionDAO;
	}

	/***
	 * This method is used to show the user collections</br>
	 * Those collections can be shown ordered by various columns
	 * 
	 * @param request {@link RenderRequest} gets all data sent by the request
	 * 
	 * @return modelAndView {@link ModelAndView} modelAndView reference to view with name 'collection'
	 */
	@RenderMapping
	public ModelAndView showSavedCollections(RenderRequest request) {
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Enter in method \"showSavedCollections\"");
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("home");
		boolean orderAsc=true;
		String orderType = request.getParameter("orderType");
		String orderColumn = request.getParameter("orderColumn");
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_SAVED_COLLECTIONS);
		Principal principal = request.getUserPrincipal();
		if (principal != null){
			Long liferayUserId = Long.parseLong(principal.toString());
			Integer pageNumber = 1;

			if (StringUtils.isNotBlank(request.getParameter("pageNumber"))){
				pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
			}

			// Check if the order should be ascending, descending or no order.
			if (orderType != null) {
				if (orderType.equals("orderAsc")) {
					orderAsc=true;
				} else {
					orderAsc=false;
				}
			} else {
				orderAsc=false;
				orderType = "none";
			}

			// Check if if exists a column selected to order.
			if (orderColumn == null
					|| orderColumn.isEmpty()) {
				orderColumn = "none";
			}

			List<Collection> collections = this.collectionDAO.getCollectionsByUserId(liferayUserId, pageNumber, PAGESIZE, orderColumn, orderAsc);
			User user = (User) request.getAttribute(WebKeys.USER);
			modelAndView.getModelMap().addAttribute("timeZone", user.getTimeZone());
			modelAndView.getModelMap().addAttribute("pageNumber", pageNumber);
			modelAndView.getModelMap().addAttribute("totalNumberOfResults", (collections.size()>0)?this.collectionDAO.countCollectionsByUserId(liferayUserId):collections.size());
			modelAndView.getModelMap().addAttribute("pageSize", PAGESIZE);
			modelAndView.getModelMap().addAttribute("orderType", orderType);
			modelAndView.getModelMap().addAttribute("orderColumn", orderColumn);
			modelAndView.getModelMap().addAttribute("collections",collections);

		}
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Exit in method \"showSavedCollections\"");
		
		return modelAndView;
	}
	
	/***
	 * This method shows a collection by collection id and user id<br/>
	 * Maps @RenderMapping(params="action=viewCollection")
	 * 
	 * @param request {@link RenderRequest} gets all data sent by the request
	 * 
	 * @return modelAndView {@link ModelAndView} modelAndView reference to view with name 'collection'
	 */
	@RenderMapping(params="action=viewCollection")
	public ModelAndView viewCollection(RenderRequest request){
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Enter in method \"viewCollection\"");
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("collection");
		Principal principal = request.getUserPrincipal();
		if(principal!=null && request.getParameter("id")!=null){ 
			try {
			//edit checks
				long id = Long.parseLong(request.getParameter("id"));
				Long liferayUserId = Long.parseLong(principal.toString());
				Collection targetCollection = this.collectionDAO.getCollectionById(id);
				if(targetCollection!=null && (targetCollection.isPublic_() || targetCollection.getLiferayUserId() == liferayUserId)){
					modelAndView.getModelMap().addAttribute("edit",false);
					modelAndView.getModelMap().addAttribute("collection",targetCollection);
					List<CollectionContent> listCollectionContent = this.collectionContentDAO.getCollectionContentsByCollectionId(id);
					if(listCollectionContent!=null){
						List<CollectionContent> listCollectionSearches = new ArrayList<CollectionContent>();
						List<CollectionContent> listCollectionBookmarks = new ArrayList<CollectionContent>();
						for(CollectionContent collectionContent : listCollectionContent){
							if(collectionContent.getSavedBookmarks()!=null){
								listCollectionBookmarks.add(collectionContent);
							}else if(collectionContent.getEadSavedSearch()!=null){
								listCollectionSearches.add(collectionContent);
							}
						}
						if(listCollectionSearches.size()>0){
							modelAndView.getModelMap().addAttribute("collectionSearches",listCollectionSearches);
						}
						if(listCollectionBookmarks.size()>0){
							modelAndView.getModelMap().addAttribute("bookmarks",listCollectionBookmarks);
						}
					}
				}
			} catch (Exception e) {
				LOGGER.error(ApeUtil.generateThrowableLog(e));
			}
		}
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Exit in method \"viewCollection\"");
		
		return modelAndView;
	}

	/***
	 * This method creates a new collection by user id from the My collections in My pages space <br/>
	 * renders @RenderMapping(params="action=createNewCollection")
	 * 
	 * @param request {@link RenderRequest} gets all data sent by the request
	 * 
	 * @return modelAndView {@link ModelAndView} modelAndView reference to view with name 'collection'
	 */
	@RenderMapping(params="action=createNewCollection")
	public ModelAndView createNewCollection(RenderRequest request){
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Enter in method \"createNewCollection\"");
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("collection");
		modelAndView.getModelMap().addAttribute("edit",true);
		Principal principal = request.getUserPrincipal();
		Long id = request.getParameter("id")!=null?Long.parseLong(request.getParameter("id")):null;
		if(principal!=null && id!=null){ //edit checks
			long liferayUserId = Long.parseLong(principal.toString());
			Collection targetCollection = this.collectionDAO.getCollectionByIdAndUserId(id, liferayUserId);
			if(targetCollection!=null){
				modelAndView.getModelMap().addAttribute("collection",targetCollection);
				List<CollectionContent> listCollectionContent = this.collectionContentDAO.getCollectionContentsByCollectionId(id);
				if(listCollectionContent!=null){
					List<CollectionContent> listCollectionSearches = new ArrayList<CollectionContent>();
					List<CollectionContent> listCollectionBookmarks = new ArrayList<CollectionContent>();
					for(CollectionContent collectionContent : listCollectionContent){
						if(collectionContent.getSavedBookmarks()!=null){
							listCollectionBookmarks.add(collectionContent);
						}else if(collectionContent.getEadSavedSearch()!=null){
							listCollectionSearches.add(collectionContent);
						}
					}
					if(listCollectionSearches.size()>0){
						modelAndView.getModelMap().addAttribute("cSearches",listCollectionSearches);
					}
					if(listCollectionBookmarks.size()>0){
						modelAndView.getModelMap().addAttribute("cBookmarks",listCollectionBookmarks);
					}
				}
			}
		}
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Exit in method \"createNewCollection\"");
		
		return modelAndView;
	}
	
	/***
	 * This method gets the searches stored in a collection to show in My Collections in My Pages space
	 * 
	 * @param request {@link ResourceRequest} gets all data sent by the request
	 * @param response {@link ResourceResponse} gets all data sent by the response
	 * 
	 * @return modelAndView {@link ModelAndView} modelAndView reference to view with name 'collection'
	 */
	@ResourceMapping(value = "getSearches")
	public ModelAndView getSearches(ResourceRequest request, ResourceResponse response){
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Enter in method \"getSearches\"");
		
		Long id = request.getParameter("id")!=null?Long.parseLong(request.getParameter("id")):null;
		ModelAndView modelAndView = new ModelAndView();
		int pageNumber = 1;
		if(id!=null){
			if (StringUtils.isNotBlank(request.getParameter("pageNumber"))){
				pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
			}
			List<CollectionContent> restCollectionSearches = this.collectionContentDAO.getCollectionContentsByCollectionId(id, true, pageNumber, PAGESIZE);
			modelAndView.getModelMap().addAttribute("currentSearches",(restCollectionSearches!=null && restCollectionSearches.size()>0)?restCollectionSearches:null);
		}
		modelAndView.setViewName("currentSearch");
		// params
		modelAndView.getModelMap().addAttribute("edit",(StringUtils.isNotBlank(request.getParameter("edit")) && new Boolean(request.getParameter("edit"))));
		User user = (User) request.getAttribute(WebKeys.USER);
		modelAndView.getModelMap().addAttribute("timeZone", user.getTimeZone());
		modelAndView.getModelMap().addAttribute("pageNumber", pageNumber);
		modelAndView.getModelMap().addAttribute("totalNumberOfResults", id!=null?this.collectionContentDAO.countCollectionContentsByCollectionId(id, true):0);
		modelAndView.getModelMap().addAttribute("pageSize", PAGESIZE);
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Exit in method \"getSearches\"");
		
		return modelAndView;
	}
	
	/***
 	 * This method gets the bookmarks stored in a collection to show in My Collections in My Pages space
 	 * 
	 * @param request {@link ResourceRequest} gets all data sent by the request
	 * @param response {@link ResourceResponse} gets all data sent by the response
	 * 
	 * @return modelAndView {@link ModelAndView} modelAndView reference to view with name 'collection'
	 */
	@ResourceMapping(value = "getBookmarks")
	public ModelAndView getBookmarks(ResourceRequest request, ResourceResponse response){
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Enter in method \"getBookmarks\"");
		
		Long id = request.getParameter("id")!=null?Long.parseLong(request.getParameter("id")):null;
		int pageNumber = 1;
		ModelAndView modelAndView = new ModelAndView();
		if(id!=null){
			if (StringUtils.isNotBlank(request.getParameter("pageNumber"))){
				pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
			}
			List<CollectionContent> restCollectionBookmarks = this.collectionContentDAO.getCollectionContentsByCollectionId(id, false, pageNumber, PAGESIZE);
			modelAndView.getModelMap().addAttribute("currentBookmarks",(restCollectionBookmarks!=null && restCollectionBookmarks.size()>0)?restCollectionBookmarks:null);
		}
		modelAndView.setViewName("currentBookmark");
		// params
		modelAndView.getModelMap().addAttribute("edit",(StringUtils.isNotBlank(request.getParameter("edit")) && new Boolean(request.getParameter("edit"))));
		User user = (User) request.getAttribute(WebKeys.USER);
		modelAndView.getModelMap().addAttribute("timeZone", user.getTimeZone());
		modelAndView.getModelMap().addAttribute("pageNumber", pageNumber);
		modelAndView.getModelMap().addAttribute("totalNumberOfResults", id!=null?this.collectionContentDAO.countCollectionContentsByCollectionId(id, false):0);
		modelAndView.getModelMap().addAttribute("pageSize", PAGESIZE);
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Exit in method \"getBookmarks\"");
		
		return modelAndView;
	}
	
	/***
 	 * This method gets the searches that are NOT in a collection to show in My Collections in My Pages space
 	 * 
	 * @param request {@link ResourceRequest} gets all data sent by the request
	 * @param response {@link ResourceResponse} gets all data sent by the response
	 * 
	 * @return modelAndView {@link ModelAndView} modelAndView reference to view with name 'collection'
	 */
	@ResourceMapping(value = "getNewSearches")
	public ModelAndView getNewSearches(ResourceRequest request, ResourceResponse response){
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Enter in method \"getNewSearches\"");
		
		Long id = request.getParameter("id")!=null?Long.parseLong(request.getParameter("id")):null;
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("collectionSearch");
		Principal principal = request.getUserPrincipal();
		if(principal!=null){
			long liferayUserId = Long.parseLong(principal.toString());
			int pageNumber = 1;
			if (StringUtils.isNotBlank(request.getParameter("pageNumber"))){
				pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
			}
			List<EadSavedSearch> restCollectionSearches = this.eadSavedSearchDAO.getEadSavedSearchOutOfCollectionByCollectionIdAndLiferayUser(id, liferayUserId, pageNumber, PAGESIZE);
			modelAndView.getModelMap().addAttribute("searches",(restCollectionSearches!=null && restCollectionSearches.size()>0)?restCollectionSearches:null);
			// params
			User user = (User) request.getAttribute(WebKeys.USER);
			modelAndView.getModelMap().addAttribute("timeZone", user.getTimeZone());
			modelAndView.getModelMap().addAttribute("pageNumber", pageNumber);
			modelAndView.getModelMap().addAttribute("totalNumberOfResults", this.eadSavedSearchDAO.countEadSavedSearchOutOfCollectionByCollectionIdAndLiferayUser(id, liferayUserId));
			modelAndView.getModelMap().addAttribute("pageSize", PAGESIZE);
		}
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Exit in method \"getNewSearches\"");
		
		return modelAndView;
	}
	
	/***
 	 * This method gets the bookmarks that are NOT in a collection to show in My Collections in My Pages space
 	 * 
	 * @param request {@link ResourceRequest} gets all data sent by the request
	 * @param response {@link ResourceResponse} gets all data sent by the response
	 * 
	 * @return modelAndView {@link ModelAndView} modelAndView reference to view with name 'collection'
	 */
	@ResourceMapping(value = "getNewBookmarks")
	public ModelAndView getNewBookmarks(ResourceRequest request, ResourceResponse response){
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Enter in method \"getNewBookmarks\"");
		
		Long id = request.getParameter("id")!=null?Long.parseLong(request.getParameter("id")):null;
		Principal principal = request.getUserPrincipal();
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("collectionBookmark");
		if(principal!=null){
			long liferayUserId = Long.parseLong(principal.toString());
			int pageNumber = 1;
			if (StringUtils.isNotBlank(request.getParameter("pageNumber"))){
				pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
			}
			List<SavedBookmarks> restCollectionBookmarks = this.savedBookmarksDAO.getSavedBookmarksOutOfCollectionByCollectionIdAndLiferayUser(id, liferayUserId, pageNumber, PAGESIZE);

			modelAndView.getModelMap().addAttribute("bookmarks",(restCollectionBookmarks!=null && restCollectionBookmarks.size()>0)?restCollectionBookmarks:null);
			// params
			User user = (User) request.getAttribute(WebKeys.USER);
			modelAndView.getModelMap().addAttribute("timeZone", user.getTimeZone());
			modelAndView.getModelMap().addAttribute("pageNumber", pageNumber);
			modelAndView.getModelMap().addAttribute("totalNumberOfResults", this.savedBookmarksDAO.countSavedBookmarksOutOfCollectionByCollectionIdAndLiferayUser(id, liferayUserId));
			modelAndView.getModelMap().addAttribute("pageSize", PAGESIZE);
		}
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Exit in method \"getNewBookmarks\"");
		
		return modelAndView;
	}

	/***
	 * This method order the collections shown in My collections page from My Pages space by the selected header.
	 * 
	 * @param request {@link ResourceRequest} gets all data sent by the request
	 * @param response {@link ResourceResponse} gets all data sent by the response
	 * 
	 * @return modelAndView {@link ModelAndView} modelAndView reference to view with name 'collection'
	 */
	@ResourceMapping(value = "orderResults")
	public ModelAndView orderResults(ResourceRequest request, ResourceResponse response){
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Enter in method \"orderResults\"");
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("home");
		boolean orderAsc=true;
		String orderType = request.getParameter("orderType");
		String orderColumn = request.getParameter("orderColumn");
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_SAVED_COLLECTIONS);
		Principal principal = request.getUserPrincipal();
		if (principal != null){
			Long liferayUserId = Long.parseLong(principal.toString());
			Integer pageNumber = 1;

			if (StringUtils.isNotBlank(request.getParameter("pageNumber"))){
				pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
			}

			// Check if the order should be ascending, descending or no order.
			if (orderType != null) {
				if (orderType.equals("orderAsc")) {
					orderAsc=true;
				} else {
					orderAsc=false;
				}
			} else {
				orderAsc=false;
				orderType = "none";
			}

			// Check if if exists a column selected to order.
			if (orderColumn == null
					|| orderColumn.isEmpty()) {
				orderColumn = "none";
			}

			List<Collection> collections = this.collectionDAO.getCollectionsByUserId(liferayUserId, pageNumber, PAGESIZE, orderColumn, orderAsc);
			User user = (User) request.getAttribute(WebKeys.USER);
			modelAndView.getModelMap().addAttribute("timeZone", user.getTimeZone());
			modelAndView.getModelMap().addAttribute("pageNumber", pageNumber);
			modelAndView.getModelMap().addAttribute("totalNumberOfResults", (collections.size()>0)?this.collectionDAO.countCollectionsByUserId(liferayUserId):collections.size());
			modelAndView.getModelMap().addAttribute("pageSize", PAGESIZE);
			modelAndView.getModelMap().addAttribute("orderType", orderType);
			modelAndView.getModelMap().addAttribute("orderColumn", orderColumn);
			modelAndView.getModelMap().addAttribute("collections", collections);
		}
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Exit in method \"orderResults\"");
		
		return modelAndView;
	}

	/***
	 * This method deletes a collection
	 * 	 
	 * @param request {@link ActionRequest} gets the user id and the collection id
	 */
	@ActionMapping(params="action=deleteSavedCollections")
	public void deleteSavedCollection(ActionRequest request) {
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Enter in method \"deleteSavedCollection\"");
		
		Principal principal = request.getUserPrincipal();
		if (principal != null && request.getParameter("id")!=null){
			Long liferayUserId = Long.parseLong(principal.toString());
			Long id = Long.parseLong(request.getParameter("id"));
			Collection collection = this.collectionDAO.getCollectionByIdAndUserId(id, liferayUserId);
			if (collection!=null && collection.getLiferayUserId()==liferayUserId){
				try {
					this.collectionDAO.delete(collection);
				} catch (Exception e) {
					LOGGER.error(ApeUtil.generateThrowableLog(e));
				}
			}
		}
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Exit in method \"deleteSavedCollection\"");
		
	}

	/***
	 * This method deletes a Collection but not its content
	 * 
	 * @param request {@link ActionRequest} gets the Collection data
	 * 
	 * @throws IOException
	 */
	@ActionMapping(params="action=saveEditCollection")
	public void saveEditedCollection(ActionRequest request) throws IOException {
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Enter in method \"saveEditedCollection\"");
		
		Principal principal = request.getUserPrincipal();
		if (principal != null){
			Long liferayUserId = Long.parseLong(principal.toString());
			Long id = request.getParameter("id")!=null?Long.parseLong(request.getParameter("id")):null;
			boolean public_ = (request.getParameter("collectionField_public")!=null && new String(request.getParameter("collectionField_public")).equals("on"));
			boolean edit = (request.getParameter("collectionField_edit")!=null && new String(request.getParameter("collectionField_edit")).equals("on"));
			String title = request.getParameter("collectionTitle");
			String description = request.getParameter("collectionDescription");
			Collection ddbbCollection = this.collectionDAO.getCollectionByIdAndUserId(id, liferayUserId);
			if (ddbbCollection!=null && title!=null){
				ddbbCollection.setTitle(title);
				ddbbCollection.setPublic_(public_);
				ddbbCollection.setEdit(edit);
				ddbbCollection.setDescription(description);
				ddbbCollection.setModified_date(new Date());
				this.collectionDAO.update(ddbbCollection);
				//collection_search
				Enumeration<String> parametersNames = request.getParameterNames();
				List<Long> parametersIn = new ArrayList<Long>();
				List<Long> parametersOut = new ArrayList<Long>();
				List<Long> bookmarksIn = new ArrayList<Long>();
				List<Long> bookmarksOut = new ArrayList<Long>();
				while(parametersNames.hasMoreElements()){
					String parameterName = parametersNames.nextElement();
					if(parameterName!=null){
						if(parameterName.contains(SEARCH_IN)){
							if(request.getParameter(parameterName).equalsIgnoreCase("on")){
								parametersIn.add(Long.parseLong(parameterName.substring(SEARCH_IN.length())));
							}
						}else if(parameterName.contains(SEARCH_OUT)){
							if(request.getParameter(parameterName).equalsIgnoreCase("on")){
								parametersOut.add(Long.parseLong(parameterName.substring(SEARCH_OUT.length())));
							}
						}else if(parameterName.contains(BOOKMARK_IN)){
							if(request.getParameter(parameterName).equalsIgnoreCase("on")){
								bookmarksIn.add(Long.parseLong(parameterName.substring(BOOKMARK_IN.length())));
							}
						}else if(parameterName.contains(BOOKMARK_OUT)){
							if(request.getParameter(parameterName).equalsIgnoreCase("on")){
								bookmarksOut.add(Long.parseLong(parameterName.substring(BOOKMARK_OUT.length())));
							}
						}
					}
				}

				try {
					//delete
					List<Long> deleteParameters = new ArrayList<Long>();
					deleteParameters.addAll(parametersIn);
					deleteParameters.addAll(bookmarksIn);
					if(deleteParameters.size()>0){
						List<CollectionContent> collectionContentToBeDeleted = this.collectionContentDAO.getAllCollectionContentWithoutIds(deleteParameters,ddbbCollection.getId());
						if(collectionContentToBeDeleted!=null && collectionContentToBeDeleted.size()>0){
							this.collectionContentDAO.delete(collectionContentToBeDeleted);
						}
					}else{
						List<CollectionContent> collectionsContents = this.collectionContentDAO.getCollectionContentsByCollectionId(ddbbCollection.getId());
						if(collectionsContents!=null && collectionsContents.size()>0){
							this.collectionContentDAO.delete(collectionsContents);
						}
					}
					//create
					createCollectionContentParametersBookmarks(parametersOut,bookmarksOut,ddbbCollection,liferayUserId);
				} catch (Exception e) {
					LOGGER.error(ApeUtil.generateThrowableLog(e));
				}
			}
		}
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Exit in method \"saveEditedCollection\"");
		
	}
	
	/***
	 * This method creates a new collection with saved searches and saved bookmarks from</br>
	 * the @ActionMapping(params="action=saveEditCollection") and @ActionMapping(params="action=saveNewCollection").
	 * 
	 * @param parametersOut List {@link Long} list of saved searches id to add to the collection
	 * @param bookmarksOut List {@link Long} list of saved bookmarks id to add to the collection
	 * @param ddbbCollection {@link Collection} collection object
	 * @param liferayUserId {@link Long} current user id
	 * 
	 * @return true if the collection has been stored, false if not
	 */
	private boolean createCollectionContentParametersBookmarks(List<Long> parametersOut, List<Long> bookmarksOut, Collection ddbbCollection, Long liferayUserId) {
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Enter in method \"createCollectionContentParametersBookmarks\"");
		
		if(parametersOut.size()>0 || bookmarksOut.size()>0){
			Iterator<Long> itParamsOut = parametersOut.iterator();
			Iterator<Long> itBookmarksOut = bookmarksOut.iterator();
			List<EadSavedSearch> eadSavedSearches = this.eadSavedSearchDAO.getEadSavedSearchByIdsAndUserid(parametersOut,liferayUserId);
			List<SavedBookmarks> savedBookmarks = this.savedBookmarksDAO.getSavedBookmarksByIdsAndUserid(bookmarksOut, liferayUserId);
			List<CollectionContent> newCollectionContent = new ArrayList<CollectionContent>();
			if(eadSavedSearches!=null){
				while(itParamsOut.hasNext()){
					CollectionContent newCollectionSearch = new CollectionContent();
					newCollectionSearch.setCollection(ddbbCollection);
					Long newEadSavedSearch = itParamsOut.next();
					EadSavedSearch eadSavedSearch = null;
					for(int i=0;eadSavedSearch==null && i<eadSavedSearches.size();i++){
						if(newEadSavedSearch == eadSavedSearches.get(i).getId()){
							eadSavedSearch = eadSavedSearches.get(i);
						}
					}
					if(eadSavedSearch!=null){
						newCollectionSearch.setEadSavedSearch(eadSavedSearch);
						newCollectionContent.add(newCollectionSearch);
					}
				}
			}
			if(savedBookmarks!=null){
				while(itBookmarksOut.hasNext()){
					CollectionContent newCollectionSearch = new CollectionContent();
					newCollectionSearch.setCollection(ddbbCollection);
					Long newBookmark = itBookmarksOut.next();
					SavedBookmarks savedBookmark = null;
					for(int i=0;savedBookmark==null && i<savedBookmarks.size();i++){
						if(newBookmark == savedBookmarks.get(i).getId()){
							savedBookmark = savedBookmarks.get(i);
						}
					}
					if(newBookmark!=null){
						newCollectionSearch.setSavedBookmarks(savedBookmark);
						newCollectionContent.add(newCollectionSearch);
					}
				}
			}
			if(newCollectionContent.size()>0){
				this.collectionContentDAO.store(newCollectionContent);
				if (LOGGER.isDebugEnabled()) 
					LOGGER.debug("Exit in method \"createCollectionContentParametersBookmarks\" return true");
				
				return true;
			}
		}
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Exit in method \"createCollectionContentParametersBookmarks\" return false");
		
		return false;
	}

	/***
	 * from saves a new collection from Create new collection button in My Collection in My Pages space.
	 *  
	 * @param request {@link ActionRequest} gets the new collection object data
	 * "collectionTitle", 
	 * "collectionDescription", 
	 * "collectionField_public", 
	 * "collectionField_public", 
	 * "collectionField_edit"
	 * 
	 * @throws IOException
	 */
	@ActionMapping(params="action=saveNewCollection")
	public void saveCollection(ActionRequest request) throws IOException {
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Enter in method \"saveCollection\"");
		
		Principal principal = request.getUserPrincipal();
		String title = request.getParameter("collectionTitle");
		String description = request.getParameter("collectionDescription");
		boolean public_ = (request.getParameter("collectionField_public")!=null && new String(request.getParameter("collectionField_public")).equals("on"));
		boolean edit = (request.getParameter("collectionField_edit")!=null && new String(request.getParameter("collectionField_edit")).equals("on"));
		Collection newCollection = new Collection();
		if (principal != null && StringUtils.isNotBlank(title)){
			try {
				//collection
				Long liferayUserId = Long.parseLong(principal.toString());
				newCollection.setTitle(title);
				newCollection.setPublic_(public_);
				newCollection.setLiferayUserId(liferayUserId);
				newCollection.setDescription(description);
				newCollection.setEdit(edit);
				newCollection.setModified_date(new Date());
				newCollection = this.collectionDAO.store(newCollection);
				//collection content
				Enumeration<String> parametersNames = request.getParameterNames();
				List<Long> parametersOut = new ArrayList<Long>();
				List<Long> bookmarksOut = new ArrayList<Long>();
				try {
					while(parametersNames.hasMoreElements()){
						String parameterName = parametersNames.nextElement();
						if(parameterName!=null){
							if(parameterName.contains(SEARCH_OUT)){
								if(request.getParameter(parameterName).equalsIgnoreCase("on")){
									parametersOut.add(Long.parseLong(parameterName.substring(SEARCH_OUT.length())));
								}
							}else if(parameterName.contains(BOOKMARK_OUT)){
								if(request.getParameter(parameterName).equalsIgnoreCase("on")){
									bookmarksOut.add(Long.parseLong(parameterName.substring(BOOKMARK_OUT.length())));
								}
							}
						}
					}
					createCollectionContentParametersBookmarks(parametersOut, bookmarksOut, newCollection, liferayUserId);
				} catch (Exception e) {
					LOGGER.error(ApeUtil.generateThrowableLog(e));
				}
			} catch (Exception e) {
				LOGGER.error(ApeUtil.generateThrowableLog(e));
			}
		}
		if (LOGGER.isDebugEnabled()) 
		LOGGER.debug("Exit in method \"saveCollection\"");
	
	}
	
}