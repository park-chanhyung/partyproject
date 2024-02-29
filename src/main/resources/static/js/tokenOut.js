document.addEventListener('DOMContentLoaded', function() {
    // JWT 토큰이 존재하는지 확인
    const jwtToken = localStorage.getItem('jwtToken');
    console.log("토큰 "  + jwtToken)

    // 토큰이 있으면 1분 후에 로그아웃 함수 호출
    if (jwtToken != null) {
        setTimeout(logout, 60000);
    } else {
        // 토큰이 없는데 인증된 상태인지 서버에 확인 요청
        checkAuthAndLogout();
    }
});

// 서버에 인증 상태 확인 후 로그아웃 함수 호출
function checkAuthAndLogout() {
    fetch('/checkAuthEndpoint', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.ok) {
                // 인증된 상태이므로 로그아웃 실행
                logout();
            } else {
                console.log('인증되지 않은 상태입니다.');
            }
        })
        .catch(error => {
            console.error('서버 요청 중 오류가 발생했습니다:', error);
        });
}

// 로그아웃 함수 정의
function logout() {
    fetch('/jwtLogout', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.ok) {
                console.log('로그아웃이 완료되었습니다.');
                alert("로그인 유효시간이 만료되어 자동 로그아웃 됩니다.")
                // 로그아웃이 성공한 후에는 홈페이지로 리다이렉트할 수 있습니다.
                window.location.href = '/';
            } else {
                console.error('로그아웃에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('로그아웃 요청 중 오류가 발생했습니다:', error);
        });
}
