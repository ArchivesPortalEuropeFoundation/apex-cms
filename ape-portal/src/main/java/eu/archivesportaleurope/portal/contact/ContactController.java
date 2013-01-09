package eu.archivesportaleurope.portal.contact;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

/**
 * User: Yoann Moranville
 * Date: 09/01/2013
 *
 * @author Yoann Moranville
 */

@Controller(value = "contactController")
@RequestMapping(value = "VIEW")
public class ContactController {

    @ResourceMapping(value = "contact")
    public ModelAndView displayContact(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("contact");
        return modelAndView;
    }

}
