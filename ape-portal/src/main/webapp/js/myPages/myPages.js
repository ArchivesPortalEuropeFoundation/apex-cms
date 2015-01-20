/**
 * Function to add all the needed parameters to the URL passed in order to
 * avoid lost its values when the session expired when editing some information
 * of "My Pages" section (for saved searches, saved bookmarks and my
 * collections). 
 *
 * @param targetUrl URL in which all the needed values will be added.
 * @param section Current section of my pages in which the edition is performed.
 * @param form Form to be submitted after recover all the needed values.
 */
function completeUrl(targetUrl, section, form) {
	var parametersToAdd = "";

	// Checks the current section to call the needed function.
	if (section == "savedsearch") {
		// Section is "Saved searches".
		parametersToAdd = completeSavedSearchesURL(section);
	} else if (section == "savedbookmarks") {
		// Section is "Saved bookmarks".
		parametersToAdd = completeSavedBookmarksURL(section);
	} else {
		// Section is "My collections".
		parametersToAdd = completeMyCollectionsURL(section);
	}

	// Complete the URL and submit the form.
    var finalUrl = targetUrl + parametersToAdd;
	$("form#" + form).attr("action", finalUrl);
	$("form#" + form).submit();
}

/**
 * Function to add all the needed parameters when editing a saved search.
 *
 * @param section Name of the saved search portlet.
 *
 * @returns {String} String with the constructed parameters to add in the URL.
 */
function completeSavedSearchesURL(section) {
	// Final parameters values.
	var parametersToAdd = "";

	// Recover the description value.
	var description = $('input#description').val();
	// Set the description as parameter.
	parametersToAdd = "&_"+section+"_WAR_Portal_description=" + description;

	// Recover the public value.
	var isPublic = false;
	if ($("#publicSearch1").is(':checked')) {
		isPublic = true;
	}
	// Set the public value as parameter.
	parametersToAdd += "&_"+section+"_WAR_Portal_publicSearch=" + isPublic;

	// Recover the template value.
	var isTemplate = false;
	if ($("#template1").is(':checked')) {
		isTemplate = true;
	}
	// Set the template value as parameter.
	parametersToAdd += "&_"+section+"_WAR_Portal_template=" + isTemplate;

	return parametersToAdd;
}

/**
 * Function to add all the needed parameters when editing a saved bookmark.
 *
 * @param section Name of the saved bookmark portlet.
 *
 * @returns {String} String with the constructed parameters to add in the URL.
 */
function completeSavedBookmarksURL(section) {
	// Recover the description value.
	var description = $('input#description').val();

	// Set the description as parameter.
	return "&_"+section+"_WAR_Portal_description=" + description;
}

/**
 * Function to add all the needed parameters when editing a collection.
 *
 * @param section Name of the collections portlet.
 *
 * @returns {String} String with the constructed parameters to add in the URL.
 */
function completeMyCollectionsURL(section) {
	// Call function which correct the information about the selections for the
	// current page displayed.
	prevSubmit();

	// Final parameters values.
	var parametersToAdd = "";

	// Recover the title value.
	var title = $('input#collectionTitle').val();
	// Set the title as parameter.
	parametersToAdd = "&_"+section+"_WAR_Portal_collectionTitle=" + title;

	// Recover the description value.
	var description = $('textarea#collectionDescription').val();
	// Set the description as parameter.
	parametersToAdd += "&_"+section+"_WAR_Portal_collectionDescription=" + description;

	// Recover the public value.
	var isPublic = "off";
	if ($("#collectionField_public").is(':checked')) {
		isPublic = "on";
	}
	// Set the public value as parameter.
	parametersToAdd += "&_"+section+"_WAR_Portal_collectionField_public=" + isPublic;

	// Recover the edit value.
	var isEdit = "off";
	if ($("#collectionField_edit").is(':checked')) {
		isEdit = "on";
	}
	// Set the edit value as parameter.
	parametersToAdd += "&_"+section+"_WAR_Portal_collectionField_edit=" + isEdit;

	// Recover and add the selected searches.
	parametersToAdd += addElements(section, "hidden_selected_search_");

	// Recover and add the new searches.
	parametersToAdd += addElements(section, "hidden_collection_bookmark_");

	// Recover and add the selected bookmarks.
	parametersToAdd += addElements(section, "hidden_new_search_");

	// Recover and add the new bookmarks.
	parametersToAdd += addElements(section, "hidden_new_bookmark_");

	return parametersToAdd;
}

/**
 * Function to recover the information about the selected elements.
 *
 * @param section Name of the collections portlet.
 *
 * @returns {String} String with the constructed parameters to add in the URL.
 */
function addElements(section, name) {
	// Final parameters values.
	var parametersToAdd = "";

	$("input[id^='" + name + "']").each(function() {
		// Recover ID and value.
		var id = $(this).attr("id");
		var value =  $(this).val();

		// Set the current information as a parameter.
		parametersToAdd += "&_"+section+"_WAR_Portal_" + id + "=" + value;
	});

	return parametersToAdd;
}
