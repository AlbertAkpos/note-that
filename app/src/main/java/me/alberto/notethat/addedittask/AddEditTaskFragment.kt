package me.alberto.notethat.addedittask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import me.alberto.notethat.EventObserver
import me.alberto.notethat.R
import me.alberto.notethat.databinding.FragmentAddEditTaskBinding
import me.alberto.notethat.tasks.ADD_EDIT_RESULT_OK
import me.alberto.notethat.util.setupRefreshLayout
import me.alberto.notethat.util.setupSnackbar

/**
 * A simple [Fragment] subclass.
 */
class AddEditTaskFragment : Fragment() {

    private lateinit var binding: FragmentAddEditTaskBinding

    private val args by navArgs<AddEditTaskFragmentArgs>()

    private val viewModel by viewModels<AddEditTaskViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_add_edit_task, container, false)
        binding = FragmentAddEditTaskBinding.bind(root).apply {
            this.viewmodel = viewModel
        }

        binding.lifecycleOwner = this.viewLifecycleOwner

        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupSnackbar()
        setupNavigation()
        this.setupRefreshLayout(binding.refreshLayout)
        viewModel.start(args.taskId)
    }

    private fun setupNavigation() {
        viewModel.taskUpdatedEvent.observe(viewLifecycleOwner, EventObserver {
            val action = AddEditTaskFragmentDirections.actionAddEditTaskFragmentToTasksFragment(
                ADD_EDIT_RESULT_OK
            )
            findNavController().navigate(action)
        })
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

}
