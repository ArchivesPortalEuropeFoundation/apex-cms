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
<portlet:actionURL var="cleanTranslationsUrl">
    <portlet:param name="myaction" value="cleanTranslations" />
</portlet:actionURL>
<portlet:actionURL var="updateTranslationsUrl">
    <portlet:param name="myaction" value="updateTranslations" />
</portlet:actionURL>
<portlet:actionURL var="cleanServerUrl">
    <portlet:param name="myaction" value="cleanServer" />
</portlet:actionURL>
<div id="languagePortlet">
<h1>Admin portlet</h1>
<h2>Translations</h2>
<ul>
<li><a href="${cleanTranslationsUrl}">Remove all menu translations</a></li>
<li><a href="${updateTranslationsUrl}">Update all menu translations</a></li>
</ul> 
<h2>Other</h2>
<ul>
<li><a href="${cleanServerUrl}">Clean server</a></li>
</ul> 
</div>