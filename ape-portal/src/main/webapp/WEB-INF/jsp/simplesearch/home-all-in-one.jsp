<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet" %>
<portlet:defineObjects />
<portlet:actionURL var="simpleSearchUrl" >
	<portlet:param name="myaction" value="simpleSearch" />
</portlet:actionURL>
<div id="simpleSearchPortlet">
	<form:form id="simpleSearchForm" name="simpleSearchForm" commandName="simpleSearch" method="post" action="${simpleSearchUrl}">
		<div id="simpleSearch">
			<div id="simpleSearchOptionsContent" class="searchOptionsContent">
				<div class="simpleSearchOptions">
					<table id="simplesearchCriteria">
						<fmt:message key="advancedsearch.message.typesearchterms2" var="termTitle" />
						<tr>
							<td colspan="2"><form:input path="term" id="searchTerms" title="${termTitle}" tabindex="1" maxlength="100" /> <input
								type="submit" id="searchButton" title="<fmt:message key="advancedsearch.message.start"/>" tabindex="10"
								value="<fmt:message key="advancedsearch.message.search"/>" /></td>
						</tr>
					</table>
				</div>
			</div>
			<p>
				<fmt:message key="advancedsearch.message.firstmessagepartycsi" />
				<span class="bold">${units}</span>
				<fmt:message key="advancedsearch.message.firstmessagepartinst" />
				<span class="bold">${numberOfDaoUnits}</span>
				<fmt:message key="advancedsearch.message.firstmessagepartdao" />
				<span class="bold">${institutions}</span>
				<fmt:message key="advancedsearch.message.firstmessagepartdu" />
			</p>
		</div>
	</form:form>
</div>
