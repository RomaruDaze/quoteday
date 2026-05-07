package com.quoteday.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.quoteday.app.data.Quote
import com.quoteday.app.data.QuoteDatabase
import com.quoteday.app.data.SheetApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QuoteViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = QuoteDatabase.getDatabase(application).quoteDao()

    val quotes = dao.getAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            if (dao.getCount() == 0) {
                val remote = SheetApi.fetchAll()
                remote.forEach { dao.insert(it) }
            }
        }
    }

    fun addQuote(text: String, author: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val id = dao.insert(Quote(text = text.trim(), author = author.trim()))
            SheetApi.add(id.toInt(), text.trim(), author.trim())
        }
    }

    fun updateQuote(quote: Quote) {
        viewModelScope.launch {
            dao.update(quote)
            SheetApi.update(quote.id, quote.text, quote.author)
        }
    }

    fun deleteQuote(quote: Quote) {
        viewModelScope.launch {
            dao.deleteById(quote.id)
            SheetApi.delete(quote.id)
        }
    }
}
