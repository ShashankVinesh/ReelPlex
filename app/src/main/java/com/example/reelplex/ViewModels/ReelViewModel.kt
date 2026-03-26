package com.example.reelplex.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reelplex.Data.ApiClient
import com.example.reelplex.Data.ReelRepository
import com.example.reelplex.Models.ReelData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ReelUiState {
    object Idle : ReelUiState()
    object Loading : ReelUiState()
    data class Success(
        val thumbnail: String?,
        val videoUrl: String,
        val caption: String?
    ) : ReelUiState()
    data class Error(val message: String) : ReelUiState()
}


class ReelViewModel : ViewModel() {

    private val repository = ReelRepository(ApiClient.api)

    private val _uiState = MutableStateFlow<ReelUiState>(ReelUiState.Idle)
    val uiState: StateFlow<ReelUiState> = _uiState

    fun loadReel(url: String) {

        if (url.isBlank()) {
            _uiState.value = ReelUiState.Error("Enter URL")
            return
        }

        viewModelScope.launch {
            _uiState.value = ReelUiState.Loading

            val result = repository.fetchReel(url)

            result.onSuccess { data ->
                if (data.url != null) {
                    _uiState.value = ReelUiState.Success(
                        thumbnail = data.thumbnail,
                        videoUrl = data.url,
                        caption = data.caption
                    )
                } else {
                    _uiState.value = ReelUiState.Error("Invalid link")
                }
            }.onFailure {
                _uiState.value = ReelUiState.Error(
                    it.message ?: "Something went wrong"
                )
            }
        }
    }
}