<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%>
<portal:friendlyUrl var="savedSearchUrl" type="saved-search"/>
<portal:friendlyUrl var="savedSearchPublicUrl" type="saved-search" noHttps="true"/>
<portlet:actionURL var="createNewCollection" >
	<portlet:param name="action" value="saveNewCollection" />
</portlet:actionURL>
<portlet:actionURL var="updateCollection" >
	<portlet:param name="action" value="saveEditCollection" />
</portlet:actionURL>
<div id="mycollectionPortletDiv">
	<div class="collectionFieldDiv" id="collectionFieldDiv">
		<c:choose>
			<c:when test="${collection!=null}">
				<form id="frmCollectionContent" name="frmCollectionContent" method="post" action="${updateCollection}" onsubmit="return prevSubmit();">
				<input type="hidden" name="id" id="id" value="${collection.id}" />
			</c:when>
			<c:otherwise>
				<form id="frmCollectionContent" name="frmCollectionContent" method="post" action="${createNewCollection}" onsubmit="return prevSubmit();">
			</c:otherwise>
		</c:choose>
		
		
		
	<table class="defaultlayout">
		<tr>
			<th><fmt:message key="advancedsearch.text.title2"/></th> 
			<td>
				<input type="text" id="collectionTitle" name="collectionTitle" 
					<c:if test="${!edit}">disabled="disabled"</c:if>
					<c:if test="${collection!=null && collection.title!=null}">value="${collection.title}"</c:if> 
				/>
			</td>
		</tr>
		<tr>
			<th><fmt:message key="savedsearch.description"/></th> 
			<td><textarea id="collectionDescription" name="collectionDescription" class="collectionDescriptionTextarea" <c:if test="${!edit}">disabled="disabled"</c:if>>${collection.description}</textarea></td>
		</tr>
		<c:if test="${edit}">
			<tr>	
				<th><fmt:message key="savedsearch.publicaccessible"/></th>
				<td><input type="checkbox" id="collectionField_public" name="collectionField_public" <c:if test="${collection!=null && collection.public_}">checked="checked"</c:if> <c:if test="${!edit}">disabled="disabled"</c:if> /></td>				
			</tr>
			<tr>				
				<th><fmt:message key="savedsearches.overview.edit"/></th>
				<td><input type="checkbox" id="collectionField_edit" name="collectionField_edit" <c:if test="${!edit}">disabled="disabled"</c:if> <c:if test="${collection!=null && collection.edit}">checked="checked"</c:if> /></td>
			</tr>
		</c:if>
	</table>
		
		
		
