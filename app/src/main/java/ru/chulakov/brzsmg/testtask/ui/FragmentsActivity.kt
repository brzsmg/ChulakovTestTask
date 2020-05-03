package ru.chulakov.brzsmg.testtask.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import ru.chulakov.brzsmg.testtask.R

/**
 * Обрывок из FragmentsActivity.
 * TODO: Сконвертирован из Java.
 */
open class FragmentsActivity : AppCompatActivity() {
    protected var fragmentManager: FragmentManager = this.supportFragmentManager
    protected var mCurrentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*if (savedInstanceState != null) {
            mCurrentFragment = fragmentManager.getFragment(savedInstanceState, "s")
            //setFragment(mCurrentFragment!!)
        }*/
    }

    fun getFragmentsActivity(): FragmentsActivity? {
        return this
    }

    fun setFragment(fragment: Fragment) {
        var currentFragment: Fragment? = null
        if (fragmentManager.getFragments().size > 0) {
            currentFragment = fragmentManager.getFragments().get(0)
            Log.i(
                "fragment",
                "[" + fragmentManager.fragments.size
                    .toString() + "]" + Fragment::class.simpleName
            )
        }
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        if (currentFragment == null) {
            fragmentTransaction.add(R.id.fragment_frame, fragment)
        } else {
            //fragmentTransaction.remove(mFragment);
            //mFragmentManager.popBackStack();
            fragmentTransaction.replace(R.id.fragment_frame, fragment)
        }
        mCurrentFragment = fragment
        if (currentFragment != null) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commit()
    }

    fun setNextFragment(fragment: Fragment) {
        setFragment(fragment)
    }

    fun backFragment() {
        fragmentManager.popBackStack()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        //fragmentManager.putFragment(outState, mCurrentFragment?.javaClass?.simpleName!!, mCurrentFragment!!)
    }
}