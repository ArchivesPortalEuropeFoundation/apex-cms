<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<portlet:defineObjects />
<div id="directoryPortlet">
	<div id="directory-column-right-content" class="portlet-layout">
		<portal:eag eagUrl="${eagUrl}"/>
	</div>
	<div class="portlet-layout">
		<iframe id="maps" width="970" height="400" frameborder="0" scrolling="no" marginheight="0" marginwidth="0" src="${embeddedMapUrl}${mapUrlCenterParameters}"></iframe>
	</div>
</div>
<script type="text/javascript" src="https://maps.google.com/maps/api/js?sensor=false"></script>
<script type="text/javascript">
	$(document).ready(function(){
		var selectedCountryCode = "${country}";
		var input_address = "";
		$(".address").each(function(){
			input_address += $(this).html();
		});
		var archivalInstitutionName = '${archivalInstitutionName}';
		var geocoder = new google.maps.Geocoder();
		// If necessary, recover the first element in visitors address element.
		if (input_address.indexOf("<p>") != '-1') {
			input_address = input_address.substring((input_address.indexOf("<p>") + 3), input_address.indexOf("</p>"));
		}
		geocoder.geocode( { address: input_address, region: selectedCountryCode }, function(results, status) {
			if (status == google.maps.GeocoderStatus.OK) {
				var lat = results[0].geometry.location.lat();
				var lng = results[0].geometry.location.lng();
				var span = results[0].geometry.viewport.toSpan();
				var spanLat = span.lat();
				var spanLng = span.lng();
				var parameters = "&ll=" + lat +"," + lng;
				parameters = parameters + "&spn=" + spanLat +"," + spanLng;
				if (archivalInstitutionName){
					parameters = parameters + "&q=" + encodeURIComponent(archivalInstitutionName) +",+" + selectedCountryCode;
				}
				$("#maps").attr("src", "${embeddedMapUrl}" + parameters);
			}else{
				$("iframe#maps").remove();
				}
			});
		//remove see-more/see-less
		$(".displayLinkSeeMore").each(function(){$(this).remove();});
		$(".displayLinkSeeLess").each(function(){$(this).remove();});
		$("th").each(function(){
			var html = $(this).html();
			if(html.indexOf("()")!=-1){
				$(this).html(html.replace("()",""));
			}
		});
		self.print();
	});
</script>