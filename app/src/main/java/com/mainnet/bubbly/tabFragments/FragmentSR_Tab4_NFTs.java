package com.mainnet.bubbly.tabFragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mainnet.bubbly.R;
import com.mainnet.bubbly.SS_SearchResult;
import com.mainnet.bubbly.controller.NFTSell_Adapter;
import com.mainnet.bubbly.controller.NFT_Adapter;
import com.mainnet.bubbly.controller.Post_Adapter;
import com.mainnet.bubbly.model.NFTSell_Item;
import com.mainnet.bubbly.model.NFT_Item;
import com.mainnet.bubbly.model.SearchedUser_Item;
import com.mainnet.bubbly.model.UserInfo;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;
import com.mainnet.bubbly.retrofit.post_Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentSR_Tab4_NFTs extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    View v;

    private NFTSell_Adapter adapter;
    private ArrayList<NFTSell_Item> list;
    private ArrayList<SearchedUser_Item> userList;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    private Parcelable recyclerViewState;
    String keyword;

    public FragmentSR_Tab4_NFTs() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FragmentSR_Tab4_NFTs newInstance(String param1, String param2) {
        FragmentSR_Tab4_NFTs fragment = new FragmentSR_Tab4_NFTs();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        uid = ((SS_SearchResult) getActivity()).getUid();
        keyword = ((SS_SearchResult) getActivity()).getKeyword();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 레이아웃
        v = inflater.inflate(R.layout.fragment_ss_search_result, container, false);
        recyclerView = v.findViewById(R.id.rv_searchResult);

        return v;
    }

    public void selectNFTPost(){
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        //위치 유지
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        //위치 유지
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

        ViewGroup view = (ViewGroup) v.findViewById(android.R.id.content);
        list = new ArrayList<>();
        userList = new ArrayList<>();
        adapter = new NFTSell_Adapter(getActivity().getApplicationContext(), this.list, getActivity(),view);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        ApiInterface api = ApiClient.getApiClient(requireActivity()).create(ApiInterface.class);
        Call<String> call = api.selectUserSearchResultList(UserInfo.user_id,keyword);
        call.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        for(int i=0; i<jsonArray.length(); i++){
                            String jsonString = jsonArray.getString(i);
                            JSONObject jsonObject = new JSONObject(jsonString);
                            userList.add(new SearchedUser_Item(jsonObject.getString("user_id"),
                                    jsonObject.getString("nick_name"),
                                    jsonObject.getString("profile_file_name"),
                                    jsonObject.getString("login_id"),
                                    jsonObject.getString("self_info")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for(int i = 0; i<userList.size(); i++){
                        Call<List<NFTSell_Item>> call_nft = api.selectSelledNftListUsingSellerId(userList.get(i).getUser_id());
                        call_nft.enqueue(new Callback<List<NFTSell_Item>>() {
                            @Override
                            public void onResponse(Call<List<NFTSell_Item>> call, Response<List<NFTSell_Item>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<NFTSell_Item> responseResult = response.body();
                                    for(int j=0; j<responseResult.size(); j++){
                                        System.out.println("nft 보유 목록"+responseResult.get(j).getNft_id());
                                        list.add(new NFTSell_Item(responseResult.get(j).getNft_id(),
                                                responseResult.get(j).getSeller_id(),
                                                responseResult.get(j).getSell_price(),
                                                responseResult.get(j).getApp_id(),
                                                responseResult.get(j).getNft_des(),
                                                responseResult.get(j).getNft_name(),
                                                responseResult.get(j).getFile_save_url(),
                                                responseResult.get(j).getNovaland_account_addr()));
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            @Override
                            public void onFailure(Call<List<NFTSell_Item>> call, Throwable t) {
                                Log.e("nft 보유목록 가져오기 실패", t.getMessage());
                            }
                        });
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
            {
                Log.e("게시물 아이디로 게시물 조회", t.getMessage());
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        selectNFTPost();
    }

}
