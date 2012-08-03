<%@ tag  language="java" pageEncoding="UTF-8" body-content="empty"%>
<%@ attribute name="facetFields" required="true" type="java.lang.Object" rtexprvalue="true"%>
<%@ attribute name="facetName"  required="true"   %>
<%@ attribute name="currentValue"  required="false"  rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="hasId"  required="true"%>
<%@ attribute name="keyPrefix"  required="false"%>
<%@ attribute name="valueIsKey"  required="true"%>
<%@ attribute name="titleKey"  required="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="searchresults" uri="http://portal.archivesportaleurope.eu/tags/searchresults"%>
<c:if test="${!empty facetFields}">
	<c:forEach var="facetField" items="${facetFields}" >
		<c:if test="${facetField.name eq facetName and fn:length(facetField.values) > 1}">
			<div id="facet_${facetName}" class="box">
	    		<div class="boxtitle"><fmt:message key="${titleKey}"/></div>
	    		<ul>
					<c:forEach var="count" items="${facetField.values}" varStatus="varStatus">
						<c:choose>
							<c:when test="${varStatus.count lt 11}">
								<li><searchresults:facet hasId="${hasId}" name="${facetField.name}" value="${count.name}" currentValue="${currentValue}" keyPrefix="${keyPrefix}" valueIsKey="${valueIsKey}"/><span class='numberOfHits'>(${count.count})</span></li>
							</c:when>
							<c:otherwise>
								<li><a href="#" more-params="facetField=${facetName}&facetOffset=10&hasId=${hasId}&keyPrefix=${keyPrefix}&valueIsKey=${valueIsKey}" class="facet-more"><fmt:message key="advancedsearch.context.more" /></a></li>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</div>
				</ul>	
		</c:if>
	</c:forEach>
</c:if>