package eu.archivesportaleurope.portal.search.saved;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.portlet.RenderRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

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

@Controller(value = "collectionController")
@RequestMapping(value = "VIEW")
public class CollectionController {
	
	private final static Logger LOGGER = Logger.getLogger(CollectionController.class);
	private final static int PAGESIZE  = 10;
	private static final String SEARCH_IN = "selected_search_";
	private static final String SEARCH_OUT = "new_search_";
	private static final String BOOKMARK_IN = "collection_bookmark_";
	private static final String BOOKMARK_OUT = "new_bookmark_";
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

	@RenderMapping
	public ModelAndView showSavedCollections(RenderRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("home");
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_SAVED_COLLECTIONS);
		Principal principal = request.getUserPrincipal();
		if (principal != null){
			Long liferayUserId = Long.parseLong(principal.toString());
			Integer pageNumber = 1;
			if (StringUtils.isNotBlank(request.getParameter("pageNumber"))){
				pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
			}
			List<Collection> collections = this.collectionDAO.getCollectionsByUserId(liferayUserId, pageNumber, PAGESIZE);
			User user = (User) request.getAttribute(WebKeys.USER);
			modelAndView.getModelMap().addAttribute("timeZone", user.getTimeZone());
			modelAndView.getModelMap().addAttribute("pageNumber", pageNumber);
			modelAndView.getModelMap().addAttribute("totalNumberOfResults", (collections.size()>0)?this.collectionDAO.countCollectionsByUserId(liferayUserId):collections.size());
			modelAndView.getModelMap().addAttribute("pageSize", PAGESIZE);
			modelAndView.getModelMap().addAttribute("collections",collections);

		}
		return modelAndView;
	}
	
	@RenderMapping(params="action=viewCollection")
	public ModelAndView viewCollection(RenderRequest request){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("collection");
		Principal principal = request.getUserPrincipal();
		if(principal!=null && request.getParameter("id")!=null){ //edit checks
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
		}
		return modelAndView;
	}
	
	@RenderMapping(params="action=createNewCollection")
	public ModelAndView createNewCollection(RenderRequest request){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("collection");
		modelAndView.getModelMap().addAttribute("edit",true);
		Principal principal = request.getUserPrincipal();
		long liferayUserId = Long.parseLong(principal.toString());
		Long id = request.getParameter("id")!=null?Long.parseLong(request.getParameter("id")):null;
		if(principal!=null && id!=null){ //edit checks
			Collection targetCollection = this.collectionDAO.getCollectionByIdAndUserId(id, liferayUserId);
			if(targetCollection!=null){
				modelAndView.getModelMap().addAttribute("collection",targetCollection);
				CollectionContentDAO collectionContentDAO = this.collectionContentDAO;
				List<CollectionContent> listCollectionContent = collectionContentDAO.getCollectionContentsByCollectionId(id);
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
		}
		List<EadSavedSearch> restCollectionSearches = this.eadSavedSearchDAO.getEadSavedSearchOutOfCollectionByCollectionIdAndLiferayUser(id, liferayUserId);
		if(restCollectionSearches!=null && restCollectionSearches.size()>0){
			modelAndView.getModelMap().addAttribute("newSearches",restCollectionSearches);
		}
		List<SavedBookmarks> restCollectionBookmarks = this.savedBookmarksDAO.getSavedBookmarksOutOfCollectionByCollectionIdAndLiferayUser(id, liferayUserId);
		if(restCollectionBookmarks!=null && restCollectionBookmarks.size()>0){
			modelAndView.getModelMap().addAttribute("newBookmarks",restCollectionBookmarks);
		}
		return modelAndView;
	}

	@RenderMapping(params="action=deleteSavedCollections")
	public ModelAndView deleteSavedCollection(RenderRequest request) {
		Principal principal = request.getUserPrincipal();
		if (principal != null && request.getParameter("id")!=null){
			Long liferayUserId = Long.parseLong(principal.toString());
			Long id = Long.parseLong(request.getParameter("id"));
			Collection collection = this.collectionDAO.getCollectionByIdAndUserId(id, liferayUserId);
			if (collection!=null && collection.getLiferayUserId()==liferayUserId){
				this.collectionDAO.delete(collection);
			}
		}
		return showSavedCollections(request);
	}

	@RenderMapping(params="action=saveEditCollection")
	public ModelAndView saveEditedCollection(RenderRequest request) throws IOException {
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
					if(parametersIn.size()>0){
						List<CollectionContent> collectionContentToBeDeleted = collectionContentDAO.getAllCollectionContentWithoutIds(parametersIn,ddbbCollection.getId());
						if(collectionContentToBeDeleted!=null && collectionContentToBeDeleted.size()>0){
							collectionContentDAO.delete(collectionContentToBeDeleted);
						}
					}
					//create
					createCollectionContentParametersBookmarks(parametersOut,bookmarksOut,ddbbCollection,liferayUserId);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
		return showSavedCollections(request);
	}
	
	private boolean createCollectionContentParametersBookmarks(List<Long> parametersOut, List<Long> bookmarksOut, Collection ddbbCollection, Long liferayUserId) {
		if(parametersOut.size()>0 || bookmarksOut.size()>0){
			Iterator<Long> itParamsOut = parametersOut.iterator();
			Iterator<Long> itBookmarksOut = bookmarksOut.iterator();
			List<EadSavedSearch> eadSavedSearches = this.eadSavedSearchDAO.getEadSavedSearchByIdsAndUserid(parametersOut,liferayUserId);
			List<SavedBookmarks> savedBookmarks = this.savedBookmarksDAO.getSavedSearchesByIdsAndUserid(bookmarksOut,liferayUserId);
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
				return true;
			}
		}
		return false;
	}

	@RenderMapping(params="action=saveNewCollection")
	public ModelAndView saveCollection(RenderRequest request) throws IOException {
		Principal principal = request.getUserPrincipal();
		String title = request.getParameter("collectionTitle");
		String description = request.getParameter("collectionDescription");
		boolean public_ = (request.getParameter("collectionField_public")!=null && new String(request.getParameter("collectionField_public")).equals("on"));
		boolean edit = (request.getParameter("collectionField_edit")!=null && new String(request.getParameter("collectionField_edit")).equals("on"));
		Collection newCollection = new Collection();
		if (principal != null && StringUtils.isNotBlank(title)){
			//collection
			Long liferayUserId = Long.parseLong(principal.toString());
			newCollection.setTitle(title);
			newCollection.setPublic_(public_);
			newCollection.setLiferayUserId(liferayUserId);
			newCollection.setDescription(description);
			newCollection.setEdit(edit);
			newCollection = this.collectionDAO.store(newCollection);
			//collection content
			Enumeration<String> parametersNames = request.getParameterNames();
			List<Long> parametersOut = new ArrayList<Long>();
			List<Long> bookmarksOut = new ArrayList<Long>();
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
		}
		return showSavedCollections(request);
	}
}