package com.lingyang.cloud.config;

import com.google.zxing.WriterException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 添加水印util
 *
 * @author dh
 */
@Slf4j
public class WatermarkUtil {

    private WatermarkUtil() {
    }

    /**
     * 读取网络图片
     *
     * @param path 网络图片地址
     */
    public static Image readNetworkPicture(String path) {
        if (null == path) {
            throw new RuntimeException("网络图片路径不能为空");
        }
        try {
            // 创建一个URL对象,获取网络图片的地址信息
            URL url = new URL(path);
            // 将URL对象输入流转化为图片对象 (url.openStream()方法，获得一个输入流)
            BufferedImage bugImg = ImageIO.read(url.openStream());
            if (null == bugImg) {
                throw new RuntimeException("网络图片地址不正确");
            }
            return bugImg;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 水印处理
     *
     * @param image     图片对象
     * @param type      水印类型（1-文字水印 2-图片水印）
     * @param watermark 水印内容（文字水印内容/图片水印的存放路径）
     */
    public static BufferedImage manageWatermark(Image image, Integer type, String watermark) {
        int imgWidth = image.getWidth(null);
        int imgHeight = image.getHeight(null);
        BufferedImage bufImg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
        // 加水印：
        // 创建画笔
        Graphics2D graphics = bufImg.createGraphics();
        // 绘制原始图片
        graphics.drawImage(image, 0, 0, imgWidth, imgHeight, null);

        // 校验水印的类型
        if (type == 1) {
            if (StringUtils.isEmpty(watermark)) {
                throw new RuntimeException("文字水印内容不能为空");
            }
            // 添加文字水印：
            // 根据图片的背景设置水印颜色
            graphics.setColor(new Color(255, 255, 255, 128));
            // 设置字体  画笔字体样式为微软雅黑，加粗，文字大小为45pt
            graphics.setFont(new Font("微软雅黑", Font.BOLD, 45));
            // 设置水印的坐标(为原图片中间位置)
            int x = (imgWidth - getWatermarkLength(watermark, graphics)) / 2;
            int y = imgHeight / 2;
            // 画出水印 第一个参数是水印内容，第二个参数是x轴坐标，第三个参数是y轴坐标
            graphics.drawString(watermark, x, y);
            graphics.dispose();
        } else {
            // 添加图片水印：
            if (StringUtils.isEmpty(watermark)) {
                throw new RuntimeException("图片水印存放路径不能为空");
            }
            Image srcWatermark = readNetworkPicture(watermark);
            int watermarkWidth = srcWatermark.getWidth(null);
            int watermarkHeight = srcWatermark.getHeight(null);
            // 设置 alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.9f));
            // 绘制水印图片  坐标为中间位置
            graphics.drawImage(srcWatermark, (imgWidth - watermarkWidth) / 2,
                    (imgHeight - watermarkHeight) / 2, watermarkWidth, watermarkHeight, null);
            graphics.dispose();
        }
        // 定义存储的地址
        // 输出图片
        return bufImg;
    }

    /**
     * 添加水印
     *
     * @param watermarkType 水印类型（1-文字水印 2-图片水印）
     * @param path          图片路径
     * @param watermark     水印内容（文字水印内容/图片水印的存放路径）
     */
    public static BufferedImage addWatermark(Integer watermarkType, String path, String watermark) {
        if (null == watermarkType) {
            throw new RuntimeException("水印类型不能为空");
        }
        Image image = readNetworkPicture(path);
        if (watermarkType == 1) {
            // 添加文字水印
            return manageWatermark(image, 1, watermark);
        } else {
            // 添加图片水印
            return manageWatermark(image, 2, watermark);
        }
    }

    /**
     * 获取水印文字的长度
     *
     * @param watermarkContent 文字水印内容
     * @param graphics         图像类
     */
    private static int getWatermarkLength(String watermarkContent, Graphics2D graphics) {
        return graphics.getFontMetrics(graphics.getFont()).charsWidth(watermarkContent.toCharArray(), 0, watermarkContent.length());
    }


    /**
     * 获取海报
     */
    public static BufferedImage getLinkCode(String wxUrl) {
        // 获取背景图
        Image bgImage = readNetworkPicture("https://ai-cloud-system.tos-cn-beijing.volces.com/2025/08/15/wegysauidoabfvausydad.png");
        int imgWidth = 630;
        int imgHeight = 922;
        BufferedImage bufIma = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_BGR);

        //这里是关键部分
        Graphics2D g = bufIma.createGraphics();
        bufIma = g.getDeviceConfiguration().createCompatibleImage(imgWidth, imgHeight, Transparency.TRANSLUCENT);
        g = bufIma.createGraphics();

        g.drawImage(bgImage, 0, 0, null);

        // BufferedImage bufImg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_BGR);
        // 创建画笔
        Graphics2D graphics = bufIma.createGraphics();
        graphics.setBackground(new Color(0, 0, 0, 0));
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // 绘制原始图片
        graphics.drawImage(bgImage, 0, 0, imgWidth, imgHeight, null);

        // 二维码
        Image qr = readNetworkPicture(wxUrl);
        graphics.drawImage(qr, 150, 460, 320, 320, null);

        // 保存原来的Composite对象
        Composite originalComposite = graphics.getComposite();
        // 恢复原来的Composite对象
        graphics.setComposite(originalComposite);
        graphics.dispose();
        // 输出图片
        return bufIma;
    }



    // 绘制右对齐文字
    public static void drawRightAlignedText(Graphics2D g2, String text, int x, int y) {
        // 获取文字的矩形边界
        FontRenderContext frc = g2.getFontRenderContext();
        Rectangle2D bounds = g2.getFont().getStringBounds(text, frc);

        // 计算右对齐的起始位置
        int textWidth = (int) bounds.getWidth();
        int textHeight = (int) bounds.getHeight();
        int startX = x - textWidth;
        int startY = y + textHeight / 2; // 垂直居中

        // 绘制文字
        g2.drawString(text, startX, startY);
    }

    /**
     * @param text      原字符串
     * @param maxLength 显示最大长度
     * @param flag      是否在末尾补充‘...’
     * @return substring
     */
    private static String textLengthHandle(String text, int maxLength, boolean flag) {
        String result = text;
        if (text.length() > maxLength) {
            if (flag) {
                result = text.substring(0, maxLength - 1);
                result += "...";
            } else {
                result = text.substring(0, maxLength);
            }
        }
        return result;
    }

    public static String getEmojiFont(String text) {
        String font = "微软雅黑";
        Pattern pattern = Pattern.compile("\\ud83c[\\udf00-\\udfff]|\\ud83d[\\udc00-\\ude4f]|\\ud83d[\\ude80-\\udeff]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            font = "Apple Color Emoji";
        }
        return font;
    }

    private static int postition(int width, String text, int fontSize) {
        int num = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c < 255) {
                num += 0.5;
            } else {
                num += 1;
            }
        }
        return (int) Math.floor(width / 2 - fontSize * num / 2);
    }

    public static Image saveFile(String url) throws IOException {
        Image bgImage = null;
//        String path = "C:\\Users\\dou2\\Desktop\\好料背景图\\" + url.substring(url.lastIndexOf("/") + 1);
        String path = "/ffyd/static/" + url.substring(url.lastIndexOf("/") + 1);
        File e = new File(path);
        if (e.exists()) {
            bgImage = ImageIO.read(e);
        } else {
            URL urls = new URL(url);
            bgImage = ImageIO.read(urls.openStream());
            if (null == bgImage) {
                throw new RuntimeException("网络图片地址不正确");
            }
            // 获取图片输入流
            InputStream in = urls.openStream();
            // 将文件转换成字节数组
            byte[] bytes = IOUtils.toByteArray(in);
            // 导出路径和文件格式
            FileUtils.writeByteArrayToFile(new File(path), bytes);
        }
        return bgImage;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, WriterException, URISyntaxException {

    }

}