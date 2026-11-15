export const saveNetworkImage = (url) => {
	uni.showLoading({ title: "下载中" });
	uni.downloadFile({
		url,
		success: (res) => {
			uni.hideLoading();
			if (res.statusCode === 200) {
				checkPermission(res.tempFilePath);
			}
		},
	});
};
export const checkPermission = (tempPath) => {
	uni.getSetting({
		success: (res) => {
			if (res.authSetting["scope.writePhotosAlbum"]) {
				doSave(tempPath);
			} else {
				uni.authorize({
					scope: "scope.writePhotosAlbum",
					success: () => doSave(tempPath),
					fail: () => uni.openSetting(),
				});
			}
		},
	});
};
// 执行保存
export const doSave = (filePath) => {	``
	uni.saveImageToPhotosAlbum({
		filePath,
		success: () => uni.showToast({ title: "保存成功" }),
		fail: (err) => console.error("保存失败", err),
	});
};
