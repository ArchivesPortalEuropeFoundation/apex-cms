<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet" %>
<portlet:defineObjects />
<c:choose>
	<c:when test="${!empty portletPreferences.map.advancedSearchPageId and !empty portletPreferences.map.advancedSearchPortletName and !empty portletPreferences.map.numberOfDaoUnits}">
		<c:set var="advancedSearchPageId" value="${portletPreferences.map.advancedSearchPageId[0]}"/>
		<c:set var="advancedSearchPortletName" value="${portletPreferences.map.advancedSearchPortletName[0]}"/>
		<c:set var="numberOfDaoUnits" value="${portletPreferences.map.numberOfDaoUnits[0]}"/>
<portlet:resourceURL var="autocompletionUrl" id="autocompletion" />
<liferay-portlet:renderURL var="advancedSearchUrl"  plid="${advancedSearchPageId}" portletName="${advancedSearchPortletName}">
	<portlet:param name="myaction" value="simpleSearch" />
	<liferay-portlet:param  name="advanced" value="false"/>
</liferay-portlet:renderURL>
		<script type="text/javascript">
			$(document).ready(function() {
				init("${autocompletionUrl}" );
			});
		</script>
<div id="simpleSearchPortlet">
	<form:form id="simpleSearchForm" name="simpleSearchForm" commandName="simpleSearch" method="post" action="${advancedSearchUrl}">
		<div id="simpleSearch">
			<div id="simpleSearchOptionsContent" class="searchOptionsContent">
				<div class="simpleSearchOptions">
					<table id="simplesearchCriteria">
						<fmt:message key="advancedsearch.message.typesearchterms2" var="termTitle" />
						<tr>
							<td colspan="2"><form:input path="term" id="searchTerms" title="${termTitle}" tabindex="1" /> <input
								type="submit" id="searchButton" title="<fmt:message key="advancedsearch.message.start"/>" tabindex="10"
								value="<fmt:message key="advancedsearch.message.search"/>" /></td>
						</tr>
						<tr>
							<td class="leftcolumn">
								<div class="row">
									 <input
										type="checkbox" id="checkboxHierarchy" value="hierarchy" name="view" tabindex="2"/><label for="checkboxHierarchy"><fmt:message key="advancedsearch.message.hierarchy" /></label>
								</div>
								<div class="row">
									 <input type="checkbox"
										id="checkboxMethod" value="optional" name="method" tabindex="3"/><label for="checkboxMethod"><fmt:message key="advancedsearch.message.method" /></label>
								</div>
								<div class="row">
									 <input type="checkbox"
										id="checkboxDao" value="true" name="dao" tabindex="4"/><label for="checkboxDao"><fmt:message key="advancedsearch.message.dao" /></label>
								</div>
							</td>
							<td class="rightcolumn">
								<div id="resultsperpageRow" class="row">
									<label for="resultsperpage"><fmt:message key="advancedsearch.text.numberofresults" /></label>
									<form:select path="resultsperpage" id="resultsperpage" tabindex="5">
										<form:option value="10" />
										<form:option value="20" />
										<form:option value="30" />
										<form:option value="50" />
										<form:option value="100" />
									</form:select>

								</div>
							</td>
						</tr>
					</table>
				</div>
			</div>
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
	</form:form>
</div>
	</c:when>
	<c:otherwise>
		<liferay-ui:message key="please-contact-the-administrator-to-setup-this-portlet" />
	</c:otherwise>
</c:choose>