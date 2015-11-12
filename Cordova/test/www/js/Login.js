var loggedInUser = undefined;
function loginHandler(r){
    //console.log(r);
    hello(r.network).api('me').then(function(me){
        console.log(me);
        loggedInUser = me.id;
        setLogout();
    });
}
function logoutHandler(r){
    loggedInUser = undefined;
    setLogin();
}
function setLogout(){
    $('#login').unbind('click');
    $('#login').attr('onclick',"hello('google').logout().then(logoutHandler);");
    $('#login').text('Log Out');
}
function setLogin(){
    $('#login').unbind('click');
    $('#login').attr('onclick',"hello('google').login(loginHandler);");
    $('#login').text('Log In');
}
function setLoginButton(){
    if(loggedInUser==undefined)
        setLogin();
    else
        setLogout();
}
