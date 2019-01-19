"use strict";

window.CommonHandler = {
    readAcccesstoken: function () {
        return localStorage.getItem("submit.accesstoken");
    },
    ajax: function (settings) {
        var token = window.CommonHandler.readAcccesstoken();
        if (token) {
            settings.headers = {
                submittoken: token
            }
        }
        return $.ajax(settings)
    },
    checkLoggedIn: function(callback) {
        window.CommonHandler.ajax({
            url:"/api/id",
            success: function (fromServer) {
                callback(fromServer.email);
            },
            error: function( jqXHR, textStatus, errorThrown ) {
                window.location.href = "/emailLogin.html";
            }
        });
    },
    storeToken: function(token) {
        localStorage.setItem("submit.accesstoken",token);
    },
    getUrlParameter: function (name, url) {
        url = url || window.location.href;
        name = name.replace(/[\[\]]/g, "\\$&");

        var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
            results = regex.exec(url);

        if (!results) return null;
        if (!results[2]) return '';

        return decodeURIComponent(results[2].replace(/\+/g, " "));
    }

};
