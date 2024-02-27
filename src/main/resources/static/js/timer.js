document.addEventListener('DOMContentLoaded', function() {
    const Timer = document.getElementById('Timer'); // 시간 분
    let time = 60000;  //1분
    let min =1;
    let sec = 60;  // 60초

    Timer.value=min+":"+'00';

    function TIMER(){
        // 1초마다 반복
        JWTTIME = setInterval(function (){
            time = time-1000;  // 1초씩 줄어듦
            min = time/(60*1000);  // 초를 분으로 나눠줌

            if(sec>0){
                sec = sec -1;
                Timer.value=Math.floor(min)+ ':'+sec; // 소수점 아래를 버리고 출력
            }
            if(sec === 0 ){
                //0에서 -1으 하면 -59가 출력되기때문에 0이 되면 바로 sec를 60으로 돌려주고 value에는 0 출력 x
                sec=60;
                Timer.value=Math.floor(min) + ':'+'00'
            }

        },1000);
    }
    TIMER();

    setTimeout(function (){
        clearInterval(JWTTIME);

        // Ajax로 jwtLogout 호출
        $.ajax({
            url: '/jwtLogout',
            type: 'GET',
            success: function() {
                // 응답 처리
                location.reload();
            },
            error: function(error) {
                console.error('Error:', error);
            }
        });

    },60000);
});