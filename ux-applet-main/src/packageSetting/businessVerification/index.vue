<template>
	<NavCom title="实名认证" bgColor="#fff" />
	<view class="choose" v-if="authStep == 1">
		<view class="label">企业认证</view>
		<view class="tip">
			账号归属于企业，适用于企业、政府、事业单位、团体、组织等，完成实名认证可开增值税普通发票、增值税专用发票、组织（非企业）增值税普通发票
		</view>
		<view class="card">
			<view class="info">
				<view class="title">企业证件认证</view>
				<view class="option">企业证件附件 | 法人信息</view>
			</view>
			<view class="btn" @click="toAuth(1)">立即认证</view>
		</view>
		<!-- <view class="card">
			<view class="info">
				<view class="title">法人人脸识别</view>
				<view class="option">企业证件附件 | 法人信息 | 人脸识别</view>
			</view>
			<view class="btn" @click="toAuth(2)">立即认证</view>
		</view> -->
		<view class="label">个人认证</view>
		<view class="tip">
			适用于个人用户，账号归属于个人，认证后可按需升级企业认证
		</view>
		<view class="card">
			<view class="info">
				<view class="title">个人证件认证</view>
				<view class="option">身份证信息校验</view>
			</view>
			<view class="btn" @click="toAuth(3)">立即认证</view>
		</view>
		<!-- <view class="card">
			<view class="info">
				<view class="title">人脸识别</view>
				<view class="option">身份证信息校验 | 人脸识别</view>
			</view>
			<view class="btn" @click="toAuth(4)">立即认证</view>
		</view> -->
	</view>
	<!-- 企业要素认证 -->
	<view
		class="content"
		v-else-if="authStep == 2 && (authType == 1 || authType == 2)"
	>
		<view class="content-license">
			<view class="fwb fz-30 lh-42"> 营业执照 </view>
			<view class="content-license-upload mt20" @click="uploadLicense">
				<view class="img flex">
					<up-image
						width="240rpx"
						height="160rpx"
						:src="licenseImage"
						bgColor="transparent"
					/>
				</view>
				<view class="btn"> 上传工商营业执照 </view>
			</view>
		</view>
		<view class="content-form mt20">
			<up-form
				:model="companyInfo"
				:rules="companyRules"
				ref="uFormRef"
				labelPosition="top"
				:labelStyle="labelStyle"
				labelWidth="220rpx"
			>
				<up-form-item label="企业名称" prop="companyName" required>
					<up-input
						:customStyle="customStyle"
						v-model="companyInfo.companyName"
						placeholder="请输入企业名称"
						border="none"
					/>
				</up-form-item>
				<up-form-item
					label="统一社会信用代码"
					prop="companyCode"
					required
				>
					<up-input
						:customStyle="customStyle"
						v-model="companyInfo.companyCode"
						placeholder="请输入企业信用代码"
						border="none"
					/>
				</up-form-item>
				<up-form-item
					label="企业注册地址"
					prop="companyAddress"
					required
				>
					<up-input
						:customStyle="customStyle"
						v-model="companyInfo.companyAddress"
						placeholder="请输入企业注册地址"
						border="none"
					/>
				</up-form-item>
				<up-form-item label="法人姓名" prop="name" required>
					<up-input
						:customStyle="customStyle"
						v-model="companyInfo.name"
						placeholder="请输入法人姓名"
						border="none"
					/>
				</up-form-item>
				<up-form-item label="法人身份证号码" prop="idCard" required>
					<up-input
						type="number"
						:customStyle="customStyle"
						v-model="companyInfo.idCard"
						placeholder="请输入身份证号码"
						border="none"
					/>
				</up-form-item>
			</up-form>
		</view>
	</view>
	<!-- 个人要素认证 -->
	<view
		class="content"
		v-else-if="authStep == 2 && (authType == 3 || authType == 4)"
	>
		<view class="content-license">
			<view class="fwb fz-30 lh-42"> 营业执照 </view>
			<view class="flex">
				<view
					class="content-license-upload mt20"
					@click="uploadIdCard(1)"
				>
					<view class="img flex">
						<up-image
							width="240rpx"
							height="160rpx"
							:src="frontImg"
							bgColor="transparent"
						/>
					</view>
					<view class="btn"> 上传人像面 </view>
				</view>
				<view
					class="content-license-upload mt20 ml20"
					@click="uploadIdCard(2)"
				>
					<view class="img flex">
						<up-image
							width="240rpx"
							height="160rpx"
							:src="backImg"
							bgColor="transparent"
						/>
					</view>
					<view class="btn"> 上传国徽面 </view>
				</view>
			</view>
		</view>
		<view class="content-form mt20">
			<up-form
				:model="personInfo"
				:rules="personRules"
				ref="uFormRef"
				labelPosition="top"
				:labelStyle="labelStyle"
				labelWidth="220rpx"
			>
				<up-form-item label="姓名" prop="name" required>
					<up-input
						:customStyle="customStyle"
						v-model="personInfo.name"
						placeholder="请输入姓名"
						border="none"
					/>
				</up-form-item>
				<up-form-item label="身份证" prop="idCard" required>
					<up-input
						:customStyle="customStyle"
						v-model="personInfo.idCard"
						placeholder="请输入身份证号"
						border="none"
					/>
				</up-form-item>
			</up-form>
		</view>
	</view>
	<view class="footer" v-if="authStep == 2">
		<view class="footer-btn back" @click="backStep"> 上一步 </view>
		<view class="footer-btn next" @click="submit"> 确认 </view>
	</view>
