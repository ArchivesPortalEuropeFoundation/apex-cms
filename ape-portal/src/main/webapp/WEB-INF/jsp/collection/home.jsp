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
<portlet:renderURL var="deleteMyCollectionUrl" >
	<portlet:param name="action" value="deleteSavedCollections" />
</portlet:renderURL>
<portlet:renderURL var="myCollections" />
<div id="mycollectionPortletDiv">
	<div id="mycollectionCreateAction" class="collectionBigAction">
		<a href="${createMyCollectionUrl}" >Create</a>
	</div>
	<c:choose>
		<c:when test="${not empty collections}">
			<div class="collectionPaginator">
				<div class="boxtitle">
					<div class="numberOfPages">
						<ape:pageDescription numberOfItems="${totalNumberOfResults}" pageSize="${pageSize}" pageNumber="${pageNumber}" />
					</div>
					<div id="child-paging" class="paging">
						<ape:paging numberOfItems="${totalNumberOfResults}" pageSize="${pageSize}" pageNumber="${pageNumber}"
							refreshUrl="${myCollections}" pageNumberId="pageNumber" />
					</div>
				</div>
			</div>
			<div class="collectionFieldHeader">
				<div class="collectionFieldTitle collectionHeader">Title</div>
				<div class="collectionFieldDescription collectionHeader">Description</div>
				<div class="collectionFieldPublic collectionHeader">Features</div>
				<div class="collectionFieldActions collectionHeader">Actions</div>
			</div>
			
			<c:forEach var="collection" items="${collections}" varStatus="counter">
				<div class="collectionField" id="collectionField_${counter}">
					<div class="collectionFieldTitle collectionContent"><c:out value="${collection.title}" /></div>
					<div class="collectionFieldDescription collectionContent">&nbsp;${collection.description}</div>
					<div class="collectionFieldPublic collectionContent">
					<c:choose>
						<c:when test="${collection.public_}">Public</c:when><c:otherwise>Private</c:otherwise>
					</c:choose>
						<%-- <input type="checkbox" id="collectionField_${counter}_public" <c:if test="${collection.public_}">checked="checked"</c:if> disabled="disabled" /> --%>
					</div>
					<div class="collectionFieldActions collectionContent">
						<div>
							<a href="${myCollectionUrl}&id=${collection.id}">View</a>
						</div>
						<div>
							<a href="${createMyCollectionUrl}&id=${collection.id}" >Edit</a>
						</div>
						<div>
							<a href="${deleteMyCollectionUrl}&id=${collection.id}" >Delete</a>
						</div>
					</div>
				</div>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<h2>There aren't collections</h2>
		</c:otherwise>
	</c:choose>
</div>