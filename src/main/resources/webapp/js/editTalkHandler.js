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
              name: speakerDom.find(".speakername").val()
          };
          speakers.push(speakerobj);
        });
        var talkToSubmit = {
            speakers: speakers
        };
        console.log(talkToSubmit);
    }
};
