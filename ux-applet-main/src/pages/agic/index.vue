<template>
	<view class="content">
		<MyNavTitle title="AGI-C" />
		<agicCard v-for="item in dataList" :key="item.id" :info="item" @consult="openConsultDialog"></agicCard>
	</view>
	<MyTabbar />
	<view class="dialog" v-if="dialogVisible">
		<image class="title" :src="$ICON('/image/cosult-title.png')" />
		<view class="dialog-form">
			<formCreateUni ref="formCreateRef" :option="option"></formCreateUni>
		</view>

		<view class="form-agreement flex-1 fz-26 lh-36	x" @click="IsChecked = !IsChecked">
			<image class="form-agreement-icon mr16" :src="$ICON(`/image/${IsChecked ? 'IsCheck' : 'check'}.png`)
				" />
			我已阅读并同意
			<text @click.stop="open" class="form-agreement-tip">
				《AI智算云平台推广协议》
			</text>
		</view>
		<button @click="getFormData" class="dialog-btn mt20 mb20">确认</button>
		<view class="dialog-tip">表单提交后，会有工作人员与您联系</view>
		<view class="dialog-tip">如果您现在就需要帮助，可以与我们客服联系</view>
		<view class="dialog-close" @click="dialogVisible = false">X</view>
	</view>
	<view class="mask" v-if="dialogVisible"></view>
	<serviceDialog v-if="codeDialogVisible" @close="closeCodeDialog"></serviceDialog>
</template>

