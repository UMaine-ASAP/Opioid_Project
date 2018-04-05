const express = require('express');
const bodyParser = require('body-parser');
const path = require('path');
const request = require('request');
const session  = require('express-session');
const logger = require('morgan')
const https = require('https');
const fs = require('fs');
const app = express();

////     SETTINGS    ////
const HTTPS = false;
const API_URL = "http://localhost:4300"; // "https://emac.asap.um.maine.edu:1337"; //
//// END OF SETTINGS ////

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
    res.render('dashboard', {subtitle: 'Dashboard', head: req.session.head_admin, token: true, username: req.session.username});
  } else {
    res.redirect('/login');
  }
});

// Page for managing reports
app.get('/reports', (req, res) => {
  if (req.session.token) { // redirect if no access token
    res.render('reports', {subtitle: 'Reports', head: req.session.head_admin, token: true});
  } else {
    res.redirect('/login');
  }
});

app.post('/reports', (req, res) => {
  if (req.session.token) { // redirect if no access token
    // Detect is the user is viewing or downloading the content
    if (req.body.method == "view") {
      // Get different reports based off the type
      switch (req.body.type) {
        case "audio":
          // Use the general purpose "report" function to grab the CSV file from the API
          report(req, req.body.method, req.body.type, (data) => {
            if (data.error) {
              res.render('viewReport', {subtitle: 'View Report - Error', head: req.session.head_admin, token: true, data: data.message});
            } else {
              // Convert the CSV file into HTML
              let table = csvToHTML(data.data.toString('utf8'));
              res.render('viewReport', {subtitle: 'Audio Report', head: req.session.head_admin, token: true, data: table});
            }
          });
          break;
        case "survey":
          // Get the CSV file from the API
          report(req, req.body.method, req.body.type, (data) => {
            if (data.error) {
              res.render('viewReport', {subtitle: 'View Report - Error', head: req.session.head_admin, token: true, data: data.message});
            } else {
              // Convert CSV to HTML and display.
              let table = csvToHTML(data.data.toString('utf8'));
              res.render('viewReport', {subtitle: 'Survey Report', head: req.session.head_admin, token: true, data: table});
            }
          });
          break;
        default:
          res.render('viewReport', {subtitle: 'View Report - Error', head: req.session.head_admin, token: true});
      }
    } else {
      report(req, req.body.method, req.body.type, (data) => {
        if (data.error) {
          res.render('viewReport', {subtitle: 'View Report - Error', head: req.session.head_admin, token: true, data: data.message});
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
  if (type != 'account') {
    request.get(API_URL + '/survey/report/'+type, { form: newForm }, function(err, resp, body) {
      let data = false;
      try {
        data = JSON.parse(body);
      } catch (e) {
        callback({error: true, message: "Invalid API response"});
      }
      if (err) { // if err, report it, otherwise continue
        return callback({error: true, message: err});
      } else if(data && data.error) {
        return callback({error: true, message: data.message});
      } else {
        try {
          let buff = new Buffer(data.buffer);
          return callback({error: false, data: buff});
        } catch (e) {
          return callback({error: true, message: e});
        }
      }
    });
  }
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
    request.post(API_URL + '/user/login', { form: newForm }, function(err, resp, body) {
      let data = JSON.parse(body);
      if (err) { // if err, report it, otherwise continue
        res.render('login', {subtitle: 'Login', error: err});
      } else if(data.error) {
        res.render('login', {subtitle: 'Login', error: data.message});
      } else {
        req.session.token = data.id_token;
        req.session.username = req.body.username;
        console.log(data);
        req.session.head_admin = (data.head_admin == 1) ? true : false;
        res.redirect('/');
      }
    });
  }
});

app.get('/accounts', function (req, res){
  if (!req.session.token) { // redirect if no access token
    res.redirect('/');
  } else if (!req.session.head_admin) { // redirect if not head admin
    res.redirect('/');
  } else {
    res.render('accounts', {subtitle: 'Accounts', head: req.session.head_admin, error: '', token: true});
  }
});

app.get('/accounts/add', function (req, res){
  if (!req.session.token) { // redirect if no access token
    res.redirect('/');
  } else if (!req.session.head_admin) { // redirect if not head admin
    res.redirect('/');
  } else {
    res.render('register', {subtitle: 'Add Account', head: req.session.head_admin, error: '', token: true});
  }
});

app.post('/accounts/add', function (req, res){
  if (!req.session.token) { // redirect if no access token
    res.redirect('/login');
  } else if (!req.session.head_admin) { // redirect if not head admin
    res.redirect('/');
  } else {
    // Confirming fields are not blank
    if (req.body.username == '' || req.body.password == ''){
      res.render('register', {subtitle: 'Add Account', head: req.session.head_admin, error: 'Please enter a username and password!', token: true});
    }
    // Confirming password fields match
    else if (req.body.password != req.body.confirm_password) {
      res.render('register', {subtitle: 'Add Account', head: req.session.head_admin, error: 'Passwords do not match!', token: true, username: req.body.username});
    } else {
      // create form
      let newForm = {
        username:req.body.username,
        password: req.body.password
      }
      // send to API
      request.post(API_URL + '/user/create', { form: newForm }, function(err, resp, body) {
        let data = JSON.parse(body);
        if (err) {
          res.render('register', {subtitle: 'Add Account', head: req.session.head_admin, error: err, token: true});
        } else if(data.error) {
          res.render('register', {subtitle: 'Add Account', head: req.session.head_admin, error: data.message, token: true});
        } else {
          res.render('register', {subtitle: 'Add Account', head: req.session.head_admin, error: 'Account has been registered!', token: true});
        }
      });
    }
  }
});

// Page for managing accounts
app.get('/accounts/manage', (req, res) => {
  if (req.session.token) { // redirect if no access token
    if (req.session.head_admin) {
      // User IS a head admin

      adminManage(req, 'get', (data) => {
        if (data.error) {
          res.render('manage', {subtitle: 'Manage Accounts - Error', head: req.session.head_admin, token: true, data: data.message});
        } else {
          let table = "";
          for (let i = 0; i < data.data.length; i++) {
            table += "<opt class='admin'><span style='width: calc(50% - 40px); text-align: left;'>";
            table += "<name class='info'>" + data.data[i].id + "</name>";
            table += "<name class='info'>" + data.data[i].username + "</name>";
            table += "<name class='info'>" + (data.data[i].head_admin ? "True" : "False") + "</name></span>";
            table += "<span><a href='/accounts/manage/" + data.data[i].id + "'>Edit</a>"
            table += "<a href='/accounts/remove/"+data.data[i].id+"' data-confirm='Are you sure to delete this user?' class='danger'>Remove</a></span></opt>";
          }

          res.render('manage', {subtitle: 'Manage Accounts', head: req.session.head_admin, token: true, data: table});
        }
      });
    } else {
      // User IS NOT a head admin
      res.redirect('/');
    }
  } else {
    res.redirect('/login');
  }
});

// Page for editing an account
app.get('/accounts/manage/:id', (req, res) => {
  if (req.session.token) { // redirect if no access token
    if (req.session.head_admin) {
      // User IS a head admin

      adminManage(req, 'edit', (data) => {
        if (data.error) {
          res.render('edit', {subtitle: 'Manage Accounts - Error', head: req.session.head_admin, token: true, data: data.message});
        } else {
          res.render('edit', {subtitle: 'Manage Accounts', head: req.session.head_admin, token: true, data: data.data});
        }
      }, req.params.id);
    } else {
      // User IS NOT a head admin
      res.redirect('/');
    }
  } else {
    res.redirect('/login');
  }
});

// Page for editing an account
app.post('/accounts/manage/:id', (req, res) => {
  if (req.session.token) { // redirect if no access token
    if (req.session.head_admin) {
      // User IS a head admin

      // Tesing the data
      console.log(req.body);
      if (req.body.password === req.body.confirm_password) {
        // if passwords match send data

        // form must go [username, head_admin, password]
        // Password set to false if it is left blank
        let form = {
          username: req.body.username,
          head_admin: (req.body.head_admin == "on") ? 1 : 0,
          password: (req.body.password != "") ? req.body.password : false
        };

        // send to server
        adminManage(req, 'update', (data) => {
          console.log(data);
          if (data.error) {
            res.render('edit', {subtitle: 'Manage Accounts - Error', head: req.session.head_admin, token: true, data: {}, error: data.message});
          } else {
            res.render('edit', {subtitle: 'Manage Accounts', head: req.session.head_admin, token: true, data: data.data, error: "User was updated"});
          }
        }, req.params.id, form);
      } else {
        adminManage(req, 'edit', (data) => {
          res.render('edit', {subtitle: 'Manage Accounts - Error', head: req.session.head_admin, token: true, data: data.data, error: "Passwords do not match!"});
        }, req.params.id);
      }
    } else {
      // User IS NOT a head admin
      res.redirect('/');
    }
  } else {
    res.redirect('/login');
  }
});

// Page for editing an account
app.get('/accounts/remove/:id', (req, res) => {
  if (req.session.token) { // redirect if no access token
    if (req.session.head_admin) {
      // User IS a head admin
      adminManage(req, 'remove', (data) => {
        if (data.error) {
          console.log(data.message)
          res.render('manage', {subtitle: 'Manage Accounts - Error', head: req.session.head_admin, token: true, error: data.message});
        } else {
          res.redirect('/accounts/manage');
        }
      }, req.params.id);
    } else {
      // User IS NOT a head admin
      res.redirect('/');
    }
  } else {
    res.redirect('/login');
  }
});

// The all-in-one admin management function! Just send in the parameters needed and you are done!
const adminManage = (req, method, callback, id, form) => {
  // Send data
  let newForm = {
    token:req.session.token,
    id: id || null,
    form: form || null
  }
  // Send post request to server
  request.post(API_URL + '/user/'+method, { form: newForm }, function(err, resp, body) {
    let data = false;
    // try to gather data if it is there
    try {
      data = JSON.parse(body);
    } catch (e) {
      return callback({error: true, message: "Invalid API response"});
    }
    // if err, report it, otherwise continue
    if (err) {
      return callback({error: true, message: err});
    } else if(data && data.error) {
      return callback({error: true, message: data.message});
    } else {
      // If everything is all good continue
      return callback({error: false, data: data});
    }
  });
}

app.get('/logout', (req, res) => {
  if (req.session.token) { // redirect if no access token
    delete req.session.token;
    delete req.session.username;
    delete req.session.head_admin;
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
            head: req.session.head_admin,
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
        head: req.session.head_admin,
        message: err.message,
        error: { error: { status: err.status } }
    });
});

if (HTTPS) {

  // Setup for SSL Cert
  app.set('port', 443);

  /* Grab the Certifications */
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

  // Serve The Server over 443
  var server = https.createServer(options, app);

  server.listen(app.get('port'));
} else {
  // Serve the server over 80
  app.listen(80);
}
