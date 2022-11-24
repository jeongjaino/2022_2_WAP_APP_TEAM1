package com.example.witt.presentation.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.witt.BuildConfig
import com.example.witt.R
import com.example.witt.data.api.KakaoAPI
import com.example.witt.data.model.search.Place
import com.example.witt.databinding.FragmentMapSearchBinding
import com.example.witt.presentation.base.BaseFragment
import com.example.witt.data.model.search.ResultSearchKeyword
import com.example.witt.presentation.ui.search.adapter.MapSearchAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MapSearchFragment: BaseFragment<FragmentMapSearchBinding>(R.layout.fragment_map_search){

    companion object{
        const val BASE_URL = "https://dapi.kakao.com"
        const val API_KEY = BuildConfig.KAKAO_REST_API_KEY
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchKeywordInit()

    }

    private fun searchKeywordInit(){
        binding.editText.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                searchKeyword(binding.editText.text.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun searchKeyword(keyword: String){
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)
        val call = api.getSearchKeyword(API_KEY,keyword)
        CoroutineScope(Dispatchers.IO).launch{
            call.enqueue(object : Callback<ResultSearchKeyword> {
                override fun onResponse(
                    call: Call<ResultSearchKeyword>,
                    response: Response<ResultSearchKeyword>
                ) {
                    val data: MutableList<Place> = loadData(response.body())
                    val adapter = MapSearchAdapter()
                    adapter.listData = data
                    binding.searchMapRecyclerView.adapter = adapter
                    binding.searchMapRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                }

                override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                    Log.w("mapSearch", "통신 실패 : ${t.message}")
                }
            })
        }
    }

    private fun loadData(searchResult: ResultSearchKeyword?):MutableList<Place>{
        val data:MutableList<Place> = mutableListOf()
        if (!searchResult?.documents.isNullOrEmpty()) {
            for(index in 0 until (searchResult?.documents?.size!!)){
                searchResult.documents.get(index).let { data.add(it) }
            }
        }
        return data
    }
}