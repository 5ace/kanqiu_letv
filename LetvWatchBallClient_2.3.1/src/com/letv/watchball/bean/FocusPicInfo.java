package com.letv.watchball.bean;

import android.text.TextUtils;
import com.letv.watchball.utils.TextUtil;

/**
 * 视频新闻页焦点图
 * 
 * @author liuhanzhi
 * 
 */
public class FocusPicInfo extends Base {
	public Body body;

	public class Body {

		public FocusPic[] focuspic;

	}

	public class FocusPic {
		public String vid;

		public long getVid() {
			if (!TextUtils.isEmpty(vid)) {
				return Long.parseLong(vid);
			}
			return -1;
		}

		public void setVid(String vid) {
			this.vid = vid;
		}

		public long getPid() {
			if (!TextUtils.isEmpty(pid)) {
				return Long.parseLong(pid);
			}

			return -1;

		}

		public void setPid(String pid) {
			this.pid = pid;
		}

		public String getNameCn() {
			return nameCn;
		}

		public void setNameCn(String nameCn) {
			this.nameCn = nameCn;
		}

		public String getSubTitle() {
			return subTitle;
		}

		public void setSubTitle(String subTitle) {
			this.subTitle = subTitle;
		}

		public int getCid() {
			if (cid != null) {
				return Integer.parseInt(cid);
			}
			return 0;
		}

		public void setCid(String cid) {
			this.cid = cid;
		}

		public int getType() {
			return Integer.parseInt(type);
		}

		public void setType(String type) {
			this.type = type;
		}

		public int getAt() {
			return Integer.parseInt(at);
		}

		public void setAt(String at) {
			this.at = at;
		}

		public String getPic() {
			return pic;
		}

		public void setPic(String pic) {
			this.pic = pic;
		}

		public String getPic_200_150() {
			return pic_200_150;
		}

		public void setPic_200_150(String pic_200_150) {
			this.pic_200_150 = pic_200_150;
		}

		public String pid;
		public String nameCn;
		public String subTitle;
		public String cid;
		public String type;
		public String at;
		public String pic;
		public String pic_200_150;

	}
}
