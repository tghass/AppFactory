var EmployeeListView = function () {
    this.template = function(employees){
        return new EJS({url:'templates/employeeList.ejs'}).render({employees:employees});
    }

    var employees;

    this.initialize = function() {
        this.$el = $('<div/>');
		this.$el.on('submit', '.new-employee', this.addUser);
		this.render();
		$(document).on('submit', 'form.new-employee', this.addUser);
	};
	
    this.addUser = function() {
		console.log('hur33');
		service.addUser($('#the-form').val()).done(function(success) {
			console.log('hhhhhhhhe');
		});
    };
    this.setEmployees = function(list) {
        employees = list;
        this.render();
    }

    this.render = function() {
        this.$el.html(this.template(employees));
        return this;
    };
	
	
	
    this.initialize();

}
