package com.android.employeemanagmentsystem.ui.employee_dashboard.ui.application_details

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.employeemanagmentsystem.R
import com.android.employeemanagmentsystem.data.models.responses.Application
import com.android.employeemanagmentsystem.data.models.responses.Employee
import com.android.employeemanagmentsystem.data.network.apis.IOApplicationApi
import com.android.employeemanagmentsystem.data.repository.AuthRepository
import com.android.employeemanagmentsystem.data.repository.IOApplicationRepository
import com.android.employeemanagmentsystem.data.room.AppDatabase
import com.android.employeemanagmentsystem.data.room.EmployeeDao
import com.android.employeemanagmentsystem.databinding.FragmentIoApplicationDetailsBinding
import com.android.employeemanagmentsystem.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IoApplicationDetails: Fragment(R.layout.fragment_io_application_details) {

    private lateinit var binding: FragmentIoApplicationDetailsBinding
    private lateinit var application: Application

    private lateinit var authRepository: AuthRepository
    private lateinit var employeeDao: EmployeeDao
    private lateinit var ioApplicationRepository: IOApplicationRepository
    private lateinit var ioApplicationApi: IOApplicationApi

    private lateinit var curr_user: Employee

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentIoApplicationDetailsBinding.bind(view)
        authRepository = AuthRepository()
        employeeDao = AppDatabase.invoke(requireContext()).getEmployeeDao()
        ioApplicationApi = IOApplicationApi.invoke()
        ioApplicationRepository = IOApplicationRepository()

        application = arguments?.get("Application") as Application

        setApplicationData()



        handleApplyOrDecline()
    }

    private fun handleApplyOrDecline() {

        GlobalScope.launch {
            curr_user = authRepository.getEmployee(employeeDao)

            //if current user is hod and employee just applied application
            var hod_cond = (curr_user.role_id.toInt() == ROLE_HOD) and (application.status_id.toInt() == IO_APPLICATION_APPLIED)

            //if current user is registrar and hod had approved application
            var registrar_cond = (curr_user.role_id.toInt() == ROLE_Registrar) and (application.status_id.toInt() == IO_APPROVED_BY_HOD)

            //if current user is principle and hod had approved application
            var principle_cond = (curr_user.role_id.toInt() == ROLE_Principle) and (application.status_id.toInt() == IO_APPROVED_BY_REGISTRAR)

            withContext(Dispatchers.Main){
                //show approve or decline button if any one is true
                binding.LinearButtonLayout.isVisible = hod_cond or registrar_cond or principle_cond
            }

        }


        binding.btnApply.setOnClickListener {

            GlobalScope.launch {



                // if hod click approved button change status id to approve_by_hod
                if(curr_user.role_id.toInt() == ROLE_HOD){
                    val status = ioApplicationRepository.updateStatusId(application.id, IO_APPROVED_BY_HOD.toString(), ioApplicationApi)


                    withContext(Dispatchers.Main){
                        requireContext().toast(status.status)
                    }

                }
                // if registrar click approve button change status id to approve by registrar
                else if(curr_user.role_id.toInt() == ROLE_Registrar){
                    val status = ioApplicationRepository.updateStatusId(application.id, IO_APPROVED_BY_REGISTRAR.toString(), ioApplicationApi)
                    withContext(Dispatchers.Main){
                        requireContext().toast(status.status)
                    }

                }
                // if principle click approve button change status id to approve by principle
                else if(curr_user.role_id.toInt() == ROLE_Principle){
                    val status = ioApplicationRepository.updateStatusId(application.id, IO_APPROVED_BY_PRINCIPLE.toString(), ioApplicationApi)
                    withContext(Dispatchers.Main){
                        requireContext().toast(status.status)
                    }

                }
                withContext(Dispatchers.Main){
                    findNavController().popBackStack()
                }
            }

        }

        binding.btnDecline.setOnClickListener {

            GlobalScope.launch {
                // if hod click approved button change status id to approve_by_hod
                if(curr_user.role_id.toInt() == ROLE_HOD){
                    val status = ioApplicationRepository.updateStatusId(application.id, IO_Declined_BY_Hod.toString(), ioApplicationApi)
                    withContext(Dispatchers.Main){
                        requireContext().toast(status.status)
                    }

                }
                // if registrar click approve button change status id to approve by registrar
                else if(curr_user.role_id.toInt() == ROLE_Registrar){
                    val status = ioApplicationRepository.updateStatusId(application.id, IO_Declined_BY_Hod.toString(), ioApplicationApi)
                    withContext(Dispatchers.Main){
                        requireContext().toast(status.status)
                    }

                }
                // if principle click approve button change status id to approve by principle
                else if(curr_user.role_id.toInt() == ROLE_Principle){
                    val status = ioApplicationRepository.updateStatusId(application.id, IO_Declined_BY_Hod.toString(), ioApplicationApi)
                    withContext(Dispatchers.Main){
                        requireContext().toast(status.status)
                    }

                }
                withContext(Dispatchers.Main){
                    findNavController().popBackStack()
                }
            }

        }



    }

    private fun setApplicationData() {
        binding.apply {

            tvIoTitle.text = application.title
            tvDesc.text = application.remark
            tvDate.text = application.date
            tvPdf.text = application.application
            tvApplicationType.text = application.getApplicationStringType

        }
    }

}