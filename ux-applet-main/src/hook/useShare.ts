import { useAuth } from "@/store";
import { buildIcon } from "@/utils/icon";
const Auth = useAuth();
type onShareProps = {
	currentTitle: Ref<string>;
	currentImage: Ref<string>;
	currentPath: Ref<string>;
	currentId: Ref<string>;
	otherParams: Ref<any>;
	onShareAppMessage: (ShareAppMessageOption: any) => void;
	onShareTimeline: (ShareAppMessageOption: any) => void;
	resetData: () => void;
};
const onShare = (path?: string, isDefaultImage?: boolean): onShareProps => {
	let currentTitle: Ref<string> = ref("逸云智算");
	let currentId: Ref<string> = ref("");
	let currentImage: Ref<string> = ref("");
	let currentPath: Ref<string> = ref(path || "/pages/index/index");
	let otherParams: Ref<any> = ref({});
	if (isDefaultImage) {
		currentImage.value = buildIcon("/share.webp");
	}
	const buildData = () => {
		return {
			invite_user: Auth.USER_INFO?.id || "",
			id: currentId.value,
			...otherParams.value,
		};
	};
	onShareAppMessage(() => {
		return {
			title: currentTitle.value,
			imageUrl: currentImage.value,
			path: `${currentPath.value}${uni.$u.queryParams(buildData())}`,
		};
	});
	onShareTimeline(() => {
		return {
			title: currentTitle.value,
			imageUrl: currentImage.value,
			path: `${currentPath.value}${uni.$u.queryParams(buildData())}`,
		};
	});
	const resetData = () => {
		setTimeout(() => {
			currentTitle.value = "逸云智算";
			currentPath.value = path || "/pages/index/index";
			currentId.value = "";
		}, 0);
	};

	return {
		onShareAppMessage,
		onShareTimeline,
		resetData,
		currentId,
		currentTitle,
		currentImage,
		currentPath,
		otherParams,
	};
};
export default onShare;
