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
        self.conferenceTemplate = self.$el.find("#conferenceTemplate").html();
        self.talkTemplate = self.$el.find("#talkTemplate").html();
        window.CommonHandler.ajax({
            url: "/api/all",
            success: self.populateTalks
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
