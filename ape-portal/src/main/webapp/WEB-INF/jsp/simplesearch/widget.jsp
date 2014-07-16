<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet" %>
<portlet:defineObjects />
<portlet:resourceURL var="autocompletionUrl" id="autocompletion" />
<portal:page  varPlId="eadSearchPlId"  varPortletId="eadSearchPortletId" portletName="eadsearch" friendlyUrl="/search"/>	
<liferay-portlet:renderURL var="eadSearchUrl"  plid="${eadSearchPlId}" portletName="${eadSearchPortletId}">
	<portlet:param name="myaction" value="simpleSearch" />
	<liferay-portlet:param  name="advanced" value="false"/>
</liferay-portlet:renderURL>
<portal:page  varPlId="eacCpfSearchPlId"  varPortletId="eacCpfSearchPortletId" portletName="eaccpfsearch" friendlyUrl="/name-search"/>		
<liferay-portlet:renderURL var="eacCpfSearchUrl"  plid="${eacCpfSearchPlId}" portletName="${eacCpfSearchPortletId}">
	<portlet:param name="myaction" value="simpleSearch" />
</liferay-portlet:renderURL>
<portal:page  varPlId="eagSearchPlId"  varPortletId="eagSearchPortletId" portletName="eagsearch" friendlyUrl="/institution-search"/>		
<liferay-portlet:renderURL var="eagSearchUrl"  plid="${eagSearchPlId}" portletName="${eagSearchPortletId}">
	<portlet:param name="myaction" value="simpleSearch" />
</liferay-portlet:renderURL>
		<script type="text/javascript">
			$(document).ready(function() {
				initSimpleSearchAutocompletion("${autocompletionUrl}", true );
			});
		</script>

<c:choose>
	<c:when test="${resultsType ==  'eac-cpf'}">
		<c:set var="searchUrl" value="${eacCpfSearchUrl}"/>
	</c:when>
	<c:when test="${resultsType ==  'eag'}">
		<c:set var="searchUrl" value="${eagSearchUrl}"/>
	</c:when>	
	<c:otherwise>
		<c:set var="searchUrl" value="${eadSearchUrl}"/>
	</c:otherwise>
</c:choose>
<div id="widgetSimpleSearchPortlet">
		<div id="simpleSearch">
			<div class="simpleSearchOptions">
				<form id="simpleSearchForm" method="post" action="${searchUrl}" target="_blank">
					<input type="hidden" name="savedSearchId" value="${savedSearchId}"/>
					<input name="term" id="searchTerms" type="text" title="${termTitle}" tabindex="1" maxlength="100"  autocomplete="off" placeholder="<fmt:message key="simplesearch.message.search.ape"/>"/> <input
								type="submit" id="searchButton" title="<fmt:message key="advancedsearch.message.start"/>" tabindex="10"
								value="<fmt:message key="advancedsearch.message.search"/>" />
				</form>				
			</div>
			<div id="poweredBy"><fmt:message key="simplesearch.message.poweredby"/> <a href="http://www.archivesportaleurope.net"><fmt:message key="simplesearch.message.poweredby.ape"/></a></div>
		</div>
	
</div>	
