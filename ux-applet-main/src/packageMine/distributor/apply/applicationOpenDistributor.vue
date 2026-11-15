<template>
	<NavCom title="申请成为合作伙伴" bgColor="#fff" />
	<view class="content">
		<image
			class="content-bg"
			width="100%"
			:src="$ICON('/mine/applicationOpenDistributor_bg.png')"
			mode="widthFix"
		/>

		<view class="content-form">
			<image
				class="content-form-title"
				:src="$ICON('/mine/applicationOpenDistributor_title.png')"
			/>
			<up-form
				labelPosition="top"
				:model="dataForm"
				:rules="rules"
				ref="uFormRef"
				labelWidth="120rpx"
				:labelStyle="labelStyle"
			>
				<up-form-item label="真实姓名" prop="name">
					<up-input
						:customStyle="customStyle"
						v-model="dataForm.name"
						placeholder="请输入您的真实姓名"
						border="none"
					/>
				</up-form-item>
				<up-form-item label="联系电话" prop="phone">
					<up-input
						v-model="dataForm.phone"
						border="none"
						placeholder="请输入联系电话"
						:customStyle="customStyle"
					/>
				</up-form-item>
			</up-form>

			<view
				class="content-form-agreement flex-1 fz-26 lh-36"
				@click="IsChecked = !IsChecked"
			>
				<image
					class="content-form-agreement-icon mr16"
					:src="
						$ICON(`/image/${IsChecked ? 'IsCheck' : 'check'}.png`)
					"
				/>
				我已阅读并同意
				<text @click.stop="open" class="content-form-agreement-tip">
					《AI智算云平台推广协议》
				</text>
			</view>
			<view
				class="content-form-submit mt30 fz-32 lh-44 flex"
				:class="{ active: isSubmit }"
				@click="submit"
			>
				提交申请
			</view>
		</view>

		<up-popup :show="showPopup" @close="close" round="24rpx">
			<view class="content-popup">
				<view class="flex fwb pb20 fz-34 lh-48">
					AI智算云平台推广协议
				</view>
				<view class="content-popup-agreement">
					<up-parse :content="configData.introduce" />
				</view>
				<view class="content-popup-btn mt30 between fz-28 lh-40">
					<view class="content-popup-btn-item flex" @click="close">
						不同意，暂不申请
					</view>
					<view
						class="content-popup-btn-item flex content-popup-btn-agree"
						@click="agree"
					>
						阅读并同意
					</view>
				</view>
			</view>
		</up-popup>
	</view>
</template>

<script lang="ts" setup>
import { extendApplyApi, getExtendConfigDetailApi } from "@/api/model/mine";
import { useAuth } from "@/store";
import { myShowToast } from "@/utils";
const showPopup = ref(false);
const close = () => (showPopup.value = false);
const open = () => (showPopup.value = true);

//#region ! 表单
const customStyle = reactive({
	background: "#F7F8FB",
	borderRadius: "16rpx",
	padding: "30rpx",
});
const labelStyle = reactive({
	fontSize: "26rpx",
	fontWeight: "bold",
	marginBottom: "20rpx",
});

const userInfo = useAuth().USER_INFO;
const dataForm = ref({ name: "", phone: "", userId: userInfo.userId });
const rules = reactive({
	name: [
		{
			required: true,
			message: "请输入真实姓名",
			trigger: "blur",
		},
	],
	phone: [
		{
			required: true,
			message: "请输入联系电话",
			trigger: "blur",
		},
		{
			// 自定义验证函数，见上说明
			validator: (rule: any, value: any, callback: any) =>
				uni.$u.test.mobile(value),
			message: "手机号码不正确",
			// 触发器可以同时用blur和change
			trigger: ["change", "blur"],
		},
	],
});
//#endregion

const isSubmit = computed(
	() => IsChecked.value && dataForm.value.name && dataForm.value.phone
);
const agree = () => {
	close();
	IsChecked.value = true;
};

const IsChecked = ref(false);

const uFormRef = ref();
const submit = async () => {
	if (!isSubmit.value) return myShowToast("请同意推广协议", "error");
	await uFormRef.value.validate();
	await extendApplyApi(dataForm.value);
	// 清空当前页面栈跳转
	uni.redirectTo({
		url: "/packageMine/distributor/apply/reviewStatus?status=1",
	});
};

const configData = ref<TKeyValue>({});
const getConfig = async () => {
	const { data } = await getExtendConfigDetailApi();
	configData.value = data;
};
onLoad(() => getConfig());
</script>

<style lang="scss" scoped>
.content {
	background-color: #fff;
	padding-top: 162rpx;
	position: relative;
	background: #deebfb;
	height: 100vh;
	box-sizing: border-box;

	&-bg {
		width: 100%;
	}

	&-form {
		position: absolute;
		z-index: 2;
		top: 602rpx;
		margin: auto 30rpx;
		padding: 20rpx 30rpx 48rpx;
		border-radius: 16rpx;
		background: #fff;

		&-title {
			padding: 0 20rpx 0 22rpx;
			width: 588rpx;
			height: 98rpx;
		}

		&-agreement {
			margin-top: 160rpx;

			&-icon {
				width: 28rpx;
				height: 28rpx;
			}

			&-tip {
				color: #3972fd;
			}
		}

		&-submit {
			border-radius: 16rpx;
			background: #dddddd;
			color: #ffffff;
			padding: 28rpx;
		}

		.active {
			background: #3972fd;
		}
	}

	&-popup {
		padding: 30rpx;

		&-agreement {
			height: 856rpx;
			overflow: auto;
			padding: 30rpx 24rpx;
			background: #f7f8fb;
			border-radius: 24rpx;
		}

		&-btn {
			&-item {
				border: 2rpx solid #e5e5e5;
				border-radius: 40rpx;
				width: 332rpx;
				padding: 20rpx 0;
			}

			&-agree {
				background: #3972fd;
				color: #fff;
			}
		}
	}
}
</style>
