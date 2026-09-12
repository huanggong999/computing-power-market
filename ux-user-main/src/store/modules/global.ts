import { defineStore } from "pinia";

export const useGlobalStore = defineStore("Global", {
  state: (): { language: null | string } => {
    return {
      language: "zh",
    };
  },
  actions: {
    // Set GlobalState
    setGlobalLanguage(type: LanguageType) {
      this.language = type;
    },
  },
  persist: true,
});

export type LanguageType = "zh" | "en" | null;
