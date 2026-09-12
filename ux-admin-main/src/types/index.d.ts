interface IDescriptionsItem {
  label: string;
  value: string;
  hidden?: boolean;
  slot?: boolean;
  align?: "left" | "right" | "center";
}

type btnType =
  | ""
  | "default"
  | "text"
  | "success"
  | "warning"
  | "info"
  | "primary"
  | "danger";

interface IOperationBtnItem {
  label: string;
  type?: btnType;
  show?: (row?: any) => boolean;
  click: (row: any) => void;
  auth?: string;
  disabled?: (row?: any) => boolean;
}

type TKeyValue<T = any> = { [key: string]: T };

type TPromiseFn<T = any, K = any> = (
  params: T,
  noLoading: boolean = false
) => Res<K> | any;
