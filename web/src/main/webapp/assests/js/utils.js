$(document).ready(function(){
    $('#employeeList').click(function () {
        employeeList();
    });
});


function employeeList() {
    $('#employeeListForm').prop('action', 'employees.form');
    $('#employeeListButton').prop('value', 'Employees');
    $('#employeeListForm').show();
}