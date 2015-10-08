//sitepoint.com/using-node-mysql-javascript-client/

var express= require('express');
var app = express();

app.enable("jsonp callback");
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
app.get('/search/', function(req,res) {

		//query db differently depending on input param
		if (typeof req['query'].ename != 'undefined') {
	
			var queryEname = "SELECT e.id, e.firstName, e.lastName, e.title, e.pic, count(r.id) reportCount " +
                "FROM employee e LEFT JOIN employee r ON r.managerId = e.id " +
                "WHERE concat(concat(e.firstName,' '),e.lastName) LIKE ? "+
                "GROUP BY e.id ORDER BY e.lastName, e.firstName";
			con.query(queryEname, "%"+[req['query'].ename]+"%", function(err,rows,fields) {
				if (err) throw err;
				res.jsonp(rows);
			});
		}
		else if (typeof req['query'].eid != 'undefined') {
				  
				//Caution: all '?' are replaced, including those in 
				//input strings.
				var queryEid = "SELECT e.id, e.firstName, e.lastName, e.title, e.city, e.officePhone, e.cellPhone, e.email, e.pic, e.managerId, m.firstName managerFirstName, m.lastName managerLastName, count(r.id) reportCount " +
                    "FROM employee e " +
                    "LEFT JOIN employee r ON r.managerId = e.id " +
                    "LEFT JOIN employee m ON e.managerId = m.id " +
                    "WHERE e.id=?"; 
				con.query(queryEid, [req['query'].eid], function(err,rows,fields) {
					if (err) throw err;
					res.jsonp(rows);
			});
		}
		
});

var server = app.listen(3000, function() {
	console.log("We have started our server on port 3000");
	});
