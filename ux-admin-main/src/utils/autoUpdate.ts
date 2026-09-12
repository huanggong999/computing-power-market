import { ElMessageBox } from "element-plus";
const lastScript = ref<string[]>([]);
const extractNewScripts = async (html: string): Promise<string[]> => {
  const scriptReg = /<script.*src=["'](?<src>[^"']+)/gm;
  let result: string[] = [];
  let match;
  while ((match = scriptReg.exec(html))) {
    result.push(match.groups?.src ?? "");
  }
  return result;
};

const needUpdate = async (): Promise<boolean> => {
  const response = await fetch("/").then((resp) => resp.text());
  const newScripts = await extractNewScripts(response);

  // 如果是首次加载，直接记录脚本并返回 false
  if (!lastScript.value.length) {
    lastScript.value = newScripts;
    return false;
  }
  // 如果长度不同，立即更新并返回 true
  if (newScripts.length !== lastScript.value.length) {
    lastScript.value = newScripts;
    return true;
  }
  // 检查每个脚本是否有变化
  const hasChanges = newScripts.some(
    (script, index) => script !== lastScript.value[index]
  );
  if (hasChanges) {
    lastScript.value = newScripts;
  }
  return hasChanges;
};

// 20 秒检查一次
const DURATION = 5 * 1000;

// 自动刷新
export const autoRefresh = () => {
  setTimeout(async () => {
    const need = await needUpdate();
    if (!need) return autoRefresh(); // 继续检查
    ElMessageBox.confirm("页面有更新，点击确定刷新页面！", "温馨提示", {
      type: "warning",
    }).then(() => window.location.reload());
  }, DURATION);
};
