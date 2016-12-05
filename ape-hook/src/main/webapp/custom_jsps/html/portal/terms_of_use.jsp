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

<%@ include file="/html/portal/init.jsp" %>

<%
String currentURL = PortalUtil.getCurrentURL(request);

String referer = ParamUtil.getString(request, WebKeys.REFERER, currentURL);

if (referer.equals(themeDisplay.getPathMain() + "/portal/update_terms_of_use")) {
	referer = themeDisplay.getPathMain() + "?doAsUserId=" + themeDisplay.getDoAsUserId();
}
%>

<aui:form action='<%= themeDisplay.getPathMain() + "/portal/update_terms_of_use" %>' name="fm">
	<aui:input name="doAsUserId" type="hidden" value="<%= themeDisplay.getDoAsUserId() %>" />
	<aui:input name="<%= WebKeys.REFERER %>" type="hidden" value="<%= referer %>" />

	<c:choose>
		<c:when test="<%= (PropsValues.TERMS_OF_USE_JOURNAL_ARTICLE_GROUP_ID > 0) && Validator.isNotNull(PropsValues.TERMS_OF_USE_JOURNAL_ARTICLE_ID) %>">
			<liferay-ui:journal-article articleId="<%= PropsValues.TERMS_OF_USE_JOURNAL_ARTICLE_ID %>" groupId="<%= PropsValues.TERMS_OF_USE_JOURNAL_ARTICLE_GROUP_ID %>" />
		</c:when>
		<c:otherwise>
			Welcome to our site. We maintain this web site as a service to our members. By using our site, you are agreeing to comply with and be bound by the following terms of use. Please review the following terms carefully. If you do not agree to these terms, you should not use this site.

			<br /><br />

			<ol>
				<li>
					<strong><u>Terms of Use</u></strong><br />

					Use of this site is subject to the following Terms and Conditions. This site is operated by The Archives Portal Europe Foundation (APEF), a foundation registered under Dutch law. The site is known as Archives Portal Europe (APE). By visiting our portal you (the USER) agree to be bound by these general Terms of Use, which you declare you have read and understood. APEF reserves the right to amend or change these Terms at any time, at its sole discretion. It is up to you to consult them regularly on this website when you log in to keep yourself informed of any changes. If you use this Website it means that you have read and accepted these alterations.

					<br /><br />
				</li>
				<li>
					<strong><u>Intellectual Property Rights</u></strong><br />

					Unless otherwise stated, the Archives Portal Europe (APE) Website is protected by Intellectual Property Rights. APEF holds the copyright for the site layout, design, images, programs, text and other information (collectively, the "Website Content"). No material may be resold or published elsewhere without APEF’s written consent. The content provider retains the right to the content published on the website (collectively, the “Content”) The terms for access to individual objects by members of the public are in accordance with international copyright law.

					<br /><br />
   				
                                        Unless otherwise stated, the content searchable and accessible through this Website is protected by Intellectual Property Rights. Every Contributing Institution retains the copyright on the metadata and the digital objects these metadata link to as published on the Archives Portal Europe (APE) Unless otherwise stated, only non-commercial and personal use of the Website and the content is permitted. Unless otherwise stated, the thumbnalils of the digital objects that the portal provides are intended for real-time viewing and cannot be downloaded (either permanently or temporarily), copied, stored, or redistributed by the user.

					<br /><br />
   				
                                        You acknowledge that you are responsible for whatever material you submit and you, not Archives Portal Europe, have full responsibility for the content, including its lawfulness, reliability, appropriateness, originality, copyright.

					<br /><br />
				</li>
				<li>
					<strong><u>Right to Use</u></strong><br />

					As a registered user you may access the open parts of this site in accordance with the Terms and Conditions. APEF gives free access to the catalogues and digitised objects of the contributing institutions to this service. Anyone can freely create an account in accordance to the standard privacy law and in accordance to these Terms of Use.

                                        <br /><br />
                                        
                                        You are allowed access to browse the site for personal use. Unreasonable use, including the systematic downloading of content without a written licence, will result in access to the site being blocked. Under no circumstances does permitted usage accessible via a registered user account include commercial exploitation of the material.
                                        
                                        <br /><br />
                                        
                                        As a registered user, your right to use the Site is not transferable. You cannot communicate your login details to anyone.

                                        <br /><br />
                                        
                                        The Archives Portal Europe Foundation will endeavour to provide access to all materials on a 24/7 basis, but cannot be held liable for any loss of service.
                                        
                                        <br /><br />
                                        
                                        Only content providers are allowed to edit and/or delete their content appearing on the Site; Archives Portal Europe is not responsible for the accuracy and truthfulness of this content.

					<br /><br />
				</li>
				<li>
					<strong><u>Copyright Agents</u></strong><br />

					We respect the intellectual property of others, and we ask you to do the same. If you believe that your work has been copied in a way that constitutes copyright infringement, please contact our Copyright Agent and provide the following information (info@archivesportaleurope.net):

					<br /><br />

                                        (a) An electronic or physical signature of the person authorized to act on behalf of the owner of the copyright interest; 

					<br /><br />

                                        (b) A description of the copyrighted work that you claim has been infringed; 

					<br /><br />

                                        (c) A description of where the material that you claim is infringing is located on the Site; 

					<br /><br />

                                        (d) Your address, telephone number, and email address; 

					<br /><br />
                                        
                                        (e) A statement by you that you have a good faith belief that the disputed use is not authorized by the copyright owner, its agent, or the law; and 

					<br /><br />

                                        (f) A statement by you, made under penalty of perjury, that the above information in your Notice is accurate and that you are the copyright owner or authorized to act on the copyright owner's behalf.

					<br /><br />
				</li>
				<li>
					<strong><u>Links to Other Web Sites</u></strong><br />

					This website provides access to partner websites via hypertext links. These other sites and parties are not under our control, and you acknowledge that we are not responsible for the accuracy, copyright compliance, legality, decency, or any other aspect of the content of such sites, nor are we responsible for errors or omissions in any references to other parties or their products and services. The inclusion of such a link or reference is provided merely as a convenience and does not imply endorsement of, or association with, the site or party by us, or any warranty of any kind, either expressed or implied.

					<br /><br />
				</li>
				<li>
					<strong><u>Liability Disclaimer</u></strong><br />

					APEF does not warrant the information on this site, or on any site that it links to. 

					<br /><br />
                                        
                                        APEF does not accept responsibility for any loss, injury, damage or lawsuit related to the use of information contained within the Archive Portal Europe website or any linked site. APEF will not be liable for any special, incidental or consequential damages whatsoever (including but not limited to, damages for loss of profits or confidential or other information, for business interruption, for loss of privacy arising out of or in any way related to the use of or inability to use the Services), even if APEF has been advised of the possibility of such damages and even if the remedy fails of its essential purpose. Similarly, APEF will not be liable for any infringement of privacy, publicity or other rights and any use by third parties that goes beyond the rights expressed in these Terms of Use

					<br /><br />
                                        
                                        APEF is not responsible for the site to be compatible with all browser software. APEF take reasonable steps necessary to ensure that the site is secure and free from viruses, but is not responsible for any loss of data or hardware damage caused through usage. Nor is the security of information passed electronically to the site guaranteed.

					<br /><br />
				</li>
				<li>
					<strong><u>Applicable Law</u></strong><br />

					These terms and conditions are construed and shall be governed in accordance with the laws of The Netherlands. Any dispute shall be subject to the non-exclusive jurisdiction of the Dutch Courts.

					<br /><br />
				</li>
			</ol>
		</c:otherwise>
	</c:choose>

	<c:if test="<%= !user.isAgreedToTermsOfUse() %>">
		<aui:button-row>
			<aui:button type="submit" value="i-agree" />

			<%
			String taglibOnClick = "alert('" + UnicodeLanguageUtil.get(pageContext, "you-must-agree-with-the-terms-of-use-to-continue") + "');";
			%>

			<aui:button onClick="<%= taglibOnClick %>" type="cancel" value="i-disagree" />
		</aui:button-row>
	</c:if>
</aui:form>
