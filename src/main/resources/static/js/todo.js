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

    // 할 일 제목 클릭 시 수정 모달창 open
    document.querySelectorAll('.todoTtl').forEach(btn => {
        btn.addEventListener('click', function() {
            const todoId = this.getAttribute('data-id');

            fetch(`/todos/popup/${todoId}`)
                .then(res => res.json())
                .then(json => {
                    const todo = json.data;
                    document.getElementById('edit-id').value = todo.id;
                    document.getElementById('edit-title').value = todo.title;
                    document.getElementById('edit-completed').checked = todo.completed;

                    new bootstrap.Modal(document.getElementById('editModal')).show();
                });
        });
    });

    // 수정 폼 제출
    document.getElementById('editForm').addEventListener("submit", function(e) {
        e.preventDefault();

        const id = document.getElementById('edit-id').value;
        const title = document.getElementById('edit-title').value;
        const completed = document.getElementById('edit-completed').checked;

        fetch(`/todos/${id}`, {
            method: 'PUT',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ title, completed })
        }).then(res => res.json()
            .then(json => {
                if (json.success) {
                    location.reload();  // 저장 후 새로고침
                } else {
                    alert('수정 실패');
                }
            }));
    });

});

function toggleDarkMode() {
    const body = document.getElementById("main-body");
    const btn = document.getElementById("darkModeBtn");

    if (body.classList.contains("bg-light")) {
        // bg-light 클래스를 제거하고 dark-mode 추가
        body.classList.remove("bg-light");
        body.classList.add("dark-mode");
        btn.textContent = "☀️ 라이트 모드";
    } else {
        // dark-mode 클래스를 제거하고 bg-light 추가
        body.classList.remove("dark-mode");
        body.classList.add("bg-light");
        btn.textContent = "🌙 다크 모드";
    }
}
