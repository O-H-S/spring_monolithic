document.addEventListener('DOMContentLoaded', function () {
    // 모달 엘리먼트 선택
    var settingsModal = document.getElementById('settingsModal');
    const settingsForm = document.getElementById('settingsForm');
    const bulkInsertForm = document.getElementById('bulkInsertForm');

    // 모달이 보여질 때 이벤트 리스너 설정
    settingsModal.addEventListener('show.bs.modal', function (event) {
        console.log("clicked");
        const button = event.relatedTarget;
        const boardId = button.getAttribute('data-board-id');
        const boardTitle = button.getAttribute('data-board-title');

        const modalBoardId = settingsModal.querySelector('#boardId');
        const modalBoardName = settingsModal.querySelector('#boardTitle');
        console.log(boardId + boardTitle);

        modalBoardId.value = boardId;
        modalBoardName.value = boardTitle;

        settingsForm.action = `/board/${boardId}/title`;
        bulkInsertForm.action = `/api/posts/${boardId}/bulk`;

    });

    document.getElementById('bulkInsertForm').addEventListener('submit', function(e) {
        e.preventDefault();

        const boardId = document.getElementById('boardId').value;
        const title = document.getElementById('title').value;
        const count = document.getElementById('count').value;
        const csrfToken = this.querySelector('[name="_csrf"]').value;

        // AJAX 요청을 사용하여 JSON 형식으로 데이터를 전송
        fetch(`/api/posts/${boardId}/bulk`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
            body: JSON.stringify({ title, count })
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                }
                throw new Error('Network response was not ok.');
            })
            .then(data => console.log(data))
            .catch(error => console.error('There has been a problem with your fetch operation:', error));
    });

    document.getElementById('deleteBoardBtn').addEventListener('click', function() {
        const boardId = document.getElementById('boardId').value;
        if (confirm("Are you sure you want to delete this board?")) {
            fetch(`/api/boards/${boardId}`, {
                method: 'DELETE',
                headers: {
                    'X-CSRF-TOKEN': document.querySelector('input[name="_csrf"]').value
                }
            }).then(response => {
                if(response.ok) {
                    // Handle successful deletion
                    console.log("Board deleted successfully");
                } else {
                    // Handle failure
                    console.error("Failed to delete board");
                }
            });
        }
    });

});
