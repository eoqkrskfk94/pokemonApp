package com.mj.pokemonapp.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mj.pokemonapp.domain.usecase.GetPokemonDetailUseCase
import com.mj.pokemonapp.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getPokemonDetailUseCase: GetPokemonDetailUseCase
) : ViewModel() {


    private val _detailStateLiveData = MutableLiveData<DetailState>(DetailState.Uninitialized)
    val detailStateLiveData: LiveData<DetailState> = _detailStateLiveData


    fun getPokemonDetail(id: Int) {
        viewModelScope.launch {
            _detailStateLiveData.value = DetailState.LoadingRead
            when (val result = getPokemonDetailUseCase(id)) {
                is Result.Success -> {
                    _detailStateLiveData.value = DetailState.SuccessReadPokemonDetail(result.data)
                }
                is Result.Error -> {
                    _detailStateLiveData.value = DetailState.Error(result.exception.toString())
                }
            }
        }
    }


}