</template>

<script setup lang="ts">
import auth, { applyRealNameVerifyApi } from "@/api/model/auth";
import { uploadIdCardApi, uploadOcrApi } from "@/api/model/file";
import { companyVerifyApi } from "@/api/model/mine";
import { useAuth } from "@/store";
import { myShowToast } from "@/utils";
import { buildIcon } from "@/utils/icon";
const authStep = ref(1);
const authType = ref(1);
const companyInfo = ref({
	companyName: "", // 企业名称
	companyCode: "", // 统一社会信用代码
	companyAddress: "", // 企业地址
	companyImg: "", // 营业执照图片
	name: "", // 法定代表人姓名
	idCard: "", // 法定代表人手机号
});
const personInfo = ref({
	name: "",
	idCard: "",
	frontImg: "", // 人像面图片
	backImg: "", // 国徽面图片
});
const AuthInfo = useAuth();
const toAuth = (type: number) => {
	authType.value = type;
	authStep.value = 2;
};
// 上传营业执照
const uploadLicense = async () => {
	try {
		const { tempFilePaths } = await uni.chooseImage({ count: 1 });
		if (!tempFilePaths || tempFilePaths.length === 0) return;
		uni.showLoading({ title: "正在上传" });
		const { data } = await uploadOcrApi(tempFilePaths[0]);
		if (!data?.ocrzs?.data?.license_main)
			return myShowToast("OCR识别失败，未获取到营业执照信息");

		const {
			住所: companyAddress = "",
			统一社会信用代码: companyCode = "",
			法定代表人: companyContactName = "",
			名称: companyName = "",
		} = data.ocrzs.data.license_main;

		companyInfo.value = {
			companyAddress,
			companyCode,
			name: companyContactName,
			idCard: "", // 保持原逻辑，默认空字符串
			companyImg: data.url,
			companyName,
		};
		myShowToast("上传成功");
	} catch (error) {
		myShowToast("上传失败，请重试");
		console.error("Error uploading image:", error);
	} finally {
		uni.hideLoading();
	}
};
// 上传身份证
const uploadIdCard = async (type: number) => {
	try {
		const { tempFilePaths } = await uni.chooseImage({ count: 1 });
		if (!tempFilePaths || tempFilePaths.length === 0) return;
		uni.showLoading({ title: "正在上传" });
		const { data } = await uploadIdCardApi(tempFilePaths[0]);
		if (!data?.ocrzs?.data?.card_front && type == 1)
			return myShowToast("信息识别失败，未获取到身份证信息");
		if (!data?.ocrzs?.data?.card_back && type == 2)
			return myShowToast("信息识别失败，未获取到身份证信息");
		if (type == 1 && data.ocrzs.data.card_front.name) {
			const { name, id_number: idCard } = data.ocrzs.data.card_front;
			personInfo.value = {
				name,
				idCard,
				frontImg: data.url,
				backImg: personInfo.value.backImg, // 保持原逻辑，国徽面图片不变
			};
			myShowToast("上传成功");
		} else if (type == 2 && data.ocrzs.data.card_back.valid_period) {
			personInfo.value.backImg = data.url;
			myShowToast("上传成功");
		} else {
			throw new Error("未获取到身份证信息");
		}
	} catch (error) {
		myShowToast("上传失败，请检查是否符合条件");
		console.error("Error uploading image:", error);
	} finally {
		uni.hideLoading();
	}
};
const licenseImage = computed(() =>
	companyInfo.value.companyImg !== ""
		? companyInfo.value.companyImg
		: buildIcon("/mine/license_upload_bg.png")
);
const frontImg = computed(() =>
	personInfo.value.frontImg !== ""
		? personInfo.value.frontImg
		: buildIcon("/mine/license_upload_bg.png")
);
const backImg = computed(() =>
	personInfo.value.backImg !== ""
		? personInfo.value.backImg
		: buildIcon("/mine/license_upload_bg.png")
);
//  判断是否可以提交 companyInfo 的值是否都不为空
const isSubmit = computed(() => {
	// 企业认证
	if (authType.value == 1 || authType.value == 2) {
		return Object.values(companyInfo.value).every((item) => item !== "");
	}
	// 个人认证
	if (authType.value == 3 || authType.value == 4) {
		return Object.values(personInfo.value).every((item) => item !== "");
	}
});
//#region ! 表单
const customStyle = reactive({
	background: "#F7F8FB",
	borderRadius: "8rpx",
	padding: "14rpx 20rpx",
});
const labelStyle = reactive({
	fontSize: "26rpx",
	fontWeight: "bold",
	marginBottom: "16rpx",
});
const companyRules = reactive({
	companyName: [
		{
			required: true,
			message: "请输入企业名称",
			trigger: ["change", "blur"],
		},
	],
	companyCode: [
		{
			required: true,
			message: "请输入企业信用代码",
			trigger: ["change", "blur"],
		},
	],
	companyAddress: [
		{
			required: true,
			message: "请输入企业注册地址",
			trigger: ["change", "blur"],
		},
	],
	name: [
		{
			required: true,
			message: "请输入法人姓名",
			trigger: ["change", "blur"],
		},
	],
	idCard: [
		{
			required: true,
			message: "请输入身份证",
			trigger: ["change", "blur"],
		},
		// {
		// 	// 自定义验证函数，见上说明
		// 	validator: (rule: any, value: any, callback: any) =>
		// 		uni.$u.test.mobile(value),
		// 	message: "手机号码不正确",
		// 	// 触发器可以同时用blur和change
		// 	trigger: ["change", "blur"],
		// },
	],
});
const personRules = reactive({
	name: [
		{
			required: true,
			message: "请输入姓名",
			trigger: ["change", "blur"],
		},
	],
	idCard: [
		{
			required: true,
			message: "请输入身份证号码",
			trigger: ["change", "blur"],
		},
	],
});
const uFormRef = ref();

