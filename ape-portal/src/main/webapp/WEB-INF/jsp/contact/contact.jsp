<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<portlet:defineObjects />
<portlet:renderURL var="contactUrl">
    <portlet:param name="myaction" value="contact" />
</portlet:renderURL>
<portlet:resourceURL escapeXml="false" id="contact"  var="contact" />
<%--<liferay-portlet:renderURL var="contactUrl"  plid="${contactPlId}" portletName="${contactPortletId}">--%>

<div align="center">
    <h2><fmt:message key="contact.form.title" /></h2>
</div>
<c:choose>
    <c:when test="${empty success}">
        <form:form id="contactForm" name="contactForm" commandName="contact" method="post" action="${contact}">
            <table class="contactForm">
                <tr>
                    <td class="tdLabel">
                        <label for="contact_email" class="label"><fmt:message key="label.email.contact" /><span class="required">*</span>:</label>
                    </td>
                    <td>
                        <form:input path="email" type="text" name="email" size="50" value="" id="contact_email" style="margin-bottom:10px;"/>
                    </td>
                </tr>
                <tr>
                    <td class="tdLabel">
                        <label for="contact_topicSubject" class="label"><fmt:message key="label.email.subject" /><span class="required">*</span>:</label>
                    </td>
                    <td>
                        <form:select path="type" name="topicSubject" id="contact_topicSubject">
                            <option value="-1">--- <fmt:message key="label.contact.items.select" /> ---</option>
                            <option value="1"><fmt:message key="label.contact.item.issues" /></option>
                            <option value="2"><fmt:message key="label.contact.item.contribute" /></option>
                            <option value="3"><fmt:message key="label.contact.item.suggestions" /></option>
                        </form:select>
                    </td>
                </tr>
                <tr>
                    <td class="tdLabel">
                        <label for="contact_feedbackText" class="label"><fmt:message key="label.feedback.comments" /><span
                                class="required">*</span>:</label>
                    </td>
                    <td>
                        <form:textarea path="feedback" name="feedbackText" cols="" rows="" id="contact_feedbackText"
                                  style="width:500px; height:180px;vertical-align:middle"/>
                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td>
                        <div id="contact_" class="apnetReCaptcha">
                            <input type="hidden" name="hidden_flag_recaptcha" value="" id="hidden_flag_recaptcha"/>
                            <script type="text/javascript" src="http://api.recaptcha.net/challenge?k=6LebX9sSAAAAAPNhohQ93xnzFSbMdKsdLeuSI2dq"></script>
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
        </form:form>
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${success == 'true'}">
                <div>
                    <fmt:message key="success.feedback.contact" />
                </div>
            </c:when>
            <c:otherwise>
                <div>
                    Error while sending your feedback... Please try again.
                </div>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>