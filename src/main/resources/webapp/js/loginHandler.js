$(function () {
    window.LoginHandler.init($("#loginmain"))
});

window.LoginHandler = {
    $el: null,
    init:function ($el) {
        var self = this;
        self.$el = $el;
        self.$el.find("#loginButton").click(self.loginButtonClicked)
    },
    loginButtonClicked: function () {
        var self = window.LoginHandler;
        var $message = self.$el.find("#messagetext");
        $message.empty();
        var payload = {
            email: self.$el.find("#email").val()
        };
        if (_.isEmpty(payload.email) {
            $message.append("Please fill in an email")
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
