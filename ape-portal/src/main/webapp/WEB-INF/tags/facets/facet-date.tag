<%@ tag  language="java" pageEncoding="UTF-8" body-content="empty"%>
<%@ attribute name="facetFields" required="true" type="java.lang.Object" rtexprvalue="true"%>
<%@ attribute name="facetName"  required="true"   %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="searchresults" uri="http://portal.archivesportaleurope.eu/tags/searchresults"%>
<%@ attribute name="titleKey"  required="true"%>
<c:if test="${!emptyfacetFields}">
	<c:forEach var="facetField" items="${facetFields}">
		<c:if test="${facetField.name eq facetName and fn:length(facetField.values) > 1}">
			<div class="box">
	    		<div class="boxtitle"><fmt:message key="${titleKey}"/></div>
	    			<ul>
					<c:forEach var="count" items="${facetField.values}">
						<c:if test="${count.count > 0}">
							<li><searchresults:date name="${facetField.name}" value="${count.name}" gap="${facetField.gap}"/><span class='numberOfHits'>(${count.count})</span></li>
						</c:if>
					</c:forEach>
					</ul>
				</div>	
		</c:if>
	</c:forEach>
</c:if>
