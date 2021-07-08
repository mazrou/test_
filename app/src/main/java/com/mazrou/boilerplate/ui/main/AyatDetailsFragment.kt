package com.mazrou.boilerplate.ui.main

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.mazrou.boilerplate.R
import com.mazrou.boilerplate.model.ui.TafseerBook
import com.mazrou.boilerplate.ui.main.state.MainStateEvent
import com.mazrou.boilerplate.ui.main.state.MainStateEvent.GetTafseerAyat
import kotlinx.android.synthetic.main.ayat_layout.view.*
import kotlinx.android.synthetic.main.fragment_ayat_details.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@ExperimentalCoroutinesApi
@FlowPreview
class AyatDetailsFragment : BaseMainFragment(R.layout.fragment_ayat_details) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        initView()
    }

    private fun initView() {
        searchable__tafseer_spinner.setTitle("إختر تفسيرا");
        searchable__tafseer_spinner.setPositiveButton("إختر");

        searchable__tafseer_spinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            viewModel.getCurrentViewStateOrNew().tafseerBooks!!.toMutableList()
        )
        searchable__tafseer_spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    viewModel.setStateEvent(GetTafseerAyat(
                        ayat = viewModel.getCurrentViewStateOrNew().selectedAyat!!.ayat!! ,
                        tafseerId=  viewModel.getCurrentViewStateOrNew().tafseerBooks!![position].id
                     ))
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
    }

    private fun subscribeObservers(){

        viewModel.viewState.observe(viewLifecycleOwner ,{
            it.selectedAyat?.let {
                surah_name_txt_view.text = "سورة${it.ayat?.surahName}"
                ayat_txt_view.text = it.ayat?.text + " (${it.ayat?.ayatNumber})"

                tafseer_txt_view.text = it.tafseer?.text

            }
        })
    }


}

