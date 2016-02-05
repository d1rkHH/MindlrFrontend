package de.gamedots.mindlr.mindlrfrontend.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.gamedots.mindlr.mindlrfrontend.R;

/**
 * Created by dirk on 10.11.2015.
 * This Fragment shows the user a login layout and publishes the sign-in
 * button event to the main activity.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private OnSignInButtonClickedListener _listener;

    /* interface for communication with activity*/
    public interface OnSignInButtonClickedListener {
        void onSignInButtonClicked();
    }

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            _listener = (OnSignInButtonClickedListener) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("not implemented");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        view.findViewById(R.id.sign_in_button).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button) {
            _listener.onSignInButtonClicked();
        }
    }
}
