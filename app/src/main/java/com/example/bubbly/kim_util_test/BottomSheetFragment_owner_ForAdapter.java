package com.example.bubbly.kim_util_test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bubbly.R;
import com.example.bubbly.SS_PostDetail;
import com.example.bubbly.controller.Post_Adapter;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//                 final BottomSheetFragment bottomSheetFragment = new BottomSheetFragment(getApplicationContext());
// 활용하고 싶으면 onClick 에 오른쪽 코드=>                 bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());

public class BottomSheetFragment_owner_ForAdapter extends BottomSheetDialogFragment {
    Context context;
    Activity activity;
    String post_id;
    int position;

    public BottomSheetFragment_owner_ForAdapter(Context context, String post_id, int position) {
        this.context = context;
        this.post_id = post_id;
        this.position = position;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_dialog_layout_owner, container, false);

        LinearLayout main = view.findViewById(R.id.sheet_main);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "내 대표 게시글 지정", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        LinearLayout delete = view.findViewById(R.id.sheet_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiInterface deletePost_api = ApiClient.getApiClient(requireActivity()).create(ApiInterface.class);
                Call<String> call = deletePost_api.deletePost(SS_PostDetail.post_id);//아이디 가져오기ㅔ
                call.enqueue(new Callback<String>()
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            // 삭제 완료 후, 화면 끄기
                            Post_Adapter.lists.remove(position);
//                            notifyItemRemoved(position);

                            dismiss();

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                    {
                        Log.e("에러", t.getMessage());
                    }
                });
                dismiss();
            }
        });


        LinearLayout nft = view.findViewById(R.id.sheet_nft);
        nft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "NFT 신청하기", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }
}
