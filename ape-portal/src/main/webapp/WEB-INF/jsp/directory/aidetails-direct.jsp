<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<portlet:defineObjects />
<portlet:renderURL var="printEagDetailsUrl" windowState="<%=LiferayWindowState.POP_UP.toString()%>">
	<portlet:param name="myaction" value="printEagDetails" />
	<portlet:param name="id" value="${aiId}" />
</portlet:renderURL>
<portal:friendlyUrl var="contentUrl" type="directory-content" />
<script type="text/javascript">
	$(document).ready(function() {
		initEagDetails();
	});
</script>
<div id="directoryPortlet">
	<div class="portlet-layout">
		<div id="directory-column-right">
			<div id="directory-column-right-content">
<c:if test="${!empty coordinates}">
	<img id="directoryAiStaticMap" src="http://maps.googleapis.com/maps/api/staticmap?zoom=8&size=640x500&maptype=roadmap
	&markers=color:red%7C${coordinates.lat},${coordinates.lon}" width="640" height="500"/>
	<div id="viewFullFond" class="linkButton">
	<a href="https://maps.google.com/maps?daddr=${coordinates.lat},${coordinates.lon}" target="_blank"><fmt:message key="advancedsearch.eag.preview.directions"/><span class="icon_new_window">&nbsp;</span></a>
	</div>
</c:if>			
				<div id="buttonsHeaderEag">
					<div class="linkButton right" id="printEagDetails">
						<a href="javascript:printEagByURL('${printEagDetailsUrl}')"><fmt:message key="label.print" /><span
							class="icon_print">&nbsp;</span></a>
					</div>
				</div>
				<div class="${mobile}">
					<portal:eag eagUrl="${eagUrl}" />
					<c:if test="${hasHoldingsGuides or hasFindingAids or hasSourceGuides}">
						<table class="aiSection otherDisplay">
							<thead>
								<tr>
									<th colspan="2"><fmt:message key="directory.archivalmaterial.title" /></th>
								</tr>
							</thead>
							<tbody>
								<c:if test="${hasHoldingsGuides}">
									<tr>
										<td colspan="2">
											<div>
												<a href="${contentUrl}/${archivalInstitution.encodedRepositorycode}/hg"><fmt:message
														key="directory.archivalmaterial.list.hg" /></a>
											</div>
										</td>
									</tr>
								</c:if>
								<c:if test="${hasFindingAids}">
									<tr>
										<td colspan="2">
											<div>
												<a href="${contentUrl}/${archivalInstitution.encodedRepositorycode}/fa"><fmt:message
														key="directory.archivalmaterial.list.fa" /></a>
											</div>
										</td>
									</tr>
								</c:if>
								<c:if test="${hasSourceGuides}">
									<tr>
										<td colspan="2">
											<div>
												<a href="${contentUrl}/${archivalInstitution.encodedRepositorycode}/sg"><fmt:message
														key="directory.archivalmaterial.list.sg" /></a>
											</div>
										</td>
									</tr>
								</c:if>
							</tbody>
						</table>
					</c:if>
				</div>
			</div>
		</div>
	</div>
</div>