//#endregion
const Auth = useAuth();
const backStep = () => {
	if (authStep.value === 1) {
		uni.navigateBack();
	} else {
		authStep.value = 1;
	}
};
// 根据当前authStep和authType来判断下一步操作
const submit = async () => {
	if (!isSubmit.value) return;
	// 需要人脸识别
	if (authType.value == 2 || authType.value == 4) {
		toAutoNym();
		return;
	}
	// 不需要人脸识别
	notToAuthNym();
	Auth.GetUserInfo();
	// uni.$u.sleep(2000).then(() => uni.navigateBack());
};
// 不去人脸认证
const notToAuthNym = () => {
	// 调用实名认证接口获取eToken
	const data: any = {
		certType: authType.value == 1 || authType.value == 2 ? "2" : "1", // 2: 企业认证 1: 个人认证
		isFace: 0,
	};
	if (authType.value == 1 || authType.value == 2) {
		data.companyName = companyInfo.value.companyName;
		data.companyCode = companyInfo.value.companyCode;
		data.companyAddress = companyInfo.value.companyAddress;
		data.companyImg = companyInfo.value.companyImg;
		data.name = companyInfo.value.name;
		data.idCard = companyInfo.value.idCard;
	} else {
		data.name = personInfo.value.name;
		data.idCard = personInfo.value.idCard;
	}
	applyRealNameVerifyApi(data).then((res: any) => {
		if (res.code == 200 && res.data.success) {
			myShowToast("实名认证成功");
			uni.$u.sleep(2000).then(() => {
				uni.navigateBack();
			});
		} else {
			if (res.data.failReason) {
				myShowToast(res.data.failReason, "error");
				return;
			}
			myShowToast("请检查信息是否有效", "error");
		}
	});
};
// 去人脸认证
const toAutoNym = () => {
	// 调用实名认证接口获取eToken
	const data: any = {
		certType: authType.value == 1 || authType.value == 2 ? "2" : "1", // 2: 企业认证 1: 个人认证
		isFace: 1,
	};
	if (authType.value == 1 || authType.value == 2) {
		data.companyName = companyInfo.value.companyName;
		data.companyCode = companyInfo.value.companyCode;
		data.companyAddress = companyInfo.value.companyAddress;
		data.companyImg = companyInfo.value.companyImg;
		data.name = companyInfo.value.name;
		data.idCard = companyInfo.value.idCard;
	} else {
		data.name = personInfo.value.name;
		data.idCard = personInfo.value.idCard;
	}
	applyRealNameVerifyApi(data).then((res: any) => {
		if (res.code == 200 && res.data.success) {
			uni.navigateTo({
				url: `/packageSetting/businessVerification/autonym?eToken=${res.data.eidToken}`,
			});
		} else {
			if (res.data.failReason) {
				myShowToast(res.data.failReason, "error");
				return;
			}
			myShowToast("请检查信息是否有效", "error");
		}
	});
};
</script>

