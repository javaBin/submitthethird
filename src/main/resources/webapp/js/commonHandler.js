"use strict";

window.CommonHandler = {
    readAcccesstoken: function (callback) {
        var token = localStorage.getItem("submit.accesstoken");
        if (!token) {
            if (callback.notLoggedIn) {
                callback.notLoggedIn();
            }
            return;
        }
        window.CommonHandler.ajax({
            url: "/api/id",
            success: function (fromServer) {
                if (callback.loggedIn(fromServer.email));
            },
            error:  function( jqXHR , textStatus, errorThrown ) {
                if (callback.notLoggedIn) {
                    callback.notLoggedIn();
                }
            }

        },token)

    },
    ajax: function (settings,token) {
        token = token || localStorage.getItem("submit.accesstoken");
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
            success: callback,
            error: function( jqXHR, textStatus, errorThrown ) {
                window.location.href = "/";
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
