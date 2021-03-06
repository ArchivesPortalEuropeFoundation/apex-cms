package eu.archivesportaleurope.portal.contact;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import eu.archivesportaleurope.portal.common.PropertiesKeys;
import eu.archivesportaleurope.portal.common.PropertiesUtil;

/**
 * User: Yoann Moranville Date: 09/01/2013
 *
 * @author Yoann Moranville
 */
public class Contact implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1372031592145506858L;
    private String username;
    private String email;
    private String type;
    private String title;
    private String aiId;
    private String url;
    private String feedback;
    private String captcha;
    private String institution;
    private String repoCode;
    private String recaptcha_challenge_field;
    private String recaptcha_response_field;
    private String recaptchaPubKey = PropertiesUtil.get(PropertiesKeys.LIFERAY_RECAPTCHA_PUB_KEY);
    private String reCaptchaUrl_script = PropertiesUtil.get(PropertiesKeys.LIFERAY_RECAPTCHA_URL);
    private String reCaptchaV2_response;
    private boolean receiveCopy = true;
    private Map<String, String> typeList = new LinkedHashMap<String, String>();

    public String getReCaptchaUrl() {
        return reCaptchaUrl_script + recaptchaPubKey;
    }

    public String getRecaptchaPubKey() {
        return recaptchaPubKey;
    }

    public void setRecaptchaPubKey(String recaptchaPubKey) {
        this.recaptchaPubKey = recaptchaPubKey;
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

    public String getAiId() {
        return aiId;
    }

    public void setAiId(String aiId) {
        this.aiId = aiId;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getRepoCode() {
        return repoCode;
    }

    public void setRepoCode(String repoCode) {
        this.repoCode = repoCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReCaptchaV2_response() {
        return reCaptchaV2_response;
    }

    public void setReCaptchaV2_response(String reCaptchaV2_response) {
        this.reCaptchaV2_response = reCaptchaV2_response;
    }
    
}
