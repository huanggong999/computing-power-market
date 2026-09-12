/** * @description: 登录 */

import request from "@/utils/request";

// |  /code { type: string } { 验证码 }
export const getCodeApi = (): Res<ICodeData> => request.get("/code?type=math");

// | /login { 登录 }
export const LoginApi = (data: ILoginParams): Res<ILoginData> =>
  request.post("/login", data, { headers: { source: "SYSTEM_LOGIN" } });
