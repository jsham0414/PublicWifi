/**
 * 
 */

const getMyLocationButton = document.getElementById("getLocation");
const getWIFIButton = document.getElementById("getWIFI");

const curruntURL = window.location.href;
const searchParameters = new URL(curruntURL).searchParams;

var latitude = searchParameters.get("lat"); 
var longitude = searchParameters.get("lnt");

if (latitude != null) {
	document.getElementById("LAT").value = latitude != null ? latitude : "0.0";
}

if (longitude != null) {
	document.getElementById("LNT").value = longitude != null ? longitude : "0.0";
}

getMyLocationButton.onclick = function() {
	navigator.geolocation.getCurrentPosition(function(pos) {
		console.log(pos);
		latitude = pos.coords.latitude;
		longitude = pos.coords.longitude;
		
		document.getElementById("LAT").value = latitude != null ? latitude : "0.0";
		document.getElementById("LNT").value = longitude != null ? longitude : "0.0";
	});
};

getWIFIButton.onclick = function() {
	latitude = document.getElementById("LAT").value;
	longitude = document.getElementById("LNT").value;
	
	location.href = "index.jsp?lat=" + encodeURIComponent(latitude) + "&lnt=" + encodeURIComponent(longitude);
};
 
