var HomeView = function(service) {
    var employeesList;

    this.template = function(){
        return new EJS({url:'templates/home.ejs'}).render({employees:employeesList});
    }

    this.render = function() {
        this.$el.html(this.template());
        return this;
    };


    this.findByName = function(itself) {
        var key = $('.search-key').val();
        if(key===undefined){ key="%20"}
        service.findByName(key).done(function(employees) {
            employeesList=employees;
            itself.data.render();
            $('.search-key').val(key);
        });
    };
	
	this.addUser = function(e) {
		e.preventDefault();
		console.log('hur33');
		var data = {'pictureurl': $("#pictureurl").val(),
					'info' : $("#info").val(),
					'name' : $("#name").val()};
					
		service.addUser(data).done(function(success) {
			console.log('hhhhhhhhe');
		});
    };
	

    this.initialize = function () {
        // Define a div wrapper for the view (used to attach events)
        this.$el = $('<div/>');
        this.$el.on('click', '#search',this, this.findByName);//Attach a div class and method to the div wrapper
        this.findByName({data:this});
		$(document).on('submit', 'form.new-employee', this.addUser);
	
    };

    this.initialize();
}
