package oO0oO0oO0o0o00.shadowchat;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.res.Resources;

public class CryptoRule{
	
	static public int METHOD_AES128RBQ=1;
	static public int METHOD_FAKE=1989;//Not encrypted at all
	
	static private String JSON_KEY_NAME="name";
	static private String JSON_KEY_TXTPFX="txtpfx";
	static private String JSON_KEY_ENCMODE="encmode";
	static private String JSON_KEY_EXTRA="extra";
	static private String JSON_KEY_METHOD="method";
	static private String JSON_KEY_POSTP="postp";
	
	private String name;
	private String txtPrefix;
	private int encmode;
	private String extra;
	private int method;
	private int  postp;
	private Resources res;
	
	private CryptoRule(){
		//
	}
	
	public CryptoRule(String name,String txtpfx,int mode,String extra,int met,int pp,Resources res){
		this.name=name;
		this.txtPrefix=txtpfx;
		this.encmode=mode;
		this.extra=extra;
		this.method=met;
		this.postp=pp;
		this.res=res;
	}

	public void setPostp(int postp){
		this.postp=postp;
	}

	public int getPostp(){
		return postp;
	}

	public void setMethod(int method){
		this.method=method;
	}

	public int getMethod(){
		return method;
	}

	public void setExtra(String extra){
		this.extra=extra;
	}

	public String getExtra(){
		return extra;
	}

	public void setEncmode(int encmode){
		this.encmode=encmode;
	}

	public int getEncmode(){
		return encmode;
	}

	public void setTxtPrefix(String txtPrefix){
		this.txtPrefix=txtPrefix;
	}

	public String getTxtPrefix(){
		return txtPrefix;
	}

	public void setName(String name){
		this.name=name;
	}

	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder(name)
		.append("    ，")
		.append(res.getString(R.string.crypto_prefix))
		.append(": ")
		.append(txtPrefix)
		.append("，使用")
		.append((encmode==0)?"口令":"密钥文件")
		.append("进行加密。");
		return sb.toString();
		//useless
	}
	
	public String getInfo(){
		return res.getString(R.string.crypto_getinfo,
			txtPrefix,Constants.RULE_METHODS[method],Constants.RULE_POSTPS[postp]
		);
	}

	public String getName(){
		return name;
	}
	
	public String serialize(){
		JSONObject jso=new JSONObject();
		try{
			jso.put(JSON_KEY_NAME,name);
			jso.put(JSON_KEY_TXTPFX,txtPrefix);
			jso.put(JSON_KEY_ENCMODE,encmode);
			jso.put(JSON_KEY_EXTRA,extra);
			jso.put(JSON_KEY_METHOD,method);
			jso.put(JSON_KEY_POSTP,postp);
			return jso.toString();
		}
		catch(JSONException e){
			return "";
		}
		/*String sep="/";
		return ""+name.length()+sep+name+txtPrefix.length()+sep+txtPrefix
		+encmode+sep+extra.length()+sep+extra+method+sep+postp;
		*/
	}
	
	static public CryptoRule unserialize(String raw,Resources res){
		CryptoRule cp=new CryptoRule();
		try{
			JSONObject jso=new JSONObject(raw);
			cp.name=jso.getString(JSON_KEY_NAME);
			cp.txtPrefix=jso.getString(JSON_KEY_TXTPFX);
			cp.encmode=jso.getInt(JSON_KEY_ENCMODE);
			cp.extra=jso.getString(JSON_KEY_EXTRA);
			cp.method=jso.getInt(JSON_KEY_METHOD);
			cp.postp=jso.getInt(JSON_KEY_POSTP);
		}
		catch(JSONException e){}
		cp.res=res;
		return cp;
	}
}
