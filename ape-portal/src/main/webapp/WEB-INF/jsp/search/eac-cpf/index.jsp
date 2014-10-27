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
<portal:friendlyUrl var="helpUrl" type="help-pages"/>
<portal:page  varPlId="eadSearchPlId"  varPortletId="eadSearchPortletId" portletName="eadsearch" friendlyUrl="/search"/>	
<liferay-portlet:renderURL var="eadSearchUrl"  plid="${eadSearchPlId}" portletName="${eadSearchPortletId}">
	<portlet:param name="myaction" value="simpleSearch" />
	<liferay-portlet:param  name="advanced" value="false"/>
</liferay-portlet:renderURL>
<portal:page  varPlId="eagSearchPlId"  varPortletId="eagSearchPortletId" portletName="eagsearch" friendlyUrl="/institution-search"/>		
<liferay-portlet:renderURL var="eagSearchUrl"  plid="${eagSearchPlId}" portletName="${eagSearchPortletId}">
	<portlet:param name="myaction" value="simpleSearch" />
</liferay-portlet:renderURL>
<c:set var="portletNamespace"><portlet:namespace/></c:set>
<portal:removeParameters  var="ajaxEacCpfSearchUrl" namespace="${portletNamespace}" parameters="myaction,term,resultsperpage"><portlet:resourceURL id="eacCpfSearch" /></portal:removeParameters>
<portal:removeParameters  var="autocompletionUrl" namespace="${portletNamespace}" parameters="myaction,term,resultsperpage,advanced,dao,view,method"><portlet:resourceURL id="autocompletion" /></portal:removeParameters>
		<script type="text/javascript">
			$(document).ready(function() {
				setCommonUrls("","${eadSearchUrl}", "${eagSearchUrl}");
				setUrls("${ajaxEacCpfSearchUrl}","${autocompletionUrl}");
				init();
				initTooltip('#sourceTabs .icon_help', "#sourceTabsSearchHelpDialog", "left", "center");
				initTooltip('#simpleSearch .icon_help', "#simpleSearchHelpDialog", "left", "center");
				initTooltip('#searchResultsContainer #tabs .icon_help', "#searchResultsHelpDialog", "right", "center");						
			});
		</script>
<div id="searchingPart">
	<div id="eacCpfSearchPortlet" class="searchPortlet">
<portal:sourceTabs results="${results}" type="eacCpf"/>

<portlet:renderURL var="eacCpfSearchUrl">
	<portlet:param name="myaction" value="eacCpfSearch" />
</portlet:renderURL>
<portal:friendlyUrl var="friendlyUrl" type="eac-cpf-display"/>
<div id="searchOptions">
			<div id="sourceTabsSearchHelpDialog" class="hidden">
				<div class="tooltipContent">
					<h3><fmt:message key="search.sourcetabs.help.title"/>:</h3>
					<ul>
						<li><b><fmt:message key="menu.archives-search"/></b>&nbsp;<fmt:message key="search.sourcetabs.help.ead.description"/></li>
						<li><b><fmt:message key="menu.name-search"/></b>&nbsp;<fmt:message key="search.sourcetabs.help.eac-cpf.description"/></li>
						<li><b><fmt:message key="menu.institution-search"/></b>&nbsp;<fmt:message key="search.sourcetabs.help.eac-cpf.description"/></li>
					</ul>
				</div>
			</div>		
			<div id="simpleSearchHelpDialog" class="hidden">
				<div class="tooltipContent">			
					<div class="tooltipSubContent">
						<ul>
							<li><fmt:message key="search.method.help" /></li>
						</ul>		
					</div>
					<div class="linkToMoreHelp"><fmt:message key="search.help.more" />&nbsp;<a href="${helpUrl}/searching" target="blank"><fmt:message key="search.help.more.link" /></a>.</div>
				</div>
			</div>
		
