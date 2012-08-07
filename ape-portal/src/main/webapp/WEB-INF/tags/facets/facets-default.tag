<%@ tag language="java" pageEncoding="UTF-8" body-content="empty"%>
<%@ attribute name="facetContainers" required="true" type="java.lang.Object" rtexprvalue="true"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="searchresults" uri="http://portal.archivesportaleurope.eu/tags/searchresults"%>
<c:forEach var="facetContainer" items="${facetContainers}">
	<div id="facet_${facetContainer.name}" class="box">
		<c:choose>
			<c:when test="${facetContainer.expanded}">
				<c:set var="collapsibleIconClass" value="expanded"/>
				<c:set var="collapsibleBox" value=""/>
			</c:when>
			<c:otherwise>
				<c:set var="collapsibleIconClass" value="collapsed"/>
				<c:set var="collapsibleBox" value="hidden"/>
			</c:otherwise>
		</c:choose>
		<div class="boxtitle"><span class="collapsibleIcon ${collapsibleIconClass}"/>
			<fmt:message key="advancedsearch.facet.title.${fn:toLowerCase(facetContainer.name)}" />
		</div>
		<ul class="${collapsibleBox}">
			<c:if test="${facetContainer.multiSelect}">
				<c:set var="cssClassPrefix" value="refinement_multiple" />
			</c:if>
			<c:forEach var="facetValue" items="${facetContainer.values}" varStatus="varStatus">
				<li><c:choose>
						<c:when test="${varStatus.last and varStatus.index == (facetContainer.limit-1)}">
							<a href="javascript:addMoreFacets('${facetContainer.name}')" class="facet-more"><fmt:message
									key="advancedsearch.context.more" /></a>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${facetValue.selected}">
									<a class="${cssClassPrefix}_selected" title="${facetValue.htmlLongDescription}"
										href="javascript:removeRefinement('${facetContainer.name}','${facetValue.id}');">${facetValue.htmlShortDescription}</a>
								</c:when>
								<c:otherwise>
									<a class="${cssClassPrefix}_notselected" title="${facetValue.htmlLongDescription}"
										href="javascript:addRefinement('${facetContainer.name}','${facetValue.id}','${facetValue.javascriptLongDescription}','${facetValue.javascriptLongDescription}');">${facetValue.htmlShortDescription}</a>
								</c:otherwise>
							</c:choose>
							<span class='numberOfHits'>(${facetValue.numberOfResults})</span>
						</c:otherwise>
					</c:choose></li>

			</c:forEach>
		</ul>
	</div>
</c:forEach>
