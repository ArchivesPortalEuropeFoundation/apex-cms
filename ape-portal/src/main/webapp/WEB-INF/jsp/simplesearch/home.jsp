<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet" %>
<portlet:defineObjects />
<portal:page  varPlId="eadSearchPlId"  varPortletId="eadSearchPortletId" portletName="eadsearch" friendlyUrl="/search"/>		
<portlet:resourceURL var="autocompletionUrl" id="autocompletion" />
<liferay-portlet:renderURL var="eadSearchUrl"  plid="${eadSearchPlId}" portletName="${eadSearchPortletId}">
	<portlet:param name="myaction" value="simpleSearch" />
	<liferay-portlet:param  name="advanced" value="false"/>
</liferay-portlet:renderURL>
		<script type="text/javascript">
			$(document).ready(function() {
				initSimpleSearchAutocompletion("${autocompletionUrl}",false);
			});
		</script>
<div id="simpleSearchPortlet">
		<div id="simpleSearch">
			<div class="simpleSearchOptions">
				<form:form id="simpleSearchForm" name="simpleSearchForm" commandName="simpleSearch" method="post" action="${eadSearchUrl}">
					<form:input path="term" id="searchTerms" title="${termTitle}" tabindex="1" maxlength="100"  autocomplete="off"/> <input
								type="submit" id="searchButton" title="<fmt:message key="advancedsearch.message.start"/>" tabindex="10"
								value="<fmt:message key="advancedsearch.message.search"/>" />
				</form:form>				
			</div>
			<div id="simpleSearchText">
			<p>
				<fmt:message key="advancedsearch.message.firstmessagepartycsi" />
				<span class="bold">${units}</span>
				<fmt:message key="advancedsearch.message.firstmessagepartinst" />
				<span class="bold">${numberOfDaoUnits}</span>
				<fmt:message key="advancedsearch.message.firstmessagepartdao" />
				<span class="bold">${institutions}</span>
				<fmt:message key="advancedsearch.message.firstmessagepartdu" />
			</p>
			 </div>
		</div>
	
</div>
