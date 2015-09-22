package com.toh.weatherboard;

//import android.R;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;

//import kr.co.geemo.weatherboard.R;
//import kr.co.geemo.weatherboard.WeatherBoard.CountDownRunner;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class ServiceClass extends Service{
	int year;
	int month;
	int day;
	int old_day;
	int hours;
	int old_hours;
	int minutes;
	int old_minutes;

	int seconds;
	int r_timer;
	int rcv_timer;
	int m_count;
	int kkk;
	int send_timer;
	int rfalls_tog;
	int start_sv;
	
	//private MediaPlayer mPlayer = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("slog", "onStart()");
		super.onStart(intent, startId); 
		// raw���ъ깮��mp3?���ｌ�?二쇱�?���?
	//	mPlayer = MediaPlayer.create(this, R.raw.way);
	//	mPlayer.start();
		old_minutes = 0;
		start_sv = 0;
		Thread myThread = null;

		Runnable runnable = new CountDownRunner("GEEMO");
		myThread = new Thread(runnable);
		myThread.start();
	}
	
	@Override
	public void onDestroy() { 
		Log.d("slog", "onDestroy()");
	//	mPlayer.stop();
		super.onDestroy();
	}
	
	
	class CountDownRunner implements Runnable {
		  private String msg;
		  public CountDownRunner(String _msg){
		   this.msg = _msg;
		  }
		// @Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {			
					doWork();
					Log.d("slog", "onDowork()");
					Thread.sleep(1000);	
					
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
				}
				
				
			}
		}
	}
	
	
public void doWork() {
				try {
					//Toast.makeText(getApplicationContext(), "DWORK.", Toast.LENGTH_SHORT).show();
					Log.d("slog", "TTTonDowork()");
					Date dt = new Date();
					Calendar cal = Calendar.getInstance(); 
					cal.setTime(dt);
					String year_s;
					String month_s;
					String day_s;
					String hours_s; 
					String minutes_s;
					String seconds_s;
					cal.add(Calendar.MONTH,1);
					year = cal.get(Calendar.YEAR);
					month = cal.get(Calendar.MONTH);
					//if(month == 9) month = 10;
					//month = cal.get(Calendar.
					day = cal.get(Calendar.DAY_OF_MONTH);
					hours = cal.get(Calendar.HOUR_OF_DAY);
					minutes = cal.get(Calendar.MINUTE);
					seconds = cal.get(Calendar.SECOND);
					if(start_sv == 0){
						old_minutes = minutes;
						start_sv = 1;
					}
					
					if(minutes == 5 && hours == 0){
				//	if((minutes == 0 && hours == 10) || (minutes == 52 && hours == 9) || (minutes == 54 && hours == 9) || (minutes == 56 && hours == 9)){
				//	if((minutes == 30 && hours == 0) || (minutes == 30 && hours == 6) || (minutes == 30 && hours == 12) || (minutes == 30 && hours == 18)){						
						Log.d("slog", "TT............()");
						//Toast.makeText(getApplicationContext(), "측정기�? ?�결?�었?�니??", Toast.LENGTH_SHORT).show();
						old_minutes = minutes;
						//Power.reboot("reboot");
						try {
				            Process proc = Runtime.getRuntime().exec(
				                    new String[] { "su", "-c", "reboot" });
				            proc.waitFor();
				        } catch (Exception ex) {
				            Log.i("Reboot Code", "Could not reboot", ex);
				        }

					}
					
					
				} catch (Exception e) {
					
				}
	
	}

}
