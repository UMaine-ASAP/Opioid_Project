const express = require('express');
const bodyParser = require('body-parser');
const path = require('path');
const request = require('request');

const app = express();

console.log('logging')
request('http://localhost:4300/test', function(error, response, body) {
  console.log('error:', error);
  console.log(body);
});

app.set('views', path.join(__dirname, 'website/views'));
app.set('view engine', 'pug');

app.use(express.static(path.join(__dirname, 'website/public')));

app.get('/', function (req, res) {
  //res.redirect('/login')
  res.render('dashboard', {subtitle: 'Dashboard', icon: '<i class="fa fa-tachometer" aria-hidden="true"></i>'});
});

app.get('/login', function (req, res){
  res.render('login', {subtitle: 'Login', error: '', icon: ''});
});

app.post('/login', function (req, res){
  request.post('http://localhost:4300/login', {form:{username:req.body.username, passowrd: req.body.password}}, function(err, response, body) {
    console.log('error:', err);
    console.log(body);
  });
  res.render('login', {subtitle: 'Login', error: 'In Development!', icon: ''});
});

app.get('/register', function (req, res){
  res.render('register', {subtitle: 'Register', error: '', icon: ''});
});

app.post('/register', function (req, res){
  res.render('register', {subtitle: 'Register', error: 'In Development!', icon: ''});
});

app.listen(80);
