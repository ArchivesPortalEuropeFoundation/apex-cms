<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%> 
<%@ taglib prefix="searchresults" uri="http://portal.archivesportaleurope.eu/tags/searchresults"%>
<%@ taglib prefix="facets" tagdir="/WEB-INF/tags/facets"%>
<portlet:defineObjects />
<script type="text/javascript">
	$(document).ready(function() {
        initListTabHandlers();
    });
</script>
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
						<li class="ui-state-default ui-corner-top"><a href="javascript:changeSearch('ead-search')"><fmt:message key="menu.archives-search" /></a></li>
						<li class="ui-state-default ui-corner-top ui-tabs-selected ui-state-active "><a href=""><fmt:message key="menu.name-search" /></a></li>
						<li class="ui-state-default ui-corner-top"><a href="javascript:changeSearch('institution-search')"><fmt:message key="menu.institutions-search" /></a></li>		
					</c:when>
					<c:otherwise>
						<li class="ui-state-default ui-corner-top ${results.eadNumberOfResultsClass}"><a href="javascript:changeSearch('ead-search')"><fmt:message key="menu.archives-search" /><span class="numberOfResults">(${results.eadNumberOfResults})</span></a></li>
						<li class="ui-state-default ui-corner-top ui-tabs-selected ui-state-active"><a href=""><fmt:message key="menu.name-search" /><span class="numberOfResults">(${results.eacCpfNumberOfResults})</span></a></li>
						<li class="ui-state-default ui-corner-top ${results.eagNumberOfResultsClass}"><a href="javascript:changeSearch('institution-search')"><fmt:message key="menu.institutions-search" /><span class="numberOfResults">(${results.eagNumberOfResults})</span></a></li>
					</c:otherwise>
			</c:choose>
			</ul>
			<div class="tab_header">
				<div id="tabHeaderContent"></div>
			</div>
</div>
<form:form id="updateCurrentSearch" name="eacCpfSearchForm" commandName="eacCpfSearch" method="post">
		<form:hidden id="updateCurrentSearch_term" path="term"/>
		<form:hidden id="updateCurrentSearch_method" path="method"/>
		<form:hidden id="updateCurrentSearch_mode" path="mode" value="update-search"/>
		<form:hidden id="updateCurrentSearch_fromdate" path="fromdate"/>
		<form:hidden id="updateCurrentSearch_todate" path="todate"/>
		<form:hidden id="updateCurrentSearch_placesFacet" path="placesFacet"/>
		<form:hidden id="updateCurrentSearch_occupationFacet" path="occupationFacet"/>
		<form:hidden id="updateCurrentSearch_exactDateSearch" path="exactDateSearch"/>
		<form:hidden id="updateCurrentSearch_facetSettings" path="facetSettings"/>
		<form:hidden id="updateCurrentSearch_order" path="order"/>
		<form:hidden id="updateCurrentSearch_startdate" path="startdate"/>	
		<form:hidden id="updateCurrentSearch_enddate" path="enddate"/>
		<form:hidden id="updateCurrentSearch_pageNumber" path="pageNumber"/>
		<form:hidden id="updateCurrentSearch_publishedFromDate" path="publishedFromDate"/>
		<form:hidden id="updateCurrentSearch_publishedToDate" path="publishedToDate"/>
