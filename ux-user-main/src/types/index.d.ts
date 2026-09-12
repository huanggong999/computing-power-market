type TKeyValue<T = any> = { [key: string]: T };

type TPromiseFn<T = any, K = any> = (
  params: T,
  noLoading: boolean = false
) => Res<K> | any;

// MAN  男 WOMAN  女  UNKNOWN  未知
type TSex = "MAN" | "WOMAN" | "UNKNOWN";
