<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>

<portlet:actionURL var="bookmarkUrl">
    <portlet:param name="myaction" value="bookmark" />
</portlet:actionURL>

<script type="text/javascript">
	 var RecaptchaOptions = {
	    theme : 'white'
	 };
</script>
<div id="bookmarkPortlet">
<h1><fmt:message key="bookmark.form.title" /></h1>

<form:form id="bookmarkForm" name="bookmarkForm" commandName="bookmark" method="post" action="${bookmarkUrl}">
    <table class="bookmarkForm">
        <tr>
            <td class="tdLabel">
                <label for="bookmark_email" class="label"><fmt:message key="label.email.bookmark" /><span class="required">*</span>:</label>
            </td>
            <td>
                <form:input path="email" type="text" name="email" size="50" id="bookmark_email" /><form:errors path="email" cssClass="errorBlock" />
            </td>
        </tr>
        <tr>
        	<td> <label for="receiveCopy" class="label"><fmt:message key="label.email.bookmark.receivecopy" />:</label></td>
        	<td><form:checkbox path="receiveCopy" id="receiveCopy"/> </td>
        </tr>
        <tr>
            <td class="tdLabel">
                <label for="bookmark_topicSubject" class="label"><fmt:message key="label.email.subject" /><span class="required">*</span>:</label>
            </td>
            <td>
                <form:select path="type" name="topicSubject" id="bookmark_topicSubject" >
                    <form:option value="">--- <fmt:message key="label.bookmark.items.select" /> ---</form:option>
                    <form:options items="${bookmark.typeList }"/>
                </form:select><form:errors path="type" cssClass="errorBlock" />
            </td>
        </tr>
        <tr>
            <td class="tdLabel">
                <label for="bookmark_feedbackText" class="label">Bookmark description</span>:</label>
            </td>
            <td>
                <form:textarea path="feedback" name="feedbackText" cols="" rows="" id="bookmark_feedbackText" /> <form:errors path="feedback" cssClass="errorBlock" />
            </td>
        </tr>
		<c:if test="${!loggedIn}">
	        <tr>
	        	<td></td>
	            <td>BUA BUA BUA BUAAAAA...</td>
	        </tr> 
        </c:if>
        <c:if test="${loggedIn}">
	        <tr>
	        	<td></td>
	            <td>TACHAAAAAN!!!</td>
	        </tr> 
        </c:if>
        <tr>
            <td colspan="2">
                <table>
                    <tr>
                        <td class="leftBtn">
                            <input type="submit" id="bookmark_label_feedback_send" value="Bookmark" class="mainButton"/>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</form:form>
</div>