"use strict";

$(function () {
    window.EmailLoginHandler.init();

});

window.EmailLoginHandler= {
    init: function () {
        var token = window.CommonHandler.getUrlParameter("token")
        if (!token) {
            window.location.href = "/";
            return;
        }
        window.CommonHandler.storeToken(token)

    }
};