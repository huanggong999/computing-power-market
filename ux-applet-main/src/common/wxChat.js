import api from "@/api";
import wx from "weixin-js-sdk";
let initReady = false;
let setTimeoutNb = 0;
import { useWebView } from "@/hooks/useWebView";
const { getEnv, postMessage } = useWebView();
// 获取微信配置信息
const init = () => {
	return new Promise(async (resolve, reject) => {
		let url = window.location.href;
		const { code, data } = await api.auth.getJsSDKAtuh.get({ url });
		if (code === 1) {
			const { appId, timestamp, nonceStr, signature } = data;
			wx.config({
				debug: false,
				appId,
				timestamp,
				nonceStr,
				signature,
				jsApiList: [
					"updateAppMessageShareData",
					"updateTimelineShareData",
					"hideMenuItems",
					"scanQRCode",
				],
			});
			wx.ready(function () {
				initReady = true;
			});
			wx.error(function (res) {
				console.log(res);
			});
			resolve();
		}
	});
};

const setShare = async ({
	toPath = "",
	query = {},
	title = "四喜财神",
	desc = "四喜财神邀您参加（集财神，兑财礼）新年好运活动",
	imgUrl = "https://file.4xsm.com/static/H5/evaluating/share_img.png",
}) => {
	if (getEnv()) {
		postMessage("share", {
			title,
			desc,
			query,
		});
		return;
	}
	if (initReady) {
		let link = toPath || uni.$u.page();
		const buildData = () => {
			return {
				...query,
			};
		};
		link = `${location.origin}${link}${uni.$u.queryParams(buildData())}`;
		wx.hideMenuItems({
			menuList: ["menuItem:copyUrl"],
		});
		wx.updateAppMessageShareData({
			title, // 分享标题
			desc, // 分享描述
			link,
			imgUrl: imgUrl, // 分享图标
			success: function () {},
		});
		wx.updateTimelineShareData({
			title, // 分享标题
			link, // 分享链接
			imgUrl: imgUrl, // 分享图标
			success: function () {},
		});
	} else {
		setTimeoutNb = setTimeoutNb + 1;
		if (setTimeoutNb < 6) {
			await init();
			setTimeout(() => {
				setShare({ toPath, query, title, desc, imgUrl });
			}, 1000);
		}
	}
};
export { setShare };
