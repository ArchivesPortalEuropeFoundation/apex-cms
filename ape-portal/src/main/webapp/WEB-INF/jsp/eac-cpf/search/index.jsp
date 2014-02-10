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
<div id="eacCpfSearchPortlet">
<portlet:renderURL var="eacCpfSearchUrl">
	<portlet:param name="myaction" value="eacCpfSearch" />
</portlet:renderURL>
<form:form id="eacCpfSearchForm" name="eacCpfSearchForm" commandName="eacCpfSearch" method="post"
				action="${eacCpfSearchUrl}">
					<div id="simpleSearch">
						<div><form:input path="term" id="searchTerms"  tabindex="1" maxlength="100"/> <input
											type="submit" id="searchButton" title="<fmt:message key="advancedsearch.message.start"/>" tabindex="10"
											value="<fmt:message key="advancedsearch.message.search"/>" /></div>
					</div>
</form:form>
<div>
<h2>${test}</h2>
</div>
</div>