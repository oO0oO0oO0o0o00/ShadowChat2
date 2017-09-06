package oO0oO0oO0o0o00.shadowchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import android.content.SharedPreferences;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Adapter;
import android.widget.AdapterView.OnItemLongClickListener;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class ManageRulesActivity extends BaseActivity{

	private SharedPreferences spref;
	private RulesAdapter ada;
	
	public void confirm(View v){
		Set<String> sb250=new ArraySet<String>();
		for(int i=0;i<ada.getCount();i++){
			sb250.add(ada.getItem(i).serialize());
		}
		spref.edit().putStringSet(Constants.SPREF_KEY_ALLRULES,sb250).apply();
		finish();
	}

	public void cancel(View v){
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		super.onActivityResult(requestCode,resultCode,data);
		//Toast.makeText(this,"",0).show();
		if(resultCode!=Activity.RESULT_OK) return;
		if(requestCode==0){
			ada.add(
				data.getStringExtra(Constants.EXTRA_KEY_RULE_NAME),
				data.getStringExtra(Constants.EXTRA_KEY_RULE_TXTPFX),
				data.getIntExtra(Constants.EXTRA_KEY_RULE_KEYMODE,0),
				data.getStringExtra(Constants.EXTRA_KEY_RULE_KEY),
				data.getIntExtra(Constants.EXTRA_KEY_RULE_METHOD,0),
				data.getIntExtra(Constants.EXTRA_KEY_RULE_POSTP,0)
			);
		}else{
			CryptoRule r=ada.getItem(requestCode-1);
			r.setName(data.getStringExtra(Constants.EXTRA_KEY_RULE_NAME));
			r.setTxtPrefix(data.getStringExtra(Constants.EXTRA_KEY_RULE_TXTPFX));
			r.setEncmode(data.getIntExtra(Constants.EXTRA_KEY_RULE_KEYMODE,0));
			r.setExtra(data.getStringExtra(Constants.EXTRA_KEY_RULE_KEY));
			r.setMethod(data.getIntExtra(Constants.EXTRA_KEY_RULE_METHOD,0));
			r.setPostp(data.getIntExtra(Constants.EXTRA_KEY_RULE_POSTP,0));
			ada.notifyDataSetChanged();
		}
	}
	
	public void addPolicy(View v){
		startActivityForResult(new Intent(this,EditRuleActivity.class)
		.putExtra(Constants.EXTRA_KEY_RULE_NAME,"")
		.putExtra(Constants.EXTRA_KEY_RULE_ALLNAMES,ada.getNames())
		.putExtra(Constants.EXTRA_KEY_RULE_KEYMODE,Constants.EXTRA_VAL_RULE_KEYMODE_CODE)
		.putExtra(Constants.EXTRA_KEY_RULE_KEY,"")
		.putExtra(Constants.EXTRA_KEY_RULE_IND,0)
		,0);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mpolicies);
		spref=getSharedPreferences(
			Constants.SPREF_MAIN,Activity.MODE_WORLD_READABLE
		);
		ListView lv=(ListView) findViewById(R.id.mpoliciesLv);
		lv.setOnItemClickListener(
			new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> p1,View p2,int p3,long p4){
					CryptoRule r=ada.getItem(p3);
					startActivityForResult(new Intent(ManageRulesActivity.this,EditRuleActivity.class)
										   .putExtra(Constants.EXTRA_KEY_RULE_NAME,r.getName())
										   .putExtra(Constants.EXTRA_KEY_RULE_TXTPFX,r.getTxtPrefix())
										   .putExtra(Constants.EXTRA_KEY_RULE_ALLNAMES,ada.getNames())
										   .putExtra(Constants.EXTRA_KEY_RULE_KEYMODE,r.getEncmode())
										   .putExtra(Constants.EXTRA_KEY_RULE_KEY,r.getExtra())
										   .putExtra(Constants.EXTRA_KEY_RULE_METHOD,r.getMethod())
										   .putExtra(Constants.EXTRA_KEY_RULE_POSTP,r.getPostp())
										   .putExtra(Constants.EXTRA_KEY_RULE_IND,p3+1)
					,p3+1);
				}
			}
		);
		lv.setOnItemLongClickListener(
			new OnItemLongClickListener(){
				@Override
				public boolean onItemLongClick(AdapterView<?> p1,View p2,int p3,long p4){
					ada.longclick=p3;
					AlertDialog dia=new AlertDialog.Builder(ManageRulesActivity.this)
						.setTitle("删除？")
						.setPositiveButton("嗯嗯",
						new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface p1,int p2){
								ada.removeLongClick();
							}
						}).setNegativeButton("才不要",null)
						.create();
					dia.show();
					return true;
				}
			}
		);
		ada=new RulesAdapter(spref.getStringSet(Constants.SPREF_KEY_ALLRULES,null));
		lv.setAdapter(ada);
	}
	
	class RulesAdapter extends BaseAdapter{

		public int longclick=-1;
		private List<CryptoRule> rules;

		public RulesAdapter(Set<String> raw){
			List<CryptoRule> policies=new ArrayList<CryptoRule>();
			this.rules=policies;
			if(raw==null) return;
			for(String s:raw){
				policies.add(CryptoRule.unserialize(s,getResources()));
			}
		}
		
		public void removeLongClick(){
			if(longclick>=0 && longclick<rules.size()){
				rules.remove(longclick);
				notifyDataSetChanged();
			}
		}
		
		public void add(String name,String txtpfx,int mode,String extra,int met,int pp){
			CryptoRule cp=new CryptoRule(name,txtpfx,mode,extra,met,pp,getResources());
			rules.add(cp);
			notifyDataSetChanged();
		}
		
		public String[] getNames(){
			String[] names=new String[rules.size()];
			for(int i=0;i<names.length;i++){
				names[i]=rules.get(i).getName();
			}
			return names;
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
			p2=LayoutInflater.from(p3.getContext()).inflate(R.layout.eplcmgrplc,p3,false);
			TextView tvName=(TextView) p2.findViewById(R.id.eplcmgrplcTvName);
			TextView tvInfo=(TextView) p2.findViewById(R.id.eplcmgrplcTvInfo);
			tvName.setText(getItem(p1).getName());
			tvInfo.setText(getItem(p1).getInfo());
			return p2;
		}
		
		
	}
	
}
