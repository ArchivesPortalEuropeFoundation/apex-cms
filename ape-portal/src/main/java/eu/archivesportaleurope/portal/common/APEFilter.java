package eu.archivesportaleurope.portal.common;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.filter.ActionFilter;
import javax.portlet.filter.EventFilter;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.RenderFilter;
import javax.portlet.filter.ResourceFilter;

import org.apache.log4j.Logger;

import eu.apenet.commons.utils.APEnetUtilities;
import eu.archivesportaleurope.persistence.jpa.JpaUtil;

public class APEFilter implements RenderFilter, ResourceFilter, ActionFilter, EventFilter {
    
    private static final Logger log = Logger.getLogger(APEFilter.class);
    private static final String LOG_STACKTRACES = "LOG_STACKTRACES";
    private boolean logStackTraces = false;

    
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(FilterConfig filterConfig) throws PortletException {
    	String logStackTracesString = filterConfig.getInitParameter(LOG_STACKTRACES);
	    logStackTraces = "true".equals(logStackTracesString);
	    log.info("JPA Filter started. Log stacktraces=" + logStackTraces);
		
	}

	@Override
	public void doFilter(ActionRequest request, ActionResponse response, FilterChain chain) throws IOException,
			PortletException {
        // There is actually no explicit "opening" of a Session, the
        // first call to HibernateUtil.beginTransaction() will get
        // a fresh Session.
        try {
            chain.doFilter(request, response);
            Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
            if (exception != null){
            	throw exception;
            }
            // Commit any pending database transaction.
            try {
            	JpaUtil.commitDatabaseTransaction();
            } catch (Exception de) {
            	logError(de);
            }

        } catch (Exception e) {
        	logError(e);
            // If an exception occurs and there is an open transaction, roll back the transaction.
            try {
            	
                JpaUtil.rollbackDatabaseTransaction();
            }

            catch (Exception de) {
            	logError(de);
            }

        } 
        finally {

            // No matter what happens, close the Session.
            try {
            	JpaUtil.closeDatabaseSession();
            }

            catch (Exception de) {
            	logError(de);
            }

        }		
	}

	@Override
	public void doFilter(ResourceRequest request, ResourceResponse response, FilterChain chain) throws IOException,
			PortletException {
        // There is actually no explicit "opening" of a Session, the
        // first call to HibernateUtil.beginTransaction() will get
        // a fresh Session.
        try {
            chain.doFilter(request, response);
            Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
            if (exception != null){
            	throw exception;
            }
            // Commit any pending database transaction.
            try {
            	JpaUtil.commitDatabaseTransaction();
            } catch (Exception de) {
            	logError(de);
            }

        } catch (Exception e) {
        	logError(e);
            // If an exception occurs and there is an open transaction, roll back the transaction.
            try {
            	
            	JpaUtil.rollbackDatabaseTransaction();
            }

            catch (Exception de) {
            	logError(de);
            }

        } 
        finally {

            // No matter what happens, close the Session.
            try {
            	JpaUtil.closeDatabaseSession();
            }

            catch (Exception de) {
            	logError(de);
            }

        }
		
	}

	@Override
	public void doFilter(RenderRequest request, RenderResponse response, FilterChain chain) throws IOException,
			PortletException {
        // There is actually no explicit "opening" of a Session, the
        // first call to HibernateUtil.beginTransaction() will get
        // a fresh Session.
		response.setContentType("text/html;charset=UTF-8");
        try {
            chain.doFilter(request, response);
            Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
            if (exception != null){
            	throw exception;
            }
            // Commit any pending database transaction.
            try {
            	JpaUtil.commitDatabaseTransaction();
            } catch (Exception de) {
            	logError(de);
            }

        } catch (Exception e) {
        	logError(e);
            // If an exception occurs and there is an open transaction, roll back the transaction.
            try {
            	
            	JpaUtil.rollbackDatabaseTransaction();
            }

            catch (Exception de) {
            	logError(de);
            }

        } 
        finally {

            // No matter what happens, close the Session.
            try {
            	JpaUtil.closeDatabaseSession();
            }

            catch (Exception de) {
            	logError(de);
            }

        }
		
	}

	@Override
	public void doFilter(EventRequest request, EventResponse response, FilterChain chain) throws IOException, PortletException {
        // There is actually no explicit "opening" of a Session, the
        // first call to HibernateUtil.beginTransaction() will get
        // a fresh Session.
        try {
            chain.doFilter(request, response);
            Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
            if (exception != null){
            	throw exception;
            }
            // Commit any pending database transaction.
            try {
            	JpaUtil.commitDatabaseTransaction();
            } catch (Exception de) {
            	logError(de);
            }

        } catch (Exception e) {
        	logError(e);
            // If an exception occurs and there is an open transaction, roll back the transaction.
            try {
            	
            	JpaUtil.rollbackDatabaseTransaction();
            }

            catch (Exception de) {
            	logError(de);
            }

        } 
        finally {

            // No matter what happens, close the Session.
            try {
            	JpaUtil.closeDatabaseSession();
            }

            catch (Exception de) {
            	logError(de);
            }

        }
		
	}

    private void logError(Throwable e){
    	if (logStackTraces){
    		log.error(APEnetUtilities.generateThrowableLog(e));
    	}else {
    		log.error(e.getMessage());
    	}
    }

}
