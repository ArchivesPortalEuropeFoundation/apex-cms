package eu.archivesportaleurope.portal.contact;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import eu.archivesportaleurope.portal.common.PropertiesKeys;
import eu.archivesportaleurope.portal.common.PropertiesUtil;


/**
 * User: Yoann Moranville
 * Date: 09/01/2013
 *
 * @author Yoann Moranville
 */
public class Contact implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1372031592145506858L;
	private String email;
    private String type;
    private String title;
    private String url;
    private String feedback;
    private String captcha;
    private String recaptcha_challenge_field;
    private String recaptcha_response_field;
    private String recaptchaPubKey = PropertiesUtil.get(PropertiesKeys.LIFERAY_RECAPTCHA_PUB_KEY);
    private String reCaptchaUrl_script = PropertiesUtil.get(PropertiesKeys.LIFERAY_RECAPTCHA_URL);
    private boolean receiveCopy = true;
    private Map<String,String> typeList = new LinkedHashMap<String,String>();

    public String getReCaptchaUrl(){
    	return reCaptchaUrl_script + recaptchaPubKey;
    }

	public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    
    public String getRecaptcha_challenge_field() {
		return recaptcha_challenge_field;
	}

	public void setRecaptcha_challenge_field(String recaptcha_challenge_field) {
		this.recaptcha_challenge_field = recaptcha_challenge_field;
	}

	public String getRecaptcha_response_field() {
		return recaptcha_response_field;
	}

	public void setRecaptcha_response_field(String recaptcha_response_field) {
		this.recaptcha_response_field = recaptcha_response_field;
	}

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isReceiveCopy() {
		return receiveCopy;
	}

	public void setReceiveCopy(boolean receiveCopy) {
		this.receiveCopy = receiveCopy;
	}

	public Map<String, String> getTypeList() {
		return typeList;
	}

	public void setTypeList(Map<String, String> typeList) {
		this.typeList = typeList;
	}


}
