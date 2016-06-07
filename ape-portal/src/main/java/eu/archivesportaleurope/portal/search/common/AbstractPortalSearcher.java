/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.archivesportaleurope.portal.search.common;

import eu.apenet.commons.solr.AbstractSearcher;
import eu.apenet.commons.solr.SolrQueryParameters;
import eu.archivesportaleurope.portal.common.PropertiesKeys;
import eu.archivesportaleurope.portal.common.PropertiesUtil;
import eu.archivesportaleurope.portal.common.email.EmailSender;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

/**
 * Refactored code, to reduce code duplication. Original author is (most probably) bastiaan
 * 
 * @author mahbub
 */
public abstract class AbstractPortalSearcher extends AbstractSearcher {

    private final static Long RESEND_EMAIL_TIME  = PropertiesUtil.getLong(PropertiesKeys.APE_SOLR_RESEND_EMAIL_TIME_WAIT);
    private static Long lastTimeEmailSend  = 0l;
    
    static {
        TIME_ALLOWED = PropertiesUtil.getInt(PropertiesKeys.APE_MAX_SOLR_QUERY_TIME);
        TIME_ALLOWED_TREE = PropertiesUtil.getInt(PropertiesKeys.APE_MAX_SOLR_QUERY_TREE_TIME);
        HTTP_TIMEOUT = PropertiesUtil.getInt(PropertiesKeys.APE_SOLR_HTTP_TIMEOUT);
    }
    

    protected QueryResponse executeQuery(SolrQuery query, SolrQueryParameters solrQueryParameters, String queryType,
            boolean needSuggestions) throws SolrServerException {
        try {
            return executeQuery(query, solrQueryParameters, queryType, needSuggestions, false);
        } catch (SolrServerException sse) {
            long currentTime = System.currentTimeMillis();
            long lastTimeSend = currentTime - RESEND_EMAIL_TIME;
            boolean send = false;
            synchronized (lastTimeEmailSend) {
                if (lastTimeSend > lastTimeEmailSend) {
                    lastTimeEmailSend = currentTime;
                    send = true;

                }
            }
            if (send) {
                EmailSender.sendExceptionToAdmin("Solr search engine has problems", sse);
            }
            throw sse;
        }
    }
}
