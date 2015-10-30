var EmployeeService = function () {

    this.initialize = function () {
        var deferred = $.Deferred();
        deferred.resolve();
        return deferred.promise();
    }
		
	this.addUser = function (data) {
		var deferred = $.Deferred();
		var url = 'http://localhost:3000/User/add';
		//var data = JSON.stringify({'pictureurl': 'hi', 'info':"ASd", 'name':'kasjd'});
		var data = JSON.stringify(data);
		console.log(data);
		console.log('before ajax');
		$.ajax({        
			url: url,
			type:"post",
			crossDomain: true,
			dataType: "json",
			contentType: "application/json; charset=UTF-8",
			data:  data,
			success:function(res){
				alert("Add successful");
				deferred.resolve(true);
			},
			error:function(xhr, status, error){
				console.log(xhr.responseText);
				console.log('in ajax');
				var err = '';
				deferred.resolve(false);
			}
       });
	   console.log('after ajax');
	   
	   return deferred.promise();
        
    }
	//GET REQUESTS
    this.findByName = function (searchKey) {

        var deferred = $.Deferred();
        var employees= [];
        var url = 'http://localhost:3000/employee/find/name/'+searchKey;
        $.ajax({
            url: url,
            success: function(data) {
                $.each(data, function(key, val) {
                    employees.push(val);
                });
                deferred.resolve(employees);
            },
            dataType: 'jsonp',
            error: function(error, two,three) {
                console.log( "Request Failed: " + two + three);
                deferred.reject("Transaction Error: ");
            }
        }); 
        return deferred.promise();

    }



    this.findById = function (id) {
        var deferred = $.Deferred();
        var url = 'http://localhost:3000/employee/find/'+id;
        $.ajax({
            url: url,
            success: function(data) {
                $.each(data, function(key, val) {
                    deferred.resolve(val);
                });
            },
            dataType: 'jsonp',
            error: function(error1, two,three) {
                console.log( "Request Failed: " + two + three);
                deferred.reject("Transaction Error: ");
            }
        }); 
        return deferred.promise();
    }



}
