<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<portlet:resourceURL var="feedbackUrl" id="feedbackAction"/>

<div id="contactPortlet">
<h1><fmt:message key="label.feedback" /></h1>

<form:form id="contactForm" name="contactForm" commandName="contact" method="post" action="${feedbackUrl}">
	<form:hidden path="aiId"/>
	<form:hidden id="aiRepoCode" path="repoCode"/>
    <table class="contactForm">
        <tr>
            <td class="tdLabel">
                <label for="contact_email" class="label"><fmt:message key="label.email.contact" /><span class="required">*</span>:</label>
            </td>
            <td>
                <form:input path="email" type="text" name="email" size="50" id="contact_email" /> <form:errors path="email" cssClass="errorBlock" />
            </td>
        </tr>
        <tr>
        	<td> <label for="receiveCopy" class="label"><fmt:message key="label.email.contact.receivecopy" />:</label></td>
        	<td><form:checkbox path="receiveCopy" id="receiveCopy"/> </td>
        </tr>    
           
        <tr>
            <td><fmt:message key="advancedsearch.facet.title.ai" /></td>
            <td id="aiName"><c:out value="${contact.institution}"/></td>
        </tr>        
        <tr>
            <td><fmt:message key="label.email.subject" />:</td>
            <td>
            	<c:out value="${contact.title}"/><form:hidden path="title"/>
            </td>
        </tr>
        <tr>
        	<td><fmt:message key="feedback.url" />:</td>
        	<td><c:out value="${contact.url}"/><form:hidden path="url"/></td>
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
	        <tr>
	        	<td></td>
	            <td>
					<div id="recaptchaDiv"></div><form:errors path="captcha" cssClass="errorBlock" />
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
                            <input type="submit" id="contactFeedbackSend" value="<fmt:message key="label.feedback.send" />" class="mainButton"/>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</form:form>
</div>