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
<portal:sourceTabs results="${results}" ajax="true" type="eacCpf"/>
<form:form id="updateCurrentSearch" name="eacCpfSearchForm" commandName="eacCpfSearch" method="post">
		<form:hidden id="updateCurrentSearch_term" path="term"/>
		<form:hidden id="updateCurrentSearch_method" path="method"/>
		<form:hidden id="updateCurrentSearch_mode" path="mode" value="update-search"/>
		<form:hidden id="updateCurrentSearch_fromdate" path="fromdate"/>
		<form:hidden id="updateCurrentSearch_todate" path="todate"/>
		<form:hidden id="updateCurrentSearch_country" path="country"/>	
		<form:hidden id="updateCurrentSearch_ai" path="ai"/>	
		<form:hidden id="updateCurrentSearch_language" path="language"/>					
		<form:hidden id="updateCurrentSearch_placesFacet" path="placesFacet"/>
		<form:hidden id="updateCurrentSearch_occupationsFacet" path="occupationsFacet"/>
		<form:hidden id="updateCurrentSearch_functionsFacet" path="functionsFacet"/>
		<form:hidden id="updateCurrentSearch_mandatesFacet" path="mandatesFacet"/>		
		<form:hidden id="updateCurrentSearch_entityTypeFacet" path="entityTypeFacet"/>				
		<form:hidden id="updateCurrentSearch_exactDateSearch" path="exactDateSearch"/>
		<form:hidden id="updateCurrentSearch_facetSettings" path="facetSettings"/>
		<form:hidden id="updateCurrentSearch_order" path="order"/>
		
		<form:hidden id="updateCurrentSearch_dateType" path="dateType"/>			
		<form:hidden id="updateCurrentSearch_startdate" path="startDate"/>	
		<form:hidden id="updateCurrentSearch_enddate" path="endDate"/>
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
							<c:if test="${results.partialResults}"><span class="partialresults">(<fmt:message key="search.message.approximately" />)</span></c:if>
						</div>
						<div id="resultPerPageContainer">
							<label for="updateCurrentSearch_resultsperpage" id="resultPerPageLabel" class="bold"><fmt:message key="advancedsearch.text.numberofresults"/></label>
							<form:select path="resultsperpage"  id="updateCurrentSearch_resultsperpage" items="${eacCpfSearch.resultsperpageValues}"/>
						</div>						
						<div id="top-paging" class="paging">
						<ape:paging numberOfItems="${results.totalNumberOfResults}" pageSize="${results.pageSize}" pageNumber="${eacCpfSearch.pageNumber}"
								refreshUrl="javascript:updatePageNumber('');" pageNumberId="pageNumber" maxNumberOfItems="${results.maxTotalNumberOfResults}"/>	
						</div>
				</c:when>
				<c:otherwise>
					<span id="noResults"><fmt:message key="search.message.notResults" /><c:if test="${results.partialResults}"><span class="partialresults"> (<fmt:message key="search.message.approximately" />)</span></c:if></span>
				</c:otherwise>
			</c:choose>	
			</div>
		</div>
		<c:if test="${empty results.errorMessage and (results.totalNumberOfResults > 0  or eacCpfSearch.mode == 'update-search')}">
			<div id="selectedRefinements" class="selectedCriteria">
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
					<c:choose>
						<c:when test="${results.totalNumberOfResults > results.maxTotalNumberOfResults}">
							<div id="toManyResults"><fmt:message key="advancedsearch.text.sorting.toomanyresults" /></div>
						</c:when>
						<c:otherwise>					
							<searchresults:order currentValue="${eacCpfSearch.order}" value="relevancy" key="advancedsearch.order.relevancy" />
							|
							<searchresults:order currentValue="${eacCpfSearch.order}" value="startdate" key="advancedsearch.text.date" />	
						</c:otherwise>
					</c:choose>
				</div>
	
		<div id="searchresultsList">	
				<c:forEach var="result" items="${results.items}">
				<portal:generateSearchWords var="encodedTerm" term="${eacCpfSearch.term}" element="${eacCpfSearch.element}"/>
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
								<portal:eacCpfPersistentLink var="url" repoCode="${result.repositoryCode}" id="${result.identifier}" searchFieldsSelectionId="${eacCpfSearch.element}"  searchTerms="${eacCpfSearch.term}"/>
								<a class="unittitle ${titleClass}" target="_blank" title="${titleWithoutHighlighting}"
									href="${url}">${title}
								</a>
								<c:if test="${!empty result.alterdate}">
									<span class="alterdate" title="${result.alterdateWithoutHighlighting}">${result.alterdate}</span>
								</c:if>																					
							</div>
							<c:if test="${!empty result.description}"><div class="scopecontent">${result.description}</div></c:if>
							<c:if test="${!empty result.other}"><div class="other">${result.other}</div></c:if>
						<div class="list-searchresult-context">
								<div class="left">
									<div class="other"><span class="subtitle"><fmt:message key="advancedsearch.facet.title.entitytypefacet" />:</span><fmt:message key="advancedsearch.facet.value.eaccpf.entitytype.${fn:toLowerCase(result.entityType)}" /></div>
									<c:if test="${!empty result.places}"><div class="other"><span class="subtitle"><fmt:message key="advancedsearch.facet.title.placesfacet" />:</span>${result.places}</div></c:if>
									<c:if test="${!empty result.occupations}"><div class="other"><span class="subtitle"><fmt:message key="advancedsearch.facet.title.occupationsfacet" />:</span>${result.occupations}</div></c:if>								
									<c:if test="${!empty result.functions}"><div class="other"><span class="subtitle"><fmt:message key="advancedsearch.facet.title.functionsfacet" />:</span>${result.functions}</div></c:if>
									<c:if test="${!empty result.mandates}"><div class="other"><span class="subtitle"><fmt:message key="advancedsearch.facet.title.mandatesfacet" />:</span>${result.mandates}</div></c:if>
									<c:if test="${!empty result.entityId}"><div class="unitid"><span class="subtitle"><fmt:message key="advancedsearch.eaccpf.element.id" /></span>${result.entityId}</div></c:if>
									<div class="countryAndInstitution"><fmt:message key="country.${fn:toLowerCase(result.country)}" />&nbsp;-&nbsp;<c:out value="${result.ai}" /></div>
									<c:if test="${result.numberOfArchivalMaterialRelations > 0 or result.numberOfNameRelations > 0 or result.numberOfInstitutions > 0}">
									<div class="relation"><span class="subtitle"><fmt:message key="advancedsearch.eaccpf.related" />:</span>
										<c:if test="${result.numberOfArchivalMaterialRelations > 0}">${result.numberOfArchivalMaterialRelations} <fmt:message key="advancedsearch.eaccpf.related.materials" /></c:if>
										<c:if test="${result.numberOfNameRelations > 0}">&mdash;&nbsp;${result.numberOfNameRelations} <fmt:message key="advancedsearch.eaccpf.related.names" /></c:if>
										<c:if test="${result.numberOfInstitutions > 0}">&mdash;&nbsp;${result.numberOfInstitutions} <fmt:message key="advancedsearch.eaccpf.related.institutions" /></c:if>
									</div>
									</c:if>

								</div>
                                                                        <div class="right">
                                                                            <c:if test="${!empty result.identifier}"><div class="other"><span class="subtitle"><fmt:message key="advancedsearch.eaccpf.element.id" />:</span>${result.identifier}</div></c:if>
                                                                        </div>
						</div>							
						</div>
						<portlet:resourceURL var="displayPreviewUrl" id="displayPreview" >
							<portlet:param  name="identifier" value="${result.identifier}"/>
							<portlet:param  name="repositoryCode" value="${result.repositoryCode}"/>
							<portlet:param  name="term" value="${encodedTerm}"/>
							<portlet:param  name="element" value="${eacCpfSearch.element}"/>
						</portlet:resourceURL>
						<div class="preview-button-holder"
						data-url="${displayPreviewUrl}">&nbsp;</div>
					</div>
					</c:forEach>
				</div>
				<div id="bottom-paging" class="paging">
					<ape:paging numberOfItems="${results.totalNumberOfResults}" pageSize="${results.pageSize}" pageNumber="${eacCpfSearch.pageNumber}"
					refreshUrl="javascript:updatePageNumber('');" pageNumberId="pageNumber" maxNumberOfItems="${results.maxTotalNumberOfResults}"/>	
				</div>
			</div>
			<div class="preview-column">
			<div id="search-preview" class="preview-container search-result-preview-container"></div>
			</div>
</div>
</c:if>
</div>
</form:form>