//sitepoint.com/using-node-mysql-javascript-client/

var express= require('express');
var app = express();

//not necessery any more: app.enable("jsonp callback");
var mysql = require('mysql');

//establish connection to the database	
var con = mysql.createConnection({
	host : 'localhost',
	user : 'root',
	password : 'hasskafka',
	database : 'employee'
});

//connect to the database
con.connect(function(err) {
	if (err) {
		console.log('Error connection to db');
		return;
	}
	console.log('Connection established.');
});


//when 
app.get('/employee/find/:id', function(req,res) {
		var queryEname = "select * from employee e ";
		queryEname += "where e.id = ?";
			con.query(queryEname, req.params.id, function(err,rows,fields) {
				if (err) throw err;
				res.jsonp(rows);
	});
});

app.get('/employee/find/name/:name', function(req,res) {
		var queryEname = "select * from employee e ";
		queryEname += "where concat(concat(e.firstName, ' '), e.lastName) LIKE ? ";
		queryEname += "GROUP BY e.id ORDER BY e.lastName, e.firstName";
		con.query(queryEname, "%"+req.params.name+ "%",function(err,rows,fields) {
				if (err) throw err;
				res.jsonp(rows);
	});
});



var server = app.listen(3000, function() {
	console.log("We have started our server on port 3000");
	});
