package oO0oO0oO0o0o00.shadowchat;

import android.app.Activity;
//import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import android.widget.ListView;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.Toast;
import android.view.View.OnClickListener;
import org.json.JSONObject;
import org.json.JSONException;
import static oO0oO0oO0o0o00.shadowchat.Constants.*;
import android.widget.EditText;
import org.json.JSONArray;
import java.io.File;
import android.support.v7.app.AlertDialog;

public class ProfileActivity extends BaseActivity{

	private int selInfo;
	private DecRulesAdapter ada;
	private String[] selItems;
	private TextView tvEt,tvEi;
	private EditText etName;
	
	public void confirm(View v){
		String name=etName.getText().toString();
		if(name.length()<1){
			Toast.makeText(this,"名称不能为空",0).show();
			return;
		}
		String[] names=getIntent().getStringArrayExtra(EXTRA_KEY_PROFILE_ALLNAMES);
		int ind=getIntent().getIntExtra(EXTRA_KEY_PROFILE_IND,0);
		int i=0;
		for(String s:names){
			if(s.equals(name) && ind!=i){
				Toast.makeText(this,"这个名字已经用过",0).show();
			}i++;
		}
		String enc=tvEt.getText().toString();
		if(enc.length()<1){
			Toast.makeText(this,"10020",0).show();
			return;
		}
		String sav=ada.save(name,enc);
		setResult(Activity.RESULT_OK,new Intent()
			.putExtra(EXTRA_KEY_PROFILE_NAME,name)
			.putExtra(EXTRA_KEY_PROFILE_INFO,sav)
		);
		finish();
	}

	public void cancel(View v){
		finish();
	}
	
	public void selectEncRule(View v){
		selInfo=-2;
		selectRule();
	}
	
	public void addDecRule(View v){
		selInfo=-1;
		selectRule();
	}
	
