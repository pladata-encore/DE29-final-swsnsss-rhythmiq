// 전역 변수로 player를 선언합니다.
let player;

// Spotify Web Playback SDK가 준비되면 호출되는 함수
window.onSpotifyWebPlaybackSDKReady = () => {
    var tracklistInput = document.getElementById("tracklist");
    var tracklistValue = tracklistInput.value;
    const token = document.getElementById("accesstoken").value;
    console.log("accesstoken is " + token);

    // player 변수에 값을 할당합니다.
    player = new Spotify.Player({
        name: 'Web Playback SDK Quick Start Player',
        getOAuthToken: cb => { cb(token); },
        volume: 0.5
    });

    // Ready
    player.addListener('ready', async ({ device_id }) => {
        console.log('Ready with Device ID', device_id);

        // Device ID를 백엔드로 전송
        await fetch('/api/v1/deviceid', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: device_id
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to send device ID to backend');
                }
                console.log('Device ID sent successfully to backend');
            })
            .catch(error => {
                console.error(error.message);
            });

        // 활성화된 디바이스 설정
        await fetch('/api/v1/activatedevice', {
            method: 'POST'
        });

        // 음악 설정
        await fetch('/api/v1/setmusic', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(tracklistValue)
        });

        console.log('All tasks completed.');

        intervalId = setInterval(async () => {
            player.getCurrentState().then(state => {
                if (!state) {
                    console.error('User is not playing music through the Web Playback SDK');
                    return;
                }
                var position = state.position;
                var duration = state.duration;
                document.getElementById('position').textContent = msToMinutesSeconds(position);
                document.getElementById('progressbar').value = position / duration * 100;
            });
        }, 50);
    });
    // Not Ready
    player.addListener('not_ready', ({ device_id }) => {
        console.log('Device ID has gone offline', device_id);
    });

    player.addListener('initialization_error', ({ message }) => {
        console.error(message);
    });

    player.addListener('authentication_error', ({ message }) => {
        console.error(message);
    });

    player.addListener('account_error', ({ message }) => {
        console.error(message);
    });

    player.addListener('player_state_changed', ({
        position,
        duration,
        track_window: { current_track }
    }) => {
        console.log('Currently Playing', current_track);
        document.getElementById('albumCover').src = current_track.album.images[0].url;
        document.getElementById('trackTitle').textContent = current_track.name;
        document.getElementById('artistName').textContent = current_track.artists.map(artist => artist.name).join(', ');
        console.log('Position in Song', position);
        document.getElementById('position').textContent = msToMinutesSeconds(position);
        console.log('Duration of Song', duration);
        document.getElementById('duration').textContent = msToMinutesSeconds(duration);
    });



    document.getElementById('playIcon2').onclick = function () {
        player.togglePlay();
        var heartIcon = document.getElementById("playIcon2");
        if (heartIcon.classList.contains("bi-play-circle-fill")) {
            heartIcon.classList.remove("bi-play-circle-fill");
            heartIcon.classList.add("bi-pause-circle-fill");
        } else {
            heartIcon.classList.remove("bi-pause-circle-fill");
            heartIcon.classList.add("bi-play-circle-fill");
        }
    };

    document.getElementById('prevIcon').onclick = function () {
        player.previousTrack();

    };

    document.getElementById('nextIcon').onclick = function () {
        player.nextTrack();
    };

    player.connect();

    document.getElementById('close-player-bar').addEventListener('click', function () {
        player.pause();
        player.removeListener('ready');
        player.disconnect();
        clearInterval(intervalId);
        var playerBar = document.getElementById('player-bar');
        if (playerBar) {
            playerBar.remove();
        }
        var playlistSidebar = document.getElementById('playlist-sidebar');
        if (playlistSidebar) {
            playlistSidebar.remove();
        }
    });

    document.getElementById('playlist-icon').addEventListener('click', function () {
        document.getElementById('playlist-sidebar').classList.toggle('show');
    });
}


function msToMinutesSeconds(ms) {
    // 1초는 1000 밀리초
    let seconds = Math.floor(ms / 1000);

    // 전체 초에서 분을 계산
    let minutes = Math.floor(seconds / 60);

    // 남은 초를 계산
    seconds = seconds % 60;

    // 두 자리 숫자로 형식을 맞추기 위해 padStart 사용
    let formattedSeconds = String(seconds).padStart(2, '0');

    return `${minutes}:${formattedSeconds}`;
}

// 