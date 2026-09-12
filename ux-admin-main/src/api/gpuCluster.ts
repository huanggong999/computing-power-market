/** * @description: 集群管理 */
import request from "@/utils/request";

// | GPU集群节点列表
export const gpuClusterNodesApi = async (params: any = {}): Res => {
  return request.get("/system/gpu/cluster/node/page", { params });
};

// | 集群列表
export const gpuClusterListApi = async (params: any = {}): Res => {
  return request.get("/system/gpu/cluster/page", { params });
};

// | 集群详情
export const gpuClusterSummaryApi = async (params: any = {}): Res => {
  return request.get("/system/gpu/cluster/summary", { params });
};

// | 集群监控总览
export const gpuClusterMonitorOverviewApi = async (): Res => {
  return request.get("/system/gpu/cluster/monitor/overview");
};

// | 节点池管理
export const gpuNodePoolListApi = async (params: any = {}): Res => {
  return request.get("/system/gpu/cluster/node-pool/page", { params });
};

// | 组件管理：基础组件镜像列表
export const gpuComponentListApi = async (params: any = {}): Res => {
  return request.get("/system/gpu/cluster/component/page", { params });
};

// | 组件管理：新增基础组件镜像
export const gpuComponentSaveApi = async (data: any): Res => {
  return request.post("/system/gpu/cluster/component/save", data);
};

// | 组件管理：编辑基础组件镜像
export const gpuComponentUpdateApi = async (data: any): Res => {
  return request.post("/system/gpu/cluster/component/update", data);
};

// | 组件管理：状态变更
export const gpuComponentStatusApi = async (id: string | number, status: number): Res => {
  return request.get(`/system/gpu/cluster/component/status?id=${id}&status=${status}`);
};
