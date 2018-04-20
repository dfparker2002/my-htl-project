/** a JS file that shall be included */
$( document ).ready(function() {
    feedImporter();
});
function feedImporter() {

    var feedURL = document.getElementById('feed-url').innerHTML;
    var feedListSize = document.getElementById('feed-list-size').innerHTML;
    var pagePath = document.getElementById('pagePath').value;
		console.log(feedURL);
		console.log(feedListSize);
		console.log(pagePath);

		$.ajax({
                url: "/bin/feeds",
    			data : {feedURL,feedListSize,pagePath},
                success: function(result) {
               console.log("result");
                    console.log(result);
                console.log(JSON.parse(result));
                 updateFeeds(JSON.parse(result),feedListSize);
                },
                error: function(xhr, status, error) {

                },
                complete: function() {
      // Schedule the next request when the current one's complete
     		 setTimeout(feedImporter, 5000);
   				}
			});

}
function updateFeeds(result,size){
$("#feed-data").empty();
 if(size<=result.rss.channel.item.length) {
  for (i = 0; i < size; i++) { 
     var item= result.rss.channel.item[i];
      var title = "<h3>"+item.title+"</h3>"; 
      var description = "<p>"+item.description+"</p>";
      var pubDate= "<span>"+item.pubDate+"</span>";
	$("#feed-data").append(title,description,pubDate);

}
 } else {
for (i = 0; i < result.rss.channel.item.length; i++) { 
     var item= result.rss.channel.item[i];
      var title = "<h3>"+item.title+"</h3>"; 
      var description = "<p>"+item.description+"</p>";
      var pubDate= "<span>"+item.pubDate+"</span>";
	$("#feed-data").append(title,description,pubDate);
 }
 }
}