var EmployeeService = function () {

    this.initialize = function () {
        var deferred = $.Deferred();
		 deferred.resolve();
		return deferred.promise();
    }

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
