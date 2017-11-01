var mysql = require('mysql');
var pool  = null;

exports.connect = function() {
    pool = mysql.createPool({
      host     : '',
      user     : '',
      password : '',
      database : ''
    });
  }

exports.get = function() {
  return pool;
}
