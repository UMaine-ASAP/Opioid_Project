var mysql = require('mysql');
var pool  = null;

exports.connect = function() {
    pool = mysql.createPool({
      host     : 'localhost',
      user     : 'root',
      password : 'AsAp4U8u',
      database : 'Opioid'
    });
  }

exports.get = function() {
  return pool;
}