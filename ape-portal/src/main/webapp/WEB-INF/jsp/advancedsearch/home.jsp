<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<fmt:requestEncoding value="UTF-8" />
<portlet:defineObjects />
<portlet:renderURL var="advancedSearchUrl">
	<portlet:param name="myaction" value="advancedSearch" />
</portlet:renderURL>
<c:set var="portletNamespace"><portlet:namespace/></c:set>
<portal:removeParameters  var="ajaxAdvancedSearchUrl" namespace="${portletNamespace}" parameters="myaction,term,resultsperpage"><portlet:resourceURL id="advancedSearch" /></portal:removeParameters>
<portlet:resourceURL var="autocompletionUrl" id="autocompletion" />
<portlet:resourceURL var="navigatedTreeUrl" id="navigatedTree" />
<portlet:resourceURL var="generateNavigatedTreeAiUrl" id="generateNavigatedTreeAi" />
<portlet:resourceURL var="generateNavigatedTreeAiContentUrl" id="generateNavigatedTreeAiContent" />
<portlet:resourceURL var="displayPreviewUrl" id="displayPreview"/>
<portlet:resourceURL var="archivalLandscapeTreeUrl" id="archivalLandscapeTree" />

<c:choose>
	<c:when test="${!empty renderRequest.preferences.map.eadDisplayFriendlyUrl and !empty renderRequest.preferences.map.eadDisplayPortletName}">
		<script type="text/javascript">
			$(document).ready(function() {
				setUrls("${ajaxAdvancedSearchUrl}","${autocompletionUrl}" );
				init();
				initArchivalLandscapeTree("${archivalLandscapeTreeUrl}","${displayPreviewUrl}", "<portlet:namespace/>");
			});
		</script>
		<div id="advancedSearchPortlet">
			<form:form id="newSearchForm" name="advancedSearchForm" commandName="advancedSearch" method="post"
				action="${advancedSearchUrl}">
				<form:hidden id="mode" path="mode" />
				<form:hidden id="advanced" path="advanced" />
				<form:hidden id="selectedNodes" path="selectedNodes"/>
				<form:hidden id="expandedNodes" path="expandedNodes"/>		
				<div id="navigatedSearch">
					<h2 id="navigatedSearchOptionsHeader" class="blockHeader collapsed">
						<fmt:message key="advancedsearch.title.navigatedsearch" />
					</h2>
					<div id="navigatedSearchOptionsContent" class="searchOptionsContent">
						<div id="navigatedSearchOptionsSubHeader">
							<fmt:message key="advancedsearch.subtitle.navigatedsearch" />
						</div>
						<div id="archivalLandscapeTree" class="treeWithPreview"></div>
						<div class="preview-column">
						<div id="al-preview" class="preview-container al-preview-container"></div>
						</div>
					</div>
				</div>
				<div id="simpleAndAdvancedSearch">
					<div id="advancedSearch">
						<h2 id="advancedSearchOptionsHeader" class="blockHeader">
							<fmt:message key="advancedsearch.title.advancedsearch" />
						</h2>
						<div id="advancedSearchOptionsContent" class="searchOptionsContent">
							<table id="advancedsearchCriteria">
								<tr>
									<td><label for="element"><fmt:message key="advancedsearch.text.selectelement" /></label></td>
									<td colspan="3"><form:select path="element" id="element" tabindex="6">
											<form:option value="0">
												<fmt:message key="advancedsearch.text.noselection" />
											</form:option>
											<form:option value="1">
												<fmt:message key="advancedsearch.text.title1" />
											</form:option>
											<form:option value="2">
												<fmt:message key="advancedsearch.text.contentsummary" />
											</form:option>
											<form:option value="3">
												<fmt:message key="advancedsearch.text.referencecode" />
											</form:option>
										</form:select></td>
								</tr>
								<tr>
									<td><label for="typedocument"><fmt:message key="advancedsearch.text.selectdocument" /></label></td>
									<td colspan="3"><form:select path="typedocument" id="typedocument" tabindex="7">
											<form:option value="">
												<fmt:message key="advancedsearch.text.noselection" />
											</form:option>
											<form:option value="fa">
												<fmt:message key="advancedsearch.text.onlyfas" />
											</form:option>
											<form:option value="hg">
												<fmt:message key="advancedsearch.text.onlyhgs" />
											</form:option>
											<form:option value="sg">
												<fmt:message key="advancedsearch.text.onlysgs" />
											</form:option>											
										</form:select></td>
								</tr>
								<tr>
									<td><label for="fromdate"><fmt:message key="advancedsearch.text.datefrom" /></label></td>
									<td><form:input path="fromdate" id="fromdate" cssClass="datefield" tabindex="8" /></td>
									<td id="datetoHeader"><label id="todateLabel" for="todate"><fmt:message key="advancedsearch.text.dateto" /></label></td>
									<td><form:input path="todate" id="todate" cssClass="datefield" tabindex="9" /></td>
								</tr>
								<tr>
									<td></td>
									<td colspan="3"><input type="checkbox" id="exactDateSearch" value="true" name="exactDateSearch" tabindex="10"><label for="exactDateSearch"><fmt:message key="advancedsearch.message.exactdatesearch" /></label></td>
								</tr>
		
							</table>
						</div>
					</div>
					<div id="simpleSearch">
						<h2 id="simpleSearchOptionsHeader" class="blockHeader">
							<fmt:message key="advancedsearch.title.simplesearch" />
						</h2>
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
												<c:choose>
													<c:when test="${param['view'] == 'hierarchy'}">
														<input type="checkbox" id="checkboxHierarchy" value="hierarchy" name="view" checked="checked" tabindex="2">
													</c:when>
													<c:otherwise>
														<input type="checkbox" id="checkboxHierarchy" value="hierarchy" name="view" tabindex="2">
													</c:otherwise>
												</c:choose>
												<label for="checkboxHierarchy"><fmt:message key="advancedsearch.message.hierarchy" /></label>
											</div>
											<div class="row">
												<c:choose>
													<c:when test="${param['method'] == 'optional'}">
														<input type="checkbox" id="checkboxMethod" value="optional" name="method" checked="checked" tabindex="3">
													</c:when>
													<c:otherwise>
														<input type="checkbox" id="checkboxMethod" value="optional" name="method" tabindex="3">
													</c:otherwise>
												</c:choose>
												<label for="checkboxMethod"><fmt:message key="advancedsearch.message.method" /></label>
											</div>
											<div class="row">
												<c:choose>
													<c:when test="${param['dao'] == 'true'}">
														<input type="checkbox" id="checkboxDao" value="true" name="dao" checked="checked" tabindex="4">
													</c:when>
													<c:otherwise>
														<input type="checkbox" id="checkboxDao" value="true" name="dao" tabindex="4">
													</c:otherwise>
												</c:choose>
												<label for="checkboxDao"><fmt:message key="advancedsearch.message.dao" /></label>
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
			<c:if test="${advancedSearch.mode == 'new'}">
				<c:set var="showResults" value="hidden" />
			</c:if>
			<div id="resultsContainer" class="${showResults }">
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
					<ul id="tabscontainer">
						<li><a href="#tabs-list"><fmt:message key="advancedsearch.text.list" /></a></li>
						<li><a href="#tabs-context"><fmt:message key="advancedsearch.text.context" /></a></li>
		
					</ul>
					<div id="tabs-context">
						<c:if test="${advancedSearch.mode != 'new' and advancedSearch.view == 'hierarchy'}">
						<jsp:include page="results.jsp" />
						</c:if> 
					</div>
					<div id="tabs-list">
						<c:if test="${advancedSearch.mode != 'new' and advancedSearch.view != 'hierarchy'}">
						<jsp:include page="results.jsp" />
					</c:if>
					</div>
				</div>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<liferay-ui:message key="please-contact-the-administrator-to-setup-this-portlet" />
	</c:otherwise>
</c:choose>

