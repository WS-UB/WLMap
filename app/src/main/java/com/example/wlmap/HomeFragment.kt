package com.example.wlmap

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import java.lang.ref.WeakReference

class HomeFragment : Fragment() {

    private lateinit var navigationButton: Button
    private lateinit var dataCollectionButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(com.example.wlmap.R.layout.fragment_home, container, false)

        // Find buttons by their IDs
        navigationButton = view.findViewById(com.example.wlmap.R.id.nav_button)
        dataCollectionButton = view.findViewById(com.example.wlmap.R.id.data_collection_button)

        // Set onClickListener for navigationButton
        navigationButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(com.example.wlmap.R.id.fragment_container, MapFragment())
                .commit()
        }

        // Set onClickListener for dataCollectionButton
        dataCollectionButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(com.example.wlmap.R.id.fragment_container, MapFragment())
                .commit()
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // drawerLayout initialization logic can go here if needed
    }
}