package com.toh.weatherboard;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.geemo.uart.driver.UART_DATA_Monitoring;
import com.geemo.uart.driver.UART_USB_DEVICE_Probe;
import com.geemo.uart.driver.UART_USB_Driver;

public class WeatherBoard extends Activity {
	final Context context = this;
	RegistrationAdapter adapter;
	RegistrationOpenHelper helper;

	private BroadcastReceiver sendBroadcastReceiver;
	private BroadcastReceiver deliveryBroadcastReceiver;
	String SENT = "SMS_SENT";
	String DELIVERED = "SMS_DELIVERED";

	private static final long GET_DATA_INTERVAL = 36000;
	/*
	 * int images[] = { R.drawable.image0, R.drawable.image1, R.drawable.image2,
	 * R.drawable.image3, R.drawable.image4, R.drawable.image5,
	 * R.drawable.image6, R.drawable.image7, R.drawable.image8,
	 * R.drawable.image9, R.drawable.image10, R.drawable.image11,
	 * R.drawable.image12, R.drawable.image13, R.drawable.image14,
	 * R.drawable.image15, R.drawable.image16, R.drawable.image17,
	 * R.drawable.image18, R.drawable.image19 };
	 */
	int index = 0;
	RelativeLayout img;
	Handler hand = new Handler();

	private final String TAG = WeatherBoard.class.getSimpleName();
	private UART_USB_Driver mUART_Device; // add pye
	private String return_msg;
	byte[] send_data1 = { 'S', 0x0d, 0x0a }; // add pye
	byte[] send_data2 = { 'E', 'R', 'S' }; // add pye
	byte[] send_data3 = { 'T' }; // add pye

	public byte[] head_data1 = new byte[6]; // add pye
	public byte[][] recv_data2 = new byte[30][22]; // add pye

	int bac_data0 = 0;
	int bac_data1 = 0;
	int bac_data2 = 0;
	int bac_data3 = 0;
	int bac_data4 = 0;
	int bac_data5 = 0;
	int bac_data6 = 0;
	int bac_data7 = 0;
	int bac_data8 = 0;
	int bac_data9 = 0;
	int uart_ok = 0;
	int w_kma = 0;
	int tod_war = 0;
	int tom_war = 0;
	int tod_snow = 0;
	int show_cnt = 0;
	int sky_state = 0;
	int pty_state = 0;
	int toom = 0;
	int toom_h = 0;
	int toom_t = 0;
	int start_flew = 0;
	int flow_timer = 0;

	double tod_data = 0;
	double r12 = 0;
	double s12 = 0;
	double r06 = 0;
	double s06 = 0;

	double ws = 0;

	String tod_str = "";
	String num_r12 = "";
	String num_pop = "";
	String num_s12 = "";
	String num_r06 = "";
	// String num_pop = "";
	String num_s06 = "";

	String num_ws = "";
	String result = "";
	String result2 = "";
	String result3 = "";
	String result4 = "";
	String emergy_str = "";
	String num;
	int today;
	public byte[] send_msg = new byte[26];
	byte[] recv_data1 = new byte[256]; // add pye
	public float randInt; // modify pye
	public int recv_flag = 0; // add pye
	public int recv_tot = 0; // add pye

	public int recv_cnt; // add pye
	public int blood_cnt; // add pye
	public int data_cnt; // add pye
	public int data_tot; // add pye
	public int real_cnt; // add pye
	public int blood_id; // add pye
	public int blood_icon; // add pye
	public int blood_YY; // add pye
	public int blood_MM; // add pye
	public int blood_DD; // add pye
	public int blood_hh; // add pye
	public int blood_mm; // add pye

	public int blood_value; // add pye
	public int blood_value_R; // add pye
	public int blood_temp; // add pye
	public int blood_temp2; // add pye
	public int r_flag; // add pye
	public int s_data; // add pye
	public int recv_ok = 0;

	double temp;
	double humi;
	double temp_max;
	double humi_max;
	double temp_min;
	double humi_min;
	double wind_d;
	double wind_s;
	double rfalltoday;
	double rfallhours;
	double old_rfallhours;
	double rfallhours_a[] = new double[60];
	double rfallyester;

	double rfalls;
	double rfallm;
	double old_rfallm;
	double rstat;

	double temps;
	double humis;
	double winds_d;
	double winds_s;

	double tempr;
	double humir;
	double windr_d;
	double windr_s;
	double rfallr;
	double rfall_change;
	double old_rfall_change;

	double temp_dis;
	double humi_dis;
	double wind_s_dis;
	double wind_d_dis;
	double rfalls_dis;
	double rfallhours_dis;
	double rfalltoday_dis;
	double rfallyester_dis;

	double t_temp;
	double t_humi;
	double t_wind_d;
	double t_wind_s;
	double t_rfallm;
	double rstats;

	int rfall_tog;
	int rfallmonth_dis;
	int rfallyear_dis;
	int dis_tog;
	int tccc;
	int year;
	int month;
	int day;
	int wday;
	int old_day;
	int hours;
	int old_hours;
	int minutes;
	int old_minutes;
	int old_minutes2;
	int seconds;
	int r_timer;
	int rcv_timer;
	int m_count;
	int kkk;
	int send_timer;
	int rfalls_tog;
	int rfalls_tog2;
	int i = 0;

	TextView tv, tv3, rn;

	// blood_cnt = 0;
	ProgressDialog progressDialog_sugar;

	MyLocationListener listener;
	LocationManager manager;
	listWeatherView listadapter; // ���������� �ѷ��ִ� ����Ʈ��� �����
	ArrayAdapter<String> sidoAdapter; // �õ� ������ �ѷ��ִ� ���ǳʿ� �����
	ArrayAdapter<String> gugunAdapter; // ���� ������ �ѷ��ִ� ���ǳʿ� �����
	ArrayAdapter<String> dongAdapter; // ���� ������ �ѷ��ִ� ���ǳʿ� �����
	Spinner sidoSpinner; // �õ����ǳ�
	Spinner gugunSpinner; // �������ǳ�
	Spinner dongSpinner; // ���齺�ǳ�
	Button getBtn; // ���� �������� ��ư
	Button gpsBtn;
	TextView text; // ���������� ��ǥ�ð�����
	TextView maxtemp, mintemp, maxhumid, minhumid;
	ListView listView1; // ���������� �ѷ��� ����Ʈ��

	String tempDong = "4215025000"; // �⺻dongcode
	String sCategory; // ����
	String sTm; // ��ǥ�ð�
	String[] sHour; // �����ð�(�� 15������ �޾ƿ� 3��*5��)
	String[] sDay; // ��¥(���°��??)
	String[] sTemp; // ����µ�
	String[] sWdKor; // ǳ��
	String[] sReh; // ����
	String[] sWfKor; // ����
	String[] sWs; // ����
	String[] smxTemp; // �ְ�µ�
	String[] smnTemp; // �����µ�
	String[] fDay; // �����µ�
	// DB�� ����
	String[] sidonum; // �õ� �ڵ�
	String[] Nsidonum; // �̰� ����table���� �������� �ڵ�
	String[] sidoname; // �õ� �̸�
	String[] gugunnum; // ���� �ڵ�
	String[] Ngugunnum;// ���� table���� ������ ���� �ڵ�
	String[] gugunname;// ���� �̸�
	String[] dongnum; // �� �ڵ�
	String[] dongname; // �� �̸�
	String[] gridx; // x��ǥ
	String[] gridy; // y��ǥ
	String[] id; // id
	String[] sLong_name; // gps�� �����ڵ��� �ּҸ� �ļ��ؼ� ������ ����

	double latitude, longitutde; // ������ �浵�� ������ ����
	double temp_Max;
	double temp_Min;
	int hum_Max;
	int hum_Min;
	static SQLiteDatabase db; // ���

	int data = 0; // �̰� �Ľ��ؼ� array�� ������ ����
	int geodata = 0; // �����ڵ��� �ļ� array ����
	boolean updated; // �̰� �������� �Ѹ������� �÷���
	boolean bCategory; // ���� ������ ���� �÷��׵�
	boolean bTm;
	boolean bHour;
	boolean bDay;
	boolean bfDay;
	boolean bTemp;
	boolean bmxTemp;
	boolean bmnTemp;
	boolean bWdKor;
	boolean bReh;
	boolean bItem;
	boolean bWfKor;
	boolean bWs;
	boolean bLong_name;
	boolean tCategory; // �̰� text�� �Ѹ������� �÷���
	boolean tTm;
	boolean tItem;

	Handler handler; // �ڵ鷯
	Handler handler2; // �����ڵ��ļ��� �ڵ鷯
	String dbFile = "weather.db3";
	String dbFolder = "/data/data/com.toh.weatherboard/datebases/";
	String numDong; // ���������� ������ ���� �����ڵ尡 ����Ǵ� ����
	String numSido; // �õ� �ڵ尡 ����Ǿ� ����table���� ���ϱ� ���� ����
	String numGugun;// ���� �ڵ尡 ����Ǿ� ��table���� ���ϱ� ���� ����
	String numsy1;

	final int tableSido = 1; // �̰� switch case������ ������ ���� ����
	final int tableGugun = 2;
	final int tableDong = 3;

	Handler handler1 = new Handler();
	Runnable refresh;

	int page = 0;

	// private Handler mHandler;
	// private Runnable mRunnable;

	/**
	 * The system's USB service.
	 */
	private UsbManager mUsbManager; // add pye

	private final ExecutorService mExecutor = Executors
			.newSingleThreadExecutor(); // add pye

	private UART_DATA_Monitoring mUART_Monitor; // add pye

