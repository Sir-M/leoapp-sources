<?php
	if ($_GET['key'] != 5453)
		die("permission denied!");

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("Connection failed: ".$db->connect_error);

	// mitgegebene Werte über get: userid, voteid, grund
	
	$userid = $_GET['userid'];
	$voteid = $_GET['voteid'];
	$grund = $_GET['grund'];
	$date = date("Y-m-d H:i:s");

	//nachricht wird gesendet
	$query = "INSERT INTO Vote VALUES (".$voteid.", ".$userid.", '".$date."', '".$grund."')";
	$result = $db->query($query);
	if ($result === false)
		die("error in query");

	$db->close();
?>