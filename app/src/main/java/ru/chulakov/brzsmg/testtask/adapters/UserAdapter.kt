package ru.chulakov.brzsmg.testtask.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.chulakov.brzsmg.testtask.models.User
import ru.chulakov.brzsmg.testtask.R

/**
 * Адаптер для пользователей.
 */
class UserAdapter(
    private var data: List<User>,
    private val onUserSelected: (user: User) -> Unit) : RecyclerView.Adapter<UserHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        holder.mvLogin.text = data[position].login
        holder.itemView.setOnClickListener {
            onUserSelected.invoke(data[position])
        }
    }
}

class UserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var mvLogin : TextView = itemView.findViewById(R.id.login)
}