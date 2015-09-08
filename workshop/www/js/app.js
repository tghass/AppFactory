// We use an "Immediate Function" to initialize the application to avoid leaving anything behind in the global scope
(function () {
    /* -------------------------- Tamara Added ----------------------------------- */
    function renderHomeView() {
	  $('body').html(homeTpl());
      $('.search-key').on('keyup', findByName);
    }
    /* ---------------------------------- Local Variables ---------------------------------- */
    //declare two var that hold the compiled version of the
	//templates defined in index.html
	HomeView.prototype.template = Handlebars.compile($("#home-tpl").html());
EmployeeListView.prototype.template = 
            Handlebars.compile($("#employee-list-tpl").html());
	var service = new EmployeeService();
    service.initialize().done(function () {
        //console.log("Service initialized");
//	renderHomeView();
//modify the service initialization logic to display the Home View when the service has been successfully initialized. Pass the service as an argument to the HOme View constructor.
    $('body').html(new HomeView(service).render().$el);
    });

    /* --------------------------------- Event Registration -------------------------------- */
   // $('.search-key').on('keyup', findByName);
   // $('.help-btn').on('click', function() {
   //    alert("Employee Directory v3.4");
   // });

    /* ---------------------------------- Local Functions ---------------------------------- */
    function findByName() {
          service.findByName($('.search-key').val()).done(function (employees) {
        $('.content').html(employeeListTpl(employees));
            
        });
    }

}());
