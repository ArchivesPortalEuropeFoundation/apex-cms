<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<portal:friendlyUrl var="aiCodeUrl" type="directory-institution-code" />
<portal:friendlyUrl var="eadDisplaySearchUrl" type="eaddisplay-search" />
<portal:friendlyUrl var="eadDisplayDirectUrl" type="eaddisplay-frontpage" />
<portal:friendlyUrl var="eadDisplaySearchPagingUrl" type="eaddisplay-search-paging" />
<portal:friendlyUrl var="eadDisplayDirectPagingUrl" type="eaddisplay-frontpage-paging" />

<c:set var="element">
	<c:out value="${param['element']}" />
</c:set>
<c:set var="term">
	<c:out value="${param['term']}" />
</c:set>
<div id="eadDisplayPortlet">
	<div class="mobile">
		<h3 id="contextInformation">
			${localizedCountryName} &gt; <a href="${aiCodeUrl}/${archivalInstitution.encodedRepositorycode}">${archivalInstitution.ainame}</a>
		</h3>
		<div>

			<c:if test="${!empty c}">
				<portal:eadNavigation clevel="${c}" />
			</c:if>

		</div>
		<div id="eadcontent">
			<div id="eaddetailsContent">
				<c:choose>
					<c:when test="${empty c}">
						<portal:ead type="frontpage" xml="${eadContent.xml}" searchTerms="${term}" searchFieldsSelectionId="${element}" />
						<portal:eadPersistentLink var="pagingUrl" repoCode="${archivalInstitution.encodedRepositorycode}" xmlTypeName="${xmlTypeName}" eadid="${eadid}"  pageNumber="{pageNumber}" searchFieldsSelectionId="${element}" searchTerms="${term}"/>
					</c:when>
					<c:otherwise>
						<portal:eadPersistentLink var="pagingUrl" repoCode="${archivalInstitution.encodedRepositorycode}" xmlTypeName="${xmlTypeName}" eadid="${eadid}"  pageNumber="{pageNumber}" unitid="${c.unitid}" searchId="${c.clId}" searchFieldsSelectionId="${element}" searchTerms="${term}"/>
						<portal:ead type="cdetails" xml="${c.xml}" searchTerms="${term}" searchFieldsSelectionId="${element}"
							aiId="${archivalInstitution.aiId}" secondDisplayUrl="${eadDisplayDirectUrl}/${archivalInstitution.encodedRepositorycode}/fa" />

					</c:otherwise>
				</c:choose>
				<c:if test="${!empty children}">
					<div id="children" class="box">
						<div class="boxtitle">
							<div class="numberOfPages">
								<ape:pageDescription numberOfItems="${totalNumberOfChildren}" pageSize="${pageSize}" pageNumber="${pageNumber}" />
							</div>
							<div id="child-paging" class="paging">
								<ape:paging numberOfItems="${totalNumberOfChildren}" pageSize="${pageSize}" pageNumber="${pageNumber}"
									refreshUrl="${pagingUrl}" pageNumberId="pageNumber" liferayFriendlyUrl="true" />

							</div>
						</div>
						<table class="fullwidth">
							<c:forEach var="child" items="${children}">
							<portal:eadPersistentLink var="childLink" repoCode="${archivalInstitution.encodedRepositorycode}" xmlTypeName="${xmlTypeName}" eadid="${eadid}" unitid="${child.unitid}" searchId="${child.clId}" searchFieldsSelectionId="${element}" searchTerms="${term}"/>
								<tr class="child childHeader">
									<td>${child.unitid }</td>
									<td><a href="${childLink}">${child.unittitle}</a></td>
								</tr>
							</c:forEach>
						</table>
					</div>
				</c:if>
			</div>
		</div>
	</div>
</div>