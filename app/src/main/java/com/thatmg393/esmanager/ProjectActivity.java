package com.thatmg393.esmanager;

import android.os.Bundle;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import com.thatmg393.esmanager.activity.BaseActivity;

import com.thatmg393.esmanager.fragments.projectactivityfragments.TreeViewFragment;
import io.github.rosemoe.sora.widget.CodeEditor;

import java.util.ArrayList;
import java.util.List;

public class ProjectActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, TabLayout.OnTabSelectedListener {
	private Toolbar toolbar;
	private DrawerLayout drawerLay;
	private NavigationView navSidebar;
	private NavigationView navExplorer;
	
	private TabLayout fileTabs;
	private ProjectViewPager pagerAdapter;
	private ViewPager2 ceContainer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);
		init();
		setSupportActionBar(toolbar);
	}
	
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// TODO: Implement me!
		}
		
		return false;
	}
	
	@Override
    public void onTabSelected(TabLayout.Tab tab) {
        ceContainer.setCurrentItem(tab.getPosition());
    }
	
	public void init() {
		drawerLay = findViewById(R.id.project_drawer_layout);
		toolbar = findViewById(R.id.project_toolbar);
		ActionBarDrawerToggle abdt = new ActionBarDrawerToggle(this, drawerLay, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLay.addDrawerListener(abdt);
        abdt.syncState();
		
		navSidebar = findViewById(R.id.project_nav_sidebar);
		navSidebar.setNavigationItemSelectedListener(this);
		
		navExplorer = findViewById(R.id.project_nav_explorer);
		getSupportFragmentManager().beginTransaction()
			.replace(R.id.project_nav_explorer_fragment_holder, new TreeViewFragment())
			.commit();
		
		pagerAdapter = new ProjectViewPager(getSupportFragmentManager(), getLifecycle());
		ceContainer = findViewById(R.id.project_editor_container);
		ceContainer.setAdapter(pagerAdapter);
		ceContainer.setUserInputEnabled(false);
		
		fileTabs = findViewById(R.id.project_tabs);
		fileTabs.addOnTabSelectedListener(this);
		
		openFile(null);
		openFile(null);
		openFile(null);
	}
	
	private void openFile(String path) {
		SoraEditorFragment sef = new SoraEditorFragment();
		sef.initializeCodeEditor(getApplicationContext());
		sef.ce.setText("imagine");
		
		pagerAdapter.addFragment(sef);
		fileTabs.addTab(fileTabs.newTab().setText("fname.lua"));
	}
	
	@Override
    public void onTabReselected(TabLayout.Tab tab) { }
	
	@Override
    public void onTabUnselected(TabLayout.Tab tab) { }
	
	private class ProjectViewPager extends FragmentStateAdapter {
		private List<SoraEditorFragment> lFrag = new ArrayList<SoraEditorFragment>();
		
		public ProjectViewPager(FragmentManager fa, Lifecycle lc) {
        	super(fa, lc);
    	}
		
		public void addFragment(SoraEditorFragment sef) {
			lFrag.add(sef);
		}
		
		public SoraEditorFragment getFragment(int position) {
			return lFrag.get(position);
		}

    	@Override
    	public Fragment createFragment(int position) {
        	return lFrag.get(position);
    	}

   	 @Override
    	public int getItemCount() {
       	 return lFrag.size();
    	}
	}
	
	public static class SoraEditorFragment extends Fragment {
		public CodeEditor ce;
		
    	@Override
    	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			return ce;
    	}
		
		@Override
		public void onResume() {
			super.onResume();
			ce.requestFocus();
		}
		
		public void initializeCodeEditor(Context context) {
			ce = new CodeEditor(context);
		}
	}
}
