var js_signup = {
    init : function () {
        var _this = this;
        $('#btn-signup').on('click', function () {
           _this.signup();
        });
    },
    signup : function (event) {
        var data = {
            userId: $('#inputID').val(),
            userPassword: $('#inputPassword').val(),
            userName: $('#inputName').val(),
            userEmail: $('#inputEmail').val()
        };

        $.ajax({
            type: 'POST',
            url: '/user-api/user-signup',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function () {
            alert('회원가입이 완료되었습니다.');
            window.location.href = '/blog/login';
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    }
}

js_signup.init();