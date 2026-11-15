import http from "@/utils/request";
export default {
	appletsConsoleList: {
		url: `/applets/console/list`,
		name: "获取控制台资源列表",
		async get(data = {}) {
			return await http.get(this.url, data);
		},
	},
};
// | /pc/console/getInstancePageList { 获取云服务器实例列表 }
export const getEcsInstanceListApi = (data: TKeyValue = {}) =>
	http.get(`/pc/console/getInstancePageList`, data);

// | /pc/container/list { 获取容器列表 }
export const getContainerListApi = (data: TKeyValue = {}) =>
	http.post(`/pc/container/list`, data);

// | /pc/image/repository/list { 获取镜像列表 }
export const getImageListApi = (data: TKeyValue = {}) =>
	http.post(`/pc/image/repository/list`, data);

// | getNetWorkProductListAPI { 获取网络产品列表 }

export const getNetWorkProductListAPI = (params: any) =>
	http.get("/pc/order/page", params);

// | /pc/console/getInstanceDetail/{id} { 获取服务器实例详情 }
export const getEcsInstanceDetailApi = (id: string) =>
	http.get(`/pc/console/getInstanceDetail/${id}`);

// | /pc/container/details/{id} { 获取容器详情 }
export const getContainerDetailApi = (id: string) =>
	http.get(`/pc/container/details/${id}`);

// | /pc/image/repository/detail/{instanceName} { 获取镜像详情 }
export const getRepositoryDetailApi = (instanceName: string) =>
	http.get(`/pc/image/repository/detail/${instanceName}`);
// 获取OCI概览信息
export const getOCIOverviewApi = (instancesName: any) =>
	http.get(`/pc/image/repository/oci/overview/${instancesName}`);