<div class="results">
		<div class="tab_header">
			<div id="tabHeaderContent">
			<c:choose>
				<c:when test="${!empty results.errorMessage}">
					<div class="error"><fmt:message key="${results.errorMessage}" /></div>
				</c:when>
				<c:when test="${results.totalNumberOfResults > 0}">				
						<div id="numberOfResults">
							<span class="bold"><fmt:message key="advancedsearch.text.results" />:</span>
							<ape:pageDescription numberOfItems="${results.totalNumberOfResults}" pageSize="${results.pageSize}" pageNumber="${eacCpfSearch.pageNumber}" numberFormat="${numberFormat}" />
						</div>
						<div id="top-paging" class="paging">
						<ape:paging numberOfItems="${results.totalNumberOfResults}" pageSize="${results.pageSize}" pageNumber="${eacCpfSearch.pageNumber}"
								refreshUrl="javascript:updatePageNumber('');" pageNumberId="pageNumber"/>	
						</div>
				</c:when>
				<c:otherwise>
					<span id="noResults"><fmt:message key="search.message.notResults" /></span>
				</c:otherwise>
			</c:choose>	
			</div>
		</div>
		<c:if test="${empty results.errorMessage and (results.totalNumberOfResults > 0  or eacCpfSearch.mode == 'update-search')}">
			<div id="selectedRefinements">
				<div id="selectedRefinementsTitle"><fmt:message key="advancedsearch.facet.title.choosed" /></div>
				<ul>
					<c:if test="${!empty selectedRefinements }">
						<c:forEach var="selectedRefinement" items="${selectedRefinements}">
						<c:choose>
							<c:when test="${selectedRefinement.date}">
						<li id='${selectedRefinement.fieldName}' class="${selectedRefinement.cssClass}"><a title='${selectedRefinement.longDescription}' href="javascript:removeDateRefinement('${selectedRefinement.fieldName}')">
				${selectedRefinement.longDescription}<span class='close-icon'></span></a></li>
							</c:when>
							<c:otherwise>
						<li id='${selectedRefinement.fieldName}_${selectedRefinement.fieldValue}' class="${selectedRefinement.cssClass}"><a title='${selectedRefinement.longDescription}' href="javascript:removeRefinement('${selectedRefinement.fieldName}','${selectedRefinement.fieldValue}')">
				${selectedRefinement.longDescription}<span class='close-icon'></span></a></li>							
							</c:otherwise>
						</c:choose>

						</c:forEach>
					</c:if>
				</ul>				
			</div>		
			<div id="resultsContainer">
			<div id="refinements">
				<facets:facets-default facetContainers="${results.facetContainers}"/>
				&nbsp;	
			</div>
			<div  id="searchResultsListContainer">	
				<div id="searchOrder">
					<div id="searchOrderTitle"><fmt:message key="advancedsearch.text.sortsearch" /></div>
					<searchresults:order currentValue="${eacCpfSearch.order}" value="relevancy" key="advancedsearch.order.relevancy" />
					|
					<searchresults:order currentValue="${eacCpfSearch.order}" value="startdate" key="advancedsearch.text.date" />	
				</div>
	
		<div id="searchresultsList">	
				<c:forEach var="result" items="${results.items}">
					<div class="list-searchresult" id="list-searchresult-${result.id}">
						<div class="list-searchresult-content list-searchresult-content-eac-cpf">
						<div class="list-searchresult-header">
								<c:choose>
									<c:when test="${empty result.title}">
										<c:set var="title"><fmt:message key="advancedsearch.text.notitle" /></c:set>
										<c:set var="titleWithoutHighlighting"><fmt:message key="advancedsearch.text.notitle" /></c:set>
										<c:set var="titleClass" value="notitle"/>
									</c:when>
									<c:otherwise>
										<c:set var="title" value="${result.title}"/>
										<c:set var="titleWithoutHighlighting" value="${result.titleWithoutHighlighting}"/>
										<c:set var="titleClass" value=""/>								
									</c:otherwise>
								</c:choose>

								<a class="unittitle ${titleClass}" target="_blank" title="${titleWithoutHighlighting}"
									href="${friendlyUrl}">${title}
								</a>
								<c:if test="${!empty result.alterdate}">
									<span class="alterdate" title="${result.alterdateWithoutHighlighting}">${result.alterdate}</span>
								</c:if>																					
							</div>
							<div class="scopecontent">${result.description}</div>

						<div class="list-searchresult-context">
								<div class="left">
									<div class="other"><span class="subtitle">Places:</span>${result.places}</div>
									<div class="other"><span class="subtitle">Occupation:</span>${result.occupation}</div>								
									<div class="unitid"><span class="subtitle"><fmt:message key="advancedsearch.message.referencecode" /></span>${result.id}</div>
									<div class="countryAndInstitution"><fmt:message key="country.${fn:toLowerCase(result.country)}" />&nbsp;-&nbsp;<c:out value="${result.ai}" /></div>
								</div>						
						</div>							
						</div>
					</div>
					</c:forEach>
				</div>
				<div id="bottom-paging" class="paging">
					<ape:paging numberOfItems="${results.totalNumberOfResults}" pageSize="${results.pageSize}" pageNumber="${eacCpfSearch.pageNumber}"
					refreshUrl="javascript:updatePageNumber('');" pageNumberId="pageNumber"/>	
				</div>
			</div>
</div>
</c:if>
</div>
</form:form>