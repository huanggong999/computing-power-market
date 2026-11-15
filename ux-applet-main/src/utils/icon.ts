import sysConfig from "@/config";
const buildIcon = function (path: string) {
	if (sysConfig.ASSETS_LOCAL && sysConfig.ASSETS_LOCAL === "true") {
		return buildImage(sysConfig.ASSETS_URL + path);
	}
	return sysConfig.ASSETS_URL + path;
};
const buildImage = (path: string) => {
	// 请注意，这不包括子目录中的文件
	return new URL(`../static/${path}`, import.meta.url).href;
};
export { buildIcon, buildImage };