<style lang="scss" scoped>
.choose {
	padding: 24rpx;
	padding-top: 206rpx;
	display: flex;
	flex-direction: column;

	.label {
		font-weight: bold;
		font-size: 36rpx;
		margin-bottom: 12rpx;
	}

	.tip {
		margin-bottom: 30rpx;
		font-weight: 400;
		font-size: 24rpx;
		color: #666666;
	}

	.card {
		display: flex;
		padding: 30rpx;
		align-items: center;
		justify-content: space-between;
		width: 86vw;
		height: 108rpx;
		background: #ffffff;
		border-radius: 16rpx 16rpx 16rpx 16rpx;
		margin-bottom: 20rpx;

		.info {
			display: flex;
			flex-direction: column;
			gap: 20rpx;

			.title {
				font-weight: bold;
				font-size: 32rpx;
			}

			.option {
				font-weight: 400;
				font-size: 24rpx;
				color: #3972fd;
			}
		}

		.btn {
			width: 172rpx;
			height: 68rpx;
			background: #3972fd;
			border-radius: 34rpx 34rpx 34rpx 34rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			color: #fff;
			font-weight: 400;
			font-size: 26rpx;
		}
	}
}

.content {
	padding: 24rpx;
	padding-top: 206rpx;

	&-license {
		padding: 30rpx 24rpx;
		background-color: #fff;
		border-radius: 16rpx;

		&-upload {
			background: #f8f8f8;
			width: 320rpx;

			.img {
				padding: 28rpx 0 26rpx;
			}

			.btn {
				font-size: 28rpx;
				color: #ffffff;
				line-height: 40rpx;
				text-align: center;
				border-radius: 0rpx 0rpx 8rpx 8rpx;
				background: #3972fd;
				width: 100%;
				padding: 10rpx 0;
			}
		}
	}

	&-form {
		padding: 0 24rpx 30rpx;
		border-radius: 16rpx;
		background-color: #fff;
	}

	&-submit {
		padding: 20rpx 0;
		background: #d9d9d9;
		border-radius: 44rpx;
		color: #ffffff;
		font-weight: 500;
		font-size: 28rpx;
		line-height: 40rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.active {
		background: var(--themeColors);
	}
}

.footer {
	display: flex;
	gap: 22rpx;
	align-items: center;
	padding: 24rpx;

	.footer-btn {
		width: 340rpx;
		height: 80rpx;
		font-weight: 500;
		font-size: 28rpx;
		border-radius: 44rpx 44rpx 44rpx 44rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.back {
		border: 1rpx solid #3972fd;
		color: #3972fd;
		background-color: #ffffff;
	}

	.next {
		background: #3972fd;
		color: #ffffff;
	}

	.active {
		background: var(--themeColors);
	}
}
</style>
