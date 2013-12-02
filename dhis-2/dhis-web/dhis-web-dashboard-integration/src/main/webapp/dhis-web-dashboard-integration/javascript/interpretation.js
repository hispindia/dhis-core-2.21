var currentPage = 0;
var pageLock = false;

$(function() {
  $(document).scroll(function() {
    isNextPage();
  });

  $("#interpretationFeed").load("getInterpretations.action", function() {
    $(".commentArea").autogrow();
  });
});

function expandComments( id ) {
  $("#comments" + id).children().show();
  $("#commentHeader" + id).hide();
}

function isNextPage() {
  var fromTop = $(document).scrollTop();
  var docHeight = $(document).height();
  var windowHeight = $(window).height();
  var threshold = parseInt(350);
  var remaining = parseInt(docHeight - ( fromTop + windowHeight ));

  if( remaining < threshold ) {
    loadNextPage();
  }
}

function loadNextPage() {
  if( pageLock == true ) {
    return false;
  }

  pageLock = true;
  currentPage++;

  $.get("getInterpretations.action", { page: currentPage }, function( data ) {
    $("#interpretationFeed").append(data);

    if( !isDefined(data) || $.trim(data).length == 0 ) {
      $(document).off("scroll");
    }

    pageLock = false;
  });
}

function postComment( uid ) {
  var text = $("#commentArea" + uid).val();

  $("#commentArea" + uid).val("");

  var url = "../api/interpretations/" + uid + "/comment";

  var created = getCurrentDate();

  var gearBox = "<div class=\"gearDropDown\">\n  <span><i class=\"fa fa-gear\"></i> <i class=\"fa fa-caret-down\"></i></span>\n</div>\n";

  if( text.length && $.trim(text).length ) {
    $.ajax(url, {
      type: "POST",
      contentType: "text/html",
      data: $.trim(text),
      success: function( data, textStatus, request ) {
        var locationArray = request.getResponseHeader('Location').split('/');
        var commentUid = locationArray[locationArray.length - 1];

        var template =
          "<div class='interpretationComment' data-ip-comment-uid='" + commentUid + "'>" +
            "<div><div class=\"interpretationName\">" +
            "<a class=\"bold userLink\" href=\"profile.action?id=${userUid}\">${userName}</a>&nbsp;" +
            "<span class=\"grey\">${created}<\/span>" + gearBox + "<\/div><\/div>" +
            "<div class=\"interpretationText\">${text}<\/div>" +
            "</div>";

        $.tmpl(template, {
          "userId": currentUser.id,
          "userUid": currentUser.uid,
          "userName": currentUser.name,
          created: created,
          text: text }).appendTo("#comments" + uid);
      }
    });
  }
}

// DropDown Actions

function editIp( e ) {
  var jqActiveGearDropDown = jQuery('.gearDropDown.active');
  var isHeader = jqActiveGearDropDown.parents('.interpretationContent').length != 0;
  var isComment = jqActiveGearDropDown.parents('.interpretationCommentArea').length != 0;
  var jqInterpretation = jqActiveGearDropDown.parents('.interpretationContainer');
  var jqInterpretationComment = jqActiveGearDropDown.parents('.interpretationComment');

  var ipUid = jqInterpretation.data('ip-uid');
  var ipCommentUid = jqInterpretationComment.data('ip-comment-uid');

  if( isHeader ) {
    var jqTarget = jqInterpretation.find('.interpretationContent').find('.interpretationText');
    setupTextArea(ipUid, ipCommentUid,jqTarget );
  } else if( isComment ) {
    var jqTarget = jqInterpretationComment.find('.interpretationText');
    setupTextArea(ipUid, ipCommentUid,jqTarget );
  }
}

function setupTextArea( ipUid, ipCommentUid, $target ) {
  var oldContent = $target.html().trim();

  var textArea = jQuery("<textarea />")
    .css({ 'width': '100%', 'height': '80px' })
    .uniqueId()
    .html(oldContent);

  var container = jQuery("<div />")
    .uniqueId()
    .append(textArea);

  var cancelButton = jQuery("<button/>")
    .text(i18n_cancel)
    .on('click', function( e ) {
      $target.html(oldContent);
    });

  var saveButton = jQuery("<button/>")
    .text(i18n_save)
    .on('click', function( e ) {
      alert('save is not implemented');
    });

  container.append(cancelButton);
  container.append(saveButton);

  $target.html(container);

  textArea.focus();
}

function deleteIp( e ) {
  var jqActiveGearDropDown = jQuery('.gearDropDown.active');
  var isHeader = jqActiveGearDropDown.parents('.interpretationContent').length != 0;
  var isComment = jqActiveGearDropDown.parents('.interpretationCommentArea').length != 0;
  var jqInterpretation = jqActiveGearDropDown.parents('.interpretationContainer');
  var jqInterpretationComment = jqActiveGearDropDown.parents('.interpretationComment');

  var ipUid = jqInterpretation.data('ip-uid');
  var ipCommentUid = jqInterpretationComment.data('ip-comment-uid');

  if( isHeader ) {
    jQuery.ajax({
      url: '../api/interpretations/' + ipUid,
      type: 'DELETE'
    }).done(function() {
      jqInterpretation.remove();
    }).error(function() {
      setHeaderDelayMessage('$i18n.getString("could_not_delete_interpretation")');
    });
  } else if( isComment ) {
    jQuery.ajax({
      url: '../api/interpretations/' + ipUid + '/comments/' + ipCommentUid,
      type: 'DELETE'
    }).done(function() {
      jqInterpretationComment.remove();
    }).error(function() {
      setHeaderDelayMessage('$i18n.getString("could_not_delete_interpretation_comment")');
    });
  }
}
