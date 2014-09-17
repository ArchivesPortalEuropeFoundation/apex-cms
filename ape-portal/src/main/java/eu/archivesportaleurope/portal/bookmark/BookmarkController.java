package eu.archivesportaleurope.portal.bookmark;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

import eu.apenet.persistence.vo.EadSavedSearch;
import eu.apenet.persistence.vo.SavedBookmarks;
import eu.archivesportaleurope.persistence.jpa.dao.SavedBookmarksJpaDAO;
import eu.archivesportaleurope.portal.common.FriendlyUrlUtil;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.portal.search.saved.SavedSearch;

@Controller(value = "bookmarkController")
@RequestMapping(value = "VIEW")
public class BookmarkController {

    private static final Logger LOGGER = Logger.getLogger(BookmarkController.class);
    private SavedBookmarksJpaDAO savedBookmarksDAO;
    private final static int PAGESIZE  = 10;
	private ResourceBundleMessageSource messageSource;

	public void setMessageSource(ResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
	public void setSavedBookmarksDAO(SavedBookmarksJpaDAO savedBookmarksDAO) {
		this.savedBookmarksDAO = savedBookmarksDAO;
	}

	@ResourceMapping(value="bookmark")
    public ModelAndView showInitialPage(@ModelAttribute("bookmark") Bookmark bookmark, ResourceRequest request) throws PortalException, SystemException {
		boolean loggedIn = request.getUserPrincipal() != null;
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("bookmark");  
        modelAndView.getModelMap().addAttribute("loggedIn",loggedIn);
		SpringResourceBundleSource source = new SpringResourceBundleSource(messageSource, request.getLocale());
    	if (loggedIn){
    		User currentUser = (User) PortalUtil.getUser(request);
    		String description = bookmark.getDescription() + " " + currentUser.getEmailAddress();
    		bookmark.setDescription(description);
				if (saveBookmark(bookmark, request)){
					modelAndView.getModelMap().addAttribute("saved",true);
					modelAndView.getModelMap().addAttribute("message",source.getString("bookmarks.saved.ok"));
				}
				else{
					modelAndView.getModelMap().addAttribute("saved",false);
					modelAndView.getModelMap().addAttribute("message",source.getString("bookmarks.saved.ko"));
				}
    	}
    	else{
			modelAndView.getModelMap().addAttribute("message",source.getString("bookmarks.logged.ko"));
    	}
        return modelAndView;
    }

	public boolean saveBookmark(@ModelAttribute(value = "eaddisplay") Bookmark bookmark,ResourceRequest resourceRequest) {
		boolean saved = false;
		if (resourceRequest.getUserPrincipal() == null){
		}else {
			long liferayUserId = Long.parseLong(resourceRequest.getUserPrincipal().toString());
			try {
				BookmarkService bookmarkService = new BookmarkService(this.savedBookmarksDAO);
				bookmarkService.saveBookmark(liferayUserId, bookmark);
				saved = true;
			}catch (Exception e) {
				LOGGER.error(e.getMessage());
				saved = false;
			}
		}
		return saved;
	}
	
    @ModelAttribute("bookmark")
    public Bookmark getCommandObject() {
        return new Bookmark();
    }
    
	
	@ModelAttribute("savedbookmarks")
	public Bookmark getSavedBookmark(PortletRequest request) {
		Principal principal = request.getUserPrincipal();
		String id = request.getParameter("id");
		Integer pageNumber = 1;
		if (StringUtils.isNotBlank(request.getParameter("pageNumber"))){
			pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
		}
		Bookmark bookmark = new Bookmark();
		bookmark.setOverviewPageNumber(pageNumber.toString());
		if (principal != null && StringUtils.isNotBlank(id)){
			Long liferayUserId = Long.parseLong(principal.toString());
			SavedBookmarks savedBookmark = savedBookmarksDAO.getSavedBookmark(liferayUserId, Long.parseLong(id));
			if (savedBookmark.getLiferayUserId() == liferayUserId){
				bookmark.setLiferay_user_id(Long.toString(liferayUserId));
				bookmark.setBookmarkName(savedBookmark.getName());
				bookmark.setDescription(savedBookmark.getDescription());
				bookmark.setPersistentLink(savedBookmark.getLink());
				bookmark.setTypedocument(savedBookmark.getTypedocument());
				bookmark.setModifiedDate(savedBookmark.getModifiedDate());
				bookmark.setId(savedBookmark.getId() +"");
			}
		}		
		return bookmark;
	}
	
 // --maps the incoming portlet request to this method
 	@RenderMapping
 	public ModelAndView showSavedBookmarks(RenderRequest request) {
 		ModelAndView modelAndView = new ModelAndView();
 		modelAndView.setViewName("home");
 		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_SAVED_BOOKMARK);
 		Principal principal = request.getUserPrincipal();
 		if (principal != null){
 			Integer pageNumber = 1;
 			if (StringUtils.isNotBlank(request.getParameter("pageNumber"))){
 				pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
 			}
			try {
	 			Long liferayUserId = Long.parseLong(principal.toString());
				List<SavedBookmarks> savedBookmarks = savedBookmarksDAO.getSavedBookmarks(liferayUserId, pageNumber, PAGESIZE);
				User user = (User) request.getAttribute(WebKeys.USER);
	 			modelAndView.getModelMap().addAttribute("timeZone", user.getTimeZone());
	 			modelAndView.getModelMap().addAttribute("pageNumber", pageNumber);
	 			modelAndView.getModelMap().addAttribute("totalNumberOfResults", savedBookmarksDAO.countSavedBookmarks(liferayUserId));
	 			modelAndView.getModelMap().addAttribute("pageSize", PAGESIZE);
	 			modelAndView.getModelMap().addAttribute("savedBookmarks",savedBookmarks);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
 		}
 		return modelAndView;
 	}
	
    @ResourceMapping(value="bookmarkAction")
    public ModelAndView showResult(@ModelAttribute("bookmark") Bookmark bookmark, BindingResult result, ResourceRequest request) throws PortalException, SystemException {
        ModelAndView modelAndView = new ModelAndView();
    	boolean loggedIn = request.getUserPrincipal() != null;
    	User currentUser = null;
    	if (loggedIn){
    		currentUser = (User) PortalUtil.getUser(request);
    		modelAndView.getModelMap().addAttribute("currentUser", currentUser);
    	}
		modelAndView.setViewName("success");
		return modelAndView;
    }

	@RenderMapping(params="myaction=editSavedBookmarksForm")
	public String showEditSavedBookmarksForm() {
		return "editSavedBookmarksForm";
	}

	@ActionMapping(params="myaction=saveEditSavedBookmarks")
	public void saveSavedBookmark(@ModelAttribute("savedSearch") Bookmark bookmark, BindingResult bindingResult,ActionRequest request, ActionResponse response) throws IOException  {
		Principal principal = request.getUserPrincipal();
		if (principal != null){
			Long liferayUserId = Long.parseLong(principal.toString());
			SavedBookmarks savedBookmark = savedBookmarksDAO.getSavedBookmark(liferayUserId, Long.parseLong(bookmark.getId()));
			if (savedBookmark.getLiferayUserId() == liferayUserId){
				savedBookmark.setDescription(bookmark.getDescription());
				try {
					savedBookmarksDAO.store(savedBookmark);
					response.sendRedirect(FriendlyUrlUtil.getRelativeUrl(FriendlyUrlUtil.SAVED_BOOKMARKS_OVERVIEW) + FriendlyUrlUtil.SEPARATOR + bookmark.getOverviewPageNumber());
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}	
	}
	
	@ActionMapping(params="myaction=deleteSavedBookmark")
	public void deleteSavedBookmark(ActionRequest request) {
		Principal principal = request.getUserPrincipal();
		if (principal != null){
			Long liferayUserId = Long.parseLong(principal.toString());
			Long id = Long.parseLong(request.getParameter("id"));
			SavedBookmarks savedBookmark = savedBookmarksDAO.getSavedBookmark(liferayUserId, id);
			if (savedBookmark.getLiferayUserId() == liferayUserId){
				try {
					savedBookmarksDAO.delete(savedBookmark);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}
}
