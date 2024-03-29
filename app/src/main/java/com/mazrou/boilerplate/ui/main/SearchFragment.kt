package com.mazrou.boilerplate.ui.main

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mazrou.boilerplate.R
import com.mazrou.boilerplate.model.ui.Racine
import com.mazrou.boilerplate.ui.BaseActivity
import com.mazrou.boilerplate.ui.main.state.MainStateEvent.*
import com.mazrou.boilerplate.util.StateEvent
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

const val SEARCH_BY_RACINE="search by racine"

@ExperimentalCoroutinesApi
@FlowPreview
class SearchFragment : BaseMainFragment(R.layout.fragment_search)  , SearchListAdapter.Interaction {

    private val _stateEvents = HashMap<String, StateEvent>()
    private lateinit var searchAdapter : SearchListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchAdapter = SearchListAdapter(this)
        initView ()
        subscribeObservers()
    }

    private fun initView (){
        racine_recycler_view.also {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = searchAdapter
        }
    }

    private fun subscribeObservers(){
        search_input.doOnTextChanged { text, _, _, _ ->
            if(text.toString().isEmpty()){
                viewModel.clearRacineSearch()
            }else{
                search(text.toString())
            }
        }
        viewModel.viewState.observe(viewLifecycleOwner ,{
            it.racineList?.let {
                racine_recycler_view.visibility = View.VISIBLE
                card_view.visibility = View.GONE
                number_of_result_txt_view.text = it.size.toString()
                searchAdapter.submitList(it)
            }?: run {
                racine_recycler_view.visibility = View.GONE
                card_view.visibility = View.VISIBLE
            }
        })
    }
    private fun search(query : String  = ""){
      /*  _stateEvents[SEARCH_BY_RACINE]?.let {
            if (viewModel.isJobAlreadyActive(it)){
                return
            }
        }?: run { */
            _stateEvents[SEARCH_BY_RACINE] = SearchByRacine(
               query = query
            )
            viewModel.setStateEvent(_stateEvents[SEARCH_BY_RACINE]!!)
      //  }
    }
    override fun onItemClicked(item: Any, index: Int) {
        when( item){
            is  Racine ->{
                viewModel.setStateEvent(GetAyatRacine(item.id))
                findNavController().navigate(R.id.nav_to_ayat_list)
                (requireActivity() as BaseActivity).hideSoftKeyboard()
            }
        }

    }
}