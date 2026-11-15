export const urlParamToJson = (url: string) => {
	if (!url) {
		return {};
	}
	let json: TKeyValue = {};
	url.split("&").forEach(
		(item) => (json[item.split("=")[0]] = item.split("=")[1])
	);
	return json;
};
