package com.example.ecommerce.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerce.Model.CategoryModel
import com.example.ecommerce.Model.ItemsModel
import com.example.ecommerce.Model.SliderModel
import com.example.ecommerce.Repository.MainRepository

class MainViewModel ():ViewModel() {
    private val repository=MainRepository()

    fun loadBanner():LiveData<MutableList<SliderModel>>{
        return repository.loadBanner()
    }

    fun loadCategory():LiveData<MutableList<CategoryModel>>{
        return repository.loadCategory()
    }

    fun loadPopular():LiveData<MutableList<ItemsModel>>{
        return repository.loadPopular()
    }

    fun loadFiltered(id:String):LiveData<MutableList<ItemsModel>>{
        return repository.loadFiltered(id)
    }


}