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
		<portal:autosuggestion solrResponse="${results.solrResponse}" styleClass="suggestionLink"
			numberOfResultsStyleClass="suggestionNumberOfHits" misSpelledStyleClass="suggestionMisspelled" />
	</div>
</c:if>
<form:form id="updateCurrentSearch" name="advancedSearchForm" commandName="advancedSearch" method="post">
		<form:hidden id="updateCurrentSearch_term" path="term"/>
		<form:hidden id="updateCurrentSearch_method" path="method"/>
		<form:hidden id="updateCurrentSearch_mode" path="mode" value="update-search"/>
		<form:hidden id="updateCurrentSearch_advanced" path="advanced"/>
		<form:hidden id="updateCurrentSearch_dao" path="dao"/>
		<form:hidden id="updateCurrentSearch_view" path="view"/>
		<form:hidden id="updateCurrentSearch_element" path="element"/>
		<form:hidden id="updateCurrentSearch_typedocument" path="typedocument"/>
		<form:hidden id="updateCurrentSearch_fromdate" path="fromdate"/>
		<form:hidden id="updateCurrentSearch_todate" path="todate"/>
		<form:hidden id="updateCurrentSearch_country" path="country"/>
		<form:hidden id="updateCurrentSearch_ai" path="ai"/>
		<form:hidden id="updateCurrentSearch_type" path="type"/>
		<form:hidden id="updateCurrentSearch_roledao" path="roledao"/>
		<form:hidden id="updateCurrentSearch_dateType" path="dateType"/>
		<form:hidden id="updateCurrentSearch_order" path="order"/>
		<form:hidden id="updateCurrentSearch_startdate" path="startdate"/>	
		<form:hidden id="updateCurrentSearch_enddate" path="enddate"/>
		
 		<c:forEach var="selectedNode" items="${advancedSearch.navigationTreeNodesSelected}">
			<input type="hidden" class="updateCurrentSearch_navigationTreeNodesSelected" name="navigationTreeNodesSelected" value="${selectedNode}"/>
		
		</c:forEach>
 		<c:forEach var="expandedNode" items="${advancedSearch.expandedNodes}">
			<input type="hidden" name="expandedNodes" value="${expandedNode}"/>
		
		</c:forEach>
	<c:choose>
		<c:when test="${advancedSearch.view == 'hierarchy'}">
			<jsp:include page="results-contexttab.jsp"/>
		</c:when>
		<c:otherwise>
			<jsp:include page="results-listtab.jsp"/>
		</c:otherwise>	
	</c:choose>

</form:form>	