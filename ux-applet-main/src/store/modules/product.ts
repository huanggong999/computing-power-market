import { defineStore } from "pinia";

export const useProduction = defineStore("networkProduct", {
	state: (): {
		production: any;
	} => ({
		production: {},
	}),
	getters: {},
	actions: {
		setProductionDetail(detail: any) {
			this.production = detail;
		},
	},
});
