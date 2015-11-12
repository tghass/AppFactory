var LoggedInUser = undefined;
function loginHandler(r){
    //console.log(r);
    hello(r.network).api('me').then(function(me){
        console.log(me);
        LoggedInUser = me.id;
        setLogout();
        window.location.hash='#';
        $(window).trigger('hashchange');
    });
}
function logoutHandler(r){
    LoggedInUser = undefined;
    setLogin();
    window.location.hash='#';
    $(window).trigger('hashchange');
}
function setLogout(){
    $('#login').unbind('click');
    $('#login').attr('onclick',"hello('google').logout().then(logoutHandler);");
    $('#login').text('Log Out');
}
function setLogin(){
    $('#login').unbind('click');
    $('#login').attr('onclick',"hello('google').login().then(loginHandler);");
    $('#login').text('Log In');
}
function setLoginButton(){
    if(LoggedInUser==undefined)
        setLogin();
    else
        setLogout();
}
