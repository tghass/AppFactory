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
// app.get('/comment/find/:id', function(req,res) {
	// var queryEname = "select * from comment ";
	// queryEname += "where id = ?";
	// con.query(queryEname, req.params.id, function(err,rows0,fields) {
		// if (err) throw err;
		// rows0.forEach(function(row) {
			// query = "select p.id, p.CreatedBy ";
			// query += "from post as p inner join comment as c ";
			// query += "where p.Id = c.PostTo and c.id = ?";
			// con.query(query, req.params.id, function(err,rows1,fields) {
				// if (err) throw err;
				// row.PostTo = rows1[0];
				// rows1.forEach(function(row) {
					// query = "select u.id, u.name ";
					// query += "from user as u inner join post as p inner join comment as c ";
					// query += "where u.id = p.CreatedBy and p.Id = c.PostTo and c.id = ?";
					// con.query(query, req.params.id, function (err, rows2, fields) {
						// row.CreatedBy = rows2[0];
						// rows0.forEach(function(row) {
							// query = "select u.id, u.name ";
							// query += "from comment as c inner join user as u ";
							// query += "where c.CreatedBy = u.ID and c.id = ?";
							// con.query(query, req.params.id, function (err, rows3, fields) {
								// row.CreatedBy = rows3[0];
							
								// res.jsonp(rows0);
							// });
						// });
					// });
				// });
			// });
		// });
		
	// });
// });
app.get('/User/find/:id', function(req,res) {
	var query = "select U.ID , U.PictureUrl , U.Info , U.Name  from User as U where id = ?";
	con.query(query, req.params.id, function(err, rows0,fields) {
		if(err) throw err;
				res.jsonp(rows0);
		});
});

app.get('/Post/find/:id', function(req,res) {
	var query = "select P.ID , P.CreatedBy , P.Content , P.CreateDate  from Post as P where id = ?";
	con.query(query, req.params.id, function(err, rows0,fields) {
		if(err) throw err;
		rows0.forEach(function(row) { 
			query = "select U.ID , U.PictureUrl , U.Info , U.Name ";
			query += "from  User as U  inner join Post as P ";
			query += "where  U.id = P.CreatedBy and P.id = ?";
			con.query(query, req.params.id, function(err, rows1,fields) {
				if (err) throw err;
				row.CreatedBy = rows1[0];
				res.jsonp(rows0);
			});
		});
	});
});
app.get('/Friendship/find/:id', function(req,res) {
	var query = "select F.ID , F.BeganDate , F.Friendship1 , F.Friendship2  from Friendship as F where id = ?";
	con.query(query, req.params.id, function(err, rows0,fields) {
		if(err) throw err;
		rows0.forEach(function(row) { 
			query = "select U.ID , U.PictureUrl , U.Info , U.Name ";
			query += "from  User as U  inner join Friendship as F ";
			query += "where  U.id = F.Friendship1 and F.id = ?";
			con.query(query, req.params.id, function(err, rows1,fields) {
				if (err) throw err;
				row.Friendship1 = rows1[0];
				rows0.forEach(function(row) { 
					query = "select U.ID , U.PictureUrl , U.Info , U.Name ";
					query += "from  User as U  inner join Friendship as F ";
					query += "where  U.id = F.Friendship1 and F.id = ?";
					con.query(query, req.params.id, function(err, rows2,fields) {
						if (err) throw err;
						row.Friendship1 = rows2[0];
						rows0.forEach(function(row) { 
							query = "select U.ID , U.PictureUrl , U.Info , U.Name ";
							query += "from  User as U  inner join Friendship as F ";
							query += "where  U.id = F.Friendship2 and F.id = ?";
							con.query(query, req.params.id, function(err, rows3,fields) {
								if (err) throw err;
								row.Friendship2 = rows3[0];
								rows0.forEach(function(row) { 
									query = "select U.ID , U.PictureUrl , U.Info , U.Name ";
									query += "from  User as U  inner join Friendship as F ";
									query += "where  U.id = F.Friendship2 and F.id = ?";
									con.query(query, req.params.id, function(err, rows4,fields) {
										if (err) throw err;
										row.Friendship2 = rows4[0];
												res.jsonp(rows0);
											});
										});
									});
								});
							});
						});
					});
				});
				});
				});


app.get('/Comment/find/:id', function(req,res) {
	var query = "select C.ID , C.CreatedBy , C.Content , C.CreateDate , C.PostTo  from Comment as C where id = ?";
	con.query(query, req.params.id, function(err, rows0,fields) {
		if(err) throw err;
		rows0.forEach(function(row) { 
			query = "select U.ID , U.PictureUrl , U.Info , U.Name ";
			query += "from  User as U  inner join Comment as C ";
			query += "where  U.id = C.CreatedBy and C.id = ?";
			con.query(query, req.params.id, function(err, rows1,fields) {
				if (err) throw err;
				row.CreatedBy = rows1[0];
				rows0.forEach(function(row) { 
					query = "select P.ID , P.CreatedBy , P.Content , P.CreateDate ";
					query += "from  Post as P  inner join Comment as C ";
					query += "where  P.id = C.PostTo and C.id = ?";
					con.query(query, req.params.id, function(err, rows2,fields) {
						if (err) throw err;
						row.PostTo = rows2[0];
						rows2.forEach(function(row) { 
							query = "select U.ID , U.PictureUrl , U.Info , U.Name ";
							query += "from  Post as P  inner join Comment as C  inner join  User as U ";
							query += "where  P.id = C.PostTo and C.id = ? and  U.id = P.CreatedBy";
							con.query(query, req.params.id, function(err, rows3,fields) {
								if (err) throw err;
								row.CreatedBy = rows3[0];
						res.jsonp(rows0);
							});
						});
					});
				});
			});
		});
	});
});

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
