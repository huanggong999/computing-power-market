import { defineStore } from "pinia";

export const useKeepAlive = defineStore("keepAlive", {
  state: (): IKeepAliveState => ({
    keepAliveName: [],
  }),
  actions: {
    // Add KeepAliveName
    async addKeepAliveName(name: string) {
      !this.keepAliveName.includes(name) && this.keepAliveName.push(name);
    },
    // Remove KeepAliveName
    async removeKeepAliveName(name: string) {
      this.keepAliveName = this.keepAliveName.filter((item) => item !== name);
    },
    // Set KeepAliveName
    async setKeepAliveName(keepAliveName: string[] = []) {
      this.keepAliveName = keepAliveName;
    },
  },
});

interface IKeepAliveState {
  keepAliveName: string[];
}
