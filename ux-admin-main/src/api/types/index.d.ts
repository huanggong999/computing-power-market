// | 统一处理 API 返回数据
type Res<T = any> = Promise<IData<T>>;

interface IData<T> {
  code: number;
  data: T;
  message: string;
  fail: boolean;
  statistics: null | any;
  success: boolean;
}

interface IConfig<T = TKeyValue> {
  configKey: TConfigKey;
  configValue: T;
  id?: number | string;
}

type TConfigKey =
  | "SELL_PRICE_RATIO"
  | "RETURN_PRICE_RATIO"
  | "ECS_SYSTEM_VOLUME"
  | "EIP_ADDRESS"
  | "";
