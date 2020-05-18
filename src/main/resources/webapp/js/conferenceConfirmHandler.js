$(function () {
    window.ConferenceConfirmHandler.init($("#conferenceConfirmMain"))
});
window.ConferenceConfirmHandler = {
    $el: null,
    init: function ($el) {
        let self = window.ConferenceConfirmHandler;
        self.$el = $el;
        let payload = {
            talkid: window.CommonHandler.getUrlParameter("talkid"),
            confirmOption: window.CommonHandler.getUrlParameter("option")
        };
        window.CommonHandler.ajax({
            data: JSON.stringify(payload),
            url: "/api/conferenceConfirm",
            method:"POST",
            success: function (fromServer) {
                self.$el.find("#waitcontent").hide();
                if (fromServer.status === "error") {
                    self.$el.find("#errormessage").append(fromServer.errormessage);
                    self.$el.find("#errorcontent").show();
                    return;
                }
                self.$el.find("#okcontent").show();
            }
        })
    }
};