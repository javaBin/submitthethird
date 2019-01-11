"use strict";

$(function () {
    $.ajax({
        url: "/api/createTalk",
        method: "POST",
        data: JSON.stringify({}),
        success: function(fromServer){

        },
        error: function( jqXHR, textStatus, errorThrown ){
            window.alert(jqXHR.status);
        }
    });
});