var sub_monitoring = {
    init : function () {
        var _this = this;
        $('#btn-monitoring').on('click', function () {
           _this.monitoring();
        });
    },
    monitoring : function () {
        var fromValue = document.getElementById("monitoring-from-date").value;
        var toValue = document.getElementById("monitoring-to-date").value;

        $.ajax({
            type: 'GET'
          , url: '/blog-api/visitor-timeline/' + fromValue + '/' + toValue
          , contentType : 'application/json; charset=utf-8'
        }).done(function (res) {
            console.log(JSON.stringify(res));

            config.data.datasets = []
            for (var i=0;i<res.length;i++) {
                var color2 = Math.floor(Math.random() * 256);
                var color3 = Math.floor(Math.random() * 256);
                result = res[i];
                if (result.hour == 0) {
                    config.data.datasets.push({
                          label: result.date,
                          borderColor: 'rgb(255, '+color2+', '+color3+')',
                          data: [],
                          tension: 0.3,
                          fill: true
                    });
                }
                config.data.datasets[config.data.datasets.length-1].data.push(result.count);
            }

            // Chart 재생성
            const copiedElement = document.importNode(
                document.querySelector('#timeline'),
                true
            );
            document.getElementById("timeline").remove();
            document.querySelector('body').append(copiedElement);

            // Chart 값 부여
            const timeline = new Chart(
            document.getElementById('timeline'),
            config
            );
        }).fail(function (error) {
            console.log(JSON.stringify(error));
            console.log(error.message);
        });
    }
}

sub_monitoring.init();

const timeArrays = ['00', '01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24'];

const data = {
    labels: timeArrays,
    datasets: []
};

const config = {
    type: 'line',
    data: data,
    options: {}
};