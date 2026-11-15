const files: any = import.meta.globEager("./model/*.ts"); // vite的写法
const keys = Object.keys(files);
const modules: TKeyValue = {};
keys.forEach((key: string) => {
	if (Object.prototype.hasOwnProperty.call(files, key)) {
		// 提取文件的名字作为模块名
		modules[key.replace(/(\.\/model\/|\.ts)/g, "")] = files[key].default;
	}
});
export default modules;
