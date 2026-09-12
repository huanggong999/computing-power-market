/** * @description: 火山云 GPU 目录 */

import request from "@/utils/request";

export const gpuCatalogApi = (): Res<any> =>
  request.get("/system/gpu/volcano/catalog");