<form:form id="newSearchForm" name="eacCpfSearchForm" commandName="eacCpfSearch" method="post"
				action="${eacCpfSearchUrl}">
				<form:hidden id="mode" path="mode" />
				<div id="simpleAndAdvancedSearch">
					<div id="advancedSearch">
						<h2 id="advancedSearchOptionsHeader" class="blockHeader">
							<fmt:message key="advancedsearch.title.advancedsearch" />
						</h2>
						<div id="advancedSearchOptionsContent" class="searchOptionsContent">
							<table id="advancedsearchCriteria">
								<tr>
									<td><label for="element"><fmt:message key="advancedsearch.text.selectelement" />:</label></td>
									<td colspan="3"><form:select path="element" id="element" tabindex="6" items="${eacCpfSearch.elementValues}"/></td>
								</tr>
								<tr>
									<td><label for="entityType"><fmt:message key="advancedsearch.text.eaccpf.entitytype" />:</label></td>
									<td colspan="3"><form:select path="entityType" id="entityType" tabindex="7"  items="${eacCpfSearch.entityTypeValues}"/></td>
								</tr>							
								<tr>
									<td><label for="fromdate"><fmt:message key="advancedsearch.text.eaccpf.datefrom" /></label></td>
									<td><form:input path="fromdate" id="fromdate" cssClass="datefield" tabindex="9" maxlength="10" /></td>
									<td id="datetoHeader"><label id="todateLabel" for="todate"><fmt:message key="advancedsearch.text.dateto" /></label></td>
									<td><form:input path="todate" id="todate" cssClass="datefield" tabindex="10"  maxlength="10"/></td>
								</tr>
								<tr>
									<td></td>
									<td colspan="3"><form:checkbox id="exactDateSearch" path="exactDateSearch" value="true" tabindex="11"/><label for="exactDateSearch"><fmt:message key="advancedsearch.message.exactdatesearch" /></label></td>
								</tr>
		
							</table>
						</div>
					</div>				
					<div id="simpleSearch">
						<h2 id="simpleSearchOptionsHeader" class="blockHeader">
							<fmt:message key="advancedsearch.title.simplesearch" /><span class="icon_help"></span>
						</h2>					
						<div id="simpleSearchOptionsContent" class="searchOptionsContent">
							<div class="simpleSearchOptions">
								<table id="simplesearchCriteria">
									<fmt:message key="advancedsearch.message.typesearchterms2" var="termTitle" />
									<tr>
										<td colspan="2"><form:input path="term" id="searchTerms" title="${termTitle}" tabindex="1" maxlength="100"/> <input
											type="submit" id="searchButton" title="<fmt:message key="advancedsearch.message.start"/>" tabindex="10"
											value="<fmt:message key="advancedsearch.message.search"/>" /></td>
									</tr>
									<tr>
										<td class="leftcolumn">
											<div class="row">
												<form:checkbox path="method" id="checkboxMethod" tabindex="3" value="optional"/>
												<label for="checkboxMethod"><fmt:message key="advancedsearch.message.method" /></label>
											</div>
										</td>
										<td class="rightcolumn">
											<div id="clearSearchRow" class="row">
												<a href="javascript:clearSearch()"><fmt:message key="searchpage.options.simple.clearsearch" /></a>	
											</div>
										</td>
									</tr>
								</table>
							</div>
						</div>
					</div>
				</div>				
</form:form>
</div>
			<c:if test="${empty results or eacCpfSearch.mode == 'new'}">
				<c:set var="showResults" value="hidden" />
			</c:if>
			<div id="searchResultsContainer" class="${showResults }">
				<div class="suggestionSearch" id="suggestionSearch">
					<c:if test="${results.showSuggestions}">
						<span class="suggestionText"> <c:choose>
								<c:when test="${results.totalNumberOfResults > 0}">
									<fmt:message key="advancedsearch.message.suggestion.results" />
								</c:when>
								<c:otherwise>
									<fmt:message key="advancedsearch.message.suggestion.noresults" />
								</c:otherwise>
							</c:choose>
						</span>
						<br />
						<portal:autosuggestion spellCheckResponse="${results.spellCheckResponse}" styleClass="suggestionLink"
							numberOfResultsStyleClass="suggestionNumberOfHits" misSpelledStyleClass="suggestionMisspelled" />
					</c:if>
				</div>
				<h2 id="searchResultsHeader">
					<fmt:message key="advancedsearch.text.results" />:
				</h2>
		
				<div id="tabs">
					<div id="tabs-list">
						<c:if test="${!empty results and eacCpfSearch.mode != 'new'}">
						<jsp:include page="results.jsp" />
					</c:if>
					</div>
				</div>
			</div>
			<div id="loadingText" class="hidden"><fmt:message key="advancedsearch.message.loading"/>
			</div>
</div>
</div>