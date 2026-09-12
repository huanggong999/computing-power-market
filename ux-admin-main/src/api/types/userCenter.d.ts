interface ISetUpPDParams {
  customerId: string;
  list: any[];
  customerNetworkList: any[];
}

// ECS 服务器  CLOUD_STORAGE 云存储  CR 云镜像  CLOUD_OBJECT_STORAGE 对象存储  CLOUD_NETWORK 网络  CR 镜像仓库  VKE 容器  AGIC AGI-C
type TSourceType =
  | "ECS"
  | "CLOUD_STORAGE"
  | "CR"
  | "CLOUD_OBJECT_STORAGE"
  | "CLOUD_NETWORK"
  | "CR"
  | "VKE"
  | "AGIC";
