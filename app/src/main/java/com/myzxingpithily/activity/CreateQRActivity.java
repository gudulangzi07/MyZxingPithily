package com.myzxingpithily.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.myzxingpithily.R;

import java.util.Hashtable;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

/**
 * @ClassName: CreateQRActivity
 * @author: 张京伟
 * @date: 2016/11/4 18:00
 * @Description:
 * @version: 1.0
 */
public class CreateQRActivity extends Activity {

    /**
     * 正方形二维码宽度
     */
    private static final int CODE_WIDTH = 440;
    /**
     * LOGO宽度值,最大不能大于二维码20%宽度值,大于可能会导致二维码信息失效
     */
    private static final int LOGO_WIDTH_MAX = CODE_WIDTH / 20;
    /**
     *LOGO宽度值,最小不能小鱼二维码10%宽度值,小于影响Logo与二维码的整体搭配
     */
    private static final int LOGO_WIDTH_MIN = CODE_WIDTH / 40;

    private Bitmap codeBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_qr);

        ImageView imageview = (ImageView)findViewById(R.id.imageview);

        Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        try {
            codeBitmap = createCode("12345678945612", logoBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        imageview.setImageBitmap(codeBitmap);
    }

    /**
     * 生成带LOGO的二维码
     */
    public Bitmap createCode(String content, Bitmap logoBitmap)
            throws WriterException {
        int logoWidth = logoBitmap.getWidth();
        int logoHeight = logoBitmap.getHeight();
        int logoHaleWidth = logoWidth >= CODE_WIDTH ? LOGO_WIDTH_MIN : LOGO_WIDTH_MAX;
        int logoHaleHeight = logoHeight >= CODE_WIDTH ? LOGO_WIDTH_MIN : LOGO_WIDTH_MAX;
        // 将logo图片按martix设置的信息缩放
        Matrix m = new Matrix();
        float sx = (float) 2 * logoHaleWidth / logoWidth;
        float sy = (float) 2 * logoHaleHeight / logoHeight;
        m.setScale(sx, sy);// 设置缩放信息
        Bitmap newLogoBitmap = Bitmap.createBitmap(logoBitmap, 0, 0, logoWidth,
                logoHeight, m, false);
        int newLogoWidth = newLogoBitmap.getWidth();
        int newLogoHeight = newLogoBitmap.getHeight();
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);//设置容错级别,H为最高
        hints.put(EncodeHintType.MAX_SIZE, LOGO_WIDTH_MAX);// 设置图片的最大值
        hints.put(EncodeHintType.MIN_SIZE, LOGO_WIDTH_MIN);// 设置图片的最小值
        hints.put(EncodeHintType.MARGIN, 2);//设置白色边距值
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.QR_CODE, CODE_WIDTH, CODE_WIDTH, hints);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int halfW = width / 2;
        int halfH = height / 2;
        // 二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
				/*
				 * 取值范围,可以画图理解下
				 * halfW + newLogoWidth / 2 - (halfW - newLogoWidth / 2) = newLogoWidth
				 * halfH + newLogoHeight / 2 - (halfH - newLogoHeight) = newLogoHeight
				 */
                if (x > halfW - newLogoWidth / 2 && x < halfW + newLogoWidth / 2
                        && y > halfH - newLogoHeight / 2 && y < halfH + newLogoHeight / 2) {// 该位置用于存放图片信息
					/*
					 *  记录图片每个像素信息
					 *  halfW - newLogoWidth / 2 < x < halfW + newLogoWidth / 2
					 *  --> 0 < x - halfW + newLogoWidth / 2 < newLogoWidth
					 *   halfH - newLogoHeight / 2  < y < halfH + newLogoHeight / 2
					 *   -->0 < y - halfH + newLogoHeight / 2 < newLogoHeight
					 *   刚好取值newLogoBitmap。getPixel(0-newLogoWidth,0-newLogoHeight);
					 */
                    pixels[y * width + x] = newLogoBitmap.getPixel(
                            x - halfW + newLogoWidth / 2, y - halfH + newLogoHeight / 2);
                } else {
                    pixels[y * width + x] = matrix.get(x, y) ? BLACK: WHITE;// 设置信息
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
