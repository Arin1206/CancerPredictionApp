package com.dicoding.asclepius.view


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dicoding.asclepius.Room.ResultEntity
import com.dicoding.asclepius.databinding.ItemBinding
import com.dicoding.asclepius.response.ArticlesItem


class ResultAdapter : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {
    private var resultList = ArrayList<ResultEntity>()
    private var articlesList = ArrayList<ArticlesItem>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        if (position < resultList.size) {
            val currentItem = resultList[position]
            holder.bind(currentItem)
        } else {
            val currentArticle = articlesList[position - resultList.size]
            holder.bind(currentArticle)
        }
    }


    override fun getItemCount() = resultList.size + articlesList.size

    fun submitList(newList: List<ResultEntity>) {
        resultList.clear()
        resultList.addAll(newList)
        notifyDataSetChanged()
    }

    fun submitArticlesList(newList: List<ArticlesItem>) {
        articlesList.clear()
        articlesList.addAll(newList)
        notifyDataSetChanged()
    }

    inner class ResultViewHolder(private val binding: ItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(resultEntity: ResultEntity) {
            binding.apply {
                tvItemName.text = resultEntity.prediction ?: "No Prediction"
                tvItemDescription.text =
                    resultEntity.confidenceScore?.toString() ?: "No Confidence Score"
                resultEntity.imageUri?.let { uri ->
                    Glide.with(itemView)
                        .load(uri)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imgItemPhoto)
                }
            }
        }

        fun bind(article: ArticlesItem) {
            binding.apply {
                tvItemName.text = article.title
                tvItemDescription.text = article.description?.toString()
                article.urlToImage?.let { uri ->
                    Glide.with(itemView)
                        .load(uri)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imgItemPhoto)
                }
            }

        }
    }
}
