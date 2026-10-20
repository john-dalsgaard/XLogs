// Client-side filter controls. Keep syntax compatible with Domino Designer.
function xlogsFilterAttribute(checkbox, name) {
	var element = checkbox;
	while (element && element.getAttribute) {
		var value = element.getAttribute(name);
		if (value != null) return value;
		element = element.parentNode;
	}
	return null;
}

function xlogsFilterKey(checkbox) {
	var path = window.location.pathname;
	return "xlogs.filters." + path.substring(0, path.lastIndexOf("/") + 1)
		+ xlogsFilterAttribute(checkbox, "data-category") + "."
		+ xlogsFilterAttribute(checkbox, "data-selector");
}

function xlogsApplyFilter(checkbox) {
	var selector = xlogsFilterAttribute(checkbox, "data-selector");
	if (!selector) return;
	var elements = document.querySelectorAll(".fileViewer " + selector);
	for (var i = 0; i < elements.length; i++) {
		elements[i].style.display = checkbox.checked ? "inline" : "none";
	}
}

function xlogsRestoreFilters() {
	var checkboxes = document.querySelectorAll(".xlogs-filters input[type=checkbox]");
	for (var i = 0; i < checkboxes.length; i++) {
		var checkbox = checkboxes[i];
		checkbox.checked = xlogsFilterAttribute(checkbox, "data-default") == "true";
		try {
			var stored = window.sessionStorage.getItem(xlogsFilterKey(checkbox));
			if (stored != null) checkbox.checked = stored == "true";
		} catch (error) {
			// Use the design default if browser storage is unavailable.
		}
		xlogsApplyFilter(checkbox);
	}
}

function xlogsFilterChanged(event) {
	var checkbox = event.target;
	if (checkbox.type != "checkbox" || !xlogsFilterAttribute(checkbox, "data-selector")) return;
	xlogsApplyFilter(checkbox);
	try {
		window.sessionStorage.setItem(xlogsFilterKey(checkbox), checkbox.checked ? "true" : "false");
	} catch (error) {
		// Filtering still works without browser storage.
	}
}

function xlogsInitializeFilters() {
	document.addEventListener("change", xlogsFilterChanged);
	xlogsRestoreFilters();
}

if (document.readyState == "loading") {
	document.addEventListener("DOMContentLoaded", xlogsInitializeFilters);
} else {
	xlogsInitializeFilters();
}
