import "miniprogram-api-typings";
import { buildIcon } from "./icon";
export const preloadImage = (arr?: Array<string>, isIcon: boolean = true) => {
	// const preloadList: Ref<Array<string>> = ref([]);
	const preloadList = ref<Array<string>>([]);
	if ((arr || []).length > 0) {
		preloadList.value = arr || [];
	}
	/* #ifdef MP-WEIXIN */
	wx.preloadAssets({
		data: preloadList.value.map((item: string) => {
			return {
				type: "image",
				src: isIcon ? buildIcon(item) : item,
			};
		}),
	});
	/* #endif */
	/* #ifndef MP-WEIXIN */
	preloadList.value.map((item: string) => {
		uni.getImageInfo({ src: isIcon ? buildIcon(item) : item });
	});
	/* #endif */
};

type TIcon = "success" | "loading" | "none" | "error";
export const myShowToast = (title: string, icon: TIcon = "none") =>
	uni.showToast({ title, icon, duration: 2000, mask: true });
