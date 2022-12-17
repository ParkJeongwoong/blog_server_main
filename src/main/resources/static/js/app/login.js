var js_login = {
    init : function () {
        var _this = this;
        $('#btn-login').on('click', function () {
           _this.login();
        });
    },
    login : function (event) {
        var data = {
            userId: $('#inputID').val(),
            password: $('#inputPassword').val()
        };

        $.ajax({
            type: 'POST',
            url: '/user-api/login',
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function (data, textStatus, xhr) {
//            window.location.href = '/';
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    }
}

js_login.init();