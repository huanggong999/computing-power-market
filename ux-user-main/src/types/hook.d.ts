interface IUseTableParams {
  requestApi?: (params: any, signal?: any) => Promise<any> | any
  title?: string
  isPage?: boolean
  requestAuto?: boolean
  initParams?: TKeyValue
}
interface PageData {
  pageSize: number
  pageNo: number
  total: number
}
interface IStateProps {
  tableData: TKeyValue[]
  pageData: PageData
  totalParam: TKeyValue
  searchParam: TKeyValue
}
