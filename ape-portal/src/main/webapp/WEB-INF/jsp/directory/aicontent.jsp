<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%>
<portlet:defineObjects />
<portal:friendlyUrl var="contentUrl" type="directory-content" />
<portal:friendlyUrl var="eadUrl" type="eaddisplay-frontpage" />
<portal:friendlyUrl var="aiCodeUrl" type="directory-institution-code"/>
<div id="directoryPortlet">
	<div class="${mobile}">
		<h2><a href="${aiCodeUrl}/${archivalInstitution.encodedRepositorycode}">${archivalInstitution.ainame}</a> &gt; <fmt:message key="advancedsearch.text.${xmlTypeName}"/></h2>
		<div id="children" class="box">
			<div class="boxtitle">
				<div class="numberOfPages">
					<ape:pageDescription numberOfItems="${totalNumberOfResults}" pageSize="${pageSize}" pageNumber="${pageNumber}" />
				</div>
				<div id="child-paging" class="paging">
					<ape:paging numberOfItems="${totalNumberOfResults}" pageSize="${pageSize}" pageNumber="${pageNumber}"
						refreshUrl="${contentUrl}/${aiRepoCode}/${xmlTypeName}/{pageNumber}" pageNumberId="pageNumber"  liferayFriendlyUrl="true"/>

				</div>
			</div>


			<table>
				<c:forEach var="ead" items="${eads}">
					<tr class="child">
						<portal:eadPersistentLink repoCode="${aiRepoCode}" xmlTypeName="${xmlTypeName}" eadid="${ead.encodedIdentifier}" var="link"/>
						<td><a href="${link}">${ead.title}</a></td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
</div>
