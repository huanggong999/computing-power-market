import { defineStore } from "pinia";
import api from "@/api";
export const useInvoice = defineStore("invoice", {
	state: () => ({
		INVOICE_DATA: {
			/**
			 * 发票金额
			 */
			amount: "",
			/**
			 * 申请时间
			 */
			applicationTime: "",
			/**
			 * 企业注册地址
			 */
			companyAddress: "",
			/**
			 * 企业统一社会信用代码（纳税人识别号）
			 */
			companyCode: "",
			/**
			 * 企业联系人名称
			 */
			companyContactName: "",
			/**
			 * 企业联系人电话
			 */
			companyContactPhone: "",
			/**
			 * 企业营业执照
			 */
			companyImg: "",
			/**
			 * 企业名称
			 */
			companyName: "",
			/**
			 * 邮箱
			 */
			email: "",
			/**
			 * 发票id
			 */
			id: "",
			/**
			 * 发票编号
			 */
			invoiceNumber: "",
			/**
			 * 发票抬头
			 */
			invoiceTitle: "",
			/**
			 * 发票类型（1增值税普通发票 2增值税专用发票）
			 */
			invoiceType: "",
			/**
			 * 发票介质
			 */
			medium: "",
			/**
			 * 发票凭证
			 */
			proof: "",
			/**
			 * 发票备注
			 */
			remark: "",
			/**
			 * 发票状态（1开票中 2已开票 3开票失败）
			 */
			status: 1,
		},
		INVOICE_TITLE: {
			/**
			 * 主键
			 */
			id: 1,
			account: "",
			/**
			 * 企业注册地址
			 */
			address: "",
			/**
			 * 开票类型（1个人 2企业）
			 */
			billingType: 1,
			/**
			 * 基本开户银行
			 */
			deposit: "",
			/**
			 * 发票抬头
			 */
			invoiceTitle: "",
			/**
			 * 发票类型（1增值税普通发票 2增值税专用发票）
			 */
			invoiceType: 1,
			/**
			 * 纳税人识别号
			 */
			number: "",
			/**
			 * 企业注册电话
			 */
			phoneNumber: "",
		},
		BILLIDS: [],
		BILL_AMOUNT: 0,
		INVOICE_TYPE: 1,
	}),
	actions: {
		SET_INVOICE_DATA(data: any) {
			this.INVOICE_DATA = data;
		},
		GET_INVOICE_DATA() {
			return this.INVOICE_DATA;
		},

		SET_INVOICE_TITLE(data: any) {
			this.INVOICE_TITLE = data;
		},
		GET_INVOICE_TITLE() {
			return this.INVOICE_TITLE;
		},
		SET_BILLIDS(data: any) {
			this.BILLIDS = data;
		},
		GET_BILLIDS() {
			return this.BILLIDS;
		},
		SET_BILL_AMOUNT(data: any) {
			this.BILL_AMOUNT = data;
		},
		GET_BILL_AMOUNT() {
			return this.BILL_AMOUNT;
		},
		SET_INVOICE_TYPE(data: any) {
			this.INVOICE_TYPE = data;
		},
		GET_INVOICE_TYPE() {
			return this.INVOICE_TYPE;
		},
	},
});
