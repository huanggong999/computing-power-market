export {};
declare global {
	interface Date {
		format?: (fmt: string) => string;
	}
}

declare module "vue" {
	interface ComponentCustomProperties {
		/**
		 * 获取图标路径
		 * @param {String} path 图标路径
		 */
		$ICON: (path: string) => string;
	}
}
declare module "./auto-import" {
	import { Plugin } from "vue";
	const AutoImport: Plugin;
	export default AutoImport;
}

declare module "uview-plus" {
	interface $u {
		/**
		 * 延时一定时间进行回调，类似于promise的使用方式
		 * @param {Number} value 数值，单位默认为ms
		 */
		sleep: (value: Number) => Promise<any>;
		/**
		 * 将一个对象形式参数转换成get传参所需参数形式，如把{name: 'lisa', age: 20}转换成?name=lisa&age=20
		 * @param {Object} data 开始的颜色
		 * @param {Boolean} isPrefix 结束的颜色
		 * @param {Boolean | String} arrayFormat 颜色等分的份额
		 */
		queryParams: (
			data: TKeyValue,
			isPrefix?: Boolean,
			arrayFormat?: Boolean | "indices" | "brackets" | "repeat" | "comma"
		) => string;
		/**
		 * 获取当前页面路径
		 */
		page: () => string;
		/**
		 * 用于获取用户传递值的px值  如果用户传递了"xxpx"或者"xxrpx"，取出其数值部分，如果是"xxxrpx"还需要用过uni.rpx2px进行转换
		 * @param {number|string} value 用户传递值的px值
		 * @param {boolean} unit
		 * @returns {number|string}
		 */
		getPx: (value: number | string, unit?: boolean) => any;
	}
}
