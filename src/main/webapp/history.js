/**
 * 
 */

const deleteButtons = document.querySelectorAll(".delete");

const curruntURL = window.location.href;
const searchParameters = new URL(curruntURL).searchParams;

var del = searchParameters.get("del");
if (del != null) {
	location.href = "history.jsp";
}

deleteButtons.forEach(function(button) {
	button.addEventListener("click", function() {
		var del = button.getAttribute("del-id");
		location.href = "history.jsp?del=" + encodeURIComponent(del);
	})
});