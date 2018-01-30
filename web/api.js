var express = require('express'),
_  = require('lodash');
var db = require("./db.js");
var ejwt = require('express-jwt');
var config  = require('./config.json');
var bcrypt = require('bcrypt-nodejs');
var jwt = require('jsonwebtoken');

var app = module.exports = express.Router();

var jwtCheck = ejwt({
  secret: config.secretKey
});

app.get('/test', function (req, res) {
  return res.send(req.body);
});

app.post('/test', function (req, res) {
  return res.send(req.body);
});

app.get('/resources', function (req, res) {
  var query = "SELECT * FROM ??";
    var table = ["resources"];

    db.get().query(query, table, function(err, rows) {
      if(err){ return res.status(400).send({"error": true});}
        res.send(rows);
    });
});

app.use('/resources', jwtCheck);

app.post('/resources', function (req, res) {
  var query = "INSERT INTO ?? (url, title, description) VALUES (?, ?, ?)";
  var table = ["resources", req.body.url, req.body.title, req.body.description];

  db.get().query(query, table, function(err, rows) {
    if(err){ return res.status(400).send({"error": true});}
      res.send({"error": false});
  });
});


// USER ENDPOINTS


function createToken(user) {
  return jwt.sign(_.omit(user, 'password'), config.secretKey, { expiresIn: "365 days" });
}

function getUserDB(username, done) {
  db.get().query('SELECT * FROM admin WHERE username = ? LIMIT 1', [username], function(err, rows, fields) {
    if (err) throw err;
    done(rows[0]);
  });
}
app.post('/user/create', function(req, res) {  
  if (!req.body.username || !req.body.password) {
    return res.status(400).send("You must send the username and the password");
  }
  getUserDB(req.body.username, function(user){
    if(!user) {
      user = {
        username: req.body.username,
        password: req.body.password // password: bcrypt.hashSync(req.body.password)
      };
      db.get().query('INSERT INTO admin SET ?', [user], function(err, result){
        if (err) throw err;
        newUser = {
          id: result.insertId,
          username: user.username,
          password: user.password
        };
        res.status(201).send({
          id_token: createToken(newUser)
        });
      });
    }
    else res.status(400).send("A user with that username already exists");
  });
});

app.post('/user/login', function(req, res) {
  if (!req.body.username || !req.body.password) {
    return res.status(400).send({"error": true, "message": "Must enter in password and username"});
  }
  getUserDB(req.body.username, function(user){
    if (!user) {
      return res.status(400).send({"error": true, "message": "Username doesnt exist"});
    }
    if (user.password !== req.body.password) { // if (!bcrypt.compareSync(req.body.password , user.password) ) {
      return res.status(400).send({"error": true, "message": "The username or password don't match"});
    }
    res.send({
      id_token: createToken(user)
    });
  });
});

app.get('/user/check/:username', function(req, res) {
  if (!req.params.username) {
    return res.status(400).send("You must send a username");
  }
  getUserDB(req.params.username, function(user){
    if(!user) res.status(201).send({username: "OK"});
    else res.status(400).send("A user with that username already exists");
  });
});


app.post('/register_device', function (req, res) {
  var query = "INSERT INTO ?? (device_id) VALUES (?)";
  var table = ["users", req.body.device_id];

  db.get().query(query, table, function(err, rows) {
    if(err){ return res.status(400).send({"error": true});}
      res.send({"error": false});
  });
});

app.get('/register_device/:device_id', function (req, res) {
  var query = "SELECT * FROM ?? WHERE device_id = ?";
  var table = ["users", req.params.device_id];

  db.get().query(query, table, function(err, rows) {
    if(err){ return res.status(400).send({"error": true});}
    if(rows.length <= 0){
      res.send({"exists": false});
    }else{
      res.send({"exists": true});
    }
  });
});

app.post('/audio_history', function (req, res) {
  var query = "INSERT INTO ?? (user_id, track_number, completion_status, creation_date) VALUES ((SELECT id FROM users WHERE device_id = ? LIMIT 1), ?, ?, ?)";
  var table = ["audio_report", req.body.device_id, req.body.track_number, req.body.completion_status, req.body.creation_date];

  db.get().query(query, table, function(err, rows) {
    if(err){ return res.status(400).send({"error": true});}
      res.send({"error": false});
  });
});

app.get('/audio_history/:device_id', function (req, res) {
  var query = "SELECT audio_report.track_number, audio_report.completion_status, audio_report.creation_date FROM audio_report INNER JOIN users ON audio_report.user_id = users.id WHERE users.device_id = ?";
  var table = [req.params.device_id];

  db.get().query(query, table, function(err, rows) {
    if(err){ return res.status(400).send({"error": true});}
    res.send(rows);
  });
});

app.post('/survey', function (req, res) {
  var query = "INSERT INTO ?? (user_id, resource_id, creation_date, rating) VALUES ((SELECT id FROM users WHERE device_id = ? LIMIT 1), ?, ?, ?)";
  var table = ["survey_responces", req.body.device_id, req.body.resource_id, req.body.creation_date, req.body.rating];

  db.get().query(query, table, function(err, rows) {
    if(err){ return res.status(400).send({"error": true});}
      res.send({"error": false});
  });
});

app.get('/survey/:device_id', function (req, res) {
  var query = "SELECT survey_responces.resource_id, survey_responces.rating, survey_responces.creation_date FROM survey_responces INNER JOIN users ON survey_responces.user_id = users.id WHERE users.device_id = ?";
  var table = [req.params.device_id];

  db.get().query(query, table, function(err, rows) {
    if(err){ return res.status(400).send({"error": true});}
    res.send(rows);
  });
});
