<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet" %>
<portlet:defineObjects />
<portal:page  varPlId="advancedSearchPlId"  varPortletId="advancedSearchPortletId" portletName="advancedsearch" friendlyUrl="/search"/>		
<portlet:resourceURL var="autocompletionUrl" id="autocompletion" />
<liferay-portlet:renderURL var="advancedSearchUrl"  plid="${advancedSearchPlId}" portletName="${advancedSearchPortletId}">
	<portlet:param name="myaction" value="simpleSearch" />
	<liferay-portlet:param  name="advanced" value="false"/>
</liferay-portlet:renderURL>
		<script type="text/javascript">
			$(document).ready(function() {
				initSimpleSearchAutocompletion("${autocompletionUrl}", true );
			});
		</script>
<div id="embeddedSimpleSearchPortlet">
		<div id="simpleSearch">
			<div class="simpleSearchOptions">
				<form id="simpleSearchForm" method="post" action="${advancedSearchUrl}">
					<input name="term" id="searchTerms" type="text" title="${termTitle}" tabindex="1" maxlength="100"  autocomplete="off" placeholder="<fmt:message key="simplesearch.message.search.inallcontent"/>"/> <input
								type="submit" id="searchButton" title="<fmt:message key="advancedsearch.message.start"/>" tabindex="10"
								value="<fmt:message key="advancedsearch.message.search"/>" />
				</form>				
			</div>
		</div>
	
</div>	
