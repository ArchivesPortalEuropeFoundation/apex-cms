<%@ tag  language="java" pageEncoding="UTF-8" body-content="empty"%>
<%@ attribute name="solrResponse" required="true" type="java.lang.Object" rtexprvalue="true"%>
<%@ attribute name="facetName"  required="true"   %>
<%@ attribute name="currentValue"  required="true"  rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="hasId"  required="true"%>
<%@ attribute name="keyPrefix"  required="false"%>
<%@ attribute name="valueIsKey"  required="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="searchresults" uri="http://portal.archivesportaleurope.eu/tags/searchresults"%>
<c:if test="${!empty solrResponse.facetFields && !empty currentValue}">
		<c:forEach var="facetField" items="${solrResponse.facetFields}">
			<c:if test="${facetField.name eq facetName}">
				<c:forTokens var="currentItem" items="${currentValue}" delims=",">
					<li>${currentItem}</li>
				</c:forTokens>
			</c:if>
		</c:forEach>
</c:if>
