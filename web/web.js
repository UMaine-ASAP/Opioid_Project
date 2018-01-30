const express = require('express');
const bodyParser = require('body-parser');
const path = require('path');
const request = require('request');

const app = express();

app.set('views', path.join(__dirname, 'website/views'));
app.set('view engine', 'pug');

app.use(express.static(path.join(__dirname, 'website/public')));
app.use(bodyParser.urlencoded({
    extended: true
}));
app.use(bodyParser.json());

app.get('/', function (req, res) {
  //res.redirect('/login')
  res.render('dashboard', {subtitle: 'Dashboard', icon: '<i class="fa fa-tachometer" aria-hidden="true"></i>'});
});

app.get('/login', function (req, res){
  res.render('login', {subtitle: 'Login', error: '', icon: ''});
});

app.post('/login', function (req, res){
  request.post('http://localhost:4300/user/login', {form:{username:req.body.username, password: req.body.password}}, function(err, resp, body) {
    let data = JSON.parse(body);
    if(err) {
      res.render('login', {subtitle: 'Login', error: err, icon: ''});
    } else if(data.error) {
      res.render('login', {subtitle: 'Login', error: data.messege, icon: ''});
    } else {
      res.render('login', {subtitle: 'Login', error: 'In Development!', icon: ''});
    }
  });
});

app.get('/register', function (req, res){
  res.render('register', {subtitle: 'Register', error: '', icon: ''});
});

app.post('/register', function (req, res){
  res.render('register', {subtitle: 'Register', error: 'In Development!', icon: ''});
});

app.listen(80);
