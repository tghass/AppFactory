var CrudView = function(service) {
    var employeesList;

    this.template = function(){
        return new EJS({url:'templates/crud.ejs'}).render();
    }

    this.render = function() {
        this.$el.html(this.template());
        return this;
    };
  
	//ADD USER
	this.addUser = function(e) {
		e.preventDefault();
		var data = {'pictureurl': $("#pictureurl").val(),
					'info' : $("#info").val(),
					'name' : $("#name").val()};
					
		service.addUser(data).done(function(success) {
			console.log('hhhhhhhhe');
		});
    };  
	
	//UPDATE USER
	this.updateUser = function(e) {
		e.preventDefault();
		var data = {'pictureurl': $("#pictureurl").val(),
					'info' : $("#info").val(),
					'name' : $("#name").val()};
		var id = $("#userid").val();
		console.log('user id' + id);
		service.updateUser(id,data).done(function(success) {
			console.log('hhhhhhhhe');
		});
    };  
	
	//DELETE USER
	this.delUser = function(e) {
		e.preventDefault();
		var id = $("#userid").val();
		service.delUser(id).done(function(success) {
			console.log('hhhhhhhhe');
		});
    };
	

    this.initialize = function () {
        // Define a div wrapper for the view (used to attach events)
        this.$el = $('<div/>');
      $(document).on('click', '#add', this.addUser);
	  $(document).on('click', '#del', this.delUser);
	  $(document).on('click', '#update', this.updateUser);
		
    };

    this.initialize();
}
