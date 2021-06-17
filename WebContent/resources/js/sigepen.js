var timeout;

function download() {
	document.getElementById('iDownload').contentWindow.go();
}

function sessionRefresh() {
	refresh();
}

function handleVisibilityChange() {
  if (document.hidden) {
    clearInterval(timeout);
  } else {
    sessionRefresh();
    setarTimeout();
  }
}

document.addEventListener("visibilitychange", handleVisibilityChange, false);

window.addEventListener("load", function() {
	setarTimeout();
});

function setarTimeout() {
	clearInterval(timeout);
	timeout = setInterval(sessionRefresh, 1810000);
}