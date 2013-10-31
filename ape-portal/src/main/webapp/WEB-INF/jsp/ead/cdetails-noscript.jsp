<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="ape"
	uri="http://commons.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="portal"
	uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<portal:friendlyUrl var="aiCodeUrl" type="directory-institution-code"/>
<portal:friendlyUrl var="eadDisplaySearchUrl" type="eaddisplay-search"/>
<portal:friendlyUrl var="eadDisplayDirectUrl" type="eaddisplay-frontpage"/>
<div id="eadDisplayPortlet">
	<h3 id="contextInformation">
		${localizedCountryName}
		&gt; <a href="${aiCodeUrl}/${archivalInstitution.repositorycode}">${archivalInstitution.ainame}</a>
	</h3>
<div>

	<c:if test="${!empty c}">
		<portal:eadNavigation clevel="${c}"/>
	</c:if>

</div>
<div id="eaddetailsContent">
<c:choose>
	<c:when test="${empty c}">
		<portal:ead type="frontpage" xml="${eadContent.xml}"  searchTerms="${term}" searchFieldsSelectionId="${element}"/>
	</c:when>
	<c:otherwise>
		<portal:ead type="cdetails" xml="${c.xml}" searchTerms="${term}" searchFieldsSelectionId="${element}" aiId="${aiId}"
			secondDisplayUrl="${eadDisplayDirectUrl}/${aiRepoCode}/fa" />
		<c:if test="${not c.leaf}">
			<div id="children" class="box">
				<div class="boxtitle">
					<div class="numberOfPages">
						<ape:pageDescription numberOfItems="${totalNumberOfChildren}" pageSize="${pageSize}" pageNumber="${pageNumber}" />
					</div>
					<div id="child-paging" class="paging">
						<ape:paging numberOfItems="${totalNumberOfChildren}" pageSize="${pageSize}" pageNumber="${pageNumber}"
							refreshUrl="javascript:updatePageNumber('${displayChildrenUrl}')" pageNumberId="pageNumber" />

					</div>
				</div>
				<portal:ead type="cdetails-child" xml="${childXml}" />
			</div>
		</c:if>
	</c:otherwise>
</c:choose>
</div>
</div>