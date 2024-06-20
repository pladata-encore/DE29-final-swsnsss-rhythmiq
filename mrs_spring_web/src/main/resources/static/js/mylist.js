function confirmDelete(playlistId, event) {
    // 이벤트 버블링을 막아 리스트 아이템 클릭 이벤트가 발생하지 않도록 합니다.
    event.stopPropagation();

    const confirmation = confirm("정말 이 플레이리스트를 삭제하시겠습니까?");
    if (confirmation) {
        deletePlaylist(playlistId);
    }
}

function deletePlaylist(playlistid) {
    data = {
        "playlistId": playlistid
    }
    fetch("/api/v1/deleteplaylist", {
        method: 'post',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            alert("삭제되었습니다.")
            window.location.href = '/user/mylists';
        })

        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });
}

function showplaylistpage(tracks, themes, enthemes, src) {
    console.log('Recommended Tracks:');
    const tokenizedTheme = JSON.parse(themes);
    const trackUris = JSON.parse(tracks);
    const entokenizedTheme = JSON.parse(enthemes);
    // Create a URL with query parameters
    const queryParams = new URLSearchParams();
    queryParams.append('tokenizedTheme', JSON.stringify(tokenizedTheme));
    queryParams.append('entokenizedTheme', JSON.stringify(entokenizedTheme));
    // Add recommended tracks to query parameters
    queryParams.append('recommendedtracks', JSON.stringify(trackUris));
    queryParams.append('playlistCoverSrc', src);
    // Redirect to /playlist with the track data as query parameters
    window.location.href = `/user/playlist?${queryParams.toString()}`;
}

// 스피너
$(document).ready(function () {
    $(document).on('click', 'li', function (event) {
        $('.spinner-wrapper').show();
    });

    $(window).on('load', function () {
        $('.spinner-wrapper').hide();
    });
});

// navigation bar
$(document).ready(function () {
    function updateMenu() {
        if ($(window).width() <= 992) {
            $('#navbarDropdown').css('pointer-events', 'auto');
            $('.list-unstyled').show();
            $('.dropdown-menu').hide();
        } else {
            $('#navbarDropdown').css('pointer-events', 'none');
            $('.list-unstyled').hide();
            $('.dropdown-menu').show();
        }
    }

    updateMenu();
    $(window).resize(updateMenu);
});
