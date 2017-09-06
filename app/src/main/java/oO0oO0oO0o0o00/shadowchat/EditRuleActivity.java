package oO0oO0oO0o0o00.shadowchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import oO0oO0oO0o0o00.shadowchat.Constants;
import android.widget.TextView;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class EditRuleActivity extends BaseActivity{

	private boolean hasFn;
	private int mode,met,pp,index;
	
	private EditText etName;
	private EditText etTxtpfx;
	private RadioButton rbMode0,rbMode1;
	private TextView tvMet,tvPp;
	private EditText etCode;
	private View vMode0,vMode1,vMode1a;
	private TextView tvFn;
	
	public void confirm(View v){
		String name=etName.getText().toString();
		if(name.equals("")){
			Toast.makeText(this,"名称不能为空",0).show();
			return;
		}
		String[] badWords=new String[]{" ","\n","/","\\"};
		for(String s:badWords){
			if(name.contains(s)){
				Toast.makeText(this,"名称不能包含符号\""+s+"\"",0).show();
				return;
			}
		}
		String[] names=getIntent().getStringArrayExtra(Constants.EXTRA_KEY_RULE_ALLNAMES);
		for(int i=0;i<names.length;i++){
			String s=names[i];
			if(s.equals(name) && i!=index-1){
				Toast.makeText(this,"此名称已被其他规则使用",0).show();
				return;
			}
		}
		String txtpfx=etTxtpfx.getText().toString();
		if(txtpfx.length()<5){
			Toast.makeText(this,"你太短了…我是说，前缀太短了",0).show();
			return;
		}
		String code=etCode.getText().toString();
		if(mode==0 && code.length()<4){
			Toast.makeText(this,"尼玛太短了…我是说，密码太短了",0).show();
			return;
		}
		if(mode!=0){
			Toast.makeText(this,"........",0).show();
			return;
		}
		setResult(Activity.RESULT_OK,new Intent()
				  .putExtra(Constants.EXTRA_KEY_RULE_NAME,name)
				  .putExtra(Constants.EXTRA_KEY_RULE_TXTPFX,txtpfx)
				  .putExtra(Constants.EXTRA_KEY_RULE_KEYMODE,mode)
				  .putExtra(Constants.EXTRA_KEY_RULE_KEY,mode==0?code:tvFn.getText())
				  .putExtra(Constants.EXTRA_KEY_RULE_METHOD,met)
				  .putExtra(Constants.EXTRA_KEY_RULE_POSTP,pp)
		);
		finish();
	}

	public void cancel(View v){
		finish();
	}
	
	public void selectMethod(View v){
		AlertDialog dia=new AlertDialog.Builder(this)
		.setItems(Constants.RULE_METHODS,
			new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1,int p2){
					met=p2;
					setupBoxes();
				}
			}
		).setTitle("选择加密方式")
		.create();
		dia.show();
	}
	
	public void selectPostp(View v){
		AlertDialog dia=new AlertDialog.Builder(this)
			.setItems(Constants.RULE_POSTPS,
			new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1,int p2){
					pp=p2;
					setupBoxes();
				}
			}
		).setTitle("选择后处理方式")
		.create();
		dia.show();
	}
	
	private void setupBoxes(){
		if(mode==0){
			hasFn=false;
			vMode0.setVisibility(View.VISIBLE);
			vMode1.setVisibility(View.GONE);
			vMode1a.setVisibility(View.GONE);
		}else if(hasFn){
			etCode.setText("");
			vMode0.setVisibility(View.GONE);
			vMode1a.setVisibility(View.VISIBLE);
			vMode1.setVisibility(View.GONE);
		}else{
			hasFn=false;
			etCode.setText("");
			vMode0.setVisibility(View.GONE);
			vMode1a.setVisibility(View.GONE);
			vMode1.setVisibility(View.VISIBLE);
		}
		tvMet.setText(Constants.RULE_METHODS[met]);
		tvPp.setText(Constants.RULE_POSTPS[pp]);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mpolicy);
		setResult(Activity.RESULT_CANCELED);
		Intent itt=getIntent();
		etName=(EditText) findViewById(R.id.mpolicyEtName);
		etTxtpfx=(EditText) findViewById(R.id.mpolicyEtTxtpfx);
		rbMode0=(RadioButton) findViewById(R.id.mpolicyRb0);
		rbMode1=(RadioButton) findViewById(R.id.mpolicyRb1);
		etCode=(EditText) findViewById(R.id.mpolicyEtCode);
		vMode0=findViewById(R.id.mpolicyBoxMode0);
		vMode1=findViewById(R.id.mpolicyBoxMode1);
		vMode1a=findViewById(R.id.mpolicyBoxMode1a);
		tvFn=(TextView) findViewById(R.id.mpolicyTvFn);
		tvMet=(TextView) findViewById(R.id.mpolicyTvMet);
		tvPp=(TextView) findViewById(R.id.mpolicyTvPostp);
		
		met=itt.getIntExtra(Constants.EXTRA_KEY_RULE_METHOD,0);
		pp=itt.getIntExtra(Constants.EXTRA_KEY_RULE_POSTP,0);
		etName.setText(itt.getStringExtra(Constants.EXTRA_KEY_RULE_NAME));
		etTxtpfx.setText(itt.getStringExtra(Constants.EXTRA_KEY_RULE_TXTPFX));
		mode=itt.getIntExtra(Constants.EXTRA_KEY_RULE_KEYMODE,0);
		rbMode0.setChecked(mode==0);
		rbMode1.setChecked(mode==1);
		if(mode==0) etCode.setText(itt.getStringExtra(Constants.EXTRA_KEY_RULE_KEY));
		else tvFn.setText(itt.getStringExtra(Constants.EXTRA_KEY_RULE_KEY));
		hasFn=itt.getStringExtra(Constants.EXTRA_KEY_RULE_KEY).equals("");
		index=itt.getIntExtra(Constants.EXTRA_KEY_RULE_IND,0);
		
		setupBoxes();
		rbMode0.setOnCheckedChangeListener(
			new CompoundButton.OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton p1,boolean p2){
					int modd;
					hasFn=false;
					if(p1.getId()==R.id.mpolicyRb0) modd=p2?0:1;
					else modd=p2?1:0;
					if(modd!=mode){
						mode=modd;
						setupBoxes();
					}
				}
			}
		);
	}
}
