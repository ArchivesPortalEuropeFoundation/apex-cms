<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<portlet:defineObjects />
<portlet:renderURL var="printEagDetailsUrl" windowState="<%=LiferayWindowState.POP_UP.toString()%>">
	<portlet:param name="myaction" value="printEagDetails" />
	<portlet:param name="id" value="${aiId}" />
</portlet:renderURL>
<portal:friendlyUrl var="contentUrl" type="directory-content" />
<script type='text/javascript'>
	$(document).ready(function() {
		document.title = "${documentTitle}";
	});
</script>
<div id="buttonsHeaderEag">
	<div class="linkButton right" id="printEagDetails">
		<a href="javascript:printEagByURL('${printEagDetailsUrl}')"><fmt:message key="label.print" /><span class="icon_print">&nbsp;</span></a>
	</div>
</div>
<div>
<a id="eagDetails"></a>
<portal:eag eagUrl="${eagUrl}"/>
	<c:if test="${hasHoldingsGuides or hasFindingAids or hasSourceGuides}">
		<table class="aiSection otherDisplay">
			<thead>
				<tr>
					<th colspan="2"><fmt:message key="directory.archivalmaterial.title"/></th>
				</tr>
			</thead>
			<tbody>
				<c:if test="${hasHoldingsGuides}">
				<tr>
					<td colspan="2">
						<div>
							<a href="${contentUrl}/${archivalInstitution.encodedRepositorycode}/hg" target="_blank"><fmt:message key="directory.archivalmaterial.list.hg"/></a>
						</div>
					</td>
				</tr>
				</c:if>
				<c:if test="${hasFindingAids}">
				<tr>
					<td colspan="2">
						<div>
							<a href="${contentUrl}/${archivalInstitution.encodedRepositorycode}/fa" target="_blank"><fmt:message key="directory.archivalmaterial.list.fa"/></a>
						</div>
					</td>
				</tr>
				</c:if>
				<c:if test="${hasSourceGuides}">
				<tr>
					<td colspan="2">
						<div>
							<a href="${contentUrl}/${archivalInstitution.encodedRepositorycode}/sg" target="_blank"><fmt:message key="directory.archivalmaterial.list.sg"/></a>
						</div>
					</td>
				</tr>
				</c:if>
			</tbody>
		</table>
	</c:if>
</div>
