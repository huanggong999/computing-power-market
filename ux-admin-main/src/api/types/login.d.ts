interface ILoginParams {
  username: string;
  password: string;
  code: string;
  uid: string;
}

interface ILoginData {
  token: string;
  userInfo: IUserInfo;
}

interface ICodeData {
  img: string;
  uid: string;
}
