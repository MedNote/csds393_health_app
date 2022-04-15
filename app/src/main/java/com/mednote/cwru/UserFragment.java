package com.mednote.cwru;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mednote.cwru.databinding.UserFragmentBinding;
import com.mednote.cwru.databinding.WearableFragmentBinding;

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


    }

    public UserViewModel getmViewModel() {
        return mViewModel;
    }

    @Override
    public void onClick(View view) {

    }
}