<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="portal"
	uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<portlet:defineObjects />
<portlet:renderURL var="printEagDetailsUrl"
	windowState="<%=LiferayWindowState.POP_UP.toString()%>">
	<portlet:param name="myaction" value="printEagDetails" />
	<portlet:param name="id" value="${aiId}" />
</portlet:renderURL>
<script type="text/javascript">
	$(document).ready(function() {
		initEagDetails();
	});
</script>
<div id="directoryPortlet">
	<div class="portlet-layout">
		<div id="directory-column-right">
			<div id="directory-column-right-content">
				<div id="buttonsHeaderEag">
					<div class="linkButton right" id="printEagDetails">
						<a href="javascript:printEagByURL('${printEagDetailsUrl}')"><fmt:message
								key="label.print" /><span class="icon_print">&nbsp;</span></a>
					</div>
				</div>
				<div>
					<portal:eag eagUrl="${eagUrl}" />
					<table class="aiSection otherDisplay">
						<thead>
							<tr>
								<th colspan="2">Other information</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td class="header">Related resource:</td>
								<td>
									<div>
										<a target="_blank"
											href="http://www.gahetna.nl/collectie/archief">Link to
											related resource</a>
										<p></p>
									</div>
								</td>
							</tr>
							<tr class="longDisplay" style="display: none;">
								<td class="header">Name of the archive (in other
									languages):</td>
								<td>
									<p>National Archives of The Netherlands</p>
								</td>
							</tr>
							<tr>
								<td class="header">Type of archive:</td>
								<td>
									<p>National archives</p>
								</td>
							</tr>
							<tr>
								<td class="header">Last update:</td>
								<td>21.05.2013</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>
