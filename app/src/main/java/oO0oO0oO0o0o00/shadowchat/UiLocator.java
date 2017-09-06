package oO0oO0oO0o0o00.shadowchat;
import android.view.View;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import android.view.ViewGroup;
import java.util.Iterator;
import android.view.ViewParent;

public class UiLocator{

	private boolean digested;
	private JSONObject pattern;
	private List<View> views=null;

	public UiLocator(JSONObject pattern){
		this.pattern=pattern;
		this.digested=false;
	}

	public static UiLocator create(String pattern_json){
		try{
			UiLocator uil=new UiLocator(new JSONObject(pattern_json));
			return uil;
		}
		catch(JSONException e){
			return null;
		}
	}

	public int useOn(View root){
		if(views==null)views=new ArrayList<View>();
		else if(views instanceof List)views.clear();
		else views=new ArrayList<View>();
		traverse_find(root);
		return views.size();
	}

	private void traverse_find(View root){
		if(root==null)return;
		if(traverse_match_property(root,pattern)){
			views.add(root);
		}
		if(root instanceof ViewGroup){
			ViewGroup vg=(ViewGroup) root;
			int max=vg.getChildCount();
			for(int i=0;i<max;i++){
				traverse_find(vg.getChildAt(i));
			}
		}
	}

	private boolean traverse_match_property(View view,JSONObject patt){
		for(Iterator<String> it=patt.keys();it.hasNext();){
			String s=it.next();
			try{
				switch(s){
				case "idchar":
					try{
						if(!patt.getString(s).equals(
						view.getResources().getResourceName(view.getId())
					))return false;
					}catch(Exception e){return false;}
					break;
				case "class":
					if(!patt.getString(s).equals(
						view.getClass().getName()
					))return false;
					break;
				case "parent":
					if(!traverse_match_property(
						(View)(view.getParent()),patt.getJSONObject(s)
					))return false;
				}
			}
			catch(Exception e){
				Logger.log("UiLocator failed:");
				Logger.log(e);
			}
		}
		return true;
	}
	
	public void hide(){
		for(View v:views){
			v.setTag(1001,v.getAlpha());
			v.setAlpha(1);
			v.setVisibility(View.GONE);
		}
	}
	
	public void remove(){
		for(View v:views){
			ViewParent p=v.getParent();
			if(!(p instanceof ViewGroup))continue;
			ViewGroup g=(ViewGroup) p;
			g.removeView(v);
		}
	}

}
