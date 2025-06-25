document.addEventListener('DOMContentLoaded', function() {  // html 문서가 완전히 로드되고 DOM이 생성된 후에 실행됨
    const checkboxes = document.querySelectorAll('.toggle-completed');

    // 체크박스가 여러 개라서 반복문 돌며 각 체크박스마다 이벤트 리스너 추가
    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const id = this.getAttribute('data-id');
            const completed = this.checked;

            // ajax
            fetch(`/view/todos/${id}/toggle`, {
                method: 'POST',
                headers: {
                    'Content-Type' : 'application/json'     // 요청 본문이 JSON 형식임을 명시
                },
                body: JSON.stringify({completed})           // 체크박스의 상태(completed)를 JSON 문자열로 변환하여 서버로 전송
            }).then(response => {
                if ( !response.ok ) {   // 서버 응답 성공
                    alert('업데이트에 실패했습니다.');
                    console.error('업데이트에 실패했습니다.');
                    console.error(response);
                }
            });
        });
    });
});

function toggleDarkMode() {
    const body = document.getElementById("main-body");
    body.classList.toggle("dark-mode");

    const btn = document.getElementById("darkModeBtn");
    if (body.classList.contains("dark-mode")) {
        btn.textContent = "☀️ 라이트 모드";
        document.getElementsByTagName('h1');
    } else {
        btn.textContent = "🌙 다크 모드";
    }
}
