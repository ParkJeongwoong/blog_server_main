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
            window.location.href = '/';
        }).fail(function (error) {
            console.log(JSON.stringify(error));
            alert("아이디와 비밀번호를 확인해주세요.");
        });
    }
}

js_login.init();