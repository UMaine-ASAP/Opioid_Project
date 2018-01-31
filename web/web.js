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
    res.render('dashboard', {subtitle: 'Dashboard', icon: '<i class="fa fa-tachometer" aria-hidden="true"></i>', token: true});
  } else {
    res.redirect('/login');
  }
});

app.get('/login', function (req, res){
  if (req.session.token) { // redirect if no access token
    res.redirect('/');
  } else {
    req.session.token =  true;
    res.render('login', {subtitle: 'Login', error: '', icon: ''});
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
  if (req.session.token) { // redirect if there is an access token
    res.redirect('/');
  } else {
    res.render('register', {subtitle: 'Register', error: '', icon: ''});
  }
});

app.post('/register', function (req, res){
  if (req.session.token) { // redirect if there is an access token
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
            icon: '',
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
        icon: '',
        message: err.message,
        error: { error: { status: err.status } }
    });
});

app.listen(80);
