package oO0oO0oO0o0o00.shadowchat;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class HelpActivity extends BaseActivity{

	private TextView b0,b1;
	private boolean busy;
	private int 猫;
	
	public void FAQ(View v){
		++猫;//艹猫
		if(猫>=20){
			Toast.makeText(this,"喵！",0).show();
			猫=0;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mhelp);
		b0=(TextView) findViewById(R.id.mhelpBanner0);
		b1=(TextView) findViewById(R.id.mhelpBanner1);
		busy=false;猫=0;
		new Thread(
			new Runnable(){
				@Override
				public void run(){
					while(HelpActivity.this!=null){
						try{
							Thread.currentThread().sleep(200);
						}
						catch(InterruptedException e){}
						if(busy) continue;
						runOnUiThread(
							new Runnable(){
								@Override
								public void run(){
									busy=true;
									String s=b0.getText().toString();
									s=s.substring(1)+s.charAt(0);
									b0.setText(s);
									s=b1.getText().toString();
									s=s.substring(1)+s.charAt(0);
									b1.setText(s);
									busy=false;
								}
							}
						);
					}
				}
			}
		).start();
	}

}