	private final UART_DATA_Monitoring.UART_Monitoring mUART_Monitoring = new UART_DATA_Monitoring.UART_Monitoring() {

		@Override
		public void UART_RunError(Exception e) {
			Log.d(TAG, "Runner stopped.");
		}

		@Override
		public void UART_Receive_NewData(final byte[] data) {
			WeatherBoard.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					WeatherBoard.this.UART_Received_DATA(data);
				}
			});
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			Runtime.getRuntime().exec("su");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		startService(new Intent("com.toh.weatherboard"));

		sendBroadcastReceiver = new BroadcastReceiver() {

			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS Sent",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(getBaseContext(), "Generic failure",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(getBaseContext(), "No service",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(getBaseContext(), "Null PDU",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(getBaseContext(), "Radio off",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};

		deliveryBroadcastReceiver = new BroadcastReceiver() {
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS Delivered",
							Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(getBaseContext(), "SMS not delivered",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};
		registerReceiver(deliveryBroadcastReceiver, new IntentFilter(DELIVERED));
		registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);

		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());

		adapter = new RegistrationAdapter(this);

		kkk = 0;
		send_timer = 0;
		r_timer = 0;
		rfall_change = 0;
		rfall_tog = 0;
		rcv_timer = 0;
		tccc = 0;

		Thread myThread = null;
		Runnable runnable = new CountDownRunner();
		myThread = new Thread(runnable);
		myThread.start();

		r_timer = 0;
		// show();

		// BroadcastReceiver detachReceiver = new BroadcastReceiver() {
		// public void onReceive(Context context, Intent intent) {
		// if (intent.getAction().equals(
		// UsbManager.ACTION_USB_DEVICE_DETACHED)) {
		// Toast.makeText(getApplicationContext(),
		// "������Ⱑ �и��Ǿ����ϴ�. ���α׷��� �����մϴ�.", Toast.LENGTH_SHORT)
		// .show();
		//
		// try {
		//
		// finish();
		// } catch (Exception e) {
		//
		// }
		// } else if (intent.getAction().equals(
		// UsbManager.ACTION_USB_DEVICE_DETACHED)) {
		// Toast.makeText(getApplicationContext(), "������Ⱑ ���� �Ǿ����ϴ�.",
		// Toast.LENGTH_SHORT).show();
		// }
		//
		// }
		// };
		//
		// IntentFilter filter = new IntentFilter();
		// filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		// registerReceiver(detachReceiver, filter);

		// usb
		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

		recv_cnt = 0;
		blood_cnt = 0;
		data_cnt = 0;
		real_cnt = 0;
		data_tot = 5;
		s_data = 0;
		r_flag = 0;
		head_data1[0] = 0;
		head_data1[1] = 0;
		head_data1[2] = 0;
		head_data1[3] = 0;
		head_data1[4] = 0;
		head_data1[5] = 0;

		// onCreate ����
		try {
			boolean bResult = isCheckDB(getBaseContext()); // DB�� �ִ���?

			if (!bResult) { // DB�� ������
				copyDB(getBaseContext()); // bd����
				Toast.makeText(getApplicationContext(), "DB�� ������",
						Toast.LENGTH_SHORT).show();
			} else { // DB�� ������
				Toast.makeText(getApplicationContext(), "�̹� DB���־��",
						Toast.LENGTH_SHORT).show();

			}

		} catch (Exception e) { // ���ܹ߻���

			Toast.makeText(getApplicationContext(), "���ܰ� �߻��߾��",
					Toast.LENGTH_SHORT).show();
		}

		handler = new Handler(); // ������&�ڵ鷯ó��
		handler2 = new Handler(); // ������&�ڵ鷯ó��

		listView1 = (ListView) findViewById(R.id.listView1); // �������� ����Ʈ��

		bCategory = bTm = bHour = bTemp = bWdKor = bReh = bDay = bWfKor = bWs = tCategory = tTm = tItem = false; // �ο�����

		listadapter = new listWeatherView(getBaseContext()); // ����Ʈ�並 ���������
		listView1.setAdapter(listadapter); // ����Ϳ� ����Ʈ�並 ����
		text = (TextView) findViewById(R.id.textView1); // �ؽ�Ʈ ��ü����
		// getBtn = (Button) findViewById(R.id.getBtn); // ��ư ��ü����
		// gpsBtn = (Button) findViewById(R.id.gpsBtn); // ��ư ��ü����
		Log.e("###", "000");
