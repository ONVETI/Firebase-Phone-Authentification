package uz.onveti.myapp90firebasephoneverification.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import uz.onveti.myapp90firebasephoneverification.R;


public class CodeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private String smsCode;
    private String mVerificationId;
    private String nomer;

    public CodeFragment() {
    }

    public static CodeFragment newInstance(String param2, String param3) {
        CodeFragment fragment = new CodeFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            smsCode = getArguments().getString(ARG_PARAM1);
            mVerificationId = getArguments().getString(ARG_PARAM2);
            nomer = getArguments().getString(ARG_PARAM3);
        }
    }

    View view;
    EditText edit_text;
    TextView kod_w;
    TextView time;
    TextView resendcode;
    int a = 60;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_code, container, false);
        edit_text = view.findViewById(R.id.code_enter_et);
        kod_w = view.findViewById(R.id.kod_where);
        time = view.findViewById(R.id.timer);
        resendcode = view.findViewById(R.id.resend_code);
        kod_w.setText("Tasdiqlash kodi +998" + nomer + " telefon raqamiga yuborildi");
        resendcode.setVisibility(View.GONE);

        countDownTimerMethod();

        edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 6) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, edit_text.getText().toString());
                    FirebaseAuth instance = FirebaseAuth.getInstance();
                    instance.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(view.getContext(), "Ishladi", Toast.LENGTH_SHORT).show();
                                HomeFragment homeFragment = new HomeFragment();
                                getFragmentManager().beginTransaction().replace(R.id.root_container, homeFragment).commit();
                            } else {
                                Toast.makeText(view.getContext(), "Tasdiqlash kodi no'tog'ri", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


        resendcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a = 60;
                countDownTimerMethod();

                resendcode.setVisibility(View.GONE);
                PhoneAuthProvider.getInstance().verifyPhoneNumber("+998" + nomer,
                        60L,
                        TimeUnit.SECONDS,
                        getActivity(), new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                String smsCode = phoneAuthCredential.getSmsCode();
                                edit_text.setText(smsCode);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                mVerificationId = newVerificationId;
                            }
                        });
            }
        });

        return view;
    }

    private void countDownTimerMethod() {
        CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                a--;
                time.setText(a + "");
            }

            @Override
            public void onFinish() {
                Toast.makeText(view.getContext(), "Vaqt tugadi", Toast.LENGTH_SHORT).show();
                resendcode.setVisibility(View.VISIBLE);
            }
        };
        countDownTimer.start();
    }
}