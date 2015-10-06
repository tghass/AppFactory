var EmployeeListView = function () {
    this.template = function(employees){
        return new EJS({url:'templates/employeeList.ejs'}).render({employees:employees});
    }

    var employees;

    this.initialize = function() {
        this.$el = $('<div/>');
        this.render();
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
