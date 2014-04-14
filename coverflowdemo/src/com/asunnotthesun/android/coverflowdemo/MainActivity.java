package com.asunnotthesun.android.coverflowdemo;

import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import com.asunnotthesun.android.widget.coverflow.CoverFlowListView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnItemClickListener {
	
	private static final String LOG_TAG = "SimpleHListActivity";
	CoverFlowListView listView;
	TestAdapter mAdapter;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.activity_main );
		
		List<String> items = new ArrayList<String>();
		for( int i = 0; i < 50; i++ ) {
			items.add( String.valueOf( i ) );
		}
		mAdapter = new TestAdapter( this, R.layout.sample_item_1, android.R.id.text1, items );
		listView.setHeaderDividersEnabled( true );
		listView.setFooterDividersEnabled( true );
				
		listView.setAdapter( mAdapter );

		Log.i( LOG_TAG, "choice mode: " + listView.getChoiceMode() );
	}
	
	@Override
	public void onContentChanged() {
		super.onContentChanged();
		listView = (CoverFlowListView) findViewById( R.id.listView1 );
	}

	
	@Override
	public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
		Log.i( LOG_TAG, "onItemClick: " + position );
		Log.d( LOG_TAG, "checked items: " + listView.getCheckedItemCount() );
		Log.d( LOG_TAG, "checked positions: " + listView.getCheckedItemPositions() );
	}
	
	class TestAdapter extends ArrayAdapter<String> {
		
		List<String> mItems;
		LayoutInflater mInflater;
		int mResource;
		int mTextResId;
		
		public TestAdapter( Context context, int resourceId, int textViewResourceId, List<String> objects ) {
			super( context, resourceId, textViewResourceId, objects );
			mInflater = LayoutInflater.from( context );
			mResource = resourceId;
			mTextResId = textViewResourceId;
			mItems = objects;
		}
		
		@Override
		public boolean hasStableIds() {
			return true;
		}
		
		@Override
		public long getItemId( int position ) {
			return getItem( position ).hashCode();
		}
		
		@Override
		public int getViewTypeCount() {
			return 3;
		}
		
		@Override
		public int getItemViewType( int position ) {
			return position%3;
		}
		
		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			
			if( null == convertView ) {
				convertView = mInflater.inflate( mResource, parent, false );
			}
			
			TextView textView = (TextView) convertView.findViewById( mTextResId );
			textView.setText( getItem( position ) );
			
			final int pos = position;
			Button btn = (Button) convertView.findViewById(R.id.button1);
			btn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					Log.d("test", "item " + pos + " clicked");
				}
			});
			
			int type = getItemViewType( position );
			
			LayoutParams params = convertView.getLayoutParams();
			if( type == 0 ) {
				params.width = dpToPx(70);
			} else if( type == 1 ) {
				params.width = dpToPx(90);
			} else {
				params.width = dpToPx(110);
			}
			
			return convertView;
		}
	}
	
	public int dpToPx(int dp) {
	    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
	    int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
	    return px;
	}
}