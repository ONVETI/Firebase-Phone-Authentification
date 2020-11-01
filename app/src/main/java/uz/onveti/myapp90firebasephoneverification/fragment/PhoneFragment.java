package uz.onveti.myapp90firebasephoneverification.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import uz.onveti.myapp90firebasephoneverification.R;


public class PhoneFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public PhoneFragment() {
    }

    public static PhoneFragment newInstance(String param1, String param2) {
        PhoneFragment fragment = new PhoneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    View view;
    TextView region_code;
    EditText phone_n;
    String smsCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_phone, container, false);
        region_code = view.findViewById(R.id.region_code);
        phone_n = view.findViewById(R.id.phone_number);
        FloatingActionButton next_id = view.findViewById(R.id.next_id_fab);


        next_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String r_code = region_code.getText().toString();
                String p_num = phone_n.getText().toString();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(r_code + p_num,
                        60L,
                        TimeUnit.SECONDS,
                        getActivity(), new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                smsCode = phoneAuthCredential.getSmsCode();
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                CodeFragment codeFragment = new CodeFragment().newInstance(s, phone_n.getText().toString());
                                getFragmentManager().beginTransaction().replace(R.id.root_container, codeFragment).commit();
                            }
                        });
            }
        });

        return view;
    }
}