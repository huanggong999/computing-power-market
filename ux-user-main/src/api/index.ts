import request from "@/utils/request";

// | /api/user/logout  { 退出登录 }
export const getLogoutAPI = (): Res<any> => request.post("/api/user/logout");
