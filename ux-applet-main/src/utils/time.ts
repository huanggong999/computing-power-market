/**
/*对Date的扩展，将 Date 转化为指定格式的String
/* 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
/* 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
/* 例子：
/* (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2019-01-02 10:19:04.423
/* (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2019-1-2 10:19:4.18
*/
interface FormatOptions<T = any> {
	[key: string]: T;
}
// eslint-disable-next-line no-extend-native, func-names
Date.prototype.format = function (fmt: any) {
	let temp = fmt;
	const o: FormatOptions = {
		"M+": this.getMonth() + 1, // 月份
		"d+": this.getDate(), // 日
		"H+": this.getHours(), // 小时
		"m+": this.getMinutes(), // 分
		"s+": this.getSeconds(), // 秒
		"q+": Math.floor((this.getMonth() + 3) / 3), // 季度
		S: this.getMilliseconds(), // 毫秒
	};
	if (/(y+)/.test(temp))
		temp = temp.replace(
			RegExp.$1,
			`${this.getFullYear()}`.substr(4 - RegExp.$1.length)
		);
	// eslint-disable-next-line no-restricted-syntax
	for (const k in o) {
		if (new RegExp(`(${k})`).test(temp))
			temp = temp.replace(
				RegExp.$1,
				RegExp.$1.length === 1
					? o[k]
					: `00${o[k]}`.substr(`${o[k]}`.length)
			);
	}

	return temp;
};
