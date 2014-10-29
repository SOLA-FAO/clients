function showFormHeaderDialog() {
    $('#formDialog').modal('show');
    return false;
}

var exception_form_heander;
function onSaveFormHeader(data) {
    handleAjax(data);
    var status = data.status;
    switch (status) {
        case "begin":
            exception_form_heander = "";
            break;
        case "success":
            if (exception_form_heander === "") {
                $('#formDialog').modal('hide');
            }
            break;
    }
}

function handleFormHeaderSaveError(data) {
    if (data.type !== "event") {
        exception_form_heander = data.errorMessage;
        alert(data.errorMessage);
    }
}

function onOpenEditRec(data, typeName) {
    var status = data.status;
    switch (status) {
        case "begin":
            $('#pnlWrapper_' + typeName).hide();
            $('#' + typeName + 'Dialog').modal('show');
            $('#waitMessage').detach().appendTo('#' + typeName + 'Body');
            $('#waitMessage').show();
            break;
        case "success":
            $('#waitMessage').hide();
            $('#pnlWrapper_' + typeName).show();
            break;
    }
}

// Sections
var exception_section;
function onOpenEditSection(data) {
    onOpenEditRec(data, 'section');
}

function onOpenEditRecordError(data) {
    if (data.type !== "event") {
        $('#waitMessage').hide();
        alert(data.name);
    }
}

function onSaveSection(data) {
    handleAjax(data);
    var status = data.status;
    switch (status) {
        case "begin":
            exception_section = "";
            break;
        case "success":
            if (exception_section === "") {
                $('#sectionDialog').modal('hide');
            }
            break;
    }
}

function handleSectionSaveError(data) {
    if (data.type !== "event") {
        exception_section = data.errorMessage;
        alert(data.errorMessage);
    }
}

// Fields
var exception_field;
function onOpenEditField(data) {
    onOpenEditRec(data, 'field');
}

function onSaveField(data) {
    handleAjax(data);
    var status = data.status;
    switch (status) {
        case "begin":
            exception_field = "";
            break;
        case "success":
            if (exception_field === "") {
                $('#fieldDialog').modal('hide');
            }
            break;
    }
}

function handleFieldSaveError(data) {
    if (data.type !== "event") {
        exception_field = data.errorMessage;
        alert(data.errorMessage);
    }
}

// Constraints
var exception_constraint;
function onOpenEditConstraint(data) {
    onOpenEditRec(data, 'constraint');
}

function onSaveConstraint(data) {
    handleAjax(data);
    var status = data.status;
    switch (status) {
        case "begin":
            exception_constraint = "";
            break;
        case "success":
            if (exception_constraint === "") {
                $('#constraintDialog').modal('hide');
            }
            break;
    }
}

function handleConstraintSaveError(data) {
    if (data.type !== "event") {
        exception_constraint = data.errorMessage;
        alert(data.errorMessage);
    }
}

// Constraint options
var exception_constraint_option;
function onOpenEditOption(data) {
    onOpenEditRec(data, 'option');
}

function onSaveOption(data) {
    handleAjax(data);
    var status = data.status;
    switch (status) {
        case "begin":
            exception_constraint_option = "";
            break;
        case "success":
            if (exception_constraint_option === "") {
                $('#optionDialog').modal('hide');
            }
            break;
    }
}

function handleOptionSaveError(data) {
    if (data.type !== "event") {
        exception_constraint_option = data.errorMessage;
        alert(data.errorMessage);
    }
}