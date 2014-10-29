var exception;

function onSaveCode(data) {
    handleAjax(data);
    var status = data.status;
    switch (status) {
        case "begin":
            exception = "";
            break;
        case "success":
            if (exception === "") {
                $('#codeDialog').modal('hide');
                animateCodesPanel();
            }
            break;
    }
}

function handleAjaxCodeError(data) {
    if (data.type !== "event") {
        exception = data.errorMessage;
        alert(data.errorMessage);
    }
}

function onOpenEditCode(data) {
    var status = data.status;
    switch (status) {
        case "begin":
            $('#pnlWrapper').hide();
            $('#codeDialog').modal('show');
            $('#waitMessage').detach().appendTo('#codeBody');
            $('#waitMessage').show();
            break;
        case "success":
            $('#waitMessage').hide();
            $('#pnlWrapper').show();
            break;
    }
}

function onOpenEditCodeError(data) {
    if (data.type !== "event") {
        $('#waitMessage').hide();
        alert(data.name);
    }
}

function animateCodesPanel() {
    $('#mainForm\\:pnlCodes').hide();
    $('#mainForm\\:pnlCodes').fadeIn('slow');
}

function onDeleteCode(data) {
    handleAjax(data);
    var status = data.status;
    switch (status) {
        case "success":
            animateCodesPanel();
            break;
    }
}