<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<portlet:defineObjects />
<%--<portlet:resourceURL var="directoryTreeUrl" id="directoryTree" />--%>
<div align="center">
    <h2>Contact form</h2>
</div>
<form id="contact" name="contact" action="/Portal/contact.action" method="post">
    <table class="contactForm">
        <tr>
            <td class="tdLabel">
                <label for="contact_email" class="label">Your e-mail address<span class="required">*</span>:</label>
            </td>
            <td>
                <input type="text" name="email" size="50" value="" id="contact_email" style="margin-bottom:10px;"/>
            </td>
        </tr>
        <tr>
            <td class="tdLabel">
                <label for="contact_topicSubject" class="label">Subject<span class="required">*</span>:</label>
            </td>
            <td>
                <select name="topicSubject" id="contact_topicSubject">
                    <option value="-1">--- Please select a topic ---</option>
                    <option value="1">Technical issues</option>
                    <option value="2">How to join and contribute</option>
                    <option value="3">Suggestions and other issues</option>
                </select>
            </td>
        </tr>
        <tr>
            <td class="tdLabel">
                <label for="contact_feedbackText" class="label">Your feedback or comments<span
                        class="required">*</span>:</label>
            </td>
            <td>
                <textarea name="feedbackText" cols="" rows="" id="contact_feedbackText"
                          style="width:500px; height:180px;vertical-align:middle"></textarea>
            </td>
        </tr>
        <!-- Due to layout lateral effects. If you remove it then the position of ReCaptcha will be wrong -->
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
                Thank you very much for your feedback. If your feedback requires a direct response, a member of the Archives
                Portal Europe team will get back to you as soon as possible. For faster response feedback should preferably
                be submitted in English.
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <table>
                    <tr>
                        <td class="leftBtn">
                            <input type="submit" id="contact_label_feedback_send" name="method:execute" value="Send feedback" class="mainButton"/>
                        </td>
                        <td>
                            <input type="submit" id="contact_label_cancel" name="action:index" value="Cancel" onclick="form.onsubmit=null"/>
                        </td>
                        <td class="rightBtn">
                            <input type="reset" name="label.reset" value="Reset"/>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</form>