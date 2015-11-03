
//Express is a minimal and flexible Node.js web application framework that provides a robust set of featuresfor web and mobile applications
var express = require('express');
var app = express();

//Establish connection to the MySQL database
var mysql = require('mysql');
var con = mysql.createConnection({
	host : 'localhost',
	user : 'root',
	password : '',
	database : 'employee'
});
con.connect(function(err) {
	if (err) {
		console.log('Error connection to db');
		return;
	}
	console.log('Connection established.');
	});


 /* Friendship: CRUD GET, DELETE, UPDATE, POST */
app.get('/Friendship/find/:id', function(req,res) {
	var query = "select Friendship.ID , Friendship.BeganDate , Friendship.Friendship1 , Friendship.Friendship2  from Friendship as Friendship where id = ?";
	con.query(query, req.params.id, function(err, rows0,fields) {
		if(err) throw err;
		rows0.forEach(function(row) { 
			query = "select User.ID , User.PictureUrl , User.Info , User.Name ";
			query += "from  User as User  inner join Friendship as Friendship ";
			query += "where  User.id = Friendship.Friendship1 and Friendship.id = ?";
			con.query(query, req.params.id, function(err, rows1,fields) {
				if (err) throw err;
				row.Friendship1 = rows1[0];
				rows0.forEach(function(row) { 
					query = "select User.ID , User.PictureUrl , User.Info , User.Name ";
					query += "from  User as User  inner join Friendship as Friendship ";
					query += "where  User.id = Friendship.Friendship2 and Friendship.id = ?";
					con.query(query, req.params.id, function(err, rows2,fields) {
						if (err) throw err;
						row.Friendship2 = rows2[0];
						res.jsonp(rows0);
					});
				});
			});
		});
	});
});

app.get('/Friendship/delete/:id', function(req,res,next) {
	var id = req.params.id;
	con.query('DELETE FROM Friendship WHERE id = ?', id, function(err,rows) {
		if (err) { console.log('Error deleting'); }
		else {
			console.log('in delete success');
			res.jsonp(rows);
		}
	});
});

app.post('/Friendship/update/:id', function(req,res,next) {
	var id = req.params.id;
	var Friendship = '';
	req.on('data', function(data) {
		Friendship += data;
	});
	req.on('end', function() {
		Friendship = JSON.parse(Friendship);
		var data = {
			BeganDate : Friendship.BeganDate ,
			Friendship1 : Friendship.Friendship1 ,
			Friendship2 : Friendship.Friendship2 
		};
		con.query('UPDATE Friendship set ? WHERE id = ?', [data, id],function(err,rows) {
			if (err) { console.log('Error updating'); }
			else {
				console.log('in update success');
				res.sendStatus(200);
			}
		});
	});
});

app.post('/Friendship/add', function(req,res) {
	var Friendship = '';
	req.on('data', function(data) {
		Friendship += data;
	});
	req.on('end', function() {
		Friendship = JSON.parse(Friendship);
		var data = {
			BeganDate : Friendship.BeganDate ,
			Friendship1 : Friendship.Friendship1 ,
			Friendship2 : Friendship.Friendship2 
		};
		con.query('INSERT INTO Friendship set ? ', data, function(err,rows) {
			if (err) { console.log('Error inserting'); }
			else {
				console.log('in insert success');
				res.sendStatus(200);
			}
		});
	});
});


 /* Comment: CRUD GET, DELETE, UPDATE, POST */
