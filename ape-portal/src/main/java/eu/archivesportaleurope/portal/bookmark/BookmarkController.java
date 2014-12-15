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
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.servlet.ModelAndView;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

import eu.apenet.persistence.vo.SavedBookmarks;
import eu.archivesportaleurope.persistence.jpa.dao.SavedBookmarksJpaDAO;
import eu.archivesportaleurope.portal.common.FriendlyUrlUtil;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.util.ApeUtil;

@Controller(value = "bookmarkController")
@RequestMapping(value = "VIEW")
public class BookmarkController {

    private static final Logger LOGGER = Logger.getLogger(BookmarkController.class);
    private SavedBookmarksJpaDAO savedBookmarksDAO;
    private final static int PAGESIZE  = 10;
	
	public void setSavedBookmarksDAO(SavedBookmarksJpaDAO savedBookmarksDAO) {
		this.savedBookmarksDAO = savedBookmarksDAO;
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
				LOGGER.error(ApeUtil.generateThrowableLog(e));
			}
 		}
 		return modelAndView;
 	}

	@RenderMapping(params="myaction=editSavedBookmarksForm")
	public String showEditSavedBookmarksForm() {
		return "editSavedBookmarksForm";
	}
	
	@ActionMapping(params="myaction=saveEditSavedBookmarks")
	public void saveSavedBookmark(@ModelAttribute("savedBookmark") Bookmark bookmark, BindingResult bindingResult,ActionRequest request, ActionResponse response) throws IOException  {
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
					LOGGER.error(ApeUtil.generateThrowableLog(e));
				}
			}
		}	
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
	
    @ModelAttribute("bookmark")
    public Bookmark getCommandObject() {
        return new Bookmark();
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
	
}
