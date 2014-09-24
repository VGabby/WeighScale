package com.bach.weighscale;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bach.bluetooth.BluetoothChatService;
import com.bach.bluetooth.DeviceListActivity;
import com.printer.bluetooth.android.BluetoothPrinter;
import com.printer.bluetooth.android.PrinterType;

public class MainActivity extends Activity {
	// Debugging
	private static final String TAG = "BluetoothChat";
	private static final boolean D = true;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	private static final int REQUEST_ENABLE_BT = 3;
	// Name of the connected device
	private String mConnectedDeviceName = null;
	// Array adapter for the conversation thread
	private ArrayAdapter<String> mConversationArrayAdapter;
	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	public static BluetoothPrinter mPrinter = null;
	// Member object for the chat services
	private BluetoothChatService mChatService = null;
	private TextView receiveTextView = null;
	/*
	 * private ViewFlipper mViewFlipper = null; private Animation slide_in_left,
	 * slide_in_right, slide_out_left, slide_out_right;
	 */
	private Button canButton = null;
	private Button xoaButton = null;
	private ListView lancan_ListView;
	private Context mContext;
	private ArrayAdapter<String> list_adapter;
	private ArrayList<String> DuLieu;
	private int sotruc = 0;
	private CheckBox nhan_check = null;
	private TextView tongTextView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		DuLieu = new ArrayList<String>();
		// DuLieu = new ArrayList<String>(Arrays.asList("Lần Cân 1: ",
		// "Lần Cân 2: "));
		list_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, DuLieu);

		lancan_ListView = (ListView) findViewById(R.id.in);
		lancan_ListView.setAdapter(list_adapter);

		// Button Declare
		canButton = (Button) findViewById(R.id.can_button);

		canButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("BACH", "++ ON Can ++");
				// TODO Auto-generated method stub
				// PrintUtils.printImage(mContext.getResources(), mPrinter,
				// true);
				/*
				 * PrintUtils.printText(mPrinter, "Hello World \n" +
				 * "this is new line");
				 */
				can_button();
			}
		});

		xoaButton = (Button) findViewById(R.id.xoa_button);

		xoaButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("BACH", "++ Xoa  ++");
				xoa_button();
			}
		});

		nhan_check = (CheckBox) findViewById(R.id.nhan_check);
		nhan_check.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				nhan_check();
			}
		});
		buttonEnable(false);
		tongTextView = (TextView) findViewById(R.id.tong);
		tongTextView.setText("Tổng: 0 Kg");

		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		// Register for broadcasts when a device is discovered
		/*
		 * IntentFilter filter = new IntentFilter(
		 * BluetoothAdapter.ACTION_STATE_CHANGED);
		 * this.registerReceiver(mStateReceiver, filter);
		 * 
		 * // Register for broadcasts when discovery has finished filter = new
		 * IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		 * this.registerReceiver(mStateReceiver, filter); filter = new
		 * IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		 * this.registerReceiver(mStateReceiver, filter); filter = new
		 * IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		 * this.registerReceiver(mStateReceiver, filter); filter = new
		 * IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
		 * this.registerReceiver(mStateReceiver, filter);
		 */

		// register Bluetooth Recevier

		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothDevice.ACTION_CLASS_CHANGED);
		filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
		this.registerReceiver(mStateReceiver, filter);

	}

	// receive the state change of the bluetooth.
	private final BroadcastReceiver mStateReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d("BACH", "Bluetooth SERVICE Changed: " + action);

			if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
				Log.d("BACH", "++ Disconnect ++");
				if (mPrinter != null) {
					mPrinter.closeConnection();
				}
				if (mChatService != null)
					mChatService.stop();
				// isConnected = false;
				// setButtonState();
			}
			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				Log.d("BACH", "Bluetooth State Changed: " + action);
			}

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onStart() {
		super.onStart();
		if (D)
			Log.e(TAG, "++ ON START ++");

		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			if (mChatService == null)
				setupChat();

		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
				// Start the Bluetooth chat services
				mChatService.start();
			} else {

			}
		}

	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();

		if (D)
			Log.e(TAG, "-- ON STOP --");
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		// Stop the BlueTooth chat services
		if (mChatService != null)
			mChatService.stop();
		try {
			// mPrinter.receive();
			if (mPrinter != null) {
				mPrinter.closeConnection();
			}
			mContext.unregisterReceiver(mStateReceiver);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		android.os.Process.killProcess(android.os.Process.myPid());
		if (D)
			Log.e(TAG, "--- ON BACK PRESS ---");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
		if (mChatService != null)
			mChatService.stop();
		try {
			// mPrinter.receive();
			if (mPrinter != null) {
				mPrinter.closeConnection();
			}
			mContext.unregisterReceiver(mStateReceiver);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		android.os.Process.killProcess(android.os.Process.myPid());
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
	}

	private void setupChat() {
		Log.d(TAG, "setupChat()");
		receiveTextView = (TextView) findViewById(R.id.receive);
		receiveTextView.setText(" Kết nối Cân");
		mConversationArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.message);
		// sendMessage("This is Spartaaa!!");

		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = new BluetoothChatService(this, mHandler);

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");
	}

	/**
	 * Sends a message.
	 * 
	 * @param message
	 *            A string of text to send.
	 */
	@SuppressWarnings({ "unused" })
	private void sendMessage(String message) {
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mChatService.write(send);

			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);
			// mOutEditText.setText(mOutStringBuffer);
		}
	}

	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					setStatus(getString(R.string.title_connected_to,
							mConnectedDeviceName));
					// mConversationArrayAdapter.clear();
					buttonEnable(true);
					break;
				case BluetoothChatService.STATE_CONNECTING:
					setStatus(R.string.title_connecting);
					buttonEnable(false);
					break;
				case BluetoothChatService.STATE_LISTEN:
					setStatus(R.string.title_connecting);
					buttonEnable(false);
				case BluetoothChatService.STATE_NONE:
					setStatus(R.string.title_not_connected);
					receiveTextView.setText("0 Kg");
					buttonEnable(false);
					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				mConversationArrayAdapter.add("Me:  " + writeMessage);
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);

				String displayMessage;
				if (readMessage.length() < 6) {
					displayMessage = Integer.toString(Integer
							.parseInt(readMessage));
					receiveTextView.setText(displayMessage + " Kg");
				}
				/*
				 * mConversationArrayAdapter.add(mConnectedDeviceName + ":  " +
				 * readMessage);
				 */
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Log.d("BACH", "mConnectedDeviceName is: "
						+ mConnectedDeviceName);

				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	private final void setStatus(int resId) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(resId);

		/*
		 * ColorDrawable colorDrawable = new
		 * ColorDrawable(Color.parseColor("#ff33b5e5"));
		 * actionBar.setBackgroundDrawable(colorDrawable);
		 */
	}

	private final void setStatus(CharSequence subTitle) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(subTitle);
	}

	private final void setStatusIn(CharSequence subTitle) {
		final ActionBar actionBar = getActionBar();
		actionBar.setTitle(subTitle);
	}

	private final void setStatusIn(int resId) {
		final ActionBar actionBar = getActionBar();
		actionBar.setTitle(resId);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE_SECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data, true);
			}
			break;
		case REQUEST_CONNECT_DEVICE_INSECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				String address = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				BluetoothDevice device = mBluetoothAdapter
						.getRemoteDevice(address);
				Log.d("In", "MAC adress" + address);
				// printer 8C:DE:52:0F:4C:62
				mConnectedDeviceName = device.getName();
				initPrinter(device);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				setupChat();
			} else {
				// User did not enable Bluetooth or an error occurred
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	private void connectDevice(Intent data, boolean secure) {
		// Get the device MAC address
		String address = data.getExtras().getString(
				DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mChatService.connect(device, secure);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent serverIntent = null;
		switch (item.getItemId()) {
		case R.id.weigh_connect_scan:
			/*
			 * String can_address = "00:1B:35:07:72:77"; BluetoothDevice device
			 * = mBluetoothAdapter .getRemoteDevice(can_address); // Attempt to
			 * connect to the device mChatService.connect(device, true);
			 */
			return true;
		case R.id.printer_connect_scan:
			/*
			 * try { // mPrinter.receive(); if (mPrinter != null) {
			 * mPrinter.closeConnection(); } unregisterReceiver(mStateReceiver);
			 * } catch (Exception ex) { ex.printStackTrace(); } if (mChatService
			 * != null) mChatService.stop();
			 */
			/*
			 * // set the animation for the view that enters the screen
			 * mViewFlipper.setInAnimation(slide_in_right); // set the animation
			 * for the view leaving th screen
			 * mViewFlipper.setOutAnimation(slide_out_left);
			 * mViewFlipper.showNext();
			 */

			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(this, BienBan.class);
			Bundle KetQua = new Bundle();
			String TongExtra = tongTextView.getText().toString();
			KetQua.putStringArrayList("LANDO", DuLieu);
			if (nhan_checked)
				TongExtra = TongExtra + "(x 2)";
			KetQua.putString("TONG", TongExtra);
			serverIntent.putExtras(KetQua);
			startActivityForResult(serverIntent, 1);

			return true;
		case R.id.discoverable:
			// Ensure this device is discoverable by others
			ensureDiscoverable();
			return true;
		}
		return false;
	}

	private void ensureDiscoverable() {
		if (D)
			Log.d(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	// ------------------- Printer------------------------
	// use device to init printer.

	private void initPrinter(BluetoothDevice device) {
		mPrinter = new BluetoothPrinter(device);
		mPrinter.setCurrentPrintType(PrinterType.TIII);
		/*
		 * if (true) { mPrinter.setCurrentPrintType(true ? PrinterType.TIII :
		 * PrinterType.T9); } else {
		 * mPrinter.setCurrentPrintType(PrinterType.T5); }
		 */
		// set handler for receive message of connect state from sdk.
		mPrinter.setHandler(mPrinterHandler);
		mPrinter.openConnection();
		mPrinter.setEncoding("GBK");
	}

	// use device to init printer.
	@SuppressWarnings({ "deprecation", "unused" })
	private void initPrinter(String deviceName) {
		mPrinter = new BluetoothPrinter(deviceName);
		if (mPrinter.isPrinterNull()) {
			// Toast.makeText(mContext, getString(R.string.no_bounded_device,
			// deviceName), Toast.LENGTH_LONG).show();
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		/*
		 * if (true) { mPrinter.setCurrentPrintType(true ? PrinterType.TIII :
		 * PrinterType.T9); } else {
		 * mPrinter.setCurrentPrintType(PrinterType.T5); }
		 */
		mPrinter.setCurrentPrintType(PrinterType.TIII);
		// set handler for receive message of connect state from sdk.
		mPrinter.setHandler(mPrinterHandler);
		mPrinter.openConnection();
		mPrinter.setEncoding("GBK");
		// String country = Locale.getDefault().getCountry();
		// Log.i(TAG, "local country is: " + country);
		// if ("TW".equalsIgnoreCase(country)) {
		// mPrinter.setEncoding("BIG5");
		// }
	}

	// The Handler that gets information back from the bluetooth printer.
	private final Handler mPrinterHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.i(TAG, "msg.what is : " + msg.what);
			switch (msg.what) {
			case BluetoothPrinter.Handler_Connect_Connecting:
				setStatusIn(R.string.title_connecting);
				/*
				 * isConnecting = true;
				 * mTitle.setText(R.string.title_connecting);
				 */// Toast.makeText(getApplicationContext(),
					// R.string.bt_connecting,Toast.LENGTH_SHORT).show();
				break;
			case BluetoothPrinter.Handler_Connect_Success:
				setStatusIn(getString(R.string.title_connected_to,
						mConnectedDeviceName));
				/*
				 * isConnected = true; isConnecting = false;
				 * mTitle.setText(getString(R.string.title_connected) + ": "+
				 * mPrinter.getPrinterName());
				 */// Toast.makeText(getApplicationContext(),
					// R.string.bt_connect_success,Toast.LENGTH_SHORT).show();
				break;
			case BluetoothPrinter.Handler_Connect_Failed:
				setStatusIn(R.string.title_not_connected);
				/*
				 * isConnected = false; isConnecting = false;
				 * mTitle.setText(R.string.title_not_connected);
				 */// Toast.makeText(getApplicationContext(),
					// R.string.bt_connect_failed, Toast.LENGTH_SHORT).show();
				break;
			case BluetoothPrinter.Handler_Connect_Closed:
				setStatusIn(R.string.title_not_connected);
				/*
				 * isConnected = false; isConnecting = false;
				 * mTitle.setText(R.string.title_not_connected);
				 */// Toast.makeText(getApplicationContext(),
					// R.string.bt_connect_closed, Toast.LENGTH_SHORT).show();
				break;
			}
			// setButtonState();
		}
	};

	// Can Function
	// function them list
	boolean nhan_checked = false;
	int tong = 0;

	private final void can_button() {
		String Giatri = (String) receiveTextView.getText();
		String DulieuLuu = null;
		sotruc++;
		DulieuLuu = "Lần Cân " + Integer.toString(sotruc) + ": ";
		list_adapter.add(DulieuLuu + Giatri);
		Log.d("test", "Size Du Lieu " + DuLieu.size());
		tinh_tong();
		lancan_ListView.setSelection(list_adapter.getCount() - 1);

	}

	private final void xoa_button() {
		if (sotruc > 0) {
			Log.d("test", "Size Du Lieu " + DuLieu.size());
			sotruc--;
			// DuLieu.remove(DuLieu.size());
			list_adapter.remove(list_adapter.getItem(sotruc));
			tinh_tong();
			lancan_ListView.setSelection(list_adapter.getCount() - 1);
		}
	
	}

	private final void tinh_tong() {
		tong = 0;
		for (int i = 0; i < DuLieu.size(); i++) {
			String temp = DuLieu.get(i);
			String[] result = temp.split("\\s");
			if (result.length == 5)
				tong += Integer.parseInt(result[3]);
		}

		if (nhan_checked) {
			tong = tong << 1;
		}
		tongTextView.setText("Tổng: " + Integer.toString(tong) + " Kg");

	}

	private final void nhan_check() {
		nhan_checked = nhan_check.isChecked();
		tinh_tong();
	}

	public void ketnoican(View view) {
		String can_address = "00:1B:35:07:72:77";
		// Get local Bluetooth adapter
		//mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		/*// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}*/

		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(can_address);
		// Attempt to connect to the device
		if (mChatService == null)
			setupChat();
		mChatService.connect(device, true);
		
	}

	public void ketnoiin(View view) {
		String address = "00:1B:35:07:25:2B"; //"8C:DE:52:0F:4C:62";
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		if (device == null) {
			Log.e("MainActivity", "can't get remote device from given MAC : "
					+ address);
			return;
		}
		Log.d("In", "MAC adress" + address);
		mConnectedDeviceName = device.getName();
		if (mPrinter != null) {
			mPrinter.closeConnection();
			mPrinter = new BluetoothPrinter(device);
			mPrinter.setCurrentPrintType(PrinterType.TIII);

			mPrinter.setHandler(mPrinterHandler);
			mPrinter.openConnection();
			mPrinter.setEncoding("GBK");

		} else
			initPrinter(device);
	}

	public void buttonEnable(boolean e) {
		canButton.setEnabled(e);
		xoaButton.setEnabled(e);
		nhan_check.setEnabled(e);
	}

}