<script setup lang="ts">
import formCreateUni from '@/uni_modules/form-create-uni_1.0.0/components/form-create-uni/form-create-uni.vue';
import { usePageList } from '@/hook/pageList';
import agicCard from './components/agicCard.vue';
import serviceDialog from './components/serviceDialog.vue';
import { getNetWorkProductFormAPI, getNetWorkProductListAPI, submitNetWorkFormApi } from '@/api/model/networkProduct.ts';
const { dataList, getList } = usePageList({ api: getNetWorkProductListAPI, requestAuto: false });
const formCreateRef = ref()
const option = ref<any>({})
const rule = ref<any>()
const dialogVisible = ref(false)
const codeDialogVisible = ref(false)
const IsChecked = ref(false)
const submit = (formData: any) => { // 点击组件内置提交按钮后的事件

	console.log(JSON.stringify(formData)); // [{"type":"input","field":"Ffyi631rgf2hn","title":"输入框","info":"","$required":true,"_fc_drag_tag":"input","hidden":false,"display":true,"values":"111"},{"type":"inputNumber","field":"F78g6323ffje1","title":"计数器","info":"","$required":false,"_fc_drag_tag":"inputNumber","hidden":false,"display":true},{"type":"radio","field":"Fo0p6323ffxyk","title":"单选框","info":"","effect":{"fetch":""},"$required":false,"options":[{"label":"选项1","value":1},{"label":"选项2","value":2}],"_fc_drag_tag":"radio","hidden":false,"display":true,"values":1},{"type":"checkbox","field":"Fjfh6323fgc5h","title":"多选框","info":"","effect":{"fetch":""},"$required":false,"options":[{"label":"选项1","value":1},{"label":"选项2","value":2}],"_fc_drag_tag":"checkbox","hidden":false,"display":true,"values":[1,2]},{"type":"select","field":"Fork1oukyaqp1g","title":"选择器","info":"","effect":{"fetch":""},"$required":false,"options":[{"label":"选项1","value":1},{"label":"选项2","value":2}],"_fc_drag_tag":"select","hidden":false,"display":true},{"type":"switch","field":"Fsuh1oukyatz0r","title":"开关","info":"","$required":false,"_fc_drag_tag":"switch","hidden":false,"display":true},{"type":"timePicker","field":"Fnnm1oukyaz4tq","title":"时间选择器","info":"","$required":false,"_fc_drag_tag":"timePicker","hidden":false,"display":true},{"type":"datePicker","field":"Fud81oukyb2p59","title":"日期选择器","info":"","$required":false,"_fc_drag_tag":"datePicker","hidden":false,"display":true},{"type":"rate","field":"Fnjf1oukyb7tps","title":"评分","info":"","$required":false,"_fc_drag_tag":"rate","hidden":false,"display":true,"value":0,"values":2},{"type":"colorPicker","field":"F5zc1oukybcmk3","title":"颜色选择器","info":"","$required":false,"_fc_drag_tag":"colorPicker","hidden":false,"display":true},{"type":"upload","field":"Futt1oukybhfh6","title":"上传","info":"","$required":false,"props":{"action":""},"_fc_drag_tag":"upload","hidden":false,"display":true}]

}
const closeCodeDialog = () => {
	codeDialogVisible.value = false
}
const getFormData = () => { // 在不点击内置的提交按钮的情况下，获取表单内输入的数据值，表单校验将不起作用
	if (!IsChecked.value) {
		uni.showToast({
			title: '请先阅读并同意《AI智算云平台推广协议》',
			icon: 'none'
		})
		return
	}
	const formData = JSON.stringify(formCreateRef.value.getFormData())
	// 给每个"前面加一个斜杠/

	submitNetWorkFormApi({
		productId: curId.value,
		json: formData
	}).then((res: any) => {
		if (res.code == 200) {
			uni.showToast({
				title: '提交成功',
				icon: 'none'
			})
			dialogVisible.value = false
			codeDialogVisible.value = true
		} else {
			uni.showToast({
				title: res.msg,
				icon: 'none'
			})
		}
	})
	// console.log(JSON.stringify(formData)); // [{"type":"input","field":"Ffyi631rgf2hn","title":"输入框","info":"","$required":true,"_fc_drag_tag":"input","hidden":false,"display":true,"values":"111"},{"type":"inputNumber","field":"F78g6323ffje1","title":"计数器","info":"","$required":false,"_fc_drag_tag":"inputNumber","hidden":false,"display":true},{"type":"radio","field":"Fo0p6323ffxyk","title":"单选框","info":"","effect":{"fetch":""},"$required":false,"options":[{"label":"选项1","value":1},{"label":"选项2","value":2}],"_fc_drag_tag":"radio","hidden":false,"display":true,"values":1},{"type":"checkbox","field":"Fjfh6323fgc5h","title":"多选框","info":"","effect":{"fetch":""},"$required":false,"options":[{"label":"选项1","value":1},{"label":"选项2","value":2}],"_fc_drag_tag":"checkbox","hidden":false,"display":true,"values":[1,2]},{"type":"select","field":"Fork1oukyaqp1g","title":"选择器","info":"","effect":{"fetch":""},"$required":false,"options":[{"label":"选项1","value":1},{"label":"选项2","value":2}],"_fc_drag_tag":"select","hidden":false,"display":true},{"type":"switch","field":"Fsuh1oukyatz0r","title":"开关","info":"","$required":false,"_fc_drag_tag":"switch","hidden":false,"display":true},{"type":"timePicker","field":"Fnnm1oukyaz4tq","title":"时间选择器","info":"","$required":false,"_fc_drag_tag":"timePicker","hidden":false,"display":true},{"type":"datePicker","field":"Fud81oukyb2p59","title":"日期选择器","info":"","$required":false,"_fc_drag_tag":"datePicker","hidden":false,"display":true},{"type":"rate","field":"Fnjf1oukyb7tps","title":"评分","info":"","$required":false,"_fc_drag_tag":"rate","hidden":false,"display":true,"value":0,"values":2},{"type":"colorPicker","field":"F5zc1oukybcmk3","title":"颜色选择器","info":"","$required":false,"_fc_drag_tag":"colorPicker","hidden":false,"display":true},{"type":"upload","field":"Futt1oukybhfh6","title":"上传","info":"","$required":false,"props":{"action":""},"_fc_drag_tag":"upload","hidden":false,"display":true}]

}
// /** 初始化 **/
// onMounted(() => {
// 	// 表单整体属性值，通过组件属性注入
option.value = { "form": { "labelPosition": "right", "size": "small", "labelWidth": "75px", } }
// 	// 表单内各组件，需要通过方法来设值，因为设值后，需要动态生成表单验证内容
// 	const rule = [{ "type": "input", "field": "Ffyi631rgf2hn", "title": "输入框", "info": "", "$required": true, "_fc_drag_tag": "input", "hidden": false, "display": true }, { "type": "inputNumber", "field": "F78g6323ffje1", "title": "计数器", "info": "", "$required": false, "_fc_drag_tag": "inputNumber", "hidden": false, "display": true }, { "type": "radio", "field": "Fo0p6323ffxyk", "title": "单选框", "info": "", "effect": { "fetch": "" }, "$required": false, "options": [{ "label": "选项1", "value": 1 }, { "label": "选项2", "value": 2 }], "_fc_drag_tag": "radio", "hidden": false, "display": true }, { "type": "checkbox", "field": "Fjfh6323fgc5h", "title": "多选框", "info": "", "effect": { "fetch": "" }, "$required": false, "options": [{ "label": "选项1", "value": 1 }, { "label": "选项2", "value": 2 }], "_fc_drag_tag": "checkbox", "hidden": false, "display": true }, { "type": "select", "field": "Fork1oukyaqp1g", "title": "选择器", "info": "", "effect": { "fetch": "" }, "$required": false, "options": [{ "label": "选项1", "value": 1 }, { "label": "选项2", "value": 2 }], "_fc_drag_tag": "select", "hidden": false, "display": true }, { "type": "switch", "field": "Fsuh1oukyatz0r", "title": "开关", "info": "", "$required": false, "_fc_drag_tag": "switch", "hidden": false, "display": true }, { "type": "timePicker", "field": "Fnnm1oukyaz4tq", "title": "时间选择器", "info": "", "$required": false, "_fc_drag_tag": "timePicker", "hidden": false, "display": true }, { "type": "datePicker", "field": "Fud81oukyb2p59", "title": "日期选择器", "info": "", "$required": false, "_fc_drag_tag": "datePicker", "hidden": false, "display": true }, { "type": "rate", "field": "Fnjf1oukyb7tps", "title": "评分", "info": "", "$required": false, "_fc_drag_tag": "rate", "hidden": false, "display": true, "value": 0 }, { "type": "colorPicker", "field": "F5zc1oukybcmk3", "title": "颜色选择器", "info": "", "$required": false, "_fc_drag_tag": "colorPicker", "hidden": false, "display": true }, { "type": "upload", "field": "Futt1oukybhfh6", "title": "上传", "info": "", "$required": false, "props": { "action": "" }, "_fc_drag_tag": "upload", "hidden": false, "display": true }]
// 	formCreateRef.value.showForm(rule)
// })

