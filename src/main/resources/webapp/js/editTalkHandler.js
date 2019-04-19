$(function () {
    window.EditTalkHandler.init($("#editTalkMain"));
});

window.EditTalkHandler = {
    $el: null,
    speakerTemplate: null,
    formatValues: {
        "PRESENTATION": {
            radioid: "formatPres",
            minutes: ["45","60"],
            helpText: "Presentations can have a length of 45 or 60 minutes. Including Q&A"
        },
        "LIGHTNING_TALK": {
            radioid: "formatLight",
            minutes: ["10","20"],
            helpText: "Lightning talks can be 10 or 20 minutes long. The time limit is strictly enforced"
        },
        "WORKSHOP": {
            radioid: "formatWork",
            minutes: ["120","240","480"],
            helpText: "Workshops last 2, 4 or 8 hours (120, 240 or 480 minutes)"
        }

    },
    speakerDoms: [],
    talkId: null,
    init: function ($el) {
        var self = window.EditTalkHandler;
        self.$el = $el;
        window.CommonHandler.checkLoggedIn(function(callback) {
            self.speakerTemplate = self.$el.find("#speakertemplate").html();
            self.$el.find("#submitTalk").click(self.submitTalk);
            self.$el.find('input[name="format"]').change(function () {
                self.updateLengthOptions(self.$el.find("input[name='format']:checked").val());
            });
            self.talkId = window.CommonHandler.getUrlParameter("id");
            if (self.talkId) {
                window.CommonHandler.ajax({
                    url: "/api/talk/" + self.talkId,
                    success:function (fromServer) {
                        self.setupTalk(fromServer);
                    }
                });

            } else {
                self.setupTalk(self.emptyTalk())
            }
        });
    },
    emptyTalk: function() {
        return {
            format: "PRESENTATION",
            speakers: [{}]
        }
    },
    setupTalk: function(talk) {
        var self = window.EditTalkHandler;
        self.$el.find("#" + self.formatValues[talk.format].radioid).prop("checked",true);
        self.updateLengthOptions(talk.format);

        self.$el.find("input[name=language][value=" + talk.language + "]").prop("checked",true);

        self.$el.find("#title").val(talk.title);
        self.$el.find("#abstract").val(talk.abstract);
        self.$el.find("#intendedAudience").val(talk.intendedAudience);
        self.$el.find("#length").val(talk.length);
        self.$el.find("#outline").val(talk.outline);
        self.$el.find("#equipment").val(talk.equipment);
        self.$el.find("#infoToProgramCommittee").val(talk.infoToProgramCommittee);
        self.$el.find("#suggestedKeywords").val(talk.suggestedKeywords);
        self.$el.find("#participation").val(talk.participation);

        self.$el.find("#addSpeaker").click(self.addSpeaker);


        self.speakerDoms = [];
        var canDelete = (talk.speakers.length > 1);
        $.each(talk.speakers,function (index, speaker) {
            self.appendSpeaker(speaker,canDelete);
        });

    },
    addSpeaker:function() {
        var self = window.EditTalkHandler;
        self.$el.find("#addSpeakerSection").hide();
        $.each(self.speakerDoms,function(index,$speaker) {
            $speaker.find(".removeSpeakerSection").show();
        });
        self.appendSpeaker({},true);
    },
    updateLengthOptions:function(format) {
        var self = window.EditTalkHandler;
        var minutes = self.formatValues[format].minutes;
        var $length = self.$el.find("#length");
        $length.empty();
        $.each(minutes,function (index, givenlength) {
            $length.append($('<option value="' + givenlength + '">' + givenlength + " minutes</option>"));
        });
        var $lengthDescription = self.$el.find("#lengthDescription");
        $lengthDescription.empty();
        $lengthDescription.append(self.formatValues[format].helpText);

    },
    appendSpeaker:function(speaker,canDelete) {
        var self = window.EditTalkHandler;
        var $speakerDom = $(self.speakerTemplate);
        $speakerDom.find(".speakerid").val(speaker.id);
        $speakerDom.find(".speakername").val(speaker.name);
        $speakerDom.find(".speakeremail").val(speaker.email);
        $speakerDom.find(".speakertwitter").val(speaker.twitter);
        $speakerDom.find(".speakerbio").val(speaker.bio);
        $speakerDom.find(".speakerpostcode").val(speaker.zipCode);

        if (!canDelete) {
            $speakerDom.find(".removeSpeakerSection").hide();
        }
        self.$el.find("#speakerList").append($speakerDom);
        self.speakerDoms.push($speakerDom);
        var deleteIndex = self.speakerDoms.length-1
        $speakerDom.find(".removeSpeaker").click(function () {
            $speakerDom.remove();
            self.speakerDoms.splice(deleteIndex,1);
            self.$el.find("#addSpeakerSection").show();
            $.each(self.speakerDoms,function(index,$speaker) {
                $speaker.find(".removeSpeakerSection").hide();
            });
        });

    },
    submitTalk: function () {
        var self = window.EditTalkHandler;
        var speakers = [];

        $.each(self.speakerDoms,function (index, speakerDom) {
          var speakerobj = {
              id: speakerDom.find(".speakerid").val(),
              name: speakerDom.find(".speakername").val(),
              email: speakerDom.find(".speakeremail").val(),
              bio: speakerDom.find(".speakerbio").val(),
              twitter: speakerDom.find(".speakertwitter").val(),
              zipCode: speakerDom.find(".speakerpostcode").val()
          };
          speakers.push(speakerobj);
        });
        var talkToSubmit = {
            id: self.talkId,
            title: self.$el.find("#title").val(),
            language: self.$el.find("input[name='language']:checked").val(),
            intendedAudience: self.$el.find("#intendedAudience").val(),
            format: self.$el.find("input[name='format']:checked").val(),
            equipment:self.$el.find("#equipment").val(),
            abstract: self.$el.find("#abstract").val(),
            outline: self.$el.find("#outline").val(),
            infoToProgramCommittee: self.$el.find("#infoToProgramCommittee").val(),
            length: self.$el.find("#length").val(),
            suggestedKeywords: self.$el.find("#suggestedKeywords").val(),
            participation: self.$el.find("#participation").val(),

            speakers: speakers
        };
        var url;
        if (self.talkId) {
            url = "/api/updateTalk";
        } else {
            url = "api/createTalk";
        }
        var payload = {talk: talkToSubmit};
        console.log(payload);
        var $errormessage=self.$el.find("#errormessage");
        $errormessage.empty();
        window.CommonHandler.ajax({
            data: JSON.stringify(payload),
            url: url,
            method:"POST",
            success: function (fromServer) {
                if (fromServer.status === "error") {
                    $errormessage.append(fromServer.errormessage);
                    return;
                }
                window.location.href="/showTalks.html"
            }
        })
    }
};
