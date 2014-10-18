package com.letv.watchball.share;


public class LetvKaixinShare {
	
//	/**
//	 * 判断是否登录
//	 * */
//	public static boolean isLogin(Context context){
//		Kaixin kaixin = Kaixin.getInstance();
//		boolean b = kaixin.loadStorage(context);
//		if(kaixin.isSessionValid()){
//			return true ;
//		}
//		return false ;
//	}
//	
//	/**
//	 * 登录
//	 * */
//	public static void login(final Activity context , final Album album){
//		Kaixin kaixin = Kaixin.getInstance();
//		kaixin.loadStorage(context);
//		if(kaixin.isSessionValid()){
//			ShareActivity.launch(context,2, album);
//			return ;
//		}
//		
//		KaixinAuthListener listener = new KaixinAuthListener() {
//			
//			@Override
//			public void onAuthError(KaixinAuthError arg0) {
//				
//			}
//			
//			@Override
//			public void onAuthComplete(Bundle arg0) {
//				ShareActivity.launch(context,2, album);
//			}
//			
//			@Override
//			public void onAuthCancelLogin() {
//				
//			}
//			
//			@Override
//			public void onAuthCancel(Bundle arg0) {
//				
//			}
//		};
//		
//		String[] permissions = {"basic", "create_records"};
//		kaixin.authorizeFull(context , permissions, listener);
//	}
//
//	/**
//	 * 分享
//	 * */
//	public static void share(String content , String imgUrl , Context context , KaixinAuthListener listener){
//		String path = ImageStorage.url2FilePath(imgUrl);
//		Kaixin kaixin = Kaixin.getInstance() ;
//		File file = new File(path);
//		InputStream in = null ;
//		try {
//			in = new FileInputStream(file);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		
//		PostRecordTask getDataTask = new PostRecordTask();
//		getDataTask.execute(kaixin, content, in, context ,listener);
//	}
//	
//	/**
//	 * 登出
//	 * */
//	public static void logout(Activity context){
//		Kaixin kaixin = Kaixin.getInstance() ;
//		kaixin.clearStorage(context);
//	}
//
//	/**
//	 * 分享的异步线程
//	 * */
//	public static class PostRecordTask extends AsyncTask<Object, Void, Integer> {
//		private static final String RESTAPI_INTERFACE_POSTRECORD = "/records/add.json";
//
//		@SuppressWarnings("unchecked")
//		protected Integer doInBackground(Object... params) {
//			if (params == null || params.length == 0 || params.length != 5) {
//				return 0;
//			}
//
//			Kaixin kaixin = (Kaixin) params[0];
//			String content = (String) params[1];
//			InputStream in = (InputStream) params[2];
//			Context context = (Context) params[3];
//			KaixinAuthListener listener = (KaixinAuthListener)params[4];
//			try {
//				
//				Bundle bundle = new Bundle();
//				bundle.putString("content", content);
//				
//				Map<String, Object> photoes = new HashMap<String, Object>();
//				photoes.put("filename", in);
//				
//				String jsonResult = kaixin.uploadContent(context,
//						RESTAPI_INTERFACE_POSTRECORD, bundle, photoes);
//
//				if (jsonResult == null) {
//					listener.onAuthError(null);
//				} else {
//					KaixinError kaixinError = Util.parseRequestError(jsonResult);
//					if (kaixinError != null) {
//						listener.onAuthError(null);
//					} else {
//						long rid = getRecordID(jsonResult);
//						if (rid > 0) {
//							listener.onAuthComplete(null);
//						} else {
//							listener.onAuthError(null);
//						}
//					}
//				}
//			} catch (MalformedURLException e1) {
//				listener.onAuthError(null);
//			} catch (IOException e1) {
//				listener.onAuthError(null);
//			} catch (Exception e1) {
//				listener.onAuthError(null);
//			}
//			return 1;
//		}
//
//		private long getRecordID(String jsonResult) throws JSONException {
//			JSONObject jsonObj = new JSONObject(jsonResult);
//			if (jsonObj == null) {
//				return 0;
//			}
//
//			long rid = jsonObj.optInt("rid");
//			return rid;
//		}
//
//		protected void onPostExecute(Integer result) {
//			
//		}
//	}
}
