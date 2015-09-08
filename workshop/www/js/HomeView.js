//If we keep adding all the core functions of the application to the immediate function (in app.js) that bootstraps the app, it will very quickly grow out of control. Thus, we create a HomeView object that encapsulates the logic to create and render the Home view.


//The constructor function takes the employee data service as an argument
var HomeView = function (service) {
	var employeeListView;
	this.initialize = function() {
		//Define a div wrapper for the view (used to attach the view related events)
		this.$el = $('<div/>');
		this.$el.on('keyup','.search-key',this.findByName);
		employeeListView = new EmployeeListView();
		this.render();
		};

	this.render = function() {
		this.$el.html(this.template());
        	$('.content', this.$el).html(employeeListView.$el);
	    	return this;
	};

	this.findByName= function() {

		service.findByName($('.search-key').val()).done(function(employees) {
	        employeeListView.setEmployees(employees);
		    });
	}	
	this.initialize();
}


