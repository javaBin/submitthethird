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
        self.speakerTemplate = self.$el.find("#speakertemplate").html();
        self.setupTalk(self.emptyTalk())
        self.$el.find("#submitTalk").click(self.submitTalk);
        self.$el.find('input[name="format"]').change(function () {
            //console.log("Format changed " + $(this).val())
            self.updateLengthOptions(self.$el.find("input[name='format']:checked").val());
        });
        self.talkId = window.CommonHandler.getUrlParameter("id");
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
        self.speakerDoms = [];
        $.each(talk.speakers,function (index, speaker) {
            self.speakerDoms.push(self.appendSpeaker(speaker));
        });

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
    appendSpeaker:function(speaker) {
        var self = window.EditTalkHandler;
        var $speakerDom = $(self.speakerTemplate);
        self.$el.find("#speakerList").append($speakerDom);
        return $speakerDom
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
            speakers: speakers
        };
        var url;
        if (self.id) {
            url = "/api/updateTalk";
        } else {
            url = "api/createTalk";
        }
        var payload = {talk: talkToSubmit};
        console.log(payload);
        window.CommonHandler.ajax({
            data: JSON.stringify(payload),
            url: url,
            method:"POST",
            success: function () {
                window.location.href="/showTalks.html"
            }
        })
    }
};
