package oO0oO0oO0o0o00.shadowchat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

import static oO0oO0oO0o0o00.shadowchat.Constants.*;
import java.io.File;

public class ProfilesActivity extends BaseActivity{

	private ProfilesListAdapter ada;
	private String key;
	
	public void confirm(View v){
		SharedPreferences spref=getSharedPreferences(SPREF_MAIN,MODE_WORLD_READABLE);
		spref.edit().putStringSet(key,ada.save()).apply();
		finish();
	}
	
	public void cancel(View v){
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		if(resultCode!=Activity.RESULT_OK) return;
		if(requestCode==0){
			ada.add(data.getStringExtra(EXTRA_KEY_PROFILE_INFO));
		}else{
			ada.set(requestCode-1,data.getStringExtra(EXTRA_KEY_PROFILE_INFO));
		}
	}
	
	public void add(View v){
		startActivityForResult(new Intent(this,ProfileActivity.class)
			.putExtra(Constants.EXTRA_KEY_PROFILE_NAME,"")
			.putExtra(Constants.EXTRA_KEY_PROFILE_ALLNAMES,ada.getNames())
			.putExtra(EXTRA_KEY_PROFILE_INFO,"")
			.putExtra(EXTRA_KEY_PROFILE_IND,-1)
			,0);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		try{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mprofiles);
		key=getIntent().getStringExtra(Constants.EXTRA_KEY_PROFILEFOR);
		SharedPreferences spref=getSharedPreferences(SPREF_MAIN,Activity.MODE_WORLD_READABLE);
		Set<String> set=spref.getStringSet(key,null);
		ListView lv=(ListView) findViewById(R.id.profilesListView1);
		ada=new ProfilesListAdapter(set);
		lv.setOnItemClickListener(
			new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> p1,View p2,int p3,long p4){
					String[] names=ada.getNames();
					startActivityForResult(new Intent(ProfilesActivity.this,ProfileActivity.class)
					.putExtra(EXTRA_KEY_PROFILE_IND,p3)
					.putExtra(EXTRA_KEY_PROFILE_ALLNAMES,names)
					.putExtra(EXTRA_KEY_PROFILE_INFO,ada.getItem(p3).toString())
					.putExtra(EXTRA_KEY_PROFILE_NAME,names[p3])
					,p3+1);
				}
			}
		);
		lv.setAdapter(ada);}catch(Exception e){
			Logger.setupIfNeed(new File(getFilesDir(),"log.txt"));
			Logger.log(e);
		}
	}
	
	class ProfilesListAdapter extends BaseAdapter{

		private List<JSONObject> profiles;
		
		public String[] getNames(){
			try{
			String[] lst=new String[profiles.size()];
			for(int i=0;i<lst.length;i++){
				lst[i]=profiles.get(i).getString(JSON_KEY_NAME);
			}
			return lst;
			}catch(JSONException e){}
			return null;
		}
		
		public Set<String> save(){
			Set<String> sav=new ArraySet<String>();
			for(JSONObject jso:profiles){
				sav.add(jso.toString());
			}
			return sav;
		}
		
		public void add(String s){
			try{
				profiles.add(new JSONObject(s));
				notifyDataSetChanged();
			}catch(JSONException e){}
		}
		
		public void set(int ind,String s){
			try{
				JSONObject jso=new JSONObject(s);
				JSONObject jsoo=profiles.get(ind);
				jsoo.put(JSON_KEY_NAME,jso.getString(JSON_KEY_NAME));
				jsoo.put(JSON_KEY_ENCRULE,jso.getString(JSON_KEY_ENCRULE));
				jsoo.put(JSON_KEY_DECRULES,jso.getJSONArray(JSON_KEY_DECRULES));
				notifyDataSetChanged();
			}catch(JSONException e){}
		}
		
		public ProfilesListAdapter(Set<String> raw){
			profiles=new ArrayList<JSONObject>();
			if(raw==null) return;
			try{
				for(String s:raw){
					profiles.add(new JSONObject(s));
				}
			}catch(JSONException e){}
		}
		
		@Override
		public int getCount(){
			return profiles.size();
		}

		@Override
		public JSONObject getItem(int p1){
			return profiles.get(p1);
		}

		@Override
		public long getItemId(int p1){
			return p1;
		}

		@Override
		public View getView(int p1,View p2,ViewGroup p3){
			View v=LayoutInflater.from(p3.getContext()).inflate(R.layout.eprofile,p3,false);
			TextView tvNmae=(TextView) v.findViewById(R.id.eprofileTvName);
			try{
				tvNmae.setText(getItem(p1).getString(JSON_KEY_NAME));
			}catch(JSONException e){}
			return v;
		}

		
	}
	
}
