package ru.chulakov.brzsmg.testtask.activities

import android.os.Bundle
import androidx.navigation.Navigation
import ru.chulakov.brzsmg.testtask.R
import ru.chulakov.brzsmg.testtask.fragments.UserListFragment
import ru.chulakov.brzsmg.testtask.ui.FragmentsActivity

class MainActivity : FragmentsActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var navController = Navigation.findNavController(this, R.id.fragment_frame);
        if(savedInstanceState == null) {
            navController.navigate(R.id.userListFragment)
            //setNextFragment(UserListFragment())
        }
    }
}
