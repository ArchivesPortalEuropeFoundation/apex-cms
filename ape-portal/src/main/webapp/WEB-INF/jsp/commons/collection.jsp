<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%>
<!-- <link rel="stylesheet" href="//code.jquery.com/ui/1.11.2/themes/smoothness/jquery-ui.css">
<script src="//code.jquery.com/jquery-1.11.2.js"></script>
<script src="//code.jquery.com/ui/1.11.2/jquery-ui.js"></script> -->

<script>
$(document).ready(function() {
	loadDialogSave();
});
</script>

<div id="mycollectionPortletDiv" class="hidden" title="<fmt:message key="collections.create"/>">
	<div class="collectionFieldDiv" id="collectionFieldDiv">
		<c:if test="${!loggedIn}">
			<div class="error">
				<fmt:message key="bookmarks.logged.ko"/>
			</div>
		</c:if>
		<c:if test="${loggedIn}">
			<form id="frmCollectionContent" name="frmCollectionContent" method="post" action="${createNewCollection}" onsubmit="return prevSubmit();">
				<table class="defaultlayout textSize">
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
						<td colspan="2">
						<div class="hidden" id="errorName" name="errorName">
								<fmt:message key="advancedsearch.text.pleaseputatitle"/>
							</div>
						</td>
					</tr>
					<tr>
						<th><fmt:message key="savedsearch.description"/></th> 
						<td><textarea id="collectionDescription" name="collectionDescription" class="collectionDescriptionTextarea" <c:if test="${!edit}">disabled="disabled"</c:if>>${collection.description}</textarea></td>
					</tr>


					<!-- Issue #1781: Collections: Public/Edit fields. -->
					<!-- Commented the display of the two fields. Uncomment
						 when the fields should be used again. -->
<!-- 					<c:if test="${edit}">
						<tr>	
							<th><fmt:message key="savedsearch.publicaccessible"/></th>
							<td>
								<input type="checkbox" id="collectionField_public" name="collectionField_public" 
									<c:if test="${!edit}">disabled="disabled"</c:if>
								/>
							</td>				
						</tr>
						<tr>				
							<th><fmt:message key="savedsearches.overview.edit"/></th>
							<td>
								<input type="checkbox" id="collectionField_edit" name="collectionField_edit" 
									<c:if test="${!edit}">disabled="disabled"</c:if>
								/>
							</td>
						</tr>
					</c:if> -->
				</table>
				<hr>
				<portlet:resourceURL var="CollectionUrl" id="saveNewCollection"/>
				<div id="searchButton" class="linkButton" style="display: inline-flex">
		 			<a href="javascript:saveCollections('${bookmarkId}','${CollectionUrl}')"><fmt:message key="collections.create.new" /></a>
				</div>
			</form>
		</c:if>
	</div>
</div>