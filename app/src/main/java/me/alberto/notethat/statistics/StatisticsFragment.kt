package me.alberto.notethat.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import me.alberto.notethat.databinding.FragmentStatisticsBinding
import me.alberto.notethat.util.setupRefreshLayout

/**
 * A simple [Fragment] subclass.
 */
class StatisticsFragment : Fragment() {
    private lateinit var binding: FragmentStatisticsBinding

    private val viewModel by viewModels<StatisticsViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)



        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        this.setupRefreshLayout(binding.refreshLayout)
    }

}
