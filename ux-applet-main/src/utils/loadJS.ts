function loadJS(url: string, callback: () => void) {
	let script = document.createElement("script");
	let fn = callback || function () {};
	script.type = "text/javascript";
	script.onload = function () {
		fn();
	};
	script.src = url;
	document.getElementsByTagName("head")[0].appendChild(script);
}

export { loadJS };
