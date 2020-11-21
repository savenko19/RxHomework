package com.example.rxhomework

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private var dispose: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val count = findViewById<TextView>(R.id.count)
        val edit: EditText = findViewById(R.id.search)
        val text: TextView = findViewById(R.id.text)

        dispose = search(edit)
            .debounce(700, TimeUnit.MILLISECONDS)
            .filter { it.isNotEmpty() }
            .map {
                count(text.text.toString(), it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                count.text = it.toString()
            }
    }

    private fun search(edit: EditText): Observable<String> {

        return Observable.create {
            edit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    it.onNext(p0.toString())
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
        }
    }

    private fun count(str: String, query: String): Int {
        return (str.length - str.replace(query, "").length) / query.length
    }

    override fun onDestroy() {
        super.onDestroy()
        dispose?.dispose()
        dispose = null
    }
}