/**
 * 绘制自定义圆角图片
 * @param {Object} options
 * @param {CanvasContext} options.ctx - 画布上下文
 * @param {String} options.imgPath - 图片路径（需已加载完成的本地路径）
 * @param {Number} options.x - 绘制起点X
 * @param {Number} options.y - 绘制起点Y
 * @param {Number} options.w - 绘制宽度
 * @param {Number} options.h - 绘制高度
 * @param {Object} options.radii - 圆角配置 { lt: 左上, rt: 右上, rb: 右下, lb: 左下 }
 */
const drawRoundImage = (options: DrawRoundImageParams) => {
	const {
		ctx,
		imgPath,
		x,
		y,
		w,
		h,
		radii = { lt: 0, rt: 0, rb: 0, lb: 0 },
	} = options;
	// 保存当前绘图状态
	ctx.save();
	// 创建自定义圆角路径
	ctx.beginPath();
	ctx.moveTo(x + radii.lt, y);
	// 右上角
	ctx.arcTo(x + w, y, x + w, y + h, radii.rt);
	// 右下角
	ctx.arcTo(x + w, y + h, x, y + h, radii.rb);
	// 左下角
	ctx.arcTo(x, y + h, x, y, radii.lb);
	// 左上角
	ctx.arcTo(x, y, x + w, y, radii.lt);
	ctx.closePath();
	// 创建裁剪区域
	ctx.clip();
	// 绘制图片（保持原始比例居中显示，可根据需要修改绘制模式）
	ctx.drawImage(imgPath, x, y, w, h);
	// 恢复绘图状态
	ctx.restore();
};
/**
 * 绘制单行省略文本（保留上下文状态）
 * @param {Object} options
 * @param {CanvasContext} options.ctx - 画布上下文
 * @param {String} options.text - 原始文本
 * @param {Number} options.x - 文本X坐标
 * @param {Number} options.y - 文本Y坐标
 * @param {Number} options.maxWidth - 最大允许宽度
 * @param {Object} [options.style] - 文本样式配置
 */
const drawEllipsisText = (options: DrawEllipsisTextParams) => {
	const { ctx, text, x, y, maxWidth, style = {} } = options;
	// 保存绘图状态
	ctx.save();

	// 设置文本样式
	ctx.font = style.font || "14px sans-serif";
	ctx.fillStyle = style.fillStyle || "#000";
	ctx.textBaseline = style.textBaseline || "top";
	ctx.textAlign = style.textAlign || "left";

	// 文本测量
	const ellipsis = "...";
	const ellipsisWidth = ctx.measureText(ellipsis).width;

	// 文本未超长直接绘制
	if (ctx.measureText(text).width <= maxWidth) {
		ctx.fillText(text, x, y);
		ctx.restore();
		return;
	}

	// 二分法查找合适文本长度
	let left = 0;
	let right = text.length;
	let resultText = text;

	while (left <= right) {
		const mid = Math.floor((left + right) / 2);
		const currentText = text.slice(0, mid);
		const currentWidth = ctx.measureText(currentText).width;

		if (currentWidth + ellipsisWidth <= maxWidth) {
			resultText = currentText + ellipsis;
			left = mid + 1;
		} else {
			right = mid - 1;
		}
	}

	// 绘制最终文本
	ctx.fillText(resultText, x, y);

	// 恢复原始状态
	ctx.restore();
};
/**
 * 使用给定的 Canvas 上下文在 Canvas 上绘制直线
 * @param {Object} options
 * @param {CanvasContext} options.ctx - 用于绘制的 Canvas 上下文对象
 * @param {Point} options.startPoint - 直线的起始点，由 x 和 y 坐标组成
 * @param {Point} options.endPoint - 直线的结束点，由 x 和 y 坐标组成
 * @param {Color} [options.color='#000000'] - 线条的颜色，默认颜色为黑色
 */
const drawLine = (options: DrawLineParams) => {
	const { ctx, startPoint, endPoint, color = "#000000" } = options;
	// 保存当前的绘图状态
	ctx.save();
	// 设置线条样式
	ctx.strokeStyle = color;
	// 移动到起点
	ctx.moveTo(startPoint.x, startPoint.y);
	// 绘制到终点
	ctx.lineTo(endPoint.x, endPoint.y);
	// 绘制线条
	ctx.stroke();
	// 恢复之前保存的绘图状态
	ctx.restore();
};

