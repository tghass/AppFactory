var HomeView = function  (service) {
    var employeeListView;

    this.template = function(){
        return new EJS({url:'templates/home.ejs'}).render('');
    }

    this.render = function() {
        this.$el.html(this.template());
        this.showAll();//Show all to start
        $('#maincontent', this.$el).html(employeeListView.$el);//Set content
        return this;
    };

    this.findByName = function() {
        service.findByName($('.search-key').val()).done(function(employees) {
            employeeListView.setEmployees(employees);
        });
    };

    this.showAll = function(){
        service.findByName('').done(function(employees) {
            employeeListView.setEmployees(employees);
        });
    }

    this.initialize = function () {
        // Define a div wrapper for the view (used to attach events)
        this.$el = $('<div/>');
        this.$el.on('keyup', '.search-key', this.findByName);//Attach a div class and method to the div wrapper
        employeeListView = new EmployeeListView();
        this.render();
    };

    this.initialize();
}
