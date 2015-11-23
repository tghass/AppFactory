var LoggedInUser = undefined;
var serv = new UserService();
function loginHandler(r){
    //console.log(r);
    hello(r.network).api('me').then(function(me){
        //console.log(me);
        serv.findByOAuthID(me.id).done(function(res){
            console.log(res);
            res=res[0];
            if(res!=undefined){//We already have a user
                LoggedInUser = res.ID;
                setLogout();
                window.location.hash='#';
                $(window).trigger('hashchange');
            }else{//Create a new user
                var data = {
                    PictureUrl : me.picture,
                    OAuthID : me.id,
                    Info : "User info",
                    Name : me.displayName 
                };
                serv.addUser(data).done(function(result){
                    serv.findByOAuthID(me.id).done(function(newResult){
                        newResult=newResult[0];
                        LoggedInUser = newResult.ID;
                        setLogout();
                        window.location.hash='#';
                        $(window).trigger('hashchange');
                    });
                });
            }
        });
        console.log("Done");
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
