package com.weijie.firerunning.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.weijie.firerunning.bean.Plan;
import com.weijie.firerunning.bean.PlanBase;
import com.weijie.firerunning.bean.PlanDay;
import com.weijie.firerunning.bean.PlanWeek;
import com.weijie.firerunning.bean.RunRecord;
import com.weijie.firerunning.util.DateUtil;


public class DataBaseUtil {

	private static DataBaseUtil dbUtil;
	private DBOpenHelper dbHelper;
	private SQLiteDatabase rdb,wdb;
	
	private DataBaseUtil() {}

	public static DataBaseUtil getInstance() {
		if(dbUtil==null) {
			dbUtil = new DataBaseUtil();
		}
		return dbUtil;
	}

	public void setContext(Context context) {
		if(dbHelper == null) {
			dbHelper = new DBOpenHelper(context);
		}
	}

	/******************RunRecord Start**************************/

	public boolean deleteRunRecord(int id) {
		if(dbHelper!=null && wdb==null) {
			wdb = dbHelper.getWritableDatabase();
		}
		int row = wdb.delete("run_record", " _id=? ", new String[] {id+""});
		if(row>0) {
			return true; 
		} else {
			return false; 
		}
	}

	public boolean checkRunRecord(String id) {
		if(dbHelper!=null && rdb==null) {
			rdb = dbHelper.getReadableDatabase();
		}
		Cursor cursor = rdb.rawQuery("select count(*) from run_record where _id like "+id, null);
		if(cursor.moveToFirst()) {
			int count = cursor.getInt(0);
			if(count==0) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	public boolean insertRunRecord(RunRecord runRecord) {
		if(checkRunRecord(runRecord.id)) {
			return false;
		}
		if(dbHelper!=null && wdb==null) {
			wdb = dbHelper.getWritableDatabase();
		}
		ContentValues values = new ContentValues();
		values.put("_id", runRecord.id);
		values.put("points", runRecord.points);
		values.put("kTime", runRecord.kTime);
		values.put("distance", runRecord.distance);
		values.put("startTime", runRecord.startTime);
		values.put("saveTime", runRecord.saveTime);
		values.put("endTime", runRecord.endTime);
		values.put("timeLength", runRecord.timeLength);
		values.put("highSpeed", runRecord.highSpeed);
		values.put("lowSpeed", runRecord.lowSpeed);
		long id = wdb.insert("run_record", null, values);
		values.clear();
		if(id>0) {
			return true;
		} else {
			return false;
		}
	}

	public List<RunRecord> getRunRecords() {
		if(dbHelper!=null && rdb==null) {
			rdb = dbHelper.getReadableDatabase();
		}
		List<RunRecord> runRecords = new ArrayList<RunRecord>();
		Cursor cursor = rdb.query("run_record", null, " _id like ?", new String[] { "529844698%" }, null, null, " saveTime desc ");
		cursor.moveToFirst();// 将游标移动到初始位置  
		for (int i = 0; i < cursor.getCount(); i++) {
			RunRecord runRecord = new RunRecord();
			runRecord.id = cursor.getString(cursor.getColumnIndex("_id"));
			runRecord.points = cursor.getString(cursor.getColumnIndex("points"));
			runRecord.kTime = cursor.getString(cursor.getColumnIndex("kTime"));
			runRecord.distance = cursor.getDouble(cursor.getColumnIndex("distance"));
			runRecord.startTime = cursor.getString(cursor.getColumnIndex("startTime"));
			runRecord.saveTime = cursor.getLong(cursor.getColumnIndex("saveTime"));
			runRecord.endTime = cursor.getString(cursor.getColumnIndex("endTime"));
			runRecord.timeLength = cursor.getString(cursor.getColumnIndex("timeLength"));
			runRecord.highSpeed = cursor.getString(cursor.getColumnIndex("highSpeed"));
			runRecord.lowSpeed = cursor.getString(cursor.getColumnIndex("lowSpeed"));
			runRecords.add(runRecord);
			cursor.moveToNext();// 将游标移到下一行  
		}
		cursor.close();
		return runRecords;
	}
	/******************RunRecord End**************************/


	/******************Plan Start**************************/
	public boolean deletePlan(int id) {
		if(dbHelper!=null && wdb==null) {
			wdb = dbHelper.getWritableDatabase();
		}
		int row = wdb.delete("plan", " pId=? ", new String[] {id+""});
		if(row>0) {
			return true; 
		} else {
			return false; 
		}
	}

	public boolean checkPlan(String id) {
		if(dbHelper!=null && rdb==null) {
			rdb = dbHelper.getReadableDatabase();
		}
		Cursor cursor = rdb.rawQuery("select count(*) from plan where pId like "+id, null);
		if(cursor.moveToFirst()) {
			int count = cursor.getInt(0);
			if(count==0) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	public boolean insertPlan(Plan plan) {
		if(checkPlan(plan.pId)) {
			return false;
		}
		if(dbHelper!=null && wdb==null) {
			wdb = dbHelper.getWritableDatabase();
		}
		ContentValues values = new ContentValues();
		values.put("pId", plan.pId);
		values.put("type", plan.type);
		values.put("title", plan.title);
		values.put("matchDate", plan.matchDate);
		values.put("startDate", plan.startDate);
		Log.e("time", "marchDate"+plan.matchDate+"  startDate="+plan.startDate);
		values.put("weeks", plan.weeks);
		values.put("maxDistance", plan.maxDistance);
		values.put("distance", plan.distance);
		values.put("state", plan.state);
		long id = wdb.insert("plan", null, values);
		values.clear();
		if(id>0) {
			insertPlanWeek(plan);
			return true;
		} else {
			return false;
		}
	}

	private void insertPlanWeek(Plan plan) {
		if(dbHelper!=null && wdb==null) {
			wdb = dbHelper.getWritableDatabase();
		}
		List<PlanWeek> weeks = new ArrayList<PlanWeek>();
		ContentValues values = new ContentValues();
		if(plan.type==0) {
			weeks.add(new PlanWeek(plan.pId+"_0", plan.pId, 0, "0", "热身"));
			weeks.add(new PlanWeek(plan.pId+"_1", plan.pId, 1, "0", "建立基础"));
			weeks.add(new PlanWeek(plan.pId+"_2", plan.pId, 2, "0", "锻炼力量"));
			weeks.add(new PlanWeek(plan.pId+"_3", plan.pId, 3, "0", "提高强度"));
			weeks.add(new PlanWeek(plan.pId+"_4", plan.pId, 4, "0", "维持体能"));
			weeks.add(new PlanWeek(plan.pId+"_5", plan.pId, 5, "0", "密集训练周"));
			weeks.add(new PlanWeek(plan.pId+"_6", plan.pId, 6, "0", "减量训练"));
			weeks.add(new PlanWeek(plan.pId+"_7", plan.pId, 7, "0", "比赛周"));
		} else if(plan.type==1) {
			weeks.add(new PlanWeek(plan.pId+"_0", plan.pId, 0, "1", "热身"));
			weeks.add(new PlanWeek(plan.pId+"_1", plan.pId, 1, "1", "扎实基础"));
			weeks.add(new PlanWeek(plan.pId+"_2", plan.pId, 2, "1", "建立基础"));
			weeks.add(new PlanWeek(plan.pId+"_3", plan.pId, 3, "1", "锻炼力量"));
			weeks.add(new PlanWeek(plan.pId+"_4", plan.pId, 4, "1", "锻炼力量"));
			weeks.add(new PlanWeek(plan.pId+"_5", plan.pId, 5, "1", "速度提升"));
			weeks.add(new PlanWeek(plan.pId+"_6", plan.pId, 6, "1", "挑战自我极限"));
			weeks.add(new PlanWeek(plan.pId+"_7", plan.pId, 7, "1", "提高强度"));
			weeks.add(new PlanWeek(plan.pId+"_8", plan.pId, 8, "1", "密集训练周"));
			weeks.add(new PlanWeek(plan.pId+"_9", plan.pId, 9, "1", "维持体能"));
			weeks.add(new PlanWeek(plan.pId+"_10", plan.pId, 10, "1", "减量训练"));
			weeks.add(new PlanWeek(plan.pId+"_11", plan.pId, 11, "1", "比赛周"));
		}

		int pos = 0;
		for(PlanWeek week:weeks) {
			values.put("p_w_Id", week.pwId);
			values.put("pId", week.pId);
			values.put("w_number", week.w_number);
			values.put("planType", week.planType);
			values.put("weekDesc", week.weekDesc);
			values.put("maxDistance", week.maxDistance);
			values.put("distance", week.distance);
			wdb.insert("plan_week", null, values);
			//insertPlanDay(week,DateUtil.stringToDate(DateUtil.FORMAT_YMD_HM, plan.startDate).getTime()+pos*7*(1000*24*3600));
			insertPlanDay(week,Long.parseLong(dateAdd(DateUtil.stringToDate(DateUtil.FORMAT_YMD_HM, plan.startDate).getTime(),pos*7)));
			values.clear();
			pos++ ;
		}
	}

	public static final String INTERVAL = ":";

	private void insertPlanDay(PlanWeek planWeek,long date) {
		List<PlanDay> planDays = new ArrayList<PlanDay>();
		ContentValues values = new ContentValues();
		if(planWeek.planType.equals("0")) {
			switch (planWeek.w_number) {
			case 0:
				planDays.add(new PlanDay(0, 6.4f, "第一天。以6.4公里的轻松跑开始你的10公里训练，并提高配速跑完最后一程。", "用舒服轻松的配速跑完4.8公里。"+INTERVAL+"加快配速跑完最后的1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(1, 9.7f, "为了提升你比赛成绩并打破你的个人记录，速度训练是必要的。今天，全力进行第一次速度训练，并且做好准备迎战明天的跑步。今天的训练是9.7公里“法特雷克法”跑。", "开始先跑1.6公里的热身轻松跑。"+INTERVAL+"尝试加入“法特雷克跑法”。每2分钟交替加快和放慢配速。这样持续进行，直到跑完4.8公里。"+INTERVAL+"接着用轻松缓和的配速跑完最后的3.2公里。", planWeek.pwId));
				planDays.add(new PlanDay(2, 8f, "今天，在完成8公里跑的同时保留精力。保持轻松的配速，慢慢从容地训练。", "用轻松舒适的配速跑8公里。", planWeek.pwId));
				planDays.add(new PlanDay(3, -1f, "进行任何进阶训练计划时，你将不仅需要着重跑步，同时也需要注意整体适能状况。交叉训练时让跑步成果更为完美的好方法，今天就好好进行交叉训练，并且做好准备迎接明天的跑步。", "交叉训练日。试试骑自行车、游泳、打羽毛球、做瑜伽。", planWeek.pwId));
				planDays.add(new PlanDay(4, 6.4f, "今天的6.4公里跑重点在于维持体能及为明天的长跑热身。保留精力，并在最后一程提高配速。", "用舒适轻松的配速跑4.8公里。"+INTERVAL+"加快配速跑完最后的1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(5, 13f, "今天，挑战你的初次长跑。以舒服的配速跑，同时记得明天可以休息。", "用轻松舒适的配速跑13公里。", planWeek.pwId));
				planDays.add(new PlanDay(6, 0f, "今天需要充分休息，恢复体能状态。明天将开始你的下一个训练周。", "好好享受休息日吧！去散步舒展筋骨，养精蓄锐，以最好的状态迎接明天的跑步日。", planWeek.pwId));
				break;
			case 1:
				planDays.add(new PlanDay(0, 8f, "进行8公里放松跑，开始本周的训练。你懂的。完成此次跑步，并为明天的“法特雷克跑法”训练做好准备。", "用舒服轻松的配速跑完6.4公里。"+INTERVAL+"加快配速跑完最后的1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(1, 9.7f, "今天的9.7公里“法特雷克跑法”训练将挑战你的速度并锻炼你的耐力。坦然接受吧。这类训练能够让你为比赛日做好准备。", "开始先跑1.6公里的热身轻松跑。"+INTERVAL+"尝试加入“法特雷克跑法”。每2分钟交替加快和放慢配速。这样持续进行，知道跑完4.8公里。"+INTERVAL+"接着用轻松缓和的配速跑完最后的3.2公里。", planWeek.pwId));
				planDays.add(new PlanDay(2, 11f, "今天，完成一次11公里跑并保持舒适的配速。慢慢来，保留体力。明天也是跑步日。", "用轻松舒适的配速跑完11公里。", planWeek.pwId));
				planDays.add(new PlanDay(3, 4.8f, "今天以轻松的配速完成4.8公里跑。这些跑步能帮助你为后续的训练计划，建立扎实的基础。", "用轻松舒适的配速跑4.8公里。", planWeek.pwId));
				planDays.add(new PlanDay(4, 6.4f, "以今天的6.4公里跑来维持体能。保留精力，迎接明天的长距离训练。", "用舒服轻松的配速跑完4.8公里。"+INTERVAL+"加快配速跑完最后的1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(5, 12f, "今天进行本次长途12公里跑。慢慢来并以可持续的配速跑完。", "用轻松舒服的配速跑12公里。", planWeek.pwId));
				planDays.add(new PlanDay(6, 0f, "今天需要充分休息，恢复体能状态。明天将开始你的下一个训练周。", "好好享受休息日吧！去散步舒展筋骨，养精蓄锐，以最好的状态迎接明天的跑步日。", planWeek.pwId));
				break;
			case 2:
				planDays.add(new PlanDay(0, 8f, "进行8公里放松跑，开始一周的训练。加快配速跑完最后一程，并为明天的渐进跑热身。", "用舒适轻松的配速跑6.4公里。"+INTERVAL+"加快配速跑完最后的1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(1, 13f, "为了让你的10公里跑步成绩更上一层楼，培养耐力对你比赛日表现至关重要。练习13公里渐进跑，是培养耐力的最佳选择。准备好去体验脂肪燃烧的感觉。", "用轻松舒适的配速跑3.2公里。"+INTERVAL+"跑8公里，并在跑步过程中提高配速。"+INTERVAL+"接着用轻松缓和的配速跑完最后的1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(2, 11f, "今天进行13公里跑，但是不要担心你的配速。距离才是重点。", "以轻松舒适的配速跑11公里。", planWeek.pwId));
				planDays.add(new PlanDay(3, 4.8f, "今天的4.8公里跑只是为了继续增加你的里程。慢慢来，完成训练距离即可。本周的训练你已经完成了一半。", "用轻松舒适的配速跑4.8公里。", planWeek.pwId));
				planDays.add(new PlanDay(4, 6.4f, "今天保持冲劲，继续进行一次6.4公里跑。提高配速平跑完最后一程，准备迎接明天的长跑训练。", "以舒服轻松的配速跑4.8公里。"+INTERVAL+"加快配速跑完最后的1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(5, 15f, "今天的15公里跑是本周距离最长的跑步。距离更长，需要的耐力更强。慢慢来，以舒服的配速跑。明天休息一天，你受之无愧。", "用轻松舒适的配速跑15公里。", planWeek.pwId));
				planDays.add(new PlanDay(6, 0f, "今天休息一天让肌肉恢复，准备迎接下周的训练。", "好好享受休息日！快走并养精蓄锐，以最好的状态迎接明天的跑步日。", planWeek.pwId));
				break;
			case 3:
				planDays.add(new PlanDay(0, 8f, "以8公里放松跑开始你的一周训练。保持舒适的配速，并为本周稍后的10公里跑保留精力。", "以轻松舒适的配速跑8公里。", planWeek.pwId));
				planDays.add(new PlanDay(1, 6f, "要能全速快跑，必须先进行快速训练，这就是今天间歇性运动计划的训练内容。在6公里跑的每个间歇，尽量提高自己的强度。", "开始先跑1.6公里的热身轻松跑。"+INTERVAL+"尝试间歇跑。以更快的配速跑完1.2公里。"+INTERVAL+"这样持续进行，直到跑完0.4公里。"+INTERVAL+"以更快的配速跑完1.2公里。"+INTERVAL+"接着用轻松缓和的配速跑完最后1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(2, 9.7f, "今天，进行轻松的9.7公里跑，继续提高每周的距离。以舒适的配速跑，同时记得明天是休息日。", "用舒服轻松的配速跑完9.7公里。", planWeek.pwId));
				planDays.add(new PlanDay(3, 0f, "你的10公里跑即将开始，因此今天好好休息。休息一天能让你耳目一新并保持活力。", "好好享受休息日吧！快乐并养精蓄锐，以最好的状态迎接明天的跑步日。", planWeek.pwId));
				planDays.add(new PlanDay(4, 3.2f, "明天就是你的5公里跑了，因此今天的重点在于做好热身与进行轻松训练。好好睡一觉，并且为明天做好准备。", "为1.6公里进行热身。"+INTERVAL+"稍微加快配速，跑完1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(5, 6.4f, "今天要跑6.4公里。练习比赛配速以及奋力提高配速的感觉。", "开始先跑1.6公里的热身轻松跑。"+INTERVAL+"用控制的比赛配速跑3.2公里。"+INTERVAL+"接着尝试鞭策自己，加速到更快且有挑战性的配速，跑完最后1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(6, 0f, "今天休息一天，让你的肌肉得以放松。明天将开始你的下一个训练周。", "好好享受休息日！散步并养精蓄锐，以最好的状态迎接后面的训练。", planWeek.pwId));
				break;
			case 4:
				planDays.add(new PlanDay(0, 0f, "今天继续休息，让你的肌肉得以放松。明天将开始你的下一个训练周。", "好好享受休息日！散步并养精蓄锐，以最好的状态迎接后面的训练。 ", planWeek.pwId));
				planDays.add(new PlanDay(1, 15f, "今天，应进行15公里渐进跑。保留精力，慢慢来。你每日都在锻炼体力。", "用轻松舒服的配速跑15公里。", planWeek.pwId));
				planDays.add(new PlanDay(2, 9.7f, "新的一天。新的跑步日。今天以9.7公里跑来维持体能。提高配速跑完最后一程，你几乎已经完成了本周训练的一半。", "用舒服轻松的配速跑完8公里。"+INTERVAL+"加快配速跑完最后的1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(3, 4.8f, "今天，踏上跑道，完成一次4.8公里跑。放慢脚步。明天也是跑步日。", "用轻松舒服的配速跑完4.8公里。", planWeek.pwId));
				planDays.add(new PlanDay(4, 8f, "今天进行8公里放松跑。这将帮助你为明天将进行的本周长跑热身。密集训练周愈来愈近，你的状态也正处于顶峰。", "用舒服轻松的配速跑6.4公里。"+INTERVAL+"加快配速跑完最后1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(5, 15f, "今天的训练是15公里长跑。随着密集训练周即将到来，这正是为了训练计划中难度最高的训练周做好准备所需的训练。", "用轻松舒服的配速跑15公里。", planWeek.pwId));
				planDays.add(new PlanDay(6, 0f, "今天休息一天，让你的肌肉得以放松。明天将开始你的下一个训练周。", "好好享受休息日！散步并养精蓄锐，以最好的状态迎接后面的训练。", planWeek.pwId));
				break;
			case 5:
				planDays.add(new PlanDay(0, 8f, "进行8公里放松跑，开始密集训练周的训练。本次跑步也能为你热身并且做好准备，迎接明天充满挑战性的间歇性运动计划。", "用舒服轻松的配速跑6.4公里。"+INTERVAL+"加快配速跑完最后的1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(1, 6f, "今天的跑步时挑战速度与耐力的6公里间歇性训练。准备好。这是在10公里跑步中打破个人记录所需的练习。", "开始先跑1.6公里的热身轻松跑。"+INTERVAL+"尝试间歇跑。以更快的配速跑完1.2公里。"+INTERVAL+"这样持续进行，直到跑完0.4公里。"+INTERVAL+"以更快的配速跑完1.2公里。接着用轻松缓和的配速跑完最后1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(2, 13f, "今天，在本次13公里跑中保留精力。慢慢来，疲惫时放慢脚步。", "用舒服轻松的配速跑完13公里。", planWeek.pwId));
				planDays.add(new PlanDay(3, 4.8f, "保持密集训练周的冲劲，进行今天的4.8公里短跑。放慢脚步，跑完距离。明天也是跑步日。", "用轻松舒服的配速跑完4.8公里。", planWeek.pwId));
				planDays.add(new PlanDay(4, 6.4f, "今天，应进行6.4公里渐进跑。本次跑步将能维持你的锻炼出的体能，也可以为明天的长跑热身。", "用轻松舒服的配速跑4.8公里。"+INTERVAL+"加快配速跑1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(5, 19f, "今天的训练是19公里渐进跑。距离很长。也很辛苦。但今天之后，你就能对所有人说：我做到了！踏上赛道，向目标进发吧。", "用轻松舒适的配速跑3.2公里。"+INTERVAL+"跑14公里，并在跑步过程中提高配速。"+INTERVAL+"接着用轻松缓和的配速跑完最后1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(6, 0f, "减量训练周于明天开始，今天充分休息。随着比赛周的来临，现在开始，你将保持轻松训练。", "好好享受休息日吧！", planWeek.pwId));
				break;
			case 6:
				planDays.add(new PlanDay(0, 8f, "进行8公里放松跑，开始减量训练周的训练。这次跑步也可以为明天难度更高的耐力健身训练热身。", "用舒服轻松的配速跑6.4公里。"+INTERVAL+"加快配速跑完最后1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(1, 13f, "今天的13公里渐进跑将能挑战你的耐力并提高你的配速。这类的跑步能确保你不会再比赛中遭受挫败。", "用轻松舒适的配速跑3.2公里。"+INTERVAL+"跑8公里，并在跑步过程中提高配速。"+INTERVAL+"接着用轻松缓和的配速跑完最后的1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(2, 9.7f, "今天，进行轻松的9.7公里跑，继续提高每周的距离。以舒适的配速跑，同时记得明天是休息日。", "用舒服轻松的配速跑完9.7公里。", planWeek.pwId));
				planDays.add(new PlanDay(3, 4.8f, "今天的4.8公里跑只是为了继续增加你的里程。慢慢来，完成训练距离即可。本周的训练你已经完成了一半。", "用轻松舒服的配速跑完4.8公里。", planWeek.pwId));
				planDays.add(new PlanDay(4, 6.4f, "以今天的6.4公里放松跑来维持你已逐渐养成的体能。加快配速，并且做好准备迎接明天本周长距离训练。", "用轻松舒服的配速跑4.8公里。"+INTERVAL+"加快配速跑1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(5, 13f, "由于本周是减量训练周，只需要跑完13公里即可。在比赛周接近的同时，留意维持身体最佳状态并保持活力。", "用舒服轻松的配速跑完13公里。", planWeek.pwId));
				planDays.add(new PlanDay(6, 0f, "整个训练基本结束，今天充分休息。随着比赛周的来临，现在开始，你将保持轻松训练。", "好好享受休息日吧！", planWeek.pwId));
				break;
			case 7:
				planDays.add(new PlanDay(0, 8f, "进行8公里放松跑，开始比赛周的训练。做好足够的热身并以最好的状态迎接即将到来的比赛。", "用舒服轻松的配速跑6.4公里。"+INTERVAL+"加快配速跑完最后1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(1, 0f, "整个训练基本结束，今天充分休息。随着比赛周的来临，现在开始，你将保持轻松训练。", "好好享受休息日吧！", planWeek.pwId));
				planDays.add(new PlanDay(2, 6f, "以轻松舒服的速度跑8公里。只需热身即可，掌握节奏。", "用舒服轻松的配速跑6公里。", planWeek.pwId));
				planDays.add(new PlanDay(3, 0f, "整个训练基本结束，今天充分休息。随着比赛周的来临，现在开始，你将保持轻松训练。", "好好享受休息日吧！", planWeek.pwId));
				planDays.add(new PlanDay(4, 0f, "整个训练基本结束，今天充分休息。随着比赛周的来临，现在开始，你将保持轻松训练。", "好好享受休息日吧！", planWeek.pwId));
				planDays.add(new PlanDay(5, 3f, "比赛周第一天，以轻松舒服的速度跑8公里。只需热身即可，掌握节奏。", "用舒服轻松的配速跑3公里。", planWeek.pwId));
				planDays.add(new PlanDay(6, 10f, "今天是比赛日。你已训练有素，现在是你展现自我的时候，尽情奔跑，拿个好成绩。", "在赛场上奔驰吧！", planWeek.pwId));
				break;
			}
		} else if(planWeek.planType.equals("1")) {
			switch (planWeek.w_number) {
			case 0:
				planDays.add(new PlanDay(0, 8f, "今天，你将进行首次训练。通过8公里建立体能基础，以轻松慢跑为主。", "用轻松缓和的配速跑8公里。", planWeek.pwId));
				planDays.add(new PlanDay(1, 8f, "今天，你将进行首次训练。通过8公里建立体能基础，以轻松慢跑为主。", "用轻松缓和的配速跑8公里。", planWeek.pwId));
				planDays.add(new PlanDay(2, 8f, "今天，你将进行首次训练。通过8公里建立体能基础，以轻松慢跑为主。", "用轻松缓和的配速跑8公里。", planWeek.pwId));
				planDays.add(new PlanDay(3, -1f, "进行任何进阶训练计划时，你将不仅需要着重跑步，同时也需要注意整体适能状况。交叉训练时让跑步成果更为完美的好方法，今天就好好进行交叉训练，并且做好准备迎接明天的跑步。", "交叉训练日。试试骑自行车、游泳、打羽毛球、做瑜伽。", planWeek.pwId));
				planDays.add(new PlanDay(4, 6.4f, "通过6.4公里跑维持你已经建立的体能，并继续完成你的热身周。利用最后一程加速度，为明天你的首次长跑热身。", "用舒服轻松的配速跑4.8公里。"+INTERVAL+"加快配速跑完最后1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(5, 12.9f, "今天，你将完成你的首次长跑-注意以轻松的配速完成12.9公里。慢慢来，以舒服的配速跑。明天是你的首个休息日。", "用轻松舒适的配速跑12.9公里。", planWeek.pwId));
				planDays.add(new PlanDay(6, 0f, "今天是你的第一个休息日。暂停跑步一天，让身体状态恢复。快速步行，舒展筋骨。明天将开始你的下一个训练周。", "好好享受休息日！快走并做彻底的伸展运动，然后养精蓄锐，以最好的状态迎接明天的训练。", planWeek.pwId));
				break;
			case 1:
				planDays.add(new PlanDay(0, 8f, "今天，通过8公里跑来维持体能。加速跑完全程，并且做好准备迎接明天的速度训练。", "用舒服轻松的配速跑6.4公里。"+INTERVAL+"加快配速跑完最后1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(1, 9.65f, "以本周的速度训练继续锻炼你的基本体能。今天的9.65公里“法特雷克跑法”Fartlek跑能让你间歇性地提高配速。在整个训练期间不断鞭策自己，同时记得明天你将迎来另一个跑步日。", "开始先跑1.6公里的热身轻松跑。"+INTERVAL+"尝试加入“法特雷克跑法”Fartlek。加快配速跑4分钟，然后慢下来匀速再跑1分钟。这样持续进行，知道跑完4.8公里。"+INTERVAL+"接着用轻松缓和的配速跑完最后的3.2公里。", planWeek.pwId));
				planDays.add(new PlanDay(2, 9.7f, "保持这周的冲劲，今天继续跑9.7公里。慢慢来，保留体力。本周的训练你几乎完成了一半。", "用轻松舒适的配速跑9.7公里。", planWeek.pwId));
				planDays.add(new PlanDay(3, 4.8f, "今天，进行短距离的4.8公里跑，继续增加你的每周里程。保持轻松配速，完成4.8公里。", "用轻松舒适的配速跑4.8公里。", planWeek.pwId));
				planDays.add(new PlanDay(4, 6.4f, "今天，以6.4公里放松跑为明天的长跑热身。提高配速跑到终点，迎接明天首个里程突破。", "用舒适轻松的配速跑4.8公里。"+INTERVAL+"加快配速跑完最后的1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(5, 16f, "今天，以16公里长跑结束你的一周训练。你可以慢慢来，注意保持舒适的配速。", "用轻松舒适的配速跑完16公里。", planWeek.pwId));
				planDays.add(new PlanDay(6, 0f, "今天需要充分休息，恢复体能状态。本周你完成了部分扎实的基础训练，现在你已经做好准备开始建立强有力的体能，确保接下来的训练顺利进行。", "好好享受休息日吧！快走并彻底的伸展运动，然后养精蓄锐，以最好的状态迎接明天的跑步。", planWeek.pwId));
				break;
			case 2:
				planDays.add(new PlanDay(0, 8f, "今天，通过8公里跑训练，保持你的体能，并提高配速完美跑完最后一程。这将可帮你为明天的速度和耐力训练热身。", "用舒服轻松的配速跑6.4公里。"+INTERVAL+"加快配速跑完最后的1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(1, 11.3f, "今天的11.3公里渐进跑将帮助提高你的速度和耐力，你将在跑步期间不断提高你的配速。接近终点时，你应该用强度最高最快单不至于失控的配速跑完。", "用舒适轻松的配速跑4.8公里。"+INTERVAL+"跑4.8公里，并在跑步过程中提高配速。"+"接着用轻松缓和的配速跑完最后1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(2, 9.7f, "今天跑完9.7公里，继续建立坚实的基础。慢慢来，以轻松的配速跑。", "用轻松舒适的配速跑完9.7公里。", planWeek.pwId));
				planDays.add(new PlanDay(3, 6.4f, "今天继续保持本周的劲头，完成6.4公里，继续建立坚实的基础。慢慢来，以轻松的配速跑。", "用轻松舒适的配速跑完6.4公里。", planWeek.pwId));
				planDays.add(new PlanDay(4, 8f, "以今天的8公里跑来维持体能。加快配速跑完最后一程。明天的跑步是本周的一次长跑。", "用舒服轻松的配速跑完6.4公里。"+INTERVAL+"加快配速跑完最后的1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(5, 19.3f, "今天、打破训练至今的最长记录。慢慢来，完成每个里程碑。你几乎已经完成本周的训练。", "用舒服轻松的配速跑19.3公里。", planWeek.pwId));
				planDays.add(new PlanDay(6, 0f, "该是休息的日子了。今天舒展筋骨，通过快速步行放松肌肉。本周逆增加了跑量，建立了坚实的基础，为你之后的训练计划打下了基石。", "好好享受休息日吧！快走并彻底的伸展运动，然后养精蓄锐，以最好的状态迎接明天的跑步。", planWeek.pwId));
				break;
			case 3:
				planDays.add(new PlanDay(0, 8f, "进行8公里放松跑，开始本周的训练。加快速度跑到终点，并且做好准备迎接明天的间歇性训练。", "用轻松舒服的配速跑6.4公里。"+INTERVAL+"加快配速跑完最后1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(1, 6f, "今天的速度训练将在间隔期间鞭策加快配速。间歇性跑步可在整个跑步期间不断提高你的配速，从而帮助你锻炼体力。", "开始先跑1.6公里的热身轻松跑。"+INTERVAL+"尝试间歇跑。以更快的配速跑完1.2公里。"+INTERVAL+"以较慢的配速完成0.4公里。"+INTERVAL+"以更快的配速跑完1.2公里。"+INTERVAL+"接着用轻松缓和的配速跑完最后的1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(2, 11f, "今天，慢慢来，努力保留精力。明天也是跑步日。", "用轻松舒适的配速跑完11公里。", planWeek.pwId));
				planDays.add(new PlanDay(3, 8f, "今明两天的内容是连续两天跑步日。今天，保持轻松的配速，从容进行训练。明天，你将提高配速。", "用轻松舒适的配速跑完8公里。", planWeek.pwId));
				planDays.add(new PlanDay(4, 8f, "今天你将完成第二个8公里。这一次，加快速度跑完最后一程，为明天的长跑和渐进跑做好准备。", "用舒服轻松的配速跑6.4公里。"+INTERVAL+"加快配速跑完最后1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(5, 19f, "今天，完成这一富有挑战性的19公里渐进跑，继续锻炼你的体力。距离很长。挑战性十足。完成今天的训练后，即可期待明天的休息日。", "用轻松舒适的配速跑4.8公里。"+INTERVAL+"跑13公里，并在跑步过程中提高配速。"+INTERVAL+"接着用轻松缓和的配速跑完最后的1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(6, 0f, "今天，好好休息，让身体充分恢复。下周，你将努力锻炼耐力，因此要充分休息。", "好好享受休息日吧！快走并彻底的伸展运动，然后养精蓄锐，以最好的状态迎接明天的跑步。", planWeek.pwId));
				break;
			case 4:
				planDays.add(new PlanDay(0, 8f, "本周训练开始时一段8公里跑，用以维持体能并做好足够的热身，迎接明天的渐进跑训练。", "用舒服轻松的配速跑6.4公里。"+INTERVAL+"加快配速平跑完最后的1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(1, 13f, "今天，完成这一13公里渐进跑，继续锻炼你的耐力。努力保留精力，并在整个跑步期间提高你的配速。", "用轻松舒适的配速跑4.8公里。"+INTERVAL+"跑6.4公里，并在跑步中提高配速。接着用轻松缓和的配速跑完最后的1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(2, 11f, "今天，继续挑战极限，完成另一次11公里跑。慢慢来，放缓配速。", "用轻松的配速跑完11公里。", planWeek.pwId));
				planDays.add(new PlanDay(3, 0f, "在今天充分休息，为你的计时赛养精蓄锐。确保你筋骨舒展，并且睡眠充足。", "好好享受休息日吧！快走并彻底的伸展运动，然后养精蓄锐，以最好的状态迎接明天的跑步。", planWeek.pwId));
				planDays.add(new PlanDay(4, 3.2f, "今天，尝试3.2公里轻松短跑，做好准备迎接明天的计时赛。这将帮你放松肌肉，迎接明天的训练。", "开始先跑1.6公里热身轻松跑。"+INTERVAL+"用你习惯的速度跑0.8公里。"+INTERVAL+"接着用轻松缓和的配速跑完最后0.8公里。", planWeek.pwId));
				planDays.add(new PlanDay(5, 19f, "今天是你的19公里计时赛。你的目标很简单：以最快的速度跑完19公里。计时赛的平均速度快6秒，就接近得到你半程马拉松的配速。祝你好运，今天的重点是速度，拼尽全力。", "开始先跑1.6公里的热身跑。"+INTERVAL+"以保守的配速完成4.8公里。"+INTERVAL+"以你可以维持的配速跑6.4公里。"+INTERVAL+"以具有竞争力的较快配速跑完4.8公里。"+INTERVAL+"接着用自己最快的配速猛冲最后1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(6, 0f, "今天，好好休息，让身体充分恢复。下周，你将努力训练速度，因此要充分休息。", "好好享受休息日吧！快走并彻底的伸展运动，然后养精蓄锐。", planWeek.pwId));
				break;
			case 5:
				planDays.add(new PlanDay(0, 0f, "今天额外休息一天。", "好好享受休息日吧！快走并彻底的伸展运动，然后养精蓄锐。", planWeek.pwId));
				planDays.add(new PlanDay(1, 13f, "今天是速度提升周的一部分，你将通过13公里“法特雷克跑法”Fartlek训练你的速度。不断鞭策自己跑完全程。你的速度会日益提高。", "开始先跑1.6公里的热身轻松跑。"+INTERVAL+"尝试加入“法特雷克跑法”Fartlek。加快配速跑4分钟，然后慢下来匀速跑1分钟。这样持续进行，跑完9.7公里。"+INTERVAL+"接着用轻松缓和的配速跑完最后的1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(2, 11f, "以轻松配速跑完11公里，进行恢复。", "用轻松舒适的配速跑完11公里。", planWeek.pwId));
				planDays.add(new PlanDay(3, 8f, "今天，坚持完成本周训练，进行一次8公里。以舒适的配速跑步，注意保留体力。明天也是跑步日。", "用轻松舒适的配速跑完8公里。", planWeek.pwId));
				planDays.add(new PlanDay(4, 8f, "今天你将跑八公里，不过这次你将提高配速跑完最后一程。在接近终点的时候加速冲刺。", "用舒服轻松的配速跑6.4公里。"+INTERVAL+"加快配速跑完最后的1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(5, 19f, "今天的渐进跑通过19公里的长距离从测试你的速度。训练过程中尽力挑战高墙，把整体速度提上去。", "用轻松舒适的配速跑3.2公里。"+INTERVAL+"跑14公里，并在跑步中提高配速。"+INTERVAL+"最后1.6公里奋力猛冲。", planWeek.pwId));
				planDays.add(new PlanDay(6, 0f, "该是休息的日子了。你的胜利，名副其实。今天，充分舒展筋骨。", "好好享受休息日吧！快走并彻底的伸展运动，然后养精蓄锐，以最好的状态迎接明天的跑步。", planWeek.pwId));
				break;
			case 6:
				planDays.add(new PlanDay(0, 8f, "以8公里放松跑开始本周的训练，以此维持体能、放松肌肉，为明天的渐进跑做准备。", "用舒适轻松的配速跑6.4公里。"+INTERVAL+"加快配速跑完最后1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(1, 14f, "今天再完成一个渐进跑，挑战你的极限。这次是14公里。做好热身准备后，竭尽全力逐步提高配速。", "用轻松舒适的配速跑4.8公里。"+INTERVAL+"跑8公里，并逐渐提高配速。"+INTERVAL+"接着用轻松缓和的配速跑最后1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(2, 11f, "今天，以舒适的步伐跑11公里。", "用舒适轻松的配速跑11公里。", planWeek.pwId));
				planDays.add(new PlanDay(3, 8f, "今天一鼓作气，继续进行本次8公里跑。今天只需慢慢跑，以轻松的配速跑完就可以。", "以舒适轻松的配速跑完8公里。", planWeek.pwId));
				planDays.add(new PlanDay(4, 8f, "今天的跑步是你熟悉的8公里放松跑。利用最后一程加快配速，为明天的长跑做准备。", "用舒适轻松的配速跑6.4公里。"+INTERVAL+"加快配速跑1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(5, 23f, "今天你将刷新你的里程记录，测试自己的极限 - 一场怪兽级的23公里跑。这是一次距离很长、强度很大的跑步，也是拉开密集训练周大幕的最佳方式。今天慢慢来，以舒服的配速跑完。", "用轻松舒适的配速跑完23公里。", planWeek.pwId));
				planDays.add(new PlanDay(6, 0f, "今天是休息日，尝试到外面散步，排酸。", "快步走，彻底舒展筋骨。注意多休息，然后养精蓄锐，以最好的状态迎接明天的跑步。", planWeek.pwId));
				break;
			case 7:
				planDays.add(new PlanDay(0, 8f, "以8公里的放松跑开始一周的训练。让你最后一程的配速提高到新水平，为明天的训练做好准备。", "用舒服轻松的配速跑6.4公里。"+INTERVAL+"加快配速跑最后1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(1, 6f, "今天，通过一个6公里间歇性训练，提高你的训练强度。侧重于“冲刺”阶段保持快速，稳定的步伐，今天的训练时为密集训练周做准备的一次完美热身。", "开始先跑1.6公里热身轻松跑。"+INTERVAL+"尝试间歇跑。以更快的配速跑1.2公里。"+INTERVAL+"稍微放缓配速跑0.4公里。"+INTERVAL+"以更快的配速跑完1.2公里。"+INTERVAL+"接着用轻松缓和的配速跑完最后的1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(2, 11f, "今天，向你的每周距离再添11公里。本周的训练你几乎已经完成了一半。", "用轻松的配速跑11公里。", planWeek.pwId));
				planDays.add(new PlanDay(3, 8f, "保持这周的冲劲，继续完成者8公里。你已经积累了相当可观的里程数，所以务必保持舒适的配速，用最好的状态迎接本周稍后的长跑。", "用轻松的配速跑完8公里。", planWeek.pwId));
				planDays.add(new PlanDay(4, 8f, "利用今天的8公里放松跑作为热身，为明天富有挑战性的渐进跑做准备。提高配速跑到终点，并记得密集训练周即将开始。", "用轻松的配速跑6.4公里。"+INTERVAL+"加快配速跑完最后1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(5, 19f, "今天，通过一趟19公里渐进跑，让训练强度更上一层楼。跑步时逐渐提高你的配速。", "用轻松舒适的配速跑3.2公里。"+INTERVAL+"跑14公里，并在跑步过程中提高配速。"+INTERVAL+"接着用轻松缓和的配速跑最后1.6公里。", planWeek.pwId));
				planDays.add(new PlanDay(6, 0f, "完成本周强度训练后，是时候享受必要的休息。今天进行较为轻松的训练，只需到外面散步，舒展筋骨。为下周的密集训练做准备。", "好好享受休息日！快走并做彻底的伸展运动，然后养精蓄锐，以最好的状态迎接明天的训练。", planWeek.pwId));
				break;
			case 8:
				planDays.add(new PlanDay(0, 8f, "以你最熟悉的跑步开始你的密集训练周 - 8公里放松跑。加快配速跑完最后一程。", "用舒适轻松的配速跑6.4公里。"+INTERVAL+"加快配速跑完最后1.6公里。", planWeek.pwId)); 
				planDays.add(new PlanDay(1, 16f, "今天，你将要练习你的半程马拉松比赛配速。以你可以驾驭的配速坚持。", "开始先以稍微舒服的配速跑1.6公里热身。"+INTERVAL+"用你比赛的配速跑13公里。"+INTERVAL+"最后1.6公里必须猛冲，把起跑落下的时间追回来。", planWeek.pwId)); 
				planDays.add(new PlanDay(2, 11f, "你即将完成密集训练周，你的状态正处于顶峰。", "用轻松舒服的配速跑完11公里。", planWeek.pwId)); 
				planDays.add(new PlanDay(3, 8f, "今天好好休息，保留精力。明天将要提高配速跑。", "用舒服轻松的配速跑8公里。", planWeek.pwId)); 
				planDays.add(new PlanDay(4, 8f, "今天完成另一次8公里，不过这次必须把速度提高到比赛的配速。", "用比赛的配速起跑，维持6.4公里。"+INTERVAL+"最后1.6公里稍微提速，坚持跑完。", planWeek.pwId)); 
				planDays.add(new PlanDay(5, 23f, "今天你将刷新你的里程记录，测试自己的极限。这是一次距离很长、强度很大的跑步，也是拉开密集训练周大幕的最佳方式。今天慢慢来，以舒服的配速跑完。", "用轻松舒服的配速跑23公里。", planWeek.pwId)); 
				planDays.add(new PlanDay(6, 0f, "今天是休息日，尝试到外面散步，排酸。", "快步走，彻底舒展筋骨。注意多休息，然后养精蓄锐，以最好的状态迎接明天的跑步。", planWeek.pwId)); 
				break;
			case 9:
				planDays.add(new PlanDay(0, 8f, "以你最熟悉的跑步开始新的一周 - 8公里放松跑。加快配速跑完最后一程。", "用舒适轻松的配速跑6.4公里。"+INTERVAL+"加快配速跑完最后1.6公里。", planWeek.pwId)); 
				planDays.add(new PlanDay(1, 14f, "今天的14公里跑是本周两次渐进跑中的一次。跑步过程中逐渐提高配速，并在冲刺阶段全力爆发。", "用轻松舒适的配速跑4.8公里。"+INTERVAL+"跑8公里，并在跑步中逐渐提升配速。"+INTERVAL+"接着把速度提到自己的乳酸门阀，猛冲1.6公里。", planWeek.pwId)); 
				planDays.add(new PlanDay(2, 11f, "今天，以舒适的配速跑11公里。本周的训练已完成了一半。", "已舒适的配速跑11公里。", planWeek.pwId)); 
				planDays.add(new PlanDay(3, 8f, "今天，以舒适的配速跑8公里。", "已舒适的配速跑8公里。", planWeek.pwId)); 
				planDays.add(new PlanDay(4, 8f, "今天的8公里训练将帮你充分热身，迎接明天本周第二个渐进跑。提高配速跑完最后一程，记住你即将圆满结束本周训练。", "用舒服轻松的配速跑6.4公里。"+INTERVAL+"特高配速跑1.6公里。", planWeek.pwId)); 
				planDays.add(new PlanDay(5, 19f, "今天，养精蓄锐，全力以赴完成这次跑步。明天是你在减量训练周之前的休息日。", "轻松舒服的配速跑4.8公里。"+INTERVAL+"跑13公里，并在跑步过程中逐渐提速。"+INTERVAL+"接着用轻松缓和的配速跑完最后1.6公里。", planWeek.pwId)); 
				planDays.add(new PlanDay(6, 0f, "今天是休息日，尝试到外面散步，排酸。", "快步走，彻底舒展筋骨。注意多休息，然后养精蓄锐，以最好的状态迎接明天的跑步。 ", planWeek.pwId));
				break;
			case 10:
				planDays.add(new PlanDay(0, 8f, "今天的8公里训练将帮你充分热身，提高配速跑完最后一程。", "用舒服轻松的配速跑6.4公里。 "+INTERVAL+"特高配速跑1.6公里。", planWeek.pwId)); 
				planDays.add(new PlanDay(1, 16f, "今天，你将要练习你的半程马拉松比赛配速。以你可以驾驭的配速坚持。", "开始先以稍微舒服的配速跑1.6公里热身。"+INTERVAL+"用你比赛的配速跑13公里。"+INTERVAL+"最后1.6公里必须猛冲，把起跑落下的时间追回来。", planWeek.pwId)); 
				planDays.add(new PlanDay(2, 8f, "昨天练习比赛配速之后，今天通过简短的8公里跑进行轻松训练。保持轻松的配速，跑完全程。", "用轻松舒服的配速跑8公里。", planWeek.pwId)); 
				planDays.add(new PlanDay(3, 6.4f, "今天，进行轻松配速6.4公里跑，从容进行训练。减量训练周的训练，现在你已经完成了一半。", "用轻松的配速跑完6.4公里。 ", planWeek.pwId)); 
				planDays.add(new PlanDay(4, 6.4f, "今天，跑完另一个6.4公里以维持体能。加速跑完最后一程，以放松肌肉，准备迎接最后一次长距离训练。", "用舒服轻松的配速跑6.4公里。"+INTERVAL+"加快配速跑1.6公里。", planWeek.pwId)); 
				planDays.add(new PlanDay(5, 13f, "今天的长距离13公里跑同样是一个渐进训练。在跑步过程中尽力逐渐提速。跑完最后一趟，就要进入体力储备阶段了。", "用轻松舒服的配速跑4.8公里。"+INTERVAL+"跑6.4公里，并在跑步中逐渐提速。"+INTERVAL+"接着用轻松缓和的配速跑完1.6公里。", planWeek.pwId)); 
				planDays.add(new PlanDay(6, 0f, "恭喜，你已经完成减量训练周。从今天开始你就要储备体力，以最强状态迎接比赛。", "可以到外面散步，放松筋骨。注意休息。", planWeek.pwId)); 
				break;
			case 11:
				planDays.add(new PlanDay(0, 8f, "比赛周第一天，以轻松舒服的速度跑8公里。只需热身即可，掌握节奏。", "用舒服轻松的配速跑8公里。", planWeek.pwId));
				planDays.add(new PlanDay(1, 6f, "以轻松舒服的速度跑6公里。只需热身即可，掌握节奏。", "用舒服轻松的配速跑6公里。 ", planWeek.pwId));
				planDays.add(new PlanDay(2, 0f, "今天开始你就要储备体力，以最强状态迎接比赛。", "可以到外面散步，放松筋骨。注意休息。", planWeek.pwId));
				planDays.add(new PlanDay(3, 6f, " 比赛周第一天，以轻松舒服的速度跑8公里。只需热身即可，掌握节奏。", "用舒服轻松的配速跑6公里。", planWeek.pwId));
				planDays.add(new PlanDay(4, 0f, "今天开始你就要储备体力，以最强状态迎接比赛。", "可以到外面散步，放松筋骨。注意休息。", planWeek.pwId));
				planDays.add(new PlanDay(5, 3f, "比赛周第一天，以轻松舒服的速度跑8公里。只需热身即可，掌握节奏。", "用舒服轻松的配速跑3公里。", planWeek.pwId));
				planDays.add(new PlanDay(6, 21.1f, "今天是比赛日。你已训练有素，现在是你展现自我的时候，尽情奔跑，那个好成绩。", "在赛场上奔驰吧！", planWeek.pwId));
				break;
			}
		}
		
		for(int i=0;i<planDays.size();i++) {
			PlanDay planDay = planDays.get(i);
			values.put("d_number", planDay.d_number);
			values.put("p_desc", planDay.p_desc);
			values.put("p_desc_list", planDay.p_desc_list);
			values.put("distance", planDay.distance);
			values.put("date", dateAdd(date,i));
			values.put("maxDistance", planDay.maxDistance);
			values.put("p_w_Id", planDay.p_w_Id);
			wdb.insert("plan_day", null, values);
			values.clear();
		}
		
	}
	
	private String dateAdd(Long time, int index) {
		Date date = new Date(time);
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, index);
		return calendar.getTimeInMillis()+"";
	}

	public List<Plan> getPlans() {
		if(dbHelper!=null && rdb==null) {
			rdb = dbHelper.getReadableDatabase();
		}
		List<Plan> plans = new ArrayList<Plan>();
		Cursor cursor = rdb.query("plan", null, null, null, null, null, null);
		cursor.moveToFirst();// 将游标移动到初始位置  
		for (int i = 0; i < cursor.getCount(); i++) {
			Plan plan = new Plan();
			plan.pId = cursor.getString(cursor.getColumnIndex("pId"));
			plan.type = cursor.getInt(cursor.getColumnIndex("type"));
			plan.title = cursor.getString(cursor.getColumnIndex("title"));
			plan.matchDate = cursor.getString(cursor.getColumnIndex("matchDate"));
			plan.startDate = cursor.getString(cursor.getColumnIndex("startDate"));
			plan.weeks = cursor.getInt(cursor.getColumnIndex("weeks"));
			plan.maxDistance = cursor.getFloat(cursor.getColumnIndex("maxDistance"));
			plan.distance = cursor.getFloat(cursor.getColumnIndex("distance"));
			plan.state = cursor.getInt(cursor.getColumnIndex("state"));
			plans.add(plan);
			cursor.moveToNext();// 将游标移到下一行  
		}
		cursor.close();
		return plans;
	}
	
	public List<PlanWeek> getPlanWeeks(String pId) {
		if(dbHelper!=null && rdb==null) {
			rdb = dbHelper.getReadableDatabase();
		}
		
		List<PlanWeek> planWeeks = new ArrayList<PlanWeek>();
		
		int sectionPosition = 0, listPosition = 0;
		
		Cursor cursor = rdb.query("plan_week", null, " pId=? ", new String[] {pId}, null, null, " w_number asc ");
		cursor.moveToFirst();// 将游标移动到初始位置  
		for (int i = 0; i < cursor.getCount(); i++) {
			PlanWeek planWeek = new PlanWeek();
			planWeek.pwId = cursor.getString(cursor.getColumnIndex("p_w_Id"));
			planWeek.pId = cursor.getString(cursor.getColumnIndex("pId"));
			planWeek.w_number = cursor.getInt(cursor.getColumnIndex("w_number"));
			planWeek.planType = cursor.getString(cursor.getColumnIndex("planType"));
			planWeek.weekDesc = cursor.getString(cursor.getColumnIndex("weekDesc"));
			planWeek.maxDistance = cursor.getFloat(cursor.getColumnIndex("maxDistance"));
			planWeek.distance = cursor.getFloat(cursor.getColumnIndex("distance"));
			
			planWeek.sectionPosition = sectionPosition;
			planWeek.listPosition = listPosition++;
			planWeek.type = PlanBase.SECTION;
			
			planWeek.planDays = getPlanDays(planWeek.pwId);
			for(PlanDay day:planWeek.planDays) {
				day.sectionPosition = sectionPosition;
				day.listPosition = listPosition++;
				day.type = PlanBase.ITEM;
			}
			//int size = planWeek.planDays.size();
			planWeeks.add(planWeek);
			sectionPosition++;
			cursor.moveToNext();// 将游标移到下一行  
		}
		cursor.close();
		return planWeeks;
	}
	
	public List<PlanDay> getPlanDays(String p_w_Id) {
		if(dbHelper!=null && rdb==null) {
			rdb = dbHelper.getReadableDatabase();
		}
		List<PlanDay> planDays = new ArrayList<PlanDay>();
		Cursor cursor = rdb.query("plan_day", null, " p_w_Id=? ", new String[] {p_w_Id}, null, null, " d_number asc ");
		cursor.moveToFirst();// 将游标移动到初始位置  
		for (int i = 0; i < cursor.getCount(); i++) {
			PlanDay planDay = new PlanDay();
			planDay.d_number = cursor.getInt(cursor.getColumnIndex("d_number"));
			planDay.p_w_Id = cursor.getString(cursor.getColumnIndex("p_w_Id"));
			planDay.p_desc = cursor.getString(cursor.getColumnIndex("p_desc"));
			planDay.p_desc_list = cursor.getString(cursor.getColumnIndex("p_desc_list"));
			planDay.maxDistance = cursor.getFloat(cursor.getColumnIndex("maxDistance"));
			planDay.distance = cursor.getFloat(cursor.getColumnIndex("distance"));
			planDay.date = cursor.getString(cursor.getColumnIndex("date"));
			planDays.add(planDay);
			cursor.moveToNext();// 将游标移到下一行  
		}
		cursor.close();
		return planDays;
	}

	public void updatePlan(Plan plan,PlanWeek planWeek,PlanDay planDay) {
		if(dbHelper!=null && wdb==null) {
			wdb = dbHelper.getWritableDatabase();
		}
		ContentValues values = new ContentValues();
		values.put("distance", planDay.distance);
		wdb.update("plan_day", values, " p_w_Id like ? and d_number=?", new String[] {planDay.p_w_Id,planDay.d_number+""});
		
		values.clear();
		values.put("distance", planWeek.distance);
		wdb.update("plan_week", values, " p_w_Id like ?", new String[] {planWeek.pwId});
		
		values.clear();
		values.put("distance", plan.distance);
		wdb.update("plan", values, " pId like ?", new String[] {plan.pId});
		
		values.clear();
	}
	
	public boolean deletePlan(String pId) {
		if(dbHelper!=null && wdb==null) {
			wdb = dbHelper.getWritableDatabase();
		}
		wdb.delete("plan_day", " p_w_Id like ? ", new String[] {pId+"%"});
		wdb.delete("plan_week", " pId = ? ", new String[] {pId});
		wdb.delete("plan", " pId = ? ", new String[] {pId});
		return true;
	}
	
	public boolean editPlan(String pId,String title) {
		if(dbHelper!=null && wdb==null) {
			wdb = dbHelper.getWritableDatabase();
		}
		ContentValues values = new ContentValues();
		values.put("title", title);
		wdb.update("plan", values, " pId like ? ", new String[] {pId});
		values.clear();
		return true;
	}
	
	/******************Plan End**************************/

}
