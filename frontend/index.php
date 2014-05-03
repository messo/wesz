<?php

include("settings.php");

/* variables */
$page = ((isset($_GET['page']))? $_GET['page'] : "");

$pages = array("changepassword" => "Jelszómódosítás",
	"getservices" => "Elérhető szolgáltatások",
	"getapplications" => "Igénybe vett szolgáltatások",
	"getremainingsum" => "Egyenleg",
	"apply" => "Szolgáltatás igénybevétele");

/* settings */
$settings['maxlength_title'] = 25;

/* session */
session_start();
if(!isset($_SESSION['loggedin'])){
	$_SESSION['loggedin'] = false;
	$_SESSION['token'] = "";
	$_SESSION['username'] = "";
}

?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1, user-scalable=no" >
<link href="holo.css" rel="stylesheet" type="text/css">
<link href="style.css" rel="stylesheet" type="text/css">
<title>Wellness</title>
</head>

<body class="holo holo-dark">
<div class="holo-accent-blue">

	<div class="holo-action-bar">
<?php

if(in_array($page, array("changepassword","getservices","getapplications","getremainingsum","apply"))){
	echo "		<h1><a href=\"index.php\" class=\"holo-up\"><img src=\"images/logo.png\" width=\"32\" height=\"32\" alt=\"logo\"> ".$pages[$page]."</a></h1>\n";
}
else{
	echo "		<h1><img src=\"images/logo.png\" width=\"32\" height=\"32\" alt=\"logo\"> Wellness</h1>\n";
}

?>
	</div>

	<div class="holo-content">
<?php

if($_SESSION['loggedin']){

	switch($page){

		case "getservices" :
			page_getservices();
		break;

		case "getremainingsum" :
			page_getremainingsum();
		break;

		case "getapplications" :
			page_getapplications();
		break;

		case "apply" :
			page_apply();
		break;

		case "changepassword" :
			page_changepassword();
		break;

		case "logout" :
			page_logout();
		break;

		default :
			page_menu();
		break;
	}
} else {
	if($page == "" || $page == "login"){
		page_login();
	}
	else {
		page_needlogin();
	}
}

/*--functions--*/
function getsoapclient(){
	global $settings;
	return new SoapClient($settings['soap_url'], array('cache_wsdl' => WSDL_CACHE_NONE));
}

function getshorttitle($text){
	global $settings;
	return ((strlen($text) > $settings['maxlength_title'])? substr($text,0,$settings['maxlength_title'])."..." :  $text);
}

/*--pages--*/

function page_menu(){
	echo "		<h2 class=\"holo-divider\">Szolgáltatások</h2>\n"
	."		<ul class=\"holo-list\">\n"
	."			<li><a href=\"index.php?page=getservices\">Elérhető szolgáltatások</a></li>\n"
	."			<li><a href=\"index.php?page=getapplications\">Igénybe vett szolgáltatások</a></li>\n"
	."		</ul>\n"
	."		<h2 class=\"holo-divider\">Lehetőségek</h2>\n"
	."		<ul class=\"holo-list\">\n"
	."			<li><a href=\"index.php?page=getremainingsum\">Egyenleg</a></li>\n"
	."			<li><a href=\"index.php?page=changepassword\">Jelszómódosítás</a></li>\n"
	."			<li><a href=\"index.php?page=logout\">Kijelentkezés</a></li>\n"
	."		</ul>\n";
}

function page_needlogin(){
	echo "		<p>A kért funkció eléréséhez bejelentkezés szükséges.</p>\n"
	."		<div class=\"holo-buttons\">\n"
	."			<input type=\"button\" value=\"Bejelentkezés\" class=\"holo-button\" onclick=\"location.href = 'index.php?page=login'\">\n"
	."		</div>\n";
}

function page_getservices(){

	$client = getsoapclient();
	
	$response_remainingsum = $client->__soapCall("getRemainingSum", array("parameters" => array("arg0" => $_SESSION['token'])));
	$remainingsum = $response_remainingsum->return;

	$response = $client->__soapCall("getServices", array("parameters" => array()));
	
	if(is_array($response->return)){
		echo "		<ul class=\"holo-list\">\n";
		foreach($response->return as $service){
			echo "			<li><a href=\"index.php?page=apply&amp;serviceid=".$service->id."\">"
			.getshorttitle($service->title)." <b>(".(($remainingsum >= $service->cost)? "<span class=\"affordable\">".$service->cost."</span>" : $service->cost)." kredit)</b>"
			."</li>";
		}
		echo "		</ul>\n";
	}
	else {
		echo "		<p>Nincsenek szolgáltatások.</p>\n";
	}
}

function page_getapplications(){

	$client = getsoapclient();
	$response = $client->__soapCall("getApplications", array("parameters" => array("arg0" => $_SESSION['token'])));

	if(is_array($response->return)){
		echo "		<ul class=\"holo-list\">\n";
		foreach($response->return as $serviceobject){
			$service = $serviceobject->service;
			echo "			<li>"
			.getshorttitle($service->title)." <b>(".$service->cost." kredit)</b>"
			."</li>\n";
		}
		echo "		</ul>\n";
	} elseif(isset($response->return->service)){
		$service = $response->return->service;

		echo "		<ul class=\"holo-list\">\n"
		.		"			<li>"
		.getshorttitle($service->title)." <b>(".$service->cost." kredit)</b>"
		."</li>\n"
		."		</ul>\n";
	}
	else {
		echo "		<p>Nincsenek igénybe vett szolgáltatások.</p>\n";
	}
}

function page_getremainingsum(){

	$client = getsoapclient();
	$response = $client->__soapCall("getRemainingSum", array("parameters" => array("arg0" => $_SESSION['token'])));

	echo "		<p>A rendelkezésre álló egyenleg: ".$response->return." kredit</p>\n";
}

