package aarambh.apps.blinkitcloneuser

import aarambh.apps.blinkitcloneuser.adapters.AdapterCategory
import aarambh.apps.blinkitcloneuser.databinding.FragmentHomeBinding
import aarambh.apps.blinkitcloneuser.models.Category
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setStatusBarColor()
        setAllCategories()
         //Inflate the layout for this fragment
        return binding.root
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun setAllCategories() {
        val categoryList = ArrayList<Category>()
        for(i in 0 until Constants.allProductsCategoryIcon.size){
            categoryList.add(Category(Constants.allProductsCategory[i], Constants.allProductsCategoryIcon[i]))
        }

        binding.rvCategories.adapter = AdapterCategory(categoryList)
    }

    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.orange)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }

        }
    }
}