package com.bach.weighscale;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import android.app.Activity;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bach.printer.PrintUtils;
import com.printer.bluetooth.android.BluetoothPrinter;

public class BienBan extends Activity {
	private ArrayList<String> DuLieu;
	private String tong;
	private String LanDo;
	String Text1;
	Button in_button, ketnoi_button, toi_button, lui_button;
	EditText nguoidung, diadiem, bienso, nguoichung;
	public BluetoothPrinter mPrinter;
	String mConnectedDeviceName;
	TextView TextBox1;
	DataBaseHelper myDbHelper;
	int SoBienBan;
	DuLieu BienBanMoi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bienban);
		// Button Declare
		in_button = (Button) findViewById(R.id.in_button);
		in_button.setEnabled(false);

		lui_button = (Button) findViewById(R.id.lui_button);
		toi_button = (Button) findViewById(R.id.toi_button);
		// Edit text
		nguoidung = (EditText) findViewById(R.id.nguoidung);
		diadiem = (EditText) findViewById(R.id.diadiem);
		bienso = (EditText) findViewById(R.id.bienso);
		nguoichung = (EditText) findViewById(R.id.nguoichung);
		// Set result CANCELED in case the user backs out
		setResult(Activity.RESULT_CANCELED);
		// time

		SimpleDateFormat sdf = new SimpleDateFormat(" HH:mm   dd/MM/yyyy");
		String currentDateandTime = sdf.format(new Date());
		Log.d("BienBan", "Current Date" + currentDateandTime);
		Bundle KetQua = this.getIntent().getExtras();
		try {
			DuLieu = KetQua.getStringArrayList("LANDO");
			tong = KetQua.getString("TONG");
		} catch (Exception e1) {
			Log.e("BienBan", "Khong co Bundle");
			DuLieu = new ArrayList<String>(Arrays.asList("Lần Cân 1: ",
					"Lần Cân 2: "));
			tong = "234214";

		}
		LanDo = "";
		for (int i = 0; i < DuLieu.size(); i++)
			LanDo = LanDo + DuLieu.get(i) + "\n";
		LanDo = ConvertToUnsign1(LanDo);
		if (tong != null)
			tong = ConvertToUnsign1(tong);
		Log.d("Bienban", LanDo);
		Log.d("Bienban", "Tong Nhan duoc " + tong);
		// soft input not showing automatically
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		// Database
		init_DataBase();
		SoBienBan = myDbHelper.getBienbanCount() + 1; // chuan bi bien ban moi
		TextBox1 = (TextView) findViewById(R.id.textView1);

		Text1 = "MASSLOAD LCD ULTRASLIM WEIGHPAD\n"
				+ "-------------------------------\n" + "So ID: 66931\n"
				+ "Bien ban so: " + Integer.toString(SoBienBan) + "\n"
				+ "Thoi gian:" + currentDateandTime + "\n" + "Khoi luong\n"
				+ LanDo + tong + "\n" + "1. Don vi kiem tra\n"
				+ "   CA HUYEN CAN GIO-TP HCM\n" + "2. Ten nguoi lai xe";
		TextBox1.setText(Text1);
		BienBanMoi = new DuLieu(SoBienBan, currentDateandTime, LanDo, tong,
				" ", " ", " ", " ");

		mPrinter = MainActivity.mPrinter;
		if (mPrinter != null) {
			if (mPrinter.isConnected())
				in_button.setEnabled(true);
		}

		in_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("BACH", "++ ON Can ++");
				// TODO Auto-generated method stub

				in_button();
			}
		});

		capnhatForm(SoBienBan);
		capnhatNutBam(SoBienBan);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("BienBan", " Bienban DESTROY");

		setResult(Activity.RESULT_CANCELED);

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

	}

	public String ConvertToUnsign1(String str) {
		String[] signs = new String[] { "aAeEoOuUiIdDyY", "áàạảãâấầậẩẫăắằặẳẵ",
				"ÁÀẠẢÃÂẤẦẬẨẪĂẮẰẶẲẴ", "éèẹẻẽêếềệểễ", "ÉÈẸẺẼÊẾỀỆỂỄ",
				"óòọỏõôốồộổỗơớờợởỡ", "ÓÒỌỎÕÔỐỒỘỔỖƠỚỜỢỞỠ", "úùụủũưứừựửữ",
				"ÚÙỤỦŨƯỨỪỰỬỮ", "íìịỉĩ", "ÍÌỊỈĨ", "đ", "Đ", "ýỳỵỷỹ", "ÝỲỴỶỸ" };
		for (int i = 1; i < signs.length; i++) {
			for (int j = 0; j < signs[i].length(); j++) {
				str = str.replace(signs[i].charAt(j), signs[0].charAt(i - 1));
			}
		}
		return str;
	}

	private final void in_button() {

		// thêm biên bản mới vào database
		if (SoBienBan == (myDbHelper.getBienbanCount() + 1)) {
			String nguoidungString = nguoidung.getText().toString();
			// bo dau
			if (nguoidungString != null)
				nguoidungString = ConvertToUnsign1(nguoidungString);
			// bo dau
			String diadiemString = diadiem.getText().toString();
			if (diadiemString != null)
				diadiemString = ConvertToUnsign1(diadiemString);
			String biensoString = bienso.getText().toString();
			if (biensoString != null)
				biensoString = ConvertToUnsign1(biensoString);

			String nguoichungString = nguoichung.getText().toString();
			if (nguoichungString != null)
				nguoichungString = ConvertToUnsign1(nguoichungString);
			BienBanMoi.setLaixe(nguoidungString);
			BienBanMoi.setDiadiem(diadiemString);
			BienBanMoi.setBiensoxe(biensoString);
			BienBanMoi.setNglamchung(nguoichungString);
			try {
				inBienBan(BienBanMoi);
			} catch (Exception e1) {
				Log.e("BienBan", "Khong co May In");
			}
			myDbHelper.addBienBan(BienBanMoi);
			SoBienBan = myDbHelper.getBienbanCount();
			// tang ID cho bien ban moi
			BienBanMoi.setId(SoBienBan + 1);
			capnhatForm(SoBienBan);
			capnhatNutBam(SoBienBan);
		} else {
			// in bien ban hien tai
			try {
				inBienBan(myDbHelper.getBienBan(SoBienBan));
			} catch (Exception e1) {
				Log.e("BienBan", "Khong co May In");
			}
		}
		Log.d("BienBan", "Bam Nut In, So Bien Ban Dang Duyet: " + SoBienBan);
	}

	public void Lui(View view) {
		SoBienBan--;
		Log.d("BienBan", "Lui " + SoBienBan);
		capnhatForm(SoBienBan);
		capnhatNutBam(SoBienBan);

	}

	public void Toi(View view) {

		SoBienBan++;
		Log.d("BienBan", "Toi " + SoBienBan);
		capnhatForm(SoBienBan);
		capnhatNutBam(SoBienBan);
	}

	private void init_DataBase() {
		myDbHelper = new DataBaseHelper(this);

		try {

			myDbHelper.createDataBase();

		} catch (IOException ioe) {

			throw new Error("Unable to create database");

		}

		try {

			myDbHelper.openDataBase();

		} catch (SQLException sqle) {

			throw sqle;

		}
	}

	private void capnhatForm(int SoBienBan) {
		DuLieu BienBan;
		if (SoBienBan <= myDbHelper.getBienbanCount())
			BienBan = myDbHelper.getBienBan(SoBienBan);
		else
			BienBan = BienBanMoi;
		String temp;
		temp = "MASSLOAD LCD ULTRASLIM WEIGHPAD\n"
				+ "-------------------------------\n" + "So ID: 66931\n"
				+ "Bien ban: " + Integer.toString(BienBan.getId()) + "\n"
				+ "Thoi gian:" + BienBan.getThoigian() + "\n" + "Khoi luong\n"
				+ BienBan.getCan() + BienBan.getTongkhoiluong() + "\n"
				+ "1. Don vi kiem tra\n" + "   CA HUYEN CAN GIO-TP HCM\n"
				+ "2. Ten nguoi lai xe";

		TextBox1.setText(temp);
		// doc gia tri cu
		nguoidung.setText(BienBan.getLaixe());
		diadiem.setText(BienBan.getDiadiem());
		bienso.setText(BienBan.getBiensoxe());
		nguoichung.setText(BienBan.getNglamchung());

	}

	private void capnhatNutBam(int SoBienBan) {
		if (SoBienBan == myDbHelper.getBienbanCount() + 1) {
			nguoidung.setEnabled(true);
			diadiem.setEnabled(true);
			bienso.setEnabled(true);
			nguoichung.setEnabled(true);
		} else {
			nguoidung.setEnabled(false);
			diadiem.setEnabled(false);
			bienso.setEnabled(false);
			nguoichung.setEnabled(false);
		}

		if ((SoBienBan <= myDbHelper.getBienbanCount() + 1) && (SoBienBan > 1)) {
			lui_button.setEnabled(true);
		} else
			lui_button.setEnabled(false);

		if ((SoBienBan <= myDbHelper.getBienbanCount()) && (SoBienBan >= 1)) {
			toi_button.setEnabled(true);
		} else
			toi_button.setEnabled(false);

	}

	private void inBienBan(DuLieu BienBan) {
		String temp;
		temp = "MASSLOAD LCD ULTRASLIM WEIGHPAD\n"
				+ "-------------------------------\n" + "So ID: 66931\n"
				+ "Bien ban so: " + Integer.toString(BienBan.getId()) + "\n"
				+ "Thoi gian:" + BienBan.getThoigian() + "\n" + "Khoi luong\n"
				+ BienBan.getCan() + BienBan.getTongkhoiluong() + "\n"
				+ "1. Don vi kiem tra\n" + "   CA HUYEN CAN GIO-TP HCM\n"
				+ "2. Ten nguoi lai xe";
		PrintUtils.printText(mPrinter, temp);

		temp = "   " + BienBan.getLaixe() + "\n" + "3. Bien so xe \n" + "   "
				+ BienBan.getBiensoxe() + "\n" + "4. Dia diem kiem tra \n"
				+ "   " + BienBan.getDiadiem() + "\n" + "5. Nguoi lam chung \n"
				+ "   " + BienBan.getNglamchung() + "\n"
				+ "   Chu ky nguoi lam chung" + "\n\n\n\n\n"
				+ "-------------------------------\n"
				+ "    COPYRIGHT @ DATNAMCO\n\n\n\n";
		PrintUtils.printText(mPrinter, temp);

	}
}
