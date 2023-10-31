document.addEventListener('DOMContentLoaded', function () {
    // 모달 엘리먼트 선택
    var settingsModal = document.getElementById('settingsModal');
    const settingsForm = document.getElementById('settingsForm');
    console.log("called");
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
    });
});
