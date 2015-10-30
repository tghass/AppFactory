var EmployeeService = function () {

    this.initialize = function () {
        var deferred = $.Deferred();
        deferred.resolve();
        return deferred.promise();
    }
		
	this.addUser = function (data) {
		var deferred = $.Deferred();
		var url = 'http://localhost:3000/User/add';
		var data = JSON.stringify(data);
		$.ajax({        
			url: url,
			type:"post",
			crossDomain: true,
			dataType: "json",
			contentType: "application/json; charset=UTF-8",
			data:  data,
			success:function(res){
				console.log("Add successful"); //TODO: this does nothing, needs to change
				deferred.resolve(true);
			},
			error:function(xhr, status, error){
				console.log(xhr.responseText);
				deferred.resolve(false);
			}
       }); 
	   return deferred.promise();
        
    }
	this.updateUser = function (id, data) {
		var deferred = $.Deferred();
		var url = 'http://localhost:3000/User/update/' + id;
		var data = JSON.stringify(data);
		$.ajax({        
			url: url,
			type:"post",
			crossDomain: true,
			dataType: "json",
			contentType: "application/json; charset=UTF-8",
			data:  data,
			success:function(res){
				console.log("Update successful"); //TODO: this does nothing, needs to change
				deferred.resolve(true);
			},
			error:function(xhr, status, error){
				console.log(xhr.responseText);
				deferred.resolve(false);
			}
       }); 
	   return deferred.promise();        
    }
	
	this.delUser = function(id) {
	    var deferred = $.Deferred();
        var employees= [];
        var url = 'http://localhost:3000/User/delete/' +id;
        $.ajax({
            url: url,
            success: function(data) {
                console.log('Delete success');
                deferred.resolve(employees);
            },
            dataType: 'jsonp',
            error: function(error, two,three) {
                console.log( "Request Failed: " + error + two);
                deferred.reject("Transaction Error: ");
            }
        }); 
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
