const saveData = function (key: string, data: any, that: any) {
	if (!that) {
		uni.navigateBack({ delta: 1 });
		return;
	}
	const eventChannel = that.getOpenerEventChannel();
	if (typeof eventChannel.emit !== "function") {
		uni.navigateBack({ delta: 1 });
		return;
	}
	eventChannel.emit(key, data);
	uni.navigateBack({ delta: 1 });
};
const getData = function (that: any) {
	return new Promise((resolve, reject) => {
		try {
			if (!that) return resolve({});
			const eventChannel = that.getOpenerEventChannel();
			// 监听acceptDataFromOpenerPage事件，获取上一页面通过eventChannel传送到当前页面的数据
			if (typeof eventChannel.on !== "function") {
				resolve({});
				return;
			}
			eventChannel.on("getData", (data: any) => {
				data = Object.assign({}, data);
				resolve(data);
			});
		} catch (err) {
			reject(err);
		}
	});
};
const navData = function ({
	url,
	events,
	data,
}: {
	url: string;
	events: any;
	data: any;
}) {
	uni.navigateTo({
		url,
		// 为指定事件添加一个监听器，获取被打开页面传送到当前页面的数据
		events,
		success: (res) => {
			// 通过eventChannel向被打开页面传送数据
			res.eventChannel.emit("getData", data);
		},
	});
};

export { saveData, getData, navData };
