package com.example.android.politicalpreparedness.util

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.models.Election
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("zip_code")
fun setSpinner(spinner: Spinner,zip:String?){
   zip.let {
       if(!it.isNullOrEmpty()){
           val zip_array = arrayOf(it)
           spinner.adapter = ArrayAdapter<String>(spinner.context, R.layout.support_simple_spinner_dropdown_item, zip_array)
       }
   }
}

@BindingAdapter("formattedDate")
fun bindDate(textview:TextView,date:Date){

    var formattedDate: String? = null
    try{
        formattedDate = SimpleDateFormat().format(date)
    }catch (e:Exception){
        e.printStackTrace()
    }

    textview.text = formattedDate
   // return formattedDate.toString()

}

@BindingAdapter("list_data")
fun bindElectionRecyclerViews(recyclerView:RecyclerView,data:List<Election>?){
   // val adapter = recyclerView.adapter as ElectionListAdapter
    val adapter = ElectionListAdapter(ElectionListener {
        // Go to election Info
    })

    recyclerView.adapter = adapter
    adapter.submitList(data)
}

@BindingAdapter("url_click")
fun bindTextViewWithListener(view:TextView,url:String?){
   view.setOnClickListener {
       enableLink(it,url)
   }
}

fun enableLink(it: View?, url: String?) {
    if(!url.isNullOrEmpty()) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        it?.context?.startActivity(intent)
    }
}

object BindingAdapters {
    @BindingAdapter("android:fadeVisible")
    @JvmStatic
    fun setFadeVisible(view: View, visible: Boolean? = true) {
        if (view.tag == null) {
            view.tag = true
            view.visibility = if (visible == true) View.VISIBLE else View.GONE
        } else {
           // view.animate().cancel()
            if (visible == true) {
                view.visibility == View.GONE
               /* if (view.visibility == View.GONE)
                    view.fadeIn()*/
            } else {
                view.visibility == View.VISIBLE
                /*if (view.visibility == View.VISIBLE)
                    view.fadeOut()*/
            }
        }
    }
}