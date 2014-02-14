<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet" %>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%> 
<portlet:defineObjects />
<portlet:renderURL var="simpleSearchUrl" >
	<portlet:param name="myaction" value="simpleSearch" />
</portlet:renderURL>
<portal:friendlyUrl var="eadFriendlyUrl" type="eaddisplay-search"/>
<portal:friendlyUrl var="eacCpfFriendlyUrl" type="eac-cpf-display"/>
<div id="searchingPart">
<div id="simpleSearchPortlet">
	<form:form id="simpleSearchForm" name="simpleSearchForm" commandName="simpleSearch" method="post" action="${simpleSearchUrl}">
		<div id="simpleSearch">
			<div id="simpleSearchOptionsContent" class="searchOptionsContent">
				<div class="simpleSearchOptions">
					<table id="simplesearchCriteria">
						<fmt:message key="advancedsearch.message.typesearchterms2" var="termTitle" />
						<tr>
							<td colspan="2"><form:input path="term" id="searchTerms" title="${termTitle}" tabindex="1" maxlength="100" /> <input
								type="submit" id="searchButton" title="<fmt:message key="advancedsearch.message.start"/>" tabindex="10"
								value="<fmt:message key="advancedsearch.message.search"/>" /></td>
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
<h2 id="searchResultsHeader">
	<fmt:message key="advancedsearch.text.results" />:
</h2>
<div class="results">
			<div id="resultsContainer">
			<div  id="eadResultsContainer" class="box">	
				<div class="boxtitle">
					Archival Material(${eadResults.totalNumberOfResults}) <a href="advanced-search">Advanced</a>	
				</div>
				<portal:generateSearchWords var="encodedTerm" term="${simpleSearch.term}" element="0"/>		
				<div id="searchresultsList">	
				<c:forEach var="result" items="${eadResults.items}">
					<div class="list-searchresult" id="list-searchresult-${result.id}">
						<div class="list-searchresult-content">
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
								<c:choose>
									<c:when test="${empty encodedTerm}">
										<c:set var="url" value="${eadFriendlyUrl}/${result.id}"/>
									</c:when>
									<c:otherwise>
										<c:set var="url" value="${eadFriendlyUrl}/${result.id}/0/${encodedTerm}"/>
									</c:otherwise>
								</c:choose>
								<a class="unittitle ${titleClass}" target="_blank" title="${titleWithoutHighlighting}"
									href="${url}">${title}
								</a>
								<c:if test="${!empty result.alterdate}">
									<span class="alterdate" title="${result.alterdateWithoutHighlighting}">${result.alterdate}</span>
								</c:if>																					
							</div>					
						</div>
					</div>
					</c:forEach>
				</div>
			</div>
			<div  id="eacCpfResultsContainer" class="box" >	
				<div class="boxtitle">
					Persons & Entities(${eacCpfResults.totalNumberOfResults})  <a href="persons-entities">Advanced</a>		
				</div>
	
				<div id="searchresultsList">	
				<c:forEach var="result" items="${eacCpfResults.items}">
					<div class="list-searchresult" id="list-searchresult-${result.id}">
						<div class="list-searchresult-content">
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
							<c:choose>
									<c:when test="${empty encodedTerm}">
										<c:set var="url" value="${eacCpfFriendlyUrl}/${result.id}"/>
									</c:when>
									<c:otherwise>
										<c:set var="url" value="${eacCpfFriendlyUrl}/${result.id}/0/${encodedTerm}"/>
									</c:otherwise>
								</c:choose>
								<a class="unittitle ${titleClass}" target="_blank" title="${titleWithoutHighlighting}"
									href="${url}">${title}
								</a>
								<c:if test="${!empty result.alterdate}">
									<span class="alterdate" title="${result.alterdateWithoutHighlighting}">${result.alterdate}</span>
								</c:if>																					
							</div>					
						</div>
					</div>
					</c:forEach>
				</div>
			</div>
			<div  id="eagResultsContainer" class="box">	
				<div class="boxtitle">
					Archival Institutions(2)   <a href="directory">Advanced</a>		
				</div>
	
				<div id="searchresultsList">	
					<div class="list-searchresult" id="list-searchresult-1">
						<div class="list-searchresult-content">
						<div class="list-searchresult-header">
								<a class="unittitle" target="_blank"
									href="directory">Bundesarchiv
								</a>
								<span class="alterdate">Germany</span>											
							</div>					
						</div>
					</div>
					<div class="list-searchresult" id="list-searchresult-2">
						<div class="list-searchresult-content">
						<div class="list-searchresult-header">
								<a class="unittitle" target="_blank"
									href="directory">Nationaal Archief
								</a>
								<span class="alterdate">Netherlands</span>											
							</div>					
						</div>
					</div>					
				</div>
			</div>						
</div>
</div>


</div>
</div>