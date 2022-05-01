package com.mednote.cwru;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
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
import com.mednote.cwru.login.LoginRepository;
import com.mednote.cwru.login.models.LoggedInUser;
import com.mednote.cwru.serverapi.DataRequestStatus;
import com.mednote.cwru.serverapi.ServerInteractionViewModel;

public class UserFragment extends Fragment implements View.OnClickListener {

    private UserViewModel mViewModel;
    private LoggedInUser loggedInUser;

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

        // To Doctor View Activity
        Button addBtn = (Button) getView().findViewById(R.id.go_to_add_record_btn);
        addBtn.setOnClickListener(this);
        Button viewBtn = (Button) getView().findViewById(R.id.go_to_view_record_btn);
        viewBtn.setOnClickListener(this);
        getView().findViewById(R.id.editview).setOnClickListener(this);

        // Updating UI

        LoginRepository loginRepository = LoginRepository.getInstance(null);
        getmViewModel().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (propertyId == BR.dataRequestStatus) {
                    if (getServerInteractionViewModel().getDataRequestStatus() == DataRequestStatus.data_received) {
                        loggedInUser = loginRepository.getLoggedInUser();

                        getmViewModel().setName(loggedInUser.getAccountData().getName().toString());
                    }
                }
            }
        });
    }

    private ServerInteractionViewModel getServerInteractionViewModel() {
        return ((MainActivity) getActivity()).getServerInteractionViewModel();
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
      if (viewClicked == R.id.editview) {
          Intent new_intent = new Intent(getActivity(), DoctorNoteAddActivity.class);
          startActivity(new_intent);
      }
        if (viewClicked == R.id.go_to_add_record_btn) {
            Intent intent = new Intent(getActivity(), DoctorNoteAddActivity.class);
            startActivity(intent);
        }
        if (viewClicked == R.id.go_to_view_record_btn) {
            Intent intent = new Intent(getActivity(), DoctorNoteViewActivity.class);
            startActivity(intent);
        }
    }
}