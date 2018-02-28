const express = require('express');
const bodyParser = require('body-parser');
const path = require('path');
const request = require('request');
const session  = require('express-session');
const logger = require('morgan')
const https = require('https');
const fs = require('fs');
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

app.post('/reports', (req, res) => {
  if (req.session.token) { // redirect if no access token
    if (req.body.method == "view") {
      switch (req.body.type) {
        case "audio":
          report(req, req.body.method, req.body.type, (data) => {
            if (data.error) {
              res.render('viewReport', {subtitle: 'View Report - Error', token: true, data: data.messege});
            } else {
              let table = csvToHTML(data.data.toString('utf8'));
              res.render('viewReport', {subtitle: 'Audio Report', token: true, data: table});
            }
          });
          break;
        case "survey":
          report(req, req.body.method, req.body.type, (data) => {
            if (data.error) {
              res.render('viewReport', {subtitle: 'View Report - Error', token: true, data: data.messege});
            } else {
              let table = csvToHTML(data.data.toString('utf8'));
              res.render('viewReport', {subtitle: 'Survey Report', token: true, data: table});
            }
          });
          break;
        default:
          res.render('viewReport', {subtitle: 'View Report - Error', token: true});
      }
    } else {
      report(req, req.body.method, req.body.type, (data) => {
        if (data.error) {
          res.render('viewReport', {subtitle: 'View Report - Error', token: true, data: data.messege});
        } else {
          let d = new Date();
          let curr_date = d.getDate();
          let curr_month = d.getMonth() + 1;
          let curr_year = d.getFullYear();
          let formet = curr_month + "-" + curr_date + "-" + curr_year;
          res.setHeader('Content-disposition', 'attachment; filename='+req.body.type+'-report-'+format+'.csv');
          res.setHeader('Content-type', 'text/csv');
          res.send(data.data.toString('utf8'));
        }
      });
    }
  } else {
    res.redirect('/login');
  }
});

var report = (req, method, type, callback) => {
  let newForm = {
    token:req.session.token
  }
  // sending data to API
  request.get('https://emac.asap.um.maine.edu:1337/survey/report/'+type, { form: newForm }, function(err, resp, body) {
    let data = false;
    try {
      data = JSON.parse(body);
    } catch (e) {}
    if (err) { // if err, report it, otherwise continue
      return callback({error: true, messege: err});
    } else if(data && data.error) {
      return callback({error: true, messege: data.messege});
    } else {
      try {
        let buff = new Buffer(data.buffer);
        return callback({error: false, data: buff});
      } catch (e) {
        return callback({error: true, messege: e});
      }
    }
  });
}

/* Converts CSV given from API to a table */
var csvToHTML = (data) => {
  // Create an array of each row of data
  let allRows = data.split(/\r?\n|\r/);
  // Remove the last row (it's always empty);
  allRows.pop();

  // Create a string containing the table
  let table = '<table>';
  for (let singleRow = 0; singleRow < allRows.length; singleRow++) {
    if (singleRow === 0) {
      table += '<thead>';
      table += '<tr>';
    } else {
      table += '<tr>';
    }
    let rowCells = allRows[singleRow].split(',');
    for (let rowCell = 0; rowCell < rowCells.length; rowCell++) {
      if (singleRow === 0) {
        table += '<th>';
        table += rowCells[rowCell];
        table += '</th>';
      } else {
        table += '<td>';
        table += rowCells[rowCell];
        table += '</td>';
      }
    }
    if (singleRow === 0) {
      table += '</tr>';
      table += '</thead>';
      table += '<tbody>';
    } else {
      table += '</tr>';
    }
  }
  table += '</tbody>';
  table += '</table>';

  // return the table to be displayed
  return table;
}

app.get('/login', function (req, res){
  if (req.session.token) { // redirect if no access token
    res.redirect('/');
  } else {
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
    request.post('https://emac.asap.um.maine.edu:1337/user/login', { form: newForm }, function(err, resp, body) {
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
      request.post('https://emac.um.maine.edu:1337/user/create', { form: newForm }, function(err, resp, body) {
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
    delete req.session.username;
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

//app.listen(80);

/* Setup Green-Lock for SSL Cert */

//require('greenlock-express').create({
//  server: 'staging',
//  email: 'dylan.bulmer@maine.edu',
//  agreeTos: true,
//  approveDomains: [ 'emac.asap.um.maine.edu' ],
//  app: app
//}).listen(443);
var port = 443;
app.set('port', port);

var key = fs.readFileSync('/var/www/Opioid/Opioid_Project/web/private.key');
var cert = fs.readFileSync( '/var/www/Opioid/Opioid_Project/web/certificate.crt' );


var options = {
  key: key,
  cert: cert,
  ca: [
    fs.readFileSync('/var/www/Opioid/Opioid_Project/web/isgrootx1.pem'),
    fs.readFileSync('/var/www/Opioid/Opioid_Project/web/lets-encrypt-x3-cross-signed.pem')
  ]
};


var server = https.createServer(options, app);

server.listen(port);
