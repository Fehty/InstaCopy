package com.fehty.instacopy.Activity.Data

data class MessageData(
        val id: Int,
        val text: String,
        val filename: Any,
        val date: String,
        val comments: MutableList<CommentData>?,
        val userId: Int,
        val likes: List<UsersWhoLikedPostData>
)
