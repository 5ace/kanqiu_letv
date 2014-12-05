package com.letv.watchball.ui;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.letv.watchball.LetvApplication;
import com.letv.watchball.R;
import com.letv.watchball.bean.AlbumNew;
import com.letv.watchball.bean.Game;
import com.letv.watchball.utils.TextUtil;

public class IntroductionBuilder {

	public static void build(AlbumNew album, View root, Game game) {
		if (album == null || root == null) {
			return;
		}

		TextView title = (TextView) root.findViewById(R.id.introduction_title);
		TextView score = (TextView) root.findViewById(R.id.introduction_score);
		TextView info01 = (TextView) root
				.findViewById(R.id.introduction_info01);
		TextView info02 = (TextView) root
				.findViewById(R.id.introduction_info02);
		TextView info03 = (TextView) root
				.findViewById(R.id.introduction_info03);
		TextView info04 = (TextView) root
				.findViewById(R.id.introduction_info04);
		TextView info05 = (TextView) root
				.findViewById(R.id.introduction_info05);
		TextView intro = (TextView) root.findViewById(R.id.introduction_intro);
		if (game != null && game.status == 1) {// 直播
			title.setTextAppearance(LetvApplication.getInstance()
					.getApplicationContext(), R.style.letv_text_12_ff000000);
			title.setSingleLine(false);
			title.setMaxLines(3);
			// String[] split = game.playDate.split(".");
			// StringBuilder stringBuilder = new StringBuilder();
			// String[] split1 = split[2].split("周");
			// stringBuilder.append(split[0]).append("年").append(split[1]).append("月").append(split1[0]).append("日 周").append(split1[1]);
			title.setText(game.level + "-" + game.matchName + "-" + game.home
					+ "VS" + game.guest + " (" + game.playDate + " "
					+ game.playTime + ")");
			title.setVisibility(View.VISIBLE);
			score.setVisibility(View.GONE);
			info01.setVisibility(View.GONE);
			info02.setVisibility(View.GONE);
			info03.setVisibility(View.GONE);
			info04.setVisibility(View.GONE);
			info05.setVisibility(View.GONE);
			intro.setVisibility(View.GONE);
			return;
		}
		title.setTextAppearance(LetvApplication.getInstance()
				.getApplicationContext(), R.style.letv_text_15_ff000000);
		switch (album.getCid()) {
		case AlbumNew.Channel.TYPE_TV:
			title.setText(album.getNameCn());
			score.setText(String.valueOf(album.getScore()));
			score.setVisibility(View.VISIBLE);
			info01.setText(TextUtil.getString(R.string.detail_episode,
					album.getNowEpisodes(), album.getEpisode()));
			info02.setText(TextUtil.getString(R.string.detail_director,
					album.getDirectory()));
			info03.setText(TextUtil.getString(R.string.detail_starring,
					album.getStarring()));
			info04.setText(TextUtil.getString(R.string.detail_years,
					album.getReleaseDate()));
			info05.setText(TextUtil.getString(R.string.detail_type,
					album.getAlbumType()));
			intro.setText(TextUtil.getString(R.string.detail_synopsis,
					album.getDescription()));
			break;
		case AlbumNew.Channel.TYPE_MOVIE:
			title.setText(album.getNameCn());
			score.setText(String.valueOf(album.getScore()));
			score.setVisibility(View.VISIBLE);
			info01.setText(TextUtil.getString(R.string.detail_director,
					album.getDirectory()));
			info02.setText(TextUtil.getString(R.string.detail_starring,
					album.getStarring()));
			info03.setText(TextUtil.getString(R.string.detail_years,
					album.getReleaseDate()));
			info04.setText(TextUtil.getString(R.string.detail_area,
					album.getArea()));
			info05.setText(TextUtil.getString(R.string.detail_type,
					album.getAlbumType()));
			intro.setText(TextUtil.getString(R.string.detail_synopsis,
					album.getDescription()));
			break;
		case AlbumNew.Channel.TYPE_CARTOON:
			title.setText(album.getNameCn());
			score.setText(String.valueOf(album.getScore()));
			score.setVisibility(View.VISIBLE);
			info01.setText(TextUtil.getString(R.string.detail_episode,
					album.getNowEpisodes(), album.getEpisode()));
			info02.setText(TextUtil.getString(R.string.detail_tag,
					album.getTag()));
			info03.setText(TextUtil.getString(R.string.detail_area,
					album.getArea()));
			info04.setText(TextUtil.getString(R.string.detail_years,
					album.getReleaseDate()));
			info05.setText(TextUtil.getString(R.string.detail_type,
					album.getAlbumType()));
			intro.setText(TextUtil.getString(R.string.detail_synopsis,
					album.getDescription()));
			break;
		case AlbumNew.Channel.TYPE_JOY:
			title.setText(album.getNameCn());
			score.setVisibility(View.GONE);
			info01.setText(TextUtil.getString(R.string.detail_tag,
					album.getTag()));
			info02.setText(TextUtil.getString(R.string.detail_area,
					album.getArea()));
			info03.setText(TextUtil.getString(R.string.detail_type,
					album.getAlbumType()));
			info04.setVisibility(View.GONE);
			info05.setVisibility(View.GONE);
			intro.setText(TextUtil.getString(R.string.detail_synopsis,
					album.getDescription()));
			break;
		case AlbumNew.Channel.TYPE_TVSHOW:
			title.setText(album.getNameCn());
			score.setText(String.valueOf(album.getScore()));
			score.setVisibility(View.VISIBLE);
			info01.setText(TextUtil.getString(R.string.detail_tag,
					album.getTag()));
			info02.setText(TextUtil.getString(R.string.detail_total,
					album.getNowEpisodes()));
			info03.setText(TextUtil.getString(R.string.detail_area,
					album.getArea()));
			info04.setText(TextUtil.getString(R.string.detail_type,
					album.getSubCategory()));
			info05.setVisibility(View.GONE);
			intro.setText(TextUtil.getString(R.string.detail_synopsis,
					album.getDescription()));
			break;
		case AlbumNew.Channel.TYPE_PE:
			title.setText(album.getNameCn());
			score.setVisibility(View.GONE);
			info01.setText(TextUtil.getString(R.string.detail_tag,
					album.getTag()));
			info02.setText(TextUtil.getString(R.string.detail_type,
					album.getSubCategory()));
			info03.setVisibility(View.GONE);
			info04.setVisibility(View.GONE);
			info05.setVisibility(View.GONE);
			intro.setText(TextUtil.getString(R.string.detail_synopsis,
					album.getDescription()));
			break;
		case AlbumNew.Channel.TYPE_DOCUMENT_FILM:
			title.setText(album.getNameCn());
			score.setText(String.valueOf(album.getScore()));
			score.setVisibility(View.VISIBLE);
			info01.setText(TextUtil.getString(R.string.detail_episode,
					album.getNowEpisodes(), album.getEpisode()));
			info02.setText(TextUtil.getString(R.string.detail_tag,
					album.getTag()));
			info03.setText(TextUtil.getString(R.string.detail_type,
					album.getSubCategory()));
			info04.setVisibility(View.GONE);
			info05.setVisibility(View.GONE);
			intro.setText(TextUtil.getString(R.string.detail_synopsis,
					album.getDescription()));
			break;
		case AlbumNew.Channel.TYPE_LETV_MAKE:
			title.setText(album.getNameCn());
			score.setText(String.valueOf(album.getScore()));
			score.setVisibility(View.VISIBLE);
			info01.setText(TextUtil.getString(R.string.detail_episode,
					album.getNowEpisodes(), album.getEpisode()));
			info02.setText(TextUtil.getString(R.string.detail_tag,
					album.getTag()));
			info03.setText(TextUtil.getString(R.string.detail_type,
					album.getAlbumType()));
			info04.setVisibility(View.GONE);
			info05.setVisibility(View.GONE);
			intro.setText(TextUtil.getString(R.string.detail_synopsis,
					album.getDescription()));
			break;
		case AlbumNew.Channel.TYPE_OPEN_CLASS:
			title.setText(album.getNameCn());
			score.setText(String.valueOf(album.getScore()));
			score.setVisibility(View.VISIBLE);
			info01.setText(TextUtil.getString(R.string.detail_lecturer,
					album.getInstructor()));
			info02.setText(TextUtil.getString(R.string.detail_school,
					album.getSchool()));
			info03.setText(TextUtil.getString(R.string.detail_discipline,
					album.getSchool()));
			info04.setText(TextUtil.getString(R.string.detail_area,
					album.getArea()));
			info05.setText(TextUtil.getString(R.string.detail_language,
					album.getLanguage()));
			intro.setText(TextUtil.getString(R.string.detail_synopsis,
					album.getDescription()));
			break;
		case AlbumNew.Channel.TYPE_FASHION:
			title.setText(album.getNameCn());
			score.setText(String.valueOf(album.getScore()));
			score.setVisibility(View.VISIBLE);
			info01.setText(TextUtil.getString(R.string.detail_area,
					album.getArea()));
			info02.setText(TextUtil.getString(R.string.detail_tag,
					album.getTag()));
			info03.setText(TextUtil.getString(R.string.detail_type,
					album.getSubCategory()));
			info04.setVisibility(View.GONE);
			info05.setVisibility(View.GONE);
			intro.setText(TextUtil.getString(R.string.detail_synopsis,
					album.getDescription()));
			break;
		case AlbumNew.Channel.TYPE_CAR:
			title.setText(album.getNameCn());
			score.setVisibility(View.GONE);
			info01.setText(TextUtil.getString(R.string.detail_tag,
					album.getTag()));
			info02.setText(TextUtil.getString(R.string.detail_type,
					album.getSubCategory()));
			info03.setVisibility(View.GONE);
			info04.setVisibility(View.GONE);
			info05.setVisibility(View.GONE);
			intro.setText(TextUtil.getString(R.string.detail_synopsis,
					album.getDescription()));
			break;
		case AlbumNew.Channel.TYPE_TOURISM:
			title.setText(album.getNameCn());
			score.setVisibility(View.GONE);
			info01.setText(TextUtil.getString(R.string.detail_tag,
					album.getTag()));
			info02.setText(TextUtil.getString(R.string.detail_theme,
					album.getSubCategory()));
			info03.setText(TextUtil.getString(R.string.detail_type,
					album.getTravelType()));
			info04.setVisibility(View.GONE);
			info05.setVisibility(View.GONE);
			intro.setText(TextUtil.getString(R.string.detail_synopsis,
					album.getDescription()));
			break;
		case AlbumNew.Channel.TYPE_FINANCIAL:
			title.setText(album.getNameCn());
			score.setVisibility(View.GONE);
			info01.setText(TextUtil.getString(R.string.detail_tag,
					album.getTag()));
			info02.setText(TextUtil.getString(R.string.detail_type,
					album.getAlbumType()));
			info03.setVisibility(View.GONE);
			info04.setVisibility(View.GONE);
			info05.setVisibility(View.GONE);
			intro.setText(TextUtil.getString(R.string.detail_synopsis,
					album.getDescription()));
			break;
		}
	}

	public static void clear(View root) {
		if (root == null) {
			return;
		}

		TextView title = (TextView) root.findViewById(R.id.introduction_title);
		TextView score = (TextView) root.findViewById(R.id.introduction_score);
		TextView info01 = (TextView) root
				.findViewById(R.id.introduction_info01);
		TextView info02 = (TextView) root
				.findViewById(R.id.introduction_info02);
		TextView info03 = (TextView) root
				.findViewById(R.id.introduction_info03);
		TextView info04 = (TextView) root
				.findViewById(R.id.introduction_info04);
		TextView info05 = (TextView) root
				.findViewById(R.id.introduction_info05);
		TextView intro = (TextView) root.findViewById(R.id.introduction_intro);

		title.setText(null);
		score.setText(null);
		info01.setText(null);
		info02.setText(null);
		info03.setText(null);
		info04.setText(null);
		info05.setText(null);
		intro.setText(null);

		title.setVisibility(View.GONE);
		score.setVisibility(View.GONE);
		info01.setVisibility(View.GONE);
		info02.setVisibility(View.GONE);
		info03.setVisibility(View.GONE);
		info04.setVisibility(View.GONE);
		info05.setVisibility(View.GONE);
		intro.setVisibility(View.GONE);

	}
}