function page_apply(){

	$client = getsoapclient();

	if(isset($_POST['option']) && $_POST['option'] == "confirm"){
		$response = $client->__soapCall("apply", array("parameters" => array("arg0" => $_SESSION['token'], "arg1" => $_POST['serviceid'])));
		
		if($response->return){
			echo "		<p class=\"success\">Szolgáltatás igénybevéve.</p>\n";
		} else{
			echo "		<p class=\"failure\">Nem sikerült a szolgáltatás igénybevétele, mivel nem rendelkezik elegendő kredittel.</p>\n";
		}
		page_menu();
	} else {
		$response_remainingsum = $client->__soapCall("getRemainingSum", array("parameters" => array("arg0" => $_SESSION['token'])));
		$remainingsum = $response_remainingsum->return;

		$response = $client->__soapCall("getServiceById", array("parameters" => array("arg0" => $_GET['serviceid'])));

		$service = $response->return;

		echo "		<form action=\"index.php?page=apply\" id=\"applyform\" method=\"post\">\n"
		."			<input type=\"hidden\" name=\"option\" value=\"confirm\">\n"
		."			<input type=\"hidden\" name=\"serviceid\" value=\"".$_GET['serviceid']."\">\n"
		."			<p>Erősítse meg a szolgáltatás igénybevételét!</p>\n"
		."			<h2 class=\"holo-divider\">Adatok</h2>\n"
		."			<p><b>Szolgáltatás:</b> ".$service->title."</p>\n"
		."			<p><b>Leírás:</b> ".$service->description."</p>\n"
		."			<p><b>Ár:</b> ".(($remainingsum >= $service->cost)? "<span class=\"affordable\">".$service->cost."</span>" : $service->cost)." kredit</p>\n"
		."			<p><b>Rendelkezésre álló egyenleg:</b> ".$remainingsum." kredit</p>\n"
		."			<div class=\"holo-buttons holo-plain\">\n"
		."				<button type=\"button\" onclick=\"document.getElementById('applyform').submit()\">Igénybe vesz</button>\n"
		."				<button type=\"button\" onclick=\"window.location.href = 'index.php'\">Mégse</button>\n"
		."			</div>\n"
		."		</form>\n";
	}
}

function page_logout(){
	$_SESSION['loggedin'] = false;
	page_login();
}

function page_login(){

	$showform = true;

	if(isset($_POST['username'])){
		$client = getsoapclient();
		$response = $client->__soapCall("authenticate", array("parameters" => array("arg0" => $_POST['username'], "arg1" => $_POST['password'])));
	
		if(isset($response->return)){
			$_SESSION['loggedin'] = true;
			$_SESSION['username'] = $_POST['username'];
			$_SESSION['token'] = $response->return;
			
			$showform = false;
			
			echo "		<p class=\"success\">Sikeres belépés.</p>\n";
			page_menu();
		}
		else{
			echo "		<p>A megadott felhasználónév-jelszó páros nem megfelelő.</p>\n";
		}
	}

	if($showform){
		echo "		<form action=\"index.php?page=login\" method=\"post\">\n"
		."			<div class=\"holo-field\">\n"
		."				<div class=\"holo-field-bracket\"></div>\n"
		."				<input type=\"text\" name=\"username\" placeholder=\"Felhasználónév\">\n"
		."			</div>\n"
		."			<div class=\"holo-field\">\n"
		."				<div class=\"holo-field-bracket\"></div>\n"
		."				<input type=\"password\" name=\"password\" placeholder=\"Jelszó\">\n"
		."			</div>\n"
		."			<div class=\"holo-buttons\">\n"
		."				<input type=\"submit\" value=\"Belépés\" class=\"holo-button\">\n"
		."			</div>\n"
		."		</form>";
	}
}

function page_changepassword(){

	$showform = true;

	if(isset($_POST['oldpassword'])){
		
		if($_POST['newpassword'] != $_POST['newpasswordconfirm']){
			echo "		<p>A megadott új jelszavak nem egyeznek.</p>\n";
		}
		else{
			$client = getsoapclient();
			$response = $client->__soapCall("changePassword", array("parameters" => array("arg0" => $_SESSION['username'], "arg1" => $_POST['oldpassword'], "arg2" => $_POST['newpassword'])));
		
			if(isset($response->return) && !$response->return){
				echo "		<p>A megadott jelenlegi jelszó nem megfelelő.</p>\n";
			}
			else {
				$showform = false;
				
				echo "		<p class=\"success\">Sikeres jelszómódosítás.</p>\n";
				page_menu();
			}
		}
	}

	if($showform){
		echo "		<form action=\"index.php?page=changepassword\" method=\"post\">\n"
		."			<div class=\"holo-field\">\n"
		."				<div class=\"holo-field-bracket\"></div>\n"
		."				<input type=\"password\" name=\"oldpassword\" placeholder=\"Jelenlegi jelszó\">\n"
		."			</div>\n"
		."			<div class=\"holo-field\">\n"
		."				<div class=\"holo-field-bracket\"></div>\n"
		."				<input type=\"password\" name=\"newpassword\" placeholder=\"Új jelszó\">\n"
		."			</div>\n"
		."			<div class=\"holo-field\">\n"
		."				<div class=\"holo-field-bracket\"></div>\n"
		."				<input type=\"password\" name=\"newpasswordconfirm\" placeholder=\"Új jelszó újra\">\n"
		."			</div>\n"
		."			<div class=\"holo-buttons\">\n"
		."				<input type=\"submit\" value=\"Jelszómódosítás\" class=\"holo-button\">\n"
		."			</div>\n"
		."		</form>";
	}
}

?>
	</div><!-- /content div -->
</div><!-- /accent div -->
</body>
</html>