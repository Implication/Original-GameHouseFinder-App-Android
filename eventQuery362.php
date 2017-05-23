<?php
$user = "appUser";
$sql = new mysqli("107.184.164.127:1543","appUser","setPass789because!","sys");
if (mysqli_connect_errno()) {
  printf("Connect failed: %s\n", mysqli_connect_error());
  exit;
}
$query = "SELECT * FROM Event";
$result = $sql->query($query);
if (!$result) {
  printf("Query failed: %s\n", $mysqli->error);
  exit;
}
while($row = $result->fetch_row()) {
  $rows[]=$row;
}
$result->close();
$sql->close();
print json_encode($rows);
?>
