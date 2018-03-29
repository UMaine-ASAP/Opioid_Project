var mysql = require('mysql');
var pool  = null;

exports.connect = function() {
	pool = mysql.createPool({
		host     : 'localhost',
      	user     : 'root',
      	password : 'George@1972',
      	database : 'Opioid',
      	port     : '3306',
      	charset: 'utf8mb4'
  	});
}

exports.get = function() {
  return pool;
}
