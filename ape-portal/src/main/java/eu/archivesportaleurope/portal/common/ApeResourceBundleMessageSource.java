package eu.archivesportaleurope.portal.common;

import java.text.MessageFormat;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.AbstractMessageSource;

import eu.apenet.commons.utils.APEnetUtilities;
import eu.apenet.commons.utils.Cache;
import eu.apenet.commons.utils.CacheManager;
import eu.apenet.persistence.dao.TopicDAO;

public class ApeResourceBundleMessageSource extends AbstractMessageSource {
	private static final String LANGUAGE_PREFIX = "language.";
	private static final String COUNTRY_PREFIX = "country.";
	private static final String TOPIC_PREFIX = "topic.";
	private final static Cache<String, String> topicCache = CacheManager.getInstance().<String, String>initCache("topicCache");
	private TopicDAO topicDAO;
	
    public TopicDAO getTopicDAO() {
		return topicDAO;
	}
	public void setTopicDAO(TopicDAO topicDAO) {
		this.topicDAO = topicDAO;
	}
	protected String getMessageInternal(String code, Object[] args, Locale locale) {
		if (StringUtils.isNotBlank(code)){
			if (code.startsWith(LANGUAGE_PREFIX)){
				try {
					String language = code.substring(LANGUAGE_PREFIX.length());
					String result = APEnetUtilities.getIso3ToIso2LanguageCodesMap().get(language).getDisplayLanguage(locale);
					if (StringUtils.isNotBlank(result)){
						return result;
					}
				}catch (Exception e){
					
				}
			}else if (code.startsWith(COUNTRY_PREFIX)){
				try {
					String country = code.substring(COUNTRY_PREFIX.length());
					
					if (StringUtils.isNotBlank(country)){
						country = country.replace("_", " ");
						country = country.substring(0,1).toUpperCase() + country.substring(1);
						return country;
					}
				}catch (Exception e){
					
				}
			}else if (topicDAO != null && code.startsWith(TOPIC_PREFIX)){
				try {
					String topicCode = code.substring(TOPIC_PREFIX.length());
					String topic = topicCache.get(topicCode);
					if (topic == null){
						topic = topicDAO.getDescription(topicCode);
						if (topic != null){
							topicCache.put(topicCode, topic);
						}
					}
					return topic;
				}catch (Exception e){
					
				}
			}
		}   	
		return getMessageFromParent(code, args, locale);
    }
	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}

}
