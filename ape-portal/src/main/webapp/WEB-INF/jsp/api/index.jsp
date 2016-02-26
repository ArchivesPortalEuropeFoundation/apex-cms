<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<portlet:defineObjects />
<portlet:actionURL var="actionUrl">
    <portlet:param name="myaction" value="getApiKey" />
</portlet:actionURL>
<portlet:actionURL var="changeApiKey">
    <portlet:param name="myaction" value="changeApiKey" />
</portlet:actionURL>


<div id="languagePortlet">
    <h1>Get API Key</h1>
    <h2>API Key</h2>

    <form:form id="getApiKey" name="getApiKey" commandName="apiKey" method="post"
               action="${actionUrl}">
        <form:hidden path="firstName" value="${apiKey.firstName}"/>
        <form:hidden path="lastName" value="${apiKey.lastName}"/>
        <form:hidden path="email" value="${apiKey.email}"/>
        <form:hidden path="key" value="${apiKey.key}"/>
        <table class="defaultlayout">
            <tr>
                <th>First Name<!--<fmt:message key="savedsearch.id"/>-->:</th>
                <td><c:out value="${apiKey.firstName}"/></td>
            </tr>
            <tr>
                <th>Last Name<!--<fmt:message key="savedsearch.id"/>-->:</th>
                <td><c:out value="${apiKey.lastName}"/></td>
            </tr>
            <tr>
                <th>Email<!--<fmt:message key="savedsearch.id"/>-->:</th>
                <td><c:out value="${apiKey.email}"/></td>
            </tr>
            <c:choose>
                <c:when test="${apiKey.key ne 'empty' and apiKey.key ne 'renew'}">
                    <tr>
                        <th>Domain Name<!--<fmt:message key="savedsearch.id"/>-->:</th>
                        <td><c:out value="${apiKey.domain}"/></td>
                    </tr>
                    <tr>
                        <th>Your Api Key<!--<fmt:message key="savedsearch.id"/>-->:</th>
                        <td><c:out value="${apiKey.key}"/></td>
                    </tr>
                    <a href="${changeApiKey}">Change Api Key</a>

                </c:when>
                <c:otherwise>
                    <tr>
                        <th class="description">Domain Name<!--<fmt:message key="savedsearch.id"/>-->:</th>
                        <td><form:input path="domain"  cssClass="longInput" maxlength="100" value="${apiKey.domain}"/></td>
                    </tr>
                    <tr>
                        <td class="linkButton" colspan="2">
                            <input type="submit" value="Submit"/>
                        </td>
                    </tr>
                    <tr>
                        <th>Your Api Key<!--<fmt:message key="savedsearch.id"/>-->:</th>
                        <td><c:out value="${apiKey.key}"/></td>
                    </tr>

                </c:otherwise>
            </c:choose>
        </table>
    </form:form>
</div>