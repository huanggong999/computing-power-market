// | 统一处理 API 返回数据
type Res<T = any> = Promise<IData<T>>;

interface IData<T> {
  code: number;
  data: T;
  msg: string;
  requestId: number;
}

// | 登录校验
type ValidatedFields<T> = keyof T;

interface ILoginData {
  token: string;
  user: IUserInfo;
}

//
interface ITaskDetail {
  anonymous: number; // 0 表示 false
  cateId: number;
  completeDesc: string;
  completeImg: string;
  commissionRation: string;
  conditionList?: {
    conditionId: number;
    conditionKey: string;
    conditionValue: string;
    id: number;
    taskId: number;
  }[];
  contactName: string;
  contactTel: string;
  cover: string[];
  createTime: Date; // 使用 Date 类型
  id: number;
  intro: string;
  modifyTime: Date; // 使用 Date 类型
  name: string;
  orderNo: string;
  price: number;
  publishUid: number;
  publishName: string;
  publishAvatar: string;
  receiveUid: number;
  recommend: number;
  receiveName: string;
  receiveAvatar: string;
  status: TaskStatus; // 使用枚举类型
  taskEnrollList: any[]; // 报名人
  taskKeywordList?: {
    keyword: string;
    taskId: number;
  }[];
}

type TaskStatus =
  | "WAIT_PAY"
  | "WAIT_RECEIVE"
  | "WAIT_CONFIRM"
  | "WAIT_COMPLETE"
  | "WAIT_CHECK"
  | "COMPLETE"
  | "CANCEL";

// 是否已读（0 未读 1 已读）
type THasRead = 0 | 1;
type TRelateType = "task";

interface INotice {
  createTime: string;
  hasRead: THasRead;
  id: string;
  message: string;
  relateId: string;
  relateType: TRelateType;
  title: string;
  uid: 0;
  url: string;
}

// 新增任务

// 定义 TaskKeyword 和 Condition 的子类型
interface TaskKeyword {
  keyword: string;
  taskId?: string;
}

interface Condition {
  conditionId: string;
}

// 定义主类型
interface ITaskParams {
  name: string;
  price: number;
  taskKeywordList: TaskKeyword[];
  conditionList: Condition[];
  intro: string;
  anonymous: TAnonymous;
  cover: string[];
}
type TAnonymous = 0 | 1;
