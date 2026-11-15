interface DrawRoundImageOptions {
	lt: number;
	rt: number;
	rb: number;
	lb: number;
}

interface DrawRoundImageParams {
	ctx: CanvasContext;
	imgPath: CanvasImageSource;
	x: number;
	y: number;
	w: number;
	h: number;
	radii?: DrawRoundImageOptions;
}
interface TextStyle {
	font?: string;
	fillStyle?: string;
	textAlign?: CanvasTextAlign;
	textBaseline?: CanvasTextBaseline;
	// 可以根据需要添加更多样式属性
}

interface DrawEllipsisTextParams {
	ctx: CanvasContext;
	text: string;
	x: number;
	y: number;
	maxWidth: number;
	style?: TextStyle;
}

// 定义点的接口
interface DrawLinePoint {
	x: number;
	y: number;
}

interface DrawLineParams {
	ctx: CanvasContext;
	startPoint: DrawLinePoint;
	endPoint: DrawLinePoint;
	color?: string;
}

// 定义样式接口
interface DrawBorderStyle {
	color?: string;
	lineWidth?: number;
}

// 定义矩形位置和大小的接口
interface DrawRectangle {
	x: number;
	y: number;
	width: number;
	height: number;
}
interface DrawRoundedBorderParams {
	ctx: CanvasContext;
	rect: DrawRectangle;
	radius: number;
	style?: DrawBorderStyle;
}

// 定义文本样式接口
interface DrawTextStyle {
	font?: string;
	fillStyle?: string;
	lineHeight: number;
	textAlign?: CanvasTextAlign;
	textBaseline?: CanvasTextBaseline;
}
interface DrawMultilineEllipsisTextOptions {
	ctx: CanvasContext;
	text: string;
	x: number;
	y: number;
	maxWidth: number;
	maxLines: number;
	style: DrawTextStyle;
}
