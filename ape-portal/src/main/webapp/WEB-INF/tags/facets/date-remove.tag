<%@ tag  language="java" pageEncoding="UTF-8" body-content="empty"%>
<%@ attribute name="solrResponse" required="true" type="java.lang.Object" rtexprvalue="true"%>
<%@ attribute name="facetName"  required="true"   %>
<%@ attribute name="currentValue"  required="true"  rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="titleKey"  required="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="searchresults" uri="http://portal.archivesportaleurope.eu/tags/searchresults"%>
<%@ taglib prefix="url" uri="http://commons.archivesportaleurope.eu/tags/url"%>
<c:if test="${!empty solrResponse.facetDates and !empty currentValue}">
	<c:forEach var="facetField" items="${solrResponse.facetDates}">
		<c:if test="${facetField.name eq facetName}">
			<li><fmt:message key="${titleKey}"/><searchresults:date-remove currentValue="${currentValue}" name="${facetName}"  gap="${facetField.gap}"><span class="close-icon"></span></searchresults:date-remove></li>
		</c:if>
	</c:forEach>
</c:if>
