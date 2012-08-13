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
<portlet:resourceURL var="ajaxAdvancedSearchUrl" id="advancedSearch" />
<portlet:resourceURL var="autocompletionUrl" id="autocompletion" />
<portlet:resourceURL var="navigatedTreeUrl" id="navigatedTree" />
<portlet:resourceURL var="generateNavigatedTreeAiUrl" id="generateNavigatedTreeAi" />
<portlet:resourceURL var="generateNavigatedTreeAiContentUrl" id="generateNavigatedTreeAiContent" />
<portlet:resourceURL var="displayPreviewUrl" id="displayPreview"/>
<c:choose>
	<c:when test="${!empty renderRequest.preferences.map.eadDisplayPageId and !empty renderRequest.preferences.map.eadDisplayPortletName}">
		<script type="text/javascript">
			$(document).ready(function() {
				setUrls("${ajaxAdvancedSearchUrl}","${autocompletionUrl}" );
				init();
				initNavigatedTree("${navigatedTreeUrl}", "${generateNavigatedTreeAiUrl}", "${generateNavigatedTreeAiContentUrl}","${displayPreviewUrl}", "<portlet:namespace/>");
			});
		</script>
		<div id="advancedSearchPortlet">
			<form:form id="newSearchForm" name="advancedSearchForm" commandName="advancedSearch" method="post"
				action="${advancedSearchUrl}">
				<form:hidden id="mode" path="mode" />
				<form:hidden id="advanced" path="advanced" />
		
				<div id="navigatedSearch">
					<div id="navigatedSearchOptionsHeader" class="searchOptionsHeader collapsed">
						<fmt:message key="advancedsearch.title.navigatedsearch" />
					</div>
					<div id="navigatedSearchOptionsContent" class="searchOptionsContent">
						<div id="navigatedSearchOptionsSubHeader">
							<fmt:message key="advancedsearch.subtitle.navigatedsearch" />
						</div>
						<div id="archivalLandscapeTree" class="treeWithPreview"></div>
							<form:select id="navigationTreeNodesSelected" path="navigationTreeNodesSelected" multiple="true" cssClass="hidden"/>
						<div id="al-preview"  class="preview-container">
				
						</div>
					</div>
				</div>
				<div id="simpleAndAdvancedSearch">
					<div id="advancedSearch">
						<div id="advancedSearchOptionsHeader" class="searchOptionsHeader">
							<fmt:message key="advancedsearch.title.advancedsearch" />
						</div>
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
		
							</table>
						</div>
					</div>
					<div id="simpleSearch">
						<div id="simpleSearchOptionsHeader" class="searchOptionsHeader">
							<fmt:message key="advancedsearch.title.simplesearch" />
						</div>
						<div id="simpleSearchOptionsContent" class="searchOptionsContent">
							<div class="simpleSearchOptions">
								<table>
									<fmt:message key="advancedsearch.message.typesearchterms2" var="termTitle" />
									<tr>
										<td colspan="2"><form:input path="term" id="searchTerms" title="${termTitle}" tabindex="1" /> <input
											type="submit" id="searchButton" title="<fmt:message key="advancedsearch.message.start"/>" tabindex="10"
											value="<fmt:message key="advancedsearch.message.search"/>" /></td>
									</tr>
									<tr>
										<td class="leftcolumn">
											<div class="row">
												<label for="checkboxHierarchy"><fmt:message key="advancedsearch.message.hierarchy" /></label>
												<c:choose>
													<c:when test="${param['view'] == 'hierarchy'}">
														<input type="checkbox" id="checkboxHierarchy" value="hierarchy" name="view" checked="checked" tabindex="2">
													</c:when>
													<c:otherwise>
														<input type="checkbox" id="checkboxHierarchy" value="hierarchy" name="view" tabindex="2">
													</c:otherwise>
												</c:choose>
											</div>
											<div class="row">
												<label for="checkboxMethod"><fmt:message key="advancedsearch.message.method" /></label>
												<c:choose>
													<c:when test="${param['method'] == 'optional'}">
														<input type="checkbox" id="checkboxMethod" value="optional" name="method" checked="checked" tabindex="3">
													</c:when>
													<c:otherwise>
														<input type="checkbox" id="checkboxMethod" value="optional" name="method" tabindex="3">
													</c:otherwise>
												</c:choose>
											</div>
											<div class="row">
												<label for="checkboxDao"><fmt:message key="advancedsearch.message.dao" /></label>
												<c:choose>
													<c:when test="${param['dao'] == 'true'}">
														<input type="checkbox" id="checkboxDao" value="true" name="dao" checked="checked" tabindex="4">
													</c:when>
													<c:otherwise>
														<input type="checkbox" id="checkboxDao" value="true" name="dao" tabindex="4">
													</c:otherwise>
												</c:choose>
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
				<div id="searchResultsHeader">
					<fmt:message key="text.search.results" />
				</div>
		
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

