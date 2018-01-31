const express = require('express');
const bodyParser = require('body-parser');
const path = require('path');
const request = require('request');
const session  = require('express-session');

const app = express();

app.set('views', path.join(__dirname, 'website/views'));
app.set('view engine', 'pug');

app.use(session({
    secret: 'keyboard cat',
    resave: false,
    saveUninitialized: true
}));
app.use(express.static(path.join(__dirname, 'website/public')));
app.use(bodyParser.urlencoded({
    extended: true
}));
app.use(bodyParser.json());

app.get('/', function (req, res) {
  if (req.session.token) {
    res.render('dashboard', {subtitle: 'Dashboard', icon: '<i class="fa fa-tachometer" aria-hidden="true"></i>'});
  } else {
    res.redirect('/login');
  }
});

app.get('/login', function (req, res){
  if (req.session.token) {
    res.redirect('/');
  } else {
    res.render('login', {subtitle: 'Login', error: '', icon: ''});
  }
});

app.post('/login', function (req, res){
  if (req.session.token) {
    res.redirect('/');
  } else {
    let newForm = {
      username:req.body.username,
      password: req.body.password
    }
    request.post('http://localhost:4300/user/login', { form: newForm }, function(err, resp, body) {
      let data = JSON.parse(body);
      if (err) {
        res.render('login', {subtitle: 'Login', error: err, icon: ''});
      } else if(data.error) {
        res.render('login', {subtitle: 'Login', error: data.messege, icon: ''});
      } else {
        req.session.token = data.id_token;
        res.redirect('/');
      }
    });
  }
});

app.get('/register', function (req, res){
  if (req.session.token) {
    res.redirect('/');
  } else {
    res.render('register', {subtitle: 'Register', error: '', icon: ''});
  }
});

app.post('/register', function (req, res){
  if (req.session.token) {
    res.redirect('/');
  } else {
    // Confirming passwords match
    if (req.body.password === req.body.confirm_password) {
      // create form
      let newForm = {
        username:req.body.username,
        password: req.body.password
      }
      // send to API
      request.post('http://localhost:4300/user/create', { form: newForm }, function(err, resp, body) {
        let data = JSON.parse(body);
        if (err) {
          res.render('register', {subtitle: 'Register', error: err, icon: ''});
        } else if(data.error) {
          res.render('register', {subtitle: 'Register', error: data.messege, icon: ''});
        } else {
          res.render('register', {subtitle: 'Register', error: 'In Development!', icon: ''});
        }
      });
    } else {
      res.render('register', {subtitle: 'Register', error: 'Passwords do not match!', icon: ''});
    }
  }
});

app.get('/device', (req, res) => {
  if (req.session.token) {
    res.render('device', {subtitle: 'Register Device', error: '', icon: ''});
  } else {
    res.redirect('/login');
  }
});

app.post('/device', (req, res) => {
  if (req.session.token) {
    if (req.body.device_id) {
      // create form
      let newForm = {
        device_id: req.body.device_id
      }
      // send to API
      request.post('http://localhost:4300/register_device', { form: newForm }, function(err, resp, body) {
        let data = JSON.parse(body);
        if (err) {
          res.render('device', {subtitle: 'Register Device', error: err, icon: ''});
        } else if(data.error) {
          res.render('device', {subtitle: 'Register Device', error: "Device could not be added!", icon: ''});
        } else {
          res.render('device', {subtitle: 'Register Device', error: 'Device has been added!', icon: ''});
        }
      });
    } else {
      res.render('device', {subtitle: 'Register Device', error: 'Please input a device ID', icon: ''});
    }
  } else {
    res.redirect('/login');
  }
});

app.get('/logout', (req, res) => {
  if (req.session.token) {
    delete req.session.token;
  }
  res.redirect('/');
});

app.listen(80);
