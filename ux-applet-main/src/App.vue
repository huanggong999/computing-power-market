<script lang="ts" setup>
import { onLaunch, onShow, onHide } from "@dcloudio/uni-app";
import { tool } from "@/utils/tool";
import { useAuth } from "@/store";
import { urlParamToJson } from "@/hook/useParam";
const Auth = useAuth();
// @ts-ignore
import { initEid } from "./mp_ecard_sdk/main.js";

onLaunch((options?) => {
	initEid();
});
onShow((options?: any) => {
	const { referrerInfo, scene } = options;
	/* 判断是否从eID数字身份⼩程序返回 */
	const { appId } = referrerInfo;
	if (scene === 1038 && appId === "wx0e2cb0b052a91c92") {
		console.log(referrerInfo.extraData.token, "人脸回调token");
		return;
	} else {
		// 执⾏接⼊⽅⼩程序原本的逻辑
		console.log("App Show");
		const queryData = uni.getEnterOptionsSync(); // uni-app版本 3.5.1+ 支持
		console.log(queryData);
		if (queryData.query.invite_user) {
			tool.set("INVITE_USER", queryData.query.invite_user);
		}
		if (queryData.query.device_code) {
			tool.set("INVITE_DEVICE", queryData.query.device_code);
		}

		if (queryData.query.token) {
		Auth.SET_TOKEN(queryData.query.token);
		}
		if (!!Auth.TOKEN) {
			Auth.GetUserInfo();
		}
		if (queryData.query.scene) {
			Auth.SET_SCENE(queryData.query.scene);
			// 如果未登录则跳转注册页
			if (!Auth.TOKEN) {
				uni.navigateTo({
					url: "/pages/transferPage/index",
				});
				// uni.navigateTo({
				// 	url: '/pages/login/register?type=register'
				// })
			}
		}
	}
});
onHide(() => {
	console.log("App Hide");
});
</script>
<style lang="scss">
@import "uview-plus/index.scss";
@import "@/styles/common.scss";

/* #ifdef H5 */
uni-page-head {
	display: none;
}

/* #endif */
page,
html,
body {
	background-color: #f3f4f6;
	font-family: PingFangSC, PingFang SC;
	-webkit-overflow-scrolling: touch !important;
	box-sizing: border-box;
	--themeColors: #3972fd;
	color: #000;
	box-sizing: border-box;
}
</style>
