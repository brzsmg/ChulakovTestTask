package ru.chulakov.brzsmg.testtask.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sebbia.brzsmg.testtask.types.DateTime
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import ru.chulakov.brzsmg.testtask.models.SearchResults
import ru.chulakov.brzsmg.testtask.models.User
import ru.chulakov.brzsmg.testtask.R
import ru.chulakov.brzsmg.testtask.adapters.UserAdapter
import ru.chulakov.brzsmg.testtask.app
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


/**
 * Фрагмент со списком пользователей.
 */
class UserListFragment : Fragment() {

    companion object {
        private const val START_PAGE: Int = 1
        private const val END_PAGE: Int = -1
    }

    //Views and Adapters
    private lateinit var mvList : RecyclerView
    private lateinit var mvProgress : ProgressBar
    private lateinit var mvSearchLogin : EditText
    private lateinit var mLayoutManager : LinearLayoutManager
    private lateinit var mAdapter : UserAdapter

    //Data
    private var mData : ArrayList<User> = ArrayList()
    private var mRequest : Disposable? = null
    private var mPage : Int = START_PAGE
    private var waitTime : Long = 0
    private var search : String = ""

    private var loading : Boolean = false
        set(value){
            if(value) {
                mvProgress.visibility = View.VISIBLE
                if(mPage < 2) {
                    mvList.visibility = View.GONE
                }
            } else {
                mvProgress.visibility = View.GONE
                mvList.visibility = View.VISIBLE
            }
            field = value
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(savedInstanceState != null) {
            @Suppress("UNCHECKED_CAST")
            mData.addAll(savedInstanceState.getSerializable("data") as ArrayList<User>)
            mPage = savedInstanceState.getInt("page")
            search = savedInstanceState.getString("search")!!
        }
        activity?.title = "Пользователи"
        val view = inflater.inflate(R.layout.fragment_user_list, container, false)
        mvList = view.findViewById(R.id.list)
        mvSearchLogin = view.findViewById(R.id.search_login)
        mvProgress = view.findViewById(R.id.progress)
        mLayoutManager = LinearLayoutManager(activity)
        mvList.layoutManager = mLayoutManager
        mAdapter = UserAdapter(mData) { user ->
            val bundle = Bundle()
            bundle.putSerializable("user", user)
            findNavController().navigate(R.id.action_details, bundle)
        }
        mvList.adapter = mAdapter

        mvProgress.visibility = View.GONE
        mvList.visibility = View.VISIBLE

        var t : Timer? = null

        mvSearchLogin.doAfterTextChanged {
            if(search == mvSearchLogin.text.toString()) {
                return@doAfterTextChanged
            }
            search = mvSearchLogin.text.toString()
            if(t != null) {
                t?.cancel()
            }
            t = Timer("WaitIn", false)
            t?.schedule(1000) {
                activity?.runOnUiThread {
                    Log.d("Search", "UPDATE")
                    mData.clear()
                    mAdapter.notifyDataSetChanged()
                    mPage = START_PAGE
                    requestNextPage()
                }
            }
        }

        mvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val total = mLayoutManager.itemCount
                val currentLastItem: Int = mLayoutManager.findLastVisibleItemPosition()
                if (currentLastItem == total - 1) {
                    requestNextPage()
                }
            }
        })

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("data", mData)
        outState.putInt("page", mPage)
        outState.putString("search", search)
    }

    override fun onStart() {
        super.onStart()
        if(mPage == START_PAGE) {
            requestNextPage()
        }
    }

    fun requestNextPage() {
        if(mPage == END_PAGE) {
            return
        }
        if(loading) {
            return
        }
        if(waitTime > DateTime().timestamp) {
            return //TODO: это не поможет, если время расходиться
        }
        waitTime = 0
        loading = true
        val pattern : String = mvSearchLogin.text.toString()
        var query = "type:user"
        if(pattern.isNotEmpty()) {
            query += " $pattern in:login" //injection
        }
        Log.i("PAGINATION", "Запос страницы $mPage")
        mRequest?.dispose()
        mRequest = app.gitHubApi.requestSearchUsers(mPage, 30, query)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ result ->
                if(result.isSuccessful) {
                    val list = result.body()!!.items
                    if(list.count() > 0) {
                        mPage++
                        val start = mData.count()
                        mData.addAll(list)
                        mAdapter.notifyItemRangeInserted(start, list.count())
                    } else {
                        if(mPage > (START_PAGE + 1) ) {
                            Toast.makeText(activity, "Данных больше нет.", Toast.LENGTH_SHORT)
                                .show()
                        }
                        mPage = END_PAGE
                    }
                } else {
                    Toast.makeText(activity,generateError(result), Toast.LENGTH_LONG).show()
                }
                loading = false
            }, { error ->
                Toast.makeText(activity,"Исключение: " + error.javaClass.simpleName, Toast.LENGTH_SHORT).show()
                error.printStackTrace()
                loading = false
            })
    }

    private fun generateError(response : Response<SearchResults<User>>) : String {
        var result : String
        try {
            val limit = response.headers().get("X-RateLimit-Limit")?.toInt()
            val remaining = response.headers().get("X-RateLimit-Remaining")?.toInt()
            if(remaining == 0) {
                waitTime = response.headers().get("X-RateLimit-Reset")?.toLong()!! * 1000
                val reset = DateTime(waitTime).toFormat("HH:mm:ss")
                result = "Закончился лимит из $limit запросов.\nЖдите до $reset."
            } else {
                result = "Ошибка " + response.code() + ", запросов осталось " + remaining + "/" + limit + "."
            }
        } catch (ignore : Exception) {
            result = "Ошибка " + response.code() + "."
        }
        return result
    }

}