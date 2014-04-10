package eu.archivesportaleurope.portal.common;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.support.AbstractMessageSource;

public class ApeResourceBundleMessageSource extends AbstractMessageSource {
	private static final String LANGUAGE_PREFIX = "language.";
	private static final String COUNTRY_PREFIX = "country.";
    private Map<String, Locale> languages = new HashMap<String, Locale>();
    public ApeResourceBundleMessageSource(){
        String[] isoLanguages = Locale.getISOLanguages();

        // Add 639-2/B variants below this line
        languages.put("alb", new Locale("sq"));
        languages.put("arm", new Locale("hy"));
        languages.put("baq", new Locale("eu"));
        languages.put("bur", new Locale("my"));
        languages.put("chi", new Locale("zh"));
        languages.put("cze", new Locale("cs"));
        languages.put("dut", new Locale("nl"));
        languages.put("fre", new Locale("fr"));
        languages.put("geo", new Locale("ka"));
        languages.put("ger", new Locale("de"));
        languages.put("gre", new Locale("el"));
        languages.put("ice", new Locale("is"));
        languages.put("mac", new Locale("mk"));
        languages.put("mao", new Locale("mi"));
        languages.put("may", new Locale("ms"));
        languages.put("per", new Locale("fa"));
        languages.put("rum", new Locale("ro"));
        languages.put("slo", new Locale("sk"));
        languages.put("tib", new Locale("bo"));
        languages.put("wel", new Locale("cy"));
        // Add remaining 639-2 variants; any T variants of countries listed above will not be added to the set
        for (String language : isoLanguages) {
            Locale locale = new Locale(language);
            String languageCode = locale.getISO3Language();
            languages.put(languageCode, locale);
        }
    }
    protected String getMessageInternal(String code, Object[] args, Locale locale) {
		if (code != null){
			if (code.startsWith(LANGUAGE_PREFIX)){
				try {
					String language = code.substring(LANGUAGE_PREFIX.length());
					String result = languages.get(language).getDisplayLanguage(locale);
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
