<%--
/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>
<%@ include file="/html/taglib/init.jsp" %>

<%@ page import="com.liferay.taglib.ui.LanguageTag" %>

<%

String formName = (String)request.getAttribute("liferay-ui:language:formName");

String formAction = (String)request.getAttribute("liferay-ui:language:formAction");

if (Validator.isNull(formAction)) {
	LiferayPortletURL liferayPortletURL = null;

	if (portletResponse != null) {
		LiferayPortletResponse liferayPortletResponse = (LiferayPortletResponse)portletResponse;

		liferayPortletURL = liferayPortletResponse.createLiferayPortletURL(PortletKeys.LANGUAGE, PortletRequest.ACTION_PHASE);
	}
	else {
		liferayPortletURL = new PortletURLImpl(request, PortletKeys.LANGUAGE, plid, PortletRequest.ACTION_PHASE);
	}

	liferayPortletURL.setWindowState(WindowState.NORMAL);
	liferayPortletURL.setPortletMode(PortletMode.VIEW);
	liferayPortletURL.setAnchor(false);

	liferayPortletURL.setParameter("struts_action", "/language/view");
	liferayPortletURL.setParameter("redirect", currentURL);

	formAction = liferayPortletURL.toString();
}

String name = (String)request.getAttribute("liferay-ui:language:name");
Locale[] locales = (Locale[])request.getAttribute("liferay-ui:language:locales");
int displayStyle = GetterUtil.getInteger((String)request.getAttribute("liferay-ui:language:displayStyle"));

Map langCounts = new HashMap();

for (int i = 0; i < locales.length; i++) {
	Integer count = (Integer)langCounts.get(locales[i].getLanguage());

	if (count == null) {
		count = new Integer(1);
	}
	else {
		count = new Integer(count.intValue() + 1);
	}

	langCounts.put(locales[i].getLanguage(), count);
}

Set duplicateLanguages = new HashSet();

for (int i = 0; i < locales.length; i++) {
	Integer count = (Integer)langCounts.get(locales[i].getLanguage());

	if (count.intValue() != 1) {
		duplicateLanguages.add(locales[i].getLanguage());
	}
}
%>


		<aui:form action="<%= formAction %>" method="post" name="<%= formName %>">
					<div class="changeLanguage">
			<aui:select changesContext="<%= true %>" label="" name="<%= name %>" onChange="document.getElementById('_82_fm').submit()" title="language">

				<%
				for (int i = 0; i < locales.length; i++) {
					String language = locales[i].getDisplayLanguage(locales[i]);
					String country = locales[i].getDisplayCountry(locales[i]);
					
					String label = language;
                    label = Character.toUpperCase(label.charAt(0)) + label.substring(1); //Added by Yoann
					if (duplicateLanguages.contains(locales[i].getLanguage())){
						label += " (" + country + ")";
					}

				%>

					<aui:option label="<%= label %>" lang="<%= LocaleUtil.toW3cLanguageId(locales[i]) %>" selected="<%= (locale.getLanguage().equals(locales[i].getLanguage()) && locale.getCountry().equals(locales[i].getCountry())) %>" value="<%= LocaleUtil.toLanguageId(locales[i]) %>" />

				<%
				}
				%>

			</aui:select>
			</div>
		</aui:form>
