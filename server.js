var express = require('express');
var bodyParser = require('body-parser');
http = require('http');

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

var server = http.createServer(app);

server.listen(port);