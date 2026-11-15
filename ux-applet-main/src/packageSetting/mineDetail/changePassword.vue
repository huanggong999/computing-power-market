<template>
	<NavCom title="修改密码" bgColor="#fff" />
	<view class="content">
		<view class="fwb fz-44 lh-60">修改密码</view>
		<view class="fz-26 lh-36 mt16 tip">使用手机号重新编辑密码</view>

		<view class="content-form mt72">
			<up-form
				:model="dataForm"
				:rules="rules"
				ref="uFormRef"
				labelWidth="162rpx"
				:labelStyle="labelStyle"
			>
				<up-form-item label="手机号" prop="phone" borderBottom>
					<up-input
						:customStyle="customStyle"
						v-model="dataForm.phone"
						placeholder="请输入您的手机号"
						border="none"
					/>
				</up-form-item>
				<up-form-item label="验证码" prop="code" borderBottom>
					<up-input
						type="number"
						v-model="dataForm.code"
						border="none"
						placeholder="请输入联系电话"
						:customStyle="customStyle"
					>
						<template #suffix>
							<up-code
								ref="uCodeRef"
								@change="codeChange"
								seconds="60"
								changeText="X秒重新获取"
							></up-code>
							<view
								@click="getCode"
								class="tip"
								:class="{ activeCodeText: isSendCode }"
							>
								{{ tips }}
							</view>
						</template>
					</up-input>
				</up-form-item>
				<up-form-item label="设置密码" prop="password" borderBottom>
					<up-input
						type="password"
						:customStyle="customStyle"
						v-model="dataForm.password"
						placeholder="请输入登录密码"
						border="none"
						clearable
					/>
				</up-form-item>

				<view class="submit mt70" @click="submit"> 确认修改 </view>
			</up-form>
		</view>
	</view>
</template>

<script setup lang="ts">
import api from "@/api";
import { resetPasswordAPI } from "@/api/model/user";
import { useAuth } from "@/store";
import { myShowToast } from "@/utils";

//#region ! 表单
const customStyle = reactive({
	background: "transparent",
	padding: "30rpx",
});
const labelStyle = reactive({
	fontSize: "30rpx",
	marginBottom: "32rpx",
	marginTop: "30rpx",
});
const dataForm = ref({ phone: "", password: "", code: "", uid: "" });

const rules = reactive({
	phone: [
		{ required: true, message: "请输入手机号", trigger: "blur" },
		{
			// 自定义验证函数，见上说明
			validator: (rule: any, value: any, callback: any) =>
				uni.$u.test.mobile(value),
			message: "手机号码不正确",
			// 触发器可以同时用blur和change
			trigger: ["change", "blur"],
		},
	],
	password: [{ required: true, message: "请输入登录密码", trigger: "blur" }],
	code: [{ required: true, message: "请输入验证码", trigger: "blur" }],
});
const tips = ref("");
const codeChange = (text: any) => (tips.value = text);
const uCodeRef = ref();
const uFormRef = ref();
const isSendCode = computed(() =>
	uni.$u.test.mobile(dataForm.value.phone as unknown as number)
);
// 获取验证码参数
const codeParams = computed(() => ({
	type: "SMS",
	phone: dataForm.value.phone,
	tmsg: "1",
}));
// 获取验证码
const getCode = async () => {
	if (!isSendCode.value) return;
	if (!uCodeRef.value.canGetCode) return myShowToast("倒计时结束后再发送");
	const { data } = await api.user.getAuthCode.get(codeParams.value);
	dataForm.value.uid = data.uid;
	myShowToast("验证码已发送");
	uCodeRef.value.start();
};
//#endregion
const Auth = useAuth();

const submit = async () => {
	await uFormRef.value.validate();
	await resetPasswordAPI(dataForm.value);
	myShowToast("已修改成功，请重新登录");
	Auth.CLEAR_TOKEN();
	// 清空当前页面栈跳转
	uni.$u.sleep(2000).then(() => {
		// ! 跳转登录页
		uni.navigateTo({
			url: "/pages/login/index?isClear=true",
		});
	});
};
</script>

<style lang="scss" scoped>
.content {
	margin: 205rpx 24rpx 24rpx;
	padding: 60rpx 30rpx;
	background-color: #fff;
	border-radius: 16rpx;
	.tip {
		color: #83889d;
	}
	.activeCodeText {
		color: #3972fd;
	}
	.submit {
		padding: 20rpx 0;
		background: var(--themeColors);
		border-radius: 44rpx;
		color: #ffffff;
		font-weight: 500;
		font-size: 28rpx;
		line-height: 40rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}
}
</style>
