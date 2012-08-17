<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%> 
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<c:set var="element"><c:out value="${param['element']}" /></c:set>
<c:set var="term"><c:out value="${param['term']}" /></c:set>
<c:set var="id"><c:out value="${param['id']}" /></c:set>
<c:set var="aiId"><c:out value="${param['aiId']}" /></c:set>
<c:set var="ecId"><c:out value="${param['ecId']}" /></c:set>
<c:set var="type"><c:out value="${param['type']}" /></c:set>
<portlet:renderURL var="displayOtherEadUrl" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
	<portlet:param name="aiId" value="${aiId}"/>
	<portlet:param  name="xmlTypeId" value="0"/>
</portlet:renderURL>
<portlet:resourceURL var="displayChildrenUrl" id="displayEadDetails">
	<portlet:param name="id" value="${id}"/>
	<portlet:param name="element" value="${element}"/>
	<portlet:param name="term" value="${term}"/>
</portlet:resourceURL>	
<portlet:renderURL var="printUrl" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
	<portlet:param name="myaction" value="printEadDetails" />
	<portlet:param name="id" value="${id}"/>
	<portlet:param name="ecId" value="${ecId}"/>
	<portlet:param name="element" value="${element}"/>
	<portlet:param name="term" value="${term}"/>
	<portlet:param name="type" value="${type}"/>
	<portlet:param name="pageNumber" value="${pageNumber}"/>
</portlet:renderURL>	
	<script>
		$(document)
				.ready(
						function() {
							$("#print")
									.click(
											function() {
												var preview = window
														.open(
																'${printUrl}',
																'printpreview',
																'width=1000,height=600,left=10,top=10,menubar=0,toolbar=0,status=0,location=0,scrollbars=1,resizable=1');
												preview.focus();
											});
							 document.title = "${documentTitle}";

						});
	</script>
<div id="print">
	<img id="print" align="right" src="${pageContext.request.contextPath}/images/print-icon.png" alt="print"
		title='<fmt:message key="label.print"/>' />
</div>
		<c:choose>
			<c:when test="${empty c}"><portal:ead type="${type}" xml="${eadContent.xml}"/></c:when>
			<c:otherwise>
<portal:ead type="cdetails" xml="${c.xml}" searchTerms="${term}"  searchFieldsSelectionId="${element}" aiId="${aiId}" secondDisplayUrl="${displayOtherEadUrl}"/>
		<c:if test="${not c.leaf}">
		<div id="children" class="box">
			<div class="boxtitle">
					<div class="numberOfPages">
						<ape:pageDescription numberOfItems="${totalNumberOfChildren}" pageSize="${pageSize}" pageNumber="${pageNumber}" />
					</div>
					<div id="child-paging" class="paging">
						<ape:paging numberOfItems="${totalNumberOfChildren}" pageSize="${pageSize}" pageNumber="${pageNumber}"
							refreshUrl="javascript:updatePageNumber('${displayChildrenUrl}')" pageNumberId="pageNumber"/>
							
					</div>
				</div>
				<portal:ead type="cdetails-child" xml="${childXml}"/>
			</div>
		</c:if>
</c:otherwise>
</c:choose>