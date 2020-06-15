package me.alberto.notethat.tasks

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import me.alberto.notethat.EventObserver
import me.alberto.notethat.R
import me.alberto.notethat.data.source.DefaultTasksRepository
import me.alberto.notethat.databinding.FragmentTasksBinding
import me.alberto.notethat.util.setupRefreshLayout
import me.alberto.notethat.util.setupSnackbar
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class TasksFragment : Fragment() {

    private lateinit var binding: FragmentTasksBinding

    private val viewModel by viewModels<TasksViewModel> {
        TasksViewModelFactory(DefaultTasksRepository.getRepository(requireActivity().application))
    }

    private val args by navArgs<TasksFragmentArgs>()

    private lateinit var listAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTasksBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }

        setHasOptionsMenu(true)


        return binding.root
    }


    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_clear -> {
                viewModel.clearCompletedTasks()
                true
            }
            R.id.menu_filter -> {
                showFilteringPopUpMenu()
                true
            }
            R.id.menu_refresh -> {
                viewModel.loadTasks(true)
                true
            }
            else -> false
        }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.lifecycleOwner = this.viewLifecycleOwner
        setupSnackbar()
        setupListAdapter()
        setupRefreshLayout(binding.refreshLayout, binding.tasksList)
        setupNavigation()
        setupFab()
    }

    private fun setupNavigation() {
        viewModel.openTaskEvent.observe(viewLifecycleOwner, EventObserver {
            openTaskDetails(it)
        })

        viewModel.newTaskEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToAddNewTask()
        })
    }


    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        arguments?.let {
            viewModel.showEditResultMessage(args.userMessage)
        }
    }


    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_tasks, menu)

            setOnMenuItemClickListener {
                viewModel.setFiltering(
                    when (it.itemId) {
                        R.id.active -> TasksFilterType.ACTIVE_TASKS
                        R.id.completed -> TasksFilterType.COMPLETED_TASKS
                        else -> TasksFilterType.ALL_TASKS
                    }
                )
                true
            }
            show()
        }
    }


    private fun setupFab() {
        activity?.findViewById<FloatingActionButton>(R.id.add_task_fab)?.let {
            it.setOnClickListener {
                navigateToAddNewTask()
            }
        }
    }

    private fun navigateToAddNewTask() {
        val action = TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
            null,
            resources.getString(R.string.add_task)
        )
        findNavController().navigate(action)
    }

    private fun openTaskDetails(taskId: String) {
        val action = TasksFragmentDirections
            .actionTasksFragmentToTaskDetailFragment(taskId)
        findNavController().navigate(action)
    }

    private fun setupListAdapter() {
        val viewModel = binding.viewmodel
        if (viewModel != null) {
            listAdapter = TaskAdapter(viewModel)
            binding.tasksList.adapter = listAdapter
        } else {
            Timber.w("Viewmodel not initialised ")
        }
    }

}
