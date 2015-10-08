function loginHandler(r){
    console.log(r);
    hello(r.network).api('me').then(function(me){
        console.log(me);
    });
}
// We use an "Immediate Function" to initialize the application to avoid leaving anything behind in the global scope
(function () {

    // Initate the library
    hello.init({
        google : '164737927993-l34g84brkg96ufe30ve2mjpg6lcen2pg.apps.googleusercontent.com',
        facebook : '160981280706879',
        windows : '00000000400D8578'
    }, {
        //
        // Define the OAuth2 return URL
        // This can be anything you like, providing its the callback which you have registered with the providers for OAuth2
        // It could even be localhost, e.g. http://localhost/somepath as phonegap is not run from a domain so SameOrigin breaks, instead we take advantage of being able to read the popups URL in PhoneGap
        redirect_uri : 'http://localhost/index.html'
    });


    var service = new EmployeeService();

    service.initialize().done(function () {
        router.addRoute('', function() {
            $('body').html(new HomeView(service).render().$el);
        });

        router.addRoute('employees/:id', function(id) {
            service.findById(parseInt(id)).done(function(employee) {
                $('body').html(new EmployeeView(employee).render().$el);
            });
        });

        router.start();
    });

}());
