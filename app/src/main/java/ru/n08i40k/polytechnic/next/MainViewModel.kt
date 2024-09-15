package ru.n08i40k.polytechnic.next

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.n08i40k.polytechnic.next.data.AppContainer
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val appContainer: AppContainer) : ViewModel()