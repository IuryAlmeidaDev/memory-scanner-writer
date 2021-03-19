var timeout;
clearInterval(timeout);
timeout = setInterval(sessionTimeout, 1799000);

function download() {
	document.getElementById('iDownload').contentWindow.go();
}

function sessionTimeout() {
	logout();
}