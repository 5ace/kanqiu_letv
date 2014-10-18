package com.letv.watchball.parser;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import com.letv.http.parse.LetvMainParser;
import com.letv.watchball.bean.RealLink;
/**
 * 解析真实的播放地址
 * @author 
 *
 */
public class LiveRealParser  extends LetvMainParser<RealLink , String>{
	@Override
	public RealLink parse(String data) throws Exception {
		// TODO Auto-generated method stub
		RealLink result = new RealLink();
		JSONObject object = new JSONObject(data);
		if(object.has("nodelist")) {
			JSONArray nodeList = object.getJSONArray("nodelist");
			for(int i = 0; i < nodeList.length(); i ++) {
				JSONObject o = nodeList.getJSONObject(i);
				if(has(o, "location")) {
					String location = nodeList.getJSONObject(i).getString("location");
					if(!TextUtils.isEmpty(location)) {
						result.setLocation(location);
						return result;
					}
				}
			}
		}
		return null;
	}

	@Override
	protected boolean canParse(String data) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected String getData(String data) throws Exception {
		// TODO Auto-generated method stub
		return data;
	}
}

/**

{
remote: "10.58.107.34",
host: "10.58.107.34",
ipstart: "10.0.0.0",
ipend: "10.255.255.255",
geo: "CN.1.0.2",
desc: "中国-北京市-未知地区-联通",
buss: "buss=26,alv=21,qos=4,host=1,port=80",
level: 61,
usep2p: 1,
flag: "0",
expect: 3,
actual: 3,
needtest: 0,
curtime: 1379920323,
starttime: 1379833984,
endtime: 0,
cliptime: 6,
timeshift: 24,
dir: "m3u8/tianjin",
cdnpath: "leflv/tianjin",
liveflv: 1,
livehls: 1,
livep2p: 1,
livertmp: 0,
livesftime: 60,
livesfmust: 0,
forcegslb: 600,
nodelist: [
{
gone: 751,
pool: "SD-JN-CNC2",
detail: "751,750",
playlevel: 1,
slicetime: 480,
leavetime: 90,
location: "http://119.188.122.78/m3u8/tianjin/desc.m3u8?tag=live&video_type=m3u8&stream_id=tianjin&useloc=0&mslice=3&path=119.188.122.39,60.217.237.199,60.217.237.225&geo=CN-1-0-2&cips=10.58.107.34&tmn=1379920323&pnl=751,750,225&sign=live_phone&platid=10&playid=1&termid=2&pay=0&tm=1380004314&splatid=1003&ostype=andriod&hwtype=un&key=224654654"
},
{
gone: 750,
pool: "SD-JN-CNC1",
detail: "750,750",
playlevel: 1,
slicetime: 473,
leavetime: 89,
location: "http://60.217.237.164/m3u8/tianjin/desc.m3u8?tag=live&video_type=m3u8&stream_id=tianjin&useloc=0&mslice=3&path=60.217.237.167,60.217.237.224&geo=CN-1-0-2&cips=10.58.107.35&tmn=1379920323&pnl=750,750,225&sign=live_phone&platid=10&playid=1&termid=2&pay=0&tm=1380004314&splatid=1003&ostype=andriod&hwtype=un&key=224654654"
},
{
gone: 730,
pool: "SD-QD-CNC2",
detail: "730,730",
playlevel: 1,
slicetime: 433,
leavetime: 85,
location: "http://119.167.147.46/m3u8/tianjin/desc.m3u8?tag=live&video_type=m3u8&stream_id=tianjin&useloc=0&mslice=3&path=119.167.147.44,119.167.147.98&geo=CN-1-0-2&cips=10.58.107.36&tmn=1379920323&pnl=730,730,212&sign=live_phone&platid=10&playid=1&termid=2&pay=0&tm=1380004314&splatid=1003&ostype=andriod&hwtype=un&key=224654654"
}
]
}

**/