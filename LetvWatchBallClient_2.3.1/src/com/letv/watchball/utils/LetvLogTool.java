package com.letv.watchball.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Environment;

public class LetvLogTool {

	private final String PATH = Environment.getExternalStorageDirectory()
			+ "/letvWatchBall/PushLog/";

	private final String FILE_NAME = "push.log";
//	private final String PUSH_FILE_NAME = "push.log";
	private File dir = new File(PATH);
	private File file = null;
	
	private final int POOL_SIZE = 3;
	private int cpuNums = Runtime.getRuntime().availableProcessors();
	private ExecutorService executor = Executors.newFixedThreadPool(cpuNums * POOL_SIZE); 
	
	
	private static LetvLogTool mLetvLogTool = null;
	
	public synchronized static LetvLogTool getInstance() {
        if (mLetvLogTool == null) {
        	mLetvLogTool = new LetvLogTool();
        }
        return mLetvLogTool;
    }

	public void log(String data) {

		if (!LetvUtil.sdCardMounted()) {
			return;
		}

		if (!dir.exists()) {
			dir.mkdirs();
		}

		if (file == null) {
			file = new File(dir, FILE_NAME);
		}
		
		executor.execute(new Handler(data));
	}

	public void log(String data, String fileName) {

		if (!LetvUtil.sdCardMounted()) {
			return;
		}

		File dir = new File(PATH);

		if (!dir.exists()) {
			dir.mkdirs();
		}

		if (file == null || (file != null && !file.getName().equalsIgnoreCase(fileName))) {
			file = new File(dir, fileName);
		}
		
		executor.execute(new Handler(data));
	}
	
	class Handler implements Runnable {
		
		private String data;
		
		public Handler(String data) {
			this.data = data;
		}

		@Override
		public void run() {
			synchronized (file) {
				BufferedReader stringReader = null;
				FileWriter fileWriter = null;

				try {

					stringReader = new BufferedReader(new StringReader(data));

					fileWriter = new FileWriter(file, true);

					String line = null;

					while ((line = stringReader.readLine()) != null) {
						fileWriter.write(line);
						fileWriter.write("\r\n");
					}

					stringReader.close();
					fileWriter.flush();
					fileWriter.close();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						stringReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
}
