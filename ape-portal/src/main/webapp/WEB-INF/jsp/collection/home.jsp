<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%>
<%-- <portal:friendlyUrl var="myCollectionUrl" type="saved-collection-overview"/> --%>
<portlet:renderURL var="myCollectionUrl" >
	<portlet:param name="action" value="viewCollection" />
</portlet:renderURL>
<portlet:renderURL var="createMyCollectionUrl" >
	<portlet:param name="action" value="createNewCollection" />
</portlet:renderURL>
<portlet:actionURL var="deleteMyCollectionUrl" >
	<portlet:param name="action" value="deleteSavedCollections" />
</portlet:actionURL>

<portlet:renderURL var="myCollections" />

<div id="savedCollectionsPortlet">
	<div id="mycollectionCreateAction" class="collectionBigAction">
		<a href="${createMyCollectionUrl}" ><fmt:message key="collections.create"/></a>
	</div>
	<c:choose>
		<c:when test="${not empty collections}">
			<h2><fmt:message key="collections.collections"/></h2>
			<div id="children" class="box">
				<div class="boxtitle">
					<div class="numberOfPages">
						<ape:pageDescription numberOfItems="${totalNumberOfResults}" pageSize="${pageSize}" pageNumber="${pageNumber}" />
					</div>		
					<div id="child-paging" class="paging">
						<ape:paging numberOfItems="${totalNumberOfResults}" pageSize="${pageSize}" pageNumber="${pageNumber}" 
						refreshUrl="${myCollections}" pageNumberId="pageNumber" />
					</div>
				</div>
				<input id="orderToSet" type="hidden" value="${orderAsc}">
				<table id="savedCollectionsTable" class="defaultlayout"> 
					<tr id="crown">
						<portlet:resourceURL var="orderResultsUrl" id="orderResults">
						    <portlet:param name="pageNumber" value="${pageNumber}" />
						</portlet:resourceURL>
						<th id="title" class="name header" onclick="javascript:getval('${orderResultsUrl}',this.id)"><fmt:message key="advancedsearch.text.title2"/></th>
						<th id="description" class="description header" onclick="javascript:getval('${orderResultsUrl}',this.id)"><fmt:message key="savedsearch.description"/></th>
						<th id="public_collection" class="type header" onclick="javascript:getval('${orderResultsUrl}',this.id)"><fmt:message key="collections.features"/></th>
						<th id="modified_date" class="modified header" onclick="javascript:getval('${orderResultsUrl}',this.id)"><fmt:message key="savedsearch.modified"/></th>
						<th id="actions" class="actions"><fmt:message key="savedsearches.overview.actions"/></th>
					</tr>
					<c:forEach var="collection" items="${collections}" varStatus="counter">
						<tr class="${trClass}">
							<td><c:out value="${collection.title}" /></td>
							<td><c:out value="${collection.description}" /></td>
							<td>
								<c:choose>
									<c:when test="${collection.public_}"><fmt:message key="savedsearch.publicaccessible"/></c:when>
									<c:otherwise><fmt:message key="collections.private"/></c:otherwise>
								</c:choose>
							</td>
							<td>
								<fmt:formatDate pattern="dd-MMM-yyyy HH:mm z" value="${collection.modified_date}" timeZone="${timeZone}"/>
							</td>
							<td>
	 							<div>
									<a href="${myCollectionUrl}&id=${collection.id}"><fmt:message key="collections.view"/></a>
									<a href="${createMyCollectionUrl}&id=${collection.id}" ><fmt:message key="savedsearches.overview.edit"/></a>
									<a href="${deleteMyCollectionUrl}&id=${collection.id}" onclick="return confirm('<fmt:message key="collections.delete.areyousure"/>')"><fmt:message key="savedsearches.overview.delete"/></a>
								</div>
							</td>
						</tr>
					</c:forEach>
				</table>
			</div>
		</c:when>
		<c:otherwise>
			<h2><fmt:message key="collections.no"/></h2>
		</c:otherwise>
	</c:choose>
</div>