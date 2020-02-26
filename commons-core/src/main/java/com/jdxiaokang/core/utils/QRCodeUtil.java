package com.jdxiaokang.core.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.jdxiaokang.core.pool.OkHttpClientSingleton;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Optional;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: commons-parent
 * @description:
 * @date 2020/2/25
 */
public class QRCodeUtil {
    private static final String CHARSET = "utf-8";
    // LOGO宽度
    private static final int WIDTH = 60;
    // LOGO高度
    private static final int HEIGHT = 60;


    /**
     * 生成二维码字节流
     * @param content 内容
     * @param imgPath 二维码logo图片  在线地址
     * @param width 宽度
     * @param height 高度
     * @param format 图片格式
     */
    public static byte[] generateQRCode(String content, String imgPath,int width, int height,
                                        String format) throws Exception {
        BufferedImage bufferedImage = createImage(content, imgPath, width, height);
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, format, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }

    private static BufferedImage createImage(String content, String imgPath,int width, int height) throws Exception {
        HashMap<EncodeHintType, Comparable<?>> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.QR_CODE, width, height, hints);

        BufferedImage image = new BufferedImage(bitMatrix.getWidth(), bitMatrix.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000
                        : 0xFFFFFFFF);
            }
        }
        if (StringUtils.isBlank(imgPath)) {
            return image;
        }
        // 插入图片
        QRCodeUtil.insertImage(image, imgPath,width,height,true);
        return image;
    }
    /**
     * 插入LOGO
     *
     * @param source
     *            二维码图片
     * @param imgPath
     *            LOGO图片地址
     * @param width 二维码宽度
     * @param height 二维码高度
     * @param needCompress
     *            是否压缩
     * @throws Exception
     */
    private static void insertImage(BufferedImage source, String imgPath,
                                    int width, int height,
                                    boolean needCompress) throws Exception {
        OkHttpClient okHttpClient = OkHttpClientSingleton.INSTANCE.getClient();
        Call call = okHttpClient.newCall(new Request.Builder().url(imgPath).build());
        Response response = call.execute();
        Image src = ImageIO.read(Optional.ofNullable(response.body()).map(ResponseBody::byteStream).orElseThrow(()->new RuntimeException("获取图片失败")));
        int logWidth = src.getWidth(null);
        int logHeight = src.getHeight(null);
        if (needCompress) { // 压缩LOGO
            if (logWidth > WIDTH) {
                logWidth = WIDTH;
            }
            if (logHeight > HEIGHT) {
                logHeight = HEIGHT;
            }
            Image image = src.getScaledInstance(logWidth, logHeight,
                    Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(logWidth, logHeight,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (width - logWidth) / 2;
        int y = (height - logHeight) / 2;
        graph.drawImage(src, x, y, logWidth, logHeight, null);
        Shape shape = new RoundRectangle2D.Float(x, y, logWidth, logWidth, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }


    public static void main(String[] args) throws Exception {
        String url = "https://static.jdxiaokang.com/jdxiaokang/distribution/2020/02/202002041644052944886.jpg";
        byte[] bytes = generateQRCode(url, url, 300, 300, "jpg");
        FileImageOutputStream imageOutputStream = new FileImageOutputStream(new File("/Users/mac/Documents/zwq/code"));
        imageOutputStream.write(bytes);
        imageOutputStream.close();
        OkHttpClientSingleton.INSTANCE.getClient().dispatcher().executorService().shutdown();

    }





}