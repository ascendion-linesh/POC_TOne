$(document).ready(function() {
    $('#bookList').DataTable({
        "lengthMenu": [[5,10,15,20,-1], [5,10,15,20,"All"]],
        "ordering": false,
        stateSave: true
    });

    $("#bookList").on('page.dt', function() {
        $('html, body').animate({
            scrollTop: $('#bookList').offset().top
        }, 200);
    });
});
