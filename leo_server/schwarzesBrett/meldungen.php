<?php

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("Connection failed: ".$db->connect_error);

	$date = date("Y-m-d");

	$query = "SELECT Titel, Adressat, Inhalt, UNIX_TIMESTAMP(Erstelldatum) as Erstell, UNIX_TIMESTAMP(Ablaufdatum) as Ablauf FROM Eintr�ge WHERE Ablaufdatum >= '".$date."'";
	$result = $db->query($query);
	if ($result !== false)
		while ($row = $result->fetch_assoc())
			echo utf8_encode($row['Titel']) . ";" . utf8_encode($row['Adressat']) . ";" . utf8_encode($row['Inhalt']) . ";" . $row['Erstell'] . ";" . $row['Ablauf'] . "_next_";
	else
		die("error in query");
		
	$db->close();

?>