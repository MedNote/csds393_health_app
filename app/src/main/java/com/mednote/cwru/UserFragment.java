package com.mednote.cwru;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mednote.cwru.databinding.UserFragmentBinding;
import com.mednote.cwru.databinding.WearableFragmentBinding;
import com.mednote.cwru.googlefit.WearableFragment;

public class UserFragment extends Fragment implements View.OnClickListener {

    private UserViewModel mViewModel;

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // And binding
        UserFragmentBinding binding =  DataBindingUtil.inflate(inflater, R.layout.user_fragment, container, false);
        mViewModel = new UserViewModel();
        binding.setViewmodel(getmViewModel());
        View rootView = binding.getRoot();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button loginButton = (Button) getView().findViewById(R.id.status_tv);
        loginButton.setOnClickListener(this);
    }

    public UserViewModel getmViewModel() {
        return mViewModel;
    }

    @Override
    public void onClick(View view) {
      int viewClicked = view.getId();
      if (viewClicked == R.id.status_tv) {
          Fragment thirdFragment = new WearableFragment();
          FragmentTransaction transaction = getFragmentManager().beginTransaction();
          transaction.replace(R.id.nav_host_fragment , thirdFragment);
          transaction.addToBackStack(null);
          transaction.commit();
      }
    }
}