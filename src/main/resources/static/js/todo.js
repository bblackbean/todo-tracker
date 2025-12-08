document.addEventListener('DOMContentLoaded', function() {  // html 문서가 완전히 로드되고 DOM이 생성된 후에 실행됨
    // 페이지 로드 시 날짜/날씨 함수 호출
    displayTodayInfo();

    const calendarEl = document.getElementById('calendar');
    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',    // 초기 뷰를 월간 달력으로 설정
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay' // 월, 주, 일 보기 버튼
        },
        editable: true,   // 이벤트 드래그, 리사이즈 가능 여부
        selectable: true, //날짜 선택 가능 여부
        googleCalendarApiKey: document.getElementById('calendarKey').value,
        eventSources: [
            // 1. 기존 todo 데이터
            {
                events: function(fetchInfo, successCallback, failureCallback) {
                    fetch('/todos')     // 기존에 만들어둔 전체 조회 api
                        .then(res => res.json())
                        .then(apiRes => {
                            if ( apiRes.success ) {
                                // console.log('data ::: ', apiRes.data);
 
                                const events = apiRes.data.map(todo => {
                                    const endDate = new Date(todo.endDate);
                                    endDate.setDate(endDate.getDate() + 1);     // FullCalendar의 end는 exclusive이므로, +1일을 해줘야 정상적으로 표시됨
 
                                    return {
                                        id: todo.id,
                                        title: todo.title,
                                        start: todo.startDate,
                                        end: endDate.toISOString().split('T')[0],
                                        color: todo.color,
                                        extendedProps: {
                                            completed: todo.completed
                                        }
                                    }
                                });
 
                                successCallback(events);
                            } else {
                                failureCallback(new Error('Falied to fetch todos'));
                            }
                        }).catch(error => failureCallback(error));
                }
            },
            // 2. 대한민국 공휴일 데이터 소스(Google Calendar API)
            {
                // eventSources 배열에는 googleCalendarId만 명시
                googleCalendarId: 'ko.south_korea#holiday@group.v.calendar.google.com',
                color: '#e63946',   // 공휴일 배경색
                textColor: 'white'  // 공휴일 텍스트색
            }
        ],
        // 달력의 이벤트를 클릭했을 때의 동작
        eventClick: function(info) {
            // Google Calendar 이벤트는 id가 없으므로, 직접 만든 이벤트만 모달창 open
            if (info.event.id) {
                openEditModal(info.event.id);
            }
        }
    });
    
    calendar.render();  // 달력을 화면에 렌더링

    /**
     * 등록 폼 제출 이벤트 처리
     * */
    document.getElementById('addForm').addEventListener("submit", function(e) {
        e.preventDefault();

        const newTodo = {
            title: document.getElementById('add-title').value,
            startDate: document.getElementById('add-startDate').value,
            endDate: document.getElementById('add-endDate').value,
            color: document.getElementById('add-color').value,
            completed: false // 새로운 할 일은 항상 미완료 상태
        };

        fetch('/todos', { // POST 요청으로 할 일 생성 API 호출
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(newTodo)
        }).then(res => res.json())
            .then(json => {
                if (json.success) {
                    // 모달창 닫기
                    const addModal = bootstrap.Modal.getInstance(document.getElementById('addModal'));
                    addModal.hide();
                    // 캘린더 이벤트 새로고침
                    calendar.refetchEvents();
                } else {
                    alert('등록 실패: ' + (json.message || '알 수 없는 오류'));
                }
            });
    });

    /**
     * 수정 폼 제출
     * */
    document.getElementById('editForm').addEventListener("submit", function(e) {
        e.preventDefault();

        const id = document.getElementById('edit-id').value;
        const title = document.getElementById('edit-title').value;
        const startDate = document.getElementById('edit-startDate').value;
        const endDate = document.getElementById('edit-endDate').value;
        const completed = document.getElementById('edit-completed').checked;
        const color = document.getElementById('edit-color').value;

        fetch(`/todos/${id}`, {
            method: 'PUT',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ title, completed, startDate, endDate, color })
        }).then(res => res.json()
            .then(json => {
                if (json.success) {
                    // 수정 모달창 닫고 캘린더 새로고침
                    const editModal = bootstrap.Modal.getInstance(document.getElementById('editModal'));
                    editModal.hide();
                    calendar.refetchEvents();
                } else {
                    alert('수정 실패');
                }
            }));
    });

});

/**
 * 모달창 닫힐 때 입력 필드 초기화
 * */
document.getElementById('addModal').addEventListener('hidden.bs.modal', function () {
    document.getElementById('addForm').reset();
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

/**
 * 수정 모달창 open
 * */
function openEditModal(todoId) {
    fetch(`/todos/popup/${todoId}`)
        .then(res => res.json())
        .then(json => {
            if ( json.success ) {
                const todo = json.data;
                document.getElementById('edit-id').value = todo.id;
                document.getElementById('edit-title').value = todo.title;
                document.getElementById('edit-startDate').value = todo.startDate;
                document.getElementById('edit-endDate').value = todo.endDate;
                document.getElementById('edit-completed').checked = todo.completed;
                document.getElementById('edit-color').value = todo.color;

                new bootstrap.Modal(document.getElementById('editModal')).show();
            } else {
                alert('할 일 정보를 가져오는데 실패했습니다.');
            }
        }).catch(error => console.error('Error fetching todo for edit : ', error));
}


/**
 * 오늘 날짜 및 날씨 정보 표시
 */
function displayTodayInfo() {
    // 1. 날짜 표시
    const dateEl = document.getElementById('today-date');
    const today = new Date();
    const options = { year : 'numeric', month: 'long', day: 'numeric', weekday: 'long' };
    dateEl.textContent = today.toLocaleDateString('ko-KR', options);

    // 2. 날씨 표시
    const weatherEl = document.getElementById('today-weather');
    if ( navigator.geolocation ) {
        navigator.geolocation.getCurrentPosition(position => {
            const lat = position.coords.latitude;
            const lon = position.coords.longitude;

            // 날씨 Proxy API 호출
            fetch(`/api/weather?lat=${lat}&lon=${lon}`)
                .then(response => response.json())
                .then(data => {
                    const icon = data.weather[0].icon;
                    const temp = data.main.temp.toFixed(1); // 소수점 첫째 자리까지
                    const description = data.weather[0].description;

                    weatherEl.innerHTML = `
                        <img src="https://openweathermap.org/img/wn/${icon}.png" alt="Weather icon" style="width: 40px; height: 40px;">
                        <span class="ms-1">${temp}°C, ${description}</span>
                    `;
                }).catch(error => {
                    console.error('날씨 정보를 가져오는 데 실패했습니다.', error);
                    weatherEl.textContent = '날씨 정보 로딩 실패';
                });
        }, () => {
            weatherEl.textContent = '위치 정보를 허용해주세요.';
        });
    } else {
        weatherEl.textContent = '위치 정보를 사용할 수 없습니다.';
    }
}