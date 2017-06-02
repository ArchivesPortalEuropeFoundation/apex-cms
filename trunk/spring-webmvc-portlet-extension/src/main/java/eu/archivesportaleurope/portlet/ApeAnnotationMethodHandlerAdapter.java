package eu.archivesportaleurope.portlet;

import javax.portlet.PortletRequest;
import javax.portlet.StateAwareResponse;

import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.Errors;

import eu.archivesportaleurope.portlet.spring.AnnotationMethodHandlerAdapter;


/**
 * This class is used to prevent Spring to use always the session for form binding
 * @author bastiaan
 *
 */
public class ApeAnnotationMethodHandlerAdapter extends AnnotationMethodHandlerAdapter  {

	@Override
	protected void persistImplicitModel(PortletRequest request,
			StateAwareResponse stateResponse, ExtendedModelMap implicitModel) {
		boolean hasErrors = false;
		for (Object object: implicitModel.values()){
			if (object instanceof Errors){
				Errors errors = (Errors) object;
				hasErrors = hasErrors || errors.hasErrors();
			}
		}
		if (hasErrors){
			super.persistImplicitModel(request, stateResponse, implicitModel);
		}
	}


}
