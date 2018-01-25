const express = require('express');
const bodyParser = require('body-parser');
const path = require('path');

const app = express();

app.set('views', path.join(__dirname, 'website/views'));
app.set('view engine', 'pug');

app.use(express.static(path.join(__dirname, 'website/public')));

app.get('/', function (req, res) {
  //res.redirect('/login')
  res.render('dashboard', {subtitle: 'Dashboard <i class="fa fa-tachometer" aria-hidden="true"></i>'});
});

app.get('/login', function (req, res){
  res.render('login', {subtitle: 'Login', error: ''});
});

app.post('/login', function (req, res){
  res.render('login', {subtitle: 'Login', error: 'In Development!'});
});

app.get('/register', function (req, res){
  res.render('register', {subtitle: 'Register', error: ''});
});

app.post('/register', function (req, res){
  res.render('register', {subtitle: 'Register', error: 'In Development!'});
});

app.listen(80);
