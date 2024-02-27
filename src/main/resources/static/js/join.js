// // 도메인 직접 입력 or domain option 선택
// const domainListEl = document.querySelector('#domain-list')
// const domainInputEl = document.querySelector('#domain-txt')
// // select 옵션 변경 시
// domainListEl.addEventListener('change', (event) => {
//     // option에 있는 도메인 선택 시
//     if(event.target.value !== "type") {
//         // 선택한 도메인을 input에 입력하고 disabled
//         domainInputEl.value = event.target.value
//         domainInputEl.disabled = true
//     } else { // 직접 입력 시
//         // input 내용 초기화 & 입력 가능하도록 변경
//         domainInputEl.value = ""
//         domainInputEl.disabled = false
//     }
// })
//
// $(document).ready(function() {
//     // 이메일 인증번호 버튼 클릭 이벤트
//     $("#checkEmail").click(function(event) {
//         event.preventDefault(); // 버튼의 기본 동작 막기
//         console.log("에러 클릭 발생: ", err); // 에러 처리
//         // Ajax 요청 보내기
//         $.ajax({
//             type: "POST",
//             url: "/emailConfirm",
//             data: {
//                 "memail": $("#memail").val()
//             },
//             success: function(data) {
//                 alert("해당 이메일로 인증번호 발송이 완료되었습니다. \n 확인부탁드립니다.");
//                 console.log("data : " + data);
//                 chkEmailConfirm(data, $("#memailconfirm"), $("#memailconfirmTxt"));
//             },
//             error: function(err) {
//                 console.log("에러 발생: ", err); // 에러 처리
//             }
//         });
//     });
//
//     // 이메일 인증번호 확인 함수
//     function chkEmailConfirm(data, $memailconfirm, $memailconfirmTxt) {
//         $memailconfirm.on("keyup", function() {
//             if (data != $memailconfirm.val()) {
//                 emconfirmchk = false;
//                 $memailconfirmTxt.html("<span id='emconfirmchk'>인증번호가 잘못되었습니다</span>")
//                     .css({
//                         "color": "#FA3E3E",
//                         "font-weight": "bold",
//                         "font-size": "10px"
//                     });
//             } else {
//                 emconfirmchk = true;
//                 $memailconfirmTxt.html("<span id='emconfirmchk'>인증번호 확인 완료</span>")
//                     .css({
//                         "color": "#0D6EFD",
//                         "font-weight": "bold",
//                         "font-size": "10px"
//                     });
//             }
//         });
//     }
// });