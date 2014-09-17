<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%>
<portal:friendlyUrl var="savedSearchUrl" type="saved-search"/>
<portal:friendlyUrl var="savedSearchPublicUrl" type="saved-search" noHttps="true"/>
<portlet:renderURL var="createNewCollection" >
	<portlet:param name="action" value="saveNewCollection" />
</portlet:renderURL>
<portlet:renderURL var="updateCollection" >
	<portlet:param name="action" value="saveEditCollection" />
</portlet:renderURL>
<div id="mycollectionPortletDiv">
	<div class="collectionFieldDiv" id="collectionFieldDiv">
		<c:choose>
			<c:when test="${collection!=null}">
				<form name="frm" method="post" action="${updateCollection}">
				<input type="hidden" name="id" id="id" value="${collection.id}" />
			</c:when>
			<c:otherwise>
				<form name="frm" method="post" action="${createNewCollection}">
			</c:otherwise>
		</c:choose>
			<div class="collectionSearchFieldHeader">
				<div class="collectionFieldTitle"><fmt:message key="advancedsearch.text.title2"/></div> 
				<div class="collectionFieldDescription"><fmt:message key="savedsearch.description"/></div> 
				<c:if test="${edit}">
				<div class="collectionFieldPublic2"><fmt:message key="savedsearch.publicaccessible"/></div>
				<div class="collectionFieldEdit2"><fmt:message key="savedsearches.overview.edit"/></div>
				</c:if>
			</div>
			<div class="collectionSearchFieldContent">
				<div class="collectionFieldTitle">
					<input type="text" id="collectionTitle" name="collectionTitle" 
						<c:if test="${!edit}">disabled="disabled"</c:if>
						<c:if test="${collection!=null && collection.title!=null}">value="${collection.title}"</c:if> 
					/>
				</div>
				<div class="collectionFieldDescription">
					<textarea id="collectionDescription" name="collectionDescription" class="collectionDescriptionTextarea" <c:if test="${!edit}">disabled="disabled"</c:if>>${collection.description}</textarea>
				</div>
				<c:if test="${edit}">
				<div class="collectionFieldPublic2">
					<input type="checkbox" id="collectionField_public" name="collectionField_public" <c:if test="${collection!=null && collection.public_}">checked="checked"</c:if> <c:if test="${!edit}">disabled="disabled"</c:if> />
				</div>
				<div class="collectionFieldEdit2">
					<input type="checkbox" id="collectionField_edit" name="collectionField_edit" <c:if test="${!edit}">disabled="disabled"</c:if> <c:if test="${collection!=null && collection.edit}">checked="checked"</c:if> />
				</div>
				</c:if>
			</div>
			<div class="collectionSearchField">
				<c:if test="${collectionSearches!=null && collectionSearches.size() > 0}">
				<div>Saved searches:</div>
				<div class="collectionSearchesHeader" id="collectionSearchesHeader">
					<c:if test="${edit}">
						<div class="collectionSearchCheckbox"><fmt:message key="collections.signal"/></div>
					</c:if>
					<div class="collectionSearchId"><fmt:message key="savedsearch.id"/></div>
					<div class="<c:choose><c:when test="${edit}">collectionSearchTerms</c:when><c:otherwise>collectionSearchTerms2</c:otherwise></c:choose>"><fmt:message key="collections.terms"/></div>
					<div class="<c:choose><c:when test="${edit}">collectionSearchDate</c:when><c:otherwise>collectionSearchDate2</c:otherwise></c:choose>"><fmt:message key="advancedsearch.text.date"/></div>
				</div>
				<div id="collectionSearches" >
				<c:forEach var="collectionSearch" items="${collectionSearches}">
					<div class="collectionSearches" id="collectionSearch_<c:out value="${collectionSearch.id}" />">
						<c:if test="${edit}">
							<div class="collectionSearchCheckbox"><input type="checkbox" name="selected_search_${collectionSearch.id}" id="selected_search_${collectionSearch.id}" checked="checked" /></div>
						</c:if>
						<div class="collectionSearchId"><c:out value="${collectionSearch.eadSavedSearch.id}" /></div>
						<div class="<c:choose><c:when test="${edit}">collectionSearchTerms</c:when><c:otherwise>collectionSearchTerms2</c:otherwise></c:choose>"><c:out value="${collectionSearch.eadSavedSearch.searchTerm}" /></div>
						<div class="<c:choose><c:when test="${edit}">collectionSearchDate</c:when><c:otherwise>collectionSearchDate2</c:otherwise></c:choose>"><c:out value="${collectionSearch.eadSavedSearch.modifiedDate}" /></div>
						<c:if test="${!edit}">
						<div class="collectionSearchAction">
							<c:choose>
								<c:when test="${collectionSearch.eadSavedSearch.publicSearch}">
									<div>
										<a href="${savedSearchPublicUrl}/${collectionSearch.eadSavedSearch.id}" > <fmt:message key="savedsearches.overview.viewresults"/></a>
									</div>
									<div>
										<a href="${savedSearchPublicUrl}/${collectionSearch.eadSavedSearch.id}/new" ><fmt:message key="savedsearches.overview.viewresults.new"/></a>
									</div>
								</c:when>
								<c:otherwise>
									<div>
										<a href="${savedSearchUrl}/${collectionSearch.eadSavedSearch.id}"><fmt:message key="savedsearches.overview.viewresults"/></a>
									</div>
									<div>
										<a href="${savedSearchUrl}/${collectionSearch.eadSavedSearch.id}/new"><fmt:message key="savedsearches.overview.viewresults.new"/></a>
									</div>
								</c:otherwise>
							</c:choose>
						</div>
						</c:if>
					</div>
				</c:forEach>
				</div>
				</c:if>
			</div>
			<c:if test="${newSearches!=null && newSearches.size()>0}">
			<div id="newCollectionSearches">
				<div><fmt:message key="collections.searches.out"/></div>
				<div class="collectionSearchesHeader" id="collectionSearchesHeader">
					<c:if test="${edit}">
						<div class="collectionSearchCheckbox"><fmt:message key="collections.signal"/></div>
					</c:if>
					<div class="collectionSearchId"><fmt:message key="savedsearch.id"/></div>
					<div class="<c:choose><c:when test="${edit}">collectionSearchTerms</c:when><c:otherwise>collectionSearchTerms2</c:otherwise></c:choose>"><fmt:message key="collections.terms"/></div>
					<div class="<c:choose><c:when test="${edit}">collectionSearchDate</c:when><c:otherwise>collectionSearchDate2</c:otherwise></c:choose>"><fmt:message key="advancedsearch.text.date"/></div>
				</div>
				<c:forEach var="newSearch" items="${newSearches}">
					<div class="collectionSearches" id="newCollectionSearch_<c:out value="${newSearch.id}" />">
						<c:if test="${edit}">
							<div class="collectionSearchCheckbox"><input type="checkbox" name="new_search_${newSearch.id}" id="new_search_${newSearch.id}" /></div>
						</c:if>
						<div class="collectionSearchId"><c:out value="${newSearch.id}" /></div>
						<div class="<c:choose><c:when test="${edit}">collectionSearchTerms</c:when><c:otherwise>collectionSearchTerms2</c:otherwise></c:choose>"><c:out value="${newSearch.searchTerm}" /></div>
						<div class="<c:choose><c:when test="${edit}">collectionSearchDate</c:when><c:otherwise>collectionSearchDate2</c:otherwise></c:choose>"><c:out value="${newSearch.modifiedDate}" /></div>
						<c:if test="${!edit}">
						<div class="collectionSearchAction">
							<c:choose>
								<c:when test="${newSearch.publicSearch}">
									<div>
										<a href="${savedSearchPublicUrl}/${newSearch.id}" > <fmt:message key="savedsearches.overview.viewresults"/></a>
									</div>
									<div>
										<a href="${savedSearchPublicUrl}/${newSearch.id}/new" ><fmt:message key="savedsearches.overview.viewresults.new"/></a>
									</div>
								</c:when>
								<c:otherwise>
									<div>
										<a href="${savedSearchUrl}/${newSearch.id}"><fmt:message key="savedsearches.overview.viewresults"/></a>
									</div>
									<div>
										<a href="${savedSearchUrl}/${collectionSearch.eadSavedSearch.id}/new"><fmt:message key="savedsearches.overview.viewresults.new"/></a>
									</div>
								</c:otherwise>
							</c:choose>
						</div>
						</c:if>
					</div>
				</c:forEach>
			</div>
			</c:if>
			<c:if test="${bookmarks!=null && bookmarks.size()>0}">
				<div><fmt:message key="collections.bookmarks.saved"/></div>
				<div class="collectionSearchesHeader" id="collectionBookmarksHeader">
					<c:if test="${edit}">
						<div class="collectionSearchCheckbox"><fmt:message key="collections.signal"/></div>
					</c:if>
					<div class="collectionSearchId"><fmt:message key="savedsearch.id"/></div>
					<div class="<c:choose><c:when test="${edit}">collectionSearchTerms</c:when><c:otherwise>collectionSearchTerms2</c:otherwise></c:choose>">Name</div>
					<div class="<c:choose><c:when test="${edit}">collectionSearchDate</c:when><c:otherwise>collectionSearchDate2</c:otherwise></c:choose>">Description</div>
				</div>
				<c:forEach var="collectionBookmark" items="${bookmarks}">
					<div class="collectionSearches" id="newCollectionBookmarks_<c:out value="${collectionBookmark.id}" />">
						<c:if test="${edit}">
							<div class="collectionSearchCheckbox"><input type="checkbox" name="collection_bookmark_${collectionBookmark.id}" id="collection_bookmark_${collectionBookmark.id}" checked="checked" /></div>
						</c:if>
						<div class="collectionSearchId"><c:out value="${collectionBookmark.savedBookmarks.id}" /></div>
						<div class="<c:choose><c:when test="${edit}">collectionSearchTerms</c:when><c:otherwise>collectionSearchTerms2</c:otherwise></c:choose>"><c:out value="${collectionBookmark.savedBookmarks.name}" /></div>
						<div class="<c:choose><c:when test="${edit}">collectionSearchDate</c:when><c:otherwise>collectionSearchDate2</c:otherwise></c:choose>"><c:out value="${collectionBookmark.savedBookmarks.description}" /></div>
						<c:if test="${!edit}">
						<div class="collectionSearchAction">
							<a href="${collectionBookmark.savedBookmarks.link}"><fmt:message key="collections.bookmarks.see"/></a>
						</div>
						</c:if>
					</div>
				</c:forEach>
			</c:if>
			<c:if test="${newBookmarks!=null && newBookmarks.size()>0}">
				<div><fmt:message key="collections.bookmarks.out"/></div>
				<div class="collectionSearchesHeader" id="collectionBookmarksHeader">
					<c:if test="${edit}">
						<div class="collectionSearchCheckbox"><fmt:message key="collections.signal"/></div>
					</c:if>
					<div class="collectionSearchId"><fmt:message key="savedsearch.id"/></div>
					<div class="<c:choose><c:when test="${edit}">collectionSearchTerms</c:when><c:otherwise>collectionSearchTerms2</c:otherwise></c:choose>"><fmt:message key="advancedsearch.eaccpf.element.name"/></div>
					<div class="<c:choose><c:when test="${edit}">collectionSearchDate</c:when><c:otherwise>collectionSearchDate2</c:otherwise></c:choose>"><fmt:message key="savedsearch.description"/></div>
				</div>
				<c:forEach var="collectionBookmark" items="${newBookmarks}">
					<div class="collectionSearches" id="newCollectionBookmarks_<c:out value="${collectionBookmark.id}" />">
						<c:if test="${edit}">
							<div class="collectionSearchCheckbox"><input type="checkbox" name="new_bookmark_${collectionBookmark.id}" id="new_bookmark_${collectionBookmark.id}" /></div>
						</c:if>
						<div class="collectionSearchId"><c:out value="${collectionBookmark.id}" /></div>
						<div class="<c:choose><c:when test="${edit}">collectionSearchTerms</c:when><c:otherwise>collectionSearchTerms2</c:otherwise></c:choose>"><c:out value="${collectionBookmark.name}" /></div>
						<div class="<c:choose><c:when test="${edit}">collectionSearchDate</c:when><c:otherwise>collectionSearchDate2</c:otherwise></c:choose>"><c:out value="${collectionBookmark.description}" /></div>
						<c:if test="${!edit}">
						<div class="collectionSearchAction">
							<a href="${collectionBookmark.link}"><fmt:message key="collections.bookmarks.see"/></a>
						</div>
						</c:if>
					</div>
				</c:forEach>
			</c:if>
			<c:if test="${edit}">
				<div class="collectionFieldSubmit">
					<input type="submit" value="<fmt:message key="savedsearch.save"/>" />
				</div>
			</c:if>
		</form>
	</div>
</div>