/**
 * 在 Canvas 上绘制带有指定弧度边框的矩形，且不影响之前绘制的内容
 * @param {Object} options
 * @param {CanvasContext} options.ctx - 用于绘制的 Canvas 上下文对象
 * @param {DrawRectangle} options.rect - 矩形的位置和大小，包含 x、y 坐标以及宽度和高度
 * @param {number} options.radius - 边框的圆角半径，单位为像素
 * @param {DrawBorderStyle} [options.style={ color: '#000000', lineWidth: 1 }] - 边框的样式，包括颜色和宽度，默认颜色为黑色，宽度为 1 像素
 */
const drawRoundedBorder = (options: DrawRoundedBorderParams) => {
	const {
		ctx,
		rect,
		radius,
		style = { color: "#000000", lineWidth: 1 },
	} = options;
	// 保存当前的绘图状态
	ctx.save();
	const { x, y, width, height } = rect;
	// 设置边框的颜色和宽度
	ctx.strokeStyle = style.color;
	ctx.lineWidth = style.lineWidth;
	// 开始绘制路径
	ctx.beginPath();
	// 移动到左上角
	ctx.moveTo(x + radius, y);
	// 绘制右上角的弧线
	ctx.arcTo(x + width, y, x + width, y + radius, radius);
	// 绘制右下角的弧线
	ctx.arcTo(x + width, y + height, x + width - radius, y + height, radius);
	// 绘制左下角的弧线
	ctx.arcTo(x, y + height, x, y + height - radius, radius);
	// 绘制左上角的弧线
	ctx.arcTo(x, y, x + radius, y, radius);
	// 关闭路径
	ctx.closePath();
	// 绘制边框
	ctx.stroke();
	// 恢复之前保存的绘图状态
	ctx.restore();
};

/**
 * 在 Canvas 上绘制多行省略文本
 * @param {Object} options
 * @param {CanvasContext} options.ctx - 用于绘制的 Canvas 上下文对象
 * @param {string} options.text - 要绘制的文本内容
 * @param {number} options.x - 文本绘制起始点的 x 坐标
 * @param {number} options.y - 文本绘制起始点的 y 坐标
 * @param {number} options.maxWidth - 文本允许的最大宽度
 * @param {number} options.maxLines - 允许显示的最大行数
 * @param {TextStyle} [options.style={ font: '16px sans-serif', fillStyle: '#000000', lineHeight: 20 }] - 文本的样式，包括字体、填充颜色和行高
 */
const drawMultilineEllipsisText = (
	options: DrawMultilineEllipsisTextOptions
) => {
	const { ctx, text, x, y, maxWidth, maxLines, style } = options;
	// 保存当前的绘图状态
	ctx.save();

	// 设置文本样式
	ctx.font = style.font || "14px sans-serif";
	ctx.fillStyle = style.fillStyle || "#000";
	ctx.textBaseline = style.textBaseline || "top";
	ctx.textAlign = style.textAlign || "left";

	const lines: string[] = [];
	let currentLine = "";

	// 逐字处理文本，将其拆分成多行
	for (let i = 0; i < text.length; i++) {
		const char = text[i];
		const testLine = currentLine + char;
		const testWidth = ctx.measureText(testLine).width;

		if (testWidth > maxWidth) {
			lines.push(currentLine);
			currentLine = char;
		} else {
			currentLine = testLine;
		}
	}
	// 添加最后一行
	lines.push(currentLine);

	// 处理行数超过限制的情况
	if (lines.length > maxLines) {
		const lastLine = lines[maxLines - 1];
		let newLastLine = "";
		for (let i = 0; i < lastLine.length; i++) {
			const char = lastLine[i];
			const testLine = newLastLine + char + "...";
			const testWidth = ctx.measureText(testLine).width;
			if (testWidth > maxWidth) {
				break;
			}
			newLastLine += char;
		}
		lines[maxLines - 1] = newLastLine + "...";
		// 截取到指定行数
		lines.length = maxLines;
	}

	// 绘制每行文本
	lines.forEach((line, index) => {
		const currentY = y + index * style.lineHeight;
		ctx.fillText(line, x, currentY);
	});

	// 恢复之前保存的绘图状态
	ctx.restore();
};
export {
	drawRoundImage,
	drawEllipsisText,
	drawLine,
	drawRoundedBorder,
	drawMultilineEllipsisText,
};