//		// Warning alertDialog start
//		boolean firstboot = getSharedPreferences("BOOT_PREF", MODE_PRIVATE)
//				.getBoolean("firstboot", true);
//		if (firstboot) {
//			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//					context);
//			LayoutInflater inflater = WeatherBoard.this.getLayoutInflater();
//			// this is what I did to added the layout to the alert dialog
//			View layout = inflater.inflate(R.layout.dialog, null);
//			alertDialogBuilder.setView(layout);
//			sidoSpinner = (Spinner) layout.findViewById(R.id.sidospinner); // �õ���
//			gugunSpinner = (Spinner) layout.findViewById(R.id.gugunspinner); // ������
//			dongSpinner = (Spinner) layout.findViewById(R.id.dongspinner); // �����
//
//			sidoSpinner.setOnItemSelectedListener(new OnItemSelectedListener() { // �̺κ���
//						@Override
//						public void onItemSelected(AdapterView<?> parent,
//								View v, int position, long id) { // ���ý�
//							numSido = sidonum[position]; // �õ��� ���õǸ� �ش� �ڵ带 ������
//															// �ִ´�
//							queryData(tableGugun); // ���� DB������~
//						}
//
//						@Override
//						public void onNothingSelected(AdapterView<?> parent) { // ��
//
//						}
//					});
//			gugunSpinner
//					.setOnItemSelectedListener(new OnItemSelectedListener() { // �̺κ���
//
//						@Override
//						public void onItemSelected(AdapterView<?> parent,
//								View v, int position, long id) { // ���ý�
//							numGugun = gugunnum[position]; // ������ ���õǸ� �ش� �ڵ带
//															// ������
//							queryData(tableDong); // ���� DB������~
//						}
//
//						@Override
//						public void onNothingSelected(AdapterView<?> parent) { // ��
//
//						}
//					});
//			dongSpinner.setOnItemSelectedListener(new OnItemSelectedListener() { // �̺κ���
//
//						@Override
//						public void onItemSelected(AdapterView<?> parent,
//								View v, int position, long id) { // ���ý�
//							tempDong = dongnum[position];
//							numDong = tempDong; // ���õ� �����ڵ带 ������ ����
//						}
//
//						@Override
//						public void onNothingSelected(AdapterView<?> parent) { // ��
//																				// ���ý�
//						}
//					});
//
//			// set dialog message
//			alertDialogBuilder
//					.setMessage("��ġ�� ������ �ּ���.")
//					.setCancelable(false)
//					.setPositiveButton("Ȯ��",
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int id) {
//									Log.e("###", "111");
//									numDong = tempDong;
//									text.setText(""); // �ϴ� �ߺ��ؼ� ������� ����ؼ� ����
//									String numdong = numDong.toString();
//									Log.e("numdong", numdong);
//									long val = adapter.insertDetails(numdong);
//									Log.e("###", "aaa");
//									numsy1 = adapter.queryName();
//									System.out.println("numsy1" + numsy1);
//
//									network_thread thread = new network_thread(); // ���������(UI
//									thread.start(); // ������ ����
//									dialog.cancel();
//								}
//							});
//
//			// create alert dialog
//			AlertDialog alertDialog = alertDialogBuilder.create();
//
//			// show it
//			alertDialog.show();
//
//			getSharedPreferences("BOOT_PREF", MODE_PRIVATE).edit()
//					.putBoolean("firstboot", false).commit();
//		}
		
		refresh = new Runnable() {
			public void run() {
				// Do something
				numDong = "4119554000";
				text.setText("");
				show();
				network_thread thread = new network_thread(); // ���������(UI
				thread.start(); // ������ ����
				Toast.makeText(getApplicationContext(), "Refresh Test.",
						Toast.LENGTH_SHORT).show();
				handler1.postDelayed(refresh, 300000);
				 //Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				 // Vibrate for 500 milliseconds
				 //v.vibrate(1000);
			}
		};
		handler1.post(refresh);

		
//		numDong = "4119554000";
//		text.setText("");
//		//show();
//		network_thread thread = new network_thread(); // ���������(UI
//		thread.start(); // ������ ����
//		Log.e("###", "222");
		// mRunnable = new Runnable() {
		// @Override
		// public void run() {
		// network_thread thread = new network_thread(); // ���������(UI
		// thread.start(); // ������ ����
		// Toast.makeText(getApplicationContext(), "Refresh Test.",
		// Toast.LENGTH_SHORT).show();
		// }
		// };
		//
		// mHandler = new Handler();
		// mHandler.postDelayed(mRunnable, 60000);
		
		maxtemp = (TextView) findViewById(R.id.maxTemp);
		mintemp = (TextView) findViewById(R.id.minTemp);
		maxhumid = (TextView) findViewById(R.id.maxHumid);
		minhumid = (TextView) findViewById(R.id.minHumid);
		
//		if (page == 0) {
//			queryData(tableSido); // �õ� DB ������ ����
//		}
//		Log.e("###", "444");
//		page++;
	}

	/**
	 * DB�� �������� �κ� �õ�, ����, ���� ��� ���̺��� ���ڵ尡 �ٸ��⶧���� case���� ��µ� �ڵ尡 �ʹ� �����;;
	 * 
	 * @author Ans
	 * @param table
	 */
	private void queryData(final int table) {
		// TODO Auto-generated method stub
		Log.e("###", "555");
		openDatabase(dbFolder + dbFile); // db�� ����� �������� db�� ������ �´�
		String sql = null; // sql��ɾ ������ ����
		Cursor cur = null; // db������ Ŀ��
		int Count; // db���� �� ����

		switch (table) {

		case tableSido:
			sql = "select sido_num, sido_name from t_sido"; // �õ� ���̺��� �õ��ڵ��
															// �õ��̸�
			cur = db.rawQuery(sql, null); // Ŀ���� ����
			break;
		case tableGugun: // ���� ���̺��� �õ����� ���õ� �õ��� ����������
			sql = "select sido_num, gugun_num, gjgun_name from t_gugun where sido_num = "
					+ numSido;
			cur = db.rawQuery(sql, null);
			break;
		case tableDong: // ���� ���̺� ���õ� �����ڵ�� ���ؼ�
			sql = "select gugun_num, dong_num, dong_name, gridx, gridy, _id from t_dong where gugun_num = "
					+ numGugun;
			cur = db.rawQuery(sql, null);
			break;
		default:
			break;
		}

		Count = cur.getCount(); // db�� ������ ����

		switch (table) {

		case tableSido:

			sidoname = new String[Count]; // ������ŭ �迭�� �����
			sidonum = new String[Count];

			if (cur != null) { // �̺κ��� Ŀ���� �����͸� �о�ͼ� ������ �����ϴ� �κ�
				cur.moveToFirst();
				startManagingCursor(cur);
				for (int i = 0; i < Count; i++) {
					sidonum[i] = cur.getString(0);
					sidoname[i] = cur.getString(1);
					cur.moveToNext();
				}
				// ������ ������ �Ǿ����� ���ǳʸ� ����� �ѷ�����
				// ����͸� ���� ���ǳʿ� donglist �־���
				sidoAdapter = new ArrayAdapter<String>(getBaseContext(),
						android.R.layout.simple_spinner_item, sidoname);
				sidoAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // dropdown����
				sidoSpinner.setAdapter(sidoAdapter); // ���ǳ� ����
			}
			break;
		case tableGugun: // ������ �����۾�

			Nsidonum = new String[Count];
			gugunnum = new String[Count];
			gugunname = new String[Count];
			if (cur != null) {
				cur.moveToFirst();
				startManagingCursor(cur);
				for (int i = 0; i < Count; i++) {
					Nsidonum[i] = cur.getString(0);
					gugunnum[i] = cur.getString(1);
					gugunname[i] = cur.getString(2);
					cur.moveToNext();
				}
				// ����͸� ���� ���ǳʿ� donglist �־���
				gugunAdapter = new ArrayAdapter<String>(getBaseContext(),
						android.R.layout.simple_spinner_item, gugunname); // ����͸�
				gugunAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // dropdown����
				gugunSpinner.setAdapter(gugunAdapter);

			}
			break;

		case tableDong: // ���鵵 �����۾�

			Ngugunnum = new String[Count];
			dongnum = new String[Count];
			dongname = new String[Count];
			gridx = new String[Count];
			gridy = new String[Count];
			id = new String[Count];
			if (cur != null) {
				cur.moveToFirst();
				startManagingCursor(cur);
				for (int i = 0; i < Count; i++) {
					Ngugunnum[i] = cur.getString(0);
					dongnum[i] = cur.getString(1);
					dongname[i] = cur.getString(2);
					gridx[i] = cur.getString(3);
					gridy[i] = cur.getString(4);
					id[i] = cur.getString(5);
					cur.moveToNext();
				}
				cur.close();
				dongAdapter = new ArrayAdapter<String>(getBaseContext(),
						android.R.layout.simple_spinner_item, dongname); // ����͸�
				dongAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // dropdown����
				dongSpinner.setAdapter(dongAdapter);
			}
			break;

		default:
			break;

		}
	}

	/**
	 * �̺κ��� db�� �����ִ� �κ�
	 * 
	 * @author Ans
	 * @param databaseFile
	 */
	public static void openDatabase(String databaseFile) {

		try {
			db = SQLiteDatabase.openDatabase( // ������ ������ db�� �����ͼ� �а�,���� �����ϰ�
												// �о�´�
					databaseFile, null, SQLiteDatabase.OPEN_READWRITE);

		} catch (SQLiteException ex) {

		}
	}

	public static void closeDatabase() {
		try {
			// close database
			db.close();
		} catch (Exception ext) {
			ext.printStackTrace();

		}
	}

	// DB�� �ֳ� üũ�ϱ�
	public boolean isCheckDB(Context mContext) {

		String filePath = dbFolder + dbFile;
		File file = new File(filePath);

		if (file.exists()) { // db������ ������ ������ true
			return true;
		}

		return false; // �ƴ� default�� false�� ��ȯ

	}

	// DB�� �����ϱ�
	// assets�� /db/xxxx.db ������ ��ġ�� ���α׷��� ���� DB�������� �����ϱ�
	public void copyDB(Context mContext) { // ���� db�� ���� ��� ���縦 �ؾߵȴ�.

		AssetManager manager = mContext.getAssets(); // asserts �������� ������ �б�����
														// ���ܴ�.���� ��
		String folderPath = dbFolder; // db���� //�ϴ� DB�� �� ������ ������ �Ͽ����� ��߰���?
		String filePath = dbFolder + dbFile; // db������ ���ϰ��
		File folder = new File(folderPath);
		File file = new File(filePath);

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			// �ϴ� asserts������ db�������� db�� ��������
			InputStream is = manager.open("db/" + "weather.db3");
			BufferedInputStream bis = new BufferedInputStream(is);

			if (folder.exists()) { // �츮�� �����Ϸ��� db������ �̹� ������ �Ѿ��
			} else {
				folder.mkdirs(); // ������� ������ ������
			}

			if (file.exists()) { // ������ �ִٸ�
				file.delete(); // �ϴ� �����
				file.createNewFile(); // �� ������ ������
			}

			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			int read = -1;
			byte[] buffer = new byte[1024]; // buffer�� 1024byte�ϱ� 1k�� �������ְ�
			while ((read = bis.read(buffer, 0, 1024)) != -1) { // db������ �о
																// buffer�� �ְ�
				bos.write(buffer, 0, read); // buffer���� ���� ���� ���Ͽ� ����
			} // �������ش� �Ǵµ� ��Ƴ�;;

			bos.flush();

			bos.close();
			fos.close();
			bis.close();
			is.close();

		} catch (IOException e) {

		}
	}

	/**
	 * ���û�� �����Ͽ� �����ް� �ѷ��ִ� ������
	 * 
	 * @author Ans
	 * 
	 */
	class network_thread extends Thread { // ���û ������ ���� ������
		/**
		 * ���û�� �����ϴ� ������ �̰����� Ǯ�ļ��� �̿��Ͽ� ���û���� ������ �޾ƿ� ������ array������ �־���
		 * 
		 * @author Ans
		 */
		public void run() {
			Log.e("###", "666");
			try {
				updated = false;
				sHour = new String[100]; // �����ð�(��� 15���ۿ� �ȵ������� �˳��ϰ� 20���� ��Ƴ���)
				sDay = new String[100]; // ��¥
				sTemp = new String[100]; // ����µ�
				smxTemp = new String[100]; // �ְ�µ�
				smnTemp = new String[100]; // �����µ�
				sWdKor = new String[100]; // ǳ��
				sReh = new String[100]; // ����
				sWfKor = new String[100]; // ����
				sWs = new String[100]; // ǳ��
				data = 0;

				XmlPullParserFactory factory = XmlPullParserFactory
						.newInstance(); // �̰��� Ǯ�ļ��� ����ϰ� �ϴ°�
				factory.setNamespaceAware(true); // �̸��� ���鵵 �ν�
				XmlPullParser xpp = factory.newPullParser(); // Ǯ�ļ� xpp��� ��ü ����
				Log.e("numDong",numDong);
				String weatherUrl = "http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone="
						+ numDong; // �̰��� ���ûURL
				URL url = new URL(weatherUrl); // URL��ü����
				InputStream is = url.openStream(); // ������ url�� inputstream�� �־�
													// ������ �ϰԵȴ�.
				xpp.setInput(is, "UTF-8"); // �̷��� �ϸ� ������ �ȴ�. ���������� utf-8��

				int eventType = xpp.getEventType(); // Ǯ�ļ����� �±������� �����´�.

				while (eventType != XmlPullParser.END_DOCUMENT) { // ������ ���� �ƴҶ�

					switch (eventType) {
					case XmlPullParser.START_TAG: // '<'�����±׸� ��������

						if (xpp.getName().equals("category")) { // �±׾��� �̸���
																// ī�װ��ϋ� (�̰�
																// �����̸��� ���´�)
							bCategory = true;

						}
						if (xpp.getName().equals("pubDate")) { // ��ǥ�ð�����
							bTm = true;

						}
						if (xpp.getName().equals("hour")) { // �����ð�
							bHour = true;

						}
						if (xpp.getName().equals("day")) { // ������(���� ���� ��)
							bDay = true;

						}
						if (xpp.getName().equals("temp")) { // �����ð����� ����µ�
							bTemp = true;

						}
						if (xpp.getName().equals("wdKor")) { // ǳ������
							bWdKor = true;

						}
						if (xpp.getName().equals("reh")) { // ��������
							bReh = true;

						}
						if (xpp.getName().equals("wfKor")) { // ��������(����, ��������,
																// ��������, �帲, ��,
																// ��/��, ��)
							bWfKor = true;

						}
						if (xpp.getName().equals("ws")) { // ǳ��
							bWs = true;

						}
						if (xpp.getName().equals("tmx")) { // ǳ��
							bmxTemp = true;

						}
						if (xpp.getName().equals("tmn")) { // ǳ��
							bmnTemp = true;

						}
						break;

					case XmlPullParser.TEXT: // �ؽ�Ʈ�� ��������

						if (bCategory) { // �����̸�
							sCategory = xpp.getText();
							bCategory = false;
						}
						if (bTm) { // ��ǥ�ð�
							sTm = xpp.getText();
							bTm = false;
						}
						if (bHour) { // �����ð�
							sHour[data] = xpp.getText();
							bHour = false;
						}
						if (bDay) { // ������¥
							sDay[data] = xpp.getText();
							bDay = false;
						}
						if (bTemp) { // ����µ�
							sTemp[data] = xpp.getText();
							// System.out.println("TEMP"+sTemp[data]);
							bTemp = false;
						}
						if (bWdKor) { // ǳ��
							sWdKor[data] = xpp.getText();
							bWdKor = false;
						}
						if (bReh) { // ����
							sReh[data] = xpp.getText();
							bReh = false;
						}
						if (bWfKor) { // ����
							sWfKor[data] = xpp.getText();
							bWfKor = false;
						}
						if (bWs) { // ǳ��
							sWs[data] = xpp.getText();
							bWs = false;
						}
						if (bmxTemp) { // �ְ�µ�
							smxTemp[data] = xpp.getText();
							bmxTemp = false;
						}
						if (bmnTemp) { // �����µ�
							smnTemp[data] = xpp.getText();
							bmnTemp = false;
						}
						break;

					case XmlPullParser.END_TAG: // '</' �����±׸� ������ (�̺κ��� �߿�)

						if (xpp.getName().equals("item")) { // �±װ� ������ ������ �±��̸���
															// item�̸�(�̰� ���� ������
															// ��
							tItem = true; // ���� �̶� ��� ������ ȭ�鿡 �ѷ��ָ� �ȴ�.
							view_text(); // �ѷ��ִ� ��~
						}
						if (xpp.getName().equals("pubDate")) { // �̰� ��ǥ�ð������ϱ�
																// 1���������Ƿ� �ٷ�
																// �ѷ�����
							tTm = true;
							//view_text();
						}
						if (xpp.getName().equals("category")) { // �̰͵� ���������� �ٷ�
																// �ѷ��ָ� ��
							tCategory = true;
							//view_text();
						}
						if (xpp.getName().equals("data")) { // data�±״� �����ð�����
															// ���������� �ϳ����̴�.
							data++; // �� data�±� == ���� ���� �׷��Ƿ� �̶� array�� ����������
						}
						break;
					}
					eventType = xpp.next(); // �̰� ���� �̺�Ʈ��~
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		/**
		 * �� �κ��� �ѷ��ִ°� �Ѹ��°� �ڵ鷯��~
		 * 
		 * @author Ans
		 */
		private void view_text() {
			Log.e("###", "777");
			handler.post(new Runnable() { // �⺻ �ڵ鷯�ϱ� handler.post�ϸ��

				@Override
				public void run() {
					Log.e("###", "888");
					if (tCategory) { // �����̸� ���Դ�
						text.setText(text.getText() + "����:" + sCategory + "\n");
						tCategory = false;
					}
					if ((tTm) && (sTm.length() > 11)) { // ��ǥ�ð� ���Դ�
						text.setText(text.getText() + "��ǥ�ð�:" + sTm + "\n");
						tTm = false;
					}
					if (tItem) { // ������ �� �о���

						for (int i = 0; i < data; i++) { // array�� �Ǿ������� for������
							Log.e("###", "999");
							if (sDay[i] != null) { // �̰� null integer ���� ������

								if (sDay[i].equals("0")) { // ��ǥ�ð��� 0�̸� ����
									System.out.println("sDay[i]" + sDay[i]);
									// sDay[i] = "��¥:" + "����";

								} else if (sDay[i].equals("1")) { // 1�̸� ����
									sDay[i] = "��¥:" + "����";

								} else if (sDay[i].equals("2")) { // 2�̸� ��
									sDay[i] = "��¥:" + "��";

								}
							}
						}

						TextView rn = (TextView) findViewById(R.id.temper1);
						rn.setText(String.valueOf(sTemp[0]));
						Log.e("###", "aaa");
						TextView rn4 = (TextView) findViewById(R.id.humidity1);
						rn4.setText(String.valueOf(sReh[0]));

						TextView rn1 = (TextView) findViewById(R.id.windDirection1);
						rn1.setText(String.valueOf(sWdKor[0]));

						TextView rn5 = (TextView) findViewById(R.id.windSpeed1);
						double mydouble = Double.parseDouble(sWs[0]);
						String result = String.format("%.1f", mydouble);
						rn5.setText(String.valueOf(result));

						Calendar cal = Calendar.getInstance();
						int whatday = cal.get(Calendar.DAY_OF_MONTH);
						Log.e("###", "bbb");
						
						if (today != whatday) { // today != whatday
							temp_Max = -100.0;
							temp_Min = 100.0;
							hum_Max = 0;
							hum_Min = 100;
							Log.e("###", "ggg");
							today = whatday;

						} else {
							for (int k = 0; k < data; k++) { // array�� �Ǿ�������
								Log.e("###", "hhh");
								if (sDay[k].equals("0")) {
									double stemp1 = Double
											.parseDouble(sTemp[k]);
									int reh = Integer.parseInt(sReh[k]);
									if (stemp1 > temp_Max) {
										temp_Max = stemp1;
									}
									if (stemp1 < temp_Min) {
										temp_Min = stemp1;
									}
									if (reh > hum_Max) {
										hum_Max = reh;
										maxhumid.setText(String.valueOf(reh));
									}
									if (reh < hum_Min) {
										hum_Min = reh;
										minhumid.setText(String.valueOf(reh));
									}
								}
							}
						}
						double stemp3 = Double.parseDouble(smxTemp[0]);
						if (stemp3 < temp_Max) {
							String total1 = String.valueOf(temp_Max);
							maxtemp.setText(total1);
						} else {
							String total2 = String.valueOf(stemp3);
							maxtemp.setText(total2);
						}
						double stemp4 = Double.parseDouble(smnTemp[0]);
						if (stemp4 < temp_Min) {
							String total1 = String.valueOf(temp_Min);
							mintemp.setText(total1);
						} else {
							String total2 = String.valueOf(stemp4);
							mintemp.setText(total2);
						}
						Log.e("###", "iii");
						ImageView weatherinfo = (ImageView) findViewById(R.id.weatherInfo);
						if (sWfKor[0].equals("����")) {
							weatherinfo.setImageResource(R.drawable.nb01);
						}
						if (sWfKor[0].equals("���� ����")) {
							weatherinfo.setImageResource(R.drawable.nb02);
						}
						if (sWfKor[0].equals("���� ����")) {
							weatherinfo.setImageResource(R.drawable.nb03);
						}
						if (sWfKor[0].equals("�帲")) {
							weatherinfo.setImageResource(R.drawable.nb04);
						}
						if (sWfKor[0].equals("��")) {
							weatherinfo.setImageResource(R.drawable.nb08);
						}
						if (sWfKor[0].equals("��/��")) {
							weatherinfo.setImageResource(R.drawable.nb13);
						}
						if (sWfKor[0].equals("��")) {
							weatherinfo.setImageResource(R.drawable.nb11);
						}

						listadapter.setDay(sDay); // ���������� listview�� �ѷ�����
						listadapter.setTime(sHour);
						listadapter.setTemp(sTemp);
						listadapter.setWind(sWdKor);
						listadapter.setHum(sReh);
						listadapter.setWeather(sWfKor);
						updated = true; // ������ ������� flag�� true��
						listadapter.notifyDataSetChanged();
						tItem = false;
						data = 0; // ������ ������ ���������� �Ǹ� ó������ �����ؾ߰���?

					}

				}
			});
		}
	}

	/**
	 * �̰����� GPS����� �����ʵ��� ������ �ش�
	 * 
	 * @author Ans
	 */
	public void getMyLocation() {

		manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); // GPS
																				// ����
		long minTime = 60000; // every 60sec
		float minDistance = 1; // if moved over 1miter
		listener = new MyLocationListener(); // ������ ������
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime,
				minDistance, listener);// GPS
		manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				minTime, minDistance, listener);// ������

	}

	/**
	 * �̺κ��� �����ڵ��ؼ� �ļ��ϴ� ��
	 * 
	 * @author Administrator
	 * 
	 */
	class getaddress extends Thread {
		public void run() {
			try {

				sLong_name = new String[100];
				XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();
				xppf.setNamespaceAware(true);
				XmlPullParser xpp = xppf.newPullParser();

				String geocode = "http://maps.googleapis.com/maps/api/geocode/xml?latlng="
						+ latitude
						+ ","
						+ longitutde
						+ "&sensor=true&language=ko";
				URL url = new URL(geocode);
				// URL��ü����
				InputStream is = url.openStream();

				xpp.setInput(is, "UTF-8");
				int eventType = xpp.getEventType();
				geodata = 0;

				while (eventType != XmlPullParser.END_DOCUMENT) { // ������ ���� �ƴҶ�
					switch (eventType) {
					case XmlPullParser.START_TAG: // '<'�����±׸� ��������
						if (xpp.getName().equals("long_name")) { // �±׾��� �̸���
																	// ī�װ��ϋ�
																	// (�̰� �����̸���
																	// ���´�)
							bLong_name = true;

						}
						break;
					case XmlPullParser.TEXT: // �ؽ�Ʈ�� ��������
												// �ռ� �����±׿��� ���������� ������ �÷��׸�
												// true�� �ߴµ� ���⼭ �÷��׸� ����
												// ������ ������ �־��� �Ŀ� �÷��׸� false��~
						if (bLong_name) { // �����̸�
							sLong_name[geodata] = xpp.getText();
							bLong_name = false;
						}
						break;

					case XmlPullParser.END_TAG: // '</' �����±׸� ������ (�̺κ��� �߿�)
						if (xpp.getName().equals("GeocodeResponse")) { // �±װ�
																		// ������
																		// ������
																		// �±��̸���
																		// item�̸�(�̰�
																		// ����
																		// ������ ��

							showtext();
							break; // ���� �̶� ��� ������ ȭ�鿡 �ѷ��ָ� �ȴ�.
						}
						if (xpp.getName().equals("address_component")) { // data�±״�
																			// �����ð�����
																			// ����������
																			// �ϳ����̴�.
							geodata++; // �� data�±� == ���� ���� �׷��Ƿ� �̶� array�� ����������
						}
						break;
					}

					eventType = xpp.next();

					// TODO Auto-generated catch block
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void showtext() {
			handler2.post(new Runnable() {

				@Override
				public void run() {

					Toast.makeText(
							getApplicationContext(),
							"���� ��ġ�� " + sLong_name[3] + " " + sLong_name[2]
									+ " " + sLong_name[1], Toast.LENGTH_SHORT)
							.show();
					String sql = "select gugun_num, dong_num, dong_name, gridx, gridy, _id from t_dong where dong_name = "
							+ "'" + sLong_name[1] + "'";
					Cursor cur = db.rawQuery(sql, null);
					if (cur.getCount() != 0) {
						cur.moveToFirst();
						numDong = cur.getString(1);
						cur.close();
						network_thread thread = new network_thread(); // ���������(UI
																		// ���������
																		// system
																		// ���´�)
						thread.start(); // ������ ����
					}
				}
			});
		}
	}

	/**
	 * �̰����� ������ ���ǿ��� ������ �޾ƿ´�
	 * 
	 * @author Ans
	 * 
	 */
	class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {

			latitude = location.getLatitude();// get latitued
			longitutde = location.getLongitude();// get longitutde
			manager.removeUpdates(listener); // ���ǿ��� ���� ����(�̺κ��� �ּ�ó���ϸ� �����ѰŴ��
												// ��� �޾ƿ�)
			getaddress thread = new getaddress(); // �����ڵ��� ������

			thread.start(); // ������ ����
		}

		@Override
		public void onProviderDisabled(String provider) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}
	}

	/**
	 * 
	 * �̺κ��� ������ ����Ʈ�信 �ѷ��ִ� �����
	 * 
	 * @author Ans
	 * 
	 */
	class listWeatherView extends BaseAdapter {

		String[] day, time, temp, wind, hum, weather;
		Context mContext;
		String temp_data[] = new String[15]; // �ӽ÷� ������� nullpointexception ����

		public listWeatherView(Context context) {
			mContext = context;
		}

		public void setDay(String[] data) {
			day = data;
		}

		public void setTime(String[] data) {
			time = data;
		}

		public void setTemp(String[] data) {
			temp = data;
		}

		public void setWind(String[] data) {
			wind = data;
		}

		public void setHum(String[] data) {
			hum = data;
		}

		public void setWeather(String[] data) {
			weather = data;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub

			return temp_data.length; // ����Ʈ���� ����
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return temp_data[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parents) {

			showWeather layout = null;

			if (convertView != null) { // ��ũ�ѷ� �Ѿ �並 ������ �ʰ� ����
				layout = (showWeather) convertView;
			} else {
				layout = new showWeather(mContext.getApplicationContext()); // ���̾ƿ�
																			// ����

			}

			if (updated) { // ���������� �޾ƿ�����
				layout.setDate(day[position]); // ���̾ƿ����� �ѷ���
				layout.setTime(time[position]);
				layout.setTemp(temp[position]);
				layout.setWind(wind[position]);
				layout.setHum(hum[position]);
				layout.setWeather(weather[position]);

			}

			return layout;
		}
	}

	// ���û �������� ����
	private String loadKmaData() throws Exception {
		String page = "http://www.kma.go.kr/wid/queryDFS.jsp?gridx=57&gridy=125";
		URL url = new URL(page);
		HttpURLConnection urlConnection = (HttpURLConnection) url
				.openConnection();
		if (urlConnection == null)
			return null;
		urlConnection.setConnectTimeout(10000);// �ִ� 10�� ���
		urlConnection.setUseCaches(false);// �Ź� �������� �о����
		StringBuilder sb = new StringBuilder();// ��� ���ڿ� ����ü
		if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			System.out.println(urlConnection);
			InputStream inputStream = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(inputStream);
			// ���پ� �б�
			BufferedReader br = new BufferedReader(isr);
			while (true) {
				String line = br.readLine();// ���������� html �ڵ� �о����
				if (line == null)
					break;// ��Ʈ���� ������ null����
				sb.append(line + "\n");
			}// end while
			br.close();
			System.out.println(br);

		}// end if
		return sb.toString();
	}

	// ���û �������� ����
	private String wloadKmaData() throws Exception {
		String page = "http://www.kma.go.kr/weather/forecast/mid-term-xml.jsp?stnId=109";
		URL url = new URL(page);
		HttpURLConnection urlConnection = (HttpURLConnection) url
				.openConnection();
		if (urlConnection == null)
			return null;
		urlConnection.setConnectTimeout(10000);// �ִ� 10�� ���
		urlConnection.setUseCaches(false);// �Ź� �������� �о����
		StringBuilder sb = new StringBuilder();// ��� ���ڿ� ����ü
		if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			System.out.println(urlConnection);
			InputStream inputStream = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(inputStream);
			// ���پ� �б�
			BufferedReader br = new BufferedReader(isr);
			while (true) {
				String line = br.readLine();// ���������� html �ڵ� �о����
				if (line == null)
					break;// ��Ʈ���� ������ null����
				sb.append(line + "\n");
			}// end while
			br.close();
			System.out.println(br);

		}// end if
		return sb.toString();
	}

	public void sendSMS(String phoneNumber, String message) {
		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";
		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
				SENT), 0);
		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
				new Intent(DELIVERED), 0);
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.weather_board, menu);
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mUART_Monitor != null) {
			Log.i(TAG, "Stopping UART ..");
			mUART_Monitor.UART_stop();
			mUART_Monitor = null;
		}

		if (mUART_Device != null) {
			try {
				mUART_Device.UART_close();
			} catch (IOException e) {
				// Ignore.
			}
			mUART_Device = null;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mUART_Device = UART_USB_DEVICE_Probe.UART_DATA_ACQ(mUsbManager);
		Log.d(TAG, "Resumed, mUART_Device=" + mUART_Device);
		if (mUART_Device == null) {
			//Toast.makeText(getApplicationContext(), "�����Ⱑ ����Ǿ� ���� �ʽ��ϴ�.",
					//Toast.LENGTH_SHORT).show();
		} else {
			try {
				mUART_Device.UART_open();
				mUART_Device.UART_SETTING(9600, 8, 1, 0);

			} catch (IOException e) {
				Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
				Toast.makeText(getApplicationContext(), "�����ϴµ� ������ �߻��Ͽ����ϴ�.",
						Toast.LENGTH_SHORT).show();
				try {
					mUART_Device.UART_close();
				} catch (IOException e2) {
					// Ignore.
				}
				mUART_Device = null;
				return;
			}
			Toast.makeText(getApplicationContext(), "�����Ⱑ ����Ǿ����ϴ�.",
					Toast.LENGTH_SHORT).show();
		}

		if (mUART_Monitor != null) {
			Log.i(TAG, "Stopping UART ..");
			mUART_Monitor.UART_stop();
			mUART_Monitor = null;
		}

		if (mUART_Device != null) {
			Log.i(TAG, "Starting UART ..");
			mUART_Monitor = new UART_DATA_Monitoring(mUART_Device,
					mUART_Monitoring);
			mExecutor.submit(mUART_Monitor);
		}

	}

	public void show() {
		show_cnt++;
		if (show_cnt > 200)
			show_cnt = 200;
		TextView emergency = (TextView) findViewById(R.id.emerGency);
		if (tod_war == 0) {
			emergency.setTextColor(Color.rgb(255, 255, 255));
			emergy_str = "Ư��  ����  ����";
		} else {
			if (tod_war == 1) {
				emergency.setTextColor(Color.rgb(255, 255, 0));
			} else {
				emergency.setTextColor(Color.rgb(255, 0, 0));
			}
			if (tod_snow == 1) {
				emergy_str = "�� ��      �� ��\r\r\n���� " + tod_data + "mm";
			}
			if (tod_snow == 2) {
				emergy_str = "�� ��      �� ��\r\r\n���� " + tod_data + "mm";
			}
		}
		emergency.setText(emergy_str);
		// TextView rn = (TextView) findViewById(R.id.temper1);

		num = String.format("%3.1f", temp_dis);
		temp_dis = Double.parseDouble(num);
		/*
		 * if(show_cnt > 30){ if(temp_max <= temp_dis) temp_max = temp_dis;
		 * if(temp_min >= temp_dis) temp_min = temp_dis; } else{ temp_max =
		 * temp_dis; temp_min = temp_dis; }
		 */
		if (temp_dis <= 99.0 && temp_dis >= -60.0)
			// rn.setText(String.valueOf(sTemp));
			// String st = rn.toString();
			// System.out.println(sTemp + "TEMPsys");
			// Log.e("TEMP",st);

			// discomIndex = (double) Math.round(discomIndex * 10) / 10;
			// discom.setText(String.valueOf(discomIndex));

			// TextView rn1 = (TextView) findViewById(R.id.windDirection1);
			// if (wind_d_dis > 348.7 && wind_d_dis <= 11.2)
			// rn1.setText("��");
			// else if (wind_d_dis > 11.2 && wind_d_dis <= 33.7)
			// rn1.setText("�Ϻϵ�");
			// else if (wind_d_dis > 33.7 && wind_d_dis <= 56.2)
			// rn1.setText("�ϵ�");
			// else if (wind_d_dis > 56.2 && wind_d_dis <= 78.7)
			// rn1.setText("���ϵ�");
			// else if (wind_d_dis > 78.7 && wind_d_dis <= 101.2)
			// rn1.setText("��");
			// else if (wind_d_dis > 101.2 && wind_d_dis <= 123.7)
			// rn1.setText("������");
			// else if (wind_d_dis > 123.7 && wind_d_dis <= 146.2)
			// rn1.setText("����");
			// else if (wind_d_dis > 146.2 && wind_d_dis <= 168.7)
			// rn1.setText("������");
			// else if (wind_d_dis > 168.7 && wind_d_dis <= 191.2)
			// rn1.setText("��");
			// else if (wind_d_dis > 191.2 && wind_d_dis <= 213.7)
			// rn1.setText("������");
			// else if (wind_d_dis > 213.7 && wind_d_dis <= 236.2)
			// rn1.setText("����");
			// else if (wind_d_dis > 236.2 && wind_d_dis <= 258.7)
			// rn1.setText("������");
			// else if (wind_d_dis > 258.7 && wind_d_dis <= 281.2)
			// rn1.setText("��");
			// else if (wind_d_dis > 281.2 && wind_d_dis <= 303.7)
			// rn1.setText("���ϼ�");
			// else if (wind_d_dis > 303.7 && wind_d_dis <= 326.7)
			// rn1.setText("�ϼ�");
			// else if (wind_d_dis > 326.2 && wind_d_dis <= 348.7)
			// rn1.setText("�Ϻϼ�");
			// else
			// rn1.setText("��")
			;
		TextView rn2 = (TextView) findViewById(R.id.precipitation1);
		num = String.format("%3.1f", rfallhours_dis);
		rfallhours_dis = Double.parseDouble(num);
		rn2.setText(String.valueOf(rfallhours_dis));

		TextView rn3 = (TextView) findViewById(R.id.todayPrecipitation1);
		// Random rand3 = new Random();
		// rfall = rand3.nextInt(50);
		// rfall = 10.1; // ���� ����
		num = String.format("%3.1f", rfalltoday_dis);
		rfalltoday_dis = Double.parseDouble(num);
		rn3.setText(String.valueOf(rfalltoday_dis));

		// TextView rn4 = (TextView) findViewById(R.id.humidity1);
		// Random rand4 = new Random();
		// humi = 60.0;
		num = String.format("%3.1f", humi_dis);
		humi_dis = Double.parseDouble(num);
		if (show_cnt > 30) {
			if (humi_max <= humi_dis)
				humi_max = humi_dis;
			if (humi_min >= humi_dis)
				humi_min = humi_dis;
		} else {
			humi_max = humi_dis;
			humi_min = humi_dis;
		}
		if (humi_dis <= 99.0 && humi_dis >= 0.0)
			// rn4.setText(String.valueOf(humi_dis));
			// Log.e("@@@","333");
			// TextView rn5 = (TextView) findViewById(R.id.windSpeed1);
			// Random rand5 = new Random();
			// wind_s = rand5.nextInt(50);
			// wind_s = 12.3;
			num = String.format("%3.1f", wind_s_dis);
		wind_s_dis = Double.parseDouble(num);
		// rn5.setText(String.valueOf(wind_s_dis));

		TextView rn6 = (TextView) findViewById(R.id.atmosphericPressure1);
		// Random rand6 = new Random();
		// pickedNumber6 = rand6.nextInt(50);
		// rfallh = 34.5; // �ð� ����
		// rfallhours = 32.1;
		num = String.format("%3.1f", rfallyester_dis);
		rfallyester_dis = Double.parseDouble(num);
		rn6.setText(String.valueOf(rfallyester_dis));

		TextView rn7 = (TextView) findViewById(R.id.yesterdayPrecipitation1);
		// Random rand7 = new Random();
		// pickedNumber7 = rand7.nextInt(50);
		// rfally = 45.6; //���� ����
		// num = String.format("%4d", rfallmonth_dis);
		// rfallmonth_dis = Double.parseDouble(num);

		// TextView maxtemp = (TextView) findViewById(R.id.maxTemp);
		// double results0 = 0.0;
		// String stringdouble = Double.toString(temp_max);
		// maxtemp.setText(stringdouble);

		// TextView mintemp = (TextView) findViewById(R.id.minTemp);
		// double results1 = 0.0;
		// String stringdouble1 = Double.toString(temp_min);
		// mintemp.setText(stringdouble1);

		// TextView maxhumid = (TextView) findViewById(R.id.maxHumid);
		// double results2 = 0.0;
		// String stringdouble2 = Double.toString(humi_max);
		// maxhumid.setText(stringdouble2);

		// TextView minhumid = (TextView) findViewById(R.id.minHumid);
		// double results3 = 0.0;
		// String stringdouble3 = Double.toString(humi_min);
		// minhumid.setText(stringdouble3);
		// if(minutes == 0 || minutes == 20 || minutes == 29){
		tv = (TextView) findViewById(R.id.editText);
		tv.setSelected(true);
		tv3 = (TextView) findViewById(R.id.editText3);
		tv3.setSelected(true);
		result = "���� ���� ��� ��";
		result3 = "���� ���� ��� ��";

		// }
		dis_tog++;
		if (start_flew == 0) {

			flow_timer = 15;
			// start_flew = 1;
		} else {
			flow_timer = 600;
		}
		flow_timer = dis_tog;
		if (dis_tog >= flow_timer) {
			dis_tog = 0;
			try {
				String html = loadKmaData();
				String whtml = wloadKmaData();
				result = "";
				result2 = "";
				result3 = "";
				result4 = "";
				// setMarqueeSpeed(tv,1,1);
				// setMarqueeSpeed(tv3,1,1);
				// DOM �Ľ�.
				ByteArrayInputStream bai = new ByteArrayInputStream(
						html.getBytes());
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				// dbf.setIgnoringElementContentWhitespace(true);//ȭ��Ʈ�����̽� ����
				DocumentBuilder builder = dbf.newDocumentBuilder();
				Document parse = builder.parse(bai);// DOM �ļ�
				// �±� �˻�
				NodeList datas = parse.getElementsByTagName("data");
				// String result = "data�±� �� =" + datas.getLength()+"\n";

				// 17���� data�±׸� ������ ����

				ByteArrayInputStream wbai = new ByteArrayInputStream(
						whtml.getBytes());
				DocumentBuilderFactory wdbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder wbuilder = wdbf.newDocumentBuilder();
				Document wparse = builder.parse(wbai);// DOM �ļ�
				String xpath = "//wid/body/location[@city='11B20201']";
				NodeList wdatas = parse.getElementsByTagName(xpath);

				int ttttt = 0;
				sky_state = 100;
				pty_state = 100;

				for (int idx = 0; idx < datas.getLength(); idx++) {

					// �ʿ��� �������� ���� ���� ����
					String day = "";
					String hour = "";
					String sky = "";
					String temp = "";

					int pop = 0;
					// toom_h = 5;

					Node node = datas.item(idx);// data �±� ����
					int childLength = node.getChildNodes().getLength();
					// �ڽ��±� ��� ����
					NodeList childNodes = node.getChildNodes();
					for (int childIdx = 0; childIdx < childLength; childIdx++) {
						Node childNode = childNodes.item(childIdx);
						int count = 0;
						if (childNode.getNodeType() == Node.ELEMENT_NODE) {
							count++;
							// �±��� ��츸 ó��

							// ����,����,�� ����(�ð����� ����)
							if (childNode.getNodeName().equals("day")) {
								int su = Integer.parseInt(childNode
										.getFirstChild().getNodeValue());

								start_flew = 1;
								switch (su) {
								case 0:
									day = "�ٿ���";
									toom = 0;
									break;
								case 1:
									day = "�ڳ���";
									toom = 1;
									break;
								case 2:
									day = "�ٸ�";
									toom = 2;
									break;
								default:
									break;
								}
							} else if (childNode.getNodeName().equals("sky")) {
								if (sky_state == 100) {
									sky_state = Integer.parseInt(childNode
											.getFirstChild().getNodeValue());
								}
								// index = sky_state;

								//
							} else if (childNode.getNodeName().equals("pty")) {
								if (pty_state == 100) {
									pty_state = Integer.parseInt(childNode
											.getFirstChild().getNodeValue());
								}
								// index = sky_state;

								//
							} else if (childNode.getNodeName().equals("hour")) {

								int t_hour = Integer.parseInt(childNode
										.getFirstChild().getNodeValue());
								switch (t_hour) {
								case 0:
									hour = "00";
									break;
								case 1:
									hour = "01";
									break;
								case 2:
									hour = "02";
									break;
								case 3:
									hour = "03";
									break;
								case 4:
									hour = "04";
									break;
								case 5:
									hour = "05";
									break;
								case 6:
									hour = "06";
									break;
								case 7:
									hour = "07";
									break;
								case 8:
									hour = "08";
									break;
								case 9:
									hour = "09";
									break;
								case 10:
									hour = "10";
									break;
								case 11:
									hour = "11";
									break;
								case 12:
									hour = "12";
									break;
								case 13:
									hour = "13";
									break;
								case 14:
									hour = "14";
									break;
								case 15:
									hour = "15";
									break;
								case 16:
									hour = "16";
									break;
								case 17:
									hour = "17";
									break;
								case 18:
									hour = "18";
									break;
								case 19:
									hour = "19";
									break;
								case 20:
									hour = "20";
									break;
								case 21:
									hour = "21";
									break;
								case 22:
									hour = "22";
									break;
								case 23:
									hour = "23";
									break;
								case 24:
									hour = "24";
									break;
								default:
									hour = "24";
									break;
								}

								// hour =
								// childNode.getFirstChild().getNodeValue();
								if (toom == 1) {
									if (t_hour == 9)
										toom_h = idx;
									// toom_h = 5;
								}
								if (toom == 0) {
									if (t_hour == 15)
										toom_t = idx;
									// toom_h = 5;
								}
								// �ϴû����ڵ� �м�
							} else if (childNode.getNodeName().equals("wfKor")) {
								sky = childNode.getFirstChild().getNodeValue();

							} else if (childNode.getNodeName().equals("temp")) {
								temp = childNode.getFirstChild().getNodeValue();
							} else if (childNode.getNodeName().equals("tmx")) {
								float temp_vmax = Float.parseFloat(childNode
										.getFirstChild().getNodeValue());
								tod_str = String.format("%3.1f", temp_vmax);
								double temp_tmax = Double.parseDouble(tod_str);
								// tod_str = String.format("%3.1f",tod_data);
								if (toom == 0) {
									if (temp_max < temp_tmax)
										temp_max = temp_tmax;
								}
								// temp_max = String.format("%3.1f", r12);

								// temp_dis = Double.parseDouble(num);
							} else if (childNode.getNodeName().equals("tmn")) {
								float temp_vmin = Float.parseFloat(childNode
										.getFirstChild().getNodeValue());
								tod_str = String.format("%3.1f", temp_vmin);
								double temp_tmin = Double.parseDouble(tod_str);
								// tod_str = String.format("%3.1f",tod_data);
								if (toom == 1) {
									if (temp_min > temp_tmin)
										temp_min = temp_tmin;
								}
								// temp_max = String.format("%3.1f", r12);

								// temp_dis = Double.parseDouble(num);
							} else if (childNode.getNodeName().equals("pop")) {
								pop = Integer.parseInt(childNode
										.getFirstChild().getNodeValue());

							} else if (childNode.getNodeName().equals("r12")) {
								// r12 =
								// Integer.parseInt(childNode.getFirstChild().getNodeValue());
								r12 = Float.parseFloat(childNode
										.getFirstChild().getNodeValue());
								if (toom == 0) {
									if (r12 == 0.0) {
										tod_war = 0;
										tod_snow = 0;
									} else if (r12 < 50.0) {
										tod_war = 1;
										tod_snow = 1;
										tod_str = String.format("%3.1f", r12);
										tod_data = Double.parseDouble(tod_str);
										tod_str = String.format("%3.1f",
												tod_data);
									} else {
										tod_war = 2;
										tod_snow = 1;
										tod_str = String.format("%3.1f", r12);
										tod_data = Double.parseDouble(tod_str);
										tod_str = String.format("%3.1f",
												tod_data);

									}
								}
								if (toom == 1) {
									if (r12 == 0.0)
										tom_war = 0;
									else if (r12 < 50.0)
										tom_war = 1;
									else
										tom_war = 2;
								}

								num_r12 = String.format("%3.1f", r12);

								// temp_dis = Double.parseDouble(num);
							} else if (childNode.getNodeName().equals("s12")) {
								// r12 =
								// Integer.parseInt(childNode.getFirstChild().getNodeValue());
								s12 = Float.parseFloat(childNode
										.getFirstChild().getNodeValue());
								if (toom == 0) {
									if (s12 == 0.0) {
										if (tod_war == 0) {
											tod_war = 0;
										}
									} else if (s12 < 50.0) {
										if (tod_war < 1) {
											tod_war = 1;
											tod_snow = 2;
											tod_str = String.format("%3.1f",
													r12);
											tod_data = Double
													.parseDouble(tod_str);
											tod_str = String.format("%3.1f",
													tod_data);

										}
									} else {
										if (tod_war < 2) {
											tod_war = 2;
											tod_snow = 2;
											tod_str = String.format("%3.1f",
													r12);
											tod_data = Double
													.parseDouble(tod_str);
											tod_str = String.format("%3.1f",
													tod_data);
										}
									}
								}
								if (toom == 1) {
									if (s12 == 0.0) {
										if (tom_war == 0)
											tom_war = 0;
									} else if (s12 < 50.0) {
										if (tom_war < 1)
											tom_war = 1;
									} else {
										if (tom_war < 2)
											tom_war = 2;
									}
								}
								num_s12 = String.format("%3.1f", s12);
							}

							else if (childNode.getNodeName().equals("r06")) {
								// r12 =
								// Integer.parseInt(childNode.getFirstChild().getNodeValue());
								r06 = Float.parseFloat(childNode
										.getFirstChild().getNodeValue());
								if (toom == 0) {
									if (r06 == 0.0) {
										tod_war = 0;
										tod_snow = 0;
									} else if (r06 < 25.0) {
										tod_war = 1;
										tod_snow = 1;
										tod_str = String.format("%3.1f", r06);
										tod_data = Double.parseDouble(tod_str);
										tod_str = String.format("%3.1f",
												tod_data);
									} else {
										tod_war = 2;
										tod_snow = 1;
										tod_str = String.format("%3.1f", r06);
										tod_data = Double.parseDouble(tod_str);
										tod_str = String.format("%3.1f",
												tod_data);

									}
								}
								if (toom == 1) {
									if (r06 == 0.0)
										tom_war = 0;
									else if (r06 < 25.0)
										tom_war = 1;
									else
										tom_war = 2;
								}

								num_r06 = String.format("%3.1f", r06);

								// temp_dis = Double.parseDouble(num);
							} else if (childNode.getNodeName().equals("s06")) {
								// r12 =
								// Integer.parseInt(childNode.getFirstChild().getNodeValue());
								s06 = Float.parseFloat(childNode
										.getFirstChild().getNodeValue());
								if (toom == 0) {
									if (s06 == 0.0) {
										if (tod_war == 0) {
											tod_war = 0;
										}
									} else if (s06 < 25.0) {
										if (tod_war < 1) {
											tod_war = 1;
											tod_snow = 2;
											tod_str = String.format("%3.1f",
													r06); // ������ ���� Ȯ�� �ʿ�, �켱
															// ������ ����Ÿ ����
											tod_data = Double
													.parseDouble(tod_str);
											tod_str = String.format("%3.1f",
													tod_data);

										}
									} else {
										if (tod_war < 2) {
											tod_war = 2;
											tod_snow = 2;
											tod_str = String.format("%3.1f",
													r06);
											tod_data = Double
													.parseDouble(tod_str);
											tod_str = String.format("%3.1f",
													tod_data);
										}
									}
								}
								if (toom == 1) {
									if (s06 == 0.0) {
										if (tom_war == 0)
											tom_war = 0;
									} else if (s06 < 25.0) {
										if (tom_war < 1)
											tom_war = 1;
									} else {
										if (tom_war < 2)
											tom_war = 2;
									}
								}
								num_s06 = String.format("%3.1f", s06);
							}

							else if (childNode.getNodeName().equals("ws")) {
								// r12 =
								// Integer.parseInt(childNode.getFirstChild().getNodeValue());
								ws = Float.parseFloat(childNode.getFirstChild()
										.getNodeValue());

								num_ws = String.format("%3.1f", ws);
							}
							/*
							 * else if (childNode.getNodeName().equals("s12")) {
							 * s12 = Integer.parseInt(childNode.getFirstChild().
							 * getNodeValue()); }
							 */

						}
					}// end ���� for��
					if (ttttt == 0) {
						if (r12 > 0.0)
							result += day + " " + hour + "�� ����  - " + sky
									+ ", " + temp + "��, ǳ�� " + num_ws
									+ ", ����Ȯ�� " + pop + "%, 6�ð� ���� ������ "
									+ num_r12 + "mm\n";
						else if (s12 > 0.0)
							result += day + " " + hour + "�� ���� - " + sky + ", "
									+ temp + "��, ǳ�� " + num_ws + ", ����Ȯ�� "
									+ pop + "%, 12�ð� ���� ������ " + num_r12
									+ "mm\n";

						else if (r06 > 0.0)
							result += day + " " + hour + "�� ���� - " + sky + ", "
									+ temp + "��, ǳ�� " + num_ws + ", ����Ȯ�� "
									+ pop + "%, 6�ð� ���� ������ " + num_r06 + "mm\n";

						else if (s06 > 0.0)
							result += day + " " + hour + "�� ���� - " + sky + ", "
									+ temp + "��, ǳ�� " + num_ws + ", ����Ȯ�� "
									+ pop + "%, 6�ð� ���� ������ " + num_r06 + "mm\n";

						else
							result += day + " " + hour + "��  ���� - " + sky
									+ ", " + temp + "��, ǳ�� " + num_ws
									+ ", ����Ȯ�� " + pop
									+ "%, 6~12�ð� ���� ������ 0mm\n";
					}
					/*
					 * if (ttttt == 0) { if (r12 > 0.0) result += day + " " +
					 * hour + "�� ����  -" + sky + "," + temp + "��, ǳ�� " + num_ws +
					 * ", ����Ȯ�� " + pop + "%, 12�ð� ���� ������ " + num_r12 + "ml\n";
					 * else if (s12 > 0.0) result += day + " " + hour + "�� ���� -"
					 * + sky + "," + temp + "��, ǳ�� " + num_ws + ", ����Ȯ�� " + pop
					 * + "%, 12�ð� ���� ������ " + num_r12 + "mm\n"; else result +=
					 * day + " " + hour + "��  ���� -" + sky + "," + temp +
					 * "��, ǳ�� " + num_ws + ", ����Ȯ�� " + pop +
					 * "%, 12�ð� ���� ������ 0ml\n"; }
					 */
					else if (ttttt == 1) {
						if (r12 > 0.0)
							result2 += day + " " + hour + "�� ����  - " + sky
									+ ", " + temp + "��, ǳ�� " + num_ws
									+ ", ����Ȯ�� " + pop + "%, 12�ð� ���� ������ "
									+ num_r12 + "mm\n";
						else if (s12 > 0.0)
							result2 += day + " " + hour + "�� ���� - " + sky
									+ ", " + temp + "��, ǳ�� " + num_ws
									+ ", ����Ȯ��  " + pop + "%, 12�ð� ���� ������ "
									+ num_r12 + "mm\n";
						else if (r06 > 0.0)
							result2 += day + " " + hour + "�� ���� - " + sky
									+ ", " + temp + "��, ǳ�� " + num_ws
									+ ", ����Ȯ��  " + pop + "%, 6�ð� ���� ������ "
									+ num_r06 + "mm\n";

						else if (s06 > 0.0)
							result2 += day + " " + hour + "�� ���� - " + sky
									+ ", " + temp + "��, ǳ�� " + num_ws
									+ ", ����Ȯ��  " + pop + "%, 6�ð� ���� ������ "
									+ num_r06 + "mm\n";

						else
							result2 += day + " " + hour + "��  ���� - " + sky
									+ ", " + temp + "��, ǳ�� " + num_ws
									+ ", ����Ȯ�� " + pop
									+ "%, 6~12�ð� ���� ������ 0mm\n";
					} else if (ttttt == toom_h) {
						if (r12 > 0.0)
							result3 += day + " " + hour + "�� ����  - " + sky
									+ ", " + temp + "��, ǳ�� " + num_ws
									+ ", ����Ȯ�� " + pop + "%, 12�ð� ���� ������ "
									+ num_r12 + "mm\n";
						else if (s12 > 0.0)
							result3 += day + " " + hour + "�� ���� - " + sky
									+ ", " + temp + "��, ǳ�� " + num_ws
									+ ", ����Ȯ�� " + pop + "%, 12�ð� ���� ������ "
									+ num_r12 + "mm\n";
						else if (r06 > 0.0)
							result3 += day + " " + hour + "�� ���� - " + sky
									+ ", " + temp + "��, ǳ�� " + num_ws
									+ ", ����Ȯ�� " + pop + "%, 6�ð� ���� ������ "
									+ num_r06 + "mm\n";

						else if (s12 > 0.0)
							result3 += day + " " + hour + "�� ���� - " + sky
									+ ", " + temp + "��, ǳ�� " + num_ws
									+ ", ����Ȯ�� " + pop + "%, 6�ð� ���� ������ "
									+ num_r06 + "mm\n";

						else
							result3 += day + " " + hour + "��  ���� - " + sky
									+ ", " + temp + "��, ǳ�� " + num_ws
									+ ", ����Ȯ�� " + pop
									+ "%, 6~12�ð� ���� ������ 0mm\n";
					} else if (ttttt == toom_h + 1) {
						if (r12 > 0.0)
							result4 += day + " " + hour + "�� ����  - " + sky
									+ ", " + temp + "��, ǳ�� " + num_ws
									+ ", ����Ȯ�� " + pop + "%, 12�ð� ���� ������ "
									+ num_r12 + "mm\n";
						else if (s12 > 0.0)
							result4 += day + " " + hour + "�� ���� - " + sky
									+ ", " + temp + "��, ǳ�� " + num_ws
									+ ", ����Ȯ�� " + pop + "%, 12�ð� ���� ������ "
									+ num_r12 + "mm\n";

						else if (r06 > 0.0)
							result4 += day + " " + hour + "�� ���� - " + sky
									+ ", " + temp + "��, ǳ�� " + num_ws
									+ ", ����Ȯ�� " + pop + "%, 6�ð� ���� ������ "
									+ num_r06 + "mm\n";

						else if (s06 > 0.0)
							result4 += day + " " + hour + "�� ���� - " + sky
									+ ", " + temp + "��, ǳ�� " + num_ws
									+ ", ����Ȯ�� " + pop + "%, 6�ð� ���� ������ "
									+ num_r06 + "mm\n";

						else
							result4 += day + " " + hour + "��  ���� - " + sky
									+ ", " + temp + "��, ǳ�� " + num_ws
									+ ", ����Ȯ�� " + pop
									+ "%, 6~12�ð� ���� ������ 0mm\n";
					}

					ttttt++;

					// if(ttttt == 2){
					// result += "\n\n";
					// ttttt = 0;
					// }
				}// end �ٱ��� for��

				result += result2;
				// result = "test";
				result3 += result4;
				// tv.setTextColor(Color.rgb(204, 255, 0));

				// if(minutes == 0 || minutes == 20 || minutes == 29){
				// tv.setSelected(true);
				if (tod_war == 0)
					tv.setTextColor(Color.rgb(255, 255, 255));
				else if (tod_war == 1)
					tv.setTextColor(Color.rgb(255, 255, 0));
				else
					tv.setTextColor(Color.rgb(255, 0, 0));
				// setMarqueeSpeed(tv,10,1);
				tv.setText(result);
				Log.e("RESULT", result);
				// setMarqueeSpeed(tv,10,1);
				tv.setSelected(true);
				// setMarqueeSpeed(tv,10,1);
				// tv2.setText(result2);
				// tv3.setTextColor(Color.rgb(204, 255, 0));
				// tv3.setSelected(true);
				if (tom_war == 0)
					tv3.setTextColor(Color.rgb(255, 255, 255));
				else if (tom_war == 1)
					tv3.setTextColor(Color.rgb(255, 255, 0));
				else
					tv3.setTextColor(Color.rgb(255, 0, 0));
				// setMarqueeSpeed(tv3,10,1);
				tv3.setText(result3);
				// setMarqueeSpeed(tv3,10,1);
				tv3.setSelected(true);
				// setMarqueeSpeed(tv3,10,1);
				// }
				// img.setBackgroundResource(R.drawable.ani3);
				// mAnimationDrawable_1 =
				// (AnimationDrawable)imgView_1.getBackground();

				// img.setBackgroundDrawable(getResources().getDrawable(images[sky_state]));

				if (index >= 20)
					index = 0;

				w_kma = 1;

			} catch (Exception e) {
				tv.setText(result);
				// tv.setText("����" + e.getMessage());
				e.printStackTrace();
				if (w_kma == 1) {
					try {
						Process proc = Runtime.getRuntime().exec(
								new String[] { "su", "-c", "reboot" });
						proc.waitFor();
					} catch (Exception ex) {
						Log.i("Reboot Code", "Could not reboot", ex);
					}
				}
			}
		}

		if ((seconds % 10) % 10 < 5) {
			num = String.format("%c%4d", '��', rfallmonth_dis);
			rn7.setText(num);
		} else {
			num = String.format("%c%4d", '��', rfallyear_dis);
			rn7.setText(num);
		}
	}

	public void doWork() {
		runOnUiThread(new Runnable() {
			public void run() {
				try {
					Calendar c = Calendar.getInstance();
					System.out.println("Current time => " + c.getTime());

					SimpleDateFormat df = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					String formattedDate = df.format(c.getTime());
					// formattedDate have current date/time
					// Toast.makeText(WeatherBoard.this, formattedDate,
					// Toast.LENGTH_SHORT).show();

					// Now we display formattedDate value in TextView
					TextView txtView = (TextView) findViewById(R.id.currentTime);
					txtView.setText(formattedDate);
				} catch (Exception e) {
				}
			}
		});
	}

	class CountDownRunner implements Runnable {
		// @Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					doWork();
					Thread.sleep(1000); // Pause of 1 Second
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
				}
			}
		}
	}

	private void UART_Received_DATA(byte[] data) {
		int r_c, recv_length = 0;

		// final TextView rand = (TextView)
		// findViewById(R.id.enternumberofdice);
		recv_length = data.length;
		// wave = (TextView) findViewById(R.id.uv_number);
		// wave.setText(String.valueOf(recv_length));

		for (r_c = 0; r_c < recv_length; r_c++) {
			recv_data1[recv_cnt] = data[r_c];
			if ((data[r_c] & 0xFF) == 0x29) {
				// Log.d("UART","CHK-->",recv_data1[recv_cnt - 20]);

				if (r_c > 0) {
					if ((data[r_c - 1] & 0xFF) == 0x28) {
						if (recv_cnt > 23) {

							if ((recv_data1[recv_cnt - 2] & 0xFF) == (((recv_data1[recv_cnt - 23] & 0xFF)
									+ (recv_data1[recv_cnt - 22] & 0xFF)
									+ (recv_data1[recv_cnt - 21] & 0xFF)
									+ (recv_data1[recv_cnt - 20] & 0xFF)
									+ (recv_data1[recv_cnt - 19] & 0xFF)
									+ (recv_data1[recv_cnt - 18] & 0xFF)
									+ (recv_data1[recv_cnt - 17] & 0xFF)
									+ (recv_data1[recv_cnt - 16] & 0xFF)
									+ (recv_data1[recv_cnt - 15] & 0xFF)
									+ (recv_data1[recv_cnt - 14] & 0xFF)
									+ (recv_data1[recv_cnt - 13] & 0xFF)
									+ (recv_data1[recv_cnt - 12] & 0xFF)
									+ (recv_data1[recv_cnt - 11] & 0xFF)
									+ (recv_data1[recv_cnt - 10] & 0xFF)
									+ (recv_data1[recv_cnt - 9] & 0xFF)
									+ (recv_data1[recv_cnt - 8] & 0xFF)
									+ (recv_data1[recv_cnt - 7] & 0xFF)
									+ (recv_data1[recv_cnt - 6] & 0xFF)
									+ (recv_data1[recv_cnt - 5] & 0xFF)
									+ (recv_data1[recv_cnt - 4] & 0xFF) + (recv_data1[recv_cnt - 3] & 0xFF)) & 0xFF)) {
								recv_flag = 1;
								recv_tot = recv_cnt;
							} else
								recv_flag = 0;

							// recv_flag = 1;
							// recv_tot = recv_cnt;

						} else
							recv_flag = 0;
					}
				} else {
					if (recv_cnt == 0) {
						recv_cnt = 0;
						recv_tot = 0;
					} else {
						if (recv_cnt > 23) {
							if ((recv_data1[recv_cnt - 1] & 0xFF) == 0x28) {

								if ((recv_data1[recv_cnt - 2] & 0xFF) == (((recv_data1[recv_cnt - 23] & 0xFF)
										+ (recv_data1[recv_cnt - 22] & 0xFF)
										+ (recv_data1[recv_cnt - 21] & 0xFF)
										+ (recv_data1[recv_cnt - 20] & 0xFF)
										+ (recv_data1[recv_cnt - 19] & 0xFF)
										+ (recv_data1[recv_cnt - 18] & 0xFF)
										+ (recv_data1[recv_cnt - 17] & 0xFF)
										+ (recv_data1[recv_cnt - 16] & 0xFF)
										+ (recv_data1[recv_cnt - 15] & 0xFF)
										+ (recv_data1[recv_cnt - 14] & 0xFF)
										+ (recv_data1[recv_cnt - 13] & 0xFF)
										+ (recv_data1[recv_cnt - 12] & 0xFF)
										+ (recv_data1[recv_cnt - 11] & 0xFF)
										+ (recv_data1[recv_cnt - 10] & 0xFF)
										+ (recv_data1[recv_cnt - 9] & 0xFF)
										+ (recv_data1[recv_cnt - 8] & 0xFF)
										+ (recv_data1[recv_cnt - 7] & 0xFF)
										+ (recv_data1[recv_cnt - 6] & 0xFF)
										+ (recv_data1[recv_cnt - 5] & 0xFF)
										+ (recv_data1[recv_cnt - 4] & 0xFF) + (recv_data1[recv_cnt - 3] & 0xFF)) & 0xFF)) {
									recv_flag = 1;
									uart_ok = 1;
									recv_tot = recv_cnt;
								} else
									recv_flag = 0;

								// recv_flag = 1;
								// recv_tot = recv_cnt;
							}

						} else
							recv_flag = 0;
					}
				}
			}
			recv_cnt++;
			if (recv_cnt > 200) {
				recv_cnt = 0;
			}
		}

		if (recv_flag == 1) {
			r_timer = 0;
			rcv_timer++;
			if (recv_data1[recv_tot - 23] == 0x2B) {
				tempr = (recv_data1[recv_tot - 22] - 0x30) * 100
						+ (recv_data1[recv_tot - 21] - 0x30) * 10
						+ (recv_data1[recv_tot - 20] - 0x30);
			} else {
				tempr = (recv_data1[recv_tot - 22] - 0x30) * 100
						+ (recv_data1[recv_tot - 21] - 0x30) * 10
						+ (recv_data1[recv_tot - 20] - 0x30);
				tempr = -1.0 * tempr;
			}

			humir = ((recv_data1[recv_tot - 19] - 0x30) * 100
					+ (recv_data1[recv_tot - 18] - 0x30) * 10 + (recv_data1[recv_tot - 17] - 0x30));
			windr_s = ((recv_data1[recv_tot - 16] - 0x30) * 100
					+ (recv_data1[recv_tot - 15] - 0x30) * 10 + (recv_data1[recv_tot - 14] - 0x30));
			windr_d = ((recv_data1[recv_tot - 13] - 0x30) * 1000
					+ (recv_data1[recv_tot - 12] - 0x30) * 100
					+ (recv_data1[recv_tot - 11] - 0x30) * 10 + (recv_data1[recv_tot - 10] - 0x30));
			rstat = (recv_data1[recv_tot - 9] - 0x30);
			if (rstat == 0)
				rstat = 1;
			else
				rstat = 0;
			rfallr = ((recv_data1[recv_tot - 8] - 0x30) * 100
					+ (recv_data1[recv_tot - 7] - 0x30) * 10 + (recv_data1[recv_tot - 6] - 0x30));
			rfall_change = ((recv_data1[recv_tot - 5] - 0x30) * 100
					+ (recv_data1[recv_tot - 4] - 0x30) * 10 + (recv_data1[recv_tot - 3] - 0x30));

			temps = temps + tempr;
			humis = humis + humir;
			winds_s = winds_s + windr_s;
			winds_d = winds_d + windr_d;
			rfalls = rfallr;
			Log.d("UART", "RECV_COUNT -->" + rcv_timer);
			recv_cnt = 0;
			recv_ok = 1;
			recv_flag = 0;
		}

	}

	protected void setMarqueeSpeed(TextView tv_s, float speed,
			int speedIsMultiplier) {

		try {
			Field f = tv_s.getClass().getDeclaredField("mMarquee");
			f.setAccessible(true);
			Object marquee = f.get(tv_s);
			if (marquee != null) {
				Field mf = marquee.getClass().getDeclaredField("mScrollUnit");
				mf.setAccessible(true);
				float newSpeed = speed;
				if (speedIsMultiplier == 1) {
					newSpeed = mf.getFloat(marquee) * speed;
				}
				mf.setFloat(marquee, newSpeed);
				Log.i(this.getClass().getSimpleName(), String.format(
						"%s marquee speed set to %f", tv_s, newSpeed));
			}
		} catch (Exception e) {
			// ignore, not implemented in current API level
		}
	}

	@Override
	protected void onDestroy() {
		Log.i("test", "onDstory()");
		// mHandler.removeCallbacks(mRunnable);
		super.onDestroy();
	}
}
