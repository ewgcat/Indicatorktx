package com.lishuaihua.customtextviewindicator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class CustomFragment(private val content: String) : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_custom, container, false)
        val tvContent = view.findViewById<TextView>(R.id.tv_content)
        tvContent.text=content
        return view
    }


}