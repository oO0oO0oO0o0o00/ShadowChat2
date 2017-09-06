package oO0oO0oO0o0o00.shadowchat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import dalvik.system.DexClassLoader;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import eu.chainfire.libsuperuser.Shell;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static oO0oO0oO0o0o00.shadowchat.Constants.*;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static oO0oO0oO0o0o00.shadowchat.Logger.log;
import android.widget.Toast;
import android.os.Bundle;
import android.app.Activity;
import android.view.ViewGroup;

public class MainHook extends XC_MethodHook
implements IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources{

	static List<JSONObject> profiles=null;
	static Map<String,CryptoRule> rules=null;
	static int prof=0;

	static EditText inpu=null;
	static TextView tvTitleMM=null;
	static String titleMM=null;

	static Class qqText=null;
	static EditText inpv=null;
	static TextView tvTitleQQ=null;
	static String titleQQ=null;
	static Activity qqMainActivity=null;
	static Context ctx=null;
	static Class<?> qqSplashClazz=null;

	@Override
	public void handleLoadPackage(XC_LoadPackage.LoadPackageParam p1) throws Throwable{
		if(p1.packageName.equals(Constants.PM_MM)){
			initLogger();
			log("Wechat started, loading plugin...");
			XSharedPreferences spref=new XSharedPreferences(Constants.PM_THIS,Constants.SPREF_MAIN);
			if(!spref.getBoolean(Constants.SPREF_KEY_WECHAT,true)){
				log("Exited because this module was disabled.");
				return;
			}

			if(profiles==null){
				getProfiles();
				if(profiles.size()>0) prof=1;
				else prof=0;
			}

			/* 在为发送按钮设置OnClickListener时，将微信原设置的Listener
			 * 备份，然后将传入参数替换成自定义的Listener，以便在点击按
			 * 钮前执行自定义的代码，再执行备份的原Listener的点击事件，
			 * 使微信将消息发送出去。
			 */
			XC_MethodHook hooksend=new XC_MethodHook(){
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
					View btn=(View)param.thisObject;
					if(btn.getId()!=2131756121) return;
					//log("It seems like the send button has been found, trying to inject");
					btn.setTag(param.args[0]);
					param.args[0]=new OnClickListener(){
						@Override
						public void onClick(View p1){
							try{
								String text=inpu.getText().toString();
								if(prof!=0){
									JSONObject jso=profiles.get(prof-1);
									CryptoRule rule=rules.get(jso.getString(JSON_KEY_ENCRULE));
									String saf=Cryptoo.encrypt(text,rule);
									inpu.setText(saf);//这个输入框应该被事先捕获
								}
							}
							catch(Exception e){
								log("Failed encrypting message");
								Logger.log(e);
							}
							OnClickListener fuck=(View.OnClickListener) (p1.getTag());
							fuck.onClick(p1);
						}
					};
				}
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable{
				}
			};
			findAndHookMethod(View.class,"setOnClickListener",View.OnClickListener.class,hooksend);
			////

			/* 通过构造函数找到消息输入框，并保存。
			 */
			XC_MethodHook hooket=new XC_MethodHook(){
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable{
					View v=(View) param.thisObject;
					if(v.getId()==2131756115){
						EditText et=(EditText)v;
						et.setBackgroundColor(Color.GREEN);
						inpu=et;
					}
				}
			};
			findAndHookConstructor(EditText.class,Context.class,AttributeSet.class,hooket);
			////

			/* 文字气泡被微信设置文字时，先将文字解密
			 */
			XC_MethodHook hookbub=new XC_MethodHook(){
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
					View v=(View)param.thisObject;
					if(v.getId()!=2131755346) return;
					String text=null;
					if(prof!=0)try{
							JSONObject jso=profiles.get(prof-1);
							JSONArray ja=jso.getJSONArray(JSON_KEY_DECRULES);
							String saf=(String) param.args[0];
							for(int i=0;i<ja.length();i++){
								CryptoRule rule=rules.get(ja.getString(i));
								//log("Trying to decrypt with rule "+rule.getName());
								text=Cryptoo.decrypt(saf,rule);
								if(text!=null) break;
							}
						}
						catch(Exception e){
							log("Failed decrypting message");
							log(e);
						}
					if(text!=null){
						param.args[0]=text;
					}else{}
				}
			};
			findAndHookMethod(TextView.class,"setText",CharSequence.class,hookbub);

			//工具栏
			XC_MethodHook hooktit=new XC_MethodHook(){
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
					TextView v=(TextView)param.thisObject;
					if(v.getId()!=2131755274) return;
					tvTitleMM=v;
					//log("It seems that toolbar of chat window has beem found.");
					v.setTextSize(10);
					v.setSingleLine(false);
					v.setMaxLines(2);
					v.setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View p1){
								selectProfile(p1,1);
							}
						});
					String s=""+param.args[0];
					titleMM=s;
					param.args[0]=getTitle(s);log("sb250");
				}
			};
			findAndHookMethod(TextView.class,"setText",CharSequence.class,hooktit);
			//////////////Wechat END

			//////////////QQ START
		}else if(p1.packageName.equals(Constants.PM_QQ)){
			initLogger();
			log("QQ started, loading plugin");
			XSharedPreferences spref=new XSharedPreferences(Constants.PM_THIS,Constants.SPREF_MAIN);
			if(!spref.getBoolean(Constants.SPREF_KEY_WECHAT,true)){
				log("Exited because feature disabled for QQ");
				return;
			}
			/* QQ气泡的文字使用其自定义类QQtext，需要
			 * 加载该类以转换为普通字符串
			 */
			if(qqText==null)try{
					String pd=null;
					for(String s:Shell.SH.run("pm path com.tencent.mobileqq")){
						if(s.startsWith("package:")){
							pd=s.substring(8);
							break;
						}
					}if(pd!=null){
						DexClassLoader dl=new DexClassLoader(pd,p1.appInfo.dataDir,"/data/data/com.tencent.mobileqq/lib",p1.classLoader);
						Logger.log(""+(dl==null));
						qqText=dl.loadClass("com.tencent.mobileqq.text.QQText");
					}else{
						//QQ not installed
						log("你装QQ了吗？");
					}
				}
				catch(Exception e){
					log("Can't load QQText class");
					log(e);
					//return;
				}
				catch(Error e){
					log("Can't load QQText class");
					log(e);
					//虽然不知道为毛会是Error
					//return;
				}
			//log("20p4");
			XC_MethodHook hookmainact=new XC_MethodHook(){
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
					//Method met=qqSplashClazz.getMethod(g
					ctx=(Context) param.thisObject;
					qqMainActivity=(Activity) param.thisObject;
					//log("sbsbsb");
					Toast.makeText(ctx,"www",0).show();
					String vcode=ctx.getPackageManager().getPackageInfo(PM_QQ,0).versionName;
					if(vcode.equals("7.0.0")) ViewVeri.version=700;
					else if(vcode.startsWith("6.7")) ViewVeri.version=671;
					else Toast.makeText(ctx,"好像不支持此版本，如果异常请关毙",0).show();
				}
			};
			qqSplashClazz=XposedHelpers.findClass("com.tencent.mobileqq.activity.SplashActivity",p1.classLoader);
			findAndHookMethod(qqSplashClazz,"doOnCreate",Bundle.class,hookmainact);
			//if(0<256) return;
			if(profiles==null){
				getProfiles();
				if(profiles.size()>0) prof=1;
				else prof=0;
			}
			XC_MethodHook hooksend=new XC_MethodHook(){
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
					View btn=(View)param.thisObject;
					if(!ViewVeri.isQqSendbtn(btn)) return;
					//log("It seems like the send button has been found, trying to inject");
					btn.setTag(param.args[0]);
					param.args[0]=new OnClickListener(){
						@Override
						public void onClick(View p1){
							try{
								String text=inpv.getText().toString();
								//Logger.log("text="+text);
								if(prof!=0){
									JSONObject jso=profiles.get(prof-1);
									CryptoRule rule=rules.get(jso.getString(JSON_KEY_ENCRULE));
									//Logger.log(""+(text==null)+","+(rule==null));
									String saf=Cryptoo.encrypt(text,rule);
									inpv.setText(saf);//这个输入框应该事先被捕获
									//Logger.log("saf="+saf);
								}
							}
							catch(Exception e){
								log("obd9hd");
								Logger.log(e);
							}
							OnClickListener fuck=(View.OnClickListener) (p1.getTag());
							fuck.onClick(p1);
						}
					};
				}
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable{
				}
			};
			findAndHookMethod(View.class,"setOnClickListener",View.OnClickListener.class,hooksend);

			XC_MethodHook hooketcon=new XC_MethodHook(){
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable{
					try{
					EditText v=(EditText) param.thisObject;
					if(ViewVeri.isQqTextbox(v)){
						v.setBackgroundColor(Color.GREEN);
						inpv=v;
					}}catch(Exception e){
						log("1qs.");log(e);
					}
				}
			};
			findAndHookConstructor(EditText.class,Context.class,AttributeSet.class,hooketcon);
			
			XC_MethodHook hooketadd=new XC_MethodHook(){
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable{
					try{
						View v=(View) param.args[0];
						if(!(v instanceof EditText))return;
						if(ViewVeri.isQqTextbox(v)){
							v.setBackgroundColor(Color.GREEN);
							inpv=(EditText) v;
						}}catch(Exception e){
						log("1qp.");log(e);
					}
				}
			};
			
			findAndHookMethod(ViewGroup.class,"addView",View.class,Integer.TYPE,Integer.TYPE,hooketadd);
			findAndHookMethod(ViewGroup.class,"addView",View.class,Integer.TYPE,hooketadd);
			findAndHookMethod(ViewGroup.class,"addView",View.class,hooketadd);
			findAndHookMethod(ViewGroup.class,"addView",View.class,Integer.TYPE,ViewGroup.LayoutParams.class,hooketadd);
			findAndHookMethod(ViewGroup.class,"addView",View.class,ViewGroup.LayoutParams.class,hooketadd);
			/* 文字气泡被QQ设置文字时，先将文字解密
			 */
			XC_MethodHook hookbub=new XC_MethodHook(){
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
					TextView v=(TextView)param.thisObject;
					if(!ViewVeri.isQqBubtext(v)) return;
					String text=null;
					if(prof!=0)try{
							JSONObject jso=profiles.get(prof-1);
							JSONArray ja=jso.getJSONArray(JSON_KEY_DECRULES);
							Method met=qqText.getMethod("toString");
							String saf=(String) met.invoke(param.args[0]);
							//log("saa="+saf);
							for(int i=0;i<ja.length();i++){
								CryptoRule rule=rules.get(ja.getString(i));
								//Logger.log("Trying to decrypt with rule "+rule.getName());
								text=Cryptoo.decrypt(saf,rule);
								if(text!=null) break;
							}
						}
						catch(Exception e){
							//It's normal no need to log
							//log("fbofob");
							//Logger.log(e);
						}
					if(text!=null){
						param.args[0]=text;
					}else{
						//param.args[0]="999";???
					}
				}
			};
			findAndHookMethod(TextView.class,"setText",CharSequence.class,hookbub);
			//工具栏
			XC_MethodHook hooktit=new XC_MethodHook(){
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
					TextView v=(TextView)param.thisObject;
					//qqMainActivity.getFragmentManager().
					if(ViewVeri.isQqTitle(v)){
						/*flog("kli"+v.getMaxLines());
						log("1er"+v.getTextSize());
						log("wer"+param.args[0]);*/
						tvTitleQQ=v;
						v.setTextSize(10);
						v.setSingleLine(false);
						v.setMaxLines(3);
						v.setOnClickListener(new OnClickListener(){
								@Override
								public void onClick(View p1){
									selectProfile(p1,2);
								}
							});
						String s=param.args[0].toString();
						titleQQ=s;
						param.args[0]=getTitle(s);
						//updateTitle(2);
						//param.args[0]="点击切换模式\n"+param.args[0];
					}
				}
			};
			findAndHookMethod(TextView.class,"setText",CharSequence.class,hooktit);
		}
	}
	//////////////////////////
	//

	private void selectProfile(View v,final int forwho){
		try{
			String[] list=new String[profiles.size()+2];
			list[0]="不使用/None";
			for(int i=0;i<profiles.size();i++){
				try{
					list[i+1]=profiles.get(i).getString(JSON_KEY_NAME);
				}
				catch(JSONException e){
					list[i+1]="加载失败/Failed loading";
				}
			}
			list[profiles.size()+1]="刷新列表/Refresh";
			//list=new String[]{"0","8"};
			AlertDialog dia=new AlertDialog.Builder(v.getContext())
				.setTitle("选择方案/Select profile")
				.setItems(list,new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface p1,int p2){
						if(p2<profiles.size()+1){
							prof=p2;
							updateTitle(forwho);
							return;
						}
						getProfiles();
					}
				}
			)
				.create();
			dia.show();
		}
		catch(Exception e){
			Logger.log(e);
		}
	}

	private String getTitle(String ori){
		StringBuilder sb=new StringBuilder(ori);
		sb.append("\n");
		if(prof==0){
			sb.append("未加密/Not enc");
		}else{
			try{
				String pn=profiles.get(prof-1).getString(JSON_KEY_NAME);
				sb.append("当前/Using ").append(pn);
			}
			catch(JSONException e){
				sb.append("无法获取当前方案/Failed");
			}
		}
		sb.append(",点击切换/Click change");
		return sb.toString();
	}

	private void updateTitle(int forwho){
		try{
			TextView tv;
			String tit;
			if(forwho==1){
				tv=tvTitleMM;
				tit=titleMM;
			}else{
				tv=tvTitleQQ;
				tit=titleQQ;
			}
			tv.setText(tit);
		}
		catch(Exception e){
			flog(e);
		}
	}

	private void getProfiles(){
		try{
			XSharedPreferences xp=new XSharedPreferences(PM_THIS,Constants.SPREF_MAIN);
			Set<String> set=xp.getStringSet(SPREF_KEY_PROFILES_WECHAT,null);
			if(profiles==null) profiles=new ArrayList<JSONObject>();
			else profiles.clear();
			if(set!=null){
				for(String s:set){
					profiles.add(new JSONObject(s));
				}
			}else{
			}
			set=xp.getStringSet(SPREF_KEY_ALLRULES,null);
			if(rules==null) rules=new HashMap<String,CryptoRule>();
			else rules.clear();
			if(set!=null){
				for(String s:set){
					CryptoRule rule=CryptoRule.unserialize(s,null);
					rules.put(rule.getName(),rule);
				}
			}
		}
		catch(Exception e){
			Logger.log(e);
		}
	}

	@Override
	public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam p1) throws Throwable{
		//没用…
	}

	@Override
	public void initZygote(IXposedHookZygoteInit.StartupParam p1) throws Throwable{
		//辣鸡…
	}

	static private void initLogger(){
		try{
			Logger.setupIfNeed(new File("/sdcard/#_xp/_.txt"));
		}
		catch(Exception e){}
	}

	static void flog(String s){
		initLogger();
		log(s);
	}

	static void flog(Throwable s){
		initLogger();
		log(s);
	}

}
