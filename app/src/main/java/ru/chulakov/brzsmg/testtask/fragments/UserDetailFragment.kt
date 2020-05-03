package ru.chulakov.brzsmg.testtask.fragments

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.*
import ru.chulakov.brzsmg.testtask.Json
import ru.chulakov.brzsmg.testtask.Model.User
import ru.chulakov.brzsmg.testtask.R
import ru.chulakov.brzsmg.testtask.app

class UserDetailFragment : Fragment {

    private val TAG = this.javaClass.simpleName
    private var mSavedState: Bundle? = null

    lateinit var mvHtml : TextView

    lateinit var mUser : User
    var mLoaded : Boolean = false

    constructor() {

    }

    constructor(user : User) {
        mUser = user
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.setTitle("Информация о пользователе")
        val view = inflater.inflate(R.layout.fragment_user_details, container, false)
        mvHtml = view.findViewById(R.id.html)

        if(mSavedState != null) {
            loadState(mSavedState!!)
        } else if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(TAG)) {
                loadState(savedInstanceState.getBundle(TAG)!!)
            }
        } else {
            if(arguments != null) {
                mUser = arguments?.getSerializable("user") as User
            }
        }
        mSavedState = null;

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSavedState = saveState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle(TAG, if (mSavedState != null) mSavedState else saveState())
    }

    fun saveState() : Bundle {
        val state = Bundle()
        state.putString("user", Json.toJson(mUser))
        state.putBoolean("loaded", mLoaded)
        return state;
    }

    fun loadState(state : Bundle) {
        mUser = Json.fromJson(state.getString("user"), User::class.java)
        mLoaded = state.getBoolean("loaded")
    }

    override fun onStart() {
        super.onStart()
        showDetails()
        if(mLoaded) {
            return
        }
        val observer = app.gitHubApi.requestUser(mUser.login)
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

    fun showDetails() {
        val html = StringBuilder("<h3>${mUser.login}</h3>")
        if (mUser.name != null) {
            html.append("<p><i>${mUser.name}</i><br/>")
        }
        if (mUser.followers != null) {
            if (mUser.followers!!  > 0) {
                html.append("<p>Followers <b>${mUser.followers}</b><br/>")
            } else {
                html.append("<p>No followers<br/>")
            }
        }
        if(mUser.created_at != null) {
            html.append("<p>Сreated <b>" +mUser.created_at!!.toFormat("dd.MM.YYYY HH:mm:ss") + "</b><br/>")
        }

        if(mUser.bio != null){
            if(mUser.bio != ""){
                html.append("<p>Bio: ${mUser.bio}<br/>")
            }
        }

        mvHtml.text = Html.fromHtml(html.toString(), Html.FROM_HTML_MODE_COMPACT)
    }
}