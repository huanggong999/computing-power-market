interface IUseInfoData {
  userId: number;
  username: string;
  superAdmin: boolean;
  roleList: IRole[];
  menuIdList: number[] | null;
  permissions: Array<{ permission: string }> | null;
  nickName: string;
  phone: string;
  sex: "MAN" | "WOMAN" | "OTHER";
  avatar: string;
  remark: string;
}
interface IRole {
  id: number;
  roleName: string;
  dataScope: "ALL" | "PART" | "SELF";
  admin: boolean;
}
interface IUserStore extends IUseInfoData {
  token: string;
}

// getAccountListApi
interface IAccountListData {
  account: string;
  createTime: string;
  createUserAccount: string;
  disable: boolean;
  id: string;
  level: string;
  memo: string;
  name: string;
  organizationId: string;
  organizationName: string;
  phone: string;
  portrait: string;
  roleId: string;
  roleName: string;
  sex: number;
}
