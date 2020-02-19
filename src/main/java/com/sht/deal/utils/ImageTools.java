package com.sht.deal.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class ImageTools
{
	public static int getImgWidth(File file) {
		InputStream is = null;
		BufferedImage src = null;
		int ret = -1;
		try {
			is = new FileInputStream(file);
			src = ImageIO.read(is);
			ret = src.getWidth(null);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static int getImgHeight(File file) {
		InputStream is = null;
		BufferedImage src = null;
		int ret = -1;
		try {
			is = new FileInputStream(file);
			src = ImageIO.read(is);
			ret = src.getHeight(null);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
}
