var js_admin = {
    init : function () {
        var _this = this;
        $('#btn-logout').on('click', function () {
           _this.logout();
        });
        $('#btn-inverted_index').on('focus keyup', function (event) {
           _this.createInvertedIndex(event);
        });
        $('#btn-similarity-index').on('focus keyup', function (event) {
           _this.createSimilarityIndex(event);
        });
        $('#btn-similarity-index-new').on('focus keyup', function (event) {
           _this.createSimilarityIndexNew(event);
        });
    },
    logout : function () {
        $.ajax({
            type: 'POST',
            url: '/user-api/logout'
        }).done(function () {
            window.location.href = '/';
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    },
     createInvertedIndex : function () {
         $.ajax({
             type: 'put',
             url: '/blog-api/search/make-inverted-index'
         }).done(function () {
             window.location.href = '/blog/admin';
         }).fail(function (error) {
             alert(JSON.stringify(error));
         });
     },
      createSimilarityIndex : function () {
          $.ajax({
              type: 'put',
              url: '/blog-api/recommend/make-similarity-index'
          }).done(function () {
              window.location.href = '/blog/admin';
          }).fail(function (error) {
              alert(JSON.stringify(error));
          });
      },
             createSimilarityIndexNew : function () {
                 $.ajax({
                     type: 'put',
                     url: '/blog-api/recommend/make-similarity-index2'
                 }).done(function () {
                     window.location.href = '/blog/admin';
                 }).fail(function (error) {
                     alert(JSON.stringify(error));
                 });
             }
}

js_admin.init();