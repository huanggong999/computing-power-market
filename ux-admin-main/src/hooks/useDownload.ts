export const useDownload = async (
  api: (param: any) => Promise<any> | any,
  tempName: string,
  params: any = {},
  isNotify: boolean = true,
  fileType: string = ".xlsx"
) => {
  if (isNotify)
    ElNotification.info("如果数据庞大会导致下载缓慢哦，请您耐心等待！");

  try {
    const res = await api(params);
    const url = URL.createObjectURL(res);
    const link = document.createElement("a");
    link.href = url;
    link.download = `${tempName}${fileType}`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.log(error);
  }
};
