package eu.archivesportaleurope.portal.common;

import java.text.MessageFormat;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.AbstractMessageSource;

import eu.apenet.commons.utils.APEnetUtilities;

public class ApeResourceBundleMessageSource extends AbstractMessageSource {
	private static final String LANGUAGE_PREFIX = "language.";
	private static final String COUNTRY_PREFIX = "country.";

    protected String getMessageInternal(String code, Object[] args, Locale locale) {
		if (code != null){
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
