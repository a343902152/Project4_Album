// Gao Meng

function show_full(img_url) {
	console.log("show_full" + img_url);
 	document.getElementById('full_img').src = img_url;
 	document.getElementById('full').style.visibility='visible';
 	document.getElementById('full').className="full_img_trans";
 };

 function hide_full () {
 	console.log("hide_full");
 	document.getElementById('full_img').src = "";
 	document.getElementById('full').style.visibility='hidden';
 	document.getElementById('full').className="";
 };

function add_img() {
	 document.getElementById('img_file').click();
}

function submit () {
	document.getElementById('submit').click();
}