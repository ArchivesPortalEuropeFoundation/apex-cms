<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<portlet:defineObjects />
<portlet:actionURL var="translationSaveUrl">
    <portlet:param name="myaction" value="save" />
    <portlet:param name="index" value="${stat.index}" />
</portlet:actionURL>

<portlet:actionURL var="cleanTranslationsUrl">
    <portlet:param name="myaction" value="cleanTranslations" />
</portlet:actionURL>
<div id="languagePortlet">
<h2>Translations</h2>
<a href="${cleanTranslationsUrl}">Remove all menu translations</a>
    <table>
        <c:forEach var="translation" varStatus="stat" items="${translationForm.translations}">
            <form:form id="translation" name="translation" modelAttribute="translationForm" method="post" action="${translationSaveUrl}">
                <tr>
                    <td>${translation.friendlyUrl}</td>
                    <td>${translation.languageId}</td>
                    <td>
                        <form:input path="translations[${stat.index}].name" type="text" name="name" size="50" id="name" />
                        <form:input path="translations[${stat.index}].friendlyUrl" type="hidden" name="friendlyUrl" id="friendlyUrl"/>
                        <form:input path="translations[${stat.index}].languageId" type="hidden" name="languageId" id="languageId"/>
                        <input type="hidden" name="index" id="index" value="${stat.index}"/>
                    </td>
                    <td>
                        <input type="submit" id="translations_save_send" name="method:execute" value="Save" class="mainButton" />
                    </td>
                </tr>
            </form:form>
        </c:forEach>
    </table>
</div>