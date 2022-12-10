var sub_monitoring = {
    init : function () {
        var _this = this;
        $('#btn-monitoring').on('click', function () {
           _this.monitoring();
        });
        $('#monitoring-from-date').on('focus keyup', function (event) {
           _this.inputFormatting(event);
        });
        $('#monitoring-to-date').on('focus keyup', function (event) {
           _this.inputFormatting(event);
        });
    },
    inputFormatting : function (event) {
        var thisVal = event.target.value.replace(/\s|\D/gi, "");
        if (thisVal.length > 6) {
            thisVal = thisVal.substring(0,4) + '-' + thisVal.substring(4,6)+ '-' + thisVal.substring(6,8);
        } else if (thisVal.length > 4) {
            thisVal = thisVal.substring(0,4) + '-' + thisVal.substring(4,6);
        }
        event.target.value = thisVal;
    },
    monitoring : function () {
        var fromValue = document.getElementById("monitoring-from-date").value;
        var toValue = document.getElementById("monitoring-to-date").value;

        $.ajax({
            type: 'GET'
          , url: '/blog-api/visitor-in-hours/' + fromValue + '/' + toValue
          , contentType : 'application/json; charset=utf-8'
        }).done(function (res) {
//            console.log(JSON.stringify(res));
            config1.data.datasets = [];
            config2.data.datasets[0].data = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0];
            for (var i=0;i<res.length;i++) {
                var color2 = Math.floor(Math.random() * 256);
                var color3 = Math.floor(Math.random() * 256);
                result = res[i];
                if (result.hour == 0) {
                    config1.data.datasets.push({
                          label: result.date,
                          borderColor: 'rgb(255, '+color2+', '+color3+')',
                          data: [],
                          tension: 0.3,
                          fill: true
                    });
                }
                config1.data.datasets[config1.data.datasets.length-1].data.push(result.count);
                config2.data.datasets[0].data[result.hour] += result.count;
            }

            // Chart 재생성
            const copiedElement1 = document.importNode(
                document.querySelector('#visitorInHours'),
                true
            );
            const copiedElement2 = document.importNode(
                document.querySelector('#cumulativeVisitorInHours'),
                true
            );
            document.getElementById("visitorInHours").remove();
            document.getElementById("cumulativeVisitorInHours").remove();
            document.querySelector('body').append(copiedElement1);
            document.querySelector('body').append(copiedElement2);

            // Chart 값 부여
            const visitorInHours = new Chart(
            document.getElementById('visitorInHours'),
            config1
            );
            const cumulativeVisitorInHours = new Chart(
            document.getElementById('cumulativeVisitorInHours'),
            config2
            );
            console.log(config1)
            console.log(config2)
        }).fail(function (error) {
            console.log(JSON.stringify(error));
            console.log(error.message);
        });
    }
}

sub_monitoring.init();

const data = {
    labels: ['00', '01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24'],
    datasets: []
};

const config1 = {
    type: 'line',
    data: {
        labels: ['00', '01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24'],
        datasets: []
    },
    options: {
        plugins: {
            title: {
                display: true,
                text: 'Daily Visitor in hours'
            }
        }
    }
};

const config2 = {
    type: 'line',
    data: {
        labels: ['00', '01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24'],
        datasets: [{
             label: 'Visitor in hours',
             borderColor: 'rgb(0,0,0)',
             data: [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
             tension: 0.3,
             fill: true
        }]
    },
    options: {
        plugins: {
            title: {
                display: true,
                text: 'Cumulative Visitor in hours'
            }
        }
    }
};