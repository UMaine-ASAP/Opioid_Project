const express = require('express');
const bodyParser = require('body-parser');
const path = require('path');

const app = express();

app.set('views', path.join(__dirname, 'website/views'));
app.set('view engine', 'pug');

app.use(express.static(path.join(__dirname, 'website/public')));

app.get('/', function (req, res) {
  res.render('layout', {subtitle: 'Home'});
});

app.listen(80);
