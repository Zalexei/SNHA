package com.trigger.snha.views;

import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.trigger.snha.R;
import com.trigger.snha.helpers.Analytics;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class SignInFragment extends Fragment {

    @BindView(R.id.b_submit)
    AppCompatButton bSubmit;

    @BindView(R.id.til_firstname)
    TextInputLayout tilFirstname;

    @BindView(R.id.til_lastname)
    TextInputLayout tilLastname;

    @BindView(R.id.et_firstname)
    TextInputEditText etFirstname;

    @BindView(R.id.et_lastname)
    TextInputEditText etLastname;

    private SignInViewModel viewModel;
    private Unbinder binder;

    private CompositeDisposable editTextDisposables = new CompositeDisposable();
    private CompositeDisposable disposables = new CompositeDisposable();

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.sign_in_fragment, container, false);

        binder = ButterKnife.bind(this, view);

        addEditTextListeners();

        return view;
    }

    /**
     * Subscribes the model to any changes that happen in textviews
     */
    private void addEditTextListeners() {
        editTextDisposables.add(RxTextView.textChanges(etFirstname).skipInitialValue().subscribe(val -> viewModel.setFirstname(val.toString())));
        editTextDisposables.add(RxTextView.textChanges(etLastname).skipInitialValue().subscribe(val -> viewModel.setLastname(val.toString())));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // showing the keyboard right after fragment is opened
        etFirstname.post(() -> {
            if(etFirstname == null) return;

            etFirstname.requestFocus();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(etFirstname, InputMethodManager.SHOW_IMPLICIT);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(SignInViewModel.class);

        checkPrevState();

        disposables.add(viewModel.getState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleState, this::handleError));
    }

    /**
     * Checking last saved values from viewmodel
     */
    private void checkPrevState() {
        SignInViewModel.SignInScreenState prevState = viewModel.getState().getValue();
        if(prevState != null) {
            if(!etFirstname.getText().toString().equals(prevState.firstname) ||
                    !etLastname.getText().toString().equals(prevState.lastname)) {
                editTextDisposables.clear();

                etFirstname.setText(prevState.firstname);
                etLastname.setText(prevState.lastname);

                addEditTextListeners();
            }
        }
    }

    private void handleState(SignInViewModel.SignInScreenState signInScreenState) {
        switch(signInScreenState.status) {
            case INIT:
                bSubmit.setEnabled(false);
                break;
            case READY_TO_SUBMIT:
                bSubmit.setEnabled(true);
                break;
            case SUBMITTING:
                break;
        }
    }

    private void handleError(Throwable throwable) {
        Analytics.lg("SIF error", throwable.getMessage());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binder.unbind();
        disposables.dispose();
        editTextDisposables.dispose();
    }

    @OnClick(R.id.b_submit)
    public void onSubmitClicked() {
        viewModel.submit();
    }
}