app.get('/Comment/find/:id', function(req,res) {
	var query = "select Comment.ID , Comment.CreatedBy , Comment.Content , Comment.CreateDate , Comment.PostTo  from Comment as Comment where id = ?";
	con.query(query, req.params.id, function(err, rows0,fields) {
		if(err) throw err;
		rows0.forEach(function(row) { 
			query = "select User.ID , User.PictureUrl , User.Info , User.Name ";
			query += "from  User as User  inner join Comment as Comment ";
			query += "where  User.id = Comment.CreatedBy and Comment.id = ?";
			con.query(query, req.params.id, function(err, rows1,fields) {
				if (err) throw err;
				row.CreatedBy = rows1[0];
				rows0.forEach(function(row) { 
					query = "select Post.ID , Post.CreatedBy , Post.Content , Post.CreateDate ";
					query += "from  Post as Post  inner join Comment as Comment ";
					query += "where  Post.id = Comment.PostTo and Comment.id = ?";
					con.query(query, req.params.id, function(err, rows2,fields) {
						if (err) throw err;
						row.PostTo = rows2[0];
						rows2.forEach(function(row) { 
							query = "select User.ID , User.PictureUrl , User.Info , User.Name ";
							query += "from  Post as Post  inner join Comment as Comment  inner join  User as User ";
							query += "where  Post.id = Comment.PostTo and Comment.id = ? and  User.id = Post.CreatedBy";
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

app.get('/Comment/delete/:id', function(req,res,next) {
	var id = req.params.id;
	con.query('DELETE FROM Comment WHERE id = ?', id, function(err,rows) {
		if (err) { console.log('Error deleting'); }
		else {
			console.log('in delete success');
			res.jsonp(rows);
		}
	});
});

app.post('/Comment/update/:id', function(req,res,next) {
	var id = req.params.id;
	var Comment = '';
	req.on('data', function(data) {
		Comment += data;
	});
	req.on('end', function() {
		Comment = JSON.parse(Comment);
		var data = {
			CreatedBy : Comment.CreatedBy ,
			Content : Comment.Content ,
			CreateDate : Comment.CreateDate ,
			PostTo : Comment.PostTo 
		};
		con.query('UPDATE Comment set ? WHERE id = ?', [data, id],function(err,rows) {
			if (err) { console.log('Error updating'); }
			else {
				console.log('in update success');
				res.sendStatus(200);
			}
		});
	});
});

app.post('/Comment/add', function(req,res) {
	var Comment = '';
	req.on('data', function(data) {
		Comment += data;
	});
	req.on('end', function() {
		Comment = JSON.parse(Comment);
		var data = {
			CreatedBy : Comment.CreatedBy ,
			Content : Comment.Content ,
			CreateDate : Comment.CreateDate ,
			PostTo : Comment.PostTo 
		};
		con.query('INSERT INTO Comment set ? ', data, function(err,rows) {
			if (err) { console.log('Error inserting'); }
			else {
				console.log('in insert success');
				res.sendStatus(200);
			}
		});
	});
});


 /* User: CRUD GET, DELETE, UPDATE, POST */
app.get('/User/find/:id', function(req,res) {
	var query = "select User.ID , User.PictureUrl , User.Info , User.Name  from User as User where id = ?";
	con.query(query, req.params.id, function(err, rows0,fields) {
		if(err) throw err;
		res.jsonp(rows0);
	});
});

app.get('/User/delete/:id', function(req,res,next) {
	var id = req.params.id;
	con.query('DELETE FROM User WHERE id = ?', id, function(err,rows) {
		if (err) { console.log('Error deleting'); }
		else {
			console.log('in delete success');
			res.jsonp(rows);
		}
	});
});

app.post('/User/update/:id', function(req,res,next) {
	var id = req.params.id;
	var User = '';
	req.on('data', function(data) {
		User += data;
	});
	req.on('end', function() {
		User = JSON.parse(User);
		var data = {
			PictureUrl : User.PictureUrl ,
			Info : User.Info ,
			Name : User.Name 
		};
		con.query('UPDATE User set ? WHERE id = ?', [data, id],function(err,rows) {
			if (err) { console.log('Error updating'); }
			else {
				console.log('in update success');
				res.sendStatus(200);
			}
		});
	});
});

app.post('/User/add', function(req,res) {
	var User = '';
	req.on('data', function(data) {
		User += data;
	});
	req.on('end', function() {
		User = JSON.parse(User);
		var data = {
			PictureUrl : User.PictureUrl ,
			Info : User.Info ,
			Name : User.Name 
		};
		con.query('INSERT INTO User set ? ', data, function(err,rows) {
			if (err) { console.log('Error inserting'); }
			else {
				console.log('in insert success');
				res.sendStatus(200);
			}
		});
	});
});


 /* Post: CRUD GET, DELETE, UPDATE, POST */
app.get('/Post/find/:id', function(req,res) {
	var query = "select Post.ID , Post.CreatedBy , Post.Content , Post.CreateDate  from Post as Post where id = ?";
	con.query(query, req.params.id, function(err, rows0,fields) {
		if(err) throw err;
		rows0.forEach(function(row) { 
			query = "select User.ID , User.PictureUrl , User.Info , User.Name ";
			query += "from  User as User  inner join Post as Post ";
			query += "where  User.id = Post.CreatedBy and Post.id = ?";
			con.query(query, req.params.id, function(err, rows1,fields) {
				if (err) throw err;
				row.CreatedBy = rows1[0];
				res.jsonp(rows0);
			});
		});
	});
});

app.get('/Post/delete/:id', function(req,res,next) {
	var id = req.params.id;
	con.query('DELETE FROM Post WHERE id = ?', id, function(err,rows) {
		if (err) { console.log('Error deleting'); }
		else {
			console.log('in delete success');
			res.jsonp(rows);
		}
	});
});

app.post('/Post/update/:id', function(req,res,next) {
	var id = req.params.id;
	var Post = '';
	req.on('data', function(data) {
		Post += data;
	});
	req.on('end', function() {
		Post = JSON.parse(Post);
		var data = {
			CreatedBy : Post.CreatedBy ,
			Content : Post.Content ,
			CreateDate : Post.CreateDate 
		};
		con.query('UPDATE Post set ? WHERE id = ?', [data, id],function(err,rows) {
			if (err) { console.log('Error updating'); }
			else {
				console.log('in update success');
				res.sendStatus(200);
			}
		});
	});
});

app.post('/Post/add', function(req,res) {
	var Post = '';
	req.on('data', function(data) {
		Post += data;
	});
	req.on('end', function() {
		Post = JSON.parse(Post);
		var data = {
			CreatedBy : Post.CreatedBy ,
			Content : Post.Content ,
			CreateDate : Post.CreateDate 
		};
		con.query('INSERT INTO Post set ? ', data, function(err,rows) {
			if (err) { console.log('Error inserting'); }
			else {
				console.log('in insert success');
				res.sendStatus(200);
			}
		});
	});
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
	console.log('We have started our serveron port 3000');
});

