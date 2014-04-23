<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%> 
<portlet:defineObjects />
<portlet:resourceURL var="contextTreeUrl" id="contextTree" />
<portlet:resourceURL var="displayPreviewUrl" id="displayPreview" >
	<portlet:param  name="element" value="${advancedSearch.element}"/>
</portlet:resourceURL>
<script type="text/javascript">
	$(document).ready(function() {
		initContextTabHandlers("${contextTreeUrl}", "${displayPreviewUrl}", "<portlet:namespace/>");
    });
</script>

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
						${results.totalNumberOfResultsString}
					</div>			
				</c:when>
				<c:otherwise>
					<span id="noResults"><fmt:message key="search.message.notResults" /></span>
				</c:otherwise>
			</c:choose>
			</div>
		</div>  
		<c:if test="${empty results.errorMessage and results.totalNumberOfResults > 0}">
		<div id="contextTabResults">
			<div id="contextTabTree"  class="treeWithPreview">
				<ul>
					<c:forEach var="country" items="${results.countries}">
				    	<c:set var="countryTitle" ><fmt:message key="country.${fn:toLowerCase(country.name)}"/> <span>(${country.count})</span></c:set>
			    			<li data="term: '${advancedSearch.term}', icon: false, country: '${country.id}', searchType: 'ai', matchAllWords: '${advancedSearch.method}',isFolder: true, isLazy: true, title: '${countryTitle}'"></li>
			     		</c:forEach>
				</ul>
			</div>
			<div class="preview-column">
			<div id="search-preview" class="preview-container search-result-preview-container"></div>
			</div>
		</div>
		</c:if>
</div>	

