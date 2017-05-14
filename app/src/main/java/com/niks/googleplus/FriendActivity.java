package com.niks.googleplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.niks.R;

import java.util.ArrayList;
import java.util.List;

public class FriendActivity extends AppCompatActivity {

    List<String> list = new ArrayList<String>();
    List<String> img_list= new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        Intent intent = getIntent();
        list = intent.getStringArrayListExtra("friendsName");
        img_list = intent.getStringArrayListExtra("friendsPic");
        setListView();
    }

    private void setListView(){

        CustomFriendListAdapter adapter=new CustomFriendListAdapter(this,list,img_list);
        ListView list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);

    }

}
