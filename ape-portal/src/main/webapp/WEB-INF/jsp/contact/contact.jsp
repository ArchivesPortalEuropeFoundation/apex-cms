<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<portlet:defineObjects />
<div align="center">
    <h2><fmt:message key="contact.form.title" /></h2>
</div>
<form id="contact" name="contact" action="/Portal/contact.action" method="post">
    <table class="contactForm">
        <tr>
            <td class="tdLabel">
                <label for="contact_email" class="label"><fmt:message key="label.email.contact" /><span class="required">*</span>:</label>
            </td>
            <td>
                <input type="text" name="email" size="50" value="" id="contact_email" style="margin-bottom:10px;"/>
            </td>
        </tr>
        <tr>
            <td class="tdLabel">
                <label for="contact_topicSubject" class="label"><fmt:message key="label.email.subject" /><span class="required">*</span>:</label>
            </td>
            <td>
                <select name="topicSubject" id="contact_topicSubject">
                    <option value="-1">--- <fmt:message key="label.contact.items.select" /> ---</option>
                    <option value="1"><fmt:message key="label.contact.item.issues" /></option>
                    <option value="2"><fmt:message key="label.contact.item.contribute" /></option>
                    <option value="3"><fmt:message key="label.contact.item.suggestions" /></option>
                </select>
            </td>
        </tr>
        <tr>
            <td class="tdLabel">
                <label for="contact_feedbackText" class="label"><fmt:message key="label.feedback.comments" /><span
                        class="required">*</span>:</label>
            </td>
            <td>
                <textarea name="feedbackText" cols="" rows="" id="contact_feedbackText"
                          style="width:500px; height:180px;vertical-align:middle"></textarea>
            </td>
        </tr>
        <tr>
            <td></td>
            <td>
                <div id="contact_" class="apnetReCaptcha">
                    <input type="hidden" name="hidden_flag_recaptcha" value="" id="hidden_flag_recaptcha"/>
                    <script type="text/javascript" src="http://api.recaptcha.net/challenge?k=6Ld_kMISAAAAAHn-b8Ds2U_14kMykzAbTFW9kqX1"></script>
                </div>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <fmt:message key="feedbackText.info.tips" />
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <table>
                    <tr>
                        <td class="leftBtn">
                            <input type="submit" id="contact_label_feedback_send" name="method:execute" value="<fmt:message key="label.feedback.send" />" class="mainButton"/>
                        </td>
                        <td>
                            <input type="submit" id="contact_label_cancel" name="action:index" value="<fmt:message key="label.cancel" />" onclick="form.onsubmit=null"/>
                        </td>
                        <td class="rightBtn">
                            <input type="reset" name="label.reset" value="<fmt:message key="label.reset" />"/>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</form>