package me.alberto.notethat.taskdetails

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import me.alberto.notethat.EventObserver
import me.alberto.notethat.R
import me.alberto.notethat.databinding.FragmentTaskDetailBinding
import me.alberto.notethat.tasks.DELETE_RESULT_OK
import me.alberto.notethat.util.setupRefreshLayout
import me.alberto.notethat.util.setupSnackbar

/**
 * A simple [Fragment] subclass.
 */
class TaskDetailFragment : Fragment() {

    private lateinit var binding: FragmentTaskDetailBinding

    private val args: TaskDetailFragmentArgs by navArgs()

    private val viewModel by viewModels<TaskDetailViewModel>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupFab()
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        setupNavigation()
        this.setupRefreshLayout(binding.refreshLayout)
    }

    private fun setupNavigation() {
        viewModel.deleteTaskEvent.observe(viewLifecycleOwner, EventObserver {
            val action = TaskDetailFragmentDirections.actionTaskDetailFragmentToTasksFragment(
                DELETE_RESULT_OK
            )
            findNavController().navigate(action)
        })

        viewModel.editTaskEvent.observe(viewLifecycleOwner, EventObserver {
            val action = TaskDetailFragmentDirections.actionTaskDetailFragmentToAddEditTaskFragment(
                args.taskId,
                resources.getString(R.string.edit_task)
            )

            findNavController().navigate(action)
        })


    }

    private fun setupFab() {
        activity?.findViewById<View>(R.id.edit_task_fab)?.setOnClickListener {
            viewModel.editTask()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaskDetailBinding.inflate(inflater, container, false)

        binding.viewmodel = viewModel

        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.start(args.taskId)




        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                viewModel.deleteTask()
                true
            }
            else -> false
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.taskdetail_fragment_menu, menu)
    }


}
