package com.ustack.op.util;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: VerifyCodeUtil
 * @Date: 2024-04-11 12:32
 */
@Slf4j
public class VerifyCodeUtil {
    private final int width = 220;
    private final int height = 80;
    private BufferedImage codeImg;
    private Integer textLen = 4;
    private final StringBuilder sb = new StringBuilder();
    private final Random random = new Random();
    private final Color bgColor = new Color(255, 255, 255);
    private final String[] fontsName = {"宋体", "华文楷体", "黑体", "华文新魏", "华文隶书", "微软雅黑", "楷体_GB2312"};

    private final String codes = "23456789abcdefghjkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
    /**
     * 生成code（第一步调用）
     */
    public void generateCode(){
        codeImg = addCharAndLine();
    }

    public BufferedImage getCodeImg(){
        return this.codeImg;
    }

    public VerifyCodeUtil() {
    }

    public VerifyCodeUtil(Integer textLen) {
        this.textLen = textLen;
    }

    /**
     * 获取验证码的值
     *
     * @return
     */
    public String getText() {
        return sb.toString();
    }


    /**
     * 直接将验证码返回到浏览器
     *
     * @param response
     */
    public void writeCodeToResponse(HttpServletResponse response) {
        try {
            // 设置输出流
            response.setContentType("image/jpeg");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expire", 0);
            // 写入输出流中
            ImageIO.write(codeImg, "JPEG", response.getOutputStream());
        } catch (FileNotFoundException e) {
            log.error("文件未发现异常", e);
        } catch (IOException e) {
            log.error("写验证码异常", e);
        }
    }

    public String writeImageToBase64() throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(this.codeImg, "jpg", outputStream);

            Base64.Encoder encoder = Base64.getEncoder();
            String str = "data:image/jpeg;base64,";
            // 图片 Base64
            String base64Img = str + encoder.encodeToString(outputStream.toByteArray());
            return base64Img;
        }
    }
    /**
     * 生产验证码图片
     *
     * @return
     */
    public void createVerifyCode(String imageName) {
        String path = imageName + ".jpg";
        try {
            ImageIO.write(codeImg, "JPEG", new FileOutputStream(path));
        } catch (FileNotFoundException e) {
            log.error("文件未发现异常", e);
        } catch (IOException e) {
            log.error("写验证码异常", e);
        }
    }
    /**
     * 获取字体
     *
     * @return
     */
    private String getFont() {
        int index = random.nextInt(fontsName.length);
        return fontsName[index];
    }
    /**
     * 获取随机字符
     *
     * @return
     */
    private String getChar() {

        int index = random.nextInt(codes.length());
        return codes.charAt(index) + "";
    }
    /**
     * 获取字体颜色
     *
     * @return
     */
    private Color getColor() {
        int red = random.nextInt(150);
        int green = random.nextInt(150);
        int blue = random.nextInt(150);
        return new Color(red, green, blue);
    }
    /**
     * 生成图片缓存
     *
     * @return
     */
    private BufferedImage getBufferedImage() {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D pen = (Graphics2D) bi.getGraphics();
        pen.setColor(this.bgColor);
        pen.fillRect(0, 0, width, height);
        return bi;
    }
    /**
     * 给验证码图片画字符和干扰线(包含有字符和线)
     *
     * @return
     */
    private BufferedImage addCharAndLine() {
        BufferedImage bi = getBufferedImage();
        Graphics2D pen = (Graphics2D) bi.getGraphics();
        // 把验证码画到图片上
        for (int i = 0; i < this.textLen; i++) {
            String font = getFont();
            // 生成随机的样式, 0(无样式), 1(粗体), 2(斜体), 3(粗体+斜体)
            int style = random.nextInt(4);
            pen.setColor(getColor());
            int fontSize = 70;
            pen.setFont(new Font(font, style, fontSize));
            String s = getChar();
            sb.append(s);
            pen.drawString(s, 10 + i * 50, 65);
        }
        // 画3条干扰线
        int lineNumber = 3;
        pen.setColor(Color.BLUE);
        pen.setStroke(new BasicStroke(1.5F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
        for (int i = 0; i < lineNumber; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            pen.drawLine(x1, y1, x2, y2);
        }
        return bi;
    }


}