<!-- 		<div class="collectionSearchFieldHeader"> -->
<%-- 			<div class="collectionFieldTitle"><fmt:message key="advancedsearch.text.title2"/></div>  --%>
<%-- 			<div class="collectionFieldDescription"><fmt:message key="savedsearch.description"/></div>  --%>
<%-- 			<c:if test="${edit}"> --%>
<%-- 				<div class="collectionFieldPublic2"><fmt:message key="savedsearch.publicaccessible"/></div> --%>
<%-- 				<div class="collectionFieldEdit2"><fmt:message key="savedsearches.overview.edit"/></div> --%>
<%-- 			</c:if> --%>
<!-- 		</div> -->
<!-- 		<div class="collectionSearchFieldContent"> -->
<!-- 			<div class="collectionFieldTitle"> -->
<!-- 				<input type="text" id="collectionTitle" name="collectionTitle"  -->
<%-- 					<c:if test="${!edit}">disabled="disabled"</c:if> --%>
<%-- 					<c:if test="${collection!=null && collection.title!=null}">value="${collection.title}"</c:if>  --%>
<!-- 				/> -->
<!-- 			</div> -->
<!-- 			<div class="collectionFieldDescription"> -->
<%-- 				<textarea id="collectionDescription" name="collectionDescription" class="collectionDescriptionTextarea" <c:if test="${!edit}">disabled="disabled"</c:if>>${collection.description}</textarea> --%>
<!-- 			</div> -->
<%-- 			<c:if test="${edit}"> --%>
<!-- 			<div class="collectionFieldPublic2"> -->
<%-- 				<input type="checkbox" id="collectionField_public" name="collectionField_public" <c:if test="${collection!=null && collection.public_}">checked="checked"</c:if> <c:if test="${!edit}">disabled="disabled"</c:if> /> --%>
<!-- 			</div> -->
<!-- 			<div class="collectionFieldEdit2"> -->
<%-- 				<input type="checkbox" id="collectionField_edit" name="collectionField_edit" <c:if test="${!edit}">disabled="disabled"</c:if> <c:if test="${collection!=null && collection.edit}">checked="checked"</c:if> /> --%>
<!-- 			</div> -->
<%-- 			</c:if> --%>
<!-- 		</div> -->
		
		
		
		
		
		<div class="collectionSearchField" id="collectionSearchFields"></div>
		<div id="newCollectionSearches"> </div>
		<div id="bookmarksDiv"> </div>
		<div id="newBookmarksDiv"> </div>
			
		<c:if test="${cSearches!=null}">
			<c:forEach var="currentSearch" items="${cSearches}">
				<input type="hidden" name="hidden_selected_search_${currentSearch.id}" id="hidden_selected_search_${currentSearch.id}" value="on" />
			</c:forEach>
		</c:if>
		
		<c:if test="${cBookmarks!=null}">
			<c:forEach var="currentBookmark" items="${cBookmarks}">
				<input type="hidden" name="hidden_collection_bookmark_${currentBookmark.id}" id="hidden_collection_bookmark_${currentBookmark.id}" value="on" />
			</c:forEach>
		</c:if>
		
		<c:if test="${edit}">
			<div id="collectionFieldSubmit" class="collectionFieldSubmit">
				<input type="submit" value="<fmt:message key="savedsearch.save"/>" />
			</div>
		</c:if>
		</form>
		<c:choose>
			<c:when test="${edit}">
			
			<portlet:resourceURL var="getNewBookmarksUrl" id="getNewBookmarks">
			    <portlet:param name="pageNumber" value="1" />
			</portlet:resourceURL>
			<portlet:resourceURL var="getNewSearchesUrl" id="getNewSearches">
			    <portlet:param name="pageNumber" value="1" />
			</portlet:resourceURL>
			<portlet:resourceURL var="getBookmarksUrl" id="getBookmarks">
			    <portlet:param name="pageNumber" value="1" />
			    <portlet:param name="edit" value="true" />
			</portlet:resourceURL>
			<portlet:resourceURL var="getSearchesUrl" id="getSearches">
			    <portlet:param name="pageNumber" value="1" />
			    <portlet:param name="edit" value="true" />
			</portlet:resourceURL>
			<div id="getHiddenMessages" style="display:none;">
				<div id="hiddenEmptyTitleLabel"><fmt:message key="advancedsearch.text.pleaseputatitle"/></div>
			</div>
			<script type="text/javascript">
				$(document).ready(function(){
					getEditForm("${getNewBookmarksUrl}","${getNewSearchesUrl}","${getBookmarksUrl}","${getSearchesUrl}");
				});
			</script>
			</c:when>
			<c:otherwise>
				<portlet:resourceURL var="getBookmarksUrl" id="getBookmarks">
				    <portlet:param name="pageNumber" value="1" />
				    <portlet:param name="edit" value="false" />
				</portlet:resourceURL>
				<portlet:resourceURL var="getSearchesUrl" id="getSearches">
				    <portlet:param name="pageNumber" value="1" />
				    <portlet:param name="edit" value="false" />
				</portlet:resourceURL>
				<script type="text/javascript">
					$(document).ready(function(){
						getViewForm("${getBookmarksUrl}","${getSearchesUrl}",false);
					});
				</script>
			</c:otherwise>
		</c:choose>
	</div>
</div>