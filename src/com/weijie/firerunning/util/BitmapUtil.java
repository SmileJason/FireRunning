package com.weijie.firerunning.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class BitmapUtil {
	/**
	 * 旋转位图
	 * 
	 * @param b
	 * @param degrees
	 * @return
	 */
	public static Bitmap rotate(Bitmap b, int degrees) {
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.postScale(1, -1);
			m.setRotate(degrees, (float) b.getWidth() / 2,
					(float) b.getHeight() / 2);
			try {
				Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
						b.getHeight(), m, true);
				if (b != b2) {
					b = b2;
				}
			} catch (OutOfMemoryError ex) {
				// eoeAndroid建议大家如何出现了内存不足异常，最好return 原始的bitmap对象。.
			}
		}
		return b;
	}

	/**
	 * 生成二维码图片
	 * 
	 * @param text
	 * @param size
	 *            宽高
	 * @return
	 */
	public static Bitmap createQRCodeBitmap(String text, int size) {
		try {
			// 需要引入core包
			QRCodeWriter writer = new QRCodeWriter();

			if (size <= 0 || text == null || "".equals(text)
					|| text.length() < 1) {
				return null;
			}

			// 把输入的文本转为二维码
			BitMatrix martix = writer.encode(text, BarcodeFormat.QR_CODE, size,
					size);

			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix bitMatrix = new QRCodeWriter().encode(text,
					BarcodeFormat.QR_CODE, size, size, hints);
			int[] pixels = new int[size * size];
			for (int y = 0; y < size; y++) {
				for (int x = 0; x < size; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * size + x] = 0xff000000;
					} else {
						pixels[y * size + x] = 0xffffffff;
					}

				}
			}

			Bitmap bitmap = Bitmap.createBitmap(size, size,
					Bitmap.Config.ARGB_8888);

			bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
			return bitmap;

		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 保存位图
	 * 
	 * @param b
	 * @param path
	 * @throws IOException
	 */
	public static void saveBitmap(Bitmap b, String path) throws IOException {
		FileOutputStream fOut = null;
		try {
			if (FileUtil.exists(path)) {
				FileUtil.delete(path);
			}
			fOut = new FileOutputStream(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		b.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 等比例缩放(以宽与高中最小的值按比例缩放)
	 * 
	 * @param bmp
	 *            图片源
	 * @param toWidth
	 *            宽
	 * @param toHeight
	 *            高
	 * @return
	 */
	public static Bitmap parseImage(Bitmap bmp, int toWidth, int toHeight) {

		int newW;
		int newH;

		if (bmp.getWidth() * 1.0 / bmp.getHeight() >= toWidth * 1.0 / toHeight) {
			if (bmp.getWidth() > toWidth) {
				newW = toWidth;
				newH = (bmp.getHeight() * toWidth) / bmp.getWidth();
			} else {
				newW = bmp.getWidth();
				newH = bmp.getHeight();
			}
		} else {
			if (bmp.getHeight() > toHeight) {
				newH = toHeight;
				newW = (bmp.getWidth() * toHeight) / bmp.getHeight();
			} else {
				newW = bmp.getWidth();
				newH = bmp.getHeight();
			}
		}

		Bitmap newBitmap = Bitmap.createBitmap(newW, newH, Config.RGB_565);
		Canvas canvas = new Canvas(newBitmap);

		canvas.drawBitmap(bmp, new Rect(0, 0, bmp.getWidth(), bmp.getHeight()),
				new Rect(0, 0, newBitmap.getWidth(), newBitmap.getHeight()),
				new Paint());

		return newBitmap;
	}

	/**
	 * 将彩色图转换为灰度图
	 * 
	 * @param img
	 *            位图
	 * @return 返回转换好的位图
	 */
	public Bitmap convertGreyImg(Bitmap img) {
		int width = img.getWidth(); // 获取位图的宽
		int height = img.getHeight(); // 获取位图的高
		int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组
		img.getPixels(pixels, 0, width, 0, 0, width, height);
		int alpha = 0xFF << 24;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int grey = pixels[width * i + j];
				int red = ((grey & 0x00FF0000) >> 16);
				int green = ((grey & 0x0000FF00) >> 8);
				int blue = (grey & 0x000000FF);
				grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
				grey = alpha | (grey << 16) | (grey << 8) | grey;
				pixels[width * i + j] = grey;
			}
		}
		Bitmap result = Bitmap.createBitmap(width, height, Config.RGB_565);
		result.setPixels(pixels, 0, width, 0, 0, width, height);
		return result;
	}

	public static Bitmap getBitmap(String path, int width, int height) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		options.inSampleSize = computeSampleSize(options, -1, width * height);
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Config.RGB_565;
		try {
			Bitmap bmp = BitmapFactory.decodeFile(path, options);
			return bmp;
		} catch (OutOfMemoryError err) {
			err.printStackTrace();
		}

		return null;
	}

	public static Bitmap getBitmap(String path, int samplesize) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = samplesize;
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Config.RGB_565;
		try {
			Bitmap bmp = BitmapFactory.decodeFile(path, options);
			return bmp;
		} catch (OutOfMemoryError err) {
			err.printStackTrace();
		}

		return null;
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

}