var EmployeeView = function(employee) {
    this.template = function(employee){
        return new EJS({url:'templates/employee.ejs'}).render({employee:employee});
    }
  
    this.initialize = function() {
        this.$el = $('<div/>');
    };
  
    this.render = function() {
        this.$el.html(this.template(employee));
        return this;
    };
  
    this.initialize();

}
