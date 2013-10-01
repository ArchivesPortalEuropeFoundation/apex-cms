function createOpenGraphTags(){
	var firstHref = $("#featuredExhibitionDetails .thumbs a").first().attr("href");
	var metadata = "<meta property='og:image' content='" + firstHref + "' />";
	$("head").append(metadata);
}