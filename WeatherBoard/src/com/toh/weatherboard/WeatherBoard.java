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
	listWeatherView listadapter; // 날씨정보를 뿌려주는 리스트뷰용 어댑터
	ArrayAdapter<String> sidoAdapter; // 시도 정보를 뿌려주는 스피너용 어댑터
	ArrayAdapter<String> gugunAdapter; // 구군 정보를 뿌려주는 스피너용 어댑터
	ArrayAdapter<String> dongAdapter; // 동면 정보를 뿌려주는 스피너용 어댑터
	Spinner sidoSpinner; // 시도스피너
	Spinner gugunSpinner; // 구군스피너
	Spinner dongSpinner; // 동면스피너
	Button getBtn; // 날씨 가져오는 버튼
	Button gpsBtn;
	TextView text; // 날씨지역및 발표시각정보
	TextView maxtemp, mintemp, maxhumid, minhumid;
	ListView listView1; // 날씨정보를 뿌려줄 리스트뷰

	String tempDong = "4215025000"; // 기본dongcode
	String sCategory; // 동네
	String sTm; // 발표시각
	String[] sHour; // 예보시간(총 15개정도 받아옴 3일*5번)
	String[] sDay; // 날짜(몇번째날??)
	String[] sTemp; // 현재온도
	String[] sWdKor; // 풍향
	String[] sReh; // 습도
	String[] sWfKor; // 날씨
	String[] sWs; // 날씨
	String[] smxTemp; // 최고온도
	String[] smnTemp; // 최저온도
	String[] fDay; // 최저온도
	// DB용 변수
	String[] sidonum; // 시도 코드
	String[] Nsidonum; // 이건 구군table에서 가져오는 코드
	String[] sidoname; // 시도 이름
	String[] gugunnum; // 구군 코드
	String[] Ngugunnum;// 동네 table에서 가져온 구군 코드
	String[] gugunname;// 구군 이름
	String[] dongnum; // 동 코드
	String[] dongname; // 동 이름
	String[] gridx; // x좌표
	String[] gridy; // y좌표
	String[] id; // id
	String[] sLong_name; // gps로 지오코딩후 주소를 파서해서 저장할 변수

	double latitude, longitutde; // 위도와 경도를 저장할 변수
	double temp_Max;
	double temp_Min;
	int hum_Max;
	int hum_Min;
	static SQLiteDatabase db; // 디비

	int data = 0; // 이건 파싱해서 array로 넣을때 번지
	int geodata = 0; // 지오코딩용 파서 array 번지
	boolean updated; // 이건 날씨정보 뿌리기위한 플래그
	boolean bCategory; // 여긴 저장을 위한 플래그들
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
	boolean tCategory; // 이건 text로 뿌리기위한 플래그
	boolean tTm;
	boolean tItem;

	Handler handler; // 핸들러
	Handler handler2; // 지오코딩파서용 핸들러
	String dbFile = "weather.db3";
	String dbFolder = "/data/data/com.toh.weatherboard/datebases/";
	String numDong; // 최종적으로 가져다 붙일 동네코드가 저장되는 변수
	String numSido; // 시도 코드가 저장되어 구군table에서 비교하기 위한 변수
	String numGugun;// 구군 코드가 저장되어 동table에서 비교하기 위한 변수
	String numsy1;

	final int tableSido = 1; // 이건 switch case문에서 쓸려고 만든 변수
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
		// "측정기기가 분리되었습니다. 프로그램을 종료합니다.", Toast.LENGTH_SHORT)
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
		// Toast.makeText(getApplicationContext(), "측정기기가 연결 되었습니다.",
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

		// onCreate 에서
		try {
			boolean bResult = isCheckDB(getBaseContext()); // DB가 있는지?

			if (!bResult) { // DB가 없으면
				copyDB(getBaseContext()); // bd복사
				Toast.makeText(getApplicationContext(), "DB를 만들어요",
						Toast.LENGTH_SHORT).show();
			} else { // DB가 있으면
				Toast.makeText(getApplicationContext(), "이미 DB가있어요",
						Toast.LENGTH_SHORT).show();

			}

		} catch (Exception e) { // 예외발생용

			Toast.makeText(getApplicationContext(), "예외가 발생했어요",
					Toast.LENGTH_SHORT).show();
		}

		handler = new Handler(); // 스레드&핸들러처리
		handler2 = new Handler(); // 스레드&핸들러처리

		listView1 = (ListView) findViewById(R.id.listView1); // 날씨정보 리스트뷰

		bCategory = bTm = bHour = bTemp = bWdKor = bReh = bDay = bWfKor = bWs = tCategory = tTm = tItem = false; // 부울상수는

		listadapter = new listWeatherView(getBaseContext()); // 리스트뷰를 만들어주자
		listView1.setAdapter(listadapter); // 어댑터와 리스트뷰를 연결
		text = (TextView) findViewById(R.id.textView1); // 텍스트 객체생성
		// getBtn = (Button) findViewById(R.id.getBtn); // 버튼 객체생성
		// gpsBtn = (Button) findViewById(R.id.gpsBtn); // 버튼 객체생성
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
//			sidoSpinner = (Spinner) layout.findViewById(R.id.sidospinner); // 시도용
//			gugunSpinner = (Spinner) layout.findViewById(R.id.gugunspinner); // 구군용
//			dongSpinner = (Spinner) layout.findViewById(R.id.dongspinner); // 동면용
//
//			sidoSpinner.setOnItemSelectedListener(new OnItemSelectedListener() { // 이부분은
//						@Override
//						public void onItemSelected(AdapterView<?> parent,
//								View v, int position, long id) { // 선택시
//							numSido = sidonum[position]; // 시도가 선택되면 해당 코드를 변수에
//															// 넣는다
//							queryData(tableGugun); // 구군 DB가지러~
//						}
//
//						@Override
//						public void onNothingSelected(AdapterView<?> parent) { // 미
//
//						}
//					});
//			gugunSpinner
//					.setOnItemSelectedListener(new OnItemSelectedListener() { // 이부분은
//
//						@Override
//						public void onItemSelected(AdapterView<?> parent,
//								View v, int position, long id) { // 선택시
//							numGugun = gugunnum[position]; // 구군이 선택되면 해당 코드를
//															// 변수에
//							queryData(tableDong); // 동면 DB가지러~
//						}
//
//						@Override
//						public void onNothingSelected(AdapterView<?> parent) { // 미
//
//						}
//					});
//			dongSpinner.setOnItemSelectedListener(new OnItemSelectedListener() { // 이부분은
//
//						@Override
//						public void onItemSelected(AdapterView<?> parent,
//								View v, int position, long id) { // 선택시
//							tempDong = dongnum[position];
//							numDong = tempDong; // 선택된 동면코드를 변수에 넣자
//						}
//
//						@Override
//						public void onNothingSelected(AdapterView<?> parent) { // 미
//																				// 선택시
//						}
//					});
//
//			// set dialog message
//			alertDialogBuilder
//					.setMessage("위치를 설정해 주세요.")
//					.setCancelable(false)
//					.setPositiveButton("확인",
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int id) {
//									Log.e("###", "111");
//									numDong = tempDong;
//									text.setText(""); // 일단 중복해서 누를경우 대비해서 내용
//									String numdong = numDong.toString();
//									Log.e("numdong", numdong);
//									long val = adapter.insertDetails(numdong);
//									Log.e("###", "aaa");
//									numsy1 = adapter.queryName();
//									System.out.println("numsy1" + numsy1);
//
//									network_thread thread = new network_thread(); // 스레드생성(UI
//									thread.start(); // 스레드 시작
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
				network_thread thread = new network_thread(); // 스레드생성(UI
				thread.start(); // 스레드 시작
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
//		network_thread thread = new network_thread(); // 스레드생성(UI
//		thread.start(); // 스레드 시작
//		Log.e("###", "222");
		// mRunnable = new Runnable() {
		// @Override
		// public void run() {
		// network_thread thread = new network_thread(); // 스레드생성(UI
		// thread.start(); // 스레드 시작
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
//			queryData(tableSido); // 시도 DB 가지고 오자
//		}
//		Log.e("###", "444");
//		page++;
	}

	/**
	 * DB를 가져오는 부분 시도, 구군, 동면 모두 테이블명과 레코드가 다르기때문에 case문을 썼는데 코드가 너무 길어짐;;
	 * 
	 * @author Ans
	 * @param table
	 */
	private void queryData(final int table) {
		// TODO Auto-generated method stub
		Log.e("###", "555");
		openDatabase(dbFolder + dbFile); // db가 저장된 폴더에서 db를 가지고 온다
		String sql = null; // sql명령어를 저장할 변수
		Cursor cur = null; // db가져올 커서
		int Count; // db갯수 셀 변수

		switch (table) {

		case tableSido:
			sql = "select sido_num, sido_name from t_sido"; // 시도 테이블에선 시도코드와
															// 시도이름
			cur = db.rawQuery(sql, null); // 커서에 넣자
			break;
		case tableGugun: // 구군 테이블에선 시도에서 선택된 시도의 구군정보만
			sql = "select sido_num, gugun_num, gjgun_name from t_gugun where sido_num = "
					+ numSido;
			cur = db.rawQuery(sql, null);
			break;
		case tableDong: // 동면 테이블도 선택된 구군코드와 비교해서
			sql = "select gugun_num, dong_num, dong_name, gridx, gridy, _id from t_dong where gugun_num = "
					+ numGugun;
			cur = db.rawQuery(sql, null);
			break;
		default:
			break;
		}

		Count = cur.getCount(); // db의 갯수를 세고

		switch (table) {

		case tableSido:

			sidoname = new String[Count]; // 갯수만큼 배열을 만든다
			sidonum = new String[Count];

			if (cur != null) { // 이부분이 커서로 데이터를 읽어와서 변수에 저장하는 부분
				cur.moveToFirst();
				startManagingCursor(cur);
				for (int i = 0; i < Count; i++) {
					sidonum[i] = cur.getString(0);
					sidoname[i] = cur.getString(1);
					cur.moveToNext();
				}
				// 변수에 저장이 되었으니 스피너를 만들어 뿌려주자
				// 어댑터를 통해 스피너에 donglist 넣어줌
				sidoAdapter = new ArrayAdapter<String>(getBaseContext(),
						android.R.layout.simple_spinner_item, sidoname);
				sidoAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // dropdown형식
				sidoSpinner.setAdapter(sidoAdapter); // 스피너 생성
			}
			break;
		case tableGugun: // 구군도 같은작업

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
				// 어댑터를 통해 스피너에 donglist 넣어줌
				gugunAdapter = new ArrayAdapter<String>(getBaseContext(),
						android.R.layout.simple_spinner_item, gugunname); // 어댑터를
				gugunAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // dropdown형식
				gugunSpinner.setAdapter(gugunAdapter);

			}
			break;

		case tableDong: // 동면도 같은작업

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
						android.R.layout.simple_spinner_item, dongname); // 어댑터를
				dongAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // dropdown형식
				dongSpinner.setAdapter(dongAdapter);
			}
			break;

		default:
			break;

		}
	}

	/**
	 * 이부분이 db를 열어주는 부분
	 * 
	 * @author Ans
	 * @param databaseFile
	 */
	public static void openDatabase(String databaseFile) {

		try {
			db = SQLiteDatabase.openDatabase( // 선택한 폴더의 db를 가져와서 읽고,쓰기 가능하게
												// 읽어온다
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

	// DB가 있나 체크하기
	public boolean isCheckDB(Context mContext) {

		String filePath = dbFolder + dbFile;
		File file = new File(filePath);

		if (file.exists()) { // db폴더에 파일이 있으면 true
			return true;
		}

		return false; // 아님 default로 false를 반환

	}

	// DB를 복사하기
	// assets의 /db/xxxx.db 파일을 설치된 프로그램의 내부 DB공간으로 복사하기
	public void copyDB(Context mContext) { // 만약 db가 없는 경우 복사를 해야된다.

		AssetManager manager = mContext.getAssets(); // asserts 폴더에서 파일을 읽기위해
														// 쓴단다.아직 잘
		String folderPath = dbFolder; // db폴더 //일단 DB를 이 폴더에 저장을 하였으니 써야겠지?
		String filePath = dbFolder + dbFile; // db폴더와 파일경로
		File folder = new File(folderPath);
		File file = new File(filePath);

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			// 일단 asserts폴더밑 db폴더에서 db를 가져오자
			InputStream is = manager.open("db/" + "weather.db3");
			BufferedInputStream bis = new BufferedInputStream(is);

			if (folder.exists()) { // 우리가 복사하려는 db폴더가 이미 있으면 넘어가고
			} else {
				folder.mkdirs(); // 없을경우 폴더를 만들자
			}

			if (file.exists()) { // 파일이 있다면
				file.delete(); // 일단 지우고
				file.createNewFile(); // 새 파일을 만들자
			}

			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			int read = -1;
			byte[] buffer = new byte[1024]; // buffer는 1024byte니깐 1k로 지정해주고
			while ((read = bis.read(buffer, 0, 1024)) != -1) { // db파일을 읽어서
																// buffer에 넣고
				bos.write(buffer, 0, read); // buffer에서 새로 만든 파일에 쓴다
			} // 대충이해는 되는데 어렵네;;

			bos.flush();

			bos.close();
			fos.close();
			bis.close();
			is.close();

		} catch (IOException e) {

		}
	}

	/**
	 * 기상청을 연결하여 정보받고 뿌려주는 스레드
	 * 
	 * @author Ans
	 * 
	 */
	class network_thread extends Thread { // 기상청 연결을 위한 스레드
		/**
		 * 기상청을 연결하는 스레드 이곳에서 풀파서를 이용하여 기상청에서 정보를 받아와 각각의 array변수에 넣어줌
		 * 
		 * @author Ans
		 */
		public void run() {
			Log.e("###", "666");
			try {
				updated = false;
				sHour = new String[100]; // 예보시간(사실 15개밖에 안들어오지만 넉넉하게 20개로 잡아놓음)
				sDay = new String[100]; // 날짜
				sTemp = new String[100]; // 현재온도
				smxTemp = new String[100]; // 최고온도
				smnTemp = new String[100]; // 최저온도
				sWdKor = new String[100]; // 풍향
				sReh = new String[100]; // 습도
				sWfKor = new String[100]; // 날씨
				sWs = new String[100]; // 풍향
				data = 0;

				XmlPullParserFactory factory = XmlPullParserFactory
						.newInstance(); // 이곳이 풀파서를 사용하게 하는곳
				factory.setNamespaceAware(true); // 이름에 공백도 인식
				XmlPullParser xpp = factory.newPullParser(); // 풀파서 xpp라는 객체 생성
				Log.e("numDong",numDong);
				String weatherUrl = "http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone="
						+ numDong; // 이곳이 기상청URL
				URL url = new URL(weatherUrl); // URL객체생성
				InputStream is = url.openStream(); // 연결할 url을 inputstream에 넣어
													// 연결을 하게된다.
				xpp.setInput(is, "UTF-8"); // 이렇게 하면 연결이 된다. 포맷형식은 utf-8로

				int eventType = xpp.getEventType(); // 풀파서에서 태그정보를 가져온다.

				while (eventType != XmlPullParser.END_DOCUMENT) { // 문서의 끝이 아닐때

					switch (eventType) {
					case XmlPullParser.START_TAG: // '<'시작태그를 만났을때

						if (xpp.getName().equals("category")) { // 태그안의 이름이
																// 카테고리일떄 (이건
																// 동네이름이 나온다)
							bCategory = true;

						}
						if (xpp.getName().equals("pubDate")) { // 발표시각정보
							bTm = true;

						}
						if (xpp.getName().equals("hour")) { // 예보시간
							bHour = true;

						}
						if (xpp.getName().equals("day")) { // 예보날(오늘 내일 모레)
							bDay = true;

						}
						if (xpp.getName().equals("temp")) { // 예보시간기준 현재온도
							bTemp = true;

						}
						if (xpp.getName().equals("wdKor")) { // 풍향정보
							bWdKor = true;

						}
						if (xpp.getName().equals("reh")) { // 습도정보
							bReh = true;

						}
						if (xpp.getName().equals("wfKor")) { // 날씨정보(맑음, 구름조금,
																// 구름많음, 흐림, 비,
																// 눈/비, 눈)
							bWfKor = true;

						}
						if (xpp.getName().equals("ws")) { // 풍속
							bWs = true;

						}
						if (xpp.getName().equals("tmx")) { // 풍속
							bmxTemp = true;

						}
						if (xpp.getName().equals("tmn")) { // 풍속
							bmnTemp = true;

						}
						break;

					case XmlPullParser.TEXT: // 텍스트를 만났을때

						if (bCategory) { // 동네이름
							sCategory = xpp.getText();
							bCategory = false;
						}
						if (bTm) { // 발표시각
							sTm = xpp.getText();
							bTm = false;
						}
						if (bHour) { // 예보시각
							sHour[data] = xpp.getText();
							bHour = false;
						}
						if (bDay) { // 예보날짜
							sDay[data] = xpp.getText();
							bDay = false;
						}
						if (bTemp) { // 현재온도
							sTemp[data] = xpp.getText();
							// System.out.println("TEMP"+sTemp[data]);
							bTemp = false;
						}
						if (bWdKor) { // 풍향
							sWdKor[data] = xpp.getText();
							bWdKor = false;
						}
						if (bReh) { // 습도
							sReh[data] = xpp.getText();
							bReh = false;
						}
						if (bWfKor) { // 날씨
							sWfKor[data] = xpp.getText();
							bWfKor = false;
						}
						if (bWs) { // 풍속
							sWs[data] = xpp.getText();
							bWs = false;
						}
						if (bmxTemp) { // 최고온도
							smxTemp[data] = xpp.getText();
							bmxTemp = false;
						}
						if (bmnTemp) { // 최저온도
							smnTemp[data] = xpp.getText();
							bmnTemp = false;
						}
						break;

					case XmlPullParser.END_TAG: // '</' 엔드태그를 만나면 (이부분이 중요)

						if (xpp.getName().equals("item")) { // 태그가 끝나느 시점의 태그이름이
															// item이면(이건 거의 문서의
															// 끝
							tItem = true; // 따라서 이때 모든 정보를 화면에 뿌려주면 된다.
							view_text(); // 뿌려주는 곳~
						}
						if (xpp.getName().equals("pubDate")) { // 이건 발표시각정보니까
																// 1번만나오므로 바로
																// 뿌려주자
							tTm = true;
							//view_text();
						}
						if (xpp.getName().equals("category")) { // 이것도 동네정보라 바로
																// 뿌려주면 됨
							tCategory = true;
							//view_text();
						}
						if (xpp.getName().equals("data")) { // data태그는 예보시각기준
															// 예보정보가 하나씩이다.
							data++; // 즉 data태그 == 예보 개수 그러므로 이때 array를 증가해주자
						}
						break;
					}
					eventType = xpp.next(); // 이건 다음 이벤트로~
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		/**
		 * 이 부분이 뿌려주는곳 뿌리는건 핸들러가~
		 * 
		 * @author Ans
		 */
		private void view_text() {
			Log.e("###", "777");
			handler.post(new Runnable() { // 기본 핸들러니깐 handler.post하면됨

				@Override
				public void run() {
					Log.e("###", "888");
					if (tCategory) { // 동네이름 들어왔다
						text.setText(text.getText() + "지역:" + sCategory + "\n");
						tCategory = false;
					}
					if ((tTm) && (sTm.length() > 11)) { // 발표시각 들어왔다
						text.setText(text.getText() + "발표시각:" + sTm + "\n");
						tTm = false;
					}
					if (tItem) { // 문서를 다 읽었다

						for (int i = 0; i < data; i++) { // array로 되어있으니 for문으로
							Log.e("###", "999");
							if (sDay[i] != null) { // 이건 null integer 에러 예방을

								if (sDay[i].equals("0")) { // 발표시각이 0이면 오늘
									System.out.println("sDay[i]" + sDay[i]);
									// sDay[i] = "날짜:" + "오늘";

								} else if (sDay[i].equals("1")) { // 1이면 내일
									sDay[i] = "날짜:" + "내일";

								} else if (sDay[i].equals("2")) { // 2이면 모레
									sDay[i] = "날짜:" + "모레";

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
							for (int k = 0; k < data; k++) { // array로 되어있으니
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
						if (sWfKor[0].equals("맑음")) {
							weatherinfo.setImageResource(R.drawable.nb01);
						}
						if (sWfKor[0].equals("구름 조금")) {
							weatherinfo.setImageResource(R.drawable.nb02);
						}
						if (sWfKor[0].equals("구름 많음")) {
							weatherinfo.setImageResource(R.drawable.nb03);
						}
						if (sWfKor[0].equals("흐림")) {
							weatherinfo.setImageResource(R.drawable.nb04);
						}
						if (sWfKor[0].equals("비")) {
							weatherinfo.setImageResource(R.drawable.nb08);
						}
						if (sWfKor[0].equals("눈/비")) {
							weatherinfo.setImageResource(R.drawable.nb13);
						}
						if (sWfKor[0].equals("눈")) {
							weatherinfo.setImageResource(R.drawable.nb11);
						}

						listadapter.setDay(sDay); // 날씨정보를 listview로 뿌려보자
						listadapter.setTime(sHour);
						listadapter.setTemp(sTemp);
						listadapter.setWind(sWdKor);
						listadapter.setHum(sReh);
						listadapter.setWeather(sWfKor);
						updated = true; // 정보가 담겼으니 flag를 true로
						listadapter.notifyDataSetChanged();
						tItem = false;
						data = 0; // 다음에 날씨를 더가져오게 되면 처음부터 저장해야겠지?

					}

				}
			});
		}
	}

	/**
	 * 이곳에서 GPS방법과 리스너등을 세팅해 준다
	 * 
	 * @author Ans
	 */
	public void getMyLocation() {

		manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); // GPS
																				// 서비스
		long minTime = 60000; // every 60sec
		float minDistance = 1; // if moved over 1miter
		listener = new MyLocationListener(); // 리스너 만들자
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime,
				minDistance, listener);// GPS
		manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				minTime, minDistance, listener);// 기지국

	}

	/**
	 * 이부분은 지오코딩해서 파서하는 곳
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
				// URL객체생성
				InputStream is = url.openStream();

				xpp.setInput(is, "UTF-8");
				int eventType = xpp.getEventType();
				geodata = 0;

				while (eventType != XmlPullParser.END_DOCUMENT) { // 문서의 끝이 아닐때
					switch (eventType) {
					case XmlPullParser.START_TAG: // '<'시작태그를 만났을때
						if (xpp.getName().equals("long_name")) { // 태그안의 이름이
																	// 카테고리일떄
																	// (이건 동네이름이
																	// 나온다)
							bLong_name = true;

						}
						break;
					case XmlPullParser.TEXT: // 텍스트를 만났을때
												// 앞서 시작태그에서 얻을정보를 만나면 플래그를
												// true로 했는데 여기서 플래그를 보고
												// 변수에 정보를 넣어준 후엔 플래그를 false로~
						if (bLong_name) { // 동네이름
							sLong_name[geodata] = xpp.getText();
							bLong_name = false;
						}
						break;

					case XmlPullParser.END_TAG: // '</' 엔드태그를 만나면 (이부분이 중요)
						if (xpp.getName().equals("GeocodeResponse")) { // 태그가
																		// 끝나느
																		// 시점의
																		// 태그이름이
																		// item이면(이건
																		// 거의
																		// 문서의 끝

							showtext();
							break; // 따라서 이때 모든 정보를 화면에 뿌려주면 된다.
						}
						if (xpp.getName().equals("address_component")) { // data태그는
																			// 예보시각기준
																			// 예보정보가
																			// 하나씩이다.
							geodata++; // 즉 data태그 == 예보 개수 그러므로 이때 array를 증가해주자
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
							"현재 위치는 " + sLong_name[3] + " " + sLong_name[2]
									+ " " + sLong_name[1], Toast.LENGTH_SHORT)
							.show();
					String sql = "select gugun_num, dong_num, dong_name, gridx, gridy, _id from t_dong where dong_name = "
							+ "'" + sLong_name[1] + "'";
					Cursor cur = db.rawQuery(sql, null);
					if (cur.getCount() != 0) {
						cur.moveToFirst();
						numDong = cur.getString(1);
						cur.close();
						network_thread thread = new network_thread(); // 스레드생성(UI
																		// 스레드사용시
																		// system
																		// 뻗는다)
						thread.start(); // 스레드 시작
					}
				}
			});
		}
	}

	/**
	 * 이곳에서 실제로 지피에스 정보를 받아온다
	 * 
	 * @author Ans
	 * 
	 */
	class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {

			latitude = location.getLatitude();// get latitued
			longitutde = location.getLongitude();// get longitutde
			manager.removeUpdates(listener); // 지피에스 서비스 종료(이부분을 주석처리하면 설정한거대로
												// 계속 받아옴)
			getaddress thread = new getaddress(); // 지오코딩할 스레드

			thread.start(); // 스레드 시작
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
	 * 이부분은 날씨를 리스트뷰에 뿌려주는 어댑터
	 * 
	 * @author Ans
	 * 
	 */
	class listWeatherView extends BaseAdapter {

		String[] day, time, temp, wind, hum, weather;
		Context mContext;
		String temp_data[] = new String[15]; // 임시로 만들었다 nullpointexception 방지

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

			return temp_data.length; // 리스트뷰의 갯수
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

			if (convertView != null) { // 스크롤로 넘어간 뷰를 버리지 않고 재사용
				layout = (showWeather) convertView;
			} else {
				layout = new showWeather(mContext.getApplicationContext()); // 레이아웃
																			// 설정

			}

			if (updated) { // 날씨정보를 받아왔으면
				layout.setDate(day[position]); // 레이아웃으로 뿌려줌
				layout.setTime(time[position]);
				layout.setTemp(temp[position]);
				layout.setWind(wind[position]);
				layout.setHum(hum[position]);
				layout.setWeather(weather[position]);

			}

			return layout;
		}
	}

	// 기상청 날씨정보 추출
	private String loadKmaData() throws Exception {
		String page = "http://www.kma.go.kr/wid/queryDFS.jsp?gridx=57&gridy=125";
		URL url = new URL(page);
		HttpURLConnection urlConnection = (HttpURLConnection) url
				.openConnection();
		if (urlConnection == null)
			return null;
		urlConnection.setConnectTimeout(10000);// 최대 10초 대기
		urlConnection.setUseCaches(false);// 매번 서버에서 읽어오기
		StringBuilder sb = new StringBuilder();// 고속 문자열 결합체
		if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			System.out.println(urlConnection);
			InputStream inputStream = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(inputStream);
			// 한줄씩 읽기
			BufferedReader br = new BufferedReader(isr);
			while (true) {
				String line = br.readLine();// 웹페이지의 html 코드 읽어오기
				if (line == null)
					break;// 스트림이 끝나면 null리턴
				sb.append(line + "\n");
			}// end while
			br.close();
			System.out.println(br);

		}// end if
		return sb.toString();
	}

	// 기상청 날씨정보 추출
	private String wloadKmaData() throws Exception {
		String page = "http://www.kma.go.kr/weather/forecast/mid-term-xml.jsp?stnId=109";
		URL url = new URL(page);
		HttpURLConnection urlConnection = (HttpURLConnection) url
				.openConnection();
		if (urlConnection == null)
			return null;
		urlConnection.setConnectTimeout(10000);// 최대 10초 대기
		urlConnection.setUseCaches(false);// 매번 서버에서 읽어오기
		StringBuilder sb = new StringBuilder();// 고속 문자열 결합체
		if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			System.out.println(urlConnection);
			InputStream inputStream = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(inputStream);
			// 한줄씩 읽기
			BufferedReader br = new BufferedReader(isr);
			while (true) {
				String line = br.readLine();// 웹페이지의 html 코드 읽어오기
				if (line == null)
					break;// 스트림이 끝나면 null리턴
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
			//Toast.makeText(getApplicationContext(), "측정기가 연결되어 있지 않습니다.",
					//Toast.LENGTH_SHORT).show();
		} else {
			try {
				mUART_Device.UART_open();
				mUART_Device.UART_SETTING(9600, 8, 1, 0);

			} catch (IOException e) {
				Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
				Toast.makeText(getApplicationContext(), "연결하는데 문제가 발생하였습니다.",
						Toast.LENGTH_SHORT).show();
				try {
					mUART_Device.UART_close();
				} catch (IOException e2) {
					// Ignore.
				}
				mUART_Device = null;
				return;
			}
			Toast.makeText(getApplicationContext(), "측정기가 연결되었습니다.",
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
			emergy_str = "특이  사항  없음";
		} else {
			if (tod_war == 1) {
				emergency.setTextColor(Color.rgb(255, 255, 0));
			} else {
				emergency.setTextColor(Color.rgb(255, 0, 0));
			}
			if (tod_snow == 1) {
				emergy_str = "강 우      예 보\r\r\n예상 " + tod_data + "mm";
			}
			if (tod_snow == 2) {
				emergy_str = "강 설      예 보\r\r\n예상 " + tod_data + "mm";
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
			// rn1.setText("북");
			// else if (wind_d_dis > 11.2 && wind_d_dis <= 33.7)
			// rn1.setText("북북동");
			// else if (wind_d_dis > 33.7 && wind_d_dis <= 56.2)
			// rn1.setText("북동");
			// else if (wind_d_dis > 56.2 && wind_d_dis <= 78.7)
			// rn1.setText("동북동");
			// else if (wind_d_dis > 78.7 && wind_d_dis <= 101.2)
			// rn1.setText("동");
			// else if (wind_d_dis > 101.2 && wind_d_dis <= 123.7)
			// rn1.setText("동남동");
			// else if (wind_d_dis > 123.7 && wind_d_dis <= 146.2)
			// rn1.setText("남동");
			// else if (wind_d_dis > 146.2 && wind_d_dis <= 168.7)
			// rn1.setText("남남동");
			// else if (wind_d_dis > 168.7 && wind_d_dis <= 191.2)
			// rn1.setText("남");
			// else if (wind_d_dis > 191.2 && wind_d_dis <= 213.7)
			// rn1.setText("남남서");
			// else if (wind_d_dis > 213.7 && wind_d_dis <= 236.2)
			// rn1.setText("남서");
			// else if (wind_d_dis > 236.2 && wind_d_dis <= 258.7)
			// rn1.setText("서남서");
			// else if (wind_d_dis > 258.7 && wind_d_dis <= 281.2)
			// rn1.setText("서");
			// else if (wind_d_dis > 281.2 && wind_d_dis <= 303.7)
			// rn1.setText("서북서");
			// else if (wind_d_dis > 303.7 && wind_d_dis <= 326.7)
			// rn1.setText("북서");
			// else if (wind_d_dis > 326.2 && wind_d_dis <= 348.7)
			// rn1.setText("북북서");
			// else
			// rn1.setText("북")
			;
		TextView rn2 = (TextView) findViewById(R.id.precipitation1);
		num = String.format("%3.1f", rfallhours_dis);
		rfallhours_dis = Double.parseDouble(num);
		rn2.setText(String.valueOf(rfallhours_dis));

		TextView rn3 = (TextView) findViewById(R.id.todayPrecipitation1);
		// Random rand3 = new Random();
		// rfall = rand3.nextInt(50);
		// rfall = 10.1; // 오늘 강수
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
		// rfallh = 34.5; // 시간 강수
		// rfallhours = 32.1;
		num = String.format("%3.1f", rfallyester_dis);
		rfallyester_dis = Double.parseDouble(num);
		rn6.setText(String.valueOf(rfallyester_dis));

		TextView rn7 = (TextView) findViewById(R.id.yesterdayPrecipitation1);
		// Random rand7 = new Random();
		// pickedNumber7 = rand7.nextInt(50);
		// rfally = 45.6; //어제 강수
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
		result = "예보 수신 대기 중";
		result3 = "예보 수신 대기 중";

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
				// DOM 파싱.
				ByteArrayInputStream bai = new ByteArrayInputStream(
						html.getBytes());
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				// dbf.setIgnoringElementContentWhitespace(true);//화이트스패이스 생략
				DocumentBuilder builder = dbf.newDocumentBuilder();
				Document parse = builder.parse(bai);// DOM 파서
				// 태그 검색
				NodeList datas = parse.getElementsByTagName("data");
				// String result = "data태그 수 =" + datas.getLength()+"\n";

				// 17개의 data태그를 순차로 접근

				ByteArrayInputStream wbai = new ByteArrayInputStream(
						whtml.getBytes());
				DocumentBuilderFactory wdbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder wbuilder = wdbf.newDocumentBuilder();
				Document wparse = builder.parse(wbai);// DOM 파서
				String xpath = "//wid/body/location[@city='11B20201']";
				NodeList wdatas = parse.getElementsByTagName(xpath);

				int ttttt = 0;
				sky_state = 100;
				pty_state = 100;

				for (int idx = 0; idx < datas.getLength(); idx++) {

					// 필요한 정보들을 담을 변수 생성
					String day = "";
					String hour = "";
					String sky = "";
					String temp = "";

					int pop = 0;
					// toom_h = 5;

					Node node = datas.item(idx);// data 태그 추출
					int childLength = node.getChildNodes().getLength();
					// 자식태그 목록 수정
					NodeList childNodes = node.getChildNodes();
					for (int childIdx = 0; childIdx < childLength; childIdx++) {
						Node childNode = childNodes.item(childIdx);
						int count = 0;
						if (childNode.getNodeType() == Node.ELEMENT_NODE) {
							count++;
							// 태그인 경우만 처리

							// 금일,내일,모레 구분(시간정보 포함)
							if (childNode.getNodeName().equals("day")) {
								int su = Integer.parseInt(childNode
										.getFirstChild().getNodeValue());

								start_flew = 1;
								switch (su) {
								case 0:
									day = "☆오늘";
									toom = 0;
									break;
								case 1:
									day = "★내일";
									toom = 1;
									break;
								case 2:
									day = "☆모레";
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
								// 하늘상태코드 분석
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
													r06); // 적설량 서비스 확인 필요, 우선
															// 강수량 데이타 참고
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
					}// end 안쪽 for문
					if (ttttt == 0) {
						if (r12 > 0.0)
							result += day + " " + hour + "시 예보  - " + sky
									+ ", " + temp + "도, 풍속 " + num_ws
									+ ", 강수확률 " + pop + "%, 6시간 예상 강수량 "
									+ num_r12 + "mm\n";
						else if (s12 > 0.0)
							result += day + " " + hour + "시 예보 - " + sky + ", "
									+ temp + "도, 풍속 " + num_ws + ", 강수확률 "
									+ pop + "%, 12시간 예상 강수량 " + num_r12
									+ "mm\n";

						else if (r06 > 0.0)
							result += day + " " + hour + "시 예보 - " + sky + ", "
									+ temp + "도, 풍속 " + num_ws + ", 강수확률 "
									+ pop + "%, 6시간 예상 강수량 " + num_r06 + "mm\n";

						else if (s06 > 0.0)
							result += day + " " + hour + "시 예보 - " + sky + ", "
									+ temp + "도, 풍속 " + num_ws + ", 강수확률 "
									+ pop + "%, 6시간 예상 강수량 " + num_r06 + "mm\n";

						else
							result += day + " " + hour + "시  예보 - " + sky
									+ ", " + temp + "도, 풍속 " + num_ws
									+ ", 강수확률 " + pop
									+ "%, 6~12시간 예상 강수량 0mm\n";
					}
					/*
					 * if (ttttt == 0) { if (r12 > 0.0) result += day + " " +
					 * hour + "시 예보  -" + sky + "," + temp + "도, 풍속 " + num_ws +
					 * ", 강수확률 " + pop + "%, 12시간 예상 강수량 " + num_r12 + "ml\n";
					 * else if (s12 > 0.0) result += day + " " + hour + "시 예보 -"
					 * + sky + "," + temp + "도, 풍속 " + num_ws + ", 강수확률 " + pop
					 * + "%, 12시간 예상 강설량 " + num_r12 + "mm\n"; else result +=
					 * day + " " + hour + "시  예보 -" + sky + "," + temp +
					 * "도, 풍속 " + num_ws + ", 강수확률 " + pop +
					 * "%, 12시간 예상 강수량 0ml\n"; }
					 */
					else if (ttttt == 1) {
						if (r12 > 0.0)
							result2 += day + " " + hour + "시 예보  - " + sky
									+ ", " + temp + "도, 풍속 " + num_ws
									+ ", 강수확률 " + pop + "%, 12시간 예상 강수량 "
									+ num_r12 + "mm\n";
						else if (s12 > 0.0)
							result2 += day + " " + hour + "시 예보 - " + sky
									+ ", " + temp + "도, 풍속 " + num_ws
									+ ", 강수확률  " + pop + "%, 12시간 예상 강수량 "
									+ num_r12 + "mm\n";
						else if (r06 > 0.0)
							result2 += day + " " + hour + "시 예보 - " + sky
									+ ", " + temp + "도, 풍속 " + num_ws
									+ ", 강수확률  " + pop + "%, 6시간 예상 강수량 "
									+ num_r06 + "mm\n";

						else if (s06 > 0.0)
							result2 += day + " " + hour + "시 예보 - " + sky
									+ ", " + temp + "도, 풍속 " + num_ws
									+ ", 강수확률  " + pop + "%, 6시간 예상 강수량 "
									+ num_r06 + "mm\n";

						else
							result2 += day + " " + hour + "시  예보 - " + sky
									+ ", " + temp + "도, 풍속 " + num_ws
									+ ", 강수확률 " + pop
									+ "%, 6~12시간 예상 강수량 0mm\n";
					} else if (ttttt == toom_h) {
						if (r12 > 0.0)
							result3 += day + " " + hour + "시 예보  - " + sky
									+ ", " + temp + "도, 풍속 " + num_ws
									+ ", 강수확률 " + pop + "%, 12시간 예상 강수량 "
									+ num_r12 + "mm\n";
						else if (s12 > 0.0)
							result3 += day + " " + hour + "시 예보 - " + sky
									+ ", " + temp + "도, 풍속 " + num_ws
									+ ", 강수확률 " + pop + "%, 12시간 예상 강수량 "
									+ num_r12 + "mm\n";
						else if (r06 > 0.0)
							result3 += day + " " + hour + "시 예보 - " + sky
									+ ", " + temp + "도, 풍속 " + num_ws
									+ ", 강수확률 " + pop + "%, 6시간 예상 강수량 "
									+ num_r06 + "mm\n";

						else if (s12 > 0.0)
							result3 += day + " " + hour + "시 예보 - " + sky
									+ ", " + temp + "도, 풍속 " + num_ws
									+ ", 강수확률 " + pop + "%, 6시간 예상 강수량 "
									+ num_r06 + "mm\n";

						else
							result3 += day + " " + hour + "시  예보 - " + sky
									+ ", " + temp + "도, 풍속 " + num_ws
									+ ", 강수확률 " + pop
									+ "%, 6~12시간 예상 강수량 0mm\n";
					} else if (ttttt == toom_h + 1) {
						if (r12 > 0.0)
							result4 += day + " " + hour + "시 예보  - " + sky
									+ ", " + temp + "도, 풍속 " + num_ws
									+ ", 강수확률 " + pop + "%, 12시간 예상 강수량 "
									+ num_r12 + "mm\n";
						else if (s12 > 0.0)
							result4 += day + " " + hour + "시 예보 - " + sky
									+ ", " + temp + "도, 풍속 " + num_ws
									+ ", 강수확률 " + pop + "%, 12시간 예상 강수량 "
									+ num_r12 + "mm\n";

						else if (r06 > 0.0)
							result4 += day + " " + hour + "시 예보 - " + sky
									+ ", " + temp + "도, 풍속 " + num_ws
									+ ", 강수확률 " + pop + "%, 6시간 예상 강수량 "
									+ num_r06 + "mm\n";

						else if (s06 > 0.0)
							result4 += day + " " + hour + "시 예보 - " + sky
									+ ", " + temp + "도, 풍속 " + num_ws
									+ ", 강수확률 " + pop + "%, 6시간 예상 강수량 "
									+ num_r06 + "mm\n";

						else
							result4 += day + " " + hour + "시  예보 - " + sky
									+ ", " + temp + "도, 풍속 " + num_ws
									+ ", 강수확률 " + pop
									+ "%, 6~12시간 예상 강수량 0mm\n";
					}

					ttttt++;

					// if(ttttt == 2){
					// result += "\n\n";
					// ttttt = 0;
					// }
				}// end 바깥쪽 for문

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
				// tv.setText("오류" + e.getMessage());
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
			num = String.format("%c%4d", '월', rfallmonth_dis);
			rn7.setText(num);
		} else {
			num = String.format("%c%4d", '연', rfallyear_dis);
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