	private void selectRule(){
		CryptoRule[] rules=getRules();
		String[] rs=new String[rules.length];
		String[] aa=new String[rules.length];
		for(int i=0;i<rules.length;i++){
			CryptoRule r=rules[i];
			StringBuilder sb=new StringBuilder(r.getName())
			.append("\n").append(r.getInfo());
			rs[i]=sb.toString();
			aa[i]=r.getName();
		}
		selItems=aa;
		AlertDialog dia=new AlertDialog.Builder(this)//,R.style.AppTheme)
			.setTitle("选择一条")
			.setItems(rs,new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1,int p2){
					if(selInfo==-2){
						tvEt.setText(selItems[p2]);
						tvEi.setText(getRuleByName(selItems[p2]).getInfo());
					}else if(selInfo==-1){
						ada.add(ada.getCount(),selItems[p2]);
					}else{
						ada.add(selInfo+1,selItems[p2]);
					}
				}
			})
			.setPositiveButton("规则管理器",new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1,int p2){
					startActivity(new Intent(ProfileActivity.this,ManageRulesActivity.class));
				}
			})
			.setNegativeButton("算了",null)
			.create();
		dia.show();
	}

	private CryptoRule[] getRules(){
		Set<String> set=getSharedPreferences(Constants.SPREF_MAIN,Activity.MODE_WORLD_READABLE)
			.getStringSet(Constants.SPREF_KEY_ALLRULES,null);
		CryptoRule[] rules=new CryptoRule[set.size()];
		int i=0;
		for(Iterator<String> it=set.iterator();it.hasNext();i++){
			rules[i]=CryptoRule.unserialize(it.next(),getResources());
		}
		return rules;
	}

	private CryptoRule getRuleByName(String selItems){
		CryptoRule[] rules=getRules();
		for(CryptoRule r:rules){
			if(r.getName().equals(selItems)) return r;
		}
		return null;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mprofile);
		ListView drules=(ListView) findViewById(R.id.mprofileDecplcs);
		tvEt=(TextView) findViewById(R.id.mprofileTvEnctit);
		tvEi=(TextView) findViewById(R.id.mprofileTvEncinf);
		etName=(EditText) findViewById(R.id.mprofileEtName);
		setResult(Activity.RESULT_CANCELED);
		
		try{
			String jstring=getIntent().getStringExtra(EXTRA_KEY_PROFILE_INFO);
			Logger.setupIfNeed(new File(getFilesDir(),"log.txt"));
			Logger.log(jstring);
			JSONArray jsa=null;
			if(!jstring.equals("")){
				JSONObject jso=new JSONObject(jstring);
				etName.setText(jso.getString(JSON_KEY_NAME));
				CryptoRule r=getRuleByName(jso.getString(JSON_KEY_ENCRULE));
				tvEt.setText(r.getName());
				tvEi.setText(r.getInfo());
				jsa=jso.getJSONArray(JSON_KEY_DECRULES);
			}
			ada=new DecRulesAdapter(jsa);
			drules.setAdapter(ada);
		}catch(Exception e){Logger.setupIfNeed(new File(getFilesDir(),"log.txt"));Logger.log(e);}
	}
	
	class DecRulesAdapter extends BaseAdapter implements OnClickListener{

		@Override
		public void onClick(View p1){
			int posi=p1.getTag();CryptoRule r;
			//Toast.makeText(ProfileActivity.this,""+posi,0).show();
			switch(p1.getId()){
			case R.id.eprofileruleIbUp:
				r=rules.get(posi);
				rules.remove(posi);
				rules.add(posi-1,r);
				notifyDataSetChanged();
				break;
			case R.id.eprofileruleIbDn:
				r=rules.get(posi);
				rules.remove(posi);
				rules.add(posi+1,r);
				notifyDataSetChanged();
				break;
			case R.id.eprofileruleIbDl:
				rules.remove(posi);
				notifyDataSetChanged();
				break;
			case R.id.eprofileruleIbAdd:
				selInfo=posi;
				selectRule();
			}
		}

		private List<CryptoRule> rules;
		
		public DecRulesAdapter(JSONArray arr){
			List<CryptoRule> rules=new ArrayList<CryptoRule>();
			this.rules=rules;
			if(arr==null)return;
			try{
			for(int i=0;i<arr.length();i++){
				rules.add(getRuleByName(arr.getString(i)));
			}
			}catch(JSONException e){}
		}
		
		public String save(String name,String enc){
			try{
				JSONObject jso=new JSONObject();
				jso.put(JSON_KEY_NAME,name);
				jso.put(JSON_KEY_ENCRULE,enc);
				JSONArray ja=new JSONArray();
				for(int i=0;i<rules.size();i++){
					ja.put(rules.get(i).getName());
				}
				jso.put(JSON_KEY_DECRULES,ja);
				return jso.toString();
			}catch(JSONException e){}
			return null;
		}
		
		public void add(int posi,String name){
			for(CryptoRule r:rules){
				if(r.getName().equals(name)){
					Toast.makeText(ProfileActivity.this,"不能重复添加",0).show();
					return;
				}
			}
			CryptoRule cp=getRuleByName(name);
			rules.add(posi,cp);
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount(){
			return rules.size();
		}

		@Override
		public CryptoRule getItem(int p1){
			return rules.get(p1);
		}

		@Override
		public long getItemId(int p1){
			return p1;
		}

		@Override
		public View getView(int p1,View p2,ViewGroup p3){
			View v=LayoutInflater.from(p3.getContext()).inflate(R.layout.eprofilerule,p3,false);
			TextView tvName=(TextView) v.findViewById(R.id.eprofileruleTvName);
			TextView tvInfo=(TextView) v.findViewById(R.id.eprofileruleTvInfo);
			ImageButton ibUp=(ImageButton) v.findViewById(R.id.eprofileruleIbUp);
			ImageButton ibDn=(ImageButton) v.findViewById(R.id.eprofileruleIbDn);
			ImageButton ibDl=(ImageButton) v.findViewById(R.id.eprofileruleIbDl);
			ImageButton ibAd=(ImageButton) v.findViewById(R.id.eprofileruleIbAdd);
			if(p1==0)ibUp.setEnabled(false);
			if(p1==getCount()-1)ibDn.setEnabled(false);
			ibUp.setTag(p1);ibDn.setTag(p1);ibDl.setTag(p1);ibAd.setTag(p1);
			ibUp.setOnClickListener(this);ibDn.setOnClickListener(this);
			ibDl.setOnClickListener(this);ibAd.setOnClickListener(this);
			CryptoRule r=getItem(p1);
			tvName.setText(r.getName());
			tvInfo.setText(r.getInfo());
			//
			return v;
		}
	}
	
}
