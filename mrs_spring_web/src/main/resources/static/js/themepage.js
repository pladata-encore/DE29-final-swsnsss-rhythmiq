document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("themesubmit").addEventListener("click", function () {
        const inputtext = document.getElementById("themeinput").value;
        const hour = parseInt(document.getElementById("hour").value);
        const minute = parseInt(document.getElementById("minute").value);

        const totalduration = hour * 60 + minute;

        const data = {
            inputText: inputtext,
            totalDuration: totalduration
        };

        fetch("http://localhost:5000/api/v1/flask/themeselect", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })
            .then(response => response.json())
            .then(data => {
                console.log('Success:', data);

                // Extract the necessary data for the second request
                const tokenizedTheme = data.tokenizedTheme;
                const trackCounts = data.trackCounts;

                // Create the data object for the second request
                const trackData = {
                    tokenizedTheme: tokenizedTheme,
                    trackCounts: trackCounts
                };
                if (Array.isArray(tokenizedTheme) && tokenizedTheme.length === 0) {
                    // Reload the browser
                    alert("태그 추출에 실패했습니다. 다른 문장을 입력해주세요.")
                    window.location.reload();
                    return; // Exit the function
                }
                

                // Send the second POST request to get recommended tracks
                return fetch("http://localhost:5000/api/v1/flask/gettracks", {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(trackData)
                });
            })
            .then(response => response.json())
            .then(trackData => {
                console.log('Recommended Tracks:', trackData);
                const tokenizedTheme = trackData.tokenizedTheme;
                const trackUris = trackData.trackUris;
                const entokenizedTheme = trackData.entokenizedTheme
                // Create a URL with query parameters
                const queryParams = new URLSearchParams();
                queryParams.append('tokenizedTheme', JSON.stringify(tokenizedTheme));
                queryParams.append('entokenizedTheme', JSON.stringify(entokenizedTheme));
                // Add recommended tracks to query parameters
                queryParams.append('recommendedtracks', JSON.stringify(trackUris));
                // Redirect to /playlist with the track data as query parameters
                window.location.href = `/user/playlist?${queryParams.toString()}`;
            })
            .catch((error) => {
                console.error('Error:', error);
            });
    }
    );
});

// 스피너
$(document).ready(function () {
    $(document).on('click', 'button', function (event) {
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

