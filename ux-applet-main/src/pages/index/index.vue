<template>
	<view class="content">
		<MyNavTitle title="逸云数智" />
		<image class="bg" :src="$ICON('/image/home_bg.png')" />
		<view class="pd">
			<up-swiper
				:style="{ height: '266rpx' }"
				:list="bannerList"
				keyName="image"
				circular
				indicator
				radius="16rpx"
				height="266rpx"
				@change="(e:any) => (current = e.current)"
				imgMode="heightFix"
			>
				<template #indicator>
					<view class="indicator">
						<view
							class="indicator__dot"
							v-for="(item, index) in bannerList"
							:key="index"
							:class="[
								index === current && 'indicator__dot--active',
							]"
						>
						</view>
					</view>
				</template>
			</up-swiper>

			<view class="mt36"> <LatestEvents :item="activeList[0]" /> </view>
			<view class="mt36" v-if="newsList.length">
				<News :list="newsList" />
			</view>
		</view>
	</view>
	<ActivePopup
		:visible="activePopupVisible"
		:activeList="activeList"
		@close="close"
	/>
	<MyTabbar />
</template>

<script setup lang="ts">
import { getCarouselListApi } from "@/api/model/home";
import ActivePopup from "./components/ActivePopup.vue";
import LatestEvents from "./components/LatestEvents.vue";
import News from "./components/News.vue";
import { getNewsListApi } from "@/api/model/news";
import { getActiveDetailsListApi } from "@/api/model/activity";
import onShare from "@/hook/useShare";
const { onShareAppMessage, onShareTimeline } = onShare();
const bannerList = ref<TKeyValue[]>([]);
const newsList = ref<TKeyValue[]>([]);
const activeList = ref<TKeyValue[]>([]);

const current = ref(0);
// 活动弹窗
const activePopupVisible = ref(false);
const close = () => (activePopupVisible.value = false);

onLoad(() => (activePopupVisible.value = true));

const getBanner = async () => {
	const { data } = await getCarouselListApi();
	bannerList.value = data;
};

const getNews = async () => {
	const { data } = await getNewsListApi({ pageSize: 4 });
	newsList.value = data.list;
};
const getActive = async () => {
	const { data } = await getActiveDetailsListApi();
	activeList.value = data;
};

const promiseList = reactive([getBanner(), getNews(), getActive()]);

const promiseAll = async () => await Promise.all(promiseList);

onShow(() => promiseAll());
</script>

<style lang="scss" scoped>
.content {
	padding-top: 176rpx;
	position: relative;
	.bg {
		position: absolute;
		top: 0;
		width: 100%;
		height: 360rpx;
		display: block;
		z-index: 1;
	}
	.pd {
		padding: 24rpx 24rpx 0 24rpx;
		position: relative;
		z-index: 2;
	}
	.indicator {
		@include flex(row);
		justify-content: center;

		&__dot {
			height: 6px;
			width: 6px;
			border-radius: 100px;
			background-color: rgba(255, 255, 255, 0.35);
			margin: 0 5px;
			transition: background-color 0.3s;

			&--active {
				background-color: #ffffff;
			}
		}
	}
}
</style>
