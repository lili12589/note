package com.notepad.namespace;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class NotepadActivity extends ListActivity implements OnScrollListener {
    /** Called when the activity is first created. */
	//���ڱ�ʾ��ǰ��������������״̬
	public static final int CHECK_STATE = 0;
	public static final int EDIT_STATE = 1;
	public static final int ALERT_STATE = 2;
	
	
	private ListView listView;
	private ListViewAdapter adapter;// ����Դ����
	private View RecordView;///�б���
	private View longClickView ;///���������Ĳ���
	private Button addRecordButton;//����
	
	private Button deleteRecordButton;//ɾ��
	private Button checkRecordButton;//�鿴
	private Button modifyRecordButton;//�޸�
	
	private DatabaseManage dm = null;// ���ݿ�������
	private Cursor cursor = null;
	
	private int id = -1;//���������Ŀ
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        RecordView = getLayoutInflater()
        		.inflate(R.layout.footer,null);//��ȡ��Ŀ�б�Ĳ���
		longClickView = getLayoutInflater()
				.inflate(R.layout.long_click,null);
		
        //��ȡ��ť���� 
        addRecordButton = (Button)
        		RecordView.findViewById(R.id.addRecordButton);
        deleteRecordButton = (Button)
        		longClickView.findViewById(R.id.deleteRecordButton);
        checkRecordButton = (Button)
        		longClickView.findViewById(R.id.checkRecordButton);
        modifyRecordButton = (Button)
        		longClickView.findViewById(R.id.modifyRecordButton);
        
        dm = new DatabaseManage(this);//���ݿ��������
        
        
        listView = getListView();//��ȡidΪlist�Ķ���
        
        listView.addFooterView(RecordView);//�����б�ײ� ��ͼ
        
        initAdapter();//��ʼ��
        
     //   this.startManagingCursor(cursor);//��cursor����Activity����
        
        setListAdapter(adapter);//�Զ�ΪidΪlist��ListView����������
        
        
        //���û���������
        listView.setOnScrollListener(this);
        listView.setOnCreateContextMenuListener(new myOnCreateContextMenuListener());
        
        
        //���ð�ť������
        addRecordButton.setOnClickListener(new AddRecordListener());//����
        deleteRecordButton.setOnClickListener(new DeleteRecordListener());//ɾ��
        checkRecordButton.setOnClickListener(new CheckRecordListener());//�鿴
        modifyRecordButton.setOnClickListener(new ModifyRecordListener());//�޸�
        
        
    }
    
    //��ʼ������Դ
	public void initAdapter(){
    	
    	dm.open();//�����ݿ��������
    	
    	cursor = dm.selectAll();//��ȡ��������
    	
    	cursor.moveToFirst();//���α��ƶ�����һ�����ݣ�ʹ��ǰ�������
    	
    	int count = cursor.getCount();//����
    	
    	ArrayList<String> items = new ArrayList<String>();
    	ArrayList<String> times = new ArrayList<String>();
    	for(int i= 0; i < count; i++){
    		items.add(cursor.getString(cursor.getColumnIndex("title")));
    		times.add(cursor.getString(cursor.getColumnIndex("time")));
    		cursor.moveToNext();//���α�ָ����һ��
    	}
   // 	cursor.close();
    	dm.close();//�ر����ݲ�������
    	adapter = new ListViewAdapter(this,items,times);//��������Դ
    }
    

	@Override
	protected void onDestroy() {//����Activity֮ǰ����������
		// TODO Auto-generated method stub
		cursor.close();//�ر��α�
		super.onDestroy();
	}
	
	//�����¼�
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}



	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
	}
	
	//---------------------------------------------------------------
	
	//����
	public class myOnCreateContextMenuListener implements OnCreateContextMenuListener{

		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			// TODO Auto-generated method stub
			final AdapterView.AdapterContextMenuInfo info = 
					(AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle("");
			//����ѡ��
			menu.add(0,0,0,"ɾ��");
			menu.add(0,1,0,"�޸�");
			menu.add(0,2,0,"�鿴");
		}
		
	}
	
	
	
	//��Ӧ���������˵��ĵ���¼�
	public boolean onContextItemSelected(MenuItem item){
		AdapterView.AdapterContextMenuInfo menuInfo = 
				(AdapterView.AdapterContextMenuInfo)item
				.getMenuInfo();
	//	HashMap<String, Object> map = List.get(menuInfo.position);
	//	Log.v("show", "shibai");
		dm.open();
	//	cursor = dm.selectAll();
		switch(item.getItemId()){
		case 0://ɾ��
			try{
				cursor.moveToPosition(menuInfo.position);

				int i = dm.delete(Long.parseLong(cursor.getString(cursor.getColumnIndex("_id"))));//ɾ������
				
				adapter.removeListItem(menuInfo.position);//ɾ������
				adapter.notifyDataSetChanged();//֪ͨ����Դ�������Ѿ��ı䣬ˢ�½���
				
			//	Log.v("show", "chenggong1" + i);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			break;
		case 1://�޸�
		//	Log.v("show", "chenggong2");
			try{
				cursor.moveToPosition(menuInfo.position);
				
				//����Activity֮���ͨѶ
				Intent intent = new Intent();
				//ͨѶʱ�����ݴ���
				intent.putExtra("id", cursor.getString(cursor.getColumnIndex("_id")));
				intent.putExtra("state", ALERT_STATE);
				intent.putExtra("title", cursor.getString(cursor.getColumnIndex("title")));
				intent.putExtra("time", cursor.getString(cursor.getColumnIndex("time")));
				intent.putExtra("content", cursor.getString(cursor.getColumnIndex("content")));
				//���ò�������һ��ָ����Activity
				intent.setClass(NotepadActivity.this, NotepadEditActivity.class);
				NotepadActivity.this.startActivity(intent);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			break;
		case 2://�鿴
		//	Log.v("show", "chenggong3");
			try{
				cursor.moveToPosition(menuInfo.position);
				
				Intent intent = new Intent();
				
				intent.putExtra("id", cursor.getString(cursor.getColumnIndex("_id")));
				intent.putExtra("title", cursor.getString(cursor.getColumnIndex("title")));
				intent.putExtra("time", cursor.getString(cursor.getColumnIndex("time")));
				intent.putExtra("content", cursor.getString(cursor.getColumnIndex("content")));
				
				intent.setClass(NotepadActivity.this, NotepadCheckActivity.class);
				NotepadActivity.this.startActivity(intent);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			break;
			default:;
		}
	//	cursor.close();
		dm.close();
		return super.onContextItemSelected(item);
		
	}
	
	
	//�̰��������
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
//		Log.v("position", position+"");
//		Log.v("id", id+"");
		
		cursor.moveToPosition(position);
		
		Intent intent = new Intent();
		
		intent.putExtra("state", CHECK_STATE);
		intent.putExtra("id", cursor.getString(cursor.getColumnIndex("_id")));
		intent.putExtra("title", cursor.getString(cursor.getColumnIndex("title")));
		intent.putExtra("time", cursor.getString(cursor.getColumnIndex("time")));
		intent.putExtra("content", cursor.getString(cursor.getColumnIndex("content")));
		
	//	cursor.close();
		dm.close();
		
		intent.setClass(NotepadActivity.this, NotepadCheckActivity.class);
		NotepadActivity.this.startActivity(intent);
		
	}

	//�½�
	public class AddRecordListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.putExtra("state", EDIT_STATE);
			intent.setClass(NotepadActivity.this,NotepadEditActivity.class);
			NotepadActivity.this.startActivity(intent);
		}
		
	}
	
	
	//------------------------------------------------------------------------
	
	public class DeleteRecordListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
		
	}
	public class CheckRecordListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
		
	}
	public class ModifyRecordListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
		
	}
}