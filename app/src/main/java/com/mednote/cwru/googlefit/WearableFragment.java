package com.mednote.cwru.googlefit;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.mednote.cwru.BR;
import com.mednote.cwru.MainActivity;
import com.mednote.cwru.R;
import com.mednote.cwru.UserFragment;
import com.mednote.cwru.databinding.WearableFragmentBinding;

import java.util.ArrayList;

public class WearableFragment extends Fragment implements View.OnClickListener {

    private MainActivity mainActivity;

    // ListView stuff
    private ListView requestStatusList;
    ArrayList<String> requestProgressList = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    public static WearableFragment newInstance() {
        return new WearableFragment();
    }

    public WearableViewModel getWearableViewModel() {
        return getMainActivity().getWearableViewModel();
    }

    public GoogleLoginViewModel getGoogleLoginViewModel() {
        return getMainActivity().getGoogleLoginViewModel();
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        // And binding
        WearableFragmentBinding binding =  DataBindingUtil.inflate(inflater, R.layout.wearable_fragment, container, false);
        mainActivity = (MainActivity) getContext();
        // Make sure every property is instantiated properly
        getWearableViewModel().setWearableGoogleLoggedIn(getGoogleLoginViewModel().getGoogleLoggedIn());
        binding.setViewmodel(getWearableViewModel());
        View rootView = binding.getRoot();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requestStatusList = (ListView) view.findViewById(R.id.wearable_process);
        view.findViewById(R.id.wearable_button_back).setOnClickListener(this);
        view.findViewById(R.id.wearable_button_google_fit).setOnClickListener(this);

        // Instantiate ListView
        requestProgressList.add("adfa");
        adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, getWearableViewModel().getObservableRequestStringList());
        requestStatusList.setAdapter(adapter);

        // Track bindable objects
        getWearableViewModel().addOnPropertyChangedCallback(onPropertyChangedCallback);
        getGoogleLoginViewModel().addOnPropertyChangedCallback(onPropertyChangedCallback);

        Button loginButton = (Button) getView().findViewById(R.id.wearable_button_back);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewClicked = v.getId();
        if (viewClicked == R.id.wearable_button_back) {
            Fragment thirdFragment = new UserFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment , thirdFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (viewClicked == R.id.wearable_button_google_fit) {
            getWearableViewModel().requestWearableData();
        }
    }

    private final Observable.OnPropertyChangedCallback onPropertyChangedCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (propertyId == BR.observableRequestStringList) {
//                requestProgressList = getWearableViewModel().getObservableRequestStringList();
                adapter.notifyDataSetChanged();
            }
            if (propertyId == BR.requestGoogleLogin) {
                if (!getGoogleLoginViewModel().getGoogleLoggedIn()) {
                    getGoogleLoginViewModel().googleSignIn();
                }
            }
            if (propertyId == BR.googleLoggedIn) {
                getWearableViewModel().setWearableGoogleLoggedIn(getGoogleLoginViewModel().getGoogleLoggedIn());
            }
        }
    };
}