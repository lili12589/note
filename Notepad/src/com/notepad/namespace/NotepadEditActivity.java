package com.notepad.namespace;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;

public class NotepadEditActivity extends Activity {
	
	public static final int CHECK_STATE = 0;
	public static final int EDIT_STATE = 1;
	public static final int ALERT_STATE = 2;
	
	private int state = -1;
	
	private Button addRecord;//����
	private Button complete;//���
	private EditText title;
	private EditText content;
	private DatabaseManage dm = null;
	
	private String id = "";
	private String titleText = "";
	private String contentText = "";
	private String timeText = "";
	
	
	protected void onCreate(Bundle savedInstanceState){
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_notepad);
		
		Intent intent = getIntent();
		state = intent.getIntExtra("state", EDIT_STATE);
		
		//��ֵ�ؼ�����
		addRecord = (Button)findViewById(R.id.addRecordButton);
		complete = (Button)findViewById(R.id.editComplete);
		title = (EditText)findViewById(R.id.editTitle);
		content = (EditText)findViewById(R.id.editContent);
		
		//���ü���
		addRecord.setOnClickListener(new AddRecordListener());
		complete.setOnClickListener(new EditCompleteListener());
		content.setOnTouchListener(new OnTouchListener(){

			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				content.setSelection(content.getText().toString().length());
//				Editable ea = content.getText();
//				Selection.setSelection(ea,ea.length());
				
				return false;
			}
			
		});
		
		if(state == ALERT_STATE){//�޸�״̬,��ֵ�ؼ�
			id = intent.getStringExtra("_id");
			titleText = intent.getStringExtra("title");
			contentText = intent.getStringExtra("content");
			timeText = intent.getStringExtra("time");
			
			title.setText(titleText);
			content.setText(contentText);
		}
		
		dm = new DatabaseManage(this);
	}
	
	/**
	 * �������Ӱ�ť
	 * @author mao
	 *
	 */
	public class AddRecordListener implements OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	/**
	 * ������ɰ�ť
	 * @author mao
	 *
	 */
	public class EditCompleteListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			titleText = title.getText().toString();
			contentText = content.getText().toString();
//			
//			Log.v(t, t);
//			Log.v(c, c);
			try{
				dm.open();
				
				if(state == EDIT_STATE)//����״̬
					dm.insert(titleText, contentText);
				if(state == ALERT_STATE)//�޸�״̬
					dm.update(Integer.parseInt(id), titleText, contentText);
				
				dm.close();
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			Intent intent = new Intent();
			intent.setClass(NotepadEditActivity.this, NotepadActivity.class);
			NotepadEditActivity.this.startActivity(intent);
			
			
			//�������
		}
		
	}
}
