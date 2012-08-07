<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="searchresults" uri="http://portal.archivesportaleurope.eu/tags/searchresults"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%> 
<%@ taglib prefix="facets" tagdir="/WEB-INF/tags/facets"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet" %>
<portlet:defineObjects />

<script type="text/javascript">
	$(document).ready(function() {
        initListTabHandlers();
    });
</script>
<c:set var="eadDisplayPageId" value="${portletPreferences.map.eadDisplayPageId[0]}"/>
<c:set var="eadDisplayPortletName" value="${portletPreferences.map.eadDisplayPortletName[0]}"/>
<div class="results">
		<div class="tab_header">
			<div id="tabHeaderContent">
			<c:choose>
				<c:when test="${!empty results.errorMessage}">
					<div class="error"><fmt:message key="${results.errorMessage}" /></div>
				</c:when>
				<c:when test="${results.totalNumberOfResults > 0}">
					<div id="numberOfResults">
						<span class="bold"><fmt:message key="advancedsearch.text.results" /></span>
						<ape:pageDescription numberOfItems="${results.totalNumberOfResults}" pageSize="${results.pageSize}" pageNumber="${advancedSearch.pageNumber}" />
					</div>
					<div id="resultPerPageContainer">
						<label for="updateCurrentSearch_resultsperpage" id="resultPerPageLabel" class="bold"><fmt:message key="advancedsearch.text.numberofresults"/></label>
						<form:select path="resultsperpage" id="updateCurrentSearch_resultsperpage">
							<form:option value="10" />
							<form:option value="20" />
							<form:option value="30" />
							<form:option value="50" />
							<form:option value="100" />
						</form:select>		
					</div>
					<div id="goToPage">
						<span class="subtitle">Go to page:</span>
						<form:input id="updateCurrentSearch_pageNumber" path="pageNumber"/>
						<span class="subtitle">of</span>${results.totalNumberOfPages}
					</div>
					<div id="top-paging" class="paging">
					<ape:paging numberOfItems="${results.totalNumberOfResults}" pageSize="${results.pageSize}" pageNumber="${advancedSearch.pageNumber}"
							refreshUrl="javascript:updatePageNumber('');" pageNumberId="pageNumber"/>	
					</div>			
				</c:when>
				<c:otherwise>
					<fmt:message key="search.message.notResults" />
				</c:otherwise>
			</c:choose>
			</div>
		</div>
		<c:if test="${empty results.errorMessage and (results.totalNumberOfResults > 0  or advancedSearch.mode == 'update-search')}">
			<div id="selectedRefinements">
				<div id="selectedRefinementsTitle"><fmt:message key="advancedsearch.facet.title.choosed" /></div>
				<ul>
				</ul>				
			</div>
			
			<div id="resultsContainer">
			<div id="refinements">
				<facets:facets-default facetContainers="${results.facetContainers}"/>
				&nbsp;	
			</div>
			<div  id="searchresultsContainer">	
				<div id="searchOrder">
					<div id="searchOrderTitle"><fmt:message key="advancedsearch.text.sortsearch" /></div>
					<searchresults:order currentValue="${advancedSearch.order}" value="relevancy" key="advancedsearch.order.relevancy" />
					|
					<searchresults:order currentValue="${advancedSearch.order}" value="startdate" key="advancedsearch.text.date" />
					|
					<searchresults:order currentValue="${advancedSearch.order}" value="titlesort" key="advancedsearch.text.title2" />
					|
					<searchresults:order currentValue="${advancedSearch.order}" value="unitidsort" key="advancedsearch.text.refcode" />
					|
					<searchresults:order currentValue="${advancedSearch.order}" value="unitidfondsort" key="advancedsearch.order.eadid" />				
				</div>
			<c:if test="${results.totalNumberOfResults > 0}">					
			<div id="searchresultsList">
				<c:forEach var="result" items="${results.items}">
					<div class="list-searchresult" id="list-searchresult-${result.id}">
						<div class="list-searchresult-content">
							<div class="list-searchresult-header">
								<c:choose>
									<c:when test="${empty result.title}">
										<c:set var="title"><fmt:message key="advancedsearch.text.notitle" /></c:set>
										<c:set var="titleShortDescription"><fmt:message key="advancedsearch.text.notitle" /></c:set>
										<c:set var="titleClass" value="searchresulttitle notitle"/>
									</c:when>
									<c:otherwise>
										<c:set var="title" value="${result.title}"/>
										<c:set var="titleShortDescription" value="${result.titleShortDescription}"/>
										<c:set var="titleClass" value="searchresulttitle"/>								
									</c:otherwise>
								</c:choose>	
								<liferay-portlet:renderURL var="displayEadUrl" plid="${eadDisplayPageId}" portletName="${eadDisplayPortletName}" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
									<liferay-portlet:param name="id" value="${result.id}"/>
									<liferay-portlet:param  name="term" value="${advancedSearch.term}"/>
									<liferay-portlet:param  name="element" value="${advancedSearch.element}"/>
								</liferay-portlet:renderURL>				
								<a class="ccontent" target="_blank" title="${title}"
									href="${displayEadUrl}">
									<span class="${titleClass}">${titleShortDescription}</span>
								</a>													
								<c:if test="${!empty result.alterdate}">
									<span class="alterdate" title="${result.alterdate}">${result.alterdateShortDescription}</span>
								</c:if>
							</div>
							<div>${result.scopecontent}</div>
							<div>${result.other}</div>
								<c:if test="${result.dao}">
								<span  class="special_info">
								<fmt:message key="advancedsearch.facet.title.dao" />
									<c:forEach var="daoType" items="${result.roledao}">
											<c:if test="${fn:contains(daoType, 'IMAGE')}">
												<fmt:message var="daoTitle" key="advancedsearch.dao.image" />
												<span class="icon_dao_type_small_image" title="${daoTitle}">${daoTitle}</span>
											</c:if>
											<c:if test="${fn:contains(daoType, 'TEXT')}">
												<fmt:message var="daoTitle" key="advancedsearch.dao.text" />
												<span class="icon_dao_type_small_text" title="${daoTitle}">${daoTitle}</span>
											</c:if>
											<c:if test="${fn:contains(daoType, 'VIDEO')}">
												<fmt:message var="daoTitle" key="advancedsearch.dao.video" />
												<span class="icon_dao_type_small_video" title="${daoTitle}">${daoTitle}</span>
											</c:if>
											<c:if test="${fn:contains(daoType, 'SOUND')}">
												<fmt:message var="daoTitle" key="advancedsearch.dao.sound" />
												<span class="icon_dao_type_small_sound" title="${daoTitle}">${daoTitle}</span>
											</c:if>
											<c:if test="${fn:contains(daoType, 'UNSPECIFIED')}">
												<fmt:message var="daoTitle" key="advancedsearch.dao.unspecified" />
												<span class="icon_dao_type_small_unspecified" title="${daoTitle}">${daoTitle}</span>
											</c:if>							
									</c:forEach>					
								</span>
								</c:if>	
							<div class="list-searchresult-context">
								<div>
									<span class="subtitle"><fmt:message key="advancedsearch.message.document" /></span><c:out value="${result.fond}"/>
								</div>
								<div>
								<div class="left">
									<div>
										<c:choose>
											<c:when test="${!empty result.unitIdOfFond}">
												<span class="subtitle"><fmt:message key="advancedsearch.message.eadid" /></span>${result.unitIdOfFond}
											</c:when>
											<c:when test="${!empty result.unitid}">
												<span class="secondSubtitle"><fmt:message key="advancedsearch.message.referencecode" /></span>${result.unitid}
											</c:when>
											<c:when test="${!empty result.otherUnitid}">
												<span class="secondSubtitle"><fmt:message key="advancedsearch.message.referencecode" /></span>${result.otherUnitid}
											</c:when>																		
										</c:choose>
									</div>
									<div><fmt:message key="country.${fn:toLowerCase(result.country)}" />&nbsp;-&nbsp;<c:out value="${result.ai}" /></div>
								</div>
								<div class="list-searchresult-actions hidden">
									<ul>
										<li><a href="javascript:addOnlyThisRefinement('fond','${result.fondId }','${result.escapedFond}','${result.escapedFond}')"><fmt:message key="advancedsearch.facet.document.only" /></a></li>
									</ul>
								</div>
								</div>
							</div>
													
						</div>
						<portlet:resourceURL var="displayPreviewUrl" id="displayPreview" >
							<portlet:param  name="id" value="${result.id}"/>
							<portlet:param  name="term" value="${advancedSearch.term}"/>
							<portlet:param  name="element" value="${advancedSearch.element}"/>
						</portlet:resourceURL>
						<div class="preview-button-holder"
						url="${displayPreviewUrl}">&nbsp;</div>
					</div>
				</c:forEach>
					<div id="bottom-paging" class="paging">
					<ape:paging numberOfItems="${results.totalNumberOfResults}" pageSize="${results.pageSize}" pageNumber="${advancedSearch.pageNumber}"
							refreshUrl="javascript:updatePageNumber('');" pageNumberId="pageNumber"/>	
					</div>	
			</div>
			</c:if>
			</div>
			<div id="preview-absolute" class="preview-container"></div>
			</div>
			
		</c:if>
</div>