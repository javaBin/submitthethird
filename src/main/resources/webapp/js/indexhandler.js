"use strict";

$(function () {
    window.IndexHandler.init($("#indexmain"));
});

window.IndexHandler = {
    $el:null,
    init: function ($el) {
        var self = window.IndexHandler;
        self.$el = $el;
        window.CommonHandler.readAcccesstoken({
            loggedIn: function () {
                window.location.href = "/showTalks.html";
            },
            notLoggedIn:function () {
                $el.find("#loginSection").show();
                $el.find("#waitSection").hide();

            }
        });

        self.$el.find("#loginButton").click(self.loginButtonClicked);

    },
    loginButtonClicked: function () {
        var self = window.IndexHandler;
        var $message = self.$el.find("#messagetext");
        $message.empty();
        var payload = {
            email: self.$el.find("#email").val()
        };
        if (_.isEmpty(payload.email)) {
            $message.append("Please fill in an email");
            return;
        }
        window.CommonHandler.ajax({
            url: "/api/createToken",
            method:"POST",
            data: JSON.stringify(payload),
            success: function (fromServer) {
                if (fromServer.status === "error") {
                    $message.append(fromServer.errormessage);
                    return;
                }
                $message.append("An email has been sent to you. Please check your mail")
            }
        })
    }
};