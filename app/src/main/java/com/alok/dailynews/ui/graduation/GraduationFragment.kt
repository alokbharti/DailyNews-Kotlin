package com.alok.dailynews.ui.graduation

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.graphics.rotationMatrix
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alok.dailygraduation.ui.graduation.GraduationCard
import com.alok.dailynews.databinding.DialogGraduationInfoBinding
import com.alok.dailynews.databinding.FragmentGraduationBinding
import com.alok.dailynews.interfaces.OnSwipe
import com.alok.dailynews.models.GraduationItem
import com.alok.dailynews.utility.Utils
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipePlaceHolderView
import com.mindorks.placeholderview.SwipeViewBuilder
import java.io.File


class GraduationFragment : Fragment(), AdapterView.OnItemClickListener, OnSwipe<GraduationItem>{

    private lateinit var swipeView: SwipePlaceHolderView
    private lateinit var viewModel: GraduationViewModel
    private lateinit var binding: FragmentGraduationBinding
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private var imageFile: File? = null
    private lateinit var dialogBinding: DialogGraduationInfoBinding
    private val GET_FROM_GALLERY = 101
    private lateinit var messageDialog: AlertDialog
    private var currentSelectedCollege = "IIITDM Jabalpur"
    private var totalCardNumber = MutableLiveData<Int>()
    private lateinit var rotationAnimation: RotateAnimation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        binding = FragmentGraduationBinding.inflate(inflater, container, false)
        rotationAnimation = RotateAnimation(0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GraduationViewModel::class.java)

        messageDialog = AlertDialog.Builder(requireContext()).create()
        messageDialog.setButton(Dialog.BUTTON_POSITIVE, "OK") { dialog, which ->
            dialog.dismiss()
        }

        arrayAdapter = ArrayAdapter<String>(this.requireContext(), android.R.layout.simple_spinner_item)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        viewModel.collegeList.observe(viewLifecycleOwner, Observer {
            if (it!=null){
                arrayAdapter.addAll(it)
                binding.collegeSpinner.setText(it[0],true)
            }
        })

        binding.collegeSpinner.setAdapter(arrayAdapter)
        binding.collegeSpinner.onItemClickListener = this

        val bottomMargin = Utils.dpToPx(250)
        val windowSize : Point = Utils.getDisplaySize(requireActivity().windowManager)

        swipeView = binding.graduationSwipeView
        swipeView.getBuilder<SwipePlaceHolderView, SwipeViewBuilder<SwipePlaceHolderView>>()
            /*.setDisplayViewCount(3)*/
            .setWidthSwipeDistFactor(8f) // horizontal distance = display width / 8
            .setHeightSwipeDistFactor(10f) // vertical distance = display height / 10
            .setSwipeDecor(
                SwipeDecor()
                .setViewWidth(windowSize.x)
                .setViewHeight(windowSize.y - bottomMargin)
                .setViewGravity(Gravity.TOP)
                .setPaddingTop(20)
                .setRelativeScale(0.01f))

        viewModel.collegeMemories.observe(viewLifecycleOwner, Observer {
            swipeView.removeAllViews()
            if (it==null || it.size ==0){
                binding.noMemoriesTv.text = "No memories found!"
                binding.noMemoriesTv.visibility = View.VISIBLE
            } else{
                for (memory in it){
                    val card = GraduationCard(requireContext(), memory, this)
                    swipeView.addView(card)
                }
                binding.graduationMemoriesCl.visibility = View.VISIBLE
                binding.noMemoriesTv.visibility = View.GONE
            }
            binding.graduationLoadingLl.visibility = View.GONE
            binding.loadingPb.visibility = View.GONE
            binding.refreshBtn.clearAnimation()
        })

        dialogBinding = DialogGraduationInfoBinding.inflate(layoutInflater, null, false)
        val dialog = AlertDialog.Builder(context).setCancelable(false)
            .setView(dialogBinding.root).create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.collegeSearchSpinner.setAdapter(arrayAdapter)
        dialogBinding.cancelBtn.setOnClickListener{
            dialog.dismiss()
        }

        dialogBinding.uploadImage.setOnClickListener{
            startActivityForResult(Intent(Intent.ACTION_PICK, INTERNAL_CONTENT_URI), GET_FROM_GALLERY)
        }

        dialogBinding.submitMemoryBtn.setOnClickListener{
            val email = dialogBinding.emailEt.text.toString()
            val college = dialogBinding.collegeSearchSpinner.text.toString()
            val title = dialogBinding.graduationTitle.text.toString()
            if (imageFile!=null) {
                viewModel.addCollegeMemory(imageFile!!, email, college, title)
                dialogBinding.memoryPb.visibility = View.VISIBLE
            } else {
                Toast.makeText(context, "Please wait, image is uploading", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isMemorySubmitted.observe(viewLifecycleOwner, Observer {
            dialogBinding.memoryPb.visibility = View.GONE
            if (it){
                dialogBinding.emailEt.setText("")
                dialogBinding.graduationTitle.setText("")
                dialogBinding.imagePath.text = "College memory picture"
                dialog.dismiss()
                showDialog("Congratulations on graduation!")
            } else {
                Toast.makeText(requireContext(), "Failed to submit, try again!!", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.compressedFile.observe(viewLifecycleOwner, Observer {
            imageFile = it
        })

        binding.graduationAddBtn.setOnClickListener{
            dialog.show()
        }

        binding.refreshBtn.setOnClickListener{
            rotationAnimation.repeatCount = 0
            rotationAnimation.duration = 2000
            rotationAnimation.fillAfter = true
            binding.refreshBtn.startAnimation(rotationAnimation)

            binding.loadingPb.visibility = View.VISIBLE
            viewModel.loadCollegeMemories(currentSelectedCollege)
        }

        totalCardNumber.observe(viewLifecycleOwner, Observer {
            Log.d("GraduationFragment","total card: $it")
            if (it==1){
                binding.noMemoriesTv.text = "You've seen all the memories, to see more select college name from the above list"
                binding.noMemoriesTv.visibility = View.VISIBLE
            }
        })
    }

    private fun showDialog(message: String){
        messageDialog.setMessage(message)
        messageDialog.show()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val collegeName:String = parent!!.getItemAtPosition(position) as String
        Log.d("GraduationFragment","collegeSelected: $collegeName")
        binding.noMemoriesTv.visibility = View.GONE
        if (collegeName!=currentSelectedCollege) {
            binding.loadingPb.visibility = View.VISIBLE
            viewModel.loadCollegeMemories(collegeName)
            currentSelectedCollege = collegeName
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK){

            if (data!=null) {
                val selectedImage = data.data
                Log.d("GraduationFragment","imagePath: ${selectedImage.toString()}")
                dialogBinding.imagePath.text = selectedImage.toString().split("/").last().split("%2F").last()
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedImage)
                viewModel.getCompressedFile(requireContext(), bitmap)
            }
        }
    }

    override fun onSwipeRight(item: GraduationItem) {
        totalCardNumber.value = swipeView.allResolvers.size
    }

    override fun onSwipeLeft(item: GraduationItem) {
        totalCardNumber.value = swipeView.allResolvers.size
    }

}