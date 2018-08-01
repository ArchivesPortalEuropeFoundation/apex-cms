<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>

<portlet:actionURL var="contactUrl">
    <portlet:param name="myaction" value="contact" />
</portlet:actionURL>

<script type="text/javascript">
    var RecaptchaOptions = {
        theme: 'white'
    };
    $(document).ready(function () {
        $('#contactForm').submit(function () {
            $('input[type=submit]', this).attr('disabled', 'disabled');
        });
    });

</script>
<div id="contactPortlet">
    <h1><fmt:message key="contact.form.title" /></h1>

    <form:form id="contactForm" name="contactForm" commandName="contact" method="post" action="${contactUrl}">
        <table class="contactForm">
            <tr>
                <td class="tdLabel">
                    <label for="contact_username" class="label"><fmt:message key="feedback.username" /><span class="required">*</span>:</label>
                </td>
                <td>
            <form:input path="username" type="text" name="username" size="50" id="contact_username" /><form:errors path="username" cssClass="errorBlock" />
            </td>
            </tr> 
            <tr>
                <td class="tdLabel">
                    <label for="contact_email" class="label"><fmt:message key="label.email.contact" /><span class="required">*</span>:</label>
                </td>
                <td>
            <form:input path="email" type="text" name="email" size="50" id="contact_email" /><form:errors path="email" cssClass="errorBlock" />
            </td>
            </tr>
            <tr>
                <td> <label for="receiveCopy" class="label"><fmt:message key="label.email.contact.receivecopy" />:</label></td>
                <td><form:checkbox path="receiveCopy" id="receiveCopy"/> </td>
            </tr>
            <tr>
                <td class="tdLabel">
                    <label for="contact_topicSubject" class="label"><fmt:message key="label.email.subject" /><span class="required">*</span>:</label>
                </td>
                <td>
            <form:select path="type" name="topicSubject" id="contact_topicSubject" >
                <form:option value="">--- <fmt:message key="label.contact.items.select" /> ---</form:option>
                <form:options items="${contact.typeList }"/>
            </form:select><form:errors path="type" cssClass="errorBlock" />
            </td>
            </tr>
            <tr>
                <td class="tdLabel">
                    <label for="contact_feedbackText" class="label"><fmt:message key="label.feedback.comments" /><span class="required">*</span>:</label>
                </td>
                <td>
            <form:textarea path="feedback" name="feedbackText" cols="" rows="" id="contact_feedbackText" /> <form:errors path="feedback" cssClass="errorBlock" />
            </td>
            </tr>
            <c:if test="${!loggedIn}">
                <!-- then you are not logged -->     
                <tr>
                    <td></td>
                    <td>
                        <script src='https://www.google.com/recaptcha/api.js'></script>
                <div class="g-recaptcha" data-sitekey="6Lc2ElkUAAAAABPVZhmI-Da9khZvYdUbORKueJVt"></div>
                <form:errors path="captcha" cssClass="errorBlock" />
                </td>
                </tr> 
            </c:if>
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
                                <input type="submit" id="contact_label_feedback_send" value="<fmt:message key="label.feedback.send" />" class="mainButton"/>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </form:form>
</div>