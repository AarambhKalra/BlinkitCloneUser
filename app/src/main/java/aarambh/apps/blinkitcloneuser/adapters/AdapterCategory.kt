package aarambh.apps.blinkitcloneuser.adapters


import aarambh.apps.blinkitcloneuser.databinding.ItemViewProductCategoryBinding
import aarambh.apps.blinkitcloneuser.models.Category
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler

class AdapterCategory (val categoryList : ArrayList<Category>) : RecyclerView.Adapter<AdapterCategory.CategoryViewHolder>() {
    class CategoryViewHolder(val binding: ItemViewProductCategoryBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(ItemViewProductCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.binding.apply {
            ivCategoryImage.setImageResource(categoryList[position].image)
            tvCategoryTitle.text = categoryList[position].title
        }

    }
}