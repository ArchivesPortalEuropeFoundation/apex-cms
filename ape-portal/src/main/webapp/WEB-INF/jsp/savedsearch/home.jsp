<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%>
<c:choose>
	<c:when test="${not empty eadSavedSearches}">
		<div id="children" class="box">
			<div class="boxtitle">
				<div class="numberOfPages">
					<ape:pageDescription numberOfItems="${totalNumberOfResults}" pageSize="${pageSize}" pageNumber="${pageNumber}" />
				</div>
				<div id="child-paging" class="paging">
					<ape:paging numberOfItems="${totalNumberOfResults}" pageSize="${pageSize}" pageNumber="${pageNumber}"
						refreshUrl="test/{pageNumber}" pageNumberId="pageNumber"  liferayFriendlyUrl="true"/>

				</div>
			</div>


			<table>
				<tr>
					<th><fmt:message key="savedsearches.overview.label"/></th><th><fmt:message key="savedsearches.overview.modified"/></th><th><fmt:message key="savedsearches.overview.publicaccessible"/></th><th><fmt:message key="savedsearches.overview.term"/></th>
				</tr>
				<c:forEach var="eadSavedSearch" items="${eadSavedSearches}">
				<tr>
					<td><c:out value="${eadSavedSearch.label}"/></td>
					<td>${eadSavedSearch.modifiedDate}</td>
					<td>${eadSavedSearch.publicSearch}</td>
					<td><c:out value="${eadSavedSearch.term}"/></td>
					</tr>
				</c:forEach>
			</table>
		</div>

	</c:when>
	<c:otherwise>
		<h2><fmt:message key="savedsearches.overview.nothing"/></h2>
	</c:otherwise>
</c:choose>