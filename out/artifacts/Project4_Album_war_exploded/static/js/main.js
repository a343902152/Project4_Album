// Gao Meng

function show_full(img_url) {
	console.log("show_full" + img_url);
 	document.getElementById('full_img').src = img_url;
 	document.getElementById('full').style.visibility='visible';
 	document.getElementById('full').className="full full_img_trans";
 };

 function hide_full () {
 	console.log("hide_full");
 	document.getElementById('full_img').src = "";
 	document.getElementById('full').style.visibility='hidden';
 	document.getElementById('full').className="full";
 };

function add_img() {
	 document.getElementById('img_file').click();
}

function submit () {
	document.getElementById('submit').click();
}

// ===========================

function edit_name(url) {
	console.log("show_full_form" + url);
 	document.getElementById('name_edit_form').action = url;
 	document.getElementById('name_editor').style.visibility='visible';
 	document.getElementById('name_editor').className="full_img_trans";
};

function hide_full_form () {
	console.log("hide_full_form");
	document.getElementById('name_edit_form').action = "";
	document.getElementById('name_editor').style.visibility='hidden';
	document.getElementById('name_editor').className="";
};