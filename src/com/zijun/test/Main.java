package com.zijun.test;

import java.awt.event.HierarchyListener;
import java.io.File;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

class imageshow {
	private String nameString;
	private static final String FILE_SUFFIX_STRING = ".jpg";
	private Mat iMat;
	private Mat tplMat;
	private Mat[] tpl_Pyramid;
	private static int num_Images=0;;
	private static final int LAYER = 5;

	private Mat[] result_pyramid;

	// private String filehead;
	public imageshow(String put) {

		nameString = put;
		System.out.println(put);
		nameString = put;
	}

	public imageshow() {
		// TODO Auto-generated constructor stub
		// do nothing
	}

	public boolean tplimgLoad(String tplName) {
		
		String fullname = tplName;
		System.out.println(fullname);
		File checkFileExistence = new File(fullname);
		if (!checkFileExistence.exists()) {
			System.out.println("template file dose not exist!\n");
			return false;
		}
      
		tplMat = Highgui.imread(fullname);
		Imgproc.cvtColor(tplMat, tplMat, Imgproc.COLOR_RGBA2GRAY);
		Highgui.imwrite("GrayScale_Template.png", tplMat);
		Imgproc.threshold(tplMat, tplMat, 20, 255, Imgproc.THRESH_BINARY
				| Imgproc.THRESH_OTSU);
		// Save binary template
		Highgui.imwrite("Binary_Template.png", tplMat);
		// Run the image Pyramid
		tpl_Pyramid = new Mat[LAYER];
		tpl_Pyramid[0] = new Mat();
		tpl_Pyramid[0] = tplMat;
        
		
		for(int i=1;i<LAYER;i++){
			    tpl_Pyramid[i]=new Mat();
			    //Imgproc.resize(src, dst, dsize)
			    //or: USE pymid
			    Imgproc.pyrDown(tpl_Pyramid[i-1], tpl_Pyramid[i]);
			    
			    
		}
		
		//Store image to see results:
		
		for(int i=0;i<LAYER;i++){
			String layeredTemplate="Layered_Template_"+String.format("%02d", i+1)+".png";
			Highgui.imwrite(layeredTemplate,tpl_Pyramid[i]);
			
		}
		
		
		
		return true;

	}

	public boolean imageLoad() {

		String numString = String.format("%03d", num_Images+1);
		String fullname = nameString + "_" + numString + FILE_SUFFIX_STRING;
		System.out.println(fullname+"  processing...");
		File checkFileExistence = new File(fullname);
		if (checkFileExistence.exists()) {
			num_Images=num_Images+1;
			iMat = Highgui.imread(fullname);
			Imgproc.cvtColor(iMat, iMat, Imgproc.COLOR_RGBA2GRAY);
			// x=x+1;
			System.out.println(" Reading image"+num_Images+"Success!");
			return true;
		} else {
			System.out.println("fail");
			return false;
		}

	}

	public void tempalte_matching() {

		result_pyramid = new Mat[LAYER];

		for (int i = 0; i < LAYER; i++) {
			result_pyramid[i] = new Mat();
			Imgproc.matchTemplate(iMat, tpl_Pyramid[i], result_pyramid[i],
					Imgproc.TM_CCORR_NORMED);

		}
	}

	public boolean tplSave() {
		return true;
	}

	public boolean result_mat_Save() {
		
		for (int i = 0; i < LAYER; i++) {
			String filename = "NCCresult_" + String.format("%03d", num_Images) + "_"
					+ String.format("%02d", i) + ".png";
			if (Highgui.imwrite(filename, result_pyramid[i])) {

			} else {
				return false;
			}
		}
		return true;
	}

}

public class Main {

	public static void main(String args[]) {
		System.loadLibrary("opencv_java245");

		
		String template_file="res/capture.png";
		String file_headString = "res/IMG";

		imageshow is = new imageshow(file_headString);  // Here defines the header_00x file format
		
		is.tplimgLoad(template_file);
		int count_of_images = 0;

		while (is.imageLoad()) {
			count_of_images=count_of_images+1;
			//Mat mat_result = new Mat();

			is.tempalte_matching();
			boolean tag = is.result_mat_Save();
			System.out.println(tag);
			
			System.out.println("" + count_of_images);
		}
		if (count_of_images == 0) {
			System.out.println("errors included! \n");

		}
		System.out.println("Successful!");

	}

}