onMounted(() => {
	getList()
})
// 当前id
const curId = ref()
// 打开咨询表单
const openConsultDialog = (id: string) => {
	curId.value = id
	getNetWorkProductFormAPI(id).then((res: any) => {
		rule.value = JSON.parse(res.data.json)
		formCreateRef.value.showForm(rule.value)
	})
	dialogVisible.value = true;
	console.log('openConsultDialog', id);
}
</script>

<style lang="scss" scoped>
.content {
	background: linear-gradient(180deg, #e6eefe 0%, #f3f4f6 100%);
	padding: 0 24rpx;
	padding-top: 206rpx;
	padding-bottom: 30rpx;
}

/* 居中弹窗 */
.dialog {
	position: fixed;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	z-index: 11;
	width: 590rpx;
	height: 1058rpx;
	background: #FFFFFF;
	border-radius: 16rpx 16rpx 16rpx 16rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 20rpx 40rpx;

	.title {
		width: 592rpx;
		height: 100rpx;
		background: #FFFFFF;
		border-radius: 16rpx 16rpx 16rpx 16rpx;
	}

	.dialog-form {
		flex: 1;
		overflow: auto;
	}

	.form-agreement {
		margin-top: 40rpx;
		display: flex;
		align-items: center;
		gap: 20rpx;

		&-icon {
			width: 28rpx;
			height: 28rpx;
		}

		&-tip {
			color: #3972fd;
		}
	}

	&-btn {
		width: 610rpx;
		height: 64rpx;
		background: #3972FD;
		border-radius: 32rpx 32rpx 32rpx 32rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		font-weight: 400;
		font-size: 26rpx;
		color: #FFFFFF;
	}

	&-tip {

		font-weight: 400;
		font-size: 28rpx;

	}

	&-close {
		position: absolute;
		top: 20rpx;
		right: 20rpx;
		font-size: 32rpx;
		color: #999999;
	}
}

/* 遮罩层 */
.mask {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: rgba(0, 0, 0, 0.5);
	z-index: 10;
	overflow: hidden;
}
</style>
