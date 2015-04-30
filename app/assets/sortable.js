// Convert from (possibly empty) 0-based JavaScript index to 1-based position.
function position(index) {
  return index ? index + 1 : 1;
}

$(function() {
  var fromPosition;
  $('#sortable').sortable({

    // Save the start position and indicate dragging.
    start: function(event, ui) {
      fromPosition = position(ui.item.index());
      ui.item.addClass('list-group-item-info');
      ui.item.find('.badge').remove();
    },

    // After dragging, update the position.
    update: function(event, ui) {
      var toPosition = position(ui.item.index());

      // Ajax request to the server, using the URL from the UL data attribute.
      var url = ui.item.closest('ul').data('url');
      var jqXHR = $.ajax({
        url: url,
        type: 'PUT',
        data: { from: fromPosition, to: toPosition }
      });

      // Indicate successful reposition: remove highlight and badges.
      jqXHR.done(function(jqXHR, error) {
        ui.item.removeClass('list-group-item-info');
        ui.item.parent().find('.badge').remove();
      });

      // Simplistic error handling.
      jqXHR.fail(function() {
        ui.item.removeClass('list-group-item-info').addClass('list-group-item-danger');
      });
    }
  });
});
