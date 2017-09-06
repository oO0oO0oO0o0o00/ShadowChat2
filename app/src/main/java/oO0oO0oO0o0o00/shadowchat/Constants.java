package oO0oO0oO0o0o00.shadowchat;

public class Constants{
	
	static final public String PM_THIS="oO0oO0oO0o0o00.shadowchat";
	static final public String LOG_FILE="/sdcard/shadowchatlog.txt";
	static final public String PM_MM="com.tencent.mm";
	static final public String PM_QQ="com.tencent.mobileqq";
	
	static final public String SPREF_MAIN="main";
	static final public String SPREF_KEY_WECHAT="wechat";
	static final public String SPREF_KEY_PROFILES_WECHAT="profw";
	static final public String SPREF_KEY_ALLRULES="rules";
	
	static final public String EXTRA_KEY_PROFILEFOR="proffor";
	static final public int EXTRY_VAL_PROFILEFOR_WECHAT=1;
	static final public int EXTRY_VAL_PROFILEFOR_QQ=2;
	
	static final public String EXTRA_KEY_PROFILE_NAME="profname";
	static final public String EXTRA_KEY_PROFILE_INFO="proinfo";
	static final public String EXTRA_KEY_PROFILE_POLICIES="profps";
	static public String EXTRA_KEY_PROFILE_ALLNAMES="profans";
	static final public String EXTRA_KEY_PROFILE_IND="index";
	
	static public String EXTRA_KEY_RULE_NAME="rulname";
	static public String EXTRA_KEY_RULE_ALLNAMES="rulans";
	static public String EXTRA_KEY_RULE_TXTPFX="rultxtpfx";//明文前缀，用于验证解密是否成功
	static public String EXTRA_KEY_RULE_METHOD="rulmet";//加密方法
	static public String EXTRA_KEY_RULE_POSTP="rulpp";//后处理
	static public String EXTRA_KEY_RULE_KEYMODE="rulkmode";//密钥模式，口令或文件
	static public int EXTRA_VAL_RULE_KEYMODE_CODE=0;
	static public int EXTRA_VAL_RULE_KEYMODE_KSTR=1;
	static public String EXTRA_KEY_RULE_KEY="rulkey";//口令或文件名
	static public String EXTRA_KEY_RULE_IND="index";
	
	static final public String JSON_KEY_NAME="name";
	static final public String JSON_KEY_ENCRULE="encrypt_rule";
	static final public String JSON_KEY_DECRULES="decrypt_rule";
	
	static public String[] RULE_METHODS=new String[]{
		"AES-128-RBQ"
	};
	
	static public String[] RULE_POSTPS=new String[]{
		"16进制","Base64","常用汉字64"
	};
}
