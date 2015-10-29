var EmployeeService = function () {

    this.initialize = function () {
        var deferred = $.Deferred();
        deferred.resolve();
        return deferred.promise();
    }
			// Create the XHR object.
	/*	function createCORSRequest(method, url) {
		  var xhr = new XMLHttpRequest();
		  if ("withCredentials" in xhr) {
			// XHR for Chrome/Firefox/Opera/Safari.
			xhr.open(method, url, true);
		  } else if (typeof XDomainRequest != "undefined") {
			// XDomainRequest for IE.
			xhr = new XDomainRequest();
			xhr.open(method, url);
		  } else {
			// CORS not supported.
			xhr = null;
		  }
		  return xhr;
		}

		// Helper method to parse the title tag from the response.
		function getTitle(text) {
		  return text.match('<title>(.*)?</title>')[1];
		}

		// Make the actual CORS request.
		function makeCorsRequest() {
		  // All HTML5 Rocks properties support CORS.
		  var url = 'http://updates.html5rocks.com';

		  var xhr = createCORSRequest('GET', url);
		  if (!xhr) {
			alert('CORS not supported');
			return;
		  }

		  // Response handlers.
		  xhr.onload = function() {
			var text = xhr.responseText;
			var title = getTitle(text);
			alert('Response from CORS request to ' + url + ': ' + title);
		  };

		  xhr.onerror = function() {
			alert('Woops, there was an error making the request.');
		  };

		  xhr.send();
		} */
	this.addUser = function () {
		console.log('in add user');
		var deferred = $.Deferred();
		var url = 'http://localhost:3000/User/add';
		var data = JSON.stringify({'id': 'hi'});
		var req;
		$(document).bind("mobileinit", function() {
			$.support.cors = true;
			$.mobile.allowCrossDomainPages = true;
		});
		$.ajax({        
			url: url,
			type:"post",
			crossDomain: true,
			data: data,
			success:function(res){
				alert("Add successful");
				deferred.resolve(true);
			},
			error:function(xhr, status, error){
				console.log(xhr.responseText);
				var err = '';
				deferred.resolve(false);
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
