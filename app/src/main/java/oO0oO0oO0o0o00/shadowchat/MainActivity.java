package oO0oO0oO0o0o00.shadowchat;

//import android.app.*;
//import android.os.*;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import dalvik.system.DexClassLoader;
import java.io.IOException;
import java.io.DataInputStream;
import android.widget.Toast;
import eu.chainfire.libsuperuser.Shell;
import java.io.File;
import java.lang.reflect.Method;

public class MainActivity extends BaseActivity{
	
	SharedPreferences spref;
	
	public void switchWechatEncrypt(View v){
		Switch sw=(Switch) v;
		spref.edit().putBoolean(Constants.SPREF_KEY_WECHAT,sw.isChecked()).apply();
	}
	
	public void editProfiles(View v){
		startActivity(new Intent(this,ProfilesActivity.class)
			.putExtra(Constants.EXTRA_KEY_PROFILEFOR,Constants.SPREF_KEY_PROFILES_WECHAT)
		 );
		//Logger.setupIfNeed(new File(getFilesDir(),"log.txt"));
		
	}
	
	public void editPolicies(View v){
		startActivity(new Intent(this,ManageRulesActivity.class));
	}
	
	public void showHelp(View v){
		startActivity(new Intent(this,HelpActivity.class));
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mmain);
		spref=getSharedPreferences(Constants.SPREF_MAIN,Activity.MODE_WORLD_READABLE);
		Switch sw=(Switch) findViewById(R.id.mainSwWechat);
		sw.setChecked(spref.getBoolean(Constants.SPREF_KEY_WECHAT,true));
    }
}
