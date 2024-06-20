document.addEventListener("DOMContentLoaded", function () {
    // 모든 playIcon 요소를 찾습니다.
    const playIcons = document.querySelectorAll('.bi-play-circle');

    // 각 playIcon 요소에 클릭 이벤트 리스너를 추가합니다.
    playIcons.forEach(icon => {
        icon.addEventListener("click", function () {
            fetch('../templates/playerbar.html')
                .then(response => response.text())
                .then(html => {
                    document.body.insertAdjacentHTML('beforeend', html);

                    // HTML에 연결된 CSS 파일을 가져와 삽입합니다.
                    const linkElement = document.createElement('link');
                    linkElement.rel = 'stylesheet';
                    linkElement.href = '../static/css/playerbar.css';
                    document.head.appendChild(linkElement);

                    // HTML에 연결된 JavaScript 파일을 가져와 실행합니다.
                    const scriptElement = document.createElement('script');
                    scriptElement.src = '../static/js/playerbar.js';
                    document.body.appendChild(scriptElement);
                });
        });
    });
});
