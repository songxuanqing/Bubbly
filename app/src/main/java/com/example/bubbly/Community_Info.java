package com.example.bubbly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.example.bubbly.controller.Post_Adapter;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.post_Response;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Community_Info extends AppCompatActivity {

    String com_id;
    Toolbar toolbar;
    //////////////////////////////////////////////
    Post_Adapter post_adapter;
    ArrayList<post_Response> postList;
    LinearLayoutManager linearLayoutManager;

    SharedPreferences preferences;
    String user_id;

    RecyclerView recyclerView;

    private Parcelable recyclerViewState;
    
    LinearLayout posting;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_info);

        Intent intent = getIntent();
        com_id = intent.getStringExtra("com_id");

        initialize();
        clickable();

        selectPost_Followee_Communit();
    }

    private void clickable() {
        posting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getApplicationContext(), Add_Posting_Create.class);
                mIntent.putExtra("com_id","커뮤아이디");
                startActivity(mIntent);
            }
        });
    }

    private void initialize() {
        // 툴바
        toolbar = findViewById(R.id.com_info_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        posting = findViewById(R.id.community_info_posting);



        recyclerView = findViewById(R.id.com_info_recyclerview);
    }


    private void selectPost_Followee_Communit(){
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //위치 유지
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        //위치 유지
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

        postList = new ArrayList<>();
        post_adapter = new Post_Adapter(getApplicationContext(), this.postList,getApplicationContext());
        recyclerView.setAdapter(post_adapter);
        post_adapter.notifyDataSetChanged();

        preferences = getSharedPreferences("novarand",MODE_PRIVATE);
        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값
        ApiInterface selectPostMeAndFolloweeAndCommunity_api = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<post_Response>> call = selectPostMeAndFolloweeAndCommunity_api.selectPostMeAndFolloweeAndCommunity(user_id);
        call.enqueue(new Callback<List<post_Response>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<post_Response>> call, @NonNull Response<List<post_Response>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
//                    progressBar.setVisibility(View.GONE);
                    List<post_Response> responseResult = response.body();
                    for(int i=0; i<responseResult.size(); i++){;
                        postList.add(new post_Response(responseResult.get(i).getPost_id(),
                                responseResult.get(i).getPost_writer_id(),
                                responseResult.get(i).getWriter_name(),
                                responseResult.get(i).getPost_contents(),
                                responseResult.get(i).getFile_save_names(),
                                responseResult.get(i).getLike_count(),
                                responseResult.get(i).getLike_yn(),
                                responseResult.get(i).getShare_post_yn(),
                                responseResult.get(i).getNft_post_yn(),
                                responseResult.get(i).getNick_name(),
                                responseResult.get(i).getProfile_file_name(),
                                responseResult.get(i).getCre_datetime(),
                                responseResult.get(i).getMentioned_user_list()));
                    }
                    post_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<post_Response>> call, @NonNull Throwable t)
            {
                Log.e("게시물 아이디로 게시물 조회", t.getMessage());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}