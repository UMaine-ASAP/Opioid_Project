const express = require('express');
const bodyParser = require('body-parser');
const path = require('path');
const request = require('request');
const session  = require('express-session');
const logger = require('morgan')

const app = express();

// Setting up page renderer
app.set('views', path.join(__dirname, 'website/views'));
app.set('view engine', 'pug');

// Setup logger
app.use(logger(':remote-addr :method :url :status - :response-time ms'));

// Setting up session
app.use(session({
    secret: 'keyboard cat',
    resave: false,
    saveUninitialized: true
}));

// Creating a public path for static files
app.use(express.static(path.join(__dirname, 'website/public')));

// Setting up JSON Parser
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

// Main page
app.get('/', function (req, res) {
  if (req.session.token) { // redirect if no access token
    res.render('dashboard', {subtitle: 'Dashboard', token: true, username: req.session.username});
  } else {
    res.redirect('/login');
  }
});

app.get('/reports', (req, res) => {
  if (req.session.token) { // redirect if no access token
    res.render('reports', {subtitle: 'Reports', token: true});
  } else {
    res.redirect('/login');
  }
});

app.get('/login', function (req, res){
  if (req.session.token) { // redirect if no access token
    res.redirect('/');
  } else {
    req.session.token =  true; // current work-around for login system
    req.session.username = "Test User";
    res.render('login', {subtitle: 'Login', error: ''});
  }
});

app.post('/login', function (req, res){
  if (req.session.token) { // redirect if no access token
    res.redirect('/');
  } else {
    let newForm = {
      username:req.body.username,
      password: req.body.password
    }
    // sending data to API
    request.post('http://localhost:4300/user/login', { form: newForm }, function(err, resp, body) {
      let data = JSON.parse(body);
      if (err) { // if err, report it, otherwise continue
        res.render('login', {subtitle: 'Login', error: err});
      } else if(data.error) {
        res.render('login', {subtitle: 'Login', error: data.messege});
      } else {
        req.session.token = data.id_token;
        req.session.username = req.body.username;
        res.redirect('/');
      }
    });
  }
});

app.get('/accounts', function (req, res){
  if (!req.session.token) { // redirect if no access token
    res.redirect('/');
  } else {
    res.render('register', {subtitle: 'Add Account', error: '', token: true});
  }
});

app.post('/accounts', function (req, res){
  if (!req.session.token) { // redirect if no access token
    res.redirect('/');
  } else {
    // Confirming fields are not blank
    if (req.body.username == '' || req.body.password == ''){
      res.render('register', {subtitle: 'Add Account', error: 'Please enter a username and password!', token: true});
    }
    // Confirming password fields match
    else if (req.body.password != req.body.confirm_password) {
      res.render('register', {subtitle: 'Add Account', error: 'Passwords do not match!', token: true, username: req.body.username});
    } else {
      // create form
      let newForm = {
        username:req.body.username,
        password: req.body.password
      }
      // send to API
      request.post('http://localhost:4300/user/create', { form: newForm }, function(err, resp, body) {
        let data = JSON.parse(body);
        if (err) {
          res.render('register', {subtitle: 'Add Account', error: err, token: true});
        } else if(data.error) {
          res.render('register', {subtitle: 'Add Account', error: data.messege, token: true});
        } else {
          res.render('register', {subtitle: 'Add Account', error: 'Account has been registered!', token: true});
        }
      });
    }
  }
});

app.get('/logout', (req, res) => {
  if (req.session.token) { // redirect if no access token
    delete req.session.token;
  }
  res.redirect('/');
});

// catch 404 and forward to error handler
app.use(function (req, res, next) {
    var err = new Error('Not Found');
    err.status = 404;
    next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
    app.use(function (err, req, res, next) {
        if (!err.status) { err.status =  500 }
        res.status(err.status);
        res.render('error', {
            subtitle: 'Error',
            message: err.message,
            error: err
        });
    });
}

// production error handler
// no stacktraces leaked to user
app.use(function (err, req, res, next) {
    if (!err.status) { err.status =  500 }
    res.status(err.status);
    res.render('error', {
        subtitle: 'Error',
        message: err.message,
        error: { error: { status: err.status } }
    });
});

app.listen(80);
