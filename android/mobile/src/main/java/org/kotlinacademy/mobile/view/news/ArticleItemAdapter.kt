package org.kotlinacademy.mobile.view.news

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.kotlinacademy.common.bindView
import org.kotlinacademy.common.loadImage
import org.kotlinacademy.common.openUrl
import org.kotlinacademy.common.recycler.BaseViewHolder
import org.kotlinacademy.common.recycler.ItemAdapter
import org.kotlinacademy.common.startShareIntent
import org.kotlinacademy.data.*
import org.kotlinacademy.mobile.R

class ArticleItemAdapter(
        private val article: Article,
        private val commentClicked: (Article) -> Unit
) : ItemAdapter<ArticleItemAdapter.ViewHolder>(R.layout.item_article) {

    override fun onCreateViewHolder(itemView: View, parent: ViewGroup) = ViewHolder(itemView)

    override fun ViewHolder.onBindViewHolder() {
        titleView.text = article.title
        subtitleView.text = article.subtitle
        imageView.loadImage(article.imageUrl)
        setUpListeners()
    }

    private fun ViewHolder.setUpListeners() {
        val context = itemView.context
        itemView.setOnClickListener {
            context.openUrl(article.url)
        }
        commentButton.setOnClickListener {
            commentClicked(article)
        }
        shareButton.setOnClickListener {
            context.startShareIntent(article.title, article.url ?: article.subtitle)
        }
    }

    class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        val titleView: TextView by bindView(R.id.titleView)
        val subtitleView: TextView by bindView(R.id.subtitleView)
        val imageView: ImageView by bindView(R.id.imageView)
        val commentButton: ImageView by bindView(R.id.commentButton)
        val shareButton: ImageView by bindView(R.id.shareButton)
    }
}