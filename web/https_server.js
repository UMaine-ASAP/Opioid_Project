var express = require('express');
var bodyParser = require('body-parser');
var https = require('https');
var fs = require('fs');
var api = require('./api.js');

var app = express();

app.use(bodyParser.json()); 
app.use(bodyParser.urlencoded({ extended: true }))
app.use(api);

app.use(function(err, req, res, next) {
  res.status(err.status || 500).send("Unable to do the thing you wanted to do");
});

module.exports = app;

var db = require('./db.js');
db.connect();

var port = 4300;
app.set('port', port);

var key = fs.readFileSync('/var/www/Opioid/Opioid_Project/web/private.key');
var cert = fs.readFileSync( '/var/www/Opioid/Opioid_Project/web/certificate.crt' );
var ca = fs.readFileSync( '/var/www/Opioid/Opioid_Project/web/certificate.crt' );

var options = {
  key: key,
  cert: cert,
  ca: ca
};

var server = https.createServer(options, app);

server.listen(port);
