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
<c:if test="${!empty eadSearch.selectedSimpleSearchTopic}">
	<div class="hidden" id="NEWsimpleSearchTopicSelected">
	<li><a title='${eadSearch.selectedSimpleSearchTopic.longDescription}' href="javascript:removeSearchOption('#simpleSearchTopicSelected')">
${eadSearch.selectedSimpleSearchTopic.longDescription}<span class='close-icon'></span></a></li>	
</div>						
</c:if>

<portal:sourceTabs results="${results}" ajax="true" type="ead"/>
<form:form id="updateCurrentSearch" name="eadSearchForm" commandName="eadSearch" method="post">
		<form:hidden id="updateCurrentSearch_term" path="term"/>
		<form:hidden id="updateCurrentSearch_method" path="method"/>
		<form:hidden id="updateCurrentSearch_mode" path="mode" value="update-search"/>
		<form:hidden id="updateCurrentSearch_advanced" path="advanced"/>
		<form:hidden id="updateCurrentSearch_simpleSearchDao" path="simpleSearchDao"/>
		<form:hidden id="updateCurrentSearch_simpleSearchTopic" path="simpleSearchTopic"/>		
		<form:hidden id="updateCurrentSearch_view" path="view"/>
		<form:hidden id="updateCurrentSearch_element" path="element"/>
		<form:hidden id="updateCurrentSearch_typedocument" path="typedocument"/>
		<form:hidden id="updateCurrentSearch_fromdate" path="fromdate"/>
		<form:hidden id="updateCurrentSearch_todate" path="todate"/>
		<form:hidden id="updateCurrentSearch_exactDateSearch" path="exactDateSearch"/>
		<form:hidden id="updateCurrentSearch_language" path="language"/>
		<input type="hidden" id="updateCurrentSearch_country" name="country" value="${eadSearch.country}"/>
		<input type="hidden" id="updateCurrentSearch_ai" name="ai" value="${eadSearch.ai}"/>
		<input type="hidden" id="updateCurrentSearch_topic" name="topic" value="${eadSearch.topic}"/>		
		<input type="hidden" id="updateCurrentSearch_fond" name="fond" value="${eadSearch.fond}"/>
		<input type="hidden" id="updateCurrentSearch_type" name="type" value="${eadSearch.type}"/>
		<input type="hidden" id="updateCurrentSearch_dao" name="dao" value="${eadSearch.dao}"/>
		<input type="hidden" id="updateCurrentSearch_roledao" name="roledao" value="${eadSearch.roledao}"/>
		<input type="hidden" id="updateCurrentSearch_level" name="level" value="${eadSearch.level}"/>
		<input type="hidden" id="updateCurrentSearch_dateType" name="dateType" value="${eadSearch.dateType}"/>
		<input type="hidden" id="updateCurrentSearch_facetSettings" name="facetSettings" value="${eadSearch.facetSettings}"/>
		<form:hidden id="updateCurrentSearch_order" path="order"/>
		<form:hidden id="updateCurrentSearch_startdate" path="startdate"/>	
		<form:hidden id="updateCurrentSearch_enddate" path="enddate"/>
		<form:hidden id="updateCurrentSearch_selectedNodes" path="selectedNodes"/>
		<form:hidden id="updateCurrentSearch_pageNumber" path="pageNumber"/>
		<form:hidden id="updateCurrentSearch_publishedFromDate" path="publishedFromDate"/>
		<form:hidden id="updateCurrentSearch_publishedToDate" path="publishedToDate"/>
	<c:choose>
		<c:when test="${eadSearch.view == 'hierarchy'}">
			<jsp:include page="results-contexttab.jsp"/>
		</c:when>
		<c:otherwise>
			<jsp:include page="results-listtab.jsp"/>
		</c:otherwise>	
	</c:choose>

</form:form>	