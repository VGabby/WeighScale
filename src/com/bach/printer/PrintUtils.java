package com.bach.printer;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bach.weighscale.R;
import com.printer.bluetooth.android.BluetoothPrinter;


public class PrintUtils {
	
	/*public static void printNote(Resources resources, BluetoothPrinter mPrinter) {
		mPrinter.init();
		StringBuffer sb = new StringBuffer();
		//mPrinter.setPrinter(BluetoothPrinter.COMM_LINE_HEIGHT, 80);

		mPrinter.setPrinter(BluetoothPrinter.COMM_ALIGN, BluetoothPrinter.COMM_ALIGN_CENTER);
		// �ֺź�����������һ��
		mPrinter.setCharacterMultiple(1, 1);
	//	mPrinter.printText(resources.getString(R.string.shop_company_title) + "\n");

		mPrinter.setPrinter(BluetoothPrinter.COMM_ALIGN, BluetoothPrinter.COMM_ALIGN_LEFT);
		// �ֺ�ʹ��Ĭ��
		mPrinter.setCharacterMultiple(0, 0);
		sb.append(resources.getString(R.string.shop_num) + "574001\n");
		sb.append(resources.getString(R.string.shop_receipt_num) + "S00003169\n");
		sb.append(resources.getString(R.string.shop_cashier_num) + "s004_s004\n");
		
		sb.append(resources.getString(R.string.shop_receipt_date) + "2012-06-17\n");
		sb.append(resources.getString(R.string.shop_print_time) + "2012-06-17 13:37:24\n");
		mPrinter.printText(sb.toString()); //��ӡ
		
		printTable1(resources, mPrinter); //��ӡ���
		
		sb = new StringBuffer();
		if (mPrinter.getCurrentPrintType() == PrinterType.TIII || mPrinter.getCurrentPrintType() == PrinterType.T5) {
			sb.append(resources.getString(R.string.shop_goods_number) + "                6.00\n");
			sb.append(resources.getString(R.string.shop_goods_total_price) + "                35.00\n");
			sb.append(resources.getString(R.string.shop_payment) + "                100.00\n");
			sb.append(resources.getString(R.string.shop_change) + "                65.00\n");
		}else{
			sb.append(resources.getString(R.string.shop_goods_number) + "                                6.00\n");
			sb.append(resources.getString(R.string.shop_goods_total_price) + "                                35.00\n");
			sb.append(resources.getString(R.string.shop_payment) + "                                100.00\n");
			sb.append(resources.getString(R.string.shop_change) + "                                65.00\n");
		}
		
		sb.append(resources.getString(R.string.shop_company_name) + "\n");
		sb.append(resources.getString(R.string.shop_company_site) + "www.jiangsu1510.com\n");
		sb.append(resources.getString(R.string.shop_company_address) + "\n");
		sb.append(resources.getString(R.string.shop_company_tel) + "0574-88222999\n");
		sb.append(resources.getString(R.string.shop_Service_Line) + "4008-567-567 \n");
		if (mPrinter.getCurrentPrintType() == PrinterType.TIII || mPrinter.getCurrentPrintType() == PrinterType.T5) {
			sb.append("==============================\n");
		}else{
			sb.append("==============================================\n");
		}
		mPrinter.printText(sb.toString());
		
		mPrinter.setPrinter(BluetoothPrinter.COMM_ALIGN, BluetoothPrinter.COMM_ALIGN_CENTER);
		mPrinter.setCharacterMultiple(0, 1);
		mPrinter.printText(resources.getString(R.string.shop_thanks) + "\n");
		mPrinter.printText(resources.getString(R.string.shop_demo) + "\n\n\n");
	}*/
	
	public static void printImage(Resources resources, BluetoothPrinter mPrinter, boolean is_thermal) {
		Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.goodwork);
		//getCanvasImage������û�����������ͼ��,printImage������ӡͼ��. 
		if(is_thermal)
		{
			mPrinter.printImage(bitmap); 
		}
		else
		{
			mPrinter.printImageDot(bitmap, 1, 0);
		}
		mPrinter.setPrinter(BluetoothPrinter.COMM_PRINT_AND_WAKE_PAPER_BY_LINE, 1); //��1��
	}
	public static void printText(BluetoothPrinter mPrinter, String content) {
		mPrinter.init();
		//�����и�
		//mPrinter.setPrinter(BluetoothPrinter.COMM_LINE_HEIGHT, 80);
		
		//��ӡ�ı�
		mPrinter.printText(content);
		//����
		mPrinter.setPrinter(BluetoothPrinter.COMM_PRINT_AND_NEWLINE);
	}
}
