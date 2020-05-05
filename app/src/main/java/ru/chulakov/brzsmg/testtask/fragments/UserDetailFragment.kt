package ru.chulakov.brzsmg.testtask.fragments

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.chulakov.brzsmg.testtask.models.User
import ru.chulakov.brzsmg.testtask.R
import ru.chulakov.brzsmg.testtask.app

class UserDetailFragment : Fragment() {

    //Views
    private lateinit var mvAvatar : ImageView
    private lateinit var mvName : TextView
    private lateinit var mvLogin : TextView
    private lateinit var mvFolowers : TextView
    private lateinit var mvCreatedAt : TextView
    private lateinit var mvBio : TextView

    //Parameters
    private lateinit var mUser : User

    //Data
    private var mRequest : Disposable? = null
    private var mLoaded : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(arguments != null) {
            mUser = arguments?.getSerializable("user") as User
        }
        if(savedInstanceState != null) {
            mUser = savedInstanceState.getSerializable("user") as User
            mLoaded = savedInstanceState.getBoolean("loaded")
        }
        activity?.title = "Информация о пользователе"
        val view = inflater.inflate(R.layout.fragment_user_details, container, false)
        mvAvatar = view.findViewById(R.id.avatar)
        mvName = view.findViewById(R.id.name)
        mvLogin = view.findViewById(R.id.login)
        mvLogin.movementMethod = LinkMovementMethod.getInstance()
        mvFolowers = view.findViewById(R.id.followers)
        mvCreatedAt = view.findViewById(R.id.created_at)
        mvBio = view.findViewById(R.id.bio)

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("user", mUser)
        outState.putBoolean("loaded", mLoaded)
    }

    override fun onStart() {
        super.onStart()
        showDetails()
        requestData()
    }

    private fun requestData() {
        if(mLoaded) {
            return
        }
        mRequest?.dispose()
        mRequest = app.gitHubApi.requestUser(mUser.login)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ result ->
                if(result.isSuccessful) {
                    if(result.body() != null) {
                        mUser = result.body()!!
                        showDetails()
                    }
                } else {
                    Toast.makeText(activity,"Ошибка: " + result.code(), Toast.LENGTH_SHORT).show()
                }
            }, { error ->
                Toast.makeText(activity,"Исключение: " + error.javaClass.simpleName, Toast.LENGTH_SHORT).show()
                error.printStackTrace()
            })
    }

    private fun showDetails() {
        if (mUser.avatar_url != null) {
            Picasso.get()
                .load(mUser.avatar_url)
                .placeholder(R.drawable.ic_person).into(mvAvatar)
            mvAvatar.visibility = View.VISIBLE
        } else {
            mvAvatar.visibility = View.GONE
        }
        if (mUser.name != null) {
            mvName.text = mUser.name
            mvName.visibility = View.VISIBLE
        } else {
            mvName.visibility = View.GONE
        }
        mvLogin.text = Html.fromHtml("<a href=\"${mUser.html_url}\">${mUser.login}</a>", Html.FROM_HTML_MODE_COMPACT)
        if (mUser.followers != null) {
            if (mUser.followers!! > 0) {
                mvFolowers.text = Html.fromHtml(
                    "<b>${mUser.followers}</b> followers.",
                    Html.FROM_HTML_MODE_COMPACT
                )
            } else {
                mvFolowers.text = Html.fromHtml("No followers.", Html.FROM_HTML_MODE_COMPACT)
            }
            mvFolowers.visibility = View.VISIBLE
        } else {
            mvFolowers.visibility = View.GONE
        }
        if (mUser.created_at != null) {
            mvCreatedAt.text = Html.fromHtml(
                "Registration <b>${mUser.created_at?.toFormat("dd.MM.yyyy")} ${mUser.created_at?.toFormat("HH:mm:ss")}</b>.",
                Html.FROM_HTML_MODE_COMPACT
            )
            mvCreatedAt.visibility = View.VISIBLE
        } else {
            mvCreatedAt.visibility = View.GONE
        }
        if (mUser.bio != null) {
            mvBio.text = mUser.bio
            mvBio.visibility = View.VISIBLE
        } else {
            mvBio.visibility = View.GONE
        }
    }
}