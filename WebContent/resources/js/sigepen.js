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
  } else  {
    sessionRefresh();
    setTimeout();
  }
}

document.addEventListener("visibilitychange", handleVisibilityChange, false);

window.addEventListener("load", function(e) {
	setTimeout();
});

function setTimeout() {
	clearInterval(timeout);
	timeout = setInterval(sessionRefresh, 1810000);
}