<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="searchresults" uri="http://portal.archivesportaleurope.eu/tags/searchresults"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%> 
<%@ taglib prefix="facets" tagdir="/WEB-INF/tags/facets"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet" %>
<portlet:defineObjects />
<div id="searchingPart">
<div id="eacCpfSearchPortlet" class="searchPortlet">
<portlet:renderURL var="eacCpfSearchUrl">
	<portlet:param name="myaction" value="eacCpfSearch" />
</portlet:renderURL>
<portal:friendlyUrl var="friendlyUrl" type="eac-cpf-display"/>
<form:form id="eacCpfSearchForm" name="eacCpfSearchForm" commandName="eacCpfSearch" method="post"
				action="${eacCpfSearchUrl}">
					<div id="simpleSearch">
						<div><form:input path="term" id="searchTerms"  tabindex="1" maxlength="100"/> <input
											type="submit" id="searchButton" title="<fmt:message key="advancedsearch.message.start"/>" tabindex="10"
											value="<fmt:message key="advancedsearch.message.search"/>" /></div>
					</div>
</form:form>
<div>
<c:if test="${!empty results}">
<h2 id="searchResultsHeader">
	<fmt:message key="advancedsearch.text.results" />:
</h2>
<div class="results">
		<div class="tab_header">
			<div id="tabHeaderContent">
				
						<div id="numberOfResults">
							<span class="bold"><fmt:message key="advancedsearch.text.results" />:</span>
							<ape:pageDescription numberOfItems="${results.totalNumberOfResults}" pageSize="${results.pageSize}" pageNumber="${eacCpfSearch.pageNumber}" numberFormat="${numberFormat}" />
						</div>
						<div id="top-paging" class="paging">
						<ape:paging numberOfItems="${results.totalNumberOfResults}" pageSize="${results.pageSize}" pageNumber="${eacCpfSearch.pageNumber}"
								refreshUrl="javascript:updatePageNumber('');" pageNumberId="pageNumber"/>	
						</div>

			</div>
		</div>
			<div id="resultsContainer">
			<div id="refinements">
				Facets comes here
				&nbsp;	
			</div>
			<div  id="searchresultsContainer">	
				<div id="searchOrder">
					<div id="searchOrderTitle"><fmt:message key="advancedsearch.text.sortsearch" /></div>
					<searchresults:order currentValue="${eacCpfSearch.order}" value="relevancy" key="advancedsearch.order.relevancy" />
					|
					sdfadf
					|
					asdf		
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
			</div>
</div>
</div>
</c:if>
</div>

</div>
</div>