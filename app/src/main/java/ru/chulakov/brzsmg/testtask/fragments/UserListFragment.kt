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
import com.google.gson.reflect.TypeToken
import com.sebbia.brzsmg.testtask.types.DateTime
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import ru.chulakov.brzsmg.testtask.Json
import ru.chulakov.brzsmg.testtask.Model.SearchResults
import ru.chulakov.brzsmg.testtask.Model.User
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

    private val TAG = this.javaClass.simpleName
    private var mSavedState: Bundle? = null

    lateinit var mvList : RecyclerView
    lateinit var mvProgress : ProgressBar
    lateinit var mvSearchLogin : EditText
    lateinit var mLayoutManager : LinearLayoutManager
    lateinit var mAdapter : UserAdapter

    var mData : ArrayList<User> = ArrayList()
    var page : Int = -2
    var waitTime : Long = 0
    var search : String = ""

    var loading : Boolean = false
        set(value){
            if(value) {
                mvProgress.visibility = View.VISIBLE
                if(page < 2) {
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
        activity?.setTitle("Пользователи")
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
            //(activity as FragmentsActivity).setNextFragment(UserDetailFragment(user))
        }
        mvList.adapter = mAdapter

        mvProgress.visibility = View.GONE
        mvList.visibility = View.VISIBLE

        var t : Timer? = null

        if(mSavedState != null) {
            loadState(mSavedState!!)
        } else if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(TAG)) {
                loadState(savedInstanceState.getBundle(TAG)!!)
            }
        }
        mSavedState = null;

        mvSearchLogin.doAfterTextChanged {
            if(search.equals(mvSearchLogin.text.toString())) {
                return@doAfterTextChanged
            }
            search = mvSearchLogin.text.toString()
            /*if(mvSearchLogin.text.length < 3) {
                return@doOnTextChanged
            }*/
            if(t != null) {
                t?.cancel()
            }
            t = Timer("WaitIn", false)
            t?.schedule(1000) {
                activity?.runOnUiThread {
                    Log.d("Search", "UPDATE")
                    mData.clear()
                    page = 1
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

    override fun onDestroyView() {
        super.onDestroyView()
        mSavedState = saveState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(view != null) {
            outState.putBundle(
                TAG,
                if (mSavedState != null) mSavedState else saveState()
            )
        }
    }

    fun saveState() : Bundle? {
        if(mvSearchLogin == null){
            return null
        }
        val state = Bundle()
        state.putString("data", Json.toJson(mData))
        state.putInt("page", page)
        state.putString("search", mvSearchLogin.text.toString())
        return state;
    }

    fun loadState(state : Bundle) {
        var data = Json.fromJson<ArrayList<User>>(state.getString("data"), object : TypeToken<ArrayList<User>>() {}.type)
        mData.addAll(data)
        page = state.getInt("page")
        search = state.getString("search")!!
    }

    override fun onStart() {
        super.onStart()
        if(page == -2) {
            page = 1
            requestNextPage()
        } else {
            mAdapter.notifyDataSetChanged()
        }
    }

    /*override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }*/

    fun requestNextPage() {
        if(page < 0) {
            return
        }
        if(loading) {
            return
        }
        if(waitTime > DateTime().time) { //TODO: Если время расходиться это не поможет
            return
        }
        waitTime = 0
        loading = true
        var pattern : String = mvSearchLogin.text.toString()
        var query = "type:user"
        if(pattern.length > 0) {
            query += " " + pattern + " in:login" //TODO: injection
        }
        Log.i("PAGINATION","Запос страницы " + page)
        val observer = app.gitHubApi.requestSearchUsers(page, 30,query)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ result ->
                if(result.isSuccessful) {
                    val list = result.body()!!.items
                    if(list!!.count() > 0) {
                        page++
                        mData.addAll(list)
                        mAdapter.notifyDataSetChanged() //TODO: не оптимальное использование notify
                    } else {
                        if(page > 1) {
                            Toast.makeText(activity, "Данных больше нет.", Toast.LENGTH_SHORT)
                                .show()
                        }
                        page = -1
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

    fun generateError(response : Response<SearchResults<User>>) : String {
        try {
            var limit = response.headers().get("X-RateLimit-Limit")?.toInt()
            var remaining = response.headers().get("X-RateLimit-Remaining")?.toInt()
            if(remaining == 0) {
                waitTime = response.headers().get("X-RateLimit-Reset")?.toLong()!! * 1000
                var reset = DateTime(waitTime)
                return "Закончился лимит из " + limit + " запросов.\r\nЖдите до " + reset + "."
            } else {
                return "Ошибка " + response.code() + ", запросов осталось " + remaining + "/" + limit + ".";
            }
        } catch (ignore : Exception) {
            return "Ошибка " + response.code() + "."
        }
    }

}