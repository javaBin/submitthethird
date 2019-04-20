$(function () {
    window.ShowTalksHandler.init($("#showTalkMain"));
});

window.ShowTalksHandler = {
    $el: null,
    conferenceTemplate:null,
    talkTemplate: null,
    init: function ($el) {
        var self = window.ShowTalksHandler;
        self.$el = $el;
        window.CommonHandler.checkLoggedIn(function(fromServer) {
            self.conferenceTemplate = self.$el.find("#conferenceTemplate").html();
            self.talkTemplate = self.$el.find("#talkTemplate").html();
            self.$el.find("#email").append(fromServer.email);
            if (fromServer.isClosed) {
                self.$el.find("#closedSection").show();
            }
            window.CommonHandler.ajax({
                url: "/api/all",
                success: self.populateTalks
            });
        });
        self.$el.find("#addTalkButton").click(function () {
            var payload = {
                password: self.$el.find("#password").val()
            };
            window.CommonHandler.ajax({
                url: "/api/checkLatePassword",
                data: JSON.stringify(payload),
                method:"POST",
                success: function (fromServer) {
                    if (fromServer.status === "ok") {
                        if (fromServer.password) {
                            localStorage.setItem("submit.submitpassword",fromServer.password);
                        }
                        window.location.href = "/talkEdit.html";
                        return;
                    }
                    self.$el.find("#addTalkError").show();
                }

            })
        });

    },
    populateTalks: function (fromServer) {
        var self = window.ShowTalksHandler;
        var $talkList = self.$el.find("#talkList");
        $.each(fromServer.conferences,function (index, conference) {
            var $conference = $(self.conferenceTemplate);
            $conference.find(".conferenceName").append(conference.conferenceName);
            var $talksForConference = $conference.find(".talkForYear");
            $.each(conference.talks,function (index, atalk) {
                var $talk = $(self.talkTemplate);
                $talk.find(".talkLink").attr("href","/talkEdit.html?id=" + atalk.id);
                $talk.find(".talkName").append(atalk.title);
                $talksForConference.append($talk);
            });
            $talkList.append($conference);
        });
    }
};
