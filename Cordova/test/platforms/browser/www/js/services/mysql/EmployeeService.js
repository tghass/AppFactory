var EmployeeService = function () {

    this.initialize = function () {
        var deferred = $.Deferred();
		 deferred.resolve();
		return deferred.promise();
    }

    this.findByName = function (searchKey) {
		var deferred = $.Deferred();
		var employees= [];
			
		var url = 'http://128.180.150.139:3000/search';
		url += '?ename='+searchKey;
		url += '&callback=?';
		$.ajax({
			url: url,
			crossDomain: true,
			success: function(data) {
				$.each(data, function(key, val) {
					employees.push(val);
					$.each(val, function (key, val) {
						console.log(key + ':' + val);
					});
				});
				deferred.resolve(employees);
			},
			dataType: 'jsonp',
			error: function(error1, two,three) {
				console.log( "Request Failed: " + two + three);
                deferred.reject("Transaction Error: ");
			}
		}); 
	 return deferred.promise();
        
    }
	
	
	
  this.findById = function (id) {
		var deferred = $.Deferred();
	
		var url = 'http://128.180.150.139:3000/search';
		url += '?eid='+id;
		url += '&callback=?';
		var employees= [];
		$.ajax({
			url: url,
			crossDomain: true,
			success: function(data) {
				$.each(data, function(key, val) {
					employees.push(val);
					$.each(val, function (key, val) {
						console.log(key + ':' + val);
					});
				});
				deferred.resolve(employees);
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