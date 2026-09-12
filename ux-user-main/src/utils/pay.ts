// | 支付宝支付
export const aliPay = (orderNo: string) => {
  window.open(
    `${
      import.meta.env.VITE_APP_BASE_API
    }/pc/order/getAlipayForm?orderNo=${orderNo}`,
    "_blank"
  );
  //
  // const divForm = document.createElement("div");
  // divForm.innerHTML = res;
  // document.body.appendChild(divForm);
  // const form = Array.from(document.forms).at(-1);
  // // const form = document.querySelector('[name="punchout_form"]');
  // if (form instanceof HTMLFormElement) {
  //   form.setAttribute("target", "_blank");
  //   form.submit();
  // } else {
  //   ElMessage.error("支付宝支付调起失败");
  // }
  // document.body.removeChild(divForm);
};
