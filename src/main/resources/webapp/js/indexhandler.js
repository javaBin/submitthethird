"use strict";

$(function () {
    window.indexHandler.init();
    /*
    $.ajax({
        url: "/api/createTalk",
        method: "POST",
        data: JSON.stringify({}),
        success: function(fromServer){

        },
        error: function( jqXHR, textStatus, errorThrown ){
            window.alert(jqXHR.status);
        }
    });*/
});

window.indexHandler = {
    init: function () {
        var token = window.CommonHandler.readAcccesstoken()
        if (token) {
            window.location.href = "/listTalks.html"
        } else {
            window.location.href = "/login.html"
        }
    }
};