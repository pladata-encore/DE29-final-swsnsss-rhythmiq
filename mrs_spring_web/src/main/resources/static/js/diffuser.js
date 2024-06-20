async function getPlaylistArt(data) {
	try {
		const response = await fetch(
			"https://api-inference.huggingface.co/models/stabilityai/stable-diffusion-xl-base-1.0",
			{
				headers: {
					Authorization: "Bearer 허깅 페이스에서 생성한 Accesstoken을 입력해주세요",
					"Content-Type": "application/json"
				},
				method: "POST",
				body: JSON.stringify(data),
			}
		);
		
		if (!response.ok) {
			throw new Error(`HTTP error! status: ${response.status}`);
		}

		const result = await response.blob();
		
		return result;
	} catch (error) {
		console.error("Error querying the API:", error);
	}
}

function saveBlobToLocalStorage(blob) {
    const reader = new FileReader();
    reader.onloadend = function () {
        localStorage.setItem("playlistCoverBlob", reader.result);
    };
    reader.readAsDataURL(blob);
}

// 저장된 이미지 블롭을 로컬 스토리지에서 불러오기

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