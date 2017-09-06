package oO0oO0oO0o0o00.shadowchat;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;

public class ViewVeri{
	
	static int version=0;
	
	static public boolean isQqTitle(TextView tv){
		String s=tv.getParent().getClass().getName();
		Logger.log("qow"+s);
		if(!s.contains("RightLinearLayout"))return false;
		String a=tv.getResources().getResourceName(tv.getId());
		Logger.log("qwn"+a);
		return a.contains("title");
	}
	
	static public boolean isQqTextbox(View et){
		return et.getClass().getName().contains("XEditTextEx");
	}
	
	static public boolean isQqSendbtn(View v){
		return v.getClass().getName().contains("PatchedButton");
	}
	
	static public boolean isQqBubtext(TextView tv){
		String cl=tv.getClass().getName();
		if(cl.contains("ETTextView"))return true;
		return cl.contains("AnimationTextView");
	}
}
