<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>

<portlet:defineObjects />
<c:if test="${results.showSuggestions}">
	<div class="hidden" id="NEWsuggestionSearch">
		<span class="suggestionText"> <c:choose>
				<c:when test="${results.totalNumberOfResults > 0}">
					<fmt:message key="advancedsearch.message.suggestion.results" />
				</c:when>
				<c:otherwise>
					<fmt:message key="advancedsearch.message.suggestion.noresults" />
				</c:otherwise>
			</c:choose>
		</span> <br />
		<portal:autosuggestion spellCheckResponse="${results.spellCheckResponse}" styleClass="suggestionLink"
			numberOfResultsStyleClass="suggestionNumberOfHits" misSpelledStyleClass="suggestionMisspelled" />
	</div>
</c:if>
<div class="hidden" id="NEWsourceTabs">
			<ul id="tabscontainer" class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
			<c:choose>
					<c:when test="${empty results}">
						<li class="ui-state-default ui-corner-top ui-tabs-selected ui-state-active"><a href=""><fmt:message key="menu.archives-search" /></a></li>
						<li class="ui-state-default ui-corner-top"><a href="javascript:changeSearch('name-search')"><fmt:message key="menu.name-search" /></a></li>
						<li class="ui-state-default ui-corner-top"><a href="javascript:changeSearch('institution-search')"><fmt:message key="menu.institutions-search" /></a></li>		
					</c:when>
					<c:otherwise>
						<li class="ui-state-default ui-corner-top ui-tabs-selected ui-state-active"><a href=""><fmt:message key="menu.archives-search" /><span class="numberOfResults">(${results.eadNumberOfResults})</span></a></li>
						<li class="ui-state-default ui-corner-top ${results.eacCpfNumberOfResultsClass}"><a href="javascript:changeSearch('name-search')"><fmt:message key="menu.name-search" /><span class="numberOfResults">(${results.eacCpfNumberOfResults})</span></a></li>
						<li class="ui-state-default ui-corner-top ${results.eagNumberOfResultsClass}"><a href="javascript:changeSearch('institution-search')"><fmt:message key="menu.institutions-search" /><span class="numberOfResults">(${results.eagNumberOfResults})</span></a></li>
					</c:otherwise>
			</c:choose>
			</ul>
			<div class="tab_header">
				<div id="tabHeaderContent"></div>
			</div>
</div>
<form:form id="updateCurrentSearch" name="advancedSearchForm" commandName="advancedSearch" method="post">
		<form:hidden id="updateCurrentSearch_term" path="term"/>
		<form:hidden id="updateCurrentSearch_method" path="method"/>
		<form:hidden id="updateCurrentSearch_mode" path="mode" value="update-search"/>
		<form:hidden id="updateCurrentSearch_advanced" path="advanced"/>
		<form:hidden id="updateCurrentSearch_simpleSearchDao" path="simpleSearchDao"/>
		<form:hidden id="updateCurrentSearch_view" path="view"/>
		<form:hidden id="updateCurrentSearch_element" path="element"/>
		<form:hidden id="updateCurrentSearch_typedocument" path="typedocument"/>
		<form:hidden id="updateCurrentSearch_fromdate" path="fromdate"/>
		<form:hidden id="updateCurrentSearch_todate" path="todate"/>
		<form:hidden id="updateCurrentSearch_exactDateSearch" path="exactDateSearch"/>
		<input type="hidden" id="updateCurrentSearch_country" name="country" value="${advancedSearch.country}"/>
		<input type="hidden" id="updateCurrentSearch_ai" name="ai" value="${advancedSearch.ai}"/>
		<input type="hidden" id="updateCurrentSearch_fond" name="fond" value="${advancedSearch.fond}"/>
		<input type="hidden" id="updateCurrentSearch_type" name="type" value="${advancedSearch.type}"/>
		<input type="hidden" id="updateCurrentSearch_dao" name="dao" value="${advancedSearch.dao}"/>
		<input type="hidden" id="updateCurrentSearch_roledao" name="roledao" value="${advancedSearch.roledao}"/>
		<input type="hidden" id="updateCurrentSearch_level" name="level" value="${advancedSearch.level}"/>
		<input type="hidden" id="updateCurrentSearch_dateType" name="dateType" value="${advancedSearch.dateType}"/>
		<input type="hidden" id="updateCurrentSearch_facetSettings" name="facetSettings" value="${advancedSearch.facetSettings}"/>
		<form:hidden id="updateCurrentSearch_order" path="order"/>
		<form:hidden id="updateCurrentSearch_startdate" path="startdate"/>	
		<form:hidden id="updateCurrentSearch_enddate" path="enddate"/>
		<form:hidden id="updateCurrentSearch_selectedNodes" path="selectedNodes"/>
		<form:hidden id="updateCurrentSearch_pageNumber" path="pageNumber"/>
		<form:hidden id="updateCurrentSearch_publishedFromDate" path="publishedFromDate"/>
		<form:hidden id="updateCurrentSearch_publishedToDate" path="publishedToDate"/>
	<c:choose>
		<c:when test="${advancedSearch.view == 'hierarchy'}">
			<jsp:include page="results-contexttab.jsp"/>
		</c:when>
		<c:otherwise>
			<jsp:include page="results-listtab.jsp"/>
		</c:otherwise>	
	</c:choose>

</form:form>	