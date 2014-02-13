package eu.archivesportaleurope.portal.contact;

import java.io.Serializable;

import com.liferay.portal.kernel.util.PropsUtil;

/**
 * User: Yoann Moranville
 * Date: 09/01/2013
 *
 * @author Yoann Moranville
 */
public class Contact implements Serializable {

    private String email;
    private String type;
    private String feedback;
    private String captcha;
    private String recaptcha_challenge_field;
    private String recaptcha_response_field;
    private String recaptchaPubKey = PropsUtil.get("captcha.engine.recaptcha.key.public");

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

	public String getRecaptchaPubKey() {
		return recaptchaPubKey;
	}

	public void setRecaptchaPubKey(String recaptchaPubKey) {
		this.recaptchaPubKey = recaptchaPubKey;
	}
}
