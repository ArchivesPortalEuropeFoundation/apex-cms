/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.archivesportaleurope.portal.api;

/**
 *
 * @author kaisar
 */
public class ApiKey {

    private long liferayUserId;
    private String status;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String domain;
    private String key;

    public ApiKey() {
    }

    public ApiKey(eu.apenet.persistence.vo.ApiKey aPIKey) {
        this.firstName = aPIKey.getFirstName();
        this.lastName = aPIKey.getLastName();
        this.email = aPIKey.getEmailAddress();
        this.key = aPIKey.getApiKey();
        this.domain = aPIKey.getUrl();
        this.liferayUserId = aPIKey.getLiferayUserId();
        this.status = aPIKey.getStatus();
    }

    public long getLiferayUserId() {
        return liferayUserId;
    }

    public void setLiferayUserId(long liferayUserId) {
        this.liferayUserId = liferayUserId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public eu.apenet.persistence.vo.ApiKey getPerApiKey(ApiKey apiKey) {
        eu.apenet.persistence.vo.ApiKey perApiKey = new eu.apenet.persistence.vo.ApiKey();
        perApiKey.setApiKey(apiKey.getKey());
        perApiKey.setEmailAddress(apiKey.getEmail());
        perApiKey.setFirstName(apiKey.getFirstName());
        perApiKey.setLastName(apiKey.getLastName());
        perApiKey.setUrl(apiKey.getDomain());
        perApiKey.setLiferayUserId(apiKey.getLiferayUserId());
        perApiKey.setStatus(apiKey.getStatus());
        return perApiKey;
    }

    @Override
    public String toString() {
        return "ApiKey{" + "liferayUserId=" + liferayUserId + ", status=" + status + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", url=" + domain + ", key=" + key + '}';
    }

